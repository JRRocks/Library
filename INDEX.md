# Index de la Library

> Point d'entrée de recherche. Pour les règles et les procédures, voir [`AGENTS.md`](AGENTS.md).
> **Toute modification d'une fiche met à jour cet index** (AGENTS.md § 2, règle 5).
>
> Légende des statuts : ✅ Éprouvé · 🟡 Testé · ⚠️ À patcher · 🔬 Candidat · ⛔ Obsolète

Dernière mise à jour : 2026-10-26

---

## 1. Moteurs catalogués

| Identifiant | Ce que ça fait | Statut | Plateforme | Source | Utilisé dans |
|---|---|---|---|---|---|
| [`scan-multimode`](moteurs/scan-multimode/README.md) | Scan à la chaîne QR/2D, code-barres, OCR filtré par regex (formules proposées à partir d'un exemple), NFC/RFID, **validé par consensus de N lectures identiques** | ✅ OCR et code-barres · 🟡 2D étendu · 🟡 `RegexSuggester` · ⚠️ NFC (patch NDEF appliqué, non vérifié sur appareil) · ⚠️ lampe après l'arrière-plan (suspecté) | Android, CameraX, ML Kit | ScanList, SuivieMAT | ScanList, SuivieMAT, Carnet de recette |
| [`paiement-sumup`](moteurs/paiement-sumup/README.md) | Encaissement carte sur lecteur SumUp Bluetooth, connexion native du SDK, sans secret embarqué | 🟡 (connexion OK sur appareil, encaissement réel non encore validé) | Android, SDK SumUp 6.0.0 | Popote Compagnie | Popote Compagnie |
| [`google-drive-appdata`](moteurs/google-drive-appdata/README.md) | Autorisation Google (Identity `AuthorizationClient`) et fichiers dans le dossier caché `appDataFolder` du Drive, en REST pur ; variante dossier visible en `drive.file` | ✅ · ⚠️ lecture des 403 (patch proposé) · ⚠️ jeton révoqué en cache (suspecté) | Android, Play Services Auth | Popote Compagnie | Popote Compagnie, Carnet de recette |
| [`maj-auto-github`](moteurs/maj-auto-github/README.md) | L'APK se met à jour seul depuis les Releases d'un dépôt GitHub public, CI de publication signée incluse | ✅ · ⚠️ annulation avalée (patch proposé) | Android et GitHub Actions | Popote Compagnie | Popote Compagnie, Carnet de recette |
| [`astro-soleil-lune`](moteurs/astro-soleil-lune/README.md) | Lever, coucher et midi solaire, jour et nuit polaires, phase, illumination et lever de la lune, hors-ligne | 🟡 (deux défauts du calcul de la lune corrigés dans la Library le 2026-10-27 ; 13 tests verts) | Kotlin pur | Android-Suntrack | Sun Track |

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
| Se connecter avec un compte Google sans secret dans l'APK | `google-drive-appdata` |
| Sauvegarder des données de l'app sur le Google Drive de l'utilisateur | `google-drive-appdata` |
| Déposer sur le Drive des fichiers qu'un autre outil (Claude Cowork, l'utilisateur) doit lire | `google-drive-appdata` (variante dossier visible en `drive.file`) |
| Distribuer et mettre à jour une app sans Play Store | `maj-auto-github` |
| Publier les APK d'une app dont le code est déjà public, sans second dépôt ni jeton | `maj-auto-github` (variante « dépôt de code public ») |
| Publier un APK signé depuis GitHub Actions | `maj-auto-github` (`src/version.yml`) |
| Connaître l'heure du lever ou du coucher du soleil, ou la phase de la lune | `astro-soleil-lune` |
| Lire un fichier Excel `.xlsx` sans bibliothèque | 🔬 candidat InnovAdapt (§ 5) ou Popote `ImportBase` |
| Lire ou écrire du GPX ou du KML, convertir UTM, MGRS ou Lambert 93 | 🔬 candidats ToolMap (§ 5) |
| Appeler un LLM avec une sortie JSON structurée | 🔬 candidats Activity-track (§ 5) |
| Récupérer un rapport de plantage sans `adb logcat` | 🔬 candidat Popote, capteur de plantage (§ 5) |
| Coloriser des pages de manga en noir et blanc | 🔬 candidat Mihon-edited-, modèle externe (§ 5) |
| Lire des étapes à voix haute, commander à la voix sans IA | 🔬 candidats Carnet de recette (§ 5) |

---

## 3. Mots-clés

| Mot-clé | Moteurs |
|---|---|
| Affiliate Key | `paiement-sumup` |
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
| SumUp, `SumUpAPI`, `SumUpState.init`, `play-services-location` | `paiement-sumup` |
| `versionCode`, mise à jour en ligne | `maj-auto-github` |
| WorkManager, échec définitif ou transitoire | `google-drive-appdata` |

---

## 4. Par plateforme

| Plateforme | Moteurs |
|---|---|
| **Kotlin pur** (réutilisable hors Android) | `astro-soleil-lune` ; dans `scan-multimode` : `ScanConsensus`, `ScanDebouncer`, `ScanValue`, `RegexRuleEngine`, `RegexSuggester`, `NfcValue` |
| **Android natif** | `scan-multimode`, `paiement-sumup`, `google-drive-appdata`, `maj-auto-github` |
| **CI GitHub Actions** | `maj-auto-github` |
| **Python** | aucun moteur pour l'instant (candidats Activity-track) |
| **JS navigateur** | aucun moteur pour l'instant (candidats InnovAdapt) |

---

## 5. Candidats repérés (🔬 à évaluer puis extraire selon AGENTS.md § 5.D)

### Popote Compagnie, code dans [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) @ `08c211a`

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Capteur de plantage | `app-terrain/.../PopoteApplication.kt` (`setDefaultUncaughtExceptionHandler` → fichier), `MainActivity.kt` (`EcranPlantage`) | Rapport de plantage lisible au lancement suivant, texte sélectionnable, sans adb. **A servi en réel** pour diagnostiquer les plantages SumUp | Extraire du code Popote |
| Grand-livre chaîné par hash et audit | `core/.../ledger/{Chaine,Empreinte,Audit}.kt` + `db/Triggers.kt` | Écritures non retouchables, vérification d'intégrité | Très lié au schéma Popote |
| Sauvegarde récupérable par trousseau asymétrique | `core/.../sauvegarde/{Trousseau,Enveloppe,Coffre,Contenu}.kt` | L'app chiffre sans pouvoir déchiffrer seule ; restauration avec le mot de passe admin | Fiche + vérification des tests de restauration réels (`docs/tests-restauration.md`) |
| Base chiffrée SQLCipher + Keystore | `core/.../db/{SqlCipherUtilitaires,ClefBase}.kt`, `administration/CoffreLocal.kt` (AES-256-GCM sous Keystore) | Chiffrement au repos de Room | Dépend de Room ; lire `docs/debogage-sqlcipher.md` |
| Sauvegarde continue « socle + points » | `core/.../sauvegarde/{SauvegardeContinue,Point,TravailSauvegarde}.kt` | Sauvegarde incrémentale vers Drive après 30 s de calme, rotation | S'appuie sur `google-drive-appdata` |
| Lecture de ticket de caisse | `core/.../ticket/{LectureTicket,Classement,Reconstitution,Releve}.kt` | OCR ML Kit (Play Services) sur PDF ou photo, reconnaissance de **formes** (libellé + montant), mémoire des classements | « Reste à éprouver » sur téléphone selon le dépôt |
| Rendu de monnaie | `core/.../model/Rendu.kt` | Coupures issues de la caisse réelle | Petit, isolable |
| Signalement de bug | `app-terrain/.../signalement/Signalement.kt` | Bouton « Signaler un problème » qui ouvre une issue GitHub via un jeton fine-grained limité à `Issues` sur le dépôt public | Jeton à ne pas copier |
| Lecture UID de carte NFC | `core/.../nfc/{UidCims,SignatureCims}.kt`, `app-terrain/.../nfc/LecteurCims.kt` | Identification par carte NFC | À rapprocher de `scan-multimode` (NFC) |
| Captures d'écran Compose sans émulateur | `app-terrain/src/test/.../capture/*` (Robolectric → PNG) | Voir un écran depuis une session cloud, méthode validée (`docs/decisions.md` § « Rendu visuel ») | Méthode à fiche, pas un moteur runtime |
| Import `.xlsx` | `core/.../import/ImportBase.kt` (+ `org/kxml2/io/KXmlParser.java`) | Import d'un tableur réel | Lié au modèle Popote |

### ScanList, [JRRocks/ScanList](https://github.com/JRRocks/ScanList) @ `1368027`

Chemins relatifs à `app/src/main/java/com/scanlist/app/`.

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Codec CSV | `data/csv/CsvCodec.kt` (+ test) | BOM UTF-8, `;`, CRLF : ouverture propre dans Excel FR | Fiche |
| Moteur de comparaison à une référence | `domain/compare/CompareEngine.kt` (+ test) | Liste de référence rouge, passée au vert à la lecture (inventaire, contrôle) | Fiche |
| Export par mail | `export/{ExportManager,ExportFileName}.kt` | Écriture en cache, FileProvider, `ACTION_SEND`, purge à 24 h, nom de fichier assaini | Fiche |
| Recherche insensible à la casse et aux accents | `domain/search/FolderSearch.kt` (+ test) | Petit, pur | Fiche |

### Android-Suntrack, [JRRocks/Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) @ `e5c94b4`

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Météo Open-Meteo | `data/WeatherRepository.kt`, `WeatherCodes.kt` | Météo actuelle **sans clé d'API ni compte** | Pas de test |
| Localisation sans Play Services | `location/LocationResolver.kt` | Dernière position connue pour le fond, position fraîche à la demande, mise en cache | Pas de test |
| Rafraîchissement de widget à la minute | `work/{TickScheduler,RefreshWorker,BootReceiver}.kt` | Alarme RTC inexacte (sans `SCHEDULE_EXACT_ALARM`, sans réveil), WorkManager 30 min, redémarrage au boot | Pas de test |
| Palette de ciel | `render/SkyPalette.kt` (+ test) | Couleurs selon la hauteur du soleil | Spécifique au rendu |

### Activity-track, [JRRocks/Activity-track](https://github.com/JRRocks/Activity-track) @ `cdf458d`

| Candidat | Chemin | Intérêt | Ce qui manque |
|---|---|---|---|
| Backends LLM interchangeables | `app/llm/backends/{base,gemini,claude_code}.py`, `android/.../llm/GeminiClient.kt` | Même requête vers l'API Gemini (sortie JSON Schema, budget de réflexion) ou vers la CLI Claude Code (`--restricted`, `--strict-mcp-config`, dossier vide, `--max-budget-usd`) | Tests pytest présents (84 tests au total dans le dépôt) ; pas de retour d'usage consigné |
| Brief LLM sous budget de tokens | `app/analytics/brief.py` | Agrégats plutôt que lignes brutes, dégradation par paliers consignée | Spécifique au sport, mais le patron est générique |
| Fusion multi-source sans doublon | `app/ingest/merge.py` | Identité par id externe puis par proximité temporelle ; **jamais écraser une valeur par null** | Fiche |
| Lecteur d'export de compte Garmin | `app/ingest/garmin_zip.py` | ZIP de ZIP, ~80 JSON, conversion d'unités | Spécifique à Garmin |
| Dépôt automatique Drive → app sans projet Cloud | README § Phase 2 (Google Apps Script) | Pousse les nouveaux fichiers d'un dossier Drive toutes les heures | À documenter |

### ToolMap, [JRRocks/ToolMap](https://github.com/JRRocks/ToolMap) @ `06c80c7`

> 919 tests JVM, mais **rien n'a jamais tourné sur un téléphone** (README). Le module `:core`
> est en Kotlin pur, donc très facile à réutiliser.

| Candidat | Chemin (`core/src/main/kotlin/fr/toolmap/core/`) | Intérêt |
|---|---|---|
| Systèmes de coordonnées | `geo/{Utm,Mgrs,Lambert93,CoordinateFormat,CoordinateParser,GridReference}.kt` | DD, DMS, UTM, MGRS, Lambert 93 : formatage et analyse |
| Traces GPX, KML et GeoJSON | `track/{GpxReader,GpxWriter,KmlReader,GeoJsonReader,TrackStats,ElevationProfile,PathSimplifier,Geodesy}.kt` | Lecture, écriture, statistiques, simplification |
| Tuiles et MBTiles hors-ligne | `tiles/{TileMath,TilePlanner,TileUrlBuilder}.kt`, `data/.../tiles/{MbTilesStore,TileInterceptor}.kt` | Cache de cartes via un intercepteur OkHttp |
| Format AQX (AlpineQuest) | `aqx/{AqxParser,AqxWriter}.kt` | Import et export de sources de cartes |
| Altimètre barométrique auto-calibré | `altitude/{BarometricAltimeter,Atmosphere,AltitudeSelector}.kt` | Fusion baromètre, relief et GPS |
| Écrivain PDF minimal | `pdf/{PdfWriter,HelveticaMetrics}.kt` | PDF sans bibliothèque (export à l'échelle) |
| Partage P2P chiffré | `p2p/{Pairing,GroupCrypto,Base45,MeshNode,Wire}.kt`, module `:p2p` (WebRTC, QR) | Position partagée sans serveur, appairage par QR |
| XML sûr | `xml/SafeSax.kt` | Analyse SAX protégée (XXE) |
| Capacités WMS/WMTS | `wizard/{WmsCapabilities,WmtsCapabilities,ApiKeyExtractor}.kt` | Ajout guidé d'une source de carte |
| Sauvegarde de base Room à chaud | `app/.../backup/RoomDatabaseFreezer.kt` | Copie cohérente pendant l'usage |

### InnovAdapt-CRM, [JRRocks/InnovAdapt-CRM](https://github.com/JRRocks/InnovAdapt-CRM) @ `42e9534`

| Candidat | Chemin (`innovadapt-crm-simple.html`) | Intérêt | Ce qui manque |
|---|---|---|---|
| Lecteur `.xlsx` sans bibliothèque | `readZipCentralDirectory`, `extractZipEntry`, `parseXlsxToRows`, `excelSerialToIso` (~l. 1570-1680) | ZIP lu à la main + `DecompressionStream('deflate-raw')` + `DOMParser` ; première feuille seulement | Pas de test ; ZIP64 non géré |
| Analyseur CSV à séparateur auto | `parseCSV` (~l. 1544) | `;` ou `,` détecté, guillemets, BOM | Pas de test |
| Export CSV pour Excel FR | `downloadCSV` (~l. 1960) | BOM, `;`, repli sur la capacité de téléchargement des Artifacts | — |
| Normalisation de dates d'import | `normalizeImportDate` | ISO, JJ/MM/AAAA, numéro de série Excel | — |
| Synchro Google Drive en Rust (mobile) | branche `claude/android-rust-mobile` (non fusionnée) | Connexion Google en un bouton | Non fusionné, à revoir plus tard |

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

---

## 6. Dépôts passés en revue

| Dépôt | Visibilité | Commit revu | Date | Résultat |
|---|---|---|---|---|
| [ScanList](https://github.com/JRRocks/ScanList) | privé | `1368027` | 2026-10-12 | `scan-multimode` + 4 candidats |
| [Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie) | public | `6e0de47` | 2026-10-12 | Dépôt de **versions** seulement (APK), pas de code : voir Suivie-Popote-Cie |
| [Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) | privé | `08c211a` | 2026-10-12 | `paiement-sumup`, `google-drive-appdata`, `maj-auto-github` + 11 candidats |
| [Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) | privé | `e5c94b4` | 2026-10-12 | `astro-soleil-lune` + 4 candidats |
| [Activity-track](https://github.com/JRRocks/Activity-track) | privé | `cdf458d` | 2026-10-12 | 5 candidats |
| [ToolMap](https://github.com/JRRocks/ToolMap) | privé | `06c80c7` | 2026-10-12 | 10 candidats (jamais testés sur appareil) |
| [InnovAdapt-CRM](https://github.com/JRRocks/InnovAdapt-CRM) | privé | `42e9534` | 2026-10-12 | 5 candidats |
| [Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) | public | `3d26111` | 2026-10-27 | Utilise `scan-multimode` (variante sans regex), `google-drive-appdata`, `maj-auto-github` ; 3 candidats (non essayés sur appareil) |
| SuivieMAT, RGL13, Sun-tracking | privés | — | — | **Pas encore revus** |

---

## 7. Documentation de référence (hors moteurs)

| Document | Contenu | Statut |
|---|---|---|
| [`docs/ui-ux/README.md`](docs/ui-ux/README.md) | Dossier UI/UX : 41 chapitres (principes, heuristiques, lois de l'UX, couleur et contraste, typographie, composants, mobile et Android, accessibilité WCAG 2.2, éthique, glossaire, check-lists), 202 sources avec URL, images libres de droits | 🔬 Rédigé à partir de sources publiques, non éprouvé en réel ; volume réel annoncé dans le README (en deçà des 200 pages visées) |

Mots-clés : UI, UX, ergonomie, heuristiques de Nielsen, WCAG, contraste, Material Design, accessibilité,
glossaire UX, check-list de conception, navigation Android.
