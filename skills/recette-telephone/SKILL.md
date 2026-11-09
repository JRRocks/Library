---
name: recette-telephone
description: Tenir la liste unique de ce que seul l'utilisateur peut vérifier sur son téléphone, lui demander peu à chaque livraison, et préparer une séance d'essai guidée quand la liste grossit. À utiliser à chaque livraison qui laisse des points « non vérifiés sur téléphone », quand l'utilisateur dit « qu'est-ce que je dois tester ? », « j'ai testé », « ça marche » ou « ça ne marche pas », et avant de déclarer une étape terminée.
---

# Recette sur le téléphone

Une session ne peut pas tout essayer : le GPS réel, la caméra, le NFC, les capteurs, deux téléphones ensemble, l'écran éteint, la batterie. L'utilisateur est le seul testeur de ces points. Sans liste, ils s'accumulent : des dizaines de tâches restent « à vérifier » et un défaut visible dès le premier essai n'est trouvé qu'après huit versions.

## 1. Une liste unique par projet

Un seul endroit (une section du plan, ou `docs/RECETTE.md`), une ligne par point :

| N° | Depuis la version | Ce qu'il fait | Ce qu'il doit voir | État |
|---|---|---|---|---|
| R12 | 1.8 | Scanner trois étiquettes à la suite | Trois lignes, un bip à chacune | à essayer |

- « Ce qu'il fait » et « ce qu'il doit voir » s'écrivent dans ses mots, sans terme technique. Le résultat attendu est observable.
- États : `à essayer`, `bon` (date), `défaut` (date, ses mots).
- Tout point que tu déclares « non vérifié sur téléphone » entre dans cette liste au moment où tu l'écris.

## 2. Avant de lui demander

Essaie toi-même tout ce qui peut l'être : tests, rendu d'écran, émulateur quand il y en a un. La liste ne contient que ce qui exige vraiment son téléphone.

## 3. À chaque livraison : peu, et le plus risqué d'abord

Dans le message final, trois points au plus, ceux qui ont le plus de chances de casser ou qui bloquent la suite. Le reste attend dans la liste. Quand elle dépasse dix points, dis-le en une ligne (« 12 points attendent ton essai »).

Ne bloque pas une livraison sur la recette, et ne relance pas à chaque message : il essaie quand il a le temps.

## 4. La séance d'essai guidée

Quand la liste est longue, avant une étape importante ou s'il le demande, prépare une page lisible sur le téléphone :

- les points rangés dans l'ordre d'un seul parcours de l'application, pour tout faire en une fois ;
- pour chacun : le geste, le résultat attendu, et de quoi répondre « bon » ou « défaut » avec un mot ;
- un bouton « Copier mes réponses » quand la page le permet. La page doit rester utilisable sans lui : les points sont numérotés, il peut répondre « 1 bon, 2 défaut : le bip est absent ».

Annonce la durée (« environ dix minutes »).

## 5. Quand il répond

- **Bon** : note la date. La tâche concernée peut passer à « terminée ».
- **Défaut** : note ses mots tels quels, demande une capture si elle aide, ouvre une tâche de correction. Un défaut passe avant une nouvelle fonction, sauf s'il en décide autrement.
- **« Ça marche »** sans détail : marque bons les points de la dernière livraison, pas toute la liste.

## 6. Aider le téléphone à parler

Quand un point peut échouer sans rien montrer, donne à l'application un moyen de le dire : un écran de diagnostic dont le rapport se copie, ou un rapport de plantage lisible sur le téléphone. Il colle le rapport dans la conversation, et la correction ne se fait plus à l'aveugle.
