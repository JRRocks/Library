# Paiement carte SumUp : SDK Android, lecteur Bluetooth, connexion native

| Champ | Valeur |
|---|---|
| **Identifiant** | `paiement-sumup` |
| **Statut** | 🟡 **Testé.** Intégration éprouvée sur appareil réel jusqu'à l'écran de connexion SumUp (deux plantages terrain corrigés, voir plus bas). **Aucun encaissement réel n'a encore abouti** (essai sur terminal physique en attente). La logique de résultat est couverte par des tests Robolectric |
| **Plateforme** | Android natif, Kotlin, minSdk 26, AGP 8.8.0, Kotlin 2.0.21, Java 17 |
| **Dépendances** | `com.sumup:merchant-sdk:6.0.0` (dépôt `https://maven.sumup.com/releases`), `com.google.android.gms:play-services-location:21.3.0` (**obligatoire**, voir plus bas), `com.android.tools:desugar_jdk_libs:2.1.5` |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé, code source de [Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie)), commit `08c211a` (2026-10-12) |
| **Utilisé dans** | Popote Compagnie |
| **Fichiers d'origine** | `app-terrain/src/main/kotlin/fr/popote/terrain/sumup/CheckoutSumUp.kt`, `PopoteApplication.kt`, `MainActivity.kt` (`onActivityResult`), `docs/decisions.md` (sections « Paiement carte SumUp » et « Connexion SumUp native ») |
| **Dernière mise à jour de la fiche** | 2026-10-12 |

## À quoi ça sert

On encaisse un montant par carte bancaire sur un **lecteur SumUp Bluetooth externe** (Solo,
Air, 3G, PIN+) depuis l'app, avec les **écrans natifs du SDK** : connexion au compte
marchand, montant, insertion de la carte, PIN, confirmation. L'app ne garde **aucun secret** :
le marchand se connecte avec l'e-mail et le mot de passe de son compte SumUp dans l'écran
SumUp, et le SDK conserve lui-même la session.

On écrit en base **seulement après** un `Resultat.Reussi` authentique. Une carte refusée ou
une saisie annulée ne laisse aucune trace.

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

## Pièges trouvés sur appareil réel (à appliquer d'office)

| # | Symptôme | Cause | Correctif |
|---|---|---|---|
| 1 | `NullPointerException` dans `ReaderModuleCoreState.getDaggerHandler()` au premier appel `SumUpAPI` | Le SDK **ne s'initialise pas tout seul** (aucun `<provider>` d'auto-init) et la doc d'intégration ne le dit pas | `SumUpState.init(this)` (`com.sumup.reader.sdk.api.SumUpState`) dans `Application.onCreate()`, **avant tout autre appel SumUp** |
| 2 | `NoClassDefFoundError: com/google/android/gms/location/LocationServices` à l'ouverture de l'écran de connexion | Le SDK instancie `LocationServices` sans déclarer la dépendance dans son `.pom` | `implementation("com.google.android.gms:play-services-location:21.3.0")` dans le **module qui porte le SDK** |
| 3 | `ERROR_DUPLICATE_FOREIGN_TX_ID` sur « Réessayer » après un refus | Même `foreignTransactionId` réutilisé | **Nouveau `UUID` à chaque tentative** |
| 4 | Le résultat n'arrive jamais avec `rememberLauncherForActivityResult` | `checkout()` et `openLoginActivity()` appellent eux-mêmes `startActivityForResult` (API classique) | Surcharger `Activity.onActivityResult` et relayer vers `CheckoutSumUp.onResultat(...)` |
| 5 | Compilation : API Java 8+ absentes en minSdk 26 | Dépendances transitives du SDK (`auth-sdk`, etc.) | `isCoreLibraryDesugaringEnabled = true` et `coreLibraryDesugaring(desugar_jdk_libs)` |

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
               is CheckoutSumUp.Resultat.Echec  -> afficherErreur(r.motif)                    // + bouton « Réessayer »
               CheckoutSumUp.Resultat.Annule    -> Unit
           }
       }
   }
   Button(onClick = { CheckoutSumUp.payer(activite, montant, "Vente comptoir") }) { Text("Payer par carte") }
   ```
   `payer()` ouvre la connexion à la volée si la session n'existe pas, puis enchaîne sur le
   paiement. `connecte: StateFlow<Boolean>` reflète `SumUpAPI.isLoggedIn()` pour l'écran de
   réglages, et `connexion` porte le résultat d'une connexion demandée explicitement.

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

## Tests

`tests/CheckoutSumUpTest.kt` (Robolectric) simule la réponse du terminal en appelant
directement `onResultat`. Dans Popote, `forkEvery = 1` sur les tests Gradle évite que l'état
du singleton fuie d'une classe de test à l'autre. Recettes bac à sable SumUp (cartes de test,
montant d'échec forcé `11`) : skill `.claude/skills/sumup-testing/SKILL.md` du dépôt source.

## Points ouverts

- [ ] **Premier encaissement réel** sur un lecteur physique : réussite, refus, annulation,
      « Réessayer ».
- [ ] **Durée de vie de la session** : aucune doc SumUp ne garantit que la session de
      `openLoginActivity` survit aux redémarrages. À vérifier : tuer l'app, redémarrer le
      téléphone, attendre quelques jours.
- [ ] Délai de garde : dans Popote, une vente en attente est un `rememberSaveable` et bascule
      en échec au bout de **3 min** sans réponse du terminal (l'écran SumUp a pu tuer le
      processus).
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
