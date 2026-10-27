# Journal de la Library

> Entrée la plus récente **en haut**. Une entrée par changement de la bibliothèque.
> Ne jamais modifier ni supprimer une entrée passée : on corrige par une nouvelle entrée.
>
> Gabarit :
> ```
> ## AAAA-MM-JJ — [identifiant(s)] Titre court
> - **Type** : ajout · retour positif · patch · resynchronisation · revue de dépôt · transverse
> - **Quoi** :
> - **Preuve / vérification** :
> - **Apps à prévenir** : (apps qui n'ont pas encore le changement, ou « aucune »)
> ```

---

## 2026-10-27 — [docs/methodes] Fiches méthode : captures Compose sans émulateur, Android en session cloud

- **Type** : ajout + transverse
- **Quoi** : nouveau dossier `docs/methodes/` (outillage déjà mis en œuvre dans les apps, sans
  `src/`) :
  - `captures-compose-robolectric.md` : voir un écran Compose réel en PNG depuis une session
    cloud. Méthode de Popote, extensions du Carnet de recette (sombre, police, largeurs,
    paysage) et de Popote (plusieurs tailles), gabarit de test complet écrit pour la fiche,
    pièges (instabilité Robolectric, nom de test accentué, faux imports, photos absentes) ;
    jamais de PNG rendu sur des données réelles.
  - `android-en-session-cloud.md` : SDK dans le conteneur ou par hook SessionStart, miroir
    Maven contre les 429, ordres de grandeur, tests purs sans Gradle (script donné), Rust,
    Node et Python en session, livraison d'un APK de plus de 30 Mio, clé de debug commune
    (jamais copiée ici), pièges de CI GitHub Actions, revue de dépôt (clones superficiels).
  - `README.md` d'index ; INDEX § 2, § 3 et § 7 ; lien depuis le `README.md` racine.
  Remplace le candidat Popote « Captures d'écran Compose sans émulateur ».
- **Preuve / vérification** : rendu des captures « constaté fidèle par le propriétaire » le
  2026-09-29 (Popote `docs/decisions.md`). Le gabarit de la fiche a été exécuté dans un projet
  minimal en session (AGP 8.8.0, Robolectric 4.13, SDK installé dans le conteneur) : 4 PNG
  produits et relus, puis relancé par la contre-vérification après retouche. Bloc
  d'installation du SDK rejoué dans un dossier neuf (20 s). Tests purs sans Gradle rejoués sur
  `astro-soleil-lune` (13/13). Extensions et livraison par `release/` ou pré-version : 🟡.
- **Apps à prévenir** : ScanList et SuivieMAT, dont le `CLAUDE.md` affirme encore qu'un build
  n'est généralement pas possible en session cloud (SuivieMAT compile pourtant en session
  depuis le 2026-09-24).

---

## 2026-10-27 — [rapport-plantage] Ajout du moteur Rapport de plantage

- **Type** : ajout
- **Quoi** : nouveau moteur `rapport-plantage`, repris de ToolMap (branche
  `ccr-e59b55ac-kt4qb9` @ `2e992e1`, code identique à `fa96a1a`, la version installée sur le
  téléphone). `src/` : `CrashReporter.kt`, `CrashActivity.kt`, `CrashReport.kt` ; `tests/` :
  `CrashReportTest`, `CrashReporterTest`, `CrashActivityTest` ; packages d'origine. Fiche
  complète : gestionnaire posé dans `attachBaseContext`, écran dans un processus `:crash`,
  plantages natifs relus par `ApplicationExitInfo`, saut de l'initialisation, ADR-029,
  intégration (manifeste, textes, `GIT_SHA`, couplages à défaire), vie privée (fin du logcat et
  modèle du téléphone dans le rapport). Variante minimale de Popote décrite sans code, avec sa
  règle de confidentialité (jamais d'entité ni de donnée personnelle dans un message
  d'exception). Les évolutions de la branche p16 (`159e9e4`, `3e6677a`) restent hors de `src/`
  (🟡). Remplace le candidat Popote « Capteur de plantage ».
- **Preuve / vérification** : ToolMap `docs/JOURNAL.md` (branche p16) l. 1560-1561 : rapport
  affiché sur un Galaxy S25 (Android 15) le 2026-10-03, transmis par l'utilisateur, correctif
  `c620a38`. Popote `docs/decisions.md` (v13.18, 2026-09-03) : pile d'un plantage SumUp lue dans
  le fichier du capteur. Fichiers copiés identiques (`cmp`) à `2e992e1` et à `fa96a1a`.
  `CrashReportTest` 7/7 en JVM sans Gradle (avec `kotlin-test-junit` 2.0.21) ; tests
  Robolectric non exécutables ici. Plantages natifs et saut de l'initialisation : aucune preuve
  réelle, d'où 🟡.
- **Apps à prévenir** : Carnet de recette (son `docs/cadrage.md` § 11 renvoie encore au
  candidat Popote). Popote, pour information : sa variante ne lit pas les plantages natifs et
  son gestionnaire est posé après `SumUpState.init`. ToolMap : `fa96a1a` et `2e992e1` n'existent
  que sur la branche `ccr-e59b55ac-kt4qb9`, à garder (ou à baliser) pour la provenance.

---

## 2026-10-27 — [paiement-sumup] Revue Popote : débit possible malgré `Echec` ou `Annule`

- **Type** : revue de dépôt
- **Quoi** :
  - Code et tests identiques à Popote `6149a81` (et à la pointe `93e5d5b`), hors gabarit
    `<AFFILIATE_KEY>`. Toujours aucun encaissement réel.
  - Point de vigilance ajouté (constat de décompilation déjà présent dans Popote à `08c211a`) :
    `onBackPressed()` de l'écran de paiement du SDK peut rendre `Annule` ou `Echec` après un vrai
    débit. Parade de Popote : `avertirDebitEventuel()` avant tout « Réessayer » (Comptoir, Comptes,
    Cotisation), qui prévient sans rien détecter. Piège « Réessayer » réécrit, exemple Compose
    corrigé, étape d'intégration sur l'avertissement et le délai de garde de 3 min.
  - Hypothèse de revue, non vérifiée, consignée en vigilance : après une recréation de l'activité,
    le `Reussi` émis par un `MutableSharedFlow` sans relecture peut être perdu si
    `onActivityResult` passe avant que l'écran l'écoute (même risque chez Popote).
  - Statut précisé : l'écran de connexion a été atteint en v13.19 puis a planté ; le correctif
    v13.20 n'a jamais été confirmé (l'INDEX disait « connexion OK »). `forkEvery = 1` présenté
    comme un confinement, pas comme une cause démontrée. Sections « Composants », « Réglages
    éprouvés » et « Points de vigilance » ajoutées. `src/` et `tests/` non modifiés, 🟡 inchangé
    (limite du SDK, pas un défaut du code).
- **Preuve / vérification** : `diff` Library ↔ app : seule la ligne de la clé diffère, tests
  identiques (9) ; recherche de la vraie clé dans toute la Library : 0 occurrence. Citations de
  `docs/todo.md`, `docs/decisions.md`, `ui/Comptoir.kt`, `ui/Comptes.kt`, `ui/Cotisation.kt`
  relues. Perte d'une valeur émise sans abonné démontrée par un test JVM hors Library. Tests
  Robolectric non exécutables ici.
- **Apps à prévenir** : Popote Compagnie, pour l'hypothèse du `Reussi` perdu (à vérifier lors de
  l'essai sur terminal physique prévu dans `docs/todo.md`).

---

## 2026-10-27 — [google-drive-appdata] Revue Popote et Carnet : variante complétée, vigilances, adresse de test

- **Type** : revue de dépôt
- **Quoi** :
  - Popote `6149a81` (inchangé jusqu'à `93e5d5b`) : `DriveAppData.kt` identique à `src/` hors
    patchs de la Library ; aucun des patchs du 2026-10-26, pas de `clearToken`. Commits `fa90649`
    et `9391ed9` vérifiés fidèles au Carnet `8dc4820` (moteur inchangé jusqu'à `797fae7`).
  - Section « Variante » : provenance (`f198fd3`), `RefusDrive` et `estPanneReseau`,
    `echecGoogle` (le cas « code 8 » n'est jamais observé), `Echec(reseau)`, espaces insécables,
    documentés sans report dans `src/` ; phrase sur le déplacement du dossier nuancée (simple
    commentaire de code) ; renvoi aux réimplémentations d'InnovAdapt-CRM (`d2c2f6e`).
  - Tableaux « Composants » et « Réglages éprouvés » ajoutés (gabarit). Étape 3 de
    l'intégration complétée par les fonctions du patch 403.
  - Vigilances : dépôt manuel seul éprouvé (WorkManager jamais vérifié en réel), effet de bord
    du patch 403 (un 403 hors refus d'autorisation devient transitoire et serait réessayé sans
    fin), mode « Test » à 7 jours (à vérifier pour `AuthorizationClient`), un 404 n'est jamais un
    succès en écriture, durée de vie du jeton inconnue, pas d'épinglage TLS ; dans un dépôt
    public, une clé de debug versionnée permet aussi d'installer un APK tiers par-dessus une
    version de debug (ToolMap ADR-047). Jeton en cache : défaut suspecté aussi chez Popote et
    InnovAdapt.
  - `tests/DriveAppDataTest.kt` : adresse en `gmail.com` remplacée par une adresse en
    `example.org` (écart volontaire avec Popote). Statut inchangé.
- **Preuve / vérification** : `diff -u` Popote ↔ `src/` ; diffs Carnet `f198fd3..797fae7` sur
  `drive/` ; citations relues dans les deux dépôts par la contre-vérification. 28/28 tests verts
  hors Gradle (Kotlin 2.0.21, JUnit 4.13.2, bouchons Android, `kotlinx-serialization` 1.6.2) :
  valable pour les fonctions pures seulement.
- **Apps à prévenir** : Popote Compagnie (403 : patch de la Library à reporter, avec son effet
  de bord à surveiller ; jeton en cache : défaut suspecté, correctif dans `DriveCarnet.avecJeton`
  du Carnet). InnovAdapt-CRM, hors « Utilisé dans » : pièges communs (jeton en cache, 404, 403
  non lu) si sa synchro est gardée.

---

## 2026-10-27 — [maj-auto-github] Patch CI : mots de passe de signature hors de `Properties.load`

- **Type** : patch + revue de dépôt
- **Quoi** :
  - `src/version.yml` écrivait `storePassword` et `keyPassword` dans `keystore.properties`, que
    `build.gradle.kts` relit par `Properties.load` : un « \ » y est lu comme un échappement, une
    espace de tête est retirée, un « \ » final avale la ligne `keyAlias`. Correctif repris de
    Carnet de recette (`f198fd3`) : le fichier ne porte plus que `storeFile` et `keyAlias`, les
    mots de passe passent par l'environnement de l'étape de compilation
    (`SIGNATURE_STORE_PASSWORD`, `SIGNATURE_KEY_PASSWORD`) ; commentaire trompeur corrigé.
    Composant CI ⚠️ (latent, patch appliqué, jamais exercé par une vraie publication).
  - Fiche : tableau « Composants », extrait `signingConfigs.release` (sans valeur), tableau
    « Pièges de CI » avec provenance (vérification du `.jks` par `keytool`, `base64 -d
    --ignore-garbage`, secrets par `env`, quota d'artefacts, `setup-android`, `workflow_dispatch`,
    balise refusée en session, minutes épuisées, PKCS12), exemple d'interface en ligne d'état du
    Carnet (non éprouvé), commit de provenance de la variante.
  - Historique : revue Popote `6149a81` (✅ inchangé ; 4 aperçus 127 à 130 publiés, aucun essai
    sur téléphone consigné, aucune publication publique nouvelle) et Carnet (aucune balise à
    `797fae7`). `MiseAJour.kt` et `MiseAJourTest.kt` non modifiés.
- **Preuve / vérification** : démonstration JDK 21 avec une vraie clé PKCS12 et les blocs `run`
  de l'ancien et du nouveau `version.yml` : trois mots de passe piégés échouent avant
  (« keystore password was incorrect ») et signent après ; deux témoins signent dans les deux
  cas ; refaite indépendamment par la contre-vérification. `MiseAJourTest` 9/9 en JVM (bouchons
  Android). Aucun run GitHub Actions possible ici.
- **Apps à prévenir** : Popote Compagnie (défaut latent dans `version.yml` et
  `app-terrain/build.gradle.kts`, à changer ensemble, sans urgence ; patch d'annulation toujours
  absent). Carnet de recette a déjà le correctif.

---

## 2026-10-27 — [scan-multimode] Patch NDEF, ajout de RegexSuggester, SuivieMAT dans « Utilisé dans »

- **Type** : patch + ajout (composant) + revue de dépôt
- **Quoi** :
  - NFC : `FLAG_READER_SKIP_NDEF_CHECK` retiré de `src/NfcScanSurface.kt`, commentaire faux
    corrigé. D'après le code AOSP (`android14-release`), `NfcService` saute la lecture NDEF avec
    ce drapeau et `NativeNfcTag` n'ajoute la techno NDEF que pendant cette lecture :
    `Ndef.get(tag)` renvoyait donc `null` et seul l'UID était lu. La variante « `ndef.connect()`
    dans `NfcTagDecoder` » du patch proposé est retirée, car inopérante. Le NFC reste ⚠️.
  - Nouveau composant 🟡 `RegexSuggester` et son test (27 tests), copiés sans modification de
    SuivieMAT `5c81d3c` (identiques à `5eb5198` et à la pointe `65da616`) : il propose des
    formules regex à partir d'exemples, sans IA. L'interface est décrite, pas copiée.
  - SuivieMAT ajouté à « Utilisé dans » : fork de ScanList, moteur identique jusqu'à `5eb5198`
    hormis l'habillage de `NfcScanSurface`, scan jamais vérifié sur téléphone. Une preuve
    indirecte NFC (lecteur armé sur le téléphone de l'utilisateur, 2026-09-25) est consignée,
    sans promotion.
  - Points de vigilance : ICU (accolade littérale non échappée, `--` en classe, messages de
    `frenchMessage` calés sur le JDK), UID aléatoire et UID à ne pas exporter, option aperçu
    ou économie de l'OCR. Ligne `ScanValue` corrigée (`normalize` garde la casse).
- **Preuve / vérification** : tests purs sans Gradle (Kotlin 2.0.21, JUnit 4.13.2) :
  `RegexSuggesterTest`, `RegexRuleEngineTest`, `ScanConsensusTest`, `ScanDebouncerTest`,
  `NfcValueTest` : 91/91, relancés par la contre-vérification. Fichiers `RegexSuggester`
  identiques (`cmp`) à la source. Patch NDEF non vérifié sur appareil : aucun test ne couvre ce
  chemin Android ; la pile NFC de Samsung peut différer d'AOSP.
- **Apps à prévenir** : ScanList (`1368027` / `2ef0b6d`) et SuivieMAT (`65da616`) n'ont pas le
  patch NDEF ; SuivieMAT est peu concerné (il ne lit que l'UID et peut garder le drapeau).

---

## 2026-10-27 — [library] AGENTS.md : pièges de revue, dossier docs/methodes

- **Type** : transverse
- **Quoi** : § 5.E complété par les pièges rencontrés pendant la revue du 2026-10-27 :
  revoir la branche active et pas seulement `main` (la revue du 2026-10-12 était déjà périmée
  pour Activity-track et ToolMap) ; cloner sans `--depth` (un clone superficiel a fait croire à
  une branche orpheline d'InnovAdapt et à une fausse divergence d'Android-Suntrack) ;
  revérifier la pointe distante avant de consigner ; récupérer et fusionner d'abord les
  branches de la Library écrites par d'autres sessions. § 4 : dossiers `docs/ui-ux/` et
  `docs/methodes/` ajoutés à la structure.
- **Preuve / vérification** : constats des contre-revues du 2026-10-27 (clones complets,
  `git cat-file`, `git ls-remote`).
- **Apps à prévenir** : aucune

---

## 2026-10-27 — [astro-soleil-lune] Patch : transit de la veille et jour du changement d'heure

- **Type** : patch + revue de dépôt
- **Quoi** :
  - `MoonCalculator.calculate` rendait, 26 à 29 jours par an, le transit de la **veille au soir**
    (premier maximum de la fenêtre élargie), et jamais `null`, contrairement à sa KDoc. Le jour
    de 23 h (passage à l'heure d'été), le balayage fixe de 24 h débordait sur le lendemain et
    pouvait rendre un lever ou un coucher d'après minuit ; le jour de 25 h, la dernière heure
    était ignorée. Correctif au plus petit périmètre : longueur réelle du jour local, et seuls
    les maxima dont l'heure affinée tombe dans le jour comptent.
  - `tests/AstroTest.kt` : deux tests de reproduction (2024 sur trois villes ; New York
    2020-2030) et le test d'altitude `altitudeDeg` repris de `SkyPaletteTest` (Android-Suntrack).
  - Fiche : statut précisé (8 tests contre éphémérides publiées sur 13), tableau Composants,
    vigilances « époque de la théorie lunaire » (deux effets du bug d'origine) et « couverture
    faible côté lune », `declinationDeg` sans valeur par défaut, revue Android-Suntrack et
    Sun-tracking dans l'historique.
- **Preuve / vérification** : défaut signalé par une contre-revue (portage Python), puis
  reproduit en Kotlin : les deux nouveaux tests échouent sur l'ancien code (Paris 2024-01-23 :
  transit du 22 à 22 h 43 ; New York 2023-03-12 : lever du 13 à 0 h 44) et passent après le
  correctif. 13/13 tests verts (Kotlin 2.0.21, JUnit 4.13.2, sans Gradle). Calcul pur : aucun
  matériel requis, mais aucun retour terrain non plus, d'où 🟡.
- **Apps à prévenir** : Sun Track (Android-Suntrack, `MoonCalculator.kt` inchangé à `2cb08aa`).
  Sun-tracking est une copie périmée (bug d'époque) à archiver ou aligner.

---

## 2026-10-27 — [scan-multimode] Anti-rebond glissant et lampe après l'arrière-plan (Carnet de recette)

- **Type** : retour d'usage (relecture adverse) · patch proposé
- **Quoi** : variante du Carnet complétée (points 8 à 10 : verrou glissant et voisin écarté,
  numéro nettement le plus grand, liaison, rotation de capture et lampe de `CameraPreview`).
  Points de vigilance : ⚠️ lampe éteinte après un passage en arrière-plan (suspecté, patch
  proposé côté Carnet), verrou à durée fixe de `ScanDebouncer` qui revalide une valeur restée
  en vue.
- **Preuve / vérification** : relecture adverse du Carnet, chaque constat contre-vérifié par
  un second agent ; tests JVM du verrou glissant (`SuiviEtiquettesTest`). Lampe : déduite du
  code de CameraX, aucun essai sur appareil. Non reporté dans `src/`.
- **Apps à prévenir** : ScanList (lampe, si le défaut se confirme sur un téléphone).

---

## 2026-10-27 — [scan-multimode] Utilisé dans Carnet de recette, variante OCR sans regex ; trois candidats

- **Type** : retour d'usage (intégration) · revue de dépôt
- **Quoi** : fiche `scan-multimode` : ligne « Utilisé dans », section « Variante : OCR par
  éléments dans le réticule, sans regex (Carnet de recette) » (sept adaptations), précision
  dans les points de vigilance : `addOnSuccessListener` sans exécuteur rappelle sur le fil
  principal, pas sur le fil d'analyse. INDEX : ligne du moteur, trois candidats du Carnet
  (lecture à voix haute par segments, commandes vocales, lecture de pages publiques), dépôt
  ajouté aux dépôts passés en revue.
- **Preuve / vérification** : copies de `ScanConsensus`, `ScanDebouncer` et `ScanValue` dans
  le Carnet avec leurs tests repris, au vert (`:core:test`, `:app:testDebugUnitTest`) ;
  géométrie de l'image testée en JVM. **Aucun essai sur appareil** : statut inchangé.
- **Apps à prévenir** : aucune (la précision sur le fil ne change pas le comportement de
  ScanList).

---

## 2026-10-27 — [docs/ui-ux] Crédits d'images complétés

- **Type** : transverse
- **Quoi** : fin d'un travail laissé non commité le 2026-10-21. Auteur de
  `Color_star-en__tertiary_names_.png` relevé (Kwamikagami, CC BY-SA 4.0) ; crédit ajouté pour
  `Double_Diamond_by_the_Design_Council.png` (déjà dans le dépôt, sans ligne de crédit) ; nouvelle
  figure 37.7 (`CVD-friendly_sequential_colormaps.png`), l'illustration de palettes séquentielles
  qu'annonçait déjà le texte du chapitre 37. Une spirale de Fibonacci téléchargée mais citée
  nulle part n'est pas ajoutée.
- **Preuve / vérification** : auteurs et licences relus sur l'API de Wikimedia Commons
  (`extmetadata` Artist et LicenseShortName) pour les trois fichiers.
- **Apps à prévenir** : aucune

---

## 2026-10-27 — [library] Branches d'autres sessions intégrées

- **Type** : transverse
- **Quoi** :
  - Branche `claude/recipe-app-planner-92zsww` (session Carnet de recette, commits `fa90649`
    et `9391ed9`) intégrée par avance rapide : ses deux entrées sont juste en dessous.
  - Branche `ccr-be4abd50-9zo09e` (2026-10-21) fusionnée. Elle partait du commit initial du
    dépôt et ajoutait au README une note sur le modèle de colorisation manga-colorization-v2.
    La note est déplacée dans l'INDEX : candidat « Colorisation IA de manga » (§ 5,
    Mihon-edited-) et ligne « Je cherche à… ». Le README garde la structure du catalogue.
- **Preuve / vérification** : dépôt du modèle cloné (`a0d0e44`, 2025-03-31) : aucun fichier
  de licence, dépendances PyTorch, poids sur Google Drive. Mihon-edited- `main` @ `d001e6f`
  = amont Mihon, sans `tools/colorizer-server`. Les temps de calcul de la note d'origine ne
  sont pas mesurés ici.
- **Apps à prévenir** : aucune

---

## 2026-10-26 — [google-drive-appdata] Jeton révoqué gardé en cache (suspecté)

- **Type** : patch (proposé, hors `src/`)
- **Quoi** : point de vigilance ⚠️ sur la fiche. Après un retrait d'accès depuis le compte
  Google, Play Services rend encore le jeton en cache, et « reconnecter » boucle jusqu'à son
  expiration. Patch proposé : `GoogleAuthUtil.clearToken` sur tout `AutorisationRevoquee`
  (code de référence : `DriveCarnet.avecJeton`, JRRocks/Carnet-de-recette).
- **Preuve / vérification** : seconde relecture adversariale du Carnet, contre-vérifiée sur
  la documentation Android (« Clear the token cache »). Le code du Carnet compile et passe
  ses tests. Non reporté dans `src/` (restructuration de cinq fonctions, Library non
  compilable ici). Aucun essai sur appareil.
- **Apps à prévenir** : Popote Compagnie.

---

## 2026-10-26 — [google-drive-appdata][maj-auto-github] Utilisés dans Carnet de recette, deux patchs

- **Type** : patch + intégration dans une nouvelle app
- **Quoi** :
  - Les deux moteurs sont intégrés dans [Carnet de recette](https://github.com/JRRocks/Carnet-de-recette)
    (dépôt public) : ligne « Utilisé dans » et section « Variante » de chaque fiche
    (`drive.file` et dossier visible ; Releases du dépôt de code public, sans second dépôt).
  - **Patch `maj-auto-github`** : `publicationDuLancement` avalait l'annulation (`runCatching`)
    et ne proposait plus aucune mise à jour du processus après un écran recréé pendant la
    requête. `try`/`catch` qui relance l'annulation ; deux tests.
  - **Patch `google-drive-appdata`** : tout 403 devenait `AutorisationRevoquee`, y compris
    « API non activée » ou une limite de débit. Le corps d'erreur est maintenant lu
    (`exceptionPour`) ; quatre tests.
  - Avertissement dans les prérequis de `google-drive-appdata` : dans un dépôt public, ne
    jamais déclarer au client OAuth le SHA-1 de la clé debug versionnée.
- **Preuve / vérification** : défauts relevés par une relecture adversariale du lot 1 du
  Carnet, chacun contre-vérifié par un second relecteur. Les correctifs y sont compilés et
  testés : 32 tests JVM verts, et le test d'annulation échoue sur l'ancien code. Ici, la
  Library ne se compile pas ; le code reporté est le même, au package près. **Aucun essai sur
  appareil** : composants concernés passés à ⚠️.
- **Apps à prévenir** : Popote Compagnie (porte encore les deux défauts).

---

## 2026-10-21 — [docs/ui-ux] Dossier UI/UX sourcé

- **Type** : ajout
- **Quoi** :
  - Nouveau dossier `docs/ui-ux/` : 41 chapitres en français (processus, recherche, heuristiques,
    lois de l'UX, Gestalt, typographie, couleur et contraste, composants, mobile et Android,
    Material Design 3, accessibilité, performance, contenu, design systems, éthique, IA, GOV.UK,
    modèles mentaux, qualité Android, glossaire, synthèse).
  - `SOURCES.md` (202 sources avec URL), `images/` (images libres de droits de Wikimedia Commons)
    et `images/CREDITS.md`.
  - `INDEX.md` § 7 et `README.md` mis à jour.
- **Preuve / vérification** : relecture des chapitres contre les pages sources téléchargées ;
  plusieurs affirmations non étayées corrigées. Toutes les images référencées existent. Pas de
  code : aucun build ni test. Limites : `m3.material.io` et Apple HIG illisibles
  automatiquement ; deux images souhaitées non récupérées ; auteur d'une image à confirmer ;
  volume réel inférieur aux 200 pages visées (voir `docs/ui-ux/README.md`).
- **Apps à prévenir** : aucune

---

## 2026-10-12 — [library] Création du catalogue

- **Type** : transverse + ajout + revue de dépôt
- **Quoi** :
  - Structure du dépôt : `AGENTS.md` (guide des agents), `INDEX.md` (index de recherche),
    `JOURNAL.md`, `CLAUDE.md`, gabarit `moteurs/_modele/`.
  - Cinq moteurs catalogués avec leur code et leurs tests de référence :
    `scan-multimode` (ScanList), `paiement-sumup`, `google-drive-appdata`, `maj-auto-github`
    (Popote Compagnie, code dans Suivie-Popote-Cie), `astro-soleil-lune` (Android-Suntrack).
  - Revue des sept dépôts (dont Activity-track, ToolMap et InnovAdapt-CRM, sans moteur extrait) : 39 candidats notés dans l'INDEX § 5.
  - Clé Affiliate SumUp remplacée par le gabarit `<AFFILIATE_KEY>` dans `paiement-sumup/src/CheckoutSumUp.kt`.
- **Preuve / vérification** : statuts établis à partir des docs des dépôts sources
  (`consignes/`, `docs/decisions.md`, `docs/todo.md`, `docs/JOURNAL.md`) et de la
  déclaration de l'utilisateur pour le scan. Aucun build lancé : la Library n'est pas un
  projet compilable.
- **Apps à prévenir** : ScanList, à cause du soupçon sur la lecture NDEF en mode NFC
  (`FLAG_READER_SKIP_NDEF_CHECK`, voir la fiche `scan-multimode`). À vérifier sur appareil.
