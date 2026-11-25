# Pont Claude → Antigravity

> **Statut : 🔄 en construction.** Plan écrit le 2026-11-25, feu vert de l'utilisateur le même
> jour (« Oui, commence »). P1.1 à P1.4 sont faits. Cette page deviendra la fiche méthode du pont ; la
> section « Plan » sera retirée quand toutes les tâches seront terminées.

## But

Permettre à Claude Code de confier des tâches à Antigravity CLI (`agy`, l'agent de Google) et
d'en lire le résultat, avec la même méthode de travail des deux côtés : mêmes règles, mêmes
skills, même mémoire de projet.

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

## Plan

> Statuts : ⬜ à faire · 🔄 en cours · ✅ terminé (critère vérifié, vérification écrite au journal) ·
> ⛔ bloqué (avec la raison).

| N° | Tâche | Critère que l'utilisateur peut observer | Statut |
|---|---|---|---|
| P1.1 | L'installeur de la Library écrit aussi les règles d'Antigravity : le socle, précédé de quelques lignes d'adaptation (lire les consignes de chaque projet, `CLAUDE.md` compris ; appliquer à soi ce qui parle de Claude ; signer ses entrées de journal). | « Library ? » posé à Antigravity donne la même ligne de version que chez Claude. | ✅ 2026-11-25 (journal) |
| P1.2 | L'installeur copie les skills de la Library chez Antigravity (sauf le skill de délégation, inutile de son côté). | Antigravity nomme les skills de la Library quand on lui demande lesquels il a. | ✅ 2026-11-25 (journal) |
| P1.3 | Une ligne ajoutée au socle : chaque entrée de journal dit quel agent l'a écrite. | La prochaine entrée de journal d'un projet porte « Claude » ou « Antigravity ». | ✅ 2026-11-25 (journal) |
| P1.4 | Garde-fous d'Antigravity quand Claude lui confie une tâche : il lit, modifie et compile ; il ne pousse, ne publie ni ne supprime rien ; pour du code, il travaille sur sa branche. | Essai : une consigne qui lui demande de publier est refusée, et Claude le montre. | ✅ 2026-11-25 (journal) |
| P1.5 | Skill `deleguer-antigravity` et son script : rôle, modèle, suite d'une conversation, refus d'un dépôt privé sans accord, compte rendu prêt pour le journal. | Claude confie une vraie relecture sur un dépôt public : l'avis d'Antigravity arrive dans le message final et une entrée « Antigravity » apparaît au journal du projet. | ⬜ |
| P1.6 | Cette page devient la fiche méthode (pièges compris) ; INDEX, JOURNAL et tableau des fiches à jour ; push. | Une nouvelle session délègue sans que l'utilisateur réexplique. | ⬜ |

## Constats du 2026-11-25

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

Garde-fous (P1.4). Ils sont portés par `skills/deleguer-antigravity/confier.sh` et ses règles
`garde-fous.json` ; le skill qui dit quand et comment s'en servir vient en P1.5.

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

Sources : documentation officielle d'Antigravity (antigravity.google/docs : CLI, mode sans écran,
modes, réglages, permissions, bac à sable, hooks, skills, règles) et de Claude Code
(code.claude.com/docs).
