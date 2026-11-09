# <Nom lisible du moteur : ce qu'il fait, en une ligne>

<!-- Gabarit de fiche. Copiez ce dossier en moteurs/<identifiant>/, remplissez, puis supprimez
     les commentaires. Toutes les sections sont obligatoires ; écrivez « Aucun » plutôt que de
     supprimer une section. Voir AGENTS.md § « Ajouter un moteur ». -->

| Champ | Valeur |
|---|---|
| **Identifiant** | `<identifiant-kebab-case>` (= nom du dossier) |
| **Statut** | <✅ Éprouvé · 🟡 Testé · 🔬 Candidat · ⚠️ À patcher · ⛔ Obsolète>, avec **la preuve** en une phrase |
| **Plateforme** | <Android natif / Kotlin pur / Python / JS navigateur…>, versions minimales |
| **Dépendances** | <bibliothèques + versions exactes ; « aucune » si aucune> |
| **Provenance** | [<owner>/<dépôt>](<url>) (<public/privé>), commit `<sha court>` (<AAAA-MM-JJ>) |
| **Utilisé dans** | <apps qui l'utilisent réellement> |
| **Fichiers d'origine** | <chemins dans le dépôt source> |
| **Dernière mise à jour de la fiche** | <AAAA-MM-JJ> |

## À quoi ça sert

<Le problème résolu, en langage simple. Quand l'utiliser, quand ne pas l'utiliser.>

## Comment ça marche

<Principe, pipeline, décisions clés et leur raison. Si le moteur a été corrigé après un
retour terrain : le constat, la cause, le correctif.>

## Composants

| Fichier (`src/`) | Rôle | Dépend d'Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| | | | | |

## Intégration dans une nouvelle app

1. <Dépendances à ajouter (copier-coller)>
2. <Manifeste / permissions / configuration>
3. <Fichiers à copier, packages à renommer, ce qu'il faut retirer s'il reste du code propre à l'app d'origine>
4. <Exemple d'appel minimal>

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi |
|---|---|---|
| | | |

## Points de vigilance et limites connues

- <Pièges rencontrés, limites assumées, ce qui n'a jamais été vérifié en réel>

## Historique des retours d'usage

<!-- Ne jamais réécrire ni supprimer une ligne : on en ajoute. La plus ancienne en haut. -->

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| <AAAA-MM-JJ> | <app / test / catalogage> | <ce qui a été constaté> | <statut changé, patch, réglage…> |
