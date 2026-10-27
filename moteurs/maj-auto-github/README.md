# Mise à jour automatique d'un APK depuis GitHub Releases (hors Play Store)

| Champ | Valeur |
|---|---|
| **Identifiant** | `maj-auto-github` |
| **Statut** | ✅ **Éprouvé.** En production depuis la v13.22 de Popote Compagnie : des dizaines de versions publiées (balises `v13.22` à `v13.125` sur le dépôt public, `v13.125` toujours la dernière au 2026-10-27), reçues par les téléphones des clients sans câble ni Play Store. Parties pures couvertes par `MiseAJourTest`. Deux composants ⚠️ (détail dans « Composants ») : `publicationDuLancement`, annulation avalée, **patch proposé le 2026-10-26**, non vérifié sur appareil, pas reporté dans Popote ; la CI `version.yml`, mots de passe de signature relus par `Properties.load`, **défaut latent, patch appliqué dans la Library le 2026-10-27**, vérifié par Carnet de recette en signant avec un mot de passe piégé (`f198fd3`), jamais exercé par une vraie publication |
| **Plateforme** | Android natif, Kotlin, minSdk 26, et GitHub Actions |
| **Dépendances** | `kotlinx-serialization-json` (accès `JsonObject`, sans plugin), `androidx.core` (`FileProvider`, `PackageInfoCompat`), coroutines. CI : `actions/checkout@v4`, `actions/setup-java@v4` (Temurin 17), `gradle/actions/setup-gradle@v4`, `gh` du runner ; pas de `setup-android` |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé), commit `08c211a` (2026-10-12) ; `MiseAJour.kt`, `MiseAJourTest.kt` et `version.yml` identiques à `6149a81` (2026-10-26), hors patchs de la Library. Patch CI `Properties.load` repris de [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) (public), commit `f198fd3` (2026-10-26). Dépôt public des versions : [JRRocks/Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie) |
| **Utilisé dans** | Popote Compagnie · Carnet de recette (intégré le 2026-10-26, variante « dépôt de code public » ; aucune version publiée au 2026-10-27, aucune balise `v*` ni `apercu-*`) |
| **Fichiers d'origine** | Popote : `app-terrain/src/main/kotlin/fr/popote/terrain/maj/MiseAJour.kt`, `ui/MiseAJour.kt` (boîte de dialogue), `.github/workflows/version.yml`, `docs/publication.md`. Carnet : `.github/workflows/version.yml` et `apercu.yml`, `app/build.gradle.kts` (patch `Properties.load`), `ui/EtatsUi.kt` et `ui/AccueilViewModel.kt` (exemple d'interface) |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

Une app distribuée **hors Play Store** (usage interne, petite structure) se met à jour toute
seule. À chaque lancement :

1. elle interroge **anonymement** `https://api.github.com/repos/<owner>/<repo>/releases/latest` ;
2. elle compare le `versionCode` de l'APK publié à celui qui est installé ;
3. s'il est **strictement** supérieur, elle propose la mise à jour, télécharge l'APK dans
   son stockage privé et le passe à l'installateur système.

## Architecture : deux dépôts

- **Dépôt privé** : le code (et éventuellement des données sensibles).
- **Dépôt public « versions »** : **uniquement des APK compilés**. L'app le lit **sans
  jeton**. Un jeton embarqué dans un APK distribué n'est pas un secret, et il n'existe pas
  de portée GitHub « lire seulement les releases » d'un dépôt privé.
- La CI publie dans le dépôt public avec un **PAT fine-grained** limité à `contents: write`
  sur ce seul dépôt. Même s'il fuit, il n'ouvre pas le code.

## Variante : le dépôt de code est déjà public (Carnet de recette)

Quand le code n'a rien de privé, le second dépôt n'a plus de raison d'être :

- les APK sont publiés dans les **Releases du dépôt de code** lui-même ;
- `GITHUB_TOKEN` suffit (`permissions: contents: write`) : **aucun jeton personnel** ;
- les aperçus sont des **pré-versions** du même dépôt (`apercu-<versionName>`), que
  `/releases/latest` ignore : l'app ne les propose jamais. Ils sont produits par un workflow
  séparé, `apercu.yml`, à chaque push qui touche l'app sur une branche de travail ;
  `version.yml` ne sert plus qu'à publier ;
- un aperçu est **toujours signé de la clé release**. Dans un dépôt public, la clé debug
  versionnée est publique : un APK de debug ne doit pas pouvoir se connecter à un service
  qui reconnaît l'app par sa signature (Google, voir la fiche `google-drive-appdata`). Sans
  les secrets, `apercu.yml` lance les tests et ne publie rien.

Code de référence : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette)
(public), `.github/workflows/apercu.yml` et `version.yml`, `docs/publication.md`. Commit
`f198fd3` (2026-10-26) pour le dernier changement fonctionnel (mots de passe de signature
passés par l'environnement) ; pointe revue `8dc4820` ; `maj/MiseAJour.kt` et les deux
workflows sont inchangés de `f198fd3` à `8c5251c`, et encore à `797fae7` (pointe distante du
2026-10-27, 10:53 UTC).

## Conventions qui font marcher le tout

| Règle | Pourquoi |
|---|---|
| L'APK publié s'appelle **`<prefixe>-<versionName>-<versionCode>.apk`**, nom fabriqué par la CI à partir de `build.gradle.kts` | L'API GitHub ne connaît pas le `versionCode`. Le nom de balise et le `versionName` (ex. `13.20-sumup-location`) ne s'ordonnent pas |
| Un APK sans ce suffixe est **ignoré** | Mieux vaut ne rien proposer qu'une version qu'on ne sait pas dater |
| Comparaison **strictement** supérieure | Ne jamais faire régresser un téléphone en service |
| L'APK doit être **signé de la même clé** que l'installé ; la CI **échoue** sur un APK `-unsigned` | Sinon Android refuse le remplacement et il faudrait désinstaller, donc perdre les données |
| Les mots de passe de signature passent par l'**environnement** de l'étape de compilation, jamais par `keystore.properties` | `Properties.load` lit `\` comme un échappement et retire les espaces de tête : un mot de passe généré peut être corrompu (voir « Points de vigilance ») |
| Une seule requête **par processus**, silencieuse en cas d'échec | Pas de message d'erreur pour une mise à jour non demandée ; « Plus tard » tient jusqu'au prochain lancement |
| Téléchargement en `.part`, renommé à la fin | Un fichier interrompu n'est jamais pris pour un APK complet |
| En-tête `User-Agent` **obligatoire** | GitHub répond 403 aux requêtes sans `User-Agent`, même anonymes |
| 404 sur `/releases/latest` | Normal tant que rien n'a été publié, ce n'est pas une panne |

## Composants

| Fichier (`src/`) | Rôle | Dépend d'Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| `MiseAJour.kt` | Interroge `/releases/latest`, compare les `versionCode`, télécharge en `.part`, passe l'APK à l'installateur | Oui (`Context`, `FileProvider`, `PackageInfoCompat`) ; les parties qui décident sont pures | `MiseAJourTest.kt`, 9 tests JVM | ✅ en production chez Popote · ⚠️ annulation avalée : patch proposé le 2026-10-26, non vérifié sur appareil |
| `version.yml` | CI : lit la version, restaure la clé release, teste, compile, nomme l'APK, pose la balise, publie (ou aperçu privé) | Non (GitHub Actions, `ubuntu-latest`) | Aucun test automatisé ; démonstration du défaut et du patch consignée dans l'historique (2026-10-27) | ✅ chaîne utilisée de `v13.22` à `v13.125` · ⚠️ mots de passe relus par `Properties.load` : latent, patch appliqué dans la Library, vérifié par Carnet en signant avec un mot de passe piégé (`f198fd3`), jamais exercé par une vraie publication |
| *(hors `src/`)* interface | Popote : boîte de dialogue `ui/MiseAJour.kt`. Carnet : ligne d'état (`EtatMiseAJour`, `AccueilViewModel`) | Oui (Compose) | Carnet : `AccueilViewModelTest` (1 test) et captures Robolectric | Popote ✅ en production, réapparition après rotation connue · Carnet non éprouvé (jamais sur un téléphone) |

## Intégration dans une nouvelle app

1. **Manifeste** :
   ```xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
   <provider android:name="androidx.core.content.FileProvider"
             android:authorities="${applicationId}.fileprovider"
             android:exported="false" android:grantUriPermissions="true">
       <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/file_paths"/>
   </provider>
   ```
   et `res/xml/file_paths.xml` : `<files-path name="maj" path="maj/" />`.
2. Copiez `src/MiseAJour.kt`, changez le package, puis adaptez `DEPOT`, la regex `NOM_APK`
   (préfixe) et le `User-Agent`.
3. **Au lancement** (ex. `LaunchedEffect(Unit)` de l'écran d'accueil) :
   ```kotlin
   val publication = MiseAJour.publicationDuLancement(ctx)   // null = rien à proposer
   // boîte de dialogue : versionNom, notes, taille → « Mettre à jour » / « Plus tard »
   if (!MiseAJour.peutInstaller(ctx)) ctx.startActivity(MiseAJour.ecranSourcesInconnues(ctx))
   val apk = MiseAJour.telecharger(ctx, publication) { fraction -> progression = fraction }
   MiseAJour.installer(ctx, apk)
   ```
   Depuis Android 8, « sources inconnues » est une autorisation **par app**, qu'on accorde
   seulement depuis l'écran de réglages ouvert par `ecranSourcesInconnues`. Pour éviter la
   réapparition après rotation, portez plutôt l'état dans un ViewModel (« Exemple
   d'interface » plus bas, non éprouvé).
4. **CI** : adaptez `src/version.yml`. Il lit `versionName`/`versionCode` dans
   `build.gradle.kts`, restaure la clé release depuis les secrets
   (`SIGNATURE_KEYSTORE_BASE64`, `SIGNATURE_STORE_PASSWORD`, `SIGNATURE_KEY_ALIAS`,
   `SIGNATURE_KEY_PASSWORD`), lance les tests, compile, renomme l'APK, pose la balise
   `v<versionName>` et publie sur le dépôt public avec le secret `JETON_DEPOT_VERSIONS`.
   Pour finir, il efface la clé du runner (`if: always()`). Le secret `JETON_SIGNALEMENT`
   est propre à Popote : retirez-le.

   Le fichier `keystore-release/keystore.properties` écrit par la CI ne porte que
   `storeFile` et `keyAlias`. Les deux mots de passe arrivent par l'environnement de
   l'étape « Compiler l'APK release » (`SIGNATURE_STORE_PASSWORD`, `SIGNATURE_KEY_PASSWORD`).
   `build.gradle.kts` doit donc les lire ainsi (extrait de Carnet de recette,
   `app/build.gradle.kts` à `8dc4820`, l. 11-16, 34-46 et 66-68, raccourci ; aucune valeur
   réelle) :

   ```kotlin
   import java.io.FileInputStream
   import java.util.Properties

   // Jamais versionné. Absent (clone frais, CI sans les secrets) : APK release non signé,
   // plutôt qu'un échec de toute la configuration Gradle.
   val proprietesRelease = file("keystore-release/keystore.properties")
   val signatureReleaseDisponible = proprietesRelease.exists()

   android {
       signingConfigs {
           if (signatureReleaseDisponible) {
               create("release") {
                   val proprietes = Properties().apply { load(FileInputStream(proprietesRelease)) }
                   storeFile = file("keystore-release/${proprietes.getProperty("storeFile")}")
                   keyAlias = proprietes.getProperty("keyAlias")
                   // La CI passe les mots de passe en variables d'environnement plutôt que dans le
                   // fichier : `Properties.load` traite `\` comme un échappement et retire les
                   // espaces de tête, ce qui corromprait un mot de passe généré. Le fichier reste
                   // le repli pour une compilation sur un ordinateur.
                   storePassword = System.getenv("SIGNATURE_STORE_PASSWORD") ?: proprietes.getProperty("storePassword")
                   keyPassword = System.getenv("SIGNATURE_KEY_PASSWORD") ?: proprietes.getProperty("keyPassword")
               }
           }
       }
       buildTypes {
           release {
               if (signatureReleaseDisponible) {
                   signingConfig = signingConfigs.getByName("release")
               }
           }
       }
   }
   ```

   Les deux fichiers vont ensemble. Un `version.yml` patché avec un `build.gradle.kts` qui
   ne lit que le fichier donne une signature sans mot de passe : la compilation release
   échoue, ou sort un APK `-unsigned` que l'étape « Nommer l'APK » refuse (comportement exact
   d'AGP non vérifié). Rien n'est publié, mais la CI est rouge. Les noms des secrets ne
   changent pas. Sur un ordinateur, `keystore.properties` peut encore porter `storePassword`
   et `keyPassword` (repli), avec le même piège : doublez chaque `\` ou passez par les
   variables d'environnement.
5. **Mode aperçu** (fortement conseillé si l'app est en production) : `workflow_dispatch`
   avec `apercu: true` produit le **même APK signé** en pré-version `apercu-<versionName>`
   du dépôt **privé**, sans rien publier aux clients. On le teste, puis on publie sur feu
   vert explicite.

### Exemple d'interface : la ligne d'état de Carnet de recette (non éprouvé)

`src/` ne contient que le cœur : l'interface s'écrit dans chaque app. Popote affiche une
boîte de dialogue dont l'état vit dans des `remember`, d'où sa réapparition après une
rotation (« Points de vigilance »). Carnet de recette la remplace par une **ligne d'état**
dont l'état vit dans un ViewModel. C'est une réponse à ce défaut, jamais essayée sur un
téléphone : des captures Robolectric rendent chaque état et un test JVM couvre les causes
d'échec, mais aucun test ne simule une rotation.

- `EtatMiseAJour` : `Aucune`, puis, avec le nom de la version, `Disponible`, `Ecartee`,
  `Telechargement` (et sa fraction), `AAutoriser`, `Echec` (et son motif).
- `AccueilViewModel` porte l'état. L'interrogation part de son `init`, dans
  `viewModelScope` : une rotation ne l'annule pas, et un téléchargement en cours survit à la
  recréation de l'écran.
- « Plus tard » met `Ecartee` : la ligne quitte l'écran principal jusqu'au prochain
  lancement, rotation comprise ; Réglages propose encore la version.
- Jamais « à jour » sans preuve : Réglages dit « 0.2 installée », car sans réponse de
  GitHub l'app ne peut pas savoir qu'elle est à jour.
- `AAutoriser` : l'écran « sources inconnues » est ouvert, et la ligne dit quoi faire au
  retour.
- Les causes d'échec sont dites en français (`causeDuTelechargement`, testée par
  `AccueilViewModelTest`), jamais par le message brut d'une exception. Elle s'appuie sur
  `DriveCarnet.estPanneReseau` (variante de `google-drive-appdata`) : à remplacer si l'app
  n'a pas ce moteur.

Fichiers (Carnet, `app/src/main/kotlin/fr/jrrocks/carnetderecette/`, à `8dc4820`) :
`ui/EtatsUi.kt` l. 27-47, `ui/AccueilViewModel.kt` l. 42-61 et 155-208, `ui/EcranSemaine.kt`
l. 64-121 (`LigneMiseAJour`), `ui/EcranReglages.kt` (`SectionApplication`),
`ui/composants/LigneEtat.kt` ; test `app/src/test/kotlin/fr/jrrocks/carnetderecette/ui/AccueilViewModelTest.kt`.
Commits `f5f8baf` (ligne d'état à la place de la boîte de dialogue) et `8dc4820` (« Plus
tard » = `Ecartee`, causes d'échec en français). `EtatsUi.kt`, `AccueilViewModel.kt` et le
test sont inchangés jusqu'à `797fae7` (2026-10-27).

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi |
|---|---|---|
| Décision de proposer | `versionCode` publié strictement supérieur au `versionCode` installé | Ne jamais faire régresser un téléphone en service ; en production chez Popote de la v13.22 à la v13.125 |
| Fréquence | Une interrogation par processus, silencieuse en cas d'échec | Pas de message d'erreur pour une mise à jour que personne n'a demandée ; en production chez Popote |

## API exposée (`MiseAJour`)

| Membre | Rôle |
|---|---|
| `publicationDuLancement(ctx): Publication?` | Interroge une fois par processus et rend la version à proposer, ou `null` |
| `versionInstallee(ctx): Long` | `versionCode` en cours |
| `telecharger(ctx, publication, onProgression)` | Télécharge en `.part` avec progression par paliers et rend le `File` |
| `peutInstaller(ctx)` / `ecranSourcesInconnues(ctx)` | Autorisation d'installer |
| `installer(ctx, fichier)` | `ACTION_VIEW` sur une `content://` du FileProvider |
| `lirePublication(json)`, `aProposer(pub, installee)`, `versionCodeDuNom(nom)` (`internal`) | Parties pures, testées |

## Points de vigilance

- **⚠️ Annulation avalée par `publicationDuLancement` (patch proposé le 2026-10-26).**
  Symptôme : si l'écran qui interroge est quitté ou recréé (rotation, thème) pendant la
  requête GitHub, plus aucune mise à jour n'est proposée jusqu'à la mort du processus. Cause :
  `runCatching` attrape aussi la `CancellationException`, et `interrogee` était posé avant la
  requête. Correctif dans `src/` : `try`/`catch` qui relance l'annulation, `interrogee` posé
  après la réponse. Couvert par `MiseAJourTest` (le test échoue sur l'ancien code). Relevé
  par une relecture du Carnet de recette ; **Popote porte encore le défaut** (vérifié à
  `6149a81` : `maj/MiseAJour.kt` l. 114-117).
  Effet de bord du correctif : il attrape `Exception` là où `runCatching` attrapait tout
  `Throwable`. Une `Error` levée pendant l'interrogation (`NoClassDefFoundError` après R8,
  `ExceptionInInitializerError`, manque de mémoire) n'est plus avalée et fait planter l'app au
  lancement, au lieu de rester silencieuse. Choix assumé du Carnet (même code) ; une app qui
  préfère le silence attrape `Throwable` après la clause qui relance l'annulation.
- Dans Popote, la boîte de dialogue garde aussi son état dans des `remember` : après « Plus
  tard », une rotation la fait réapparaître. Le Carnet met cet état dans un ViewModel
  (`AccueilViewModel`), ce qui règle aussi les téléchargements perdus à la rotation.
  Toujours vrai à `6149a81` (`ui/MiseAJour.kt` l. 37) ; voir « Exemple d'interface ».
- **⚠️ CI : mots de passe de signature relus par `Properties.load` (défaut latent, patch
  appliqué dans la Library le 2026-10-27).** Symptôme : la signature échoue (le JDK répond
  « keystore password was incorrect ») alors que le secret est juste, si le mot de passe contient un `\`
  ou commence par une espace. S'il finit par `\`, la ligne suivante (`keyAlias`) est avalée
  dans le mot de passe et l'alias devient `null`. Cause : l'ancien `version.yml` écrivait
  `storePassword` et `keyPassword` dans `keystore.properties`. `printf '%s\n'` les écrivait
  intacts, contrairement à `echo`, mais `build.gradle.kts` relit le fichier par
  `java.util.Properties.load`, qui traite `\` comme un échappement et retire les espaces de
  tête. Le commentaire de l'ancien `version.yml` laissait croire que `printf` suffisait.
  Correctif, repris de Carnet de recette (`f198fd3`) : le fichier ne porte plus que
  `storeFile` et `keyAlias`, les mots de passe passent par l'environnement de l'étape de
  compilation, et `build.gradle.kts` lit `System.getenv(...) ?: proprietes.getProperty(...)`
  (extrait dans « Intégration »). Vérifié par Carnet « en signant avec un mot de passe
  piégé » (message de `f198fd3` ; essai en session, aucun run CI consigné) et par une
  démonstration JDK du 2026-10-27 (historique). Jamais exercé par une vraie publication.
  **Popote porte le défaut** (`.github/workflows/version.yml` l. 113-115 et
  `app-terrain/build.gradle.kts` l. 56-58 à `6149a81`) ; il signe sans problème en
  production, donc son mot de passe ne déclenche pas le cas. À corriger au plus tard avant un
  changement de mot de passe ou de clé. L'alias passe encore par le fichier : choisissez-le
  simple (lettres et chiffres).
- **Rotation de la clé release = rupture de la chaîne** : les téléphones existants ne
  pourront plus installer. Gardez la clé (et sa sauvegarde) comme un bien précieux.
- Le dépôt de versions étant public, n'y mettez **aucune donnée** (seed vide, aucun nom).

### Pièges de CI (`version.yml` et workflows voisins)

| Piège | Ce qui se passe | Parade | Provenance |
|---|---|---|---|
| `.jks` mal décodé, mot de passe ou alias faux | La signature échoue plusieurs minutes plus tard, sur une erreur Gradle obscure | Vérifier la clé dès le décodage : `base64 -d --ignore-garbage` (tolère un CR ou une espace collés au secret), puis `keytool -list -keystore <jks> -storepass:env SIGNATURE_STORE_PASSWORD -alias "$KEY_ALIAS"` suivi d'un `::error::` clair (l'étape reçoit alors `SIGNATURE_STORE_PASSWORD` dans son `env`). **Pas dans `src/version.yml`**, qui échoue déjà, moins clairement, sur un base64 invalide (`pipefail`) | InnovAdapt `c5aba61` (`.github/workflows/android-debug.yml`) ; `-storepass:env` et `--ignore-garbage` essayés le 2026-10-27 (JDK 21, coreutils) |
| Secret interpolé dans le script (`${{ secrets.X }}` dans `run:`) | Un `$`, un `"` ou un `` ` `` du mot de passe le corrompt | Passer les secrets par `env:`, jamais par `${{ }}` dans le script. `src/version.yml` le fait déjà : à garder | InnovAdapt `c5aba61` |
| Artefact en doublon de la release | Le quota de stockage Actions (0,5 Go pour **tout le compte**, partagé avec Packages, compté en Go × heures) se remplit ; un `upload-artifact` en échec fait sauter la suite, et l'APK n'est pas compilé ou pas publié | Ne pas doubler la release par un artefact : les fichiers d'une release ne comptent pas dans ce quota. `src/version.yml` n'utilise pas `upload-artifact`. Si vous en ajoutez un : après la publication, avec `continue-on-error: true` et une rétention courte | Activity-track `855773c`, run `38150625547` (« Upload test report » en échec avant « Build debug APK ») ; InnovAdapt `02dd5cd` ; ToolMap `eee8c6b`, `docs/specs/release.md` § 1 et `.github/workflows/ci.yml` ; SuivieMAT `f8f3eaf`, `consignes/outils.md` § 6 bis |
| `android-actions/setup-android@v3` | Échoue avant toute étape Gradle : « Failed to find package 'tools' » | `src/version.yml` n'en dépend pas (le SDK du runner `ubuntu-latest` suffit). Ne pas l'ajouter ; au besoin, localiser `sdkmanager` sous `$ANDROID_SDK_ROOT/cmdline-tools/*/bin` | SuivieMAT `f8f3eaf`, `consignes/outils.md` § 6 bis (depuis 2026-09) ; Android-Suntrack, runs `38064480807`, `38075724619` et `38075805926` (octobre 2026) |
| Lancement manuel | `workflow_dispatch` (bouton *Run workflow*, `actions_run_trigger`) n'existe que si le fichier du workflow est sur la **branche par défaut** | Fusionner `version.yml` dans la branche par défaut avant la première publication ; le run lit ensuite le fichier de la référence choisie | Carnet `8dc4820`, `docs/publication.md` (note finale) ; SuivieMAT `f8f3eaf`, `consignes/outils.md` § 6 bis |
| Balise poussée depuis une session cloud Claude | Refusée (HTTP 403) par la politique réseau | Faire poser la balise par le workflow, avec le jeton de l'Action (étape « Poser la balise ») | Popote `CLAUDE.md` ; Carnet `.github/workflows/version.yml` l. 108-110 |
| Minutes Actions épuisées ou plafond de dépense atteint | Les runs sont refusés en quelques secondes, sans machine (`runner_id` 0), sans étape ni journal | Ce n'est pas le code : vérifier la facturation du compte. Pas de publication d'ici là, puisque la signature release n'a lieu qu'en CI. Parade de Popote : un runner auto-hébergé sur le PC de l'utilisateur (WSL2, `runs-on: [self-hosted, linux]`) ; la clé release y est décodée dans un dossier qui survit au job, d'où une étape d'effacement en `if: always()` à garder | SuivieMAT `f8f3eaf`, `consignes/outils.md` § 6 bis (2026-10-26) ; ToolMap `eee8c6b`, `CLAUDE.md` (« spending limit », 2026-10-16) ; runner du PC : Popote `2bd6298` (2026-10-27), `docs/runner-pc.md` |
| Création de la clé | `keytool` récent (PKCS12) ne demande qu'**un** mot de passe ; Android Studio ignore son second champ | Mettre la même valeur dans `SIGNATURE_STORE_PASSWORD` et `SIGNATURE_KEY_PASSWORD` | Carnet `8dc4820`, `docs/publication.md` § 1 |

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| v13.22 | Popote | Mise en service (motivée par les correctifs SumUp à livrer sans câble) | — |
| v13.22 → v13.125 | Popote, production | Chaîne utilisée à chaque version | Statut ✅ |
| 2026-10-12 | Catalogage Library | — | Entrée en bibliothèque |
| 2026-10-26 | Carnet de recette, intégration et relecture | Défaut : annulation avalée par `publicationDuLancement`. Variante « dépôt de code public » mise en place | Patch dans `src/` et deux tests ; composant ⚠️ jusqu'à un essai sur appareil ; section « Variante » ; Popote à prévenir |
| 2026-10-27 | Revue Popote (`6149a81`) | `MiseAJour.kt`, `MiseAJourTest.kt` et `version.yml` inchangés depuis `08c211a`, donc identiques à la Library hors patchs. 4 aperçus privés publiés entre `08c211a` et `6149a81` (`apercu-13.127` à `apercu-13.130`), aucun essai sur téléphone consigné ; aucune publication publique nouvelle (dernière balise `v13.125-sections-dette-cotis`) | Statut ✅ inchangé ; provenance vérifiée jusqu'à `6149a81` |
| 2026-10-27 | Revue Popote (`6149a81`) | Popote ne porte pas le patch d'annulation (`runCatching` et `interrogee` posé avant la requête, `maj/MiseAJour.kt` l. 114-117 ; 7 tests contre 9). Boîte de dialogue toujours en `remember` (`ui/MiseAJour.kt` l. 37) | Popote toujours à prévenir |
| 2026-10-27 | Library, patch CI | Défaut latent de `src/version.yml` : mots de passe écrits dans `keystore.properties` puis relus par `Properties.load`. Relevé par la revue de Carnet (corrigé dans Carnet en `f198fd3`). Démonstration JDK 21 : vraie clé PKCS12, blocs `run` de l'ancien et du nouveau `version.yml`, lecture comme `build.gradle.kts`. Avant : `ab\cd123`, `␣␣espace12` (deux espaces de tête) et `fin1234\` échouent (« keystore password was incorrect » ; le dernier avale `keyAlias`). Après : les trois signent ; deux témoins signent dans les deux cas. `MiseAJour.kt` et `MiseAJourTest.kt` non modifiés (9/9 verts en JVM) | `src/version.yml` patché, commentaire corrigé ; extrait `signingConfigs.release` dans « Intégration » ; composant CI ⚠️ (patch appliqué, jamais exercé par une vraie publication) ; Popote porte le défaut (`version.yml` l. 113-115, `build.gradle.kts` l. 56-58 à `6149a81`), à prévenir sans urgence ; Carnet a déjà le correctif |
| 2026-10-27 | Revue Carnet de recette (`8dc4820`, distant `797fae7`) | Aucune balise sur le dépôt au 2026-10-27 (ni `v*` ni `apercu-*`) : aucune version publiée, aucun APK installé depuis la CI. `maj/MiseAJour.kt` et les workflows inchangés de `f198fd3` à `797fae7`. Interface passée en ligne d'état (`f5f8baf`, `8dc4820`) | Section « Exemple d'interface » (non éprouvé) ; commit de provenance ajouté à la « Variante » ; statut inchangé |
| 2026-10-27 | Revues InnovAdapt, Activity-track, SuivieMAT, Android-Suntrack, ToolMap | Pièges de CI vécus ailleurs : clé vérifiée trop tard et secrets interpolés (InnovAdapt `c5aba61`), quota d'artefacts (InnovAdapt `02dd5cd`, Activity-track run `38150625547`), `setup-android` cassé, minutes épuisées. Aucun de ces dépôts n'utilise le moteur | Tableau « Pièges de CI » ; `src/` inchangé pour ces points |
| 2026-10-27 | Relecture de la Library | Effet de bord du patch d'annulation (une `Error` n'est plus avalée) ; parade de Popote aux minutes épuisées : runner auto-hébergé sur le PC (`2bd6298`, après la revue de `6149a81`) | Points de vigilance et « Pièges de CI » complétés ; section « Réglages éprouvés » ajoutée (gabarit) |
