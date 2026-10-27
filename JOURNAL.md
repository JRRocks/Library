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
