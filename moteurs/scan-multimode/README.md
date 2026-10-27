# Scan multimode : QR/2D, code-barres, OCR+regex, NFC/RFID

| Champ | Valeur |
|---|---|
| **Identifiant** | `scan-multimode` |
| **Statut global** | ✅ **Éprouvé** pour l'OCR et le code-barres (déclaré éprouvé par l'utilisateur après des tests en réel). 🟡 pour l'élargissement 2D et le générateur de formules `RegexSuggester`. ⚠️ pour le NFC : patch NDEF appliqué ici le 2026-10-27, **non vérifié sur appareil**. Détail dans le tableau des composants |
| **Plateforme** | Android natif, Kotlin, minSdk 26 |
| **Dépendances** | CameraX 1.3.4, ML Kit `barcode-scanning` 17.3.0 et `text-recognition` 16.0.1 (modèles **embarqués**, donc hors-ligne), `android.nfc` |
| **Provenance** | [JRRocks/ScanList](https://github.com/JRRocks/ScanList) (privé), commit `1368027` (2026-09-22), pour tout `src/` et `tests/` sauf `RegexSuggester`. `RegexSuggester.kt` et son test : [JRRocks/SuivieMAT](https://github.com/JRRocks/SuivieMAT) (public depuis le 2026-10-27), commit d'origine `5c81d3c` (2026-10-26), identiques à la pointe revue `5eb5198` (2026-10-27). `NfcScanSurface.kt` est **patché dans la Library** (2026-10-27, voir les points de vigilance) |
| **Utilisé dans** | ScanList · SuivieMAT (fork de ScanList pris à `1368027`, moteur identique jusqu'à `5eb5198` hormis `NfcScanSurface` habillé au thème ; scan jamais vérifié sur téléphone) · Carnet de recette (intégré le 2026-10-27, OCR seul et **sans regex** : voir la variante, pas encore essayé sur appareil) |
| **Fichiers d'origine** | `app/src/main/java/com/scanlist/app/scanner/*`, `domain/{model/ScanValue,nfc/NfcValue,regex/RegexRuleEngine}.kt`, `ui/scan/{CameraPreview,NfcScanSurface,ScanViewModel}.kt` ; SuivieMAT : `domain/regex/RegexSuggester.kt` ; tests : `app/src/test/java/com/scanlist/app/*Test.kt` |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

On scanne **à la chaîne** des objets physiques (inventaire, contrôle de matériel) avec un
seul téléphone tenu d'une main. Il y a quatre modes **mutuellement exclusifs** :

| Mode | Lit | Moteur |
|---|---|---|
| Code 2D | QR Code, PDF417, Data Matrix, Aztec | ML Kit Barcode, filtré **au décodeur** |
| Code-barres | EAN-13/8, UPC-A/E, Code 39/93/128, ITF, Codabar | ML Kit Barcode, filtré **au décodeur** |
| OCR | Texte imprimé, filtré par une **formule regex** écrite à la volée ou proposée à partir d'un exemple (`RegexSuggester`) | ML Kit Text Recognition (latin) |
| NFC/RFID | Contenu NDEF texte, sinon l'UID matériel en hexadécimal | `NfcAdapter.enableReaderMode` |

## Le problème réglé sur le terrain : la validation par consensus

**Constat en réel :** l'optique se trompe (angle, reflet, étiquette abîmée). Avec une seule
lecture, l'OCR enregistrait des valeurs fausses. En code-barres, Code 39 et ITF n'ont pas de
somme de contrôle, donc une lecture fausse mais bien formée passait aussi.

**Correctif, `ScanConsensus` :** une valeur n'est enregistrée qu'après avoir été lue **à
l'identique N fois** :

- **3 fois** en code-barres et en Code 2D (environ 100 ms de visée) ;
- **5 fois** en OCR (le bruit par image est plus fort et une lecture coûte peu) ;
- une image **sans** lecture ne remet pas le compteur à zéro (une main qui tremble ne doit
  pas repousser la validation). Une valeur **différente** qui s'intercale, elle, casse la série ;
- un compteur abandonné expire au bout de **800 ms** (`ttlMs`) ;
- une valeur validée est **purgée** aussitôt : elle peut être revalidée si l'objet repasse.

**Garde d'ambiguïté (option `ambiguityGuard`) :** la regex ne voit pas les confusions à
l'intérieur d'une même classe de caractères (`8`↔`6`, `C`↔`G`). Si une valeur nouvelle est
**à un caractère près** (distance d'édition 1) d'une valeur en cours de comptage, et que les
deux n'ont **jamais été vues dans la même image**, les deux compteurs repartent à zéro. La
condition « jamais vues ensemble » est essentielle : deux numéros de série consécutifs lus
dans la même image (un rack) sont deux vrais objets. La garde est désactivée en Code 2D,
puisque la correction d'erreur Reed-Solomon rend une mauvaise lecture quasi impossible.

## Pipeline (ordre impératif)

```
image caméra ─► décodeur filtré par mode ─► filtre regex de conformité (optionnel)
             ─► ScanConsensus (N lectures identiques) ─► ScanDebouncer (verrou 1,5 s)
             ─► dédoublonnage en base (clé ScanValue.key) ─► ScanFeedback (bip + vibration)
```

> ⚠️ **Le debouncer passe APRÈS le consensus.** Placé avant, il écarterait les lectures 2
> et 3 avant que le consensus ne les compte. Il ne sert plus de pré-filtre anti-rafale : c'est
> un verrou anti-rebond posé après la validation.

Le NFC n'a pas de bruit optique : il passe par le debouncer seul, sans consensus.

## Générateur de formules à partir d'exemples (`RegexSuggester`)

Écrire une regex est l'obstacle principal du mode OCR et du filtre de conformité des
codes. Avec `RegexSuggester`, l'utilisateur tape une valeur telle qu'elle est imprimée
(`1GH546`) et reçoit plusieurs formules, de la plus stricte à la plus large. Le moteur
vient de SuivieMAT (`5c81d3c`, décision 25). Il est en Kotlin pur et ne dépend que de
`RegexRuleEngine` et `ScanValue`.

**Principe :**

1. Les exemples sont séparés par `,`, `;` ou un retour à la ligne. L'espace n'est pas un
   séparateur : il peut faire partie de la valeur (`12 123456`). Chaque exemple passe par
   `ScanValue.normalize`, comme à l'enregistrement.
2. Chaque exemple est découpé en **blocs** : chiffres, majuscules, minuscules, lettres
   mêlées, caractères fixes. Les caractères fixes sont recopiés et échappés.
3. Plusieurs exemples sont fusionnés en un seul format. Seules la longueur d'un bloc et la
   casse de ses lettres peuvent varier : des longueurs différentes donnent une **plage**
   (`1GH546, 1GH5467` → `\d{3,4}`). Un nombre de blocs différent ou un caractère fixe
   différent donne `Result.Incompatible`, sans proposition (`12-123456, 12 123456` est
   incompatible : la variante « séparateurs » couvre les deux).
4. Jusqu'à **cinq formules**, dans cet ordre (exemple `1GH546`) :

   | `Kind` | Formule | Proposée quand |
   |---|---|---|
   | `STRICT` | `\b\d[A-Z]{2}\d{3}\b` | toujours, en premier |
   | `ANY_CASE` | `\b\d[A-Za-z]{2}\d{3}\b` | un bloc est tout en majuscules ou tout en minuscules |
   | `SEPARATORS` | `\b\d[ -]?[A-Z]{2}[ -]?\d{3}\b` | le format a une jonction : chiffres collés à des lettres, ou un séparateur seul entre deux blocs (sinon elle se confond avec la stricte et disparaît) ; espace et tiret tolérés, plus les séparateurs des exemples parmi ` ./_-` |
   | `FIXED_PREFIX` | `\b1GH\d{3}\b` | le début (tout sauf le dernier bloc) est le même dans tous les exemples |
   | `FLEXIBLE` | `\b\d+[A-Z]+\d+\b` | au moins un bloc de chiffres ou de lettres |

5. `\b` n'est posé que du côté où le format commence ou finit par un chiffre ou une
   lettre : `#123` donne `#\d{3}\b`. Un `\b#` exigerait un caractère de mot avant le `#`
   et ne reconnaîtrait plus `#123` en début de texte. En OCR, `\b` évite d'extraire un
   morceau d'un mot plus long.
6. **Chaque proposition est vérifiée par `RegexRuleEngine`** : elle doit compiler
   (`compileOrNull`) et reconnaître chaque exemple (`conforms`), sinon elle est retirée.
   Les doublons sont retirés. Les valeurs d'illustration (`alsoAccepts`, « Accepte
   aussi », et `rejects`, « Refuse ») sont construites à la main puis vérifiées contre la
   formule et contre la formule stricte. Une illustration fausse n'est pas montrée.
7. Aucun texte affichable dans le moteur : il renvoie `Result.Empty`,
   `Result.Incompatible` ou `Result.Found(blocks, suggestions)`. L'interface fait les
   phrases.

**Pourquoi pas d'IA embarquée** (décision 25 de SuivieMAT, 2026-10-26) : Gemini Nano
(ML Kit Prompt API) ne citait pas le Galaxy S25 parmi les appareils compatibles (liste
consultée le 2026-10-26), et un modèle embarqué (Gemma 3 1B, environ 500 Mo) aurait
multiplié la taille de l'APK pour un résultat peu fiable sur les quantificateurs. Un
générateur déterministe suffit à un marquage régulier, et chaque formule est vérifiée.
La saisie en phrase (« 1 chiffre, 2 lettres, 3 chiffres ») n'a pas été retenue.

**Interface facultative, non copiée** : SuivieMAT a une feuille Compose
`ui/scan/RegexGeneratorSheet.kt` (`ModalBottomSheet`) et les chaînes `regex_generator_*`
de `res/values/strings.xml` (titres et commentaires des cinq formules, pluriels qui
décrivent les blocs). Elles dépendent du thème de l'app. Le bouton est placé à côté du
champ de formule, en OCR comme dans le filtre de conformité. La formule choisie suit le
chemin d'une formule tapée à la main : en OCR, elle **désarme** la capture.

**Statut 🟡** : 27 tests JVM, verts ici. Aucun usage réel consigné : SuivieMAT note
« Générateur de formules à l'écran ⬜ Non vérifié » (`consignes/outils.md` § 8).

## Composants

| Fichier (`src/`) | Rôle | Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| `ScanConsensus.kt` | N lectures identiques, TTL, garde d'ambiguïté | Non, Kotlin pur | `ScanConsensusTest` | ✅ |
| `ScanDebouncer.kt` | Verrou anti-rebond de 1,5 s par clé, purge auto | Non | `ScanDebouncerTest` | ✅ |
| `ScanValue.kt` | Normalisation partagée : `normalize` (trim, espaces internes réduits à un seul, **casse conservée**) et `key` (la même valeur passée en majuscules, sauf `caseSensitive`). **Une seule** règle pour le dédoublonnage et la comparaison | Non | via les autres tests | ✅ |
| `RegexRuleEngine.kt` | `extract` (OCR : isole les fragments, 50 max par image), `conforms` (codes : correspondance **totale**), `validate` (messages d'erreur traduits en français), comptage des groupes capturants | Non | `RegexRuleEngineTest` | ✅ · messages d'erreur à vérifier sur appareil (ICU, voir les points de vigilance) |
| `RegexSuggester.kt` | Formules regex proposées à partir d'exemples, cinq variantes de la plus stricte à la plus large, chacune vérifiée par `RegexRuleEngine` (vient de SuivieMAT `5c81d3c`) | Non, Kotlin pur | `RegexSuggesterTest` | 🟡 aucun usage réel consigné |
| `BarcodeFormats.kt` | Masques de formats 1D et 2D **disjoints**, noms de symbologie | Constantes ML Kit | `BarcodeFormatsTest` | ✅ 1D · 🟡 PDF417, Data Matrix, Aztec pas encore vus sur un vrai code |
| `BarcodeAnalyzer.kt` | `ImageAnalysis.Analyzer` ML Kit, filtrage dans les **options** du décodeur | Oui | non (pas testable hors appareil) | ✅ |
| `OcrAnalyzer.kt` | `ImageAnalysis.Analyzer` qui remonte le texte brut ; la regex est appliquée par l'appelant | Oui | non | ✅ |
| `CameraPreview.kt` | Composable CameraX : 720p (1D, OCR) ou 1080p (2D), `STRATEGY_KEEP_ONLY_LATEST`, torche, réticule qui se remplit selon l'avancement du consensus | Oui | non | ✅ · ⚠️ lampe après l'arrière-plan (suspecté) |
| `ScanFeedback.kt` | Trois signatures sonores et haptiques : succès, doublon, hors liste | Oui | non | ✅ |
| `NfcValue.kt` | Choix NDEF/UID, décodage RTD_TEXT (UTF-8/UTF-16) | Non | `NfcValueTest` | 🟡 |
| `NfcTagDecoder.kt` | Pont `Tag` Android vers `NfcValue` ; lit `cachedNdefMessage`, rempli par la sonde NDEF du système | Oui | non | ⚠️ voir les points de vigilance |
| `NfcScanSurface.kt` | Composable qui arme et désarme `enableReaderMode` sur ON_RESUME/ON_PAUSE ; repli si pas de puce ou NFC coupé. **Patché dans la Library** : sans `FLAG_READER_SKIP_NDEF_CHECK` | Oui | non | ⚠️ patch appliqué, non vérifié sur appareil |

Les packages d'origine (`com.scanlist.app.*`) sont conservés : **renommez-les** à
l'intégration. Tous les fichiers sont ceux de ScanList `1368027`, sauf `RegexSuggester.kt`
et `RegexSuggesterTest.kt` (SuivieMAT `5c81d3c`, copiés sans modification) et
`NfcScanSurface.kt` (patch de la Library du 2026-10-27, absent de ScanList et de
SuivieMAT).

## Variante : OCR par éléments dans le réticule, sans regex (Carnet de recette)

Le Carnet lit l'étiquette d'une box de repas : un grand numéro de recette (« 207 »), le
nombre de personnes et du bruit (dates, poids, lot). Le format de l'étiquette peut changer :
**aucune regex**. On compare ce qui est lu à des **valeurs connues** (les numéros du menu de
la semaine), et le grand numéro se reconnaît à sa **taille**. D'où ces adaptations :

1. **`OcrAnalyzer` → `OcrElements`** : pour chaque image, il remonte les éléments ML Kit
   (un mot, en général) dont le **centre est dans le réticule**, avec leur hauteur et leur
   position rapportées au réticule, et plus le texte brut. Le réticule est une zone fixe en
   fractions de l'aperçu (10–90 % en largeur, 22–78 % en hauteur), la même que celle
   dessinée ; elle est projetée dans l'image droite via `ImageProxy.cropRect` et la rotation
   (largeur et hauteur inversées à 90° et 270°). `ImageProxy` toujours fermée dans un
   `finally` ; sur erreur, rien.
2. **Résultat attendu sur le fil d'analyse** : `Tasks.await(recognizer.process(...))`
   remplace les écouteurs, et le consensus est appelé depuis ce fil. L'image qui vient de
   valider est encore ouverte : la petite photo de l'étiquette en est tirée (recadrée au
   réticule, 640 px de large au plus, JPEG 70).
3. **`CameraPreview`** sans `ScanMode` ni réticule intégré (le réticule est dessiné à part,
   coins blancs sur liseré sombre qui grandissent avec la progression). Avec un analyseur,
   un `ViewPort` à la taille de l'aperçu est posé (`UseCaseGroup`) pour que `cropRect`
   corresponde à ce qui est montré. Un `ImageCapture` peut être lié en plus (photo de fiche
   en pleine résolution). Les erreurs s'affichent en phrase française fixe, jamais
   `e.message`.
4. **`ScanFeedback`** : constructeur simple avec `Context`, sans Hilt ; `outOfList()` retiré.
5. **`ScanValue.normalize`** réécrit caractère par caractère, sans regex : `isWhitespace`
   traite aussi l'espace insécable et les espaces Unicode, que `\s` ignorait.
6. **Consensus sur le numéro reconnu**, pas sur le texte lu : la même étiquette donne des
   textes différents d'une image à l'autre, le numéro reste stable. Seuil 5, TTL 800 ms,
   garde d'ambiguïté et anti-rebond 1,5 s inchangés. Le nombre de personnes est **voté**
   image par image pendant le consensus ; votes partagés ou absents → « à confirmer ».
7. `RegexRuleEngine`, `BarcodeAnalyzer`, `BarcodeFormats` et le NFC ne sont pas repris.
8. **Anti-rebond glissant** (`SuiviEtiquettes`, à la place du verrou à durée fixe de
   `ScanDebouncer`) : un numéro validé reste verrouillé tant qu'il est relu à moins de 1,5 s
   d'intervalle ; le verrou ne tombe qu'après 1,5 s sans le lire. Avec le verrou fixe, une
   étiquette gardée en vue (on corrige le nombre de personnes, téléphone toujours pointé) était
   revalidée toutes les 1,5 à 2,5 s : « déjà scannée » et double bip en boucle. Un numéro à un
   chiffre près d'un numéro verrouillé est écarté pendant ce temps : le numéro verrouillé
   n'étant plus compté, la garde d'ambiguïté du consensus ne le voyait plus (« 201 » mal lu
   sur l'étiquette « 207 » finissait par se valider). Une valeur retirée par l'utilisateur
   reçoit le même verrou (`ignorer`) : elle ne revient pas tant qu'elle reste dans le champ.
9. **Numéro nettement le plus grand** : un numéro du menu n'est retenu que si aucun autre
   élément du réticule n'atteint 90 % de sa hauteur (sinon un petit numéro imprimé ailleurs
   sur l'étiquette passait quand le grand était mal lu).
10. **`CameraPreview`** : la taille de l'aperçu n'est une clé de liaison qu'avec un analyseur
    (sinon le clavier qui s'anime relie la caméra à chaque image) ; la rotation cible de
    l'`ImageCapture` suit l'orientation physique du téléphone (`OrientationEventListener`,
    table de la doc CameraX) ; la lampe est réappliquée après chaque liaison et à `ON_RESUME`
    (voir les points de vigilance).

Code de référence : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette)
(public), `app/src/main/kotlin/fr/jrrocks/carnetderecette/scan/` (`OcrElements`,
`GeometrieImage`, `Reticule`, `CameraPreview`, `ScanFeedback`) et
`core/src/main/kotlin/fr/jrrocks/carnetderecette/core/scan/` (`ReconnaissanceEtiquette`,
`SuiviEtiquettes`, copies de `ScanConsensus`, `ScanDebouncer`, `ScanValue`), avec leurs tests
(consensus et anti-rebond repris, géométrie de l'image en JVM).

## Intégration dans une nouvelle app

1. **Dépendances** (`libs.versions.toml`) : `androidx.camera:camera-{core,camera2,lifecycle,view}`
   en 1.3.4, `com.google.mlkit:barcode-scanning:17.3.0`, `com.google.mlkit:text-recognition:16.0.1`.
   Prenez la variante **embarquée** de ML Kit (`com.google.mlkit:*`), pas
   `play-services-mlkit-*`, si l'app doit marcher hors-ligne dès l'installation.
2. **Manifeste** : `CAMERA`, `VIBRATE`, `NFC` ; `<uses-feature android:name="android.hardware.nfc" android:required="false"/>`.
3. Copiez `src/` (et `tests/`), puis renommez les packages. `ScanMode` vient de
   `domain/model/Enums.kt` dans ScanList : c'est un simple `enum { OCR, QR_CODE, BARCODE, NFC }`.
   `RegexSuggester.kt` est facultatif (il demande `RegexRuleEngine` et `ScanValue`). Les
   tests purs se lancent sans Android : `RegexSuggesterTest`, `RegexRuleEngineTest`,
   `ScanConsensusTest`, `ScanDebouncerTest`, `NfcValueTest` (`BarcodeFormatsTest` demande
   les constantes ML Kit).
4. **ViewModel** (modèle : `ScanViewModel` de ScanList) :
   ```kotlin
   private val consensus = ScanConsensus(
       threshold = if (mode == ScanMode.OCR) 5 else 3,
       ambiguityGuard = mode != ScanMode.QR_CODE,
   )
   private val debouncer = ScanDebouncer()
   @Volatile private var compiledRegex: Regex? = null   // recompilée à chaque frappe, pas à chaque image

   private fun onCodesDetected(codes: List<DetectedCode>) {        // thread d'analyse CameraX
       val conforming = codes.filter { RegexRuleEngine.conforms(it.value, compiledRegex) }
       val confirmed = consensus.submit(conforming.map { ConsensusCandidate(it.value, it.symbology) })
       progress.value = consensus.peakProgress()                   // remplissage du réticule
       val fresh = confirmed.filter { debouncer.accept(it.value) }
       if (fresh.isNotEmpty()) viewModelScope.launch { enregistrer(fresh) }
   }
   ```
   En OCR, remplacez `conforms` par `RegexRuleEngine.extract(texte, regex, groupIndex)`.
5. **Créez un seul analyseur pour toute la durée de l'écran** : recréer un client ML Kit à
   chaque recomposition recharge le modèle. Appelez `close()` à la sortie.
6. **OCR : armez la capture explicitement.** Tant que la formule est en cours de frappe,
   rien n'est écrit. Toute modification de la formule **désarme** la capture. Sans ça, une
   formule incomplète comme `\d` enregistre des dizaines de valeurs parasites. Deux
   options pour la capture désarmée :
   - **aperçu** (ScanList) : l'OCR tourne, le texte lu et les correspondances s'affichent
     en direct. On vérifie la formule sur l'étiquette avant d'armer ;
   - **économie** (SuivieMAT, décision 26, `3f0126f`) : l'aperçu est supprimé et
     `onTextRecognized` sort tout de suite (`if (!capturingState.value) return`), donc
     aucune extraction regex par image. Plus tard, `1fc037b` va plus loin : `OcrAnalyzer`
     referme l'image sans la lire tant que la capture est désarmée (paramètre `isActive`),
     pour la chauffe et la batterie. Ce changement est postérieur à la revue et non
     vérifié sur téléphone ; il n'est pas repris dans `src/`. Contrepartie : la formule ne
     se vérifie plus sur l'étiquette avant d'armer. Le générateur (étape 7) compense en
     partie, en vérifiant chaque proposition contre l'exemple saisi.
7. **Facultatif : générateur de formules.** Appelez `RegexSuggester.suggest(saisie)` depuis
   le champ de formule (OCR ou filtre de conformité) :
   ```kotlin
   when (val r = RegexSuggester.suggest(saisie)) {
       RegexSuggester.Result.Empty -> Unit                       // rien a proposer
       RegexSuggester.Result.Incompatible -> afficherFormatsDifferents()
       is RegexSuggester.Result.Found -> afficher(r.blocks, r.suggestions)  // kind, pattern, alsoAccepts, rejects
   }
   ```
   Les phrases (titre et commentaire de chaque `Kind`, description des blocs) sont à
   écrire dans `strings.xml` ; celles de SuivieMAT (`regex_generator_*`) servent de
   modèle. Une formule choisie passe par le même chemin qu'une formule tapée (elle
   désarme la capture OCR).

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi |
|---|---|---|
| Seuil du consensus, codes | 3 | Environ 100 ms de visée à la cadence du capteur |
| Seuil du consensus, OCR | 5 | Bruit par image plus élevé |
| TTL du consensus | 800 ms | Un consensus abandonné ne se complète pas avec une lecture tardive |
| Fenêtre du debouncer | 1,5 s | Couvre la durée de visibilité d'un code après la capture, sans empêcher un rescan volontaire |
| Résolution d'analyse 1D et OCR | 1280×720 | Garde la cadence sans faire chauffer l'appareil |
| Résolution d'analyse 2D | 1920×1080 | En 720p, un PDF417 tombe sous ~2 px par module |
| Correspondances regex par image | 50 max | Au-delà, l'image est ignorée (formule trop large) |

Appareil de référence : Samsung Galaxy S25.

## Points de vigilance et limites connues

- **⚠️ NFC, NDEF jamais lu avec `FLAG_READER_SKIP_NDEF_CHECK` (cause établie par lecture
  du code AOSP ; patch appliqué ici le 2026-10-27, NON vérifié sur appareil).**
  - **Cause.** ScanList passait ce drapeau à `enableReaderMode`. Dans AOSP
    (`android14-release`), `NfcService.java` remet alors le tag au callback **sans**
    appeler `findAndReadNdef()`. Or `NativeNfcTag.java` n'ajoute la techno NDEF au tag
    (`addNdefTechnology`) que dans `findAndReadNdef()` ou `findNdef()`. Donc
    `Ndef.get(tag)` renvoie `null`, `cachedNdefMessage` n'existe pas, et `NfcTagDecoder`
    retombe toujours sur l'UID. La variante « `ndef.connect()` puis `ndef.ndefMessage`
    dans `NfcTagDecoder` », proposée avant, est **inopérante** : il n'y a pas d'objet
    `Ndef` à connecter. Le commentaire d'origine (« NfcTagDecoder s'en charge lui-même »)
    était faux.
  - **Patch.** `src/NfcScanSurface.kt` ne passe plus `FLAG_READER_SKIP_NDEF_CHECK`, et le
    commentaire est corrigé. `NfcTagDecoder` est inchangé : il lit `cachedNdefMessage`,
    que la sonde du système remplit.
  - **Coût attendu.** La sonde NDEF du système tourne à chaque tag : un peu de latence au
    premier contact. Pour un tag sans NDEF, le système tente ensuite de se reconnecter ;
    si la reconnexion échoue, le tag n'est pas remis au callback (même bloc de
    `NfcService.java`), et un message système d'erreur de lecture peut s'afficher si le
    constructeur l'active (`R.bool.enable_notify_read_failed`), ce qui s'écarte de la
    règle « sur erreur, ne rien faire ». Une app qui ne lit que l'UID (badge sans NDEF)
    peut remettre le drapeau.
  - **À vérifier sur un vrai téléphone**, avec un tag NDEF texte et avec un tag sans NDEF.
    La pile NFC d'un constructeur peut différer d'AOSP. `NfcValueTest` ne couvre pas ce
    chemin : `NfcTagDecoder` et `NfcScanSurface` dépendent d'Android.
  - **Apps qui portent encore le défaut** : ScanList (`1368027` sur `main`, `2ef0b6d` sur
    `claude/init-xyw7gc`) et SuivieMAT (`5eb5198`). SuivieMAT est peu concerné : il vise
    l'UID d'une carte sans NDEF.
- **Identification par l'UID d'une carte.** Deux limites, relevées par SuivieMAT
  (`consignes/plan-suivimat.md`, § 10 ter et tableau des risques) :
  - une carte à **UID aléatoire** change d'UID à chaque présentation : l'identification
    est alors inopérante. D'après la norme ISO/IEC 14443-3 (et non d'après un constat de
    SuivieMAT), un tel UID fait 4 octets et commence par `0x08` ; c'est courant sur les
    cartes à microprocesseur. Vérifiez sur la carte réelle que deux lectures donnent le
    même UID ;
  - l'UID d'un badge ne doit **pas quitter le téléphone** : il suffit souvent à le
    cloner, surtout si le badge ouvre aussi des portes. Or le pipeline par défaut
    enregistre la valeur lue dans un dossier exportable en `.csv`. Pour identifier une
    personne, gardez l'UID sur l'appareil et exportez une référence interne.
- **Regex : ICU (Android) est plus strict que le JDK des tests.** Sur téléphone, `Regex`
  passe par ICU. Une `}` littérale non échappée (hors classe, hors quantificateur) y est
  refusée, alors que le JDK l'accepte. `--` dans une classe y est lu comme une différence
  d'ensembles (sens différent, ou erreur). `RegexRuleEngine.validate` et `compileOrNull` attrapent l'exception : pas de
  plantage, mais le verdict d'un test JVM peut différer de celui du téléphone pour une
  formule saisie. ToolMap l'a appris par un vrai plantage sur un Galaxy S25, sur une regex
  figée de ToolMap et non de ce moteur (commit `c620a38`). Les formules de
  `RegexSuggester` échappent toutes les accolades littérales et mettent le tiret en
  dernier dans les classes (relu à la main, pas exécuté sous ICU).
- **Regex : messages d'erreur ICU non traduits (à vérifier sur appareil).**
  `frenchMessage` reconnaît les descriptions anglaises du JDK (« Unclosed group »,
  « Illegal repetition », « Dangling meta character »…). Sur Android, la description est
  celle d'ICU : ToolMap a vu « Syntax error in regexp pattern » sur un Galaxy S25
  (`docs/JOURNAL.md`, avec `c620a38`). D'après la table des messages d'AOSP
  (`android14-release`,
  `external/icu/android_icu4j/libcore_bridge/src/native/com_android_icu_util_regex_PatternNative.cpp`),
  seules les branches « closing bracket » et « escape » retrouvent un message ICU. Les
  autres erreurs retombent sur « Erreur : formule invalide », et la position peut
  différer. Le comportement reste sûr ; `RegexRuleEngineTest` ne vérifie ces messages
  qu'en JVM.
- **Aperçu OCR ou économie** : garder l'OCR actif quand la capture est désarmée coûte de
  la chaleur et de la batterie. SuivieMAT a choisi de ne rien extraire tant que la capture
  est désarmée (décision 26). Voir l'étape 6 de l'intégration.
- **⚠️ Lampe éteinte après un passage en arrière-plan (suspecté, à confirmer sur appareil).**
  Quand la caméra n'a plus de cas d'usage actif (`ON_STOP`, ou `unbindAll`), CameraX éteint la
  lampe et oublie la consigne. `LaunchedEffect(camera, torchEnabled)` ne se relance pas au
  retour : `bindToLifecycle` rend le même `LifecycleCamera`, et `torchEnabled` n'a pas changé.
  Le bouton montre la lampe allumée sur une scène sombre. **Patch proposé** (appliqué dans le
  Carnet, `scan/CameraPreview.kt`) : lire la consigne par `rememberUpdatedState` et rappeler
  `enableTorch` après chaque liaison et dans un `LifecycleEventEffect(ON_RESUME)`. Non reporté
  dans `src/` tant qu'il n'est pas vu sur un téléphone.
- **Verrou de 1,5 s à durée fixe** : `ScanDebouncer` n'écrit l'heure qu'à l'acceptation. Une
  valeur restée en vue est donc revalidée toutes les 1,5 s environ (signal « doublon » répété
  dans ScanList, où le dédoublonnage en base empêche toute écriture). Acceptable pour un
  inventaire ; pour une app qui affiche le dernier résultat et le laisse corriger, préférer
  le verrou glissant de la variante du Carnet (point 8).
- **Codes empilés illisibles** : Codablock F, Code 16K, Code 49 et MicroPDF417 ne sont lus ni
  par ML Kit ni par ZXing. On les reconnaît aux **barres horizontales pleines entre les
  rangées** ; un PDF417 a ses rangées collées. Contournement : le mode OCR sur le texte en
  clair (ex. `[A-Z]{2}-\d{3}[A-Z]/\d{5}`).
- **Sur erreur, ne rien faire** : une image floue, une regex invalide ou un tag mal formé ne
  lèvent jamais d'exception jusqu'au pipeline. Le scan continue.
- `ImageProxy.close()` doit être appelé **dans tous les cas** (`addOnCompleteListener`), sinon
  le pipeline se bloque dès que le tampon est plein.
- Le consensus et le debouncer **ne sont pas thread-safe** : appelez-les seulement depuis
  un seul fil. Précision relevée par le Carnet (2026-10-27) : dans `OcrAnalyzer` et
  `BarcodeAnalyzer`, `addOnSuccessListener` sans exécuteur rappelle sur le **fil
  principal**, pas sur le fil d'analyse comme le dit le commentaire du ViewModel modèle.
  Ce n'est pas un défaut (c'est toujours un seul fil), mais le code qui suppose le fil
  d'analyse (photo tirée de l'image en cours, travail lourd) doit attendre le résultat
  avec `Tasks.await` sur le fil d'analyse. Le callback NFC tourne sur un autre thread ; dans
  ScanList il n'utilise que le debouncer, ce qui est acceptable tant qu'un écran n'a qu'un
  seul mode.
- Le décodage réel n'est exerçable ni sur émulateur ni en CI : seuls les composants en
  Kotlin pur sont couverts par des tests.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-08-24 | ScanList, tests réels | OCR et code-barres imprécis (angle, reflet) | Ajout de `ScanConsensus` et de la garde d'ambiguïté, extension de la regex aux codes |
| 2026-09-15 | ScanList, terrain | Étiquette à code « en plusieurs étages » illisible | Mode 2D élargi (PDF417, Data Matrix, Aztec) en 1080p ; Codablock F et apparentés documentés comme limite |
| 2026-10-12 | Catalogage Library | Moteur déclaré éprouvé par l'utilisateur | Entrée en bibliothèque ; NFC signalé ⚠️ |
| 2026-10-27 | Carnet de recette, relecture adverse | Verrou fixe du debouncer : revalidation en boucle d'une étiquette restée en vue ; lampe non réappliquée après l'arrière-plan (suspecté) | Variante : points 8 à 10 ; vigilance ⚠️ lampe (patch proposé côté Carnet, non reporté dans `src/`) et verrou fixe ; statut inchangé |
| 2026-10-27 | Carnet de recette, intégration | OCR adapté aux éléments dans le réticule, reconnaissance sans regex par comparaison au menu de la semaine ; écouteurs ML Kit rappelés sur le fil principal | Section « Variante » ; précision sur le fil des écouteurs ; statut inchangé (pas encore essayé sur appareil) |
| 2026-10-27 | Revue ScanList et SuivieMAT | ScanList inchangé (`main` `1368027` ; `claude/init-xyw7gc` `2ef0b6d`, un commit de CI). SuivieMAT est un fork de ScanList pris à `1368027` : jusqu'à `5eb5198`, 16 des 17 fichiers `src/` et `tests/` venus de ScanList sont identiques, `NfcScanSurface` est seulement habillé au thème (`c871326`). Aucune nouvelle preuve réelle : la phase 10 (test terrain S25) reste ⬜ dans les deux dépôts. Après la revue, `1fc037b` modifie `OcrAnalyzer` et `CameraPreview` dans SuivieMAT (OCR au repos hors capture, rotation d'analyse qui suit le téléphone), non vérifié sur téléphone | SuivieMAT ajouté à « Utilisé dans » ; pas de resynchronisation ; statuts inchangés |
| 2026-10-27 | SuivieMAT, écran de test retiré (essai du 2026-09-25) | Preuve indirecte côté NFC : un écran de test a armé `enableReaderMode` sur le téléphone de l'utilisateur avec le même patron ON_RESUME/ON_PAUSE et a lu un badge ISO 14443-4 (`IsoDep`, `NfcA`, 93 ms ; `fb94ee7`, `d273afc`, écran retiré par `a0de691`). Le patron d'armement marche sur un vrai appareil. Ni `NfcTagDecoder`, ni NDEF, ni la lecture d'UID du moteur n'ont été exercés | Information seulement ; pas de promotion du NFC |
| 2026-10-27 | Catalogage Library, patch | Défaut NDEF : cause établie par lecture du code AOSP (`FLAG_READER_SKIP_NDEF_CHECK` saute la sonde NDEF, `Ndef.get(tag)` renvoie `null`). La variante `ndef.connect()` du patch proposé est inopérante | `FLAG_READER_SKIP_NDEF_CHECK` retiré de `src/NfcScanSurface.kt`, commentaire corrigé. Non vérifié sur appareil : NFC reste ⚠️. ScanList et SuivieMAT n'ont pas ce patch |
| 2026-10-27 | SuivieMAT `5c81d3c` (décision 25) | Générateur de formules regex à partir d'exemples, sans IA, Kotlin pur, 27 tests. Aucun usage réel consigné (« Générateur de formules à l'écran ⬜ Non vérifié ») | Ajout de `RegexSuggester` (🟡) dans `src/` et `tests/`, sans modification ; fichiers identiques de `5c81d3c` à `5eb5198` |
| 2026-10-27 | ToolMap (`c620a38`), relecture de la fiche | ICU d'Android plus strict que le JDK des tests ; descriptions d'erreur ICU différentes de celles que `frenchMessage` attend | Points de vigilance ICU, UID et aperçu OCR ajoutés ; ligne `ScanValue` corrigée (`normalize` garde la casse, c'est `key` qui passe en majuscules) |
| 2026-10-27 | Tests exécutés dans la Library | `ktest.sh` (Kotlin 2.0.21, JUnit 4.13.2, sans Gradle) sur `RegexSuggesterTest`, `RegexRuleEngineTest`, `ScanConsensusTest`, `ScanDebouncerTest` et `NfcValueTest` : `OK (91 tests)` | Composants purs confirmés en JVM ; statuts inchangés (un test JVM ne vaut pas un usage réel) |
