# Index de la Library

> Point d'entrée de recherche. Pour les règles et les procédures, voir [`AGENTS.md`](AGENTS.md).
> **Toute modification d'une fiche met à jour cet index** (AGENTS.md § 2, règle 5).
>
> Légende des statuts : ✅ Éprouvé · 🟡 Testé · ⚠️ À patcher · 🔬 Candidat · ⛔ Obsolète

Dernière mise à jour : 2026-10-27

---

## 1. Moteurs catalogués

| Identifiant | Ce que ça fait | Statut | Plateforme | Source | Utilisé dans |
|---|---|---|---|---|---|
| [`scan-multimode`](moteurs/scan-multimode/README.md) | Scan à la chaîne QR/2D, code-barres, OCR filtré par regex (formules proposées à partir d'un exemple), NFC/RFID, **validé par consensus de N lectures identiques** | ✅ OCR et code-barres · 🟡 2D étendu · 🟡 `RegexSuggester` · ⚠️ NFC (patch NDEF dans `src/`, non vérifié sur appareil) · ⚠️ lampe après l'arrière-plan (suspecté, patch hors `src/`) | Android, CameraX, ML Kit | ScanList, SuivieMAT | ScanList, SuivieMAT, Carnet de recette |
| [`paiement-sumup`](moteurs/paiement-sumup/README.md) | Encaissement carte sur lecteur SumUp Bluetooth, connexion native du SDK, sans secret embarqué | 🟡 (écran de connexion atteint sur appareil, correctif du plantage suivant jamais confirmé ; aucun encaissement réel ; `Echec` ou `Annule` ne prouvent pas « pas débité ») · ⚠️ `Reussi` perdu si l'activité est recréée pendant le paiement (suspecté) | Android, SDK SumUp 6.0.0 | Popote Compagnie | Popote Compagnie |
| [`google-drive-appdata`](moteurs/google-drive-appdata/README.md) | Autorisation Google (Identity `AuthorizationClient`) et fichiers dans le dossier caché `appDataFolder` du Drive, en REST pur ; variante dossier visible en `drive.file` | ✅ (dépôt manuel ; envoi périodique jamais vérifié) · ⚠️ lecture des 403 (patch dans `src/`, non vérifié sur appareil) · ⚠️ jeton révoqué en cache (suspecté, patch hors `src/`, dans le Carnet) | Android, Play Services Auth | Popote Compagnie | Popote Compagnie, Carnet de recette |
| [`maj-auto-github`](moteurs/maj-auto-github/README.md) | L'APK se met à jour seul depuis les Releases d'un dépôt GitHub public, CI de publication signée incluse | ✅ · ⚠️ annulation avalée (patch dans `src/`, non vérifié sur appareil) · ⚠️ CI : mots de passe relus par `Properties.load` (latent, patch dans `src/`) | Android et GitHub Actions | Popote Compagnie | Popote Compagnie, Carnet de recette |
| [`astro-soleil-lune`](moteurs/astro-soleil-lune/README.md) | Lever, coucher et midi solaire, jour et nuit polaires, phase, illumination et lever de la lune, hors-ligne | 🟡 (deux défauts du calcul de la lune corrigés dans la Library le 2026-10-27 ; 13 tests verts) | Kotlin pur | Android-Suntrack | Sun Track |
| [`rapport-plantage`](moteurs/rapport-plantage/README.md) | Rapport de plantage lisible sur le téléphone, à copier ou partager, sans adb, logcat ni réseau : exception Java affichée tout de suite dans un processus `:crash`, plantage natif relu au démarrage suivant | ✅ exceptions Java/Kotlin · 🟡 plantages natifs (Android 11+) et saut de l'initialisation | Android natif | ToolMap | ToolMap, Popote Compagnie (variante minimale) |

---

## 2. Je cherche à…

| Besoin | Aller voir |
|---|---|
| Scanner un QR Code, un Data Matrix, un PDF417 ou un Aztec | `scan-multimode` (mode Code 2D) |
| Scanner un code-barres EAN, UPC, Code 128, Code 39… | `scan-multimode` (mode Code-barres) |
| Lire un numéro de série imprimé (OCR) et en extraire un motif | `scan-multimode` (OCR + `RegexRuleEngine.extract`) |
| Reconnaître un numéro sur une étiquette **sans regex** (format qui peut changer), en le comparant à une liste connue et à la taille du texte | `scan-multimode` (variante Carnet de recette : `OcrElements`, `ReconnaissanceEtiquette`) |
| Éviter les lectures fausses d'une caméra (reflets, angle) | `scan-multimode` → `ScanConsensus` |
| Éviter qu'un même code soit compté 30 fois par seconde | `scan-multimode` → `ScanDebouncer` |
| Ne pas revalider en boucle une étiquette gardée en vue | `scan-multimode` (variante Carnet de recette : anti-rebond glissant de `SuiviEtiquettes`) |
| Lire un tag NFC ou RFID (UID ou texte NDEF) | `scan-multimode` (mode NFC, ⚠️ patch NDEF non vérifié sur appareil) |
| Identifier une personne par l'UID d'une carte NFC | `scan-multimode` (mode NFC ; lire la vigilance UID : UID aléatoire, UID à ne pas exporter) |
| Valider qu'une valeur respecte un format (regex), messages d'erreur en français | `scan-multimode` → `RegexRuleEngine.validate` / `conforms` |
| Obtenir une formule regex à partir d'un exemple de valeur (« 1GH546 »), sans IA | `scan-multimode` → `RegexSuggester` (🟡) |
| Bip et vibration distincts pour succès, doublon et erreur | `scan-multimode` → `ScanFeedback` |
| Encaisser un paiement par carte bancaire | `paiement-sumup` |
| Éviter un double débit quand un paiement carte semble échoué ou annulé | `paiement-sumup` (points de vigilance, `avertirDebitEventuel`) |
| Se connecter avec un compte Google sans secret dans l'APK | `google-drive-appdata` |
| Sauvegarder des données de l'app sur le Google Drive de l'utilisateur | `google-drive-appdata` |
| Déposer sur le Drive des fichiers qu'un autre outil (Claude Cowork, l'utilisateur) doit lire | `google-drive-appdata` (variante dossier visible en `drive.file`) |
| Distinguer une panne de réseau d'un refus de Google Drive, dire les erreurs Play Services en français | `google-drive-appdata` (variante Carnet : `estPanneReseau`, `echecGoogle`, documentés, pas dans `src/`) |
| Me connecter à Google Drive depuis une app de bureau, sans Play Services | 🔬 candidat InnovAdapt, OAuth PKCE + loopback en Rust (§ 5) ; voir aussi `google-drive-appdata` |
| Distribuer et mettre à jour une app sans Play Store | `maj-auto-github` |
| Publier les APK d'une app dont le code est déjà public, sans second dépôt ni jeton | `maj-auto-github` (variante « dépôt de code public ») |
| Publier un APK signé depuis GitHub Actions | `maj-auto-github` (`src/version.yml`) |
| Signer un APK en CI sans que le mot de passe soit corrompu (`\`, espaces de tête) | `maj-auto-github` (`src/version.yml`, extrait `signingConfigs.release`) |
| Éviter les pièges GitHub Actions d'une app Android (quota d'artefacts, `setup-android`, minutes épuisées, balise refusée en session) | `maj-auto-github` (« Pièges de CI ») et [`docs/methodes/android-en-session-cloud.md`](docs/methodes/android-en-session-cloud.md) |
| Proposer une mise à jour par une ligne d'état, sans boîte de dialogue, qui survit à la rotation | `maj-auto-github` (« Exemple d'interface », Carnet de recette, non éprouvé) |
| Connaître l'heure du lever ou du coucher du soleil, ou la phase de la lune | `astro-soleil-lune` |
| Voir un écran Compose sans émulateur ni téléphone (PNG) | [`docs/methodes/captures-compose-robolectric.md`](docs/methodes/captures-compose-robolectric.md) |
| Compiler ou tester une app Android depuis une session cloud, contourner le 429 de Maven Central, lancer des tests Kotlin purs sans Gradle | [`docs/methodes/android-en-session-cloud.md`](docs/methodes/android-en-session-cloud.md) |
| Faire arriver sur le téléphone un APK de plus de 30 Mio | [`docs/methodes/android-en-session-cloud.md`](docs/methodes/android-en-session-cloud.md) § 5 |
| Lire un fichier Excel `.xlsx` sans bibliothèque | 🔬 candidat InnovAdapt (§ 5) ou Popote `ImportBase` |
| Lire ou écrire du GPX ou du KML, convertir UTM, MGRS ou Lambert 93 | 🔬 candidats ToolMap (§ 5) |
| Appeler un LLM avec une sortie JSON structurée, laisser un LLM explorer des données locales sous plafond de tokens | 🔬 candidats Activity-track (§ 5) |
| Lire des fichiers déposés dans un dossier par un autre outil, sans OAuth | 🔬 candidat Activity-track, dossier surveillé SAF (§ 5) |
| Synchroniser des données chiffrées entre plusieurs appareils par un Drive | 🔬 candidats InnovAdapt (§ 5) |
| Afficher une météo sans clé d'API | 🔬 candidat Android-Suntrack, Open-Meteo (§ 5) |
| Lire un QR Code sans Google Play Services | 🔬 candidat ToolMap, CameraX + ZXing (§ 5) |
| Filtrer les appels des numéros inconnus | 🔬 candidat Scam-Stop (§ 5, licence GPL) |
| Purger les artefacts GitHub Actions d'un compte | 🔬 candidat SuivieMAT (§ 5) |
| Récupérer un rapport de plantage sans `adb logcat`, savoir pourquoi une app installée hors Play Store se ferme | `rapport-plantage` |
| Relire au démarrage suivant un plantage natif (signal dans une `.so`) | `rapport-plantage` (`ApplicationExitInfo`, Android 11+, 🟡) |
| Coloriser des pages de manga en noir et blanc | 🔬 candidat Mihon-edited-, modèle externe (§ 5) |
| Lire des étapes à voix haute, commander à la voix sans IA | 🔬 candidats Carnet de recette (§ 5) |

---

## 3. Mots-clés

| Mot-clé | Moteurs |
|---|---|
| Affiliate Key | `paiement-sumup` |
| `ApplicationExitInfo`, tombstone, `REASON_CRASH_NATIVE` | `rapport-plantage` |
| `android:process`, processus séparé | `rapport-plantage` |
| Almanach, éphémérides, NOAA | `astro-soleil-lune` |
| APK, sideload, sources inconnues, `REQUEST_INSTALL_PACKAGES` | `maj-auto-github` |
| `AuthorizationClient`, Identity, OAuth Android, SHA-1 | `google-drive-appdata` |
| Bluetooth, lecteur de carte, TPE | `paiement-sumup` |
| CameraX, `ImageAnalysis`, `KEEP_ONLY_LATEST` | `scan-multimode` |
| Consensus, lectures consécutives, anti-faux positif | `scan-multimode` |
| Debounce, anti-rebond | `scan-multimode` |
| Distance d'édition (Levenshtein 1) | `scan-multimode` (`ScanConsensus.withinEditDistanceOne`) |
| Drive v3, `appDataFolder`, multipart, pagination `nextPageToken` | `google-drive-appdata` |
| FileProvider | `maj-auto-github` |
| GitHub Actions, release signée, keystore en secret | `maj-auto-github` |
| GitHub API `releases/latest` | `maj-auto-github` |
| `Properties.load`, `keystore.properties`, `signingConfigs`, mot de passe de signature, `keytool`, PKCS12 | `maj-auto-github` |
| Quota d'artefacts, `upload-artifact`, `setup-android`, `workflow_dispatch`, `runner_id` 0 | `maj-auto-github` (« Pièges de CI ») |
| Ligne d'état, `EtatMiseAJour`, « Plus tard » | `maj-auto-github` (exemple d'interface) |
| ML Kit Barcode, ML Kit Text Recognition | `scan-multimode` |
| OCR par éléments, réticule, `cropRect`, `ViewPort`, rotation | `scan-multimode` (variante Carnet de recette) |
| NDEF, RTD_TEXT, UID, `enableReaderMode`, `FLAG_READER_SKIP_NDEF_CHECK` | `scan-multimode` |
| UID aléatoire, clonage de badge | `scan-multimode` (vigilance NFC) |
| `onActivityResult`, `startActivityForResult` | `paiement-sumup` |
| PDF417, Data Matrix, Aztec, Codablock | `scan-multimode` |
| Phase lunaire, jour polaire, transit de la lune, changement d'heure (DST) | `astro-soleil-lune` |
| Regex, `PatternSyntaxException`, ICU (regex Android) | `scan-multimode` |
| Générateur de regex, regex par l'exemple | `scan-multimode` (`RegexSuggester`) |
| Robolectric, injection de dépendance pour un SDK non testable | `paiement-sumup` |
| Robolectric, `@GraphicsMode(NATIVE)`, capture PNG, `qualifiers`, `fontScale`, `forkEvery` | [`docs/methodes/captures-compose-robolectric.md`](docs/methodes/captures-compose-robolectric.md) |
| SessionStart, `sdkmanager`, miroir Maven, 429, `--max-workers=1`, `android-all-instrumented`, clé de debug commune, `release/` | [`docs/methodes/android-en-session-cloud.md`](docs/methodes/android-en-session-cloud.md) |
| `UncaughtExceptionHandler`, plantage, crash, alternative sans réseau à ACRA ou Crashlytics | `rapport-plantage` |
| SumUp, `SumUpAPI`, `SumUpState.init`, `play-services-location` | `paiement-sumup` |
| Double débit, « Réessayer », `onBackPressed`, délai de garde | `paiement-sumup` |
| `versionCode`, mise à jour en ligne | `maj-auto-github` |
| WorkManager, échec définitif ou transitoire | `google-drive-appdata` |
| `RefusDrive`, `estPanneReseau`, `echecGoogle`, `NETWORK_ERROR`, `UNREGISTERED_ON_API_CONSOLE` | `google-drive-appdata` (variante Carnet) |
| `clearToken`, jeton en cache, 403 `insufficientPermissions`, `accessNotConfigured`, `rateLimitExceeded` | `google-drive-appdata` |
| Écran de consentement en mode Test, refresh token 7 jours | `google-drive-appdata` (vigilance, à vérifier) |

---

## 4. Par plateforme

| Plateforme | Moteurs |
|---|---|
| **Kotlin pur** (réutilisable hors Android) | `astro-soleil-lune` ; dans `scan-multimode` : `ScanConsensus`, `ScanDebouncer`, `ScanValue`, `RegexRuleEngine`, `RegexSuggester`, `NfcValue` ; dans `rapport-plantage` : `CrashReport.kt` |
| **Android natif** | `scan-multimode`, `paiement-sumup`, `google-drive-appdata`, `maj-auto-github`, `rapport-plantage` |
| **CI GitHub Actions** | `maj-auto-github` |
| **Python** | aucun moteur pour l'instant (candidats Activity-track, dont les versions les plus portables sont en Kotlin JVM pur dans `android/core` ; colorisation manga de Mihon-edited-, modèle externe ; import d'icônes du Carnet, `outils/icones.py`) |
| **Rust** | aucun moteur pour l'instant (candidats InnovAdapt : OAuth et Drive sur le bureau, synchro chiffrée) |
| **JS navigateur** | aucun moteur pour l'instant (candidats InnovAdapt : `.xlsx`, CSV, synchro chiffrée, diagnostic) |

---

## 5. Candidats repérés (🔬 à évaluer puis extraire selon AGENTS.md § 5.D)

### Popote Compagnie, code dans [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) @ `6149a81` (pointe `93e5d5b` au 2026-10-27 : code des trois moteurs inchangé, `version.yml` passé sur un runner auto-hébergé en `2bd6298`)

> Le capteur de plantage est désormais décrit dans [`rapport-plantage`](moteurs/rapport-plantage/README.md)
> (variante minimale) ; les captures Compose sans émulateur dans [`docs/methodes/`](docs/methodes/README.md).
> Données réelles (membres, tickets, seed) dans `docs/source/`, `*/src/test/resources/` et les PNG de captures :
> ne jamais les copier.

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Sauvegarde récupérable par trousseau asymétrique | `core/.../sauvegarde/{Trousseau,Enveloppe,Coffre,Contenu}.kt` (36 tests) | L'app chiffre sans pouvoir déchiffrer seule ; restauration avec le mot de passe admin. Complète `google-drive-appdata` | **Preuve réelle partielle** : sur Galaxy S25, trousseau descellé par le mot de passe d'origine et restauration en lecture de bout en bout (`docs/debogage-sqlcipher.md`). Protocole `docs/tests-restauration.md` (tests 0 à 8) jamais coché, envoi périodique non éprouvé. Prochain à extraire |
| Base chiffrée SQLCipher + Keystore | `core/.../db/{SqlCipherUtilitaires,ClefBase}.kt`, `administration/CoffreLocal.kt` (AES-256-GCM sous Keystore) | Chiffrement au repos de Room ; pièges précieux : `.so` alignés sur 4 Ko qui plantent sur les pages de 16 Ko, `sqlcipher-android` 4.17.0 (la 4.18.0 exige compileSdk 37), `loadLibrary` explicite | **Preuve réelle** sur S25 (base ouverte chiffrée sous passphrase Keystore). Une seule app en a besoin aujourd'hui ; natif non testable en JVM |
| Sauvegarde continue « socle + points » | `core/.../sauvegarde/{SauvegardeContinue,Point,TravailSauvegarde}.kt` (38 tests) | Sauvegarde incrémentale vers Drive après 30 s de calme, rotation | Déclenchement périodique et rotation à 7 archives jamais vérifiés sur appareil ; s'appuie sur `google-drive-appdata` |
| Grand-livre chaîné par hash et audit | `core/.../ledger/{Chaine,Empreinte,Audit}.kt` + `db/Triggers.kt` | Écritures non retouchables, vérification d'intégrité | Très lié au schéma Popote ; aucun retour propre au grand-livre |
| Lecture de ticket de caisse | `core/.../ticket/{LectureTicket,Classement,Reconstitution,Releve}.kt` | OCR ML Kit (Play Services) sur PDF ou photo, reconnaissance de **formes** (libellé + montant), mémoire des classements | Lecture d'un vrai ticket sur téléphone « à faire » ; les tests lisent de vrais tickets : ne pas les copier |
| Rendu de monnaie | `core/.../model/Rendu.kt` (10 tests) | Coupures tirées de la caisse réelle | Petit, isolable. Depuis la v131 une coupure peut passer en négatif : `Rendu` filtre `quantite > 0`, à garder |
| Signalement de bug | `app-terrain/.../signalement/Signalement.kt` | Bouton « Signaler un problème » qui ouvre une issue GitHub via un jeton fine-grained limité à `Issues` sur le dépôt public | Le jeton n'est pas dans le dépôt mais il est compilé dans `BuildConfig`, donc lisible dans tout APK distribué : à écrire dans la fiche. Jeton à ne pas copier |
| Lecture UID de carte NFC | `core/.../nfc/{UidCims,SignatureCims}.kt`, `app-terrain/.../nfc/LecteurCims.kt` | Identification par carte NFC | À rapprocher de `scan-multimode` (NFC) ; une carte à UID aléatoire rend l'identification inopérante ; aucune lecture réelle consignée |
| Import `.xlsx` | `core/.../import/ImportBase.kt` (+ `org/kxml2/io/KXmlParser.java`) | Import d'un tableur réel | Lié au modèle Popote. `KXmlParser.java` est du code tiers (licence de type MIT, en-tête à garder) ; son en-tête note qu'Expat refusait un vrai classeur Google Sheets sur appareil. Le test `ImportBaseXlsxReelTest` lit des données réelles : ne pas le copier |

### ScanList, [JRRocks/ScanList](https://github.com/JRRocks/ScanList) @ `2ef0b6d`, et SuivieMAT, [JRRocks/SuivieMAT](https://github.com/JRRocks/SuivieMAT) (public depuis le 2026-10-27) @ `5eb5198`

SuivieMAT est un fork de ScanList pris à `1368027` ; il garde le namespace Kotlin `com.scanlist.app`.
Chemins relatifs à `app/src/main/java/com/scanlist/app/`. Le générateur de formules regex de SuivieMAT
est désormais un composant de `scan-multimode` (`RegexSuggester`).

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Codec CSV | `data/csv/CsvCodec.kt` (+ test) | BOM UTF-8, `;`, CRLF : ouverture propre dans Excel FR | **Défauts connus** (SuivieMAT `consignes/plan-suivimat.md` § 10) : `escapeField` ne neutralise pas l'injection de formule (valeur commençant par `=`, `+`, `-` ou `@`, alors qu'un QR Code est écrit par n'importe qui) ; l'import n'est décodé qu'en UTF-8 alors qu'Excel FR enregistre en Windows-1252. À corriger avant extraction, puis fiche commune avec les CSV JS et Rust d'InnovAdapt |
| Moteur de comparaison à une référence | `domain/compare/CompareEngine.kt` (+ test) ; SuivieMAT ajoute `domain/compare/CompareOverview.kt` (9 tests, `71ee7aa`) | Liste de référence rouge, passée au vert à la lecture (inventaire, contrôle) | Fiche. SuivieMAT corrige aussi des sessions de comparaison impossibles à rouvrir (`observeSessions` lu nulle part), défaut encore présent dans ScanList. Aucun usage réel |
| Export par mail | `export/{ExportManager,ExportFileName}.kt` | Écriture en cache, FileProvider, `ACTION_SEND`, purge à 24 h, nom de fichier assaini | Fiche |
| Recherche insensible à la casse et aux accents | `domain/search/FolderSearch.kt` (+ test) | Petit, pur | Fiche |
| Rangement chronologique « Aujourd'hui / Cette semaine / Plus ancien » | SuivieMAT `domain/folders/FolderTimeline.kt` (10 tests) | Semaine commençant le lundi, jour jugé dans le fuseau du téléphone, moment dit au plus court en français | Intérêt modeste (80 lignes) ; aucun usage réel |
| Purge des artefacts GitHub Actions | SuivieMAT `.github/workflows/nettoyage-actions.yml` | Workflow réutilisable : supprime les artefacts plus vieux que N heures en épargnant le run en cours. Le quota de 0,5 Go est commun à tout le compte | Testé contre un `gh` simulé seulement ; résultat réel non consigné. À rattacher à `maj-auto-github` ou à `docs/methodes/` |

### Android-Suntrack, [JRRocks/Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) @ `e5c94b4` et branche `meteogram-weather-icons` @ `2cb08aa`

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Météo Open-Meteo | `data/WeatherRepository.kt`, `WeatherCodes.kt` | Météo actuelle **sans clé d'API ni compte** ; version enrichie sur la branche (nuages, vent, CAPE, séries horaires de précipitation, cache de 3 h) | Pas de test. `hourIndex()` rend 0 (minuit) si `current.time` manque. Conditions d'usage d'Open-Meteo (gratuit en non commercial, données CC BY) à vérifier pour une app commerciale |
| Icônes météo à paliers | branche : `render/WeatherTiers.kt` (pur, 5 tests), `render/WeatherIconPainter.kt` (Canvas) | Nuages par paliers de 10 %, pluie = probabilité × quantité, orage selon la CAPE, vent ; même dessin en SVG (maquette) et en Canvas | Branche non fusionnée, tests jamais passés en CI, seuils choisis à la main, aucun retour réel |
| Localisation sans Play Services | `location/LocationResolver.kt` | Dernière position connue pour le fond, position fraîche à la demande, mise en cache | Pas de test |
| Rafraîchissement de widget à la minute | `work/{TickScheduler,RefreshWorker,BootReceiver}.kt` | Alarme RTC inexacte (sans `SCHEDULE_EXACT_ALARM`, sans réveil), WorkManager 30 min, redémarrage au boot | Pas de test |
| Palette de ciel | `render/SkyPalette.kt` (12 tests, 14 sur la branche) | Couleurs selon la hauteur du soleil. Leçon générique de la v1.7 : interpoler les canaux ARGB **pondérés par l'opacité**, sinon un arrêt transparent salit le fondu | Spécifique au rendu |

### Activity-track, [JRRocks/Activity-track](https://github.com/JRRocks/Activity-track) @ `855773c`

> Branche active `claude/garmin-llm-feedback-app-fnlhmp` (`main` en retard à `cdf458d`). Deux modules :
> serveur Python (91 tests pytest, 86 verts et 5 sautés sans données réelles) et app Android dont le
> cœur est en **Kotlin JVM pur** (`android/core`, 63 tests). Les tests sur données réelles se sautent en
> leur absence ; leurs valeurs attendues viennent de données de santé réelles : ne pas les copier.

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Backends LLM interchangeables | `app/llm/backends/{base,anthropic_api,claude_code}.py` ; Android : `android/.../llm/AnthropicClient.kt` | Contrat mince requête / résultat. API Anthropic (réflexion adaptive, `output_config` effort et format JSON Schema, cache d'une heure) ou CLI Claude Code cloisonnée (`--restricted`, `--strict-mcp-config`, dossier vide, `--max-budget-usd`, `--json-schema`) | Gemini retiré en `855773c`. Exécution réelle ponctuelle du backend `claude_code` sur de vraies données (`e50145d`, poste de développement) ; backend API jamais exécuté en réel ; client Android jamais compilé par la CI (quota d'artefacts) |
| Brief LLM sous budget de tokens | `app/analytics/brief.py` ; port Kotlin `android/core/.../BriefBuilder.kt` | Agrégats plutôt que lignes brutes, dégradation par paliers consignée | Défaut suspecté côté Python : l'estimation `len/3,2` sous-compte les charges numériques (mesuré sur le tokenizer Gemini). Partir du Kotlin |
| Investigation multi-tours sous plafond de tokens | `android/core/.../{Investigation,Probes}.kt` (7 + 11 tests) | Le modèle demande des tranches de données à un catalogue fixe d'actions en lecture seule ; plafond jamais dépassé ; transcript gardé pour audit | `Probes` propre au sport ; version Anthropic jamais exécutée |
| Estimation prudente de tokens et tables en colonnes | `android/core/.../{Tokens,Table}.kt` (3 tests + couverture indirecte) | Chaque chiffre et ponctuation compte pour un token : ne sous-compte jamais sur les charges mesurées ; tables JSON en colonnes (idée TOON) | Calibré sur Gemini seulement : à re-mesurer contre `count_tokens` d'Anthropic |
| Relance en arrière-plan sur 429/5xx | `android/.../work/AnalysisWorker.kt` | `Result.retry()` avec backoff exponentiel (12 tentatives), survit à la fermeture de l'app, l'interface se raccroche au travail | Pas de test ; relance depuis le début (tokens perdus) |
| Dossier surveillé par le Storage Access Framework | `android/.../work/FolderScanWorker.kt` | Alternative **sans OAuth ni projet Cloud** à `google-drive-appdata` pour lire des fichiers déposés par un autre outil ; relecture idempotente par hachage | Pas de test ; compatibilité avec un dossier Google Drive non vérifiée |
| Fusion multi-source sans doublon | `app/ingest/merge.py` ; `android/core/.../Merge.kt` (5 tests) | Identité par id externe puis par proximité temporelle ; **jamais écraser une valeur par null** | Fiche ; la version Kotlin pure est la plus isolable |
| Lecteur d'export de compte Garmin | `app/ingest/garmin_zip.py` ; `android/core/.../GarminZipReader.kt` | ZIP de ZIP, ~80 JSON, conversion d'unités | Spécifique à Garmin |
| Dépôt automatique Drive → app sans projet Cloud | `apps_script/Code.gs`, README § Phase 2 | Pousse les nouveaux fichiers d'un dossier Drive toutes les heures | Aucune preuve de déploiement |

### ToolMap, revu à `eee8c6b` ; état public [JRRocks/ToolMap](https://github.com/JRRocks/ToolMap) @ `a37dcab` (depuis le 2026-10-27)

> Revue faite le 2026-10-27 sur la ligne vivante d'alors, `sauvegarde/p16-2026-10-16` @ `eee8c6b`
> (la branche `ccr-e59b55ac-kt4qb9` @ `2e992e1` y était absorbée). Le même jour, ToolMap a été
> **republié en public sans historique** (un seul commit `a37dcab`, sessions 56 à 66 en plus, 1 501
> tests) ; l'ancien dépôt, avec tous les SHA cités ici et dans les fiches, est gardé en privé sous le
> nom `JRRocks/ToolMap-Archive`. **Utilisé sur un Galaxy S25 depuis le 2026-10-03** (journal du
> dépôt). Le module `:core` est en Kotlin pur. Ne rien reprendre d'une version antérieure à `c620a38`
> (dont l'ancien `main` `06c80c7`) : `TileUrlBuilder` et `SourceDraft` y plantent sur Android (regex
> refusée par ICU) et l'app ne s'y ouvrait sur aucun téléphone (`806ffb3`). Le rapport de plantage
> est extrait dans [`rapport-plantage`](moteurs/rapport-plantage/README.md).

| Candidat | Chemin (`core/src/main/kotlin/fr/toolmap/core/` sauf mention) | Intérêt | Ce qui manque |
|---|---|---|---|
| Systèmes de coordonnées | `geo/{Utm,Mgrs,Lambert93,CoordinateFormat,CoordinateParser,GridReference}.kt` (24 tests directs) | DD, DMS, UTM, MGRS, Lambert 93 : formatage et analyse, vérifiés contre PROJ | ⚠️ à `eee8c6b` : exceptions Norvège et Svalbard non appliquées, lettres MGRS fausses au-delà de la limite de zone, pas de grille de part et d'autre de l'équateur. Corrigés à `a37dcab` (P24.1 ✅, session 58), sans preuve sur appareil |
| Traces GPX, KML et GeoJSON | `track/{GpxReader,GpxWriter,KmlReader,GeoJsonReader,TrackStats,ElevationProfile,PathSimplifier,Geodesy}.kt` (96 tests) | Lecture, écriture (extensions Garmin conservées), statistiques, simplification | Aucun usage réel consigné ; à extraire avec `SafeSax` |
| Tuiles et MBTiles hors-ligne | `tiles/{TileMath,TilePlanner,TileUrlBuilder,TileResolver}.kt`, `data/.../tiles/{MbTilesStore,TileInterceptor}.kt` | Cache de cartes servi à MapLibre par un intercepteur OkHttp | Carte affichée sur le téléphone via l'intercepteur, cartes selon l'altitude jugées satisfaisantes. Lourd à isoler (AQX, masques). Pièges MapLibre : `MapLibre.getInstance` avant `setOkHttpClient`, artefact OpenGL ES, exception dans un intercepteur OkHttp = processus tué, taille de tuile selon la densité |
| Format AQX (AlpineQuest) | `aqx/{AqxParser,AqxWriter}.kt` | Import et export de sources de cartes | Spécifique à AlpineQuest ; clé publique IGN dans les fixtures (gabarit `<IGN_APIKEY>` en cas de copie) |
| Altimètre barométrique auto-calibré | `altitude/{BarometricAltimeter,Atmosphere,AltitudeSelector}.kt` (21 tests) | Fusion baromètre, relief et GPS | Capteur réel jamais vérifié sur téléphone |
| Écrivain PDF minimal | `pdf/{PdfWriter,HelveticaMetrics}.kt` (10 tests) | PDF sans bibliothèque (JPEG, Flate, vectoriel, Helvetica) | Rendus validés par l'utilisateur, mais produits sur l'émulateur ; A3 à 300 dpi sur téléphone non vérifié |
| Partage de position chiffré | `p2p/*.kt`, module `:p2p` (`FirebaseRelay.kt`) | Position partagée en groupe, AES-256-GCM, appairage par QR ou lien, **relais REST/SSE Firebase** (et non « sans serveur ») | Tests en direct le 2026-10-24 (426 positions émises, 403 reçues) ; P21.5 clos par décision de l'utilisateur, plusieurs corrections vérifiées en tests unitaires seulement. URL et clé Firebase compilées dans les APK |
| XML sûr | `xml/SafeSax.kt` | Analyse SAX protégée (XXE, entités) | Pas de test dédié ; à extraire avec GPX ou WMTS |
| Capacités WMS/WMTS | `wizard/{WmsCapabilities,WmtsCapabilities,ApiKeyExtractor,SourceDraft}.kt` (41 tests) | Ajout guidé d'une source de carte, clé `apikey=` masquée | Vérifié contre un vrai serveur depuis la session, pas sur appareil |
| Journal d'événements et diagnostic en direct | `eventlog/{EventLog,EventStore}.kt`, module `:debugkit` (retiré du build à `a37dcab`, ADR-072, sources gardées) | Journal numéroté de tout ce que fait l'app, envoyé en direct à un collecteur en debug | **Preuve réelle** : 12 493 lignes du téléphone sans trou ni doublon (2026-10-24). Le transfert dépend du canal chiffré du partage ; le journal contient des positions |
| Notification unique pour plusieurs services | `app/.../service/BackgroundNotification.kt` (4 tests) | Une notification de premier plan partagée, une ligne et un bouton d'arrêt par fonction | Conforme sur le téléphone (2026-10-24) ; besoin récurrent non avéré |
| Alertes des réglages qui coupent un service | `app/.../share/ShareAlerts.kt` | Localisation coupée, économiseur de batterie, optimisation de batterie de l'app | Vérifié sur émulateur seulement |
| Masque d'altitude Terrarium et décodeur PNG pur | `elevation/{ElevationMask,PngDecoder,TerrariumTile}.kt`, `tiles/SolidPng.kt` | Masquer une tuile selon une bande d'altitude ; coder et décoder du PNG sans Android | Jugé satisfaisant sur le téléphone (2026-10-23), sans mesure ; très spécifique sauf `PngDecoder` |
| Géoïde EGM96 | `geo/EgmGeoid.kt` (6 tests) + données NGA | Hauteur GPS → altitude | Aucune preuve réelle ni besoin ailleurs |
| Lecture de QR Code sans Play Services | `app/.../share/QrScanner.kt` (CameraX + ZXing) | Alternative au chemin ML Kit de `scan-multimode` | Aucun usage réel du scan QR consigné |
| Contrôle de portabilité des regex vers ICU (méthode de test) | `core/src/test/.../RegexPortabilityTest.kt` | Refuse, dès les tests JVM, les regex qu'ICU (Android) peut refuser. Né d'un plantage réel (`c620a38`) | Contrôle de grammaire, pas ICU ; seul le premier littéral d'un appel est lu ; seuils propres à ToolMap |
| Test de démarrage de l'app sous Robolectric (méthode) | `app/src/test/.../AppStartupTest.kt` | Démarre l'`Application` (Hilt) puis `MainActivity` jusqu'à la carte ; attrape les plantages au lancement | Échouait sur l'ancien code, l'app s'est ensuite ouverte sur le téléphone ; à décrire en fiche méthode |

La sauvegarde de base Room à chaud (`RoomDatabaseFreezer`, 10 lignes) n'est plus un candidat : c'est une
astuce à citer dans une future fiche de sauvegarde (`PRAGMA wal_checkpoint(TRUNCATE)` puis transaction
d'écriture tenue pendant la copie).

### InnovAdapt-CRM, [JRRocks/InnovAdapt-CRM](https://github.com/JRRocks/InnovAdapt-CRM) @ `d2c2f6e`

> Branche `claude/android-rust-mobile`, 37 commits au-dessus de `main` (`42e9534`, inchangé). Elle ajoute une
> synchro chiffrée en JS et en Rust, une PWA, un squelette Capacitor et une app Rust/Slint (Windows et
> Android). Tests exécutés pendant la revue : `cargo test -p crm-core` 79/79, `node --test` 54/54. Aucun
> essai réel consigné. Licence `UNLICENSED` : rien ne passe dans la Library sans l'accord de la propriétaire.
> Aucun secret OAuth dans l'historique du code ; la configuration des binaires publiés est à revoir avec
> la propriétaire avant toute réutilisation (détail hors Library).

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Lecteur `.xlsx` sans bibliothèque (JS) | `innovadapt-crm-simple.html` : `readZipCentralDirectory`, `extractZipEntry`, `colLettersToIndex`, `parseXlsxToRows`, `excelSerialToIso` | ZIP lu à la main + `DecompressionStream('deflate-raw')` + `DOMParser` ; première feuille seulement | Pas de test JS (patron possible : `tests/helpers.mjs` + fixture Rust `tiny_xlsx()`) ; pas de ZIP64 ; « première feuille » = `sheet1.xml` sinon tri lexical |
| Analyseur CSV à séparateur auto (JS) | `parseCSV` | `;` ou `,` détecté, guillemets, BOM | BOM **littéral** dans la regex (à remplacer par l'échappement `\uFEFF`) ; pas de test JS ; jumeau Rust testé |
| Export CSV pour Excel FR | `downloadCSV`, `triggerFileDownload` ; Rust `crates/crm-core/src/export.rs` | BOM, `;`, repli sur la capacité de téléchargement des Artifacts | Trois implémentations du même besoin (Kotlin ScanList, JS, Rust) : une fiche commune, sur la base du `CsvCodec` testé |
| Normalisation de dates d'import | `normalizeImportDate` ; Rust `import.rs` | ISO, JJ/MM/AAAA, numéro de série Excel | Ne contrôle ni le jour ni le mois (« 31/02/2026 » donne « 2026-02-31 ») |
| OAuth « application installée » et client Drive v3, en Rust | `crates/crm-core/src/sync/{oauth,drive,cloud}.rs` (8 tests contre un faux serveur) | Connexion Google (PKCE, redirection loopback) et Drive sur le **bureau**, là où Play Services n'existe pas : complète `google-drive-appdata` | Défauts à corriger : 403 non lu, jeton non invalidé sur un 401, écriture « réussie » sur un 404, dossier retenu par `modifiedTime`, relance non branchée dans l'app. Sur Android, voie non recommandée par Google |
| Synchro chiffrée multi-appareils | Rust `crates/crm-core/src/sync/{hlc,canonical,diff,replica,changelog,segments,engine,manager,docs}.rs` ; JS `core/*.mjs` | Un journal par appareil, segments chaînés par SHA-256, AES-256-GCM, fusion champ par champ ; ids **déterministes** pour les enregistrements dérivés | Collections du CRM codées en dur ; limites listées dans `PROBLEMS.md` ; aucun essai réel |
| Trousseau : clé de données enveloppée par mot de passe et par clé de récupération | `crates/crm-core/src/sync/crypto.rs`, `core/crypto.mjs` | Changer de mot de passe ne ré-enveloppe que la clé ; clé de récupération montrée une seule fois | Politique de mot de passe trop faible pour un trousseau stocké sur Drive : à durcir avant toute extraction. À rapprocher du trousseau de Popote |
| Pont Android pour app Rust/Slint | `crates/crm-app/src/android.rs`, `android-rust/.../MainActivity.java` | Navigateur, sélecteur de documents, exports via MediaStore, insets, touche Retour | Sans test, jamais lancé sur appareil |
| Diagnostic « aucune panne silencieuse » pour app HTML | `innovadapt-crm-simple.html` (`window.crmDiag`) | Journal local, bandeau, plein écran si le démarrage échoue, bascule IndexedDB → `localStorage` | Équivalent JS de `rapport-plantage` ; test de bout en bout manuel seulement |

### Scam-Stop, [JRRocks/Scam-Stop](https://github.com/JRRocks/Scam-Stop) (public) @ `b971f43`

> App créée le 2026-10-26, dérivée de Non-Contact Call Blocker (franciscogouveia/android-call-blocker),
> sous licence **GPL-3.0-or-later** : une app qui reprend ce code devient GPL. Aucun essai sur
> téléphone consigné.

| Candidat | Chemin (`app/src/main/java/com/scamstop/app/`) | Intérêt | Ce qui manque |
|---|---|---|---|
| Rejet des appels hors contacts sans permission Contacts | `NonContactCallScreeningService.kt`, `ScreeningDecision.kt`, `RoleState.kt` (7 tests) | Rôle Android « identification de l'appelant » (`CallScreeningService`) : **sans** `READ_CONTACTS`, Android ne transmet au service que les appels de numéros inconnus ; le rejet renvoie l'appelant vers la messagerie | Licence GPL ; numéros masqués jamais transmis par Android ; renvoi vers la messagerie selon l'opérateur ; aucun usage réel |
| Annonce vocale exportée en `.wav` | `VoiceGreeting.kt` | `TextToSpeech.synthesizeToFile` avec la meilleure voix française hors ligne, copie dans Téléchargements par MediaStore | Pas de test ; licence GPL ; à rapprocher de la lecture à voix haute du Carnet de recette |

### Mihon-edited-, [JRRocks/Mihon-edited-](https://github.com/JRRocks/Mihon-edited-) (fork public de Mihon) @ `d001e6f`

> Le `main` public est identique à l'amont Mihon (derniers commits du 2026-10-20) : aucun code
> propre au fork n'y est publié. Le serveur `tools/colorizer-server` cité par la branche Library
> `ccr-be4abd50-9zo09e` (2026-10-21) **ne se trouve pas dans le dépôt**.

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Colorisation IA de manga | Modèle externe [qweasdd/manga-colorization-v2](https://github.com/qweasdd/manga-colorization-v2) @ `a0d0e44` (Python : `torch`, `torchvision`, `opencv-python`, poids à télécharger depuis Google Drive) ; serveur HTTP `GET /health`, `POST /colorize` annoncé dans `tools/colorizer-server` | Colorisation locale et gratuite de pages noir et blanc : environ 1 s par page sur GPU NVIDIA, 15 à 20 s sur CPU, ou Google Colab (chiffres de la note d'origine, non mesurés ici). Astuce notée : ne garder que les couleurs du modèle et les poser sous le trait d'origine (canal L en Lab) pour garder la page nette | Code du serveur introuvable sur GitHub. **Aucune licence** dans le dépôt du modèle : droits réservés par défaut, à vérifier avant toute redistribution. Pas de test ni de retour d'usage consigné |

### Carnet de recette, [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) @ `3d26111`

Chemins relatifs à `core/src/main/kotlin/fr/jrrocks/carnetderecette/core/` et
`app/src/main/kotlin/fr/jrrocks/carnetderecette/`.

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Lecture à voix haute par segments | `audio/{TexteOral,LecteurSegments,Minuteurs,Jetons}.kt` (cœur, testés) + `audio/{Voix,Sonnerie}.kt` (app) | Texte écrit pour l'oral sans regex (« ½ cc » → « une demi-cuillère à café », « 180 °C »), machine à états pure (valider, relire, précédent, pause, déclencheurs manuel / fin de l'étape d'avant / fin de minuteur), `TextToSpeech` français hors ligne avec repli et installation de voix | Essai sur appareil |
| Commandes vocales par phrases connues | `audio/ReconnaissanceCommandes.kt` (cœur, testé) + `audio/Ecoute.kt` (app) | `SpeechRecognizer` sur l'appareil (API 31+) ou service du téléphone, comparaison à une liste de phrases sans IA, garde-fou contre la conversation et les négations, micro coupé pendant que la voix parle | Essai sur appareil (bip de relance, hors ligne) |
| Lecture de pages publiques `__NEXT_DATA__` / JSON-LD | `hellofresh/{LecteurHelloFresh,TexteHtml,AdressesHelloFresh,Sonde}.kt` + `net/PagesHelloFresh.kt` | Lecture d'une page publique comme un navigateur, redirections vérifiées une à une, taille plafonnée, sonde hebdomadaire en CI qui ouvre une issue si le site change | Très lié au site lu ; à généraliser |
| Import d'icônes Material Symbols en drawables vectoriels | `outils/icones.py` (racine du dépôt, à `8dc4820`) | Une seule famille et une seule graisse pour toutes les icônes, variante pleine pour l'onglet actif, sans `material-icons-extended` | Pas de test, jamais vu sur appareil ; icônes sous Apache 2.0 (licence à garder) |
| Test de typographie française | test des espaces insécables de `DriveCarnetTest.kt` (`app/src/test/`, à `8dc4820`) | Interdit une espace ordinaire devant « : ; ! ? » et dans les guillemets des messages affichés : utile à toutes les apps en français | Trop petit pour un moteur : à verser dans `docs/ui-ux` ou dans une convention commune |

---

## 6. Dépôts passés en revue

Revue la plus récente par dépôt ; les précédentes sont dans le [`JOURNAL.md`](JOURNAL.md).

| Dépôt | Visibilité | Commit revu | Date | Résultat |
|---|---|---|---|---|
| [ScanList](https://github.com/JRRocks/ScanList) | privé | `2ef0b6d` (branche `claude/init-xyw7gc` ; `main` = `1368027`) | 2026-10-27 | Un seul commit de CI depuis la revue du 2026-10-12 ; `scan-multimode` identique |
| [SuivieMAT](https://github.com/JRRocks/SuivieMAT) | public (depuis le 2026-10-27) | `5eb5198` (branche `claude/remove-scanlist-history-a5bqzh`, pointe `65da616`) | 2026-10-27 | Fork de ScanList à `1368027` ; utilise `scan-multimode` à l'identique ; `RegexSuggester` ajouté au moteur ; 2 candidats |
| [Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie) | public | `6e0de47` | 2026-10-27 | Inchangé : dépôt de **versions** seulement (dernière balise `v13.125`), code dans Suivie-Popote-Cie |
| [Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) | privé | `6149a81` (pointe `93e5d5b` : code des moteurs inchangé, `version.yml` sur runner auto-hébergé) | 2026-10-27 | 9 commits métier ; les trois moteurs inchangés et sans les patchs Library du 2026-10-26 ; capteur de plantage → `rapport-plantage`, captures → `docs/methodes/` |
| [Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) | privé | `e5c94b4` (`main`) + `2cb08aa` (branche `meteogram-weather-icons`) | 2026-10-27 | `astro-soleil-lune` inchangé dans l'app ; deux défauts de la lune corrigés dans la Library ; 1 candidat de plus |
| [Sun-tracking](https://github.com/JRRocks/Sun-tracking) | privé | `2aec84a` | 2026-10-27 | Republication de l'état initial d'Android-Suntrack, avec le bug d'époque lunaire : rien à extraire |
| [Activity-track](https://github.com/JRRocks/Activity-track) | privé | `855773c` (branche `claude/garmin-llm-feedback-app-fnlhmp` ; `main` = `cdf458d`) | 2026-10-27 | Gemini remplacé par l'API Anthropic ; 4 candidats de plus |
| [ToolMap](https://github.com/JRRocks/ToolMap) | public (depuis le 2026-10-27, sans historique) | `eee8c6b` (ancienne branche `sauvegarde/p16-2026-10-16`) ; état public `a37dcab` | 2026-10-27 | Utilisé sur téléphone depuis le 2026-10-03 ; `rapport-plantage` extrait ; 8 candidats de plus |
| ToolMap-Archive | privé | — | — | Ancien dépôt ToolMap (historique complet, dont tous les SHA cités avant le 2026-10-27) ; non accessible depuis la session du 2026-10-27 |
| [InnovAdapt-CRM](https://github.com/JRRocks/InnovAdapt-CRM) | privé | `d2c2f6e` (branche `claude/android-rust-mobile`, descend de `main` = `42e9534`) | 2026-10-27 | Synchro chiffrée JS et Rust revue : 5 candidats de plus |
| [Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) | public | `3d26111` (session Carnet) ; `8dc4820` revu en détail le 2026-10-27, pointe `797fae7` | 2026-10-27 | Utilise `scan-multimode` (variante sans regex), `google-drive-appdata`, `maj-auto-github` ; fiches vérifiées fidèles ; 1 défaut de CI reporté dans `maj-auto-github` ; 5 candidats (non essayés sur appareil) |
| [Scam-Stop](https://github.com/JRRocks/Scam-Stop) | public | `b971f43` | 2026-10-27 | Nouveau (créé le 2026-10-26, dérivé d'un projet GPL-3.0) ; 2 candidats |
| [Mihon-edited-](https://github.com/JRRocks/Mihon-edited-) | public (fork) | `d001e6f` | 2026-10-27 | Amont Mihon sans code propre au fork ; 1 candidat externe (colorisation) |
| RGL13 | privé | — | — | **Pas encore revu** (accès non accordé à la session du 2026-10-27) |

---

## 7. Documentation de référence (hors moteurs)

| Document | Contenu | Statut |
|---|---|---|
| [`docs/ui-ux/README.md`](docs/ui-ux/README.md) | Dossier UI/UX : 41 chapitres (principes, heuristiques, lois de l'UX, couleur et contraste, typographie, composants, mobile et Android, accessibilité WCAG 2.2, éthique, glossaire, check-lists), 202 sources avec URL, images libres de droits | 🔬 Rédigé à partir de sources publiques, non éprouvé en réel ; volume réel annoncé dans le README (en deçà des 200 pages visées) |
| [`docs/methodes/captures-compose-robolectric.md`](docs/methodes/captures-compose-robolectric.md) | Écran Compose réel en PNG sans émulateur (Robolectric en rendu natif) : gabarit de test vérifié en session, variantes sombre, police, largeurs, paysage, pièges de stabilité | ✅ méthode de base (Popote, 2026-09-29) · 🟡 extensions (Carnet de recette, plusieurs tailles de Popote) |
| [`docs/methodes/android-en-session-cloud.md`](docs/methodes/android-en-session-cloud.md) | Compiler, tester et livrer une app Android depuis claude.ai/code : SDK, miroir Maven contre les 429, tests purs sans Gradle, Rust, Node, Python, APK de plus de 30 Mio, pièges de CI GitHub Actions, clones superficiels | ✅ compilation et tests en session, APK de CI installé sur téléphone · 🟡 livraison par `release/` ou pré-version |

Index des fiches méthode : [`docs/methodes/README.md`](docs/methodes/README.md).

Mots-clés : UI, UX, ergonomie, heuristiques de Nielsen, WCAG, contraste, Material Design, accessibilité,
glossaire UX, check-list de conception, navigation Android.
