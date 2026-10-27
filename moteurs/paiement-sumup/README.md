# Paiement carte SumUp : SDK Android, lecteur Bluetooth, connexion native

| Champ | Valeur |
|---|---|
| **Identifiant** | `paiement-sumup` |
| **Statut** | 🟡 **Testé.** Sur appareil réel, l'appel au SDK atteint l'écran de connexion SumUp (v13.19, après le correctif du piège 1), qui plante aussitôt (piège 2) ; le correctif de ce second plantage (v13.20) n'a jamais été confirmé sur l'appareil. **Aucun encaissement réel n'a encore abouti** (toujours vrai au 2026-10-27, essai sur terminal physique en attente). La logique de résultat est couverte par des tests Robolectric. Limite du SDK connue par décompilation, non vérifiée sur matériel : un `Echec` ou un `Annule` ne prouve pas que la carte n'a pas été débitée (voir « Points de vigilance »). ⚠️ Défaut suspecté dans `src/` : un `Reussi` émis pendant la recréation de l'activité peut être perdu (voir « Points de vigilance ») |
| **Plateforme** | Android natif, Kotlin, minSdk 26, AGP 8.8.0, Kotlin 2.0.21, Java 17 |
| **Dépendances** | `com.sumup:merchant-sdk:6.0.0` (dépôt `https://maven.sumup.com/releases`), `com.google.android.gms:play-services-location:21.3.0` (**obligatoire**, voir plus bas), `com.android.tools:desugar_jdk_libs:2.1.5` |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé, code source de [Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie)), commit `08c211a` (2026-10-12) ; `CheckoutSumUp.kt` et `CheckoutSumUpTest.kt` identiques à `6149a81` (2026-10-26), hors gabarit `<AFFILIATE_KEY>`. Les numéros de ligne de Popote cités dans cette fiche renvoient à `6149a81` |
| **Utilisé dans** | Popote Compagnie |
| **Fichiers d'origine** | `app-terrain/src/main/kotlin/fr/popote/terrain/sumup/CheckoutSumUp.kt`, `PopoteApplication.kt`, `MainActivity.kt` (`onActivityResult`), `ui/Comptoir.kt` (`FeuilleCarteSumUp`, `avertirDebitEventuel`, `DELAI_GARDE_CARTE_MS`), `ui/Comptes.kt` et `ui/Cotisation.kt` (même feuille), `docs/decisions.md` (sections « Paiement carte SumUp » et « Connexion SumUp native »), `docs/todo.md` (« L'essai réel sur terminal SumUp physique ») |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

On encaisse un montant par carte bancaire sur un **lecteur SumUp Bluetooth externe** (Solo,
Air, 3G, PIN+) depuis l'app, avec les **écrans natifs du SDK** : connexion au compte
marchand, montant, insertion de la carte, PIN, confirmation. L'app ne garde **aucun secret** :
le marchand se connecte avec l'e-mail et le mot de passe de son compte SumUp dans l'écran
SumUp, et le SDK conserve lui-même la session.

On écrit en base **seulement après** un `Resultat.Reussi` authentique. Une carte refusée ou
une saisie annulée ne laisse aucune trace. L'inverse n'est pas garanti : un `Echec` ou un
`Annule` ne prouve pas que la carte n'a pas été débitée (voir « Points de vigilance »).

## Architecture retenue (et celles qu'on a abandonnées)

1. ~~Clé API REST (`GET /v0.1/me`)~~ : permet de *vérifier* un compte, pas de *déclencher* un
   encaissement sur un lecteur physique.
2. ~~OAuth « authorization code » maison (AppAuth, Custom Tabs, `refresh_token`)~~ : marchait,
   mais trop compliqué pour l'utilisateur final et cela supposait de garder un secret client.
   **Supprimé.**
3. ✅ **Connexion native du SDK** : `SumUpAPI.openLoginActivity(activity, SumUpLogin.builder(AFFILIATE_KEY).build(), code)`,
   **sans** `accessToken`. Le SDK affiche son propre écran de connexion et garde sa session.
   Il n'y a ni `client_id`, ni `client_secret`, ni jeton à renouveler.

**Clé Affiliate** : une seule clé par **application** (liée à l'`applicationId`), pas une par
client. Elle n'est pas secrète, puisqu'elle circule en clair dans chaque requête : on
l'embarque en `const val`. On l'obtient dans le tableau de bord SumUp du développeur, sous
Réglages → Pour les développeurs → Boîte à outils → Affiliate Keys, avec l'App ID égal à
l'`applicationId`. Dans `src/CheckoutSumUp.kt`, la valeur est remplacée par le gabarit
`<AFFILIATE_KEY>`.

## Composants

| Fichier (`src/`) | Rôle | Dépend d'Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| `CheckoutSumUp.kt` | Connexion native, encaissement (`SumUpAPI.checkout`), nouvel identifiant de transaction à chaque tentative, pont `onActivityResult` → Compose, correspondance des résultats | Oui (SDK SumUp, `Activity`, `Intent`) | `CheckoutSumUpTest.kt`, 9 tests Robolectric, SDK substitué | 🟡 connexion lancée sur appareil réel ; ni connexion aboutie ni encaissement réel consignés · ⚠️ `Reussi` perdu si l'activité est recréée pendant le paiement (suspecté, non vérifié ; patch possible : garder le dernier résultat jusqu'à sa lecture) |
| *(hors `src/`)* amorçage et relais | `SumUpState.init` dans `Application.onCreate()`, `MainActivity.onActivityResult` qui relaie vers `onResultat` | Oui | Aucun : le SDK ne tourne pas sous Robolectric | 🟡 piège 1 corrigé, confirmé sur appareil (v13.19) ; piège 2 corrigé (v13.20), jamais confirmé sur l'appareil |
| *(hors `src/`)* interface Popote | `FeuilleCarteSumUp`, `avertirDebitEventuel`, délai de garde de 3 min (`ui/Comptoir.kt`), réutilisés par `ui/Comptes.kt` et `ui/Cotisation.kt` ; vente en attente sauvée dans un `rememberSaveable` | Oui (Compose) | Dans Popote, non repris : `EcransTest` (délai de garde, vente en attente qui survit à une restauration d'état) | 🟡 jamais exercée avec un vrai terminal |

## Pièges connus (à appliquer d'office)

| # | Symptôme | Cause | Correctif | Où constaté |
|---|---|---|---|---|
| 1 | `NullPointerException` dans `ReaderModuleCoreState.getDaggerHandler()` au premier appel `SumUpAPI` | Le SDK **ne s'initialise pas tout seul** (aucun `<provider>` d'auto-init) et la doc d'intégration ne le dit pas | `SumUpState.init(this)` (`com.sumup.reader.sdk.api.SumUpState`) dans `Application.onCreate()`, **avant tout autre appel SumUp** | Appareil réel, v13.18. Correctif confirmé en v13.19 : l'écran de connexion s'ouvre (`docs/decisions.md` l. 1513-1514) |
| 2 | `NoClassDefFoundError: com/google/android/gms/location/LocationServices` à l'ouverture de l'écran de connexion | Le SDK instancie `LocationServices` sans déclarer la dépendance dans son `.pom` | `implementation("com.google.android.gms:play-services-location:21.3.0")` dans le **module qui porte le SDK** | Appareil réel, v13.19. Correctif livré en v13.20, jamais confirmé : « rien n'a été rapporté depuis » (`docs/todo.md` l. 328) |
| 3 | « Réessayer » après un refus, une interruption ou le délai de garde : `ERROR_DUPLICATE_FOREIGN_TX_ID`, ou **double débit** | (a) Même `foreignTransactionId` réutilisé. (b) `Echec` ou `Annule` ne prouvent pas que la carte n'a pas été débitée (point de vigilance ci-dessous) | (a) **Nouveau `UUID` à chaque tentative** (dans `src/`). (b) Avant tout « Réessayer », prévenir l'opérateur de vérifier le relevé SumUp : `avertirDebitEventuel()` (hors `src/`, extrait plus bas). Elle empêche la nouvelle tentative à l'aveugle, mais **ne détecte rien** | Aucun des deux sur appareil, faute d'encaissement réel. (a) est une précaution prise à la conception (`docs/decisions.md` l. 1146-1148 ; `CheckoutSumUp.kt` l. 145-148). (b) vient de la décompilation du SDK |
| 4 | Le résultat n'arrive jamais avec `rememberLauncherForActivityResult` | `checkout()` et `openLoginActivity()` appellent eux-mêmes `startActivityForResult` (API classique) | Surcharger `Activity.onActivityResult` et relayer vers `CheckoutSumUp.onResultat(...)` | Décompilation du SDK, « contrainte confirmée, pas contournable » (`docs/decisions.md` l. 1137-1141) |
| 5 | Compilation : API Java 8+ absentes en minSdk 26 | Dépendances transitives du SDK (`auth-sdk`, etc.) | `isCoreLibraryDesugaringEnabled = true` et `coreLibraryDesugaring(desugar_jdk_libs)` | Compilation |

Méthode qui a marché quand la doc SumUp était muette : **décompiler l'AAR** (`javap -p -constants`
sur les `.class` du cache Gradle) plutôt que deviner. `SumUpAPI` vit dans l'artefact
transitif `com.sumup:reader`, pas dans `merchant-sdk`, qui n'est qu'une façade.

## Intégration dans une nouvelle app

1. **`settings.gradle.kts`**, dans `dependencyResolutionManagement.repositories` :
   ```kotlin
   maven { url = uri("https://maven.sumup.com/releases") }
   ```
2. **`libs.versions.toml`** :
   ```toml
   sumup = "6.0.0"
   playServicesLocation = "21.3.0"
   desugarJdkLibs = "2.1.5"
   sumup-merchant-sdk = { group = "com.sumup", name = "merchant-sdk", version.ref = "sumup" }
   play-services-location = { group = "com.google.android.gms", name = "play-services-location", version.ref = "playServicesLocation" }
   android-desugar-jdk-libs = { group = "com.android.tools", name = "desugar_jdk_libs", version.ref = "desugarJdkLibs" }
   ```
   Le SDK 7.x existe, mais il exige AGP 9 et Gradle 9.1 et retire le plugin
   `kotlin-android`. Restez en **6.0.0** sauf si le projet est déjà sous AGP 9.
3. **Module app** : `implementation(libs.sumup.merchant.sdk)`, `implementation(libs.play.services.location)`,
   `coreLibraryDesugaring(libs.android.desugar.jdk.libs)`, `compileOptions { isCoreLibraryDesugaringEnabled = true }`.
   Le SDK fournit ses propres règles R8 (`-keep class com.sumup.reader.sdk.** { *; }`) et
   les permissions de localisation : il n'y a rien à ajouter.
4. **`Application.onCreate()`** : `SumUpState.init(this)`.
5. Copiez `src/CheckoutSumUp.kt`, changez le package, remplacez `<AFFILIATE_KEY>` et la devise
   si besoin (`SumUpPayment.Currency.EUR`).
6. **`MainActivity`** :
   ```kotlin
   @Deprecated("Deprecated in Java")
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
       CheckoutSumUp.onResultat(this, requestCode, resultCode, data)
   }
   ```
7. **Compose** :
   ```kotlin
   val activite = LocalContext.current.commeActivite()        // déroule les ContextWrapper
   LaunchedEffect(Unit) {
       CheckoutSumUp.evenements.collect { r ->
           when (r) {
               is CheckoutSumUp.Resultat.Reussi -> enregistrerVente(reference = r.reference) // TX_CODE
               is CheckoutSumUp.Resultat.Echec  -> afficherErreur(avertirDebitEventuel(r.motif)) // + bouton « Réessayer »
               // Pas de retour silencieux au repos : la carte a pu etre debitee (Points de vigilance)
               CheckoutSumUp.Resultat.Annule    ->
                   afficherErreur(avertirDebitEventuel("Paiement interrompu sans confirmation du terminal."))
           }
       }
   }
   Button(onClick = { CheckoutSumUp.payer(activite, montant, "Vente comptoir") }) { Text("Payer par carte") }
   ```
   `avertirDebitEventuel` n'est pas dans `src/` : voir l'étape 8.
   `payer()` ouvre la connexion à la volée si la session n'existe pas, puis enchaîne sur le
   paiement. `connecte: StateFlow<Boolean>` reflète `SumUpAPI.isLoggedIn()` pour l'écran de
   réglages, et `connexion` porte le résultat d'une connexion demandée explicitement.
8. **Avertissement avant « Réessayer »** (pas dans `src/` ; Popote `ui/Comptoir.kt` l. 896-898,
   à `6149a81`) :
   ```kotlin
   internal fun avertirDebitEventuel(motif: String) =
       "$motif\n\nSi la carte a néanmoins été débitée, ne relancez pas le paiement : vérifiez " +
           "le relevé ou l'historique du compte SumUp avant de recommencer."
   ```
   Popote ajoute un délai de garde : sans réponse du terminal au bout de 3 min
   (`DELAI_GARDE_CARTE_MS = 3 * 60_000L`, `ui/Comptoir.kt` l. 811), la feuille passe en
   échec avec le même avertissement (« Aucune réponse du terminal après plusieurs minutes. »).
   La vente en attente est sauvée dans un `rememberSaveable`, pour ne pas la perdre si
   l'écran est recréé pendant le paiement (couvert par `EcransTest` en restauration d'état
   simulée ; jamais vérifié sur appareil après une vraie mort du processus). Cette sauvegarde
   ne suffit peut-être pas : le résultat risque d'arriver avant que l'écran ne l'écoute
   (point de vigilance « recréation de l'activité »).

## API exposée (`CheckoutSumUp`)

| Membre | Rôle |
|---|---|
| `payer(activity, montant: Double, titre)` | Encaisse : connexion si besoin, puis `SumUpAPI.checkout`. Montant arrondi à 2 décimales (HALF_UP) |
| `connecter(activity)` / `deconnecter()` | Écran de connexion natif / `SumUpAPI.logout()` |
| `connecte: StateFlow<Boolean>` | État de session pour Compose |
| `evenements: SharedFlow<Resultat>` | `Reussi(reference = TX_CODE)`, `Echec(motif)`, `Annule` |
| `connexion: SharedFlow<ResultatConnexion>` | Résultat d'une connexion explicite |
| `onResultat(activity, requestCode, resultCode, data)` | À appeler depuis `onActivityResult` (codes 47001 et 47002) |
| `connecteReellement`, `lancerConnexion`, `lancerCheckout` (`internal var`) | Points d'injection pour les tests : le SDK ne tourne pas sous Robolectric |

**Correspondance des résultats** : `RESULT_CODE == SUCCESSFUL` donne `Reussi`. Tout autre
`RESULT_CODE` donne `Echec`. Pas de `RESULT_CODE` avec `RESULT_CANCELED` donne `Annule`.
Pas de `RESULT_CODE` sans `RESULT_CANCELED` donne `Echec` (« réponse inattendue »).
Ces résultats disent ce que l'écran SumUp a renvoyé, pas ce que la carte a subi : seul
`Reussi` prouve un débit, et rien ne prouve son absence.

## Tests

`tests/CheckoutSumUpTest.kt` (Robolectric) simule la réponse du terminal en appelant
directement `onResultat`. Dans Popote, `forkEvery = 1` (`app-terrain/build.gradle.kts`
l. 147-148) lance une JVM par classe de test : l'état des singletons comme `CheckoutSumUp`
ne passe plus d'une classe à l'autre. C'est un confinement, posé après une cascade d'échecs
en CI restée inexpliquée (premier échec dans `CorrectionsTest`, côté SumUp :
`CalledFromWrongThreadException`) ; `docs/decisions.md` (l. 3692-3720) précise qu'un run
vert avec `forkEvery = 1` « ne démontre pas la cause ». Il n'isole pas les tests d'une même
classe : c'est le rôle du `@Before preparer()` de `CheckoutSumUpTest`, qui remet l'objet à
zéro. Recettes bac à sable SumUp (cartes de test, montant d'échec forcé `11`) : skill
`.claude/skills/sumup-testing/SKILL.md` du dépôt source.

## Réglages éprouvés

Aucun : aucun encaissement réel n'a encore eu lieu. Valeurs retenues sans validation sur le
terrain : SDK `6.0.0` (étape 2) et délai de garde de 3 min (étape 8).

## Points de vigilance

- **Un `Echec` ou un `Annule` ne prouve pas que la carte n'a pas été débitée.** Limite du
  SDK, pas défaut du code : le statut reste 🟡. Constat de décompilation du SDK (`javap` sur
  l'AAR), **non vérifié sur matériel** (Popote `docs/todo.md` l. 328-336, déjà présent à
  `08c211a`) : `CardReaderPaymentAPIDrivenPageActivity.onBackPressed()` peut finir sans
  `setResult`, ce que `onResultat` lit comme `Annule`, ou rendre `ResultCode=2`
  « Transaction failed. », lu comme `Echec`, alors que la carte vient d'être débitée.
  `AffiliateResponseEvent(SUCCESSFUL)` n'est posté que par les boutons « Terminé » ou
  « Envoyer le reçu », jamais automatiquement. Conséquences :
  - un retour arrière sur l'écran de résultat peut laisser une vente payée **non
    enregistrée** (on n'écrit qu'après `Reussi`) ;
  - un « Réessayer » à l'aveugle peut **débiter deux fois** (piège 3).

  Parade de Popote : `avertirDebitEventuel()` (`ui/Comptoir.kt` l. 896) complète le message
  affiché au-dessus du bouton « Réessayer » (`FeuilleCarteSumUp`, l. 992-1004) après un
  échec, une interruption (`Annule`) ou le délai de garde. Appels : `ui/Comptoir.kt` l. 303,
  311 et 330 ; `ui/Comptes.kt` l. 1365, 1368 et 1381 ; `ui/Cotisation.kt` l. 181, 183
  et 192. Elle empêche la nouvelle tentative à l'aveugle, mais ne détecte rien : le bouton
  reste actif, et l'opérateur doit vérifier lui-même le relevé SumUp. Le texte de
  l'avertissement n'est vérifié par aucun test.
- **Le SDK peut ne jamais répondre.** Sans délai de garde, la feuille de paiement reste
  bloquée. Popote : 3 min, puis échec avec avertissement (« Intégration », étape 8).
- **⚠️ Recréation de l'activité : le `Reussi` risque d'être perdu (suspecté).** Défaut de
  `src/`, tiré du code et du cycle de vie Android, **non vérifié** (ni test, ni appareil).
  `evenements` ne rejoue rien (`MutableSharedFlow(extraBufferCapacity = 1)`,
  `src/CheckoutSumUp.kt` l. 73) : un résultat émis sans abonné est perdu, et c'est voulu
  (test `un evenement emis sans collecteur actif n est pas rejoue a un collecteur ulterieur`,
  `tests/CheckoutSumUpTest.kt` l. 176). Si Android a détruit l'activité pendant le paiement
  (mémoire, « Ne pas conserver les activités »), il rend le résultat à `onActivityResult`
  avant la première composition, donc avant le `LaunchedEffect` qui collecte (Popote
  `ui/Comptoir.kt` l. 286). La vente sauvée en `rememberSaveable` ne s'écrirait pas, alors
  que la carte est débitée, et rien n'avertirait l'opérateur : `etatCarte` repart à `Repos`
  (l. 134-141). `EcransTest` ne couvre pas ce cas : il simule la réponse après la
  restauration, quand le collecteur écoute déjà. L'essai prévu par Popote
  (`docs/todo.md` l. 369-383) le confirmerait ou l'infirmerait. Patch possible, à écrire avec
  un test : garder le dernier résultat non lu (relecture d'une valeur, ou état acquitté par
  l'écran) au lieu d'un bus sans relecture.
- **Session native** : aucune doc SumUp ne garantit qu'elle survit aux redémarrages
  (« Points ouverts »). Si elle se perd, l'écran de connexion revient au moment de la vente.

## Points ouverts

- [ ] **Premier encaissement réel** sur un lecteur physique. Popote liste six scénarios
      (`docs/todo.md` l. 338-344) : première connexion, connexion silencieuse, encaissement
      réussi, remboursement de dette par carte, carte refusée ou annulée (aucune écriture),
      « Réessayer » après refus (pas de doublon). Noter aussi ce que l'écran de résultat
      renvoie après un débit effectif, pour confirmer ou infirmer le constat de décompilation.
- [ ] **Durée de vie de la session** : aucune doc SumUp ne garantit que la session de
      `openLoginActivity` survit aux redémarrages. À vérifier : tuer l'app, redémarrer le
      téléphone, attendre quelques jours.
- [ ] Délai de garde et vente en attente (étape 8) : jamais exercés sur le terrain. Popote
      prévoit l'essai (`docs/todo.md` l. 369-383) : laisser l'écran SumUp ouvert jusqu'au
      délai de garde, puis tuer le processus pendant un paiement et revenir à l'app. Vérifier
      à cette occasion que le `Reussi` n'est pas perdu (Points de vigilance).
- Repli prévu dans Popote : sans compte connecté, un bouton « Valider un paiement extérieur »
  (avec confirmation) enregistre un paiement fait hors de l'app, marqué `validationManuelle`.

**Quand l'encaissement réel passe**, passez le statut à ✅ et ajoutez une ligne à l'historique.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| v13.18 | Popote, appareil réel | Plantage NPE au tap « Connecter » | `SumUpState.init` (piège 1) |
| v13.19 | Popote, appareil réel | `NoClassDefFoundError LocationServices` | `play-services-location` (piège 2) |
| v13.62 | Popote | Vraie clé Affiliate injectée | — |
| 2026-10-12 | Catalogage Library | Encaissement réel toujours non validé | Statut 🟡 |
| 2026-10-27 | Revue Popote (`6149a81`) | `CheckoutSumUp.kt` et `CheckoutSumUpTest.kt` identiques à la Library hors gabarit `<AFFILIATE_KEY>` (l. 48) ; `sumup/` inchangé depuis `08c211a`. Toujours aucun encaissement réel (`docs/todo.md` l. 325 : « Aucun encaissement réel n'a jamais abouti à ce jour »). Correctif du piège 2 (v13.20) jamais confirmé sur l'appareil (`docs/todo.md` l. 328) | Statut 🟡 inchangé ; provenance vérifiée jusqu'à `6149a81` ; statut précisé ; section « Pièges trouvés sur appareil réel » renommée « Pièges connus », avec une colonne « Où constaté » (seuls les pièges 1 et 2 l'ont été sur appareil) ; `src/` et `tests/` non modifiés |
| 2026-10-27 | Revue Popote, documentation | Point de vigilance du dépôt source non repris au catalogage (déjà à `08c211a`) : d'après la décompilation du SDK, `Echec` ou `Annule` possibles après un vrai débit. Parade `avertirDebitEventuel()` dans `ui/Comptoir.kt`, `ui/Comptes.kt` et `ui/Cotisation.kt`. `forkEvery = 1` : confinement d'une cascade d'échecs inexpliquée, pas une cause démontrée (`docs/decisions.md` l. 3692-3720). Hypothèse de revue, non vérifiée : après une recréation de l'activité, le `Reussi` arriverait avant l'abonnement à `evenements` et serait perdu | Sections « Composants », « Réglages éprouvés » et « Points de vigilance » ajoutées ; piège 3 et exemple Compose mis à jour ; étape 8 (avertissement, délai de garde) ; § « Tests » nuancé ; hypothèse notée en point de vigilance et en point ouvert. Limite du SDK et hypothèse non vérifiée : pas de ⚠️ |
| 2026-10-27 | Relecture de la Library | Le `Reussi` perdu après une recréation de l'activité vient de `src/CheckoutSumUp.kt` (`MutableSharedFlow` sans relecture, l. 73 ; le test l. 176 confirme qu'une valeur émise sans abonné est perdue), pas du SDK. Fortement suspecté, conséquence financière : traité comme les autres défauts suspectés de la Library (lampe de `scan-multimode`, jeton de `google-drive-appdata`) | Composant `CheckoutSumUp.kt` : ⚠️ (suspecté, non vérifié) ; statut global 🟡 inchangé ; Popote à prévenir |
