---
name: copie-recente
description: Trouver la copie la plus récente d'un projet qui vit à plusieurs endroits (GitHub et toutes ses branches, miroir Google Drive, dossier du PC), mettre les autres à jour sans rien écraser, puis refaire le miroir Drive et ranger l'APK en fin de travail sur le PC. À utiliser en début de session avant de lire le plan ou de modifier un fichier, avant tout miroir Drive, et quand l'utilisateur dit « mets à jour », « synchronise », « fais le miroir », « récupère le travail de la session cloud » ou « quelle est la dernière version ».
---

# Partir de la copie la plus récente

Un projet vit à trois endroits qui prennent de l'avance l'un sur l'autre (deux PC, des sessions cloud) :

| Copie | Où | Ce qui la fait avancer |
|---|---|---|
| GitHub | le dépôt, **toutes ses branches** | les sessions cloud, les push du PC |
| Local | le dossier du projet sur le PC | le travail sur ce PC |
| Drive | le dossier miroir du projet | le miroir lancé depuis un PC |

Les chemins exacts sont dans le `CLAUDE.md` du projet et dans le `CLAUDE.md` à la racine du dossier Drive des projets. Ne les devine pas.

## 1. Relever la version de chaque copie

La version se lit dans le fichier de version du projet (`version.properties`, `versionName` du `build.gradle.kts`, `package.json`). À version égale : le titre de la dernière entrée du journal, puis la date du dernier commit.

- **GitHub** : `git fetch origin`, puis pour chaque branche de `git branch -r` (sauf `dependabot/…`) : `git show origin/<branche>:<fichier de version>`. Une session cloud précédente a pu laisser son travail sur sa propre branche : ne regarde pas seulement `main`.
- **Local** (PC) : le fichier de version du dossier, et `git status`. Des changements non committés ou non poussés font du local la copie la plus récente.
- **Drive** (PC) : le fichier de version du dossier miroir.

En session cloud, seule la copie GitHub est à portée : relève quand même toutes les branches avant de commencer.

## 2. Mettre les autres à niveau

La copie à la version la plus haute fait référence.

- Local en retard : copie de secours des fichiers modifiés, puis `git fetch` et `git checkout -B <branche> origin/<branche>`. Les fichiers supprimés en amont ne s'enlèvent (`git clean`) qu'après vérification.
- Drive en retard : miroir (§ 4), une fois le local à jour.
- GitHub en retard : push, selon la règle de push du projet.

## 3. Ce qui est interdit

- Écraser une copie plus récente par une plus ancienne. En particulier : jamais de miroir vers le Drive tant que la version du Drive dépasse celle du local.
- Modifier un fichier directement sur le Drive, ou lancer le miroir dans l'autre sens.
- Trancher seul quand les copies **divergent** (même version et contenus différents, ou changements des deux côtés) : n'écrase rien, dis-le à l'utilisateur et propose-lui le choix (garder l'une, garder l'autre, fusionner).

## 4. Miroir Drive et APK (PC seulement)

Le Drive est monté en lecteur par Google Drive pour ordinateur. La lettre se vérifie (`Get-PSDrive`), et la racine s'appelle `Mon Drive` sur un PC, `My Drive` sur l'autre (`Test-Path "G:\My Drive"`).

```powershell
# Miroir du projet : du PC vers le Drive, jamais l'inverse
robocopy "<dossier local du projet>" "<lecteur>:\<racine Drive>\<dossier des projets>\<Projet>" /MIR /XD .git .gradle .kotlin build .idea node_modules __pycache__ .pytest_cache .venv venv /XF .env local.properties keystore.properties *.apk /R:1 /W:1

# APK : un seul dossier pour tous les projets, un nom par version
Copy-Item "<apk compilé>" "<lecteur>:\<racine Drive>\<dossier des projets>\APK release\<NomApp>.<version>.apk"
```

- `/MIR` supprime côté Drive ce qui n'existe plus en local : relis la destination avant de lancer.
- Codes de sortie de robocopy : 0 à 7 = réussi, 8 et plus = échec.
- Les APK vont dans `APK release/`, jamais dans le dossier miroir. La version monte à chaque APK, pour qu'aucun n'en écrase un autre.
- Si le projet a son propre script de miroir, utilise-le : il porte ses garde-fous.

En session cloud, pas de miroir : le connecteur Google Drive ne sait ni envoyer un APK, ni remplacer un fichier existant. Dis dans le message final que le miroir reste à faire à la prochaine session sur le PC.

## 5. Garder la trace

- Dans le journal du projet : la version de chaque copie relevée, et celle qui faisait référence.
- Dans le message final : le résultat du miroir, ou qu'il reste à faire.

## Nouveau projet

Crée son dossier miroir sur le Drive, ajoute sa ligne au tableau du `CLAUDE.md` du dossier Drive, et écris sa commande de miroir dans le `CLAUDE.md` du projet.
