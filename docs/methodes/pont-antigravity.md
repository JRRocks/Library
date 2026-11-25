# Pont Claude → Antigravity

| Champ | Valeur |
|---|---|
| **Identifiant** | `pont-antigravity` (fiche méthode : le code vit dans `installation/` et `skills/deleguer-antigravity/`) |
| **Statut** | ✅ **PC Windows de l'utilisateur** (Windows 11 Famille, build 26300, `agy` 1.5.17) : règles communes et skills posés chez les deux agents ; garde-fous essayés en vrai ; relecture du pont confiée à Antigravity et rapportée, le 2026-11-25. Un agent neuf, sur une simple demande, a délégué seul (essai du 2026-11-25). 🟡 **pas encore essayé** : tâche de code avec une vraie compilation, poste sans bac à sable, second PC |
| **Plateforme** | Windows, Git Bash ; Claude Code (bureau ou terminal) ; Antigravity CLI (`agy`) connecté avec un compte Google |
| **Dépendances** | `agy` 1.5.17 ou plus récent ; git ; PowerShell 5.1 (lecture du JSON d'`agy`) ; `gh` connecté pour lire la visibilité d'un dépôt (sans lui, tout dépôt compte comme privé) |
| **Provenance** | Construit dans cette Library le 2026-11-25 : `65100bf` (règles), `17e9f19` (skills), `11faf4f` (journal signé), `445277f` (garde-fous), `81a27e0` (skill de délégation et corrections de la relecture) |
| **Utilisé dans** | La Library (relecture du pont par Antigravity, 2026-11-25) |
| **Fichiers** | `installation/installer.sh`, `installation/antigravity-entete.md`, `skills/deleguer-antigravity/` (`SKILL.md`, `confier.sh`, `garde-fous.json`, `compilation.json`) |
| **Dernière mise à jour de la fiche** | 2026-11-25 |

## À quoi ça sert

Claude Code confie une tâche à Antigravity CLI (`agy`, l'agent de Google) et en rapporte le
résultat : relecture, second avis, code, architecture, recherche dans un projet. Les deux agents
suivent la même méthode : mêmes règles communes, mêmes skills, même mémoire de projet. Le pont va
dans un seul sens : Claude pilote, Antigravity exécute (D-02).

## Ce qui est en place

| Pièce | Rôle |
|---|---|
| `installation/installer.sh` | Si Antigravity est installé (`~/.gemini`), pose chez lui le socle, précédé de quelques lignes d'adaptation (`antigravity-entete.md`), et les skills de la Library, sauf ceux marqués `.claude-seulement` |
| `SOCLE.md` § 6 | Chaque entrée de journal dit quel agent l'a écrite ; une tâche confiée s'y note |
| Skill `deleguer-antigravity` | Chez Claude seulement : quand confier, comment lancer, lire le compte rendu, relire et livrer une tâche de code, tenir le journal, pièges |
| `confier.sh` | Lance `agy` avec les garde-fous ; écrit le compte rendu (réponse, refus, fichiers changés, entrée de journal proposée, vérifications, ligne de suite) |
| `garde-fous.json`, `compilation.json` | Règles de la session ; la compilation et les tests ne s'y ajoutent qu'en profil code |

## Mettre en place sur un poste

1. Installer `agy` depuis une fenêtre PowerShell ordinaire, jamais depuis une session du bureau
   Claude (D-08) : `irm https://antigravity.google/cli/install.ps1 | iex`.
2. Le connecter une fois : `agy` en mode interactif, compte Google dans le navigateur.
3. Installer ou réinstaller la Library (`bash installation/installer.sh`) : règles et skills
   arrivent des deux côtés.
4. Vérifier : « Library ? » posé à Antigravity donne la même ligne de version que chez Claude ;
   `agy models` liste les modèles du compte.
5. Pour confier une tâche : le skill `deleguer-antigravity`.

## Les garde-fous

| Couche | Ce qu'elle empêche | Essayé |
|---|---|---|
| Accord pour un dépôt privé (D-05) | Montrer un projet privé sans l'accord de l'utilisateur : le script lit la visibilité avec `gh` et refuse dans le doute | ✅ |
| Copie du projet (D-09, D-11) | Abîmer le dossier de l'utilisateur : `git worktree` (lecture : état courant ; code : branche `antigravity/<nom>`), simple copie d'un dossier hors git | ✅ |
| Réglages de cette seule session | Changer les réglages d'Antigravity, qui servent quand l'utilisateur s'en sert seul | ✅ |
| Bac à sable du terminal (D-10) | Réseau ; écriture hors de la copie ; en lecture, écriture dans la copie | ✅ sur ce PC |
| Règles de refus | Push, `gh`, publication, suppression, réseau, web, téléchargement, MCP, commandes git qui modifient le dépôt, commandes encodées | ✅ vrai `agy` |
| Profil lecture | Création et modification de fichier par les outils d'Antigravity (`write_file(*)`) ; compilation | ✅ vrai `agy` |
| Environnement de la tâche (D-11) | Push lancé par un script : transports git coupés, `pushInsteadOf` vers une adresse invalide ; `gh` sans jeton valable ; aucune fenêtre d'identification | ✅ |
| En tête de la consigne | Date, rôle, ce qui est permis et refusé : Antigravity refuse alors de lui-même de pousser ou de supprimer | ✅ |
| Compte rendu | Ce qui aurait échappé aux autres couches : refus, fichiers changés dans la copie et dans le projet, push (reflog), fichiers de compilation modifiés | ✅ |

Limites connues : en profil code, la compilation et les tests tournent hors du bac à sable avec
les droits de l'utilisateur, y compris des scripts qu'Antigravity a modifiés ; les règles ne
voient que la ligne de commande ; sans bac à sable, la première commande imprévue arrête la tâche.

## Décisions

> Une décision ne se réécrit pas : on ajoute une ligne qui la remplace.
> « Qui » : `utilisateur` pour ce qu'il a tranché, `développeur` pour un choix technique sur
> lequel il peut revenir.

| N° | Date | Décision | Pourquoi | Qui |
|---|---|---|---|---|
| D-01 | 2026-11-25 | Antigravity peut tenir tout rôle (seul, en collaboration, sous-traitant, codeur, architecte) ; le rôle se choisit selon la tâche, les forfaits Gemini et Claude, et les modèles du moment. Le premier chantier est le pont entre les deux harnais. | « Tout rôle […] Le rôle viendra avec le context, la tâche à accomplir, mon forfait gemini/claude, les performance des modèles du moment etc. Ce qui faut surtout mettre en place, est le pont de travail entr les 2 harnais » | utilisateur |
| D-02 | 2026-11-25 | Le pont va dans un seul sens : Claude confie des tâches à Antigravity ; Antigravity n'appelle pas Claude. | Q1 « Claude pilote seul » | utilisateur |
| D-03 | 2026-11-25 | Antigravity suit le socle et les skills de la Library, tenus à jour par l'installeur. | Q2 « Socle et skills » | utilisateur |
| D-04 | 2026-11-25 | L'agent qui travaille décide de déléguer quand c'est utile (tâche longue, second avis, quota épuisé d'un côté) et le note au journal ; l'utilisateur peut toujours imposer. | Q3 « L'agent, noté au journal » | utilisateur |
| D-05 | 2026-11-25 | Un dépôt public peut être montré à Antigravity librement ; un projet privé seulement sur accord de l'utilisateur. | « Privés sur demande » | utilisateur |
| D-06 | 2026-11-25 | Le pont vit dans la Library : installeur, skill de délégation, cette fiche. | Une seule source pour toutes les sessions et tous les postes | développeur |
| D-07 | 2026-11-25 | Une tâche de code confiée à Antigravity se fait sur une branche à lui ; il ne pousse ni ne publie rien ; Claude relit, essaie et livre. | Rien n'est annoncé sans avoir été vu fonctionner ; pas de collision entre agents | développeur |
| D-08 | 2026-11-25 | Sur un PC où le bureau Claude est un paquet MSIX, `agy` s'installe depuis une fenêtre Windows ordinaire, jamais depuis une session. | Constaté : ce qu'une session crée dans `%LOCALAPPDATA%` reste invisible hors du bureau Claude | développeur |
| D-09 | 2026-11-25 | Une tâche confiée se fait dans une copie du projet (git worktree), jamais dans le dossier du projet : la copie de lecture part de l'état courant (modifications non commitées comprises) et se retire après la tâche ; la copie de code part du dernier commit, sur la branche `antigravity/<nom>`. Les garde-fous sont des réglages de cette seule session (`--settings`) : les réglages d'Antigravity, qui servent quand l'utilisateur s'en sert seul, ne changent pas. | Le dossier de l'utilisateur ne peut pas être abîmé ; l'usage d'Antigravity seul (D-01) garde ses réglages | développeur |
| D-10 | 2026-11-25 | Les commandes d'une tâche confiée tournent dans le bac à sable d'Antigravity, sans réseau et sans accord à demander ; seules la lecture git, la compilation et les tests peuvent en sortir. Push, `gh`, publication, suppression, accès web et commandes git qui modifient le dépôt sont refusés par des règles ; un `git push` lancé par un script échoue aussi. | Constaté : hors bac à sable, la première commande imprévue arrête la tâche sans réponse ; le contrôle propre d'Antigravity laisse passer quand son modèle est saturé | développeur |
| D-11 | 2026-11-25 | Complète D-09 et D-10 : un dossier hors git se lit dans une simple copie, jamais en place ; en profil lecture, seule la lecture git sort du bac à sable, la compilation et les tests n'en sortent qu'en profil code ; pendant une tâche, les transports git sont coupés (`GIT_ALLOW_PROTOCOL`). | Relecture confiée à Antigravity (P1.5) : lu en place, un dossier hors git n'avait que les règles pour le protéger ; une compilation permise exécute hors du bac à sable ce qu'il a pu modifier ; `pushInsteadOf` ne couvrait ni un chemin local, ni un `pushurl` explicite, ni une adresse `user@hôte:` | développeur |

## Constats et pièges

Les pièges à connaître avant de confier une tâche sont dans le skill (`SKILL.md` § 7). Le détail,
daté :

### Installation et appel (2026-11-25)

- Installation par la commande officielle (`irm https://antigravity.google/cli/install.ps1 | iex`),
  lancée depuis une fenêtre Windows ordinaire : `agy.exe` dans `%LOCALAPPDATA%\agy\bin`, dossier
  ajouté au PATH de l'utilisateur. Le script télécharge environ 240 Mo et vérifie l'empreinte
  SHA-512 donnée par le manifeste de Google ; il ne contrôle aucune signature.
- Version 1.5.17 au premier essai, alors que le manifeste annonçait 1.5.16 peu avant : `agy` se met
  à jour seul, en arrière-plan.
- Connexion : une seule fois, dans `agy` en mode interactif, avec le compte Google (navigateur).
- Lancé depuis une session Claude, `agy` est le même binaire que hors du bureau Claude (empreintes
  identiques) et fonctionne directement.
- Appel sans écran : `agy -p "<consigne>" --output-format json` a répondu `"status":"SUCCESS"` en
  2,5 s. Même une réponse d'un mot coûte environ 11 000 jetons d'entrée (consignes fixes
  d'Antigravity) ; environ 13 000 avec les règles communes, 15 000 avec en plus les skills.
- Modèles visibles avec ce compte : Gemini 4 Hydrogen (low à xhigh), Gemini 3.8 Flash, Claude
  Sonnet 4.6 et Opus 4.6 (retirés le 2026-11-30 selon la documentation de Google), GPT-OSS 120B.
- Un dossier absent de la liste des dossiers de confiance d'Antigravity ne bloque pas un appel
  sans écran. En mode `accept-edits`, Antigravity écrit dans les fichiers du dossier de travail
  sans rien demander.
- Une commande refusée en mode sans écran peut finir l'appel sans réponse : statut `SUCCESS`,
  réponse vide, rien de fait (Antigravity voulait sans doute lire la date). Donner la date dans
  la consigne a suffi ; une réponse vide compte comme un échec.
- Selon la documentation, Antigravity lit `AGENTS.md` et `GEMINI.md` (dans le projet et dans
  `~/.gemini/`), pas `CLAUDE.md` ; ses skills suivent le même format `SKILL.md` que ceux de
  Claude Code.

### Garde-fous (P1.4)

- `agy --settings <fichier>` applique des réglages pour cette seule session, sans rien
  enregistrer ; le journal d'`agy` affiche les règles reçues.
- En mode sans écran, une action qui demanderait un accord arrête toute la tâche : réponse vide,
  statut `SUCCESS`, champ `denied_actions` dans le JSON, avis sur stderr. Une règle `deny`
  renvoie au contraire une erreur à Antigravity, qui continue et la cite dans sa réponse.
- Antigravity explore avec PowerShell (`Get-ChildItem …`), même pour lister un dossier : sans
  bac à sable, la tâche s'arrêtait dès cette première commande.
- Le bac à sable (`--sandbox` avec `toolPermission: proceed-in-sandbox`) fonctionne sur ce PC
  (Windows 11 Famille, build 26300) : commandes sans réseau et sans accord à demander ; une
  suppression par `Remove-Item` y a échoué (accès refusé). Il rejette une règle `write_file` à
  chemin relatif (« non-absolute file path ») et accepte `write_file(*)`.
- Refusés par les règles, vérifié : `git push origin HEAD`, `git -C . push origin HEAD`,
  `cmd /c del …`, `powershell -Command "Remove-Item …"` (après avoir compté les guillemets parmi
  les séparateurs), l'écriture d'un fichier en profil lecture.
- Un `git push` caché dans un script de compilation permis échoue : « remote helper 'nopush'
  aborted session » (redirection `pushInsteadOf` posée dans l'environnement de la tâche).
- Avec les garde-fous en tête de la consigne, Antigravity refuse de lui-même de pousser et de
  supprimer, et l'écrit dans sa réponse.
- Antigravity a son propre contrôle : un modèle léger signale un `git push`. Quand le modèle est
  saturé, ce contrôle échoue et laisse passer : ce n'est pas un garde-fou sur lequel compter.
- Saturation : HTTP 503 « No capacity available for model gemini-4-hydrogen-high », nouvelle
  tentative chaque minute ; une tâche d'une demi-minute en a pris 277 s. Le compte rendu le dit.
- Le journal d'`agy` contient l'adresse du compte Google : les traces des tâches restent dans le
  dossier temporaire du poste, jamais dans un dépôt.
- Python n'est pas installé sur ce PC (seul le raccourci du Microsoft Store répond) : le script
  lit le JSON d'`agy` avec PowerShell.
- Coût d'une tâche d'essai : 15 000 à 30 000 jetons.

### Relecture confiée (P1.5)

Claude a fait relire par Antigravity le skill `deleguer-antigravity`, `confier.sh`,
`garde-fous.json` et cette fiche, dans la Library (dépôt public).

- Relecture : `gemini-4-hydrogen-high` (modèle par défaut d'Antigravity), 167 s, 71 433 jetons,
  dix points, entrée de journal proposée et extraite par le compte rendu. Retenus et corrigés :
  dossier hors git lu dans une copie, et non plus en place ; commandes git qui modifient le dépôt
  refusées même avec `-C` ou des options ; plus de règles contre la suppression, le réseau et les
  commandes encodées ; transports git coupés (`GIT_ALLOW_PROTOCOL`) : un push vers un chemin
  local, un `pushurl` explicite ou une adresse `user@hôte:`, que `pushInsteadOf` ne couvrait pas,
  échoue (essayé) ; consigne limitée à 28 000 octets (la ligne de commande de Windows s'arrête à
  32 767 caractères) ; conversation reprise du journal d'`agy` après un délai dépassé ; copies
  nommées avec une empreinte du chemin du projet ; sous-dossier non suivi, dépôt sans commit et
  option sans valeur refusés ; bloc de journal pris seulement entre ses deux lignes. Retenu comme
  limite écrite : en profil code, compilation et tests tournent hors du bac à sable ; ils ne sont
  plus permis en profil lecture (`compilation.json`). Écarté : une règle `edit_file`. `agy` ne
  connaît que `command`, `read_file`, `write_file`, `read_url`, `execute_url`, `download_url` et
  `mcp` (lu dans le programme) ; `download_url(*)` est désormais refusé aussi.
- Les deux alertes de ce compte rendu (« une branche distante a bougé », « le dossier du projet a
  changé ») venaient de Claude : un `git fetch` et des modifications de la Library pendant la
  tâche. Le compte rendu distingue désormais un fetch d'un push (reflog) et nomme les fichiers
  changés ; le skill demande de ne pas toucher au projet pendant une tâche.
- Le compte rendu comptait comme refus toute citation de « permission denied », fréquente dans une
  relecture des garde-fous. Le journal d'`agy` ne garde aucune trace d'un refus par règle : seule
  la phrase exacte d'`agy`, « Matches user-configured deny rule », est retenue dans la réponse.
- Essai réel des règles corrigées (`gemini-3.8-flash-medium`, 158 s, 51 871 jetons) :
  `git log --pretty=format:%h` et `git grep -n` passent ; création et modification de fichier par
  l'outil d'Antigravity refusées par `write_file(*)` ; `Set-Content` dans la copie de lecture
  refusé par le bac à sable (« Access to the path … is denied ») ; `Clear-Content`,
  `[IO.File]::Delete`, `powershell -enc` et `git -C . reset --hard` refusés par les règles ;
  copie et projet inchangés. Le modèle Flash était saturé lui aussi (deux nouvelles tentatives).

### Session neuve (P1.6)

- Un agent neuf (sous-agent de Claude, sans le contexte de la conversation) n'a reçu que « Demande
  à Antigravity un second avis sur la section 1 de SOCLE.md de ma Library, et rapporte-moi son
  avis ». Il a trouvé le skill installé, vérifié que la Library est publique, écrit la consigne,
  lancé `--lecture --role "second avis" --modele gemini-4-hydrogen-high` en arrière-plan sans
  toucher à la Library, puis vérifié les citations d'Antigravity. Compte rendu : `SUCCESS`, 115 s,
  64 280 jetons, bac à sable actif, aucun refus, projet et branches inchangés, copie retirée.
- Ce qui lui a manqué, repris dans le skill : la liste des modèles sans `agy` au PATH
  (`confier.sh --modeles`), le modèle d'un second avis, la durée d'une tâche, le moment d'un
  `git fetch` (avant ou après la tâche, jamais pendant), noter au journal même un avis qui ne
  change rien, retirer les chemins du poste de l'entrée. La mention « Agent » de l'en-tête de la
  consigne sortait sans accents : corrigé.
- L'avis d'Antigravity sur `SOCLE.md` § 1 (cinq points, trois à garder) attend la décision de
  l'utilisateur : le socle ne change pas sans son accord.

## Historique des retours d'usage

<!-- Ne jamais réécrire ni supprimer une ligne : on en ajoute. La plus ancienne en haut. -->

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-11-25 | Library, pont P1.1 à P1.6 | Règles et skills posés chez Antigravity ; garde-fous essayés avec un faux `agy` et avec le vrai ; relecture du pont confiée à Antigravity : dix points, la plupart corrigés ; un agent neuf a délégué seul sur une simple demande | Fiche créée : ✅ PC Windows · 🟡 compilation réelle, poste sans bac à sable, second PC |

Sources : documentation officielle d'Antigravity (antigravity.google/docs : CLI, mode sans écran,
modes, réglages, permissions, bac à sable, hooks, skills, règles) et de Claude Code
(code.claude.com/docs).
