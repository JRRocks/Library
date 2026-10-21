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
