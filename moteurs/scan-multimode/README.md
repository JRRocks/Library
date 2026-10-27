# Scan multimode : QR/2D, code-barres, OCR+regex, NFC/RFID

| Champ | Valeur |
|---|---|
| **Identifiant** | `scan-multimode` |
| **Statut global** | ✅ **Éprouvé** pour l'OCR et le code-barres (déclaré éprouvé par l'utilisateur après des tests en réel). Le NFC et l'élargissement 2D restent 🟡, voir le tableau des composants |
| **Plateforme** | Android natif, Kotlin, minSdk 26 |
| **Dépendances** | CameraX 1.3.4, ML Kit `barcode-scanning` 17.3.0 et `text-recognition` 16.0.1 (modèles **embarqués**, donc hors-ligne), `android.nfc` |
| **Provenance** | [JRRocks/ScanList](https://github.com/JRRocks/ScanList) (privé), commit `1368027` (2026-09-22) |
| **Utilisé dans** | ScanList · Carnet de recette (intégré le 2026-10-27, OCR seul et **sans regex** : voir la variante, pas encore essayé sur appareil) |
| **Fichiers d'origine** | `app/src/main/java/com/scanlist/app/scanner/*`, `domain/{model/ScanValue,nfc/NfcValue,regex/RegexRuleEngine}.kt`, `ui/scan/{CameraPreview,NfcScanSurface,ScanViewModel}.kt` |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

On scanne **à la chaîne** des objets physiques (inventaire, contrôle de matériel) avec un
seul téléphone tenu d'une main. Il y a quatre modes **mutuellement exclusifs** :

| Mode | Lit | Moteur |
|---|---|---|
| Code 2D | QR Code, PDF417, Data Matrix, Aztec | ML Kit Barcode, filtré **au décodeur** |
| Code-barres | EAN-13/8, UPC-A/E, Code 39/93/128, ITF, Codabar | ML Kit Barcode, filtré **au décodeur** |
| OCR | Texte imprimé, filtré par une **formule regex** écrite à la volée | ML Kit Text Recognition (latin) |
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

## Composants

| Fichier (`src/`) | Rôle | Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| `ScanConsensus.kt` | N lectures identiques, TTL, garde d'ambiguïté | Non, Kotlin pur | `ScanConsensusTest` | ✅ |
| `ScanDebouncer.kt` | Verrou anti-rebond de 1,5 s par clé, purge auto | Non | `ScanDebouncerTest` | ✅ |
| `ScanValue.kt` | Normalisation partagée (trim, espaces réduits, majuscules) : **une seule** règle pour le dédoublonnage et la comparaison | Non | via les autres tests | ✅ |
| `RegexRuleEngine.kt` | `extract` (OCR : isole les fragments, 50 max par image), `conforms` (codes : correspondance **totale**), `validate` (messages d'erreur traduits en français), comptage des groupes capturants | Non | `RegexRuleEngineTest` | ✅ |
| `BarcodeFormats.kt` | Masques de formats 1D et 2D **disjoints**, noms de symbologie | Constantes ML Kit | `BarcodeFormatsTest` | ✅ 1D · 🟡 PDF417, Data Matrix, Aztec pas encore vus sur un vrai code |
| `BarcodeAnalyzer.kt` | `ImageAnalysis.Analyzer` ML Kit, filtrage dans les **options** du décodeur | Oui | non (pas testable hors appareil) | ✅ |
| `OcrAnalyzer.kt` | `ImageAnalysis.Analyzer` qui remonte le texte brut ; la regex est appliquée par l'appelant | Oui | non | ✅ |
| `CameraPreview.kt` | Composable CameraX : 720p (1D, OCR) ou 1080p (2D), `STRATEGY_KEEP_ONLY_LATEST`, torche, réticule qui se remplit selon l'avancement du consensus | Oui | non | ✅ |
| `ScanFeedback.kt` | Trois signatures sonores et haptiques : succès, doublon, hors liste | Oui | non | ✅ |
| `NfcValue.kt` | Choix NDEF/UID, décodage RTD_TEXT (UTF-8/UTF-16) | Non | `NfcValueTest` | 🟡 |
| `NfcTagDecoder.kt` | Pont `Tag` Android vers `NfcValue` | Oui | non | 🟡 ⚠️ voir les points de vigilance |
| `NfcScanSurface.kt` | Composable qui arme et désarme `enableReaderMode` sur ON_RESUME/ON_PAUSE ; repli si pas de puce ou NFC coupé | Oui | non | 🟡 |

Les packages d'origine (`com.scanlist.app.*`) sont conservés : **renommez-les** à
l'intégration.

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
   l'aperçu tourne mais rien n'est écrit. Toute modification de la formule **désarme** la
   capture. Sans ça, une formule incomplète comme `\d` enregistre des dizaines de valeurs
   parasites.

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

- **⚠️ NFC, NDEF probablement jamais lu (suspecté, à confirmer sur appareil).** `NfcScanSurface`
  passe `FLAG_READER_SKIP_NDEF_CHECK` à `enableReaderMode`. Avec ce drapeau, Android ne fait
  pas la vérification NDEF à la découverte du tag. La technologie `Ndef` n'est donc pas
  ajoutée et `Ndef.get(tag)` / `cachedNdefMessage` renvoient `null`. Résultat probable : on lit
  toujours l'UID, jamais le texte NDEF. **Patch proposé** : retirer
  `FLAG_READER_SKIP_NDEF_CHECK` des drapeaux, ou bien `ndef.connect()` puis
  `ndef.ndefMessage` dans `NfcTagDecoder`. Si un tag NDEF texte remonte quand même son UID
  sur un vrai téléphone, appliquez le patch ici et dans ScanList, puis consignez-le dans
  `JOURNAL.md`.
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
| 2026-10-27 | Carnet de recette, intégration | OCR adapté aux éléments dans le réticule, reconnaissance sans regex par comparaison au menu de la semaine ; écouteurs ML Kit rappelés sur le fil principal | Section « Variante » ; précision sur le fil des écouteurs ; statut inchangé (pas encore essayé sur appareil) |
