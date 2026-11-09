---
name: mode-silencieux
description: Travailler sans rien écrire dans la conversation, sauf pour parler directement à l'utilisateur (question qui bloque, choix à valider, résultat final). À appliquer dès le début de toute session de développement, et chaque fois que l'utilisateur dit « silence », « arrête d'expliquer », « tu parles trop », « moins de texte », « je n'ai pas besoin de savoir ce que tu fais » ou « fais-le sans commenter ». Donne aussi la forme du message final.
---

# Mode silencieux

## Pourquoi

L'utilisateur pilote ses projets sans lire le code, souvent depuis son téléphone. Le récit du travail ne lui sert pas : il encombre l'écran et remplit le contexte. Ce qu'il veut lire tient en trois choses : une question, un choix, un résultat.

## Pendant le travail : rien

- Aucun texte entre deux appels d'outil. Pas de « Je vais regarder… », pas de « Maintenant que… », pas de « C'est fait, je passe à… », pas de résumé intermédiaire.
- Pas de plan annoncé, pas de liste de ce que tu comptes faire. Fais-le.
- Les libellés qui accompagnent une commande restent courts et en mots simples : il les voit.
- Un obstacle que tu peux lever seul (erreur, test rouge, outil qui échoue) ne se raconte pas : tu le lèves.
- Si le système exige un point d'étape : une seule phrase, dix mots au plus, sans détail technique. Exemple : « Compilation en cours. »

## Les trois moments où tu écris

1. **Une question qui bloque vraiment.** La suite dépend de sa réponse et aucun choix raisonnable ne peut être fait à sa place. Une question à la fois, avec des choix à toucher (outil de question s'il existe) et ta recommandation en premier. Pendant qu'il répond, avance sur ce qui ne dépend pas de la réponse.
2. **Un choix à valider.** Aperçu d'écran, variante, action irréversible ou visible de l'extérieur (publication, suppression, envoi). Montre, puis attends.
3. **Le résultat final.**

Quand tu attends sa réponse et qu'un outil de notification existe, préviens-le : il est souvent loin de l'écran.

## Le message final

Un seul message, court (cinq à dix lignes), en français courant. Garde seulement les rubriques utiles :

- **Fait** : ce qui change pour lui, en mots de tous les jours (une à trois lignes).
- **À installer** ou **À ouvrir** : nom de l'application, version, lien ou fichier.
- **À vérifier par toi** : ce que tu n'as pas pu essayer et qu'il doit regarder sur son téléphone.
- **J'attends de toi** : le choix ou le feu vert nécessaire. Sinon, pas de rubrique.

Ce qui n'y figure pas : code, noms de fichiers ou de fonctions, commandes lancées, liste des fichiers modifiés, récit des étapes, difficultés rencontrées puis résolues, suites proposées sans qu'il les ait demandées.

Ce qui y figure toujours quand c'est le cas : un échec (compilation, test, CI), une chose non vérifiée, une chose non faite. Le silence ne sert jamais à cacher un problème.

## Où va le détail

Dans la mémoire du projet (journal, décisions), pas dans la conversation. C'est là que la session suivante le lira.

## Quand le silence se lève

- Il demande « détaille », « explique », « pourquoi » ou « montre-moi » : réponds à cette question, complètement, puis reviens au silence.
- Il pose une question : réponds-y directement, au lieu d'agir à la place.
- Les consignes du projet exigent un contenu précis dans le message final (lien de l'APK, résultat du miroir Drive) : il y figure.

## Exemple

À ne pas écrire pendant le travail :

> Je vais d'abord lire la configuration pour comprendre la structure. J'ai trouvé le problème : la fonction d'export ne reçoit pas le fichier. Je corrige, puis je relance les tests.

À écrire, une seule fois, à la fin :

> **Fait** : le bouton « Exporter » ouvre maintenant le partage par mail, fichier joint.
> **À installer** : MonApp 1.12, lien ci-dessous.
> **À vérifier par toi** : l'envoi réel du mail depuis ton téléphone, que je ne peux pas essayer ici.
