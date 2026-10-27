# Rapport de plantage lisible sur le téléphone, sans adb ni logcat

| Champ | Valeur |
|---|---|
| **Identifiant** | `rapport-plantage` |
| **Statut** | ✅ **Éprouvé pour les exceptions Java/Kotlin** · 🟡 plantages natifs et saut de l'initialisation. Le 2026-10-03, sur un Galaxy S25 (SM-S931U, Android 15), l'écran de rapport de ToolMap (APK de `fa96a1a`) s'est affiché après une exception sur un fil OkHttp ; l'utilisateur l'a transmis, et il a permis le correctif `c620a38` (ToolMap `docs/JOURNAL.md`, branche p16, l. 1560-1561). Le chemin des plantages natifs (Android 11+, `ApplicationExitInfo` et tombstone) n'a **aucune preuve réelle**. La variante minimale de Popote Compagnie a aussi servi en réel (v13.18) |
| **Plateforme** | Android natif, Kotlin, minSdk 26 ; plantages natifs à partir d'Android 11 (API 30). `CrashReport.kt` est en Kotlin pur (`java.time`) |
| **Dépendances** | `androidx.core:core-ktx` (1.19.1 chez ToolMap : `edit`, `PackageInfoCompat`, `ViewCompat`, `WindowInsetsCompat`), `androidx.annotation` (`RequiresApi`, tiré par `core`). Aucune bibliothèque de rapport, aucun réseau. Tests : JUnit 4.13.2, `kotlin-test-junit`, Robolectric 4.17 (SDK 35) |
| **Provenance** | ToolMap (privé jusqu'au 2026-10-27, aujourd'hui `JRRocks/ToolMap-Archive`), branche `ccr-e59b55ac-kt4qb9`, commit `2e992e1` (2026-10-14). Les six fichiers copiés y sont identiques à `fa96a1a` (2026-10-03), la version installée sur le téléphone (`git diff fa96a1a 2e992e1` vide sur ces fichiers). Ces deux commits n'existent que sur cette branche, reprise sans fusion git par `sauvegarde/p16-2026-10-16` (`159e9e4`). **Depuis le 2026-10-27**, cet historique est dans `JRRocks/ToolMap-Archive` (privé, ancien dépôt renommé, d'après le journal de ToolMap, session 66), et [JRRocks/ToolMap](https://github.com/JRRocks/ToolMap) est republié **en public, sans historique**, en un seul commit `a37dcab` : `CrashActivity.kt`, `CrashReport.kt` et les trois tests y sont identiques à `src/` ; `CrashReporter.kt` y porte les évolutions 🟡 de la branche p16 (`errorReport`, crochet EventLog) et `ErrorReport.kt` s'y ajoute. Variante minimale : [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé), commit `6149a81` (2026-10-26), décrite sans code |
| **Utilisé dans** | ToolMap (référence, code de `src/`) · Popote Compagnie (variante minimale, code propre). Carnet de recette le réclame (`docs/cadrage.md` § 11, l. 311 à `8dc4820` : « À extraire d'abord dans la Library ») ; aucun code au 2026-10-27 |
| **Fichiers d'origine** | ToolMap : `app/src/main/kotlin/fr/toolmap/crash/{CrashReporter,CrashActivity}.kt`, `core/src/main/kotlin/fr/toolmap/core/crash/CrashReport.kt` ; tests `core/src/test/kotlin/fr/toolmap/core/crash/CrashReportTest.kt`, `app/src/test/kotlin/fr/toolmap/crash/{CrashReporterTest,CrashActivityTest}.kt`. Intégration (non copiée ; numéros de ligne à `2e992e1`) : `app/src/main/AndroidManifest.xml` l. 150-155, `app/src/main/res/values/strings.xml` l. 1034-1041, `app/src/main/kotlin/fr/toolmap/ToolMapApp.kt` l. 21-49, `MainActivity.kt` l. 162-176, `app/build.gradle.kts` l. 27-29, 97 et 106, `app/src/test/kotlin/fr/toolmap/AppStartupTest.kt` ; décision : `docs/DECISIONS.md`, ADR-029 (l. 285). Popote : `app-terrain/src/main/kotlin/fr/popote/terrain/PopoteApplication.kt`, `MainActivity.kt` (`EcranPlantage`) |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

Une app installée hors Play Store n'a pas de console de plantages, et son utilisateur n'a ni
`adb` ni `logcat`. Sans rien, un plantage au lancement se résume à « l'appli s'ouvre et se
referme, aucun message ». Chez ToolMap, deux allers-retours avec l'utilisateur n'avaient pas
suffi à trouver la cause (ADR-029).

Ce moteur écrit le plantage dans un fichier privé et l'affiche sur le téléphone, prêt à être
copié ou partagé : version et commit de l'APK, téléphone, cause, pile d'appels, fin du journal
de l'app. **Rien ne quitte le téléphone sans un geste de l'utilisateur.**

À utiliser dans toute app distribuée en APK (voir `maj-auto-github`), surtout pendant les
essais sur appareil. À éviter tel quel si l'app a déjà un service de rapport (Crashlytics,
ACRA) : ce gestionnaire ne lui passe pas la main (« Points de vigilance »). Il ne couvre pas
les ANR (application figée).

## Comment ça marche

Deux chemins mènent au même fichier, `files/crash/pending.txt`, puis au même écran.

1. **Exception Java/Kotlin, sur n'importe quel fil.** `CrashReporter.install` pose le
   gestionnaire par défaut **en premier**, dans `Application.attachBaseContext` : une exception
   levée ensuite, même dans `onCreate`, finit sur le rapport. Le gestionnaire :
   - écrit le rapport (dans un `.tmp`, puis renommage) : version et commit, appareil, Android
     et API, ABI, versions OpenGL ES et Vulkan, processus et fil ; en titre, la **cause
     première** (la dernière de la chaîne `cause`, 32 au plus : l'enveloppe dit où,
     `ExceptionInInitializerError` par exemple, la cause dit quoi) ; puis la pile complète et
     les 400 dernières lignes du journal de l'app (`logcat -d -t 400`, 2 s d'attente au plus).
     Depuis Android 4.1, une app ne lit que ses propres lignes ;
   - ouvre `CrashActivity` (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`) ;
   - tue le processus. Si l'écriture a échoué, il rend la main au gestionnaire précédent.
2. **Plantage natif** (signal dans une bibliothèque `.so`). Aucun code Java ne tourne. Au
   démarrage suivant, `collectNativeCrash` lit `ActivityManager.getHistoricalProcessExitReasons`
   (Android 11+) et garde la dernière sortie `REASON_CRASH_NATIVE` du processus principal,
   postérieure à l'installation de l'APK (`lastUpdateTime`) et à la vérification précédente.
   Cette borne est gardée en `SharedPreferences`, écrite avec `commit = true` : le démarrage
   qui suit peut replanter avant une écriture asynchrone. Le rapport donne le signal nommé
   (`signal 11 (SIGSEGV)`) et les chaînes lisibles du tombstone : c'est un protobuf, dont on
   garde chaque suite d'au moins 4 caractères ASCII imprimables, une par ligne (une ligne
   identique à la précédente n'est pas réécrite), le tout coupé à 64 000 caractères. La
   sélection (`ExitSelection`) est pure et testée.
3. **L'écran, `CrashActivity`, tourne dans son propre processus `:crash`** (manifeste), en
   vues Android simples : ni Compose, ni Hilt, ni rien de l'app. Il s'ouvre même si c'est
   l'interface qui a planté, et même pendant que le processus de l'app meurt. Texte en chasse
   fixe, sélectionnable, et trois boutons :
   - **Copier** : presse-papiers (Android 13+ confirme seul, sinon un toast) ;
   - **Partager** : `ACTION_SEND` en texte, par le sélecteur du système ;
   - **Relancer** : rouvre `MainActivity` dans une tâche neuve.

   Les marges suivent les barres système (affichage bord à bord imposé par Android 15).
4. **Un rapport pas encore vu fait sauter l'initialisation risquée.** `Application.onCreate`
   s'arrête après `collectNativeCrash` si `hasPending`, et `MainActivity` ouvre le rapport
   avant de construire quoi que ce soit. Un plantage au lancement ne peut donc pas cacher son
   propre rapport. `takeForDisplay` renomme `pending.txt` en `last.txt` : le rapport vu reste
   relisible, et le démarrage d'après est normal.

Le processus `:crash` crée lui aussi une instance de l'`Application`. Elle ne doit rien y
faire : `isCrashProcess` en tête de `attachBaseContext` et de `onCreate`. Avec Hilt, les
dépendances lourdes de l'`Application` doivent être injectées en `dagger.Lazy` (ToolMap : le
client OkHttp, `ToolMapApp.kt` l. 21-26) : Hilt injecte dans `super.onCreate()`, avant le
retour anticipé, et le processus de l'écran les construirait aussi, au risque de replanter.

Alternatives écartées par ToolMap (ADR-029) : ACRA ou Crashlytics (envoi réseau,
dépendance), un écran dans le processus principal (il meurt avec lui), faire installer `adb`
à l'utilisateur.

Exemple de texte produit par `CrashReport.toText` (valeurs fictives de `CrashReportTest`,
sortie réelle du code de `src/`) :

```
ToolMap 0.1.0 (12) (abc1234)
when: 2026-09-21 14:13:20 +00:00
kind: java exception
device: samsung SM-A515F
android: 13 (API 33)
abis: arm64-v8a, armeabi-v7a
opengl es: 3.2
vulkan: 1.1.0
process: fr.toolmap, thread: main

java.lang.IllegalStateException: boom

java.lang.IllegalStateException: boom
	at fr.toolmap.ToolMapApp.onCreate(ToolMapApp.kt:31)

--- log ---
10-03 12:00:00.000 I ToolMap: start
```

## Composants

| Fichier (`src/`) | Rôle | Dépend d'Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| `CrashReporter.kt` (package `fr.toolmap.crash`) | Pose du gestionnaire, écriture du rapport, lecture des plantages natifs, `hasPending` / `takeForDisplay`, description de l'appareil, fin du journal | Oui | `CrashReporterTest.kt`, 4 tests Robolectric (non exécutés ici) | ✅ chemin Java · 🟡 chemin natif |
| `CrashActivity.kt` (package `fr.toolmap.crash`) | Écran du processus `:crash` : texte, Copier, Partager, Relancer | Oui (vues Android) | `CrashActivityTest.kt`, 5 tests Robolectric (non exécutés ici) | ✅ affiché et transmis le 2026-10-03 (le bouton employé n'est pas consigné ; Relancer jamais cité en réel) |
| `CrashReport.kt` (package `fr.toolmap.core.crash`) | `CrashDevice`, `CrashKind`, `CrashReport.toText`, `NativeCrash` (signal, tombstone, versions graphiques), `ProcessExit`, `ExitSelection` | Non (Kotlin pur) | `CrashReportTest.kt`, 7 tests JVM, **7/7 exécutés ici** | ✅ `toText` (format du rapport transmis) · 🟡 `NativeCrash`, `ExitSelection` (jamais sur un vrai plantage natif) |
| *(hors `src/`)* saut de l'initialisation | `ToolMapApp` et `MainActivity` : rien de risqué tant que le rapport n'est pas vu | Oui | 1 test dans `AppStartupTest` (Robolectric, propre à ToolMap, non copié ; modèle dans « Intégration ») | 🟡 : le 2026-10-03, l'écran s'est ouvert tout de suite (chemin du gestionnaire), le saut au démarrage suivant n'a pas servi |

## Intégration dans une nouvelle app

1. **Dépendances** : `androidx.core:core-ktx`. Pour les tests : `junit:junit:4.13.2`,
   `org.jetbrains.kotlin:kotlin-test-junit`, `org.robolectric:robolectric:4.17`.
2. **Manifeste** (ToolMap, `AndroidManifest.xml` l. 150-155) :
   ```xml
   <!-- The crash report has its own process: it opens while the app's process is dying (CrashReporter). -->
   <activity
       android:name=".crash.CrashActivity"
       android:exported="false"
       android:label="@string/crash_title"
       android:process=":crash" />
   ```
   Vérifiable sur l'APK : `aapt2 dump badging` montre `CrashActivity` dans le processus
   `:crash` (ToolMap, JOURNAL p16 l. 1590).
3. **Textes** (`res/values/strings.xml`), repris de ToolMap (l. 1034-1041), nom de l'app à
   remplacer. `CrashActivityTest` cherche les boutons par ces libellés et le texte « Aucun
   rapport de plantage. » :
   ```xml
   <string name="crash_title">MonApp s\'est arrêtée</string>
   <string name="crash_intro">Une erreur a arrêté l\'application. Le rapport ci-dessous indique le téléphone, la version de l\'application et l\'endroit de l\'erreur : copiez-le ou partagez-le pour qu\'elle soit corrigée. Relisez-le avant de l\'envoyer.</string>
   <string name="crash_copy">Copier</string>
   <string name="crash_share">Partager</string>
   <string name="crash_restart">Relancer</string>
   <string name="crash_copied">Rapport copié</string>
   <string name="crash_subject">Rapport de plantage MonApp</string>
   <string name="crash_none">Aucun rapport de plantage.</string>
   ```
4. **Commit dans le rapport** (`app/build.gradle.kts` l. 27-29, 97 et 106, regroupés ;
   commentaires traduits en français). Sans lui, remplacez `BuildConfig.GIT_SHA`
   (`CrashReporter.kt` l. 192) par une autre valeur :
   ```kotlin
   android {
       defaultConfig {
           // Le commit de l'APK, ecrit dans les rapports : connu en CI, "local" sinon
           val commit = providers.environmentVariable("GITHUB_SHA").orNull?.take(7) ?: "local"
           buildConfigField("String", "GIT_SHA", "\"$commit\"")
       }
       buildFeatures { buildConfig = true }
       testOptions { unitTests.isIncludeAndroidResources = true } // libelles lus par CrashActivityTest
   }
   ```
5. **Fichiers à copier et couplages à défaire.** Copiez les trois fichiers de `src/` et les
   trois de `tests/`, puis renommez les packages `fr.toolmap.crash` et `fr.toolmap.core.crash`
   (dans une app à un seul module, `CrashReport.kt` peut rester à côté des deux autres). À
   adapter :
   - `import fr.toolmap.BuildConfig` (`CrashReporter.kt` l. 15, utilisé l. 192) ;
   - `import fr.toolmap.MainActivity` et `fr.toolmap.R` (`CrashActivity.kt` l. 22-23 ;
     `MainActivity` sert au bouton Relancer, l. 123). `CrashActivityTest` importe aussi
     `MainActivity` (l. 10) et le vérifie après « Relancer » (l. 92) ;
   - `TAG = "ToolMap"` (`CrashReporter.kt` l. 41) et le nom écrit en tête du rapport
     (`CrashReport.kt` l. 45), vérifié par `CrashReportTest` l. 38 ;
   - les noms de processus `fr.toolmap` des tests (cosmétique) ;
   - `EventLog` n'est **pas** dans la version copiée : il n'arrive qu'en `3e6677a` (voir
     « Évolutions »).
6. **Application et activité principale** (adapté de `ToolMapApp.kt` l. 30-49 et
   `MainActivity.kt` l. 162-176, sans Hilt ni MapLibre) :
   ```kotlin
   class MonApp : Application() {
       override fun attachBaseContext(base: Context) {
           super.attachBaseContext(base)
           if (CrashReporter.isCrashProcess(this)) return
           // En tout premier : a partir d'ici, une exception finit sur l'ecran de rapport
           CrashReporter.install(this)
       }

       override fun onCreate() {
           super.onCreate()
           if (CrashReporter.isCrashProcess(this)) return
           CrashReporter.collectNativeCrash(this)
           // Rapport pas encore vu : rien de ce qui peut replanter ne tourne avant lui
           if (CrashReporter.hasPending(this)) return
           initialiserLeRisque()
       }

       private var pret = false

       // Rappelee par MainActivity : apres « Relancer », le processus peut etre celui qui a saute cette etape
       fun initialiserLeRisque() {
           if (pret) return
           pret = true
           // base, SDK de paiement, carte...
       }
   }

   class MainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           if (CrashReporter.hasPending(this)) {
               startActivity(CrashActivity.intent(this))
               finish()
               return
           }
           (application as MonApp).initialiserLeRisque()
           // ... interface normale
       }
   }
   ```
   L'initialisation sautée doit pouvoir être rappelée depuis l'activité sans risque de double
   appel (ToolMap : `ensureMapLibre()`, `ToolMapApp.kt` l. 60-74). Le cas visé (prévu par le
   KDoc de `ensureMapLibre`, jamais vu en réel) : après un plantage natif, le démarrage suivant
   saute l'initialisation et ouvre le rapport ; le processus principal reste vivant, et
   « Relancer » y rouvre `MainActivity` sans repasser par `Application.onCreate`.
7. **Tests.** `CrashReportTest` est un test JVM pur. `CrashReporterTest` et
   `CrashActivityTest` tournent sous Robolectric avec
   `@Config(sdk = [35], application = Application::class)` : une `Application` nue, pour ne
   pas démarrer celle de l'app (Hilt). Pour couvrir le saut de l'initialisation, ToolMap a un
   test de démarrage ; en voici le modèle (adapté de `AppStartupTest.kt` l. 35-65) :
   ```kotlin
   @RunWith(RobolectricTestRunner::class)
   @Config(sdk = [35], application = MonApp::class)
   class DemarrageTest {
       private val app get() = RuntimeEnvironment.getApplication()

       @After
       fun tearDown() {
           // L'Application a pose son gestionnaire pour toute la JVM : remettre celui du lanceur de tests
           (Thread.getDefaultUncaughtExceptionHandler() as? CrashReporter.Handler)?.let {
               Thread.setDefaultUncaughtExceptionHandler(it.previous)
           }
           File(app.filesDir, "crash").deleteRecursively()
       }

       @Test
       fun `un plantage pas encore vu passe avant l'ecran principal`() {
           CrashReporter.recordJavaCrash(app, Thread("main"), IllegalStateException("boom"))
           val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
           assertTrue(activity.isFinishing)
           val suivante = assertNotNull(shadowOf(activity).nextStartedActivity)
           assertEquals(CrashActivity::class.java.name, suivante.component?.className)
       }
   }
   ```

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi | Éprouvé ? |
|---|---|---|---|
| Pose du gestionnaire | `attachBaseContext`, avant toute autre initialisation | Une exception de l'initialisation finit elle aussi sur le rapport (ADR-029 § 1) | En partie : le gestionnaire a servi le 2026-10-03, mais pour une exception levée après le démarrage (premières tuiles) ; une exception de l'initialisation n'a pas encore été capturée en réel |
| Écran dans un processus à part | `android:process=":crash"`, vues simples | Le processus de l'app est mort ou en train de mourir | Oui (ToolMap, 2026-10-03) |
| `LOG_LINES` | 400 lignes | Ce que la plateforme et les bibliothèques ont écrit juste avant le plantage | Oui : réglage du rapport du 2026-10-03 |
| `LOG_TIMEOUT_MS` | 2 000 ms | Borne l'attente de `logcat` dans un processus qui meurt | Oui (même rapport) |
| `MAX_CAUSES` | 32 | Garde-fou contre une chaîne de causes trop longue ou qui boucle | Choisi, non mis à l'épreuve |
| `MAX_EXITS` | 16 | Sorties passées lues à chaque démarrage | Non (natif) |
| `MAX_TRACE_BYTES` | 4 Mio | Tombstone lu au plus | Non (natif) |
| `printableStrings` | `minLength` 4, `maxChars` 64 000 | Assez pour le message d'abandon, les bibliothèques et les fonctions de la pile ; bruit écarté | Non (natif) |
| Repère de version en tête | ToolMap : `versionName (versionCode)` et commit ; Popote : `Version : <versionName>` | Chez Popote, une trace venue d'une ancienne installation s'est lue comme si elle venait du code actuel et a égaré un diagnostic (`PopoteApplication.kt` l. 34-37, `docs/debogage-sqlcipher.md` § 2.1) | Oui (Popote) |

## Variante minimale : Popote Compagnie (décrite, pas copiée)

Popote Compagnie a son propre capteur, plus simple, éprouvé lui aussi.
Le code est privé (Suivie-Popote-Cie @ `6149a81`) ; voici ce qu'il fait :

- `PopoteApplication.onCreate` pose `Thread.setDefaultUncaughtExceptionHandler`. Le
  gestionnaire écrit `dernier-plantage.txt` dans `filesDir` : `Version : <versionName>` en tête,
  puis la pile (`printStackTrace`). Il appelle ensuite le gestionnaire précédent : le système
  ferme l'app comme d'habitude.
- Au lancement suivant, `MainActivity` lit le fichier. S'il existe, `EcranPlantage` (Compose)
  remplace toute l'interface : titre, texte sélectionnable en chasse fixe, bouton
  « Continuer » qui efface le fichier. Le ViewModel, donc la base, n'est créé qu'après.
- Ni fin du journal, ni description du téléphone, ni plantage natif, ni test automatisé.

| | ToolMap (`src/`) | Popote (variante) |
|---|---|---|
| Pose du gestionnaire | `attachBaseContext`, en premier | `onCreate`, après `SumUpState.init` et le lancement de la purge des exports (`PopoteApplication.kt` l. 27-32) : un plantage dans ces lignes lui échappe |
| Moment de l'affichage | Tout de suite, dans le processus `:crash` | Au lancement suivant, dans le processus principal |
| Contenu | Version et commit, téléphone, Android, ABI, OpenGL ES et Vulkan, cause première, pile, 400 lignes de journal | Version et pile |
| Plantage natif | Android 11+ (non éprouvé) | Non capturé |
| Après lecture | Gardé en `last.txt` | Effacé par « Continuer » |
| Tests | 16 tests (7 JVM, 9 Robolectric) et un test de démarrage | Aucun |

Preuves réelles (Popote `docs/decisions.md` à `6149a81`, déjà présentes à `08c211a`). Les
dates viennent des commits de `origin/main` (`3d966c4`, 2026-09-04) : la branche active
repart d'un commit sans parent (`08c211a`) et ne les montre pas.
- l. 1465-1467 : premier essai de la v13.18 sur appareil réel, un tap sur « Connecter mon
  compte SumUp » plante ; la `NullPointerException` interne au SDK est lue dans
  `dernier-plantage.txt`. Elle a mené au correctif `SumUpState.init` (v13.19). Le 2026-09-03 :
  v13.18 en `a65a085`, correctif en `45a77c5` ;
- l. 1512-1513 : en v13.19, le plantage suivant (`NoClassDefFoundError` sur
  `LocationServices`) est relevé au nouvel essai, avec sa pile. Le texte ne dit pas par quel
  canal. Correctif `3f95e5b`, le 2026-09-03 aussi ;
- `docs/debogage-sqlcipher.md` § 2.1 (l. 33-58) : pendant la migration SQLCipher (v13.1 à
  v13.4), le repère de version du rapport a tranché entre « correctif faux » et « mise à jour
  pas prise ». Repère ajouté avec la v13.1 le 2026-08-30 (`9d08bd5`), v13.4 le 2026-08-31
  (`82a011f`).

Limite consignée : un plantage natif passe sous le gestionnaire. Sur un Galaxy S25, la
première bibliothèque SQLCipher, aux `.so` alignés sur 4 Ko, faisait « flash puis
fermeture » sans aucun rapport (`docs/decisions.md` l. 700-706 ; capteur posé en `31597c0`,
correctif `4b65efe`, tous deux le 2026-08-30). C'est le cas que le chemin natif de ToolMap
vise, sans preuve à ce jour.

Le KDoc de `PopoteApplication` le dit « diagnostic temporaire, à retirer » (l. 17-18) ; il est
toujours en place à `6149a81`.

## Évolutions de la ligne vivante ToolMap (🟡, non copiées)

Sur `sauvegarde/p16-2026-10-16` (`eee8c6b`, 2026-10-24), `CrashActivity.kt`, `CrashReport.kt`
et les trois tests sont inchangés. `CrashReporter.kt` a reçu deux ajouts **jamais vérifiés sur
téléphone**, laissés hors de `src/` pour garder le ✅ honnête :

- `errorReport` et `core/.../crash/ErrorReport.kt` (+ `ErrorReportTest`, 2 tests), commit
  `159e9e4` (P16.3 « Signaler une erreur ») : un rapport envoyé à la main depuis le menu, avec
  le dernier rapport de plantage et la fin du journal. Le JOURNAL de p16 le range dans « à voir
  sur téléphone » (l. 887), même si le PLAN marque P16.3 ✅ (l. 266) ;
- crochet `EventLog` et `isMainProcess`, commit `3e6677a` (P21) : le gestionnaire inscrit
  `app.crash` dans le journal d'événements de l'app et le vide (500 ms au plus) avant de
  mourir. Il tire le journal d'événements de ToolMap (`core/.../eventlog`), candidat à part.

## Points de vigilance et limites connues

- **Vie privée : le rapport contient plus qu'une pile.** Il porte le modèle du téléphone
  (`Build.MANUFACTURER`, `Build.MODEL`), la version d'Android, et les 400 dernières lignes du
  journal de l'app : tout ce que l'app et ses bibliothèques y ont écrit (adresses, identifiants,
  messages d'erreur). Dans ToolMap, à partir de `3e6677a` et seulement en debug, le journal
  d'événements est recopié dans logcat (`TMEvent`), avec des coordonnées en mode test (JOURNAL
  p16 l. 149) : un rapport tiré d'un APK debug peut donc contenir des positions. L'écran invite
  à relire avant d'envoyer (`crash_intro`) ; gardez ce texte, et n'écrivez aucune donnée
  personnelle dans `Log` en release.
- **Règle imposée : jamais d'entité ni de donnée personnelle dans un message d'exception.** Le
  titre du rapport est `cause.toString()`, et la pile reprend tous les messages. Le texte par
  défaut d'une `data class` énumère tous ses champs : un `"$membre"` glissé dans un message
  d'erreur écrirait ses données dans le rapport, affiché, copiable, et chez Popote signalé sur
  un dépôt public. Popote a donc redéfini `toString` de `MembreEntity`, `SeedMembre` et
  `Derogation` pour que le numéro de carte du membre n'y figure jamais (`docs/decisions.md`
  l. 3080-3084, section « Le numéro de carte, vérifié chemin par chemin »). Faites de même
  pour toute classe qui porte une donnée personnelle.
- **Sauvegarde Android.** Le rapport vit dans `files/crash/`. ToolMap a
  `android:allowBackup="false"` (manifeste l. 40). Si votre app laisse la sauvegarde
  automatique active, excluez `crash/` de ses règles, sinon le rapport et sa fin de journal
  partent dans la sauvegarde du compte.
- **Plantages natifs : 🟡, jamais vus en réel.** Rien avant Android 11. Seul le processus
  principal compte (un plantage natif de `:crash` ou d'un service à part est ignoré), et seule
  la dernière sortie depuis la vérification précédente est rapportée. Le décodage du tombstone
  n'a été essayé que sur des octets fabriqués (`CrashReportTest`, `CrashReporterTest`).
- **Ni ANR ni arrêt par le système.** Seule `REASON_CRASH_NATIVE` est lue
  (`CrashReporter.kt` l. 131) : une application figée ou tuée par le système ne laisse pas de
  rapport.
- **Le gestionnaire ne passe pas la main quand il a réussi.** Il tue lui-même le processus
  (`Process.killProcess` puis `exitProcess(10)`) : pas de boîte « L'application s'est
  arrêtée » du système, et un gestionnaire posé avant lui (Crashlytics, par exemple) ne voit
  rien. Il n'appelle le précédent que si l'écriture du rapport échoue.
- **2 s d'attente au plus dans le gestionnaire**, le temps de lire `logcat` : le processus qui
  plante reste figé pendant ce temps.
- **L'écran de rapport ne répare rien.** Si la cause persiste (fichier abîmé, donnée
  illisible), « Relancer » ramène au même plantage. Chez Popote, une base illisible remontait
  jusqu'à `EcranPlantage`, dont « Continuer » rouvrait le même fichier en boucle ; corrigé dans
  le code par un écran dédié qui écarte la base, déclenchement jamais vérifié sur appareil
  (`docs/todo.md` l. 113-132, case non cochée). Un état dont l'app peut se
  remettre se traite dans l'app, pas par l'écran de plantage.
- **Tests Robolectric : le gestionnaire fuit dans la JVM des tests.** Une `Application` qui
  appelle `install` remplace le gestionnaire du lanceur de tests pour toute la suite. Remettez
  `CrashReporter.Handler.previous` dans `@After` (modèle dans « Intégration », étape 7 ;
  `CrashReporterTest` le fait aussi).
- **Ce que les tests JVM ne voient pas.** Le plantage du 2026-10-03 venait d'une expression
  régulière acceptée par le JDK des tests et refusée par le moteur ICU d'Android : ni les tests
  JVM ni Robolectric ne pouvaient le voir, seul le rapport l'a montré. C'est la raison d'être
  du moteur ; la leçon ICU elle-même concerne aussi `scan-multimode`.
- **Provenance fragile.** `fa96a1a` et `2e992e1` n'existent que sur la branche
  `ccr-e59b55ac-kt4qb9`, qui n'avance plus depuis le 2026-10-14 : le travail a continué sur
  `sauvegarde/p16-2026-10-16`, qui l'a reprise sans fusion git (`159e9e4`). Le 2026-10-27,
  ToolMap a été republié en public sans historique (`a37dcab`) : ces SHA ne se trouvent plus
  que dans l'archive privée `JRRocks/ToolMap-Archive`. Le dépôt public garde le même code
  (avec les deux ajouts ci-dessus) : c'est la référence à consulter pour une intégration.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-08-30 et 2026-08-31 (dates des commits Popote `31597c0` à `82a011f`, sur `origin/main`) | Popote, variante minimale, appareil réel | Le capteur arrive avec le chiffrement SQLCipher. Sur un Galaxy S25, la première bibliothèque plante en natif au lancement (« flash puis fermeture ») sans laisser de rapport (`docs/decisions.md` l. 700-706, correctif `4b65efe`). Puis, de la v13.1 à la v13.4, le repère `Version :` en tête de `dernier-plantage.txt` montre que la mise à jour a pris (`docs/debogage-sqlcipher.md` § 2.1) | Variante minimale : limite native consignée ; réglage « repère de version » |
| 2026-09-03 (dates des commits Popote `a65a085`, `45a77c5`, `3f95e5b`, sur `origin/main`) | Popote, variante minimale, appareil réel | v13.18 : NPE interne au SDK SumUp lue dans `dernier-plantage.txt`, d'où le correctif `SumUpState.init` (`docs/decisions.md` l. 1465-1467). v13.19 : plantage suivant relevé avec sa pile, canal non précisé (l. 1512-1513) | Variante éprouvée pour les exceptions Java |
| 2026-10-03 | ToolMap, Galaxy S25 (SM-S931U, Android 15, API 35) | APK de `fa96a1a` installé. Une `PatternSyntaxException` sur un fil « OkHttp Dispatcher » a ouvert l'écran de rapport ; l'utilisateur l'a transmis. Le rapport nommait la regex fautive (`TileUrlBuilder.kt:125`) : correctif `c620a38` (JOURNAL p16 l. 1560-1561) | Chemin Java ✅ |
| 2026-10-27 | Catalogage Library | Code repris de ToolMap `2e992e1` (identique à `fa96a1a`), sans les ajouts de `159e9e4` et `3e6677a`. `CrashReportTest` : 7/7 en JVM (Kotlin 2.0.21, JUnit 4.13.2, `kotlin-test-junit` 2.0.21, sans Gradle). `CrashReporterTest` et `CrashActivityTest` (Robolectric) non exécutés ici. Variante Popote (`6149a81`) décrite sans code, avec sa règle de confidentialité. Remplace le candidat Popote « Capteur de plantage ». Contre-vérifié le même jour : six fichiers identiques à `2e992e1` et à `fa96a1a`, numéros de ligne cités relus, exemple de rapport regénéré par `toText`, `CrashReportTest` relancé (7/7) | Entrée en bibliothèque : ✅ exceptions Java/Kotlin, 🟡 plantages natifs et saut de l'initialisation ; `src/` non modifié |
| 2026-10-27 | ToolMap republié en public | Nouveau `JRRocks/ToolMap` en un seul commit `a37dcab`, l'ancien dépôt gardé en privé sous le nom `ToolMap-Archive` (journal de ToolMap, session 66). `CrashActivity.kt`, `CrashReport.kt` et les trois tests identiques à `src/` ; `CrashReporter.kt` = version de la branche p16 (ajouts 🟡) | Provenance complétée ; statut inchangé |
| 2026-10-27 | Relecture de la Library | En-tête aligné sur l'INDEX et les composants (saut de l'initialisation 🟡) ; le correctif Popote de la base illisible n'est vérifié que dans le code, pas sur appareil | Formulations corrigées ; statut inchangé |
