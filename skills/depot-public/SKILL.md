---
name: depot-public
description: Garde-fou « rien de privé » pour un dépôt GitHub public, ou qui va le devenir - contrôle des fichiers, des messages de commit, des images et de l'identité des commits avant de committer ou de pousser. À utiliser avant tout commit ou push dans un dépôt public, quand un dépôt privé passe en public, quand on ajoute une capture d'écran, une photo, un journal de test ou un fichier venu du téléphone, et quand l'utilisateur demande « est-ce qu'il reste quelque chose de privé ? ».
---

# Dépôt public : rien de privé

Ce qui est poussé dans un dépôt public y reste, même après suppression : l'historique le garde. Le contrôle se fait donc **avant** le commit.

## 1. Le dépôt est-il public ?

Regarde sa visibilité avec l'outil GitHub de la session. Dans le doute, traite-le comme public. Un dépôt privé destiné à devenir public se traite comme public dès maintenant : le jour où il change, tout son historique devient visible.

## 2. Ce qui ne part jamais

- une adresse e-mail ;
- le prénom ou le nom d'un tiers (client, proche, collègue) : écris « un tiers », « un client » ;
- le nom d'utilisateur du poste et les chemins qui le contiennent : écris `C:\Users\<utilisateur>\…` ;
- une position réelle : coordonnées, trace GPS, adresse, y compris dans un fichier de test ou une capture ;
- un lien d'invitation, un jeton, une clé d'API, un mot de passe, un lien vers un fichier du Drive ;
- un journal de test brut : il contient des noms d'appareils, des positions, des identifiants ;
- des données réelles de clients dans un jeu d'essai : il se pseudonymise avant ;
- les métadonnées des photos et captures venues du téléphone (EXIF, bloc de fin ajouté par l'appareil) : elles se retirent avant l'ajout.

Cela vaut pour les fichiers, les messages de commit, les titres de pull request et les images.

## 3. Le contrôle

Lance le script fourni avec ce skill depuis la racine du dépôt :

```bash
bash <dossier du skill>/controle.sh                 # ce qui est prêt à être committé
bash <dossier du skill>/controle.sh origin/<branche> # tout ce qui partirait au push
```

Il regarde l'identité des commits, le contenu ajouté (adresses, chemins du poste, clés, liens privés, coordonnées), les fichiers qui n'ont rien à faire dans un dépôt (`.env`, `local.properties`, clés de signature, APK), les images qui portent des métadonnées d'appareil et les messages de commit.

Un signalement n'est pas une preuve : une licence contient des adresses, un fichier de test peut porter des coordonnées inventées. Regarde chaque ligne, corrige ce qui est privé, puis relance jusqu'à « Rien à signaler » ou jusqu'à ce que chaque signalement restant soit expliqué.

## 4. L'identité des commits

Les commits d'un dépôt public portent une adresse `noreply`, jamais l'adresse personnelle.

- Session cloud : l'identité est déjà neutre. Ne la change pas.
- PC : vérifie `git config user.email` avant le premier commit. Si c'est une adresse personnelle, règle l'identité `noreply` **dans ce dépôt** (sans `--global`) :

  ```bash
  git config user.name  "JRRocks"
  git config user.email "314636139+JRRocks@users.noreply.github.com"
  ```

- Les commits créés **par GitHub** prennent l'adresse du compte : commit initial, fichier ajouté à la main, et **fusion d'une pull request, y compris quand c'est l'outil GitHub de la session qui la fusionne**. Le réglage qui protège une fois pour toutes se trouve dans GitHub, *Settings → Emails* : « Keep my email addresses private » et « Block command line pushes that expose my email ». S'il n'est pas actif, dis-le à l'utilisateur.
- Tant que l'utilisateur n'a pas confirmé ce réglage, **ne fusionne pas par GitHub dans un dépôt public** : le commit de fusion porterait son adresse. Fais entrer le travail par un push en avance rapide, avec son accord pour la branche visée. Après une fusion, vérifie l'adresse du commit créé : `git log -1 --format=%ae origin/<branche>`.

## 5. Ce qui est déjà parti

Si une adresse, un nom ou un secret figure déjà dans l'historique poussé :

- un secret (clé, jeton, mot de passe) : préviens tout de suite l'utilisateur, il doit être remplacé ; l'effacer ne suffit pas ;
- le reste : dis ce qui est exposé et où. Réécrire l'historique d'un dépôt ou repartir d'un commit unique est une décision de l'utilisateur, jamais une initiative.

## 6. Les deux dépôts d'un même projet

Quand un projet a un dépôt public et une archive privée : aucun commit ni aucune branche de l'archive ne va dans le dépôt public. Son historique porte ce qu'on a voulu retirer.

## 7. Garder la trace

Note dans le journal du projet que le contrôle a été fait, et ce qu'il a fallu corriger.
