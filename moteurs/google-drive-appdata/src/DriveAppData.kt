package fr.popote.core.sauvegarde

import android.accounts.Account
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import fr.popote.core.db.ParametresEntity
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Le dossier applicatif de Google Drive — là où vivent les sauvegardes chiffrées.
 *
 * Portée `drive.appdata` : un dossier **par application et par compte**, invisible dans le
 * Drive du popotier, qu'aucune autre application ne peut lire. C'est aussi une portée
 * **non sensible** au sens de Google — pas de validation à passer pour la publier, ce qui
 * la rend praticable pour une popote.
 *
 * **Et c'est ce qui fait suivre les données au compte, pas au téléphone.** Sur un appareil
 * neuf, se connecter au même compte Google redonne accès au même dossier : les archives et
 * le trousseau y sont, il ne manque que le mot de passe administrateur pour les rouvrir.
 *
 * **Ce qui est déposé** (`consigne/phase-17-sauvegarde-continue.md`).
 *  - `popote-socle-<instant>.enc` ([Espace.nomSocle]) — la base entière, une fois par
 *    semaine : à la passation, ou quand les points deviendraient trop lourds ;
 *  - `popote-point-<instant>-<socle>.enc` ([Espace.nomPoint]) — les seuls blocs changés
 *    depuis ce socle, déposés dès trente secondes de calme au comptoir ; la rotation en garde
 *    tous ceux des dernières vingt-quatre heures, un par heure sur la semaine, un par jour
 *    au-delà ([aPurger]) ;
 *  - [NOM_COURANT] et `popote-AAAA-MM-JJ.enc` — le format d'avant la phase 17, plus jamais
 *    écrit, toujours relu, effacé une fois que les socles couvrent assez d'historique ;
 *  - `popote-trousseau-<empreinte>.enc` ([nomTrousseau]) — la paire de clés scellée
 *    ([Trousseau.Scelle]), redéposée à chaque sauvegarde et nommée par l'empreinte de la
 *    paire, si bien qu'une archive retrouve toujours la serrure qui l'ouvre ;
 *  - [NOM_TROUSSEAU] — le même trousseau sous le nom historique, écrit seulement s'il
 *    n'existe pas encore : point d'entrée de secours pour les archives d'avant l'empreinte.
 *
 * **Non vérifiable ici.** Aucun compte Google, aucun Play Services dans cet environnement.
 * Seules les parts sans I/O — [construireCorpsMultipart], [lireListe], [lireJetonPage],
 * [nomQuotidien], [aSupprimer] — se testent seules (`DriveAppDataTest.kt`).
 */
object DriveAppData {

    private const val PORTEE_DRIVE_APPDATA = "https://www.googleapis.com/auth/drive.appdata"
    private const val URL_UPLOAD = "https://www.googleapis.com/upload/drive/v3/files"
    private const val URL_FICHIERS = "https://www.googleapis.com/drive/v3/files"
    private const val URL_USERINFO = "https://www.googleapis.com/oauth2/v3/userinfo"
    private const val DOSSIER_APPDATA = "appDataFolder"

    /** La sauvegarde la plus récente. Un nom fixe : elle se remplace, elle ne s'accumule pas. */
    const val NOM_COURANT = "popote-courant.enc"

    /**
     * La paire de clés scellée sous le mot de passe administrateur.
     *
     * Ce nom-ci est **historique** : il ne dit pas *quelle* paire il contient. Un dossier
     * applicatif peut en héberger plusieurs — popote réinstallée, second téléphone, compte
     * Google partagé entre deux popotes — et le premier arrivé occupait alors ce nom pour
     * toujours, pendant que les archives suivantes étaient scellées vers une autre clé que
     * lui. Plus rien ne se restaurait, sans qu'aucun message ne puisse dire pourquoi.
     * Les dépôts se font désormais aussi sous [nomTrousseau], qui nomme chaque trousseau
     * par l'empreinte de sa paire ; celui-ci n'est plus qu'un point d'entrée de secours,
     * écrit une seule fois et jamais écrasé.
     */
    const val NOM_TROUSSEAU = "popote-trousseau.enc"

    private const val PREFIXE_TROUSSEAU = "popote-trousseau-"

    private const val SUFFIXE = ".enc"

    /** Une semaine glissante d'historique quotidien. */
    const val QUOTIDIENNES_GARDEES = 7

    /** Le maximum que Drive v3 accepte par page. Demander moins, c'est pagineur pour rien. */
    private const val TAILLE_PAGE = 1000

    /** Garde-fou : de quoi lister vingt mille fichiers, et de quoi ne jamais boucler sans fin. */
    private const val PAGES_MAX = 20

    /**
     * Un fichier du dossier applicatif, tel que Drive le liste. [modifie] et [taille] servent
     * à l'affichage et à départager deux fichiers de même nom ; absents d'un transport de
     * test, ils ne décident jamais seuls de ce qu'une archive contient.
     */
    data class FichierDrive(
        val id: String,
        val nom: String,
        val modifie: Instant? = null,
        val taille: Long? = null,
    )

    /** Six socles : au moins cinq semaines d'historique, et jamais tout effacé d'un appareil qui se tait. */
    const val SOCLES_GARDES = 6

    /** Tous les points de ces dernières heures sont gardés. */
    val FENETRE_POINTS_TOUS: Duration = Duration.ofHours(24)

    /** Puis un par heure jusqu'à ce délai, puis un par jour. */
    val FENETRE_POINTS_HORAIRES: Duration = Duration.ofDays(7)

    /** `20260926T105712Z` — l'instant en UTC dans un nom : triable, et sans ambiguïté au changement d'heure. */
    private val FORMAT_INSTANT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC)

    private const val MOTIF_INSTANT = """\d{8}T\d{6}Z"""

    internal fun instantDansNom(instant: Instant): String = FORMAT_INSTANT.format(instant)

    internal fun instantDuNom(texte: String): Instant? =
        runCatching { Instant.from(FORMAT_INSTANT.parse(texte)) }.getOrNull()

    /** Ce qu'un nom de fichier dit de lui-même, sans qu'on ait à le télécharger. */
    sealed interface Archive {
        val fichier: FichierDrive

        /** La base entière, à [instant]. */
        data class Socle(override val fichier: FichierDrive, val instant: Instant) : Archive

        /** Les blocs changés depuis [socle] (un nom de fichier), à [instant]. */
        data class Point(override val fichier: FichierDrive, val instant: Instant, val socle: String) : Archive

        /** `popote-courant.enc`, format d'avant la phase 17. */
        data class Courante(override val fichier: FichierDrive) : Archive

        /** `popote-AAAA-MM-JJ.enc`, format d'avant la phase 17. */
        data class Quotidienne(override val fichier: FichierDrive, val jour: LocalDate) : Archive
    }

    /**
     * Le jeu de noms d'un mode dans le dossier applicatif
     * (`consigne/phase-16-suivi-gestionnaire.md`).
     *
     * Le téléphone du popotier et celui du gestionnaire partagent le compte Google, donc le
     * **même** dossier applicatif. Si l'un écrivait sous les noms de l'autre, il écraserait sa
     * sauvegarde courante ou ferait tourner ses quotidiennes à sa place. Chaque mode a donc son
     * préfixe, et tout nom d'archive — courante, quotidienne, photo — en dérive. Le trousseau,
     * lui, est commun aux deux ([nomTrousseau]) : une popote n'a qu'une paire de clés.
     */
    enum class Espace(val prefixe: String) {
        POPOTE("popote-"),
        GESTION("gestion-");

        /** `popote-courant.enc` ou `gestion-courant.enc`. */
        val nomCourant: String get() = "${prefixe}courant$SUFFIXE"

        /** `popote-2026-08-26.enc` — les dates ISO se trient comme des chaînes, c'est voulu. */
        private val quotidienne = Regex("^" + Regex.escape(prefixe) + """\d{4}-\d{2}-\d{2}\.enc$""")

        /** Vrai pour une archive quotidienne de **cet** espace, jamais de l'autre. */
        fun estQuotidienne(nom: String): Boolean = quotidienne.matches(nom)

        /** `popote-socle-20260921T091512Z.enc` — la base entière, déposée à [instant]. */
        fun nomSocle(instant: Instant): String = "${prefixe}socle-${instantDansNom(instant)}$SUFFIXE"

        /**
         * `popote-point-20260926T105712Z-20260921T091512Z.enc` — le point de [instant], et
         * l'instant de son [socle] (un nom rendu par [nomSocle]). Le socle est dans le nom
         * pour que la rotation sache, sans rien télécharger, quels points meurent avec lui.
         */
        fun nomPoint(instant: Instant, socle: String): String {
            val instantSocle = socle.removePrefix("${prefixe}socle-").removeSuffix(SUFFIXE)
            return "${prefixe}point-${instantDansNom(instant)}-$instantSocle$SUFFIXE"
        }

        private val socle = Regex("^" + Regex.escape(prefixe) + "socle-($MOTIF_INSTANT)\\.enc$")
        private val point = Regex("^" + Regex.escape(prefixe) + "point-($MOTIF_INSTANT)-($MOTIF_INSTANT)\\.enc$")

        /**
         * Ce que [fichier] est dans **cet** espace — `null` pour un trousseau, une photo, un
         * fichier étranger, ou tout ce qui appartient à l'autre téléphone.
         */
        fun archive(fichier: FichierDrive): Archive? {
            val nom = fichier.nom
            socle.matchEntire(nom)?.let { m ->
                return instantDuNom(m.groupValues[1])?.let { Archive.Socle(fichier, it) }
            }
            point.matchEntire(nom)?.let { m ->
                val instant = instantDuNom(m.groupValues[1]) ?: return null
                return Archive.Point(fichier, instant, "${prefixe}socle-${m.groupValues[2]}$SUFFIXE")
            }
            if (nom == nomCourant) return Archive.Courante(fichier)
            if (estQuotidienne(nom)) {
                val jour = runCatching {
                    LocalDate.parse(nom.removePrefix(prefixe).removeSuffix(SUFFIXE))
                }.getOrNull() ?: return null
                return Archive.Quotidienne(fichier, jour)
            }
            return null
        }

        companion object {
            /** L'espace d'un mode lu en base — popotier pour une base qui n'en porte pas. */
            fun duMode(mode: String?): Espace =
                if (mode == ParametresEntity.MODE_GESTIONNAIRE) GESTION else POPOTE
        }
    }

    /**
     * Drive refuse le jeton (401, ou 403 de raison [RAISONS_AUTORISATION]) : le jeton n'a
     * plus les droits, réessayer ne changera rien tant que quelqu'un ne rouvre pas l'écran
     * d'autorisation. Distincte d'[IOException] — dont elle hérite pour rester attrapable là
     * où seul le cas générique importe — afin que [TravailSauvegarde] puisse la traiter en
     * échec **définitif** plutôt que de laisser `WorkManager` réessayer indéfiniment un refus
     * qui ne se résoudra jamais tout seul.
     *
     * **Patch Library du 2026-10-26** (relevé dans Carnet de recette, pas encore reporté dans
     * Popote) : la version d'origine rangeait ici **tout** 403. Or Drive rend aussi un 403
     * pour « API non activée » (`accessNotConfigured`), une limite de débit
     * (`rateLimitExceeded`) ou un quota plein (`storageQuotaExceeded`) : on annonçait une
     * autorisation retirée, et un refus transitoire devenait définitif. Voir [exceptionPour].
     */
    class AutorisationRevoquee(message: String) : IOException(message)

    /** Les raisons d'un 403 qui disent vraiment « ce jeton n'a pas les droits ». */
    private val RAISONS_AUTORISATION = setOf("insufficientPermissions", "authError")

    private const val DELAI_CONNEXION_MS = 15_000
    private const val DELAI_LECTURE_MS = 15_000

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * La demande d'autorisation à présenter au popotier : le dossier applicatif, et
     * l'adresse du compte.
     *
     * On ne demande jamais l'accès au Drive entier — la sauvegarde n'a rien à y faire, et
     * une portée large obligerait à passer la validation de Google. L'adresse
     * ([Scopes.EMAIL], non sensible elle aussi) sert uniquement à **afficher** sur quel
     * compte les archives sont déposées : le jour d'une restauration, savoir lequel des
     * comptes du téléphone les détient est la première chose qu'on cherche.
     *
     * [compte], quand il est posé, épingle la demande sur ce compte précis : sans lui,
     * `authorize()` réutilise en silence celui déjà autorisé, sans jamais présenter
     * l'écran de choix — c'est ce qui rendait « changer de compte » impossible tant que
     * l'appelant ne pinçait pas lui-même un compte différent.
     */
    fun requeteAutorisation(compte: Account? = null): AuthorizationRequest =
        AuthorizationRequest.builder()
            .setRequestedScopes(listOf(Scope(PORTEE_DRIVE_APPDATA), Scope(Scopes.EMAIL)))
            .apply { compte?.let { setAccount(it) } }
            .build()

    /**
     * L'issue d'une demande d'autorisation, telle qu'un écran doit la traiter.
     *
     * Google répond de deux façons : l'autorisation est déjà accordée et le compte est
     * connu, ou il faut présenter son écran de consentement. La seconde n'est pas un
     * échec — c'est le cas normal du tout premier branchement.
     */
    sealed interface Autorisation {
        /** Le compte est connecté ; [compte] est son adresse, à afficher et à ranger. */
        data class Accordee(val compte: String) : Autorisation

        /** Il faut présenter [intention] au popotier, puis relire [autorisationDepuisIntention]. */
        data class AEcran(val intention: PendingIntent) : Autorisation

        /** Rien n'a abouti — réseau, Play Services, ou consentement refusé. */
        data class Echec(val motif: String) : Autorisation
    }

    /**
     * Demande l'accès au dossier applicatif du compte Google.
     *
     * Contrairement à [jeton], cette fonction ne lève pas quand l'autorisation manque :
     * elle rend l'écran à présenter. C'est la différence entre le travail de fond, qui ne
     * peut qu'abandonner, et un écran, devant lequel quelqu'un peut justement accepter.
     */
    suspend fun demanderAutorisation(contexte: Context, compte: Account? = null): Autorisation = withContext(
        Dispatchers.IO,
    ) {
        val resultat = try {
            Tasks.await(Identity.getAuthorizationClient(contexte).authorize(requeteAutorisation(compte)))
        } catch (indisponible: Exception) {
            return@withContext Autorisation.Echec(
                indisponible.message ?: "Google n'a pas répondu à la demande d'autorisation.",
            )
        }
        lireResultat(resultat)
    }

    /**
     * Relit ce que l'écran de consentement a rendu. [donnees] est l'intention reçue par le
     * lanceur d'activité ; `null` ou vide signifie que le popotier a refusé ou reculé.
     */
    suspend fun autorisationDepuisIntention(contexte: Context, donnees: Intent?): Autorisation =
        withContext(Dispatchers.IO) {
            val intention = donnees
                ?: return@withContext Autorisation.Echec("Autorisation refusée.")
            try {
                lireResultat(
                    Identity.getAuthorizationClient(contexte).getAuthorizationResultFromIntent(intention),
                )
            } catch (refus: Exception) {
                Autorisation.Echec(refus.message ?: "Autorisation refusée.")
            }
        }

    /**
     * L'accord d'autorisation ne porte pas l'identité du compte — `authorize()` est une API
     * d'autorisation, pas de connexion, et `AuthorizationResult.toGoogleSignInAccount()` rend
     * une adresse `null` en pratique tant qu'aucune connexion Google Identity distincte n'a eu
     * lieu. On la redemande donc soi-même : le jeton obtenu porte la portée [Scopes.EMAIL],
     * ce qui suffit à interroger le point `userinfo` de Google pour l'adresse qui va avec.
     */
    private fun lireResultat(resultat: AuthorizationResult): Autorisation {
        resultat.pendingIntent?.takeIf { resultat.hasResolution() }
            ?.let { return Autorisation.AEcran(it) }
        val jeton = resultat.accessToken
            ?: return Autorisation.Echec("Google n'a pas rendu de jeton d'accès.")
        // Sans adresse, le compte serait connecté mais innommable, et le bandeau d'état ne
        // pourrait plus dire *où* sont les archives : on préfère le dire tout de suite.
        val compte = try {
            lireEmail(String(appel(URL_USERINFO, "GET", jeton), StandardCharsets.UTF_8))
        } catch (indisponible: IOException) {
            null
        } catch (corrompu: SerializationException) {
            null
        } ?: return Autorisation.Echec("Google n'a pas rendu l'adresse du compte.")
        return Autorisation.Accordee(compte)
    }

    /**
     * Le jeton d'accès du compte déjà autorisé.
     *
     * Lève [IllegalStateException] si l'autorisation n'a pas encore été accordée — un
     * échec **définitif** du point de vue du travail de fond : il faut quelqu'un devant
     * l'écran pour la donner, réessayer la nuit n'y changera rien. [IOException] pour tout
     * ce qui est transitoire, que `TravailSauvegarde` doit au contraire réessayer.
     */
    suspend fun jeton(contexte: Context): String = withContext(Dispatchers.IO) {
        val resultat: AuthorizationResult = try {
            Tasks.await(Identity.getAuthorizationClient(contexte).authorize(requeteAutorisation()))
        } catch (indisponible: Exception) {
            throw IOException("Autorisation Google indisponible.", indisponible)
        }
        if (resultat.hasResolution()) {
            throw IllegalStateException(
                "Le compte Google n'a pas encore autorisé la sauvegarde.",
            )
        }
        resultat.accessToken
            ?: throw IOException("Google n'a pas rendu de jeton d'accès.")
    }

    /**
     * Le contenu du dossier applicatif, **en entier**. Vide si rien n'y a jamais été déposé.
     *
     * Drive pagine, et pas seulement au-delà d'un dossier extravagant : les photos de
     * passation ([nomPhoto]) vivent dans ce même dossier, une par passation, et
     * s'ajoutent aux huit archives et aux trousseaux. La version d'avant demandait cent
     * fichiers et ignorait `nextPageToken` — au-delà, le reste était **silencieusement
     * perdu**. Un trousseau tombé dans cette part invisible se lisait alors exactement comme
     * un trousseau absent : « La sauvegarde ne se déchiffre pas », alors que la clé était
     * bien là.
     *
     * On demande donc le maximum que Drive accepte par page ([TAILLE_PAGE]) et on suit le
     * jeton jusqu'au bout, avec [PAGES_MAX] comme garde-fou : une pagination qui ne
     * s'arrêterait jamais — jeton répété par un serveur fautif — boucle sinon à l'infini
     * dans un travail de fond.
     */
    suspend fun lister(contexte: Context): List<FichierDrive> = withContext(Dispatchers.IO) {
        val jeton = jeton(contexte)
        val champs = URLEncoder.encode("nextPageToken,files(id,name,modifiedTime,size)", "UTF-8")
        val tous = mutableListOf<FichierDrive>()
        var jetonPage: String? = null
        var pages = 0
        do {
            val url = buildString {
                append(URL_FICHIERS)
                append("?spaces=").append(DOSSIER_APPDATA)
                append("&pageSize=").append(TAILLE_PAGE)
                append("&fields=").append(champs)
                jetonPage?.let { append("&pageToken=").append(URLEncoder.encode(it, "UTF-8")) }
            }
            val reponse = String(appel(url, "GET", jeton), StandardCharsets.UTF_8)
            tous += lireListe(reponse)
            jetonPage = lireJetonPage(reponse)
            pages++
        } while (jetonPage != null && pages < PAGES_MAX)
        tous
    }

    /**
     * Dépose [octets] sous [nom], en remplaçant le fichier de même nom s'il existe déjà.
     *
     * Remplacer plutôt qu'ajouter : c'est ce qui manquait à la phase 8, où chaque envoi
     * créait un `popote-<uuid>.enc` de plus sans que rien ne soit jamais effacé.
     */
    suspend fun envoyer(
        contexte: Context,
        nom: String,
        octets: ByteArray,
        existants: List<FichierDrive>? = null,
    ): Unit = withContext(Dispatchers.IO) {
        val jeton = jeton(contexte)
        val deja = (existants ?: lister(contexte)).firstOrNull { it.nom == nom }
        // `creation` doit suivre `deja` : sans lui, un remplacement rejoue `parents` dans
        // le PATCH que Drive refuse (cf. la note sur construireCorpsMultipart) — c'est
        // resté au défaut ici pendant que la fonction pure elle-même était bien testée.
        val (frontiere, corps) = construireCorpsMultipart(nom, octets, creation = deja == null)
        val url = if (deja == null) {
            "$URL_UPLOAD?uploadType=multipart"
        } else {
            "$URL_UPLOAD/${deja.id}?uploadType=multipart"
        }
        appel(
            url = url,
            methode = if (deja == null) "POST" else "PATCH",
            jeton = jeton,
            typeContenu = "multipart/related; boundary=$frontiere",
            corps = corps,
        )
    }

    /**
     * Comme [envoyer], mais [fichier] est envoyé en flux plutôt que chargé en mémoire —
     * pour la base elle-même (`TravailSauvegarde`), potentiellement bien trop grosse pour
     * tenir doublée (une fois chiffrée, une fois dans le corps HTTP) en mémoire du
     * processus. Les métadonnées multipart, elles, restent minuscules et donc en mémoire ;
     * seul le corps binaire — le contenu de [fichier] — est copié par blocs.
     */
    suspend fun envoyerFichier(
        contexte: Context,
        nom: String,
        fichier: File,
        existants: List<FichierDrive>? = null,
    ): Unit = withContext(Dispatchers.IO) {
        val jeton = jeton(contexte)
        val deja = (existants ?: lister(contexte)).firstOrNull { it.nom == nom }
        val parties = construireEnTetesMultipart(nom, creation = deja == null)
        val url = if (deja == null) {
            "$URL_UPLOAD?uploadType=multipart"
        } else {
            "$URL_UPLOAD/${deja.id}?uploadType=multipart"
        }
        appelFlux(
            url = url,
            methode = if (deja == null) "POST" else "PATCH",
            jeton = jeton,
            typeContenu = "multipart/related; boundary=${parties.frontiere}",
            tailleCorps = parties.avant.size.toLong() + fichier.length() + parties.apres.size.toLong(),
        ) { sortie ->
            sortie.write(parties.avant)
            fichier.inputStream().use { it.copyTo(sortie) }
            sortie.write(parties.apres)
        }
    }

    /**
     * Rapatrie le contenu d'un fichier du dossier applicatif.
     *
     * [progression] reçoit, au fil de la lecture, les octets déjà reçus et le total attendu
     * (`0` si Drive ne l'annonce pas) — de quoi dire « 3,2 Mo sur 12,4 Mo » pendant une
     * restauration, comme pendant le téléchargement d'une mise à jour.
     */
    suspend fun telecharger(
        contexte: Context,
        id: String,
        progression: (recus: Long, total: Long) -> Unit = { _, _ -> },
    ): ByteArray = withContext(Dispatchers.IO) {
        val connexion = (URL("$URL_FICHIERS/$id?alt=media").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = DELAI_CONNEXION_MS
            readTimeout = DELAI_LECTURE_MS
            setRequestProperty("Authorization", "Bearer ${jeton(contexte)}")
        }
        try {
            val code = connexion.responseCode
            if (code !in 200..299) throw exceptionPour(code, "GET", lireCorpsErreur(connexion))
            val total = connexion.contentLengthLong.coerceAtLeast(0L)
            lireAvecProgression(connexion.inputStream, total, progression)
        } finally {
            connexion.disconnect()
        }
    }

    /** Lit [entree] jusqu'au bout en rendant compte de chaque bloc lu à [progression]. */
    internal fun lireAvecProgression(
        entree: java.io.InputStream,
        total: Long,
        progression: (recus: Long, total: Long) -> Unit,
    ): ByteArray {
        val sortie = java.io.ByteArrayOutputStream(if (total in 1..Int.MAX_VALUE.toLong()) total.toInt() else 64 * 1024)
        val tampon = ByteArray(64 * 1024)
        var recus = 0L
        progression(0L, total)
        entree.use { flux ->
            while (true) {
                val lus = flux.read(tampon)
                if (lus < 0) break
                sortie.write(tampon, 0, lus)
                recus += lus
                progression(recus, total)
            }
        }
        return sortie.toByteArray()
    }

    /** Efface définitivement un fichier du dossier applicatif. */
    suspend fun supprimer(contexte: Context, id: String): Unit = withContext(Dispatchers.IO) {
        appel("$URL_FICHIERS/$id", "DELETE", jeton(contexte))
    }

    // ── Parts pures, testables sans réseau ni compte ───────────────────────────

    /** Ce que Drive dit de son refus : `error.errors[0].reason` et `error.message`. */
    internal data class ErreurDrive(val raison: String?, val message: String?)

    /** Lit le corps d'erreur JSON de Drive ; `null` s'il est absent ou illisible. */
    internal fun lireErreurDrive(corps: String?): ErreurDrive? {
        val erreur = corps
            ?.let { runCatching { json.parseToJsonElement(it).jsonObject["error"]?.jsonObject }.getOrNull() }
            ?: return null
        val raison = runCatching {
            erreur["errors"]?.jsonArray?.firstOrNull()?.jsonObject?.get("reason")?.jsonPrimitive?.content
        }.getOrNull()
        val message = runCatching { erreur["message"]?.jsonPrimitive?.content }.getOrNull()
        return ErreurDrive(raison, message)
    }

    /**
     * L'exception d'une réponse hors 2xx. Seul un vrai refus du jeton devient
     * [AutorisationRevoquee] (définitif) ; le reste est une [IOException] (transitoire, à
     * réessayer) qui porte le message de Google, pour que la vraie cause se lise.
     */
    internal fun exceptionPour(code: Int, methode: String, corps: String?): IOException {
        val erreur = lireErreurDrive(corps)
        val raison = erreur?.raison
        if (code == 401 || (code == 403 && raison in RAISONS_AUTORISATION)) {
            return AutorisationRevoquee("Drive a refusé l'appel $methode : HTTP $code (autorisation révoquée).")
        }
        if (raison == "accessNotConfigured" || raison == "SERVICE_DISABLED") {
            return IOException("L'API Google Drive n'est pas activée dans le projet Google Cloud.")
        }
        val detail = listOfNotNull(erreur?.message, raison?.let { "($it)" }).joinToString(" ")
        return IOException("Drive a refusé l'appel $methode : HTTP $code" + if (detail.isBlank()) "." else " — $detail")
    }

    /** `popote-2026-08-26.enc` — ou `gestion-2026-08-26.enc` pour [Espace.GESTION]. */
    fun nomQuotidien(jour: LocalDate, espace: Espace = Espace.POPOTE): String =
        "${espace.prefixe}$jour$SUFFIXE"

    /**
     * `popote-trousseau-3f9a1c0b7e2d4a58.enc` — un trousseau nommé par l'empreinte de sa
     * propre paire de clés ([Trousseau.empreinte]).
     *
     * C'est ce qui permet à une archive de **désigner sa serrure** : son en-tête porte la
     * même empreinte en clair, la restauration va donc chercher le bon trousseau au lieu
     * de parier sur un nom fixe. L'empreinte ne bouge ni au changement de mot de passe ni
     * au rescellage — la paire, elle, ne change pas — si bien qu'un dépôt sous ce nom
     * écrase toujours une version antérieure du *même* trousseau, jamais celui d'une autre
     * popote.
     */
    fun nomTrousseau(empreinte: String): String = "$PREFIXE_TROUSSEAU$empreinte$SUFFIXE"

    /**
     * Vrai pour [NOM_TROUSSEAU] comme pour tout [nomTrousseau] : les deux façons de nommer
     * un trousseau dans le dossier applicatif. Sert à les rassembler tous à la
     * restauration, quand l'empreinte cherchée ne répond pas et qu'il faut les essayer.
     */
    fun estTrousseau(nom: String): Boolean =
        nom == NOM_TROUSSEAU || (nom.startsWith(PREFIXE_TROUSSEAU) && nom.endsWith(SUFFIXE))

    /**
     * `Passation_Images/signature-1738430000123.jpg` → `popote-photo-Passation_Images_signature-1738430000123.jpg.enc`.
     *
     * Les `/` sont aplatis en `_` : le dossier applicatif est un espace de noms plat, pas
     * une arborescence. Le préfixe `<espace>photo-` les distingue des autres dépôts — il ne
     * correspond ni à une quotidienne ([Espace.estQuotidienne] : la rotation ne les touche
     * donc jamais) ni à [NOM_COURANT] ou [NOM_TROUSSEAU].
     */
    fun nomPhoto(cheminRelatif: String, espace: Espace = Espace.POPOTE): String =
        "${espace.prefixe}photo-${cheminRelatif.replace('/', '_')}$SUFFIXE"

    /**
     * **Ancien format** : la rotation des quotidiennes d'avant la phase 17. L'envoi n'en
     * dépose plus et passe désormais par [aPurger] ; gardée pour ce qu'elle documente de la
     * règle d'origine, et parce que ses tests en verrouillent les garde-fous.
     *
     * Les archives quotidiennes à effacer : tout ce qui dépasse les [garder] plus récentes,
     * plus toute archive dont le nom porte une date postérieure à [aujourdHui].
     *
     * Ne rend **jamais** [NOM_COURANT] ni [NOM_TROUSSEAU], ni quoi que ce soit qui ne porte
     * pas un nom de sauvegarde quotidienne — un fichier étranger déposé dans le dossier
     * applicatif ne se fait pas effacer par notre rotation. Le tri se fait sur le nom : les
     * dates ISO s'ordonnent lexicographiquement, il n'y a donc rien à analyser.
     *
     * **Sauf pour une horloge d'appareil mal réglée.** `popote-2099-01-01.enc` trierait
     * toujours en tête — aucune date réelle ne le dépasse jamais — et volerait pour de bon
     * une des [garder] places à une archive réellement récente, sans jamais s'effacer
     * lui-même. On l'exclut donc du tri et on l'efface directement plutôt que de le compter
     * parmi les gardées.
     */
    fun aSupprimer(
        fichiers: List<FichierDrive>,
        garder: Int = QUOTIDIENNES_GARDEES,
        aujourdHui: LocalDate = LocalDate.now(),
        espace: Espace = Espace.POPOTE,
    ): List<FichierDrive> {
        // Seules les quotidiennes de **cet** espace entrent dans la rotation : celles de
        // l'autre téléphone ne sont ni comptées ni effacées (phase 16, piège 2).
        val (futures, valides) = fichiers
            .filter { espace.estQuotidienne(it.nom) }
            .partition { dateQuotidienne(it.nom, espace).isAfter(aujourdHui) }
        return futures + valides.sortedByDescending { it.nom }.drop(garder.coerceAtLeast(0))
    }

    /**
     * Ce que la rotation de [espace] efface d'un dossier qui porte [fichiers] — le dossier
     * **après** l'envoi du moment, pour la même raison qu'[aSupprimer].
     *
     * Rien de l'autre espace, rien qui ne soit une archive (trousseaux, photos, fichiers
     * étrangers) : [Espace.archive] trie d'abord. Puis, dans l'ordre :
     *  1. **les doublons** — deux envois simultanés (tâche quotidienne et « Sauvegarder
     *     maintenant ») créaient chacun leur fichier sous le même nom, et le second ne partait
     *     plus jamais : la liste de restauration montrait deux fois « Celle du 26/09 ». Le
     *     plus récent ([FichierDrive.modifie]) reste ;
     *  2. **les socles** au-delà des [socles] plus récents — un compte, pas une date : un
     *     téléphone éteint un mois ne revient pas sur un dossier vidé ;
     *  3. **les points orphelins**, dont le socle est parti — ils ne se rejoueraient plus sur
     *     rien ;
     *  4. **les points en trop** : tous ceux des dernières [FENETRE_POINTS_TOUS] gardés, puis
     *     le dernier de chaque heure jusqu'à [FENETRE_POINTS_HORAIRES], puis le dernier de
     *     chaque jour. Ces âges se comptent depuis le **point le plus récent**, pas depuis
     *     l'horloge : même raison qu'au 2 ;
     *  5. **l'ancien format** (`popote-courant.enc`, `popote-AAAA-MM-JJ.enc`), une fois
     *     seulement que les socles couvrent à eux seuls [socles] semaines d'historique.
     */
    fun aPurger(
        fichiers: List<FichierDrive>,
        espace: Espace,
        zone: ZoneId = ZoneId.systemDefault(),
        socles: Int = SOCLES_GARDES,
    ): List<FichierDrive> {
        val archives = fichiers.mapNotNull { espace.archive(it) }
        val (uniques, doublons) = archives
            .groupBy { it.fichier.nom }
            .values
            .map { memes -> memes.sortedByDescending { it.fichier.modifie ?: Instant.EPOCH } }
            .let { groupes -> groupes.map { it.first() } to groupes.flatMap { it.drop(1) } }

        val soclesTries = uniques.filterIsInstance<Archive.Socle>().sortedByDescending { it.instant }
        val soclesGardes = soclesTries.take(socles.coerceAtLeast(1))
        val nomsGardes = soclesGardes.mapTo(HashSet()) { it.fichier.nom }
        val (vivants, orphelins) = uniques.filterIsInstance<Archive.Point>().partition { it.socle in nomsGardes }
        val anciens = if (soclesGardes.size >= socles) {
            uniques.filter { it is Archive.Courante || it is Archive.Quotidienne }
        } else {
            emptyList()
        }
        return (doublons + soclesTries.drop(socles.coerceAtLeast(1)) + orphelins + eclaircir(vivants, zone) + anciens)
            .map { it.fichier }
    }

    /** Les points de trop, selon l'âge — cf. [aPurger], point 4. Le plus récent de chaque case reste. */
    private fun eclaircir(points: List<Archive.Point>, zone: ZoneId): List<Archive.Point> {
        val reference = points.maxOfOrNull { it.instant } ?: return emptyList()
        val cases = HashSet<String>()
        return points.sortedByDescending { it.instant }.filter { point ->
            val age = Duration.between(point.instant, reference)
            val local = point.instant.atZone(zone)
            val case = when {
                age <= FENETRE_POINTS_TOUS -> return@filter false
                age <= FENETRE_POINTS_HORAIRES -> "h ${local.toLocalDate()} ${local.hour}"
                else -> "j ${local.toLocalDate()}"
            }
            !cases.add(case)
        }
    }

    /** La date portée par un nom de quotidienne — [Espace.estQuotidienne] en garantit le format. */
    private fun dateQuotidienne(nom: String, espace: Espace): LocalDate =
        LocalDate.parse(nom.removePrefix(espace.prefixe).removeSuffix(SUFFIXE))

    /** Extrait `files[].{id,name}` de la réponse de Drive. */
    internal fun lireListe(reponse: String): List<FichierDrive> =
        json.parseToJsonElement(reponse).jsonObject["files"]
            ?.jsonArray
            ?.mapNotNull { element ->
                val objet = element.jsonObject
                val id = objet["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val nom = objet["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                FichierDrive(
                    id = id,
                    nom = nom,
                    modifie = objet["modifiedTime"]?.jsonPrimitive?.content
                        ?.let { runCatching { Instant.parse(it) }.getOrNull() },
                    // Drive rend la taille en chaîne (`"size": "1234"`), pas en nombre.
                    taille = objet["size"]?.jsonPrimitive?.content?.toLongOrNull(),
                )
            }
            .orEmpty()

    /**
     * Le jeton de la page suivante, `null` quand la liste est complète.
     *
     * Drive omet la clé sur la dernière page ; une chaîne vide serait un jeton invalide et
     * compte donc pour une fin de liste elle aussi.
     */
    internal fun lireJetonPage(reponse: String): String? =
        json.parseToJsonElement(reponse).jsonObject["nextPageToken"]
            ?.jsonPrimitive
            ?.content
            ?.takeIf { it.isNotBlank() }

    /** Extrait `email` de la réponse du point `userinfo` de Google. */
    internal fun lireEmail(reponse: String): String? =
        json.parseToJsonElement(reponse).jsonObject["email"]?.jsonPrimitive?.content

    /**
     * Ce qui encadre le corps binaire d'un envoi `multipart/related` Drive v3, sans le
     * corps lui-même — pour pouvoir l'écrire en flux ([envoyerFichier]) plutôt que de le
     * concaténer en mémoire ([construireCorpsMultipart]).
     */
    internal data class PartiesMultipart(val frontiere: String, val avant: ByteArray, val apres: ByteArray)

    /**
     * Les en-têtes `multipart/related` d'un envoi Drive v3 : une partie JSON de métadonnées
     * avant le corps binaire, la frontière qui referme le tout après.
     *
     * `parents` n'est posé qu'à la **création** : Drive refuse qu'un PATCH le rejoue, et le
     * fichier est déjà dans le bon dossier de toute façon.
     */
    internal fun construireEnTetesMultipart(nomFichier: String, creation: Boolean = true): PartiesMultipart {
        val frontiere = "popote-${UUID.randomUUID()}"
        val metadonnees = if (creation) {
            """{"name":"$nomFichier","parents":["$DOSSIER_APPDATA"]}"""
        } else {
            """{"name":"$nomFichier"}"""
        }
        val avant = buildString {
            append("--").append(frontiere).append("\r\n")
            append("Content-Type: application/json; charset=UTF-8\r\n\r\n")
            append(metadonnees).append("\r\n")
            append("--").append(frontiere).append("\r\n")
            append("Content-Type: application/octet-stream\r\n\r\n")
        }.toByteArray(StandardCharsets.UTF_8)
        val apres = "\r\n--$frontiere--\r\n".toByteArray(StandardCharsets.UTF_8)
        return PartiesMultipart(frontiere, avant, apres)
    }

    /**
     * Le corps `multipart/related` complet d'un envoi Drive v3 : [construireEnTetesMultipart]
     * autour de [enveloppe], concaténés en un seul tableau — pour les envois assez petits
     * pour tenir en mémoire (le trousseau, les photos). [envoyerFichier] s'en passe.
     */
    internal fun construireCorpsMultipart(
        nomFichier: String,
        enveloppe: ByteArray,
        creation: Boolean = true,
    ): Pair<String, ByteArray> {
        val parties = construireEnTetesMultipart(nomFichier, creation)
        return parties.frontiere to (parties.avant + enveloppe + parties.apres)
    }

    // ── Transport ─────────────────────────────────────────────────────────────

    /**
     * Un appel à l'API Drive. Toute réponse hors 2xx devient une [IOException] : du point de
     * vue de la sauvegarde, un refus de Drive est transitoire — quota, panne — et se réessaie.
     * Exception : un refus du jeton devient une [AutorisationRevoquee], définitive — un jeton
     * révoqué ne se répare pas en réessayant la nuit suivante ([exceptionPour]).
     */
    private fun appel(
        url: String,
        methode: String,
        jeton: String,
        typeContenu: String? = null,
        corps: ByteArray? = null,
    ): ByteArray {
        val connexion = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = methode
            connectTimeout = DELAI_CONNEXION_MS
            readTimeout = DELAI_LECTURE_MS
            setRequestProperty("Authorization", "Bearer $jeton")
            typeContenu?.let { setRequestProperty("Content-Type", it) }
            if (corps != null) doOutput = true
        }
        try {
            corps?.let { charge -> connexion.outputStream.use { it.write(charge) } }
            return lireReponse(connexion, methode)
        } finally {
            connexion.disconnect()
        }
    }

    /**
     * Comme [appel], mais le corps est écrit par [ecrireCorps] directement dans le flux de
     * la connexion plutôt que passé en tableau — [tailleCorps] doit être sa taille exacte,
     * `setFixedLengthStreamingMode` l'exigeant pour ne jamais charger la requête entière en
     * mémoire (ni côté `HttpURLConnection`, qui bufferiserait sinon tout pour calculer la
     * longueur lui-même).
     */
    private fun appelFlux(
        url: String,
        methode: String,
        jeton: String,
        typeContenu: String,
        tailleCorps: Long,
        ecrireCorps: (OutputStream) -> Unit,
    ): ByteArray {
        val connexion = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = methode
            connectTimeout = DELAI_CONNEXION_MS
            readTimeout = DELAI_LECTURE_MS
            setRequestProperty("Authorization", "Bearer $jeton")
            setRequestProperty("Content-Type", typeContenu)
            doOutput = true
            setFixedLengthStreamingMode(tailleCorps)
        }
        try {
            connexion.outputStream.use { ecrireCorps(it) }
            return lireReponse(connexion, methode)
        } finally {
            connexion.disconnect()
        }
    }

    /** Toute réponse hors 2xx devient l'exception d'[exceptionPour]. */
    private fun lireReponse(connexion: HttpURLConnection, methode: String): ByteArray {
        val code = connexion.responseCode
        if (code !in 200..299) throw exceptionPour(code, methode, lireCorpsErreur(connexion))
        return if (methode == "DELETE") ByteArray(0) else connexion.inputStream.use { it.readBytes() }
    }

    /** Le corps d'une réponse d'erreur, s'il y en a un : c'est lui qui dit la vraie cause. */
    private fun lireCorpsErreur(connexion: HttpURLConnection): String? =
        runCatching { connexion.errorStream?.use { String(it.readBytes(), StandardCharsets.UTF_8) } }.getOrNull()
}
