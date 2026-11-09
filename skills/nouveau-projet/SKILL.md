---
name: nouveau-projet
description: Démarrer une nouvelle application avec la méthode de l'utilisateur dès le premier jour - cadrage, mémoire du projet, chaîne de livraison qui produit un fichier installable, sauvegardes, garde-fous. À utiliser quand l'utilisateur dit « nouveau projet », « nouvelle app », « on démarre » ou « crée le dépôt », quand un dépôt ne contient qu'un README, et quand un projet existant n'a ni CLAUDE.md ni journal.
---

# Démarrer un projet

Chaque projet a réinventé ses règles : où est la mémoire, comment arrive l'APK, comment monte la version. Ce skill pose le même départ partout, pour que la première session ressemble à la centième.

## 1. Cadrer

Pose les questions qui changent ce qui sera construit, numérotées, avec des choix et ta recommandation :

- ce que fait l'application, pour qui, sur quel appareil ;
- hors ligne ou non, avec ou sans compte ;
- pour lui seul ou pour d'autres personnes ;
- dépôt public ou privé (§ 6) ;
- langue de l'interface, du code et des messages de commit.

Note chaque réponse dans les décisions du projet, avec la date.

## 2. Chercher ce qui existe déjà

Avant d'écrire une brique, regarde la Library (`~/.claude/library/INDEX.md`, ou le dépôt `Library`) : moteurs déjà éprouvés, fiches méthode pour compiler et voir un écran depuis une session cloud, dossier UI/UX pour concevoir l'interface.

## 3. Poser la mémoire

Copie les gabarits du dossier `modeles/` de ce skill :

| Gabarit | Devient | Rôle |
|---|---|---|
| `modeles/CLAUDE.modele.md` | `CLAUDE.md` | ce qui est propre au projet : commandes, livraison, conventions. Moins de 200 lignes |
| `modeles/PLAN.md` | `docs/PLAN.md` | tâches numérotées, critères observables, liste « à vérifier sur le téléphone » |
| `modeles/JOURNAL.md` | `docs/JOURNAL.md` | une entrée par session, la plus récente en haut |
| `modeles/DECISIONS.md` | `docs/DECISIONS.md` | décisions numérotées et datées |

Crée aussi `docs/idees-futur.md`, vide : les idées pour plus tard iront là, jamais dans le plan.

Le `CLAUDE.md` ne recopie pas les règles communes : elles viennent de la Library.

## 4. Livrer dès le premier jour

Avant toute fonction, une application vide qui **s'installe sur son téléphone**. Dix étapes codées sans appareil ont déjà donné une application qui ne s'ouvrait pas.

- Une compilation automatique à chaque push, qui produit un fichier installable et le met là où il peut le prendre depuis son téléphone.
- Une clé de signature fixe : chaque version s'installe par-dessus la précédente, données gardées.
- Une version qui monte à chaque compilation, sans intervention : une règle manuelle s'oublie.
- Un fichier nommé `<NomApp>.<version>.apk`.
- Les tests lancés avant la compilation, dans la même chaîne.
- Un rapport de plantage lisible sur le téléphone, pour qu'un plantage ne soit jamais muet.

Dis-lui d'installer cette première version, et attends son retour avant d'empiler les fonctions.

## 5. Prévoir comment vérifier

- La logique dans un module qui se teste sans appareil.
- Un test de rendu d'écran, pour voir l'interface sans émulateur.
- Dans le plan, la liste de ce que seul son téléphone peut montrer.

## 6. Public ou privé : sa décision

| | Dépôt public | Dépôt privé |
|---|---|---|
| Compilation automatique | Gratuite | Minutes limitées, ou exécuteur sur son PC |
| Ce qui est poussé | Visible par tous, pour toujours | Visible par lui |
| Clé de debug versionnée | Devient publique : à ne pas utiliser pour une application distribuée | Sans risque |
| Règles | Rien de privé, identité de commit `noreply`, dès le premier commit | |

Un projet repris d'un autre (fork) : garde sa licence et la mention d'origine ; change le nom, l'icône et l'identifiant de l'application ; coupe ou remplace sa mise à jour intégrée.

## 7. Sauvegardes

- GitHub : le dépôt, avec sa règle de push écrite dans le `CLAUDE.md`.
- Drive (depuis le PC) : crée le dossier miroir du projet, ajoute sa ligne au tableau du `CLAUDE.md` du dossier Drive, écris sa commande de miroir dans le `CLAUDE.md` du projet.

## 8. Finir la première session

Première entrée du journal, décisions notées, plan rempli. Message final : le lien du dépôt, la première version à installer, la prochaine tâche.
