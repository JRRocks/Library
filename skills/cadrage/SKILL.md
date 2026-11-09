---
name: cadrage
description: Cadrer une demande importante ou floue par une série de questions à choix, chacune avec une recommandation, puis écrire le plan - sans coder. À utiliser quand l'utilisateur demande une nouvelle fonction d'envergure, une refonte ou une nouvelle application, quand il dit « planifie », « prépare la to-do », « pose-moi les questions », « on réfléchit avant » ou « grill me », et quand une demande laisse ouvertes plusieurs décisions qui changent ce qui sera construit.
---

# Cadrer avant de construire

L'utilisateur décide vite quand les choix sont posés devant lui, et il suit presque toujours une recommandation argumentée. Ce qui coûte cher, c'est une fonction construite puis retirée parce qu'une décision manquait au départ.

## 1. Chercher avant de demander

Lis le code et la mémoire du projet. Ne pose jamais une question à laquelle le dépôt répond déjà, ni une question dont la réponse ne change rien à ce qui sera construit.

## 2. Poser les questions

- Fais la liste des décisions ouvertes et classe-les : celles dont dépendent les autres d'abord.
- **Numérote-les** (Q1, Q2, Q3) : il répond « Q1 A, Q2 oui, Q3 B ».
- Chaque question tient en une ligne et propose deux à quatre choix. Ta recommandation vient en premier, avec sa raison en une ligne.
- Dis ce que chaque choix change **pour lui** : ce qu'il verra, ce qu'il devra faire, ce que ça coûte en attente. Pas le détail technique.
- Utilise l'outil de questions à choix quand il existe : il répond d'un doigt. Par lots de quatre au plus, la suite dépendant des réponses.
- Préviens-le par une notification quand des questions l'attendent, si l'outil existe.

## 3. Savoir s'arrêter

- Arrête quand plus aucune décision ouverte ne change ce qui sera construit.
- Une question restée sans réponse reçoit ta recommandation. Dis-le dans le plan : il doit pouvoir y revenir.
- Ce qui déborde de la demande part dans le fichier des idées pour plus tard, pas dans le plan.

## 4. Écrire le plan, et rien d'autre

Dans la mémoire du projet, en markdown, **sans toucher au code** :

- les décisions prises, numérotées et datées, avec ses mots ;
- les tâches, petites, dans l'ordre, chacune avec un critère qu'il peut observer lui-même (« le fichier exporté s'ouvre dans le tableur », « je ne vois plus le 7 devant le 33 ») ;
- ce qui ne pourra être vérifié que sur son téléphone ;
- ce qui touche à l'interface, à faire passer par un aperçu avant le code.

## 5. Attendre le feu vert

Le message final donne le lien du plan, le nombre de tâches et la première à faire. Le code commence quand il le dit.
