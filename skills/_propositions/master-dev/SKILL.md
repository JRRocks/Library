---
name: master-dev
description: Tenir le rôle de « master dev » - une session qui ne code dans aucune application, fait le tour de tous les projets, tient la Library à jour (règles communes, skills, moteurs) et rend un bilan court avec des propositions. À utiliser quand l'utilisateur dit « master dev », « session maître », « fais le tour de mes projets », « mets à jour ma méthode », « mets à jour la Library », « propose-moi des skills » ou « vérifie le travail des autres sessions ».
---

# Master dev

## Le rôle

Une session qui regarde tous les projets d'en haut. Elle **n'écrit que dans la Library** : elle ne touche au code d'aucune application, sauf demande explicite pour un dépôt précis. Elle suit l'`AGENTS.md` de la Library et, comme toute session, les règles communes.

## Avant d'écrire dans la Library

1. Récupère **toutes** les branches de la Library et fusionne-les dans la branche de travail. D'autres sessions y écrivent sans jamais fusionner. Dans un conflit de journal ou de fiche, garde les deux côtés.
2. Regarde chaque dépôt sur sa **branche active**, pas seulement `main`, avec un clone complet : un clone superficiel fait croire à de fausses divergences et cache des commits.
3. Confie les lectures volumineuses à des sous-agents, un par groupe de dépôts. Demande-leur de citer fichier et ligne, et de ne rapporter aucune donnée personnelle. Vérifie toi-même ce que tu vas écrire ou annoncer.

## Ce qu'elle regarde dans chaque projet

| Point | Question |
|---|---|
| Mémoire | Le `CLAUDE.md` est-il court et à jour ? Journal, avancement et décisions suivent-ils ? Reste-t-il des documents périmés ? |
| Livraison | Chaque changement donne-t-il un fichier installable, avec une version nouvelle et la même clé ? |
| Vérification | Les tests tournent-ils dans la chaîne ? Combien de points attendent un essai sur le téléphone ? |
| Confidentialité | Dépôt public : rien de privé dans les fichiers ni dans l'historique ? Quelle identité signe les commits ? |
| Copies | Des branches jamais fusionnées ? Une branche par défaut qui n'est pas celle qu'on livre ? |
| Décisions | Des questions ouvertes restées sans réponse ? |
| Écarts | Une règle du projet contredit-elle les règles communes, ou celle d'un autre projet ? |

## Faire évoluer la méthode

- Une demande ou une correction de l'utilisateur qui revient dans deux projets devient une règle commune (`SOCLE.md`) ou un skill.
- Une règle écrite puis oubliée deux fois ne s'écrit pas une troisième fois : elle devient un automatisme (script, contrôle dans la CI, étape d'un skill).
- `SOCLE.md` tient en une page : il est chargé dans chaque session. Le détail va dans un skill.
- Un skill nouveau naît dans `skills/_propositions/`. Il passe dans `skills/` quand l'utilisateur dit oui.
- Chaque changement s'écrit dans le journal de la Library, avec la date et les mots de l'utilisateur.

## Tenir le catalogue des moteurs

Selon l'`AGENTS.md` de la Library : retours d'usage, défauts à corriger, moteurs à ajouter, candidats repérés pendant la revue.

## Coordonner des sessions en parallèle

- Une session par projet et par branche, sur des fichiers séparés.
- Trois fichiers entrent en conflit à chaque fusion : le journal, les décisions, le fichier de version. Réserve une plage de numéros de décision par session ; à la fusion, garde les deux entrées.
- Vérifier le travail d'une autre session : relis ce qui a changé, relance les tests, essaie ce qui peut l'être. Ne fusionne que sur demande.

## Mettre en service

- Un changement de la Library ne sert qu'une fois fusionné dans `main` : pull request sur demande de l'utilisateur.
- Une fusion faite par GitHub est signée de l'adresse du compte. Tant que sa protection n'est pas confirmée, passe par un push en avance rapide, avec l'accord de l'utilisateur.
- Sessions cloud : après la fusion, propose de changer la date du script de l'environnement. Les nouvelles sessions réinstallent alors tout de suite, au lieu d'attendre l'expiration du cache (environ sept jours).
- PC : `git -C ~/.claude/library pull && bash ~/.claude/library/installation/installer.sh`.

## Le bilan

Court, dans cet ordre :

1. ce qui a été écrit dans la Library, avec le lien ;
2. trois à cinq constats, les plus graves d'abord (confidentialité et perte de données en tête), chacun avec l'action proposée ;
3. les décisions attendues de l'utilisateur, numérotées, avec une recommandation.

Rien n'est modifié hors de la Library sans son feu vert.
