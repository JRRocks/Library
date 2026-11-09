---
name: moteurs
description: Chercher dans la Library une brique déjà construite et éprouvée avant d'en écrire une, la reprendre selon sa fiche, puis rapporter comment elle s'est comportée. À utiliser avant de coder un scan (QR, code-barres, OCR, NFC), une mise à jour intégrée de l'application, une sauvegarde sur Google Drive, un paiement par carte, un rapport de plantage, des calculs de soleil et de lune, des exercices avec répétition espacée, et toute fonction qu'une autre application de l'utilisateur possède déjà ; et quand il dit « comme dans l'autre app », « reprends le moteur de » ou « on l'a déjà fait ».
---

# Reprendre un moteur de la Library

La Library range les briques déjà construites dans les applications de l'utilisateur, avec ce qui a coûté cher à apprendre : les réglages qui marchent et les pièges déjà rencontrés. Réécrire une de ces briques, c'est refaire le même chemin, pièges compris.

## Où est la Library

- `~/.claude/library/` quand l'installation l'a posée ;
- le dépôt `Library` s'il est joint à la session ;
- sinon, dépôt public : `git clone --depth 1 https://github.com/JRRocks/Library.git` dans un dossier temporaire.

## 1. Chercher

Ouvre `INDEX.md` : « Je cherche à… » (§ 2), les mots-clés (§ 3), puis les candidats (§ 5). Regarde aussi les fiches méthode (`docs/methodes/README.md`) : voir un écran sans émulateur, compiler Android depuis une session cloud.

## 2. Lire la fiche en entier

Surtout « Points de vigilance » : c'est là que sont les heures déjà perdues. Regarde le statut, composant par composant :

| Statut | Ce que tu peux en attendre |
|---|---|
| ✅ Éprouvé | A marché en conditions réelles |
| 🟡 Testé | Tests verts, pas encore vu en réel : dis-le à l'utilisateur |
| ⚠️ À patcher | Défaut connu : lis-le avant de reprendre le code |
| 🔬 Candidat | Repéré, pas extrait : le code est encore dans son dépôt d'origine |

## 3. Reprendre

- Copie `src/` et `tests/` dans le projet, renomme les paquets, suis la section « Intégration ».
- Fais passer les tests repris dans le projet.
- Garde les réglages éprouvés tels quels, sauf raison précise.
- Ne copie aucun secret, et jamais une clé de signature.

## 4. Rapporter

Dans le message final, une ligne : la brique reprise, son statut, ce qui a été vérifié.

Après un essai réel sur le téléphone :

- elle a bien marché : dis-le, avec le matériel et le contexte, pour que sa fiche soit mise à jour ;
- elle a un défaut : décris le symptôme et les conditions. Un défaut corrigé seulement dans l'application reviendra dans la suivante.

N'écris dans la Library que si elle est jointe à la session et que l'utilisateur le demande. Suis alors son `AGENTS.md`.

## 5. Proposer

Une brique neuve qui resservira (isolable, testée, sans secret ni donnée personnelle) : signale-la dans le message final comme candidate pour la Library.
