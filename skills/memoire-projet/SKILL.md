---
name: memoire-projet
description: Lire la mémoire d'un projet au début d'une session et la tenir à jour à la fin (journal, avancement, décisions), pour que la session suivante reprenne sans que l'utilisateur réexplique. À utiliser au début de toute session de travail sur un projet, avant chaque push, en fin de session, après un /clear, et quand l'utilisateur dit « reprise », « reprends », « continue », « où en est-on », « note-le » ou « mets à jour le journal ».
---

# Mémoire du projet

Chaque session repart de zéro. Ce qui n'est pas écrit dans le dépôt est perdu, et l'utilisateur ne veut pas réexpliquer. La mémoire du projet est ce qui permet à des sessions successives, sur le téléphone ou sur un PC, de faire avancer le même travail.

## Où elle se trouve

Chaque projet a sa disposition : suis-la, n'en invente pas une seconde. Le `CLAUDE.md` du projet dit où lire. Les deux dispositions courantes :

| Rôle | Disposition « docs » | Disposition « consignes » |
|---|---|---|
| Ce qui est décidé | `docs/DECISIONS.md` | `consignes/contexte.md` (décisions numérotées) |
| Où on en est | `docs/PLAN.md` | `consignes/workflow.md` (tableau d'avancement) |
| Ce qui s'est passé | `docs/JOURNAL.md` | `consignes/workflow.md` (journal) |
| Avec quoi on construit | `CLAUDE.md`, `docs/specs/` | `consignes/outils.md` |

## Au début de la session

1. Lis l'avancement, puis les **trois dernières entrées** du journal (pas le journal entier), puis les décisions avant tout choix d'architecture ou de fonctionnement.
2. Regarde le résultat de la dernière compilation de la branche : la session précédente a pu partir avant sa fin, et un échec resté sans explication se retrouve deux sessions plus tard.
3. Vérifie l'état réel du code avant de conclure quoi que ce soit : la documentation peut être en retard.
4. Choisis la tâche : celle que l'utilisateur demande ; sinon la « prochaine étape » de la dernière entrée, ou la première tâche non bloquée du plan. Marque-la « en cours ».

## Pendant le travail

- Une décision de l'utilisateur se note au moment où il la prend : date, ses mots en une courte citation, et le pourquoi.
- Distingue toujours ce qu'il a décidé de ce que tu as choisi toi-même (« choix du développeur ») : il doit pouvoir revenir sur le second sans chercher.
- Les idées « pour plus tard » vont dans un fichier à part (`docs/idees-futur.md`), jamais dans le plan. Ce fichier ne se lit que s'il le demande.
- Si le projet numérote ses tâches, le message de commit commence par le numéro de la tâche.

## À la fin, avant chaque push, même si la tâche n'est pas finie

- **Journal** : une entrée en haut, selon le gabarit du projet. À défaut :

  ```markdown
  ## AAAA-MM-JJ · <titre écrit pour l'utilisateur>
  - Branche et commit :
  - Objectif :
  - Réalisé :
  - Décisions :
  - Vérifications : <commandes lancées et résultats>
  - Non vérifié sur téléphone : <points précis>
  - Problèmes et dette :
  - Prochaine étape :
  ```

- **Avancement** : une tâche n'est « terminée » que si ses critères sont vérifiés et que la vérification est écrite. Ce qui attend un essai sur le téléphone reste « à vérifier sur téléphone », dans une liste unique.
- **Décisions** : une ligne nouvelle, numérotée et datée. Une décision ancienne se révise par une ligne qui la remplace et un renvoi, jamais en la réécrivant.
- **Spécifications** : à jour si le comportement a changé.

Vise court : ce dont la prochaine session a besoin, pas le récit de la séance.

## Règles de tenue

- **On ne réécrit pas le passé.** Une erreur du journal se corrige par une nouvelle entrée. Seule exception : retirer une donnée privée.
- **Une information, un seul endroit.** Un fait recopié dans cinq fichiers est périmé dans quatre. Le `CLAUDE.md` renvoie vers le tableau d'avancement, il ne recopie ni le nombre de tests ni la version en cours.
- **Le `CLAUDE.md` reste court** (moins de 200 lignes) : règles et commandes. Il est chargé en entier à chaque session ; le détail va dans des documents lus à la demande.
- **Un document de travail terminé se supprime** : plan fini, notes de passation, liste de tâches soldée. Les documents périmés trompent la session suivante.
- **Les questions ouvertes ne dorment pas dans un document** : pose-les à l'utilisateur, ou range-les dans une liste unique des décisions en attente.
- **Sessions en parallèle** : journal, décisions et fichier de version sont les trois fichiers qui entrent en conflit à la fusion. Réserve une plage de numéros de décision par session, et garde les deux entrées à la fusion.

## Projet sans mémoire

Ne travaille pas avec des notes improvisées. Propose de créer le trio minimal : `CLAUDE.md` (règles et commandes), `docs/JOURNAL.md`, `docs/DECISIONS.md`, et `docs/PLAN.md` dès que le projet dépasse quelques tâches.
