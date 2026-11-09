---
name: apercu-avant-code
description: Montrer un aperçu visuel (état actuel, état proposé, variantes) et obtenir un « oui » explicite AVANT de coder tout changement visible d'une application. À utiliser dès qu'une demande touche un écran, un menu, un bouton, un dialogue, une icône, une couleur, un texte affiché, une disposition, un geste ou un enchaînement d'écrans, et quand l'utilisateur dit « propose », « montre-moi », « maquette », « variantes », « refonte » ou « design ». Ne s'applique ni à la logique invisible, ni à ce qui ne sert qu'au debug.
---

# Aperçu validé avant le code

Un écran codé puis refusé coûte une session. Un aperçu refusé coûte une minute. L'utilisateur décide vite quand il voit : on lui montre d'abord.

## Ce qui est concerné

- **Oui** : tout ce qui se voit ou se touche (écran, menu, bouton, dialogue, icône, couleur, texte affiché, disposition, geste, enchaînement d'écrans).
- **Non** : logique, performance, tests, documentation sans effet visible ; et tout ce qui ne sert qu'au debug, qui se décide et se fait sans aperçu et sans question.
- L'utilisateur peut lever la règle pour une demande (« code directement », « tu peux commit dans la foulée ») : cela vaut pour cette demande seulement.

## 1. L'aperçu d'abord

Une page HTML autonome qui montre **l'état actuel** à côté de **l'état proposé**.

- **Combien de variantes** : cinq pour une refonte ou un vrai choix de design ; pour une retouche ponctuelle, l'état actuel et l'état proposé suffisent, avec deux ou trois variantes seulement si un choix se pose.
- **Repères pour répondre** : numérote les questions (1, 2, 3) et nomme les variantes (A, B, C). Il répond « 1 A, 2 oui, 3 C ». Mets ta recommandation en premier, avec sa raison en une ligne.
- **État actuel** : une capture de l'émulateur quand il y en a un ; sinon l'écran reconstruit d'après le code, en le disant.
- **Contenu réel** : les vrais libellés, en français, avec des données plausibles. Pas de texte de remplissage.
- **Page statique, sans script** : une page qui dépend d'un script a déjà refusé de s'afficher chez lui. Elle doit se lire à la largeur d'un téléphone (vérifie à 400 px de large), en clair et en sombre si l'application a les deux.
- **Rangement** : dans le dossier d'aperçus du projet (`docs/previews/` par défaut), nommé d'après la tâche et le sujet.
- **Des variantes défendables** : appuie-les sur le dossier UI/UX de la Library (`~/.claude/library/docs/ui-ux/README.md`, check-lists du chapitre 41) plutôt que sur le goût du moment. Pour une refonte, il a déjà demandé deux fois de suivre les guides *impeccable* (`github.com/pbakaus/impeccable`) : propose de les reprendre.

Montre la page dans la conversation (rendu, fichier ou page privée publiée), puis préviens-le par une notification si l'outil existe : un choix l'attend.

## 2. Attendre le « oui »

- Sans accord explicite : aucun code d'interface, aucun texte dans les ressources, aucune icône.
- En attendant, avance sur la logique invisible qui ne dépend pas du choix.
- Il peut mélanger : « l'affichage de C avec la barre de B ». Fais le mélange ; s'il change nettement l'allure, remontre-le.
- S'il répond à certaines questions et pas aux autres, ta recommandation vaut pour celles-là : dis-le dans le message final.

## 3. Coder fidèlement

- Le code suit l'aperçu validé. Un écart imposé par une contrainte technique repasse par un nouvel aperçu avant d'être codé.
- **Pas de bouton sans moteur** : chaque commande visible fait réellement quelque chose. Une option sans effet se retire. Un écran n'est pas fini tant que la logique derrière chaque commande ne l'est pas.

## 4. Montrer le résultat réel

Après le code, place le rendu réel à côté de l'aperçu validé :

- avec un émulateur : une capture ;
- sans émulateur (session cloud) : l'image produite par un test de rendu d'écran (méthode : `~/.claude/library/docs/methodes/captures-compose-robolectric.md`), ou une capture du navigateur pour une application web.

Une maquette ne remplace pas l'écran réel : des défauts visibles au premier coup d'œil n'ont été trouvés qu'après huit versions, faute de rendu réel entre les étapes d'une refonte.

Dis ce qui diffère de l'aperçu, s'il y a une différence.

## 5. Garder la trace

Dans le journal du projet : le fichier de l'aperçu, la variante retenue, la date, ses mots. Dans les décisions : la variante retenue et celles qui ont été écartées. Si le projet tient un index de son interface, mets-le à jour.
