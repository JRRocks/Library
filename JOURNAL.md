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
