# Installer la Library dans toutes mes sessions

L'installation pose dans Claude Code, pour **chaque** session, quel que soit le projet :

- les **règles communes** ([`SOCLE.md`](../SOCLE.md)), chargées au démarrage ;
- les **skills** du dossier [`skills/`](../skills/) (ceux de `skills/_propositions/` attendent mon accord et ne sont pas installés) ;
- une **copie de la Library** dans `~/.claude/library/` : catalogue des moteurs, fiches méthode, dossier UI/UX.

Rien n'est à refaire projet par projet.

## Vérifier qu'elle est en place

Dans une nouvelle session, écrire **« Library ? »** : Claude répond en une ligne avec la version installée et le nombre de skills. Dans une session cloud, la commande `/context` montre aussi `/root/.claude/CLAUDE.md` parmi les fichiers de mémoire.

## Sessions cloud (téléphone, navigateur)

Le contenu de [`environnement-cloud.sh`](environnement-cloud.sh) va dans le **script de démarrage** de l'environnement cloud : menu de l'environnement dans la barre de titre d'une session, puis *Edit*, puis *Setup script*. Une session peut aussi le proposer elle-même : une carte d'approbation s'affiche, il suffit d'accepter.

- À faire une fois par environnement (chaque environnement a son propre script).
- Le script tourne au démarrage de la première session, puis son résultat est gardé en cache environ sept jours. Un changement de la Library arrive donc dans les nouvelles sessions sous sept jours. Pour l'avoir tout de suite : changer le numéro de version écrit dans le script, ce qui force la réinstallation.
- Une session déjà ouverte garde ce qu'elle avait à son démarrage.
- Le dépôt est public : aucun identifiant n'est demandé. Il faut seulement que l'environnement laisse passer GitHub (c'est le cas du réglage par défaut).
- Le script ne peut pas empêcher une session de démarrer : en cas d'échec, la session démarre sans la Library.

### Où c'est en place

| Environnement cloud | Script de démarrage | Constaté |
|---|---|---|
| « Drive » | Posé le 2026-11-09, version 2026-11-09.3 | Oui, par une session neuve à chaque version (dix skills, puis onze, puis douze) |
| Les autres environnements | Pas encore | |

## PC Windows (Git Bash)

La première fois :

```bash
git clone https://github.com/JRRocks/Library.git ~/.claude/library
bash ~/.claude/library/installation/installer.sh
```

Pour mettre à jour :

```bash
git -C ~/.claude/library pull && bash ~/.claude/library/installation/installer.sh
```

Le plus simple : demander à Claude, sur le PC, « installe ma Library » ou « mets à jour ma Library ».

## Ce que l'installation touche, et rien d'autre

| Endroit | Ce qui est fait |
|---|---|
| `~/.claude/CLAUDE.md` | Un bloc délimité par deux repères `LIBRARY` reçoit les règles communes. Le reste du fichier est conservé. S'il existait déjà, une copie `CLAUDE.md.avant-library` est gardée la première fois |
| `~/.claude/skills/<nom>/` | Un dossier par skill, marqué d'un fichier `.library`. Un skill retiré de la Library disparaît à l'installation suivante. Un skill du même nom posé autrement n'est jamais écrasé |
| `~/.claude/library/` | Le clone de la Library |

Pour tout retirer : `bash ~/.claude/library/installation/installer.sh --retirer`, puis supprimer le script de l'environnement cloud.

## Activer un skill proposé

Déplacer son dossier de `skills/_propositions/` vers `skills/`, faire entrer le changement dans `main`, puis réinstaller (PC) ou changer le numéro de version du script (cloud).

## Ce qui est vérifié, et ce qui ne l'est pas

| Point | État |
|---|---|
| `installer.sh` sous Linux : poste vierge, fichier personnel existant, relances, skill retiré, repères abîmés, fichier vide, fins de ligne Windows, retrait | Vérifié en session le 2026-11-09 |
| Chargement de `~/.claude/CLAUDE.md` écrit par le script de démarrage d'une session cloud | Documenté par Claude Code (« Add personal preferences without committing to the repo »). **Constaté le 2026-11-09** : une session neuve a répondu à « Library ? » avec la version installée, sans utiliser d'outil |
| Chargement des skills de `~/.claude/skills/` dans une session cloud | **Constaté le 2026-11-09** dans la même session neuve : les dix skills installés y sont disponibles, les deux propositions n'y sont pas. Non documenté par Claude Code |
| Clone d'un dépôt public non joint à la session | **Constaté le 2026-11-09** pendant le script de démarrage, dans une session neuve sans aucun dépôt joint |
| `installer.sh` sous Git Bash (Windows) | Pas encore essayé : commandes courantes seulement (`cp`, `rm`, `awk`, `grep`) |
