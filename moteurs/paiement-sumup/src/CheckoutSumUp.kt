package fr.popote.terrain.sumup

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.sumup.merchant.reader.api.SumUpAPI
import com.sumup.merchant.reader.api.SumUpLogin
import com.sumup.merchant.reader.api.SumUpPayment
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orchestration du paiement carte réel — écrans natifs SumUp (`docs/decisions.md`, §
 * « Connexion SumUp native »). Vit dans `app-terrain` et nulle part ailleurs : `SumUpAPI`/
 * `SumUpPayment`/`SumUpLogin` viennent de `com.sumup:merchant-sdk`, dépendance du seul
 * module UI (`app-terrain/build.gradle.kts`) — `core` n'a pas d'Activity à leur offrir.
 *
 * **Connexion native, pas d'OAuth maison.** Le SDK ouvre son propre écran — e-mail + mot de
 * passe du compte SumUp, exactement ce qu'un popotier connaît déjà pour Google Drive — dès
 * lors qu'il a l'Affiliate Key de l'application. Ni client_id, ni client_secret, ni jeton de
 * renouvellement géré par ce projet : `SumUpAPI` garde sa propre session, [connecte] ne fait
 * que la refléter pour Compose.
 *
 * **Pourquoi ce pont, plutôt que `rememberLauncherForActivityResult`.**
 * `SumUpAPI.checkout`/`openLoginActivity` appellent eux-mêmes
 * `Activity.startActivityForResult` avec le code fourni, en dehors du mécanisme
 * `ActivityResultContract` — la réponse ne peut donc être captée que par un
 * `Activity.onActivityResult` classique (`MainActivity.kt`), qui relaie ici via
 * [onResultat]. [evenements] est le pont vers Compose pour l'issue d'un paiement ; [connexion]
 * joue le même rôle pour l'issue d'une connexion demandée explicitement depuis Administration.
 */
object CheckoutSumUp {

    /**
     * Identifiant d'application que le SDK SumUp exige à chaque appel — non secret (il
     * circule en clair dans chaque requête de connexion/paiement, jamais un mot de passe),
     * lié au paquet `fr.popote.terrain` plutôt qu'à un compte marchand particulier : une
     * seule clé pour toutes les popotes (`docs/decisions.md`, § « Connexion SumUp native »).
     * Obtenue une fois par le développeur depuis le tableau de bord SumUp — Réglages → Pour
     * les développeurs → Boîte à outils → Affiliate Keys, App ID `fr.popote.terrain`.
     */
    private const val AFFILIATE_KEY = "<AFFILIATE_KEY>" // À REMPLACER : clé Affiliate de VOTRE application (tableau de bord SumUp)

    // `internal`, pas `private` : les tests simulent la réponse du terminal en rappelant
    // [onResultat] directement (`payer()` — hors `SumUpAPI` réel, cf. [lancerCheckout]
    // ci-dessous — n'a nulle part où obtenir un vrai `requestCode` sous Robolectric).
    internal const val CODE_LOGIN = 47_001
    internal const val CODE_CHECKOUT = 47_002

    /** Ce qu'un encaissement carte a répondu, une fois l'écran SumUp refermé. */
    sealed interface Resultat {
        /** [reference] est `SumUpAPI.Response.TX_CODE` — le repère de rapprochement. */
        data class Reussi(val reference: String) : Resultat

        data class Echec(val motif: String) : Resultat

        data object Annule : Resultat
    }

    /** Ce qu'une connexion demandée explicitement (bouton Administration) a répondu. */
    sealed interface ResultatConnexion {
        data object Reussie : ResultatConnexion

        data class Echec(val motif: String) : ResultatConnexion
    }

    private val _evenements = MutableSharedFlow<Resultat>(extraBufferCapacity = 1)

    /** Le résultat d'un paiement — jamais celui d'une simple connexion. */
    val evenements = _evenements.asSharedFlow()

    private val _connexion = MutableSharedFlow<ResultatConnexion>(extraBufferCapacity = 1)

    /** Le résultat d'une connexion demandée depuis Administration → « Liaison SumUp ». */
    val connexion = _connexion.asSharedFlow()

    private val _connecte = MutableStateFlow(runCatching { SumUpAPI.isLoggedIn() }.getOrDefault(false))

    /** Reflète `SumUpAPI.isLoggedIn()` pour Compose — seule source de vérité de connexion. */
    val connecte = _connecte.asStateFlow()

    private var paiementEnAttente: PaiementEnAttente? = null

    private data class PaiementEnAttente(val montant: Double, val titre: String)

    // Trois points d'indirection vers le SDK réel, substituables en test — le SDK SumUp
    // (Bluetooth, écrans natifs) n'est pas exécutable sous Robolectric, contrairement au
    // reste de cet objet (des `MutableSharedFlow`/`MutableStateFlow` ordinaires). Par
    // défaut, chacun appelle exactement l'appel SDK qu'il remplace — aucun changement de
    // comportement en dehors des tests qui les substituent explicitement.
    internal var connecteReellement: () -> Boolean = { SumUpAPI.isLoggedIn() }
    internal var lancerConnexion: (Activity) -> Unit =
        { activity -> SumUpAPI.openLoginActivity(activity, SumUpLogin.builder(AFFILIATE_KEY).build(), CODE_LOGIN) }
    internal var lancerCheckout: (Activity, SumUpPayment) -> Unit =
        { activity, paiement -> SumUpAPI.checkout(activity, paiement, CODE_CHECKOUT) }

    /**
     * Test uniquement : force à la fois [connecte] (l'état affiché par Compose) et
     * [connecteReellement] (ce que `payer()` consulte réellement) — sous Robolectric,
     * `SumUpAPI.isLoggedIn()` ne peut refléter aucun des deux, le SDK n'étant jamais
     * réellement connecté.
     */
    internal fun forcerConnexionTest(connecte: Boolean) {
        _connecte.value = connecte
        connecteReellement = { connecte }
    }

    /** Ouvre l'écran natif SumUp — e-mail + mot de passe — pour connecter le compte marchand. */
    fun connecter(activity: Activity) {
        lancerConnexion(activity)
    }

    /** Ferme la session ouverte — le prochain encaissement carte redemandera une connexion. */
    fun deconnecter() {
        SumUpAPI.logout()
        _connecte.value = false
    }

    /**
     * Lance le paiement de [montant] — connexion à la volée si la session SumUp du
     * processus n'est pas encore ouverte ([SumUpAPI.isLoggedIn]), sinon écran de paiement
     * ouvert directement.
     */
    fun payer(activity: Activity, montant: Double, titre: String) {
        if (connecteReellement()) {
            lancerEcranPaiement(activity, montant, titre)
        } else {
            paiementEnAttente = PaiementEnAttente(montant, titre)
            lancerConnexion(activity)
        }
    }

    private fun lancerEcranPaiement(activity: Activity, montant: Double, titre: String) {
        val paiement = SumUpPayment.builder(
            BigDecimal.valueOf(montant).setScale(2, RoundingMode.HALF_UP),
            SumUpPayment.Currency.EUR,
        )
            .title(titre)
            // Une transaction par tentative : un nouvel identifiant à chaque appel, plutôt
            // que d'en garder un entre deux essais après un refus — sans quoi une nouvelle
            // tentative après carte refusée risquerait `ERROR_DUPLICATE_FOREIGN_TX_ID`.
            .foreignTransactionId(UUID.randomUUID().toString())
            .build()
        lancerCheckout(activity, paiement)
    }

    /**
     * À appeler depuis `MainActivity.onActivityResult` — le seul endroit qui le reçoit.
     * Sans effet pour tout `requestCode` étranger à ce module. [resultCode] est le signal
     * standard d'Android (`RESULT_OK`/`RESULT_CANCELED`) ; il ne sert qu'à qualifier
     * l'annulation en `CODE_CHECKOUT` ci-dessous quand le SDK SumUp lui-même ne fournit
     * aucun `RESULT_CODE` dans les extras — le seul cas déjà validé (écran refermé par le
     * popotier avant toute réponse) garde exactement le mapping actuel.
     */
    fun onResultat(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODE_LOGIN -> {
                val reussie = connecteReellement()
                _connecte.value = reussie
                val attente = paiementEnAttente
                paiementEnAttente = null
                val motif = { data?.extras?.getString(SumUpAPI.Response.MESSAGE) ?: "Connexion au compte SumUp refusée." }
                when {
                    reussie && attente != null -> lancerEcranPaiement(activity, attente.montant, attente.titre)
                    reussie -> _connexion.tryEmit(ResultatConnexion.Reussie)
                    attente != null -> _evenements.tryEmit(Resultat.Echec(motif()))
                    else -> _connexion.tryEmit(ResultatConnexion.Echec(motif()))
                }
            }

            CODE_CHECKOUT -> {
                val extras = data?.extras
                // Absent — onglet refermé par le popotier avant toute réponse — vaut
                // annulation, pas échec : `getInt` avec défaut plutôt qu'un `?:` sur un
                // type primitif.
                val code = extras?.getInt(SumUpAPI.Response.RESULT_CODE, -1) ?: -1
                val resultat = when {
                    code == SumUpAPI.Response.ResultCode.SUCCESSFUL ->
                        Resultat.Reussi(extras?.getString(SumUpAPI.Response.TX_CODE).orEmpty())
                    code != -1 -> Resultat.Echec(
                        extras?.getString(SumUpAPI.Response.MESSAGE) ?: "Paiement refusé (code $code)."
                    )
                    // Aucun RESULT_CODE SumUp : le seul cas éprouvé (`docs/todo.md`) est
                    // l'écran refermé par le popotier, qu'Android rapporte en
                    // RESULT_CANCELED — mapping inchangé, en Annule comme avant.
                    resultCode == Activity.RESULT_CANCELED -> Resultat.Annule
                    // Ni RESULT_CODE SumUp, ni RESULT_CANCELED Android : combinaison
                    // jamais observée (activité tuée ?) — qualifiée en Echec plutôt que
                    // noyée dans Annule, qui suggérait à tort un refus explicite.
                    else -> Resultat.Echec("Réponse du terminal inattendue (resultCode=$resultCode).")
                }
                _evenements.tryEmit(resultat)
            }
        }
    }
}

/** Déballe l'`Activity` derrière un `Context` de composable — `SumUpAPI` en exige une. */
tailrec fun Context.commeActivite(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.commeActivite()
    else -> error("Aucune Activity derrière ce Context — SumUpAPI ne peut pas s'y accrocher")
}
