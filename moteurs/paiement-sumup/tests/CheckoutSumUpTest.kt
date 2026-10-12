package fr.popote.terrain.sumup

import android.content.Intent
import androidx.activity.ComponentActivity
import com.sumup.merchant.reader.api.SumUpAPI
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Couvre `CheckoutSumUp.onResultat` — jusqu'ici seulement éprouvé indirectement, via le
 * blocage « compte non connecté » d'`EcransTest`. Le SDK SumUp réel (Bluetooth, écrans
 * natifs) n'est pas exécutable sous Robolectric ; les trois points d'indirection ajoutés à
 * `CheckoutSumUp.kt` pour la tâche précédente (`connecteReellement`/`lancerConnexion`/
 * `lancerCheckout`) sont substitués ici, y compris dans la branche `CODE_LOGIN` d'onResultat
 * (qui appelait jusqu'ici `SumUpAPI.isLoggedIn()` en dur — désormais `connecteReellement()`,
 * réutilisant le même seam plutôt que d'en ajouter un quatrième).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CheckoutSumUpTest {

    private lateinit var activite: android.app.Activity

    @Before
    fun preparer() {
        // `CheckoutSumUp` est un `object` : son état survivrait sinon d'un test à l'autre
        // dans cette même classe (constaté en pratique dans EcransTest.preparer()).
        CheckoutSumUp.forcerConnexionTest(false)
        CheckoutSumUp.lancerConnexion = {}
        CheckoutSumUp.lancerCheckout = { _, _ -> }
        activite = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
    }

    private fun intentReussi(reference: String = "TX-TEST") = Intent()
        .putExtra(SumUpAPI.Response.RESULT_CODE, SumUpAPI.Response.ResultCode.SUCCESSFUL)
        .putExtra(SumUpAPI.Response.TX_CODE, reference)

    private fun intentEchec(motif: String) = Intent()
        .putExtra(SumUpAPI.Response.RESULT_CODE, SumUpAPI.Response.ResultCode.ERROR_TRANSACTION_FAILED)
        .putExtra(SumUpAPI.Response.MESSAGE, motif)

    /**
     * Démarre un collecteur *avant* [action] (`CoroutineStart.UNDISPATCHED` : le corps
     * s'exécute immédiatement jusqu'à sa première suspension, donc l'abonnement à [flow] est
     * posé avant que [action] n'émette) — indispensable ici : `evenements`/`connexion` sont
     * des `MutableSharedFlow(replay = 0)`, un abonné qui arrive après coup ne reçoit jamais
     * ce qui a été émis avant lui, quel que soit `extraBufferCapacity`.
     */
    private fun <T> collecterUnSeul(flow: Flow<T>, action: () -> Unit): T? {
        var resultat: T? = null
        runBlocking {
            val job = launch(start = CoroutineStart.UNDISPATCHED) { resultat = flow.first() }
            action()
            withTimeoutOrNull(2_000) { job.join() }
            job.cancel()
        }
        return resultat
    }

    @Test
    fun `un paiement reussi emet Resultat Reussi avec la reference`() {
        val resultat = collecterUnSeul(CheckoutSumUp.evenements) {
            CheckoutSumUp.onResultat(
                activite,
                CheckoutSumUp.CODE_CHECKOUT,
                android.app.Activity.RESULT_OK,
                intentReussi("TX-42"),
            )
        }
        assertEquals(CheckoutSumUp.Resultat.Reussi("TX-42"), resultat)
    }

    @Test
    fun `un paiement refuse emet Resultat Echec avec le motif`() {
        val resultat = collecterUnSeul(CheckoutSumUp.evenements) {
            CheckoutSumUp.onResultat(
                activite,
                CheckoutSumUp.CODE_CHECKOUT,
                android.app.Activity.RESULT_OK,
                intentEchec("Carte refusée."),
            )
        }
        assertEquals(CheckoutSumUp.Resultat.Echec("Carte refusée."), resultat)
    }

    @Test
    fun `un ecran de paiement referme sans extras et resultCode CANCELED emet Resultat Annule`() {
        // Le seul cas éprouvé (`docs/todo.md`) : le popotier referme l'écran natif avant
        // toute réponse SumUp, et Android rapporte RESULT_CANCELED — mapping inchangé.
        val resultat = collecterUnSeul(CheckoutSumUp.evenements) {
            CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_CHECKOUT, android.app.Activity.RESULT_CANCELED, null)
        }
        assertEquals(CheckoutSumUp.Resultat.Annule, resultat)
    }

    @Test
    fun `un ecran de paiement referme sans extras et un resultCode inattendu emet un Echec qualifie`() {
        // Combinaison jamais observée en pratique : ni RESULT_CODE SumUp, ni
        // RESULT_CANCELED Android (activité tuée ?) — qualifiée en Echec plutôt que noyée
        // dans Annule, qui suggérait à tort un refus explicite du popotier.
        val resultat = collecterUnSeul(CheckoutSumUp.evenements) {
            CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_CHECKOUT, android.app.Activity.RESULT_OK, null)
        }
        assertEquals(
            CheckoutSumUp.Resultat.Echec("Réponse du terminal inattendue (resultCode=${android.app.Activity.RESULT_OK})."),
            resultat,
        )
    }

    @Test
    fun `une connexion reussie sans paiement en attente emet ResultatConnexion Reussie`() {
        CheckoutSumUp.connecteReellement = { true }
        val resultat = collecterUnSeul(CheckoutSumUp.connexion) {
            CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_LOGIN, android.app.Activity.RESULT_OK, Intent())
        }
        assertEquals(CheckoutSumUp.ResultatConnexion.Reussie, resultat)
        assertTrue("connecte doit refléter la connexion réussie", CheckoutSumUp.connecte.value)
    }

    @Test
    fun `une connexion refusee sans paiement en attente emet ResultatConnexion Echec`() {
        CheckoutSumUp.connecteReellement = { false }
        val resultat = collecterUnSeul(CheckoutSumUp.connexion) {
            CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_LOGIN, android.app.Activity.RESULT_OK, Intent())
        }
        assertEquals(
            CheckoutSumUp.ResultatConnexion.Echec("Connexion au compte SumUp refusée."),
            resultat,
        )
    }

    @Test
    fun `une connexion reussie avec un paiement en attente relance directement le paiement`() {
        var checkoutAppele = false
        CheckoutSumUp.lancerCheckout = { _, _ -> checkoutAppele = true }
        CheckoutSumUp.connecteReellement = { false }
        // Comme le ferait le SDK réel : `payer()` sans session ouverte mémorise le montant et
        // ouvre l'écran de connexion, avant même qu'onResultat(CODE_LOGIN, ...) ne rappelle.
        CheckoutSumUp.payer(activite, 12.5, "Vente comptoir")
        CheckoutSumUp.connecteReellement = { true }

        CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_LOGIN, android.app.Activity.RESULT_OK, Intent())

        assertTrue("le paiement en attente doit relancer directement l'écran de paiement", checkoutAppele)
    }

    @Test
    fun `une connexion refusee avec un paiement en attente emet un Echec, pas un silence`() {
        CheckoutSumUp.connecteReellement = { false }
        CheckoutSumUp.payer(activite, 12.5, "Vente comptoir")

        val resultat = collecterUnSeul(CheckoutSumUp.evenements) {
            CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_LOGIN, android.app.Activity.RESULT_OK, Intent())
        }

        assertTrue(
            "un paiement en attente resté sans compte connecté doit avertir, pas disparaître",
            resultat is CheckoutSumUp.Resultat.Echec,
        )
    }

    @Test
    fun `un evenement emis sans collecteur actif n est pas rejoue a un collecteur ulterieur`() {
        CheckoutSumUp.onResultat(activite, CheckoutSumUp.CODE_CHECKOUT, android.app.Activity.RESULT_OK, intentReussi())

        val resultat = runBlocking { withTimeoutOrNull(200) { CheckoutSumUp.evenements.first() } }

        assertNull(
            "un abonnement posé après coup (ex. recomposition tardive) ne doit pas recevoir un vieux résultat",
            resultat,
        )
    }
}
