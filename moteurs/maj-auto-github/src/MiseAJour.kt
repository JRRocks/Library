package fr.popote.terrain.maj

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * La mise à jour de l'application par elle-même : demander à GitHub quelle est la dernière
 * version publiée, et, si elle est plus récente que celle qui tourne, proposer de la
 * télécharger puis de l'installer.
 *
 * **Pourquoi.** L'application ne passe pas par le Play Store — elle sert une popote, pas un
 * public. Sans ce mécanisme, chaque correction se livre par câble : brancher le téléphone,
 * `adb install`, sur une machine qui a le SDK. Autant dire qu'elle ne se livre pas.
 *
 * **Par où.** [DEPOT] est un dépôt **public** qui ne contient que les APK publiés ; le code
 * et les fiches nominatives restent dans le dépôt privé. C'est ce qui permet d'interroger
 * l'API GitHub **sans le moindre jeton** : un secret embarqué dans un APK distribué n'est
 * pas un secret, et la portée « lire les versions d'un dépôt privé » n'existe pas sans en
 * donner bien davantage. La publication, elle, est faite par la CI
 * (`.github/workflows/version.yml`) ; la mise en place est décrite dans
 * `docs/publication.md`.
 *
 * **Ce qui décide qu'une version est « plus récente ».** Le `versionCode`, seul entier
 * qu'Android compare et que le popotier ne voit jamais. GitHub ne le connaît pas : il est
 * donc porté par le **nom du fichier** attaché à la version —
 * `popote-terrain-<versionName>-<versionCode>.apk` — que la CI fabrique à partir des
 * valeurs de `build.gradle.kts`. Un APK déposé à la main sans ce suffixe est ignoré plutôt
 * que proposé à l'aveugle.
 *
 * **Ce qui n'est pas vérifiable ici.** Ni réseau GitHub, ni `PackageManager` réel, ni
 * installateur dans cet environnement. Les parts pures — [lirePublication], [aProposer],
 * [versionCodeDuNom] — se testent seules (`MiseAJourTest.kt`) ; le reste se constate sur
 * le téléphone.
 */
object MiseAJour {

    /** Le dépôt public des versions. Doit rester identique à `DEPOT_VERSIONS` de la CI. */
    const val DEPOT = "JRRocks/Popote-Compagnie"

    private const val URL_DERNIERE = "https://api.github.com/repos/$DEPOT/releases/latest"

    // Court, et volontairement : l'interrogation se fait au lancement, pendant que le
    // popotier attend son comptoir. Mieux vaut renoncer en silence que faire patienter.
    private const val DELAI_CONNEXION_MS = 10_000
    private const val DELAI_LECTURE_MS = 10_000

    /** Le téléchargement, lui, a tout son temps : l'écran affiche sa progression. */
    private const val DELAI_LECTURE_APK_MS = 60_000

    /** Le dossier privé où atterrissent les APK téléchargés (`res/xml/file_paths.xml`). */
    private const val DOSSIER = "maj"

    /** Un téléchargement inachevé porte ce suffixe : il ne sera jamais pris pour un APK entier. */
    private const val SUFFIXE_PARTIEL = ".part"

    /**
     * `popote-terrain-13.21-maj-en-ligne-22.apk` → nom `13.21-maj-en-ligne`, code `22`.
     *
     * Le `.+` est gourmand mais le `(\d+)` final est ancré sur `.apk` : c'est bien le
     * dernier groupe de chiffres qui fait le code, même quand le nom de version en contient
     * lui-même (`13.21` ici).
     */
    private val NOM_APK = Regex("""^popote-terrain-(.+)-(\d+)\.apk$""")

    private val json = Json { ignoreUnknownKeys = true }

    /** La dernière version publiée, telle que GitHub la décrit. */
    data class Publication(
        val versionCode: Long,
        val versionNom: String,
        val nomFichier: String,
        val url: String,
        val octets: Long,
        val notes: String,
    )

    // ── Interrogation ───────────────────────────────────────────────────────────

    private val verrou = Mutex()
    private var interrogee = false
    private var retenue: Publication? = null

    /**
     * La version à proposer au popotier, ou `null` s'il n'y a rien à proposer.
     *
     * **Une seule fois par processus**, quoi qu'il arrive : la réponse est retenue, si bien
     * qu'une rotation d'écran, un retour d'arrière-plan ou une recomposition ne relancent
     * pas la requête. C'est aussi ce qui fait qu'un refus (« Plus tard ») tient jusqu'au
     * prochain lancement, sans qu'on ait à ranger quoi que ce soit sur disque.
     *
     * **Silencieuse en cas d'échec.** Pas de réseau, GitHub en panne, dépôt momentanément
     * inaccessible : l'application se lance normalement et n'en dit rien. Une popote au
     * comptoir n'a rien à faire d'un message d'erreur sur une mise à jour qu'elle n'a pas
     * demandée.
     */
    suspend fun publicationDuLancement(contexte: Context): Publication? = verrou.withLock {
        if (!interrogee) {
            interrogee = true
            retenue = runCatching { aProposer(interroger(), versionInstallee(contexte)) }.getOrNull()
        }
        retenue
    }

    /** Le `versionCode` de l'APK en train de tourner. */
    fun versionInstallee(contexte: Context): Long {
        val infos = contexte.packageManager.getPackageInfo(contexte.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(infos)
    }

    /** Interroge l'API GitHub et rend la dernière version publiée, ou `null` si elle est illisible. */
    private suspend fun interroger(): Publication? = withContext(Dispatchers.IO) {
        val connexion = (URL(URL_DERNIERE).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = DELAI_CONNEXION_MS
            readTimeout = DELAI_LECTURE_MS
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            // GitHub refuse (403) les requêtes sans `User-Agent`, y compris anonymes.
            setRequestProperty("User-Agent", "popote-terrain")
        }
        try {
            // 404 tant qu'aucune version n'a été publiée : ce n'est pas une panne.
            if (connexion.responseCode !in 200..299) return@withContext null
            lirePublication(connexion.inputStream.bufferedReader().use { it.readText() })
        } finally {
            connexion.disconnect()
        }
    }

    // ── Téléchargement ──────────────────────────────────────────────────────────

    /**
     * Télécharge l'APK de [publication] dans le stockage privé et rend le fichier obtenu.
     *
     * [onProgression] reçoit une fraction entre 0 et 1 — c'est ce que la barre de l'écran
     * affiche. Elle n'est appelée que par paliers, pas à chaque bloc lu : recomposer Compose
     * des milliers de fois ferait plus de travail que le téléchargement lui-même.
     *
     * L'écriture se fait sous un nom en `.part` renommé à la toute fin. Une coupure de
     * réseau, un processus tué, un écran quitté en cours de route : ce qui reste sur disque
     * ne pourra jamais être pris pour un APK complet et proposé à l'installation.
     */
    suspend fun telecharger(
        contexte: Context,
        publication: Publication,
        onProgression: (Float) -> Unit,
    ): File = withContext(Dispatchers.IO) {
        val dossier = File(contexte.filesDir, DOSSIER).apply { mkdirs() }
        // Les APK des versions précédentes n'ont plus de raison d'occuper le téléphone.
        dossier.listFiles()?.forEach { if (it.name != publication.nomFichier) it.delete() }

        val cible = File(dossier, publication.nomFichier)
        val partiel = File(dossier, publication.nomFichier + SUFFIXE_PARTIEL)
        val connexion = (URL(publication.url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = DELAI_CONNEXION_MS
            readTimeout = DELAI_LECTURE_APK_MS
            setRequestProperty("User-Agent", "popote-terrain")
        }
        try {
            if (connexion.responseCode !in 200..299) {
                throw IOException("Téléchargement refusé (code ${connexion.responseCode}).")
            }
            // `Content-Length` manque sur une réponse compressée ou tronquée ; la taille
            // annoncée par GitHub avec la version fait alors référence.
            val total = connexion.contentLengthLong.takeIf { it > 0 } ?: publication.octets
            var recus = 0L
            var dernierPalier = -1
            partiel.outputStream().use { sortie ->
                connexion.inputStream.use { entree ->
                    val tampon = ByteArray(64 * 1024)
                    while (true) {
                        val lus = entree.read(tampon)
                        if (lus < 0) break
                        sortie.write(tampon, 0, lus)
                        recus += lus
                        if (total > 0) {
                            val palier = (recus * 100 / total).toInt()
                            if (palier != dernierPalier) {
                                dernierPalier = palier
                                onProgression((palier / 100f).coerceIn(0f, 1f))
                            }
                        }
                    }
                }
            }
            if (total > 0 && recus != total) {
                throw IOException("Téléchargement incomplet ($recus sur $total octets).")
            }
            cible.delete()
            if (!partiel.renameTo(cible)) throw IOException("Fichier téléchargé impossible à ranger.")
            onProgression(1f)
            cible
        } catch (erreur: Throwable) {
            partiel.delete()
            throw erreur
        } finally {
            connexion.disconnect()
        }
    }

    // ── Installation ────────────────────────────────────────────────────────────

    /**
     * Le téléphone laisse-t-il cette application en installer une autre ?
     *
     * Depuis Android 8, « sources inconnues » n'est plus un réglage global mais une
     * autorisation **par application** : il faut donc l'avoir accordée à celle-ci en
     * particulier, et il n'existe aucun moyen de la demander par une boîte de dialogue
     * système — seulement d'ouvrir l'écran de réglages ([ecranSourcesInconnues]).
     */
    fun peutInstaller(contexte: Context): Boolean = contexte.packageManager.canRequestPackageInstalls()

    /** L'écran de réglages où accorder l'autorisation ci-dessus, épinglé sur cette application. */
    fun ecranSourcesInconnues(contexte: Context): Intent =
        Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${contexte.packageName}"),
        )

    /**
     * Présente l'APK téléchargé à l'installateur du système.
     *
     * Le fichier vit dans le stockage privé de l'application : l'installateur ne peut pas le
     * lire directement, il faut lui passer une `content://` du [FileProvider] assortie d'une
     * permission de lecture — la même mécanique que les exports (`file_paths.xml`).
     *
     * C'est le système qui décide ensuite, et il refusera si l'APK n'est pas signé de la
     * **même clé** que l'application installée. D'où le refus, côté CI, de publier un APK
     * non signé : il ne remplacerait jamais rien.
     */
    fun installer(contexte: Context, fichier: File) {
        val uri = FileProvider.getUriForFile(contexte, "${contexte.packageName}.fileprovider", fichier)
        contexte.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    // ── Parts pures, testables sans réseau ni téléphone ─────────────────────────

    /**
     * La version publiée décrite par la réponse de `/releases/latest`, ou `null` si elle ne
     * porte aucun APK exploitable.
     *
     * Une version peut très bien être publiée sans APK (brouillon rattrapé à la main,
     * publication interrompue), ou en porter plusieurs, ou en porter un dont le nom ne suit
     * pas la convention. Dans tous ces cas on préfère ne rien proposer : une mise à jour
     * qu'on ne sait pas dater ne se compare à rien.
     */
    internal fun lirePublication(charge: String): Publication? {
        val racine = runCatching { json.parseToJsonElement(charge).jsonObject }.getOrNull() ?: return null
        val notes = texte(racine, "body").orEmpty()
        val actifs = runCatching { racine["assets"]?.jsonArray }.getOrNull() ?: return null
        return actifs.firstNotNullOfOrNull { element ->
            val actif = runCatching { element.jsonObject }.getOrNull() ?: return@firstNotNullOfOrNull null
            val nom = texte(actif, "name") ?: return@firstNotNullOfOrNull null
            val correspondance = NOM_APK.matchEntire(nom) ?: return@firstNotNullOfOrNull null
            val code = correspondance.groupValues[2].toLongOrNull() ?: return@firstNotNullOfOrNull null
            val url = texte(actif, "browser_download_url") ?: return@firstNotNullOfOrNull null
            Publication(
                versionCode = code,
                versionNom = correspondance.groupValues[1],
                nomFichier = nom,
                url = url,
                octets = texte(actif, "size")?.toLongOrNull() ?: 0L,
                notes = notes,
            )
        }
    }

    /**
     * Ce qu'il y a à proposer : [publication] si elle est **strictement** plus récente que
     * [installee], `null` sinon.
     *
     * Strictement : réinstaller le même `versionCode` ne corrige rien et Android le refuse
     * la moitié du temps ; proposer un code **inférieur** reviendrait à faire régresser le
     * téléphone d'un popotier au premier APK mal nommé.
     */
    internal fun aProposer(publication: Publication?, installee: Long): Publication? =
        publication?.takeIf { it.versionCode > installee }

    /** Le `versionCode` porté par un nom d'APK, ou `null` si le nom ne suit pas la convention. */
    internal fun versionCodeDuNom(nom: String): Long? =
        NOM_APK.matchEntire(nom)?.groupValues?.get(2)?.toLongOrNull()

    /**
     * La valeur textuelle d'une clé, ou `null` si elle manque, vaut `null` (GitHub écrit
     * `"body": null` pour une version sans notes) ou n'est pas une valeur simple.
     *
     * Ce qui arrive de GitHub n'a rien à faire échouer : une clé inattendue vaut mieux
     * qu'une exception au lancement de l'application.
     */
    private fun texte(objet: JsonObject, cle: String): String? =
        runCatching { objet[cle]?.jsonPrimitive?.contentOrNull }.getOrNull()
}
