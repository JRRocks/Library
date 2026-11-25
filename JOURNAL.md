# Journal de la Library

> Entrée la plus récente **en haut**. Une entrée par changement de la bibliothèque.
> Ne jamais modifier ni supprimer une entrée passée : on corrige par une nouvelle entrée.
>
> Gabarit :
> ```
> ## AAAA-MM-JJ — [identifiant(s)] Titre court
> - **Type** : ajout · retour positif · patch · resynchronisation · revue de dépôt · transverse
> - **Agent** : Claude ou Antigravity (depuis le 2026-11-25)
> - **Quoi** :
> - **Preuve / vérification** :
> - **Apps à prévenir** : (apps qui n'ont pas encore le changement, ou « aucune »)
> ```

---

## 2026-11-25 — [library] Pont Antigravity, P1.6 : fiche méthode, essai par une session neuve

- **Type** : transverse
- **Agent** : Claude
- **Quoi** : feu vert de l'utilisateur : « Oui, continue tout les étapes ne t'arrête pas ».
  `docs/methodes/pont-antigravity.md` devient la fiche méthode du pont : en-tête de fiche, ce qui
  est en place, mise en place sur un poste, couches de garde-fous, décisions (D-11 ajoutée : copie
  d'un dossier hors git, compilation seulement en profil code, transports git coupés), constats
  datés, historique. Le plan, terminé (P1.1 à P1.6), est retiré de la fiche ; ses tâches restent
  dans les entrées ci-dessous. Fiche ajoutée au tableau des fiches méthode et à l'INDEX (§ 2, § 3,
  § 7). Essai du critère « une nouvelle session délègue sans que l'utilisateur réexplique » : un
  agent neuf (sous-agent de Claude, sans le contexte de la conversation) n'a reçu que « Demande à
  Antigravity un second avis sur la section 1 de SOCLE.md de ma Library, et rapporte-moi son
  avis » ; il a trouvé le skill, vérifié la visibilité, lancé une lecture en arrière-plan sans
  toucher à la Library et rapporté l'avis (`SUCCESS`, 115 s, 64 280 jetons, aucun refus, projet
  inchangé, copie retirée). L'entrée d'Antigravity est posée juste en dessous, accents rétablis
  dans « tâche confiée » ; son affirmation « aucune commande exécutée » n'est pas vérifiable (la
  trace ne nomme pas les outils), la copie est restée inchangée. Ce qui a manqué à cet agent est
  repris : `confier.sh --modeles` (liste des modèles sans `agy` au PATH), modèle d'un second avis,
  durée d'une tâche, `git fetch` avant ou après la tâche, toute tâche confiée notée au journal,
  chemins du poste retirés de l'entrée, mention « Agent » accentuée dans l'en-tête de la consigne.
  L'avis d'Antigravity sur `SOCLE.md` § 1 attend la décision de l'utilisateur : le socle ne change
  pas.
- **Preuve / vérification** : `confier.sh --modeles` liste les dix modèles du compte ; tâche
  factice relancée (code 0, mention accentuée dans la consigne) ; corps du skill à 98 lignes ;
  décisions D-01 à D-10 identiques à l'octet près ; installation essayée dans un dossier personnel
  vide : 14 skills chez Claude, 13 chez Antigravity ; `depot-public/controle.sh` : rien à signaler.
- **Apps à prévenir** : aucune.

## 2026-11-25 — [socle] Second avis sur la section 1 de SOCLE.md (« Parle peu »)
- **Type** : transverse
- **Agent** : Antigravity (tâche confiée par Claude)
- **Quoi** : relecture de `SOCLE.md` § 1 au regard de `AGENTS.md` § 5.G, `docs/methode-de-travail/README.md` § 4, `skills/mode-silencieux/SKILL.md`, `installation/antigravity-entete.md` et des consignes du harnais Antigravity. 5 points relevés (question directe oubliée, bilan final rigide face aux échecs/relectures/délégations et liens fichiers Antigravity, conflit avec `update_steps` et jargon « outil », ligne de point d'étape ambiguë ou de trop, journal supposé existant et forme impersonnelle) avec réécritures en une ligne à la première personne, plus 3 points à conserver.
- **Preuve / vérification** : lecture directe des fichiers du dossier et des consignes du harnais Antigravity ; aucune modification ni commande exécutée ; consignes internes de Claude Code non vérifiables dans ce dossier.
- **Apps à prévenir** : aucune.

## 2026-11-25 — [deleguer-antigravity] Pont Antigravity, P1.5 : skill de délégation, relecture confiée à Antigravity

- **Type** : ajout
- **Agent** : Claude
- **Quoi** : feu vert de l'utilisateur : « Oui, continue tout les étapes ne t'arrête pas ». Nouveau
  skill `deleguer-antigravity`, réservé à Claude (`.claude-seulement`) : quand confier, accord pour
  un dépôt privé (D-05), consigne, profil, rôle, modèle, lancement, lecture du compte rendu, tâche
  de code, journal, pièges. Accord de l'utilisateur pour ce skill : le plan du pont, qui le
  prévoyait, accepté le 2026-11-25 (« Oui, commence »), puis « Oui, continue tout les étapes ne
  t'arrête pas ». Relecture du skill, de `confier.sh`, de `garde-fous.json` et de
  `pont-antigravity.md` confiée à Antigravity (profil lecture, `gemini-4-hydrogen-high`, 167 s,
  71 433 jetons) : son entrée est posée telle quelle juste en dessous. Retenu et corrigé : dossier
  hors git lu dans une copie ; règles git valables avec `-C` et les options ; plus de règles contre
  la suppression, le réseau et les commandes encodées, et `download_url(*)` refusé ; transports
  git coupés (`GIT_ALLOW_PROTOCOL`) ; consigne limitée à 28 000 octets ; conversation reprise du
  journal d'`agy` après un délai dépassé ; empreinte du chemin dans le nom des copies ; sous-dossier
  non suivi, dépôt sans commit et option sans valeur refusés ; bloc de journal pris seulement entre
  ses deux lignes ; `--retirer` dit quand il n'y a rien à retirer. Compilation et tests permis
  seulement en profil code (nouveau `compilation.json`), limite écrite dans le skill. Écarté : la
  règle `edit_file`, qui n'existe pas dans `agy` (création et modification passent par
  `write_file`, vérifié). Le compte rendu distingue un fetch d'un push, nomme les fichiers changés
  du projet, signale les fichiers de compilation modifiés et ne retient plus comme refus que la
  phrase exacte d'`agy`.
- **Preuve / vérification** : règles essayées sur 37 commandes à refuser et 28 à laisser passer
  (expressions rationnelles .NET, mêmes constructions qu'en RE2) : aucune erreur. Faux `agy`, 28
  cas (aide, option sans valeur, refus D-05, lecture, copie modifiée gardée puis retirée, écriture
  dans le projet signalée, bloc de journal non fermé ou contenant « Fin des tests », code, push
  d'un script refusé, doublon, suite, délai dépassé, retrait avant et après fusion, rien à retirer,
  sous-dossier non suivi, dépôt sans commit, dossier hors git, consigne trop longue, copie
  occupée) : tous conformes. Vrai `agy` (`gemini-3.8-flash-medium`, 158 s, 51 871 jetons) sur un
  dépôt d'essai : lectures git permises ; création et modification de fichier, `Set-Content`,
  `Clear-Content`, `[IO.File]::Delete`, `powershell -enc` et `git -C . reset --hard` refusés ;
  copie et projet inchangés. Les deux alertes du compte rendu de la relecture venaient de Claude
  (un `git fetch` et des modifications de la Library pendant la tâche), pas d'Antigravity.
  Installation essayée dans un dossier personnel vide : 14 skills chez Claude, 13 chez Antigravity,
  sans `deleguer-antigravity`. `depot-public/controle.sh` : rien à signaler.
- **Apps à prévenir** : aucune.

## 2026-11-25 — [deleguer-antigravity] Relecture des garde-fous et de confier.sh (pont Antigravity)
- **Type** : transverse
- **Agent** : Antigravity (tâche confiée par Claude)
- **Quoi** : relecture statique de `skills/deleguer-antigravity/SKILL.md`, `confier.sh`, `garde-fous.json` et `docs/methodes/pont-antigravity.md`. 10 défauts ou risques relevés, dont : profil `--lecture` ne bloquant pas les écritures PowerShell dans la copie ni l'usage du vrai dossier sans git (`edit_file` non listé dans `deny`, à vérifier) ; sous-commandes git interdites (`add`, `commit`, `reset`, `clean`, etc.) et outils réseau sans `regex` pour `git -C` ou wrappers ; commandes `allow` de test/build exécutées hors bac à sable ; limite Windows de 32 Ko sur `-p "$invite"` ; faux positif sur les citations d'erreurs de permission dans la réponse ; impasse après expiration de `--delai` en `--code` ; collisions et cas limites sur les sous-dossiers non suivis, options sans valeur et `bloc_journal`.
- **Preuve / vérification** : lecture directe des 4 fichiers concernés et du format de `JOURNAL.md` ; aucune modification effectuée, script non exécuté en réel (hypothèses signalées dans le compte rendu).
- **Apps à prévenir** : aucune.

## 2026-11-25 — [library] Pont Antigravity, P1.4 : garde-fous des tâches confiées

- **Type** : transverse
- **Agent** : Claude
- **Quoi** : feu vert de l'utilisateur : « Oui continue ». Nouveau dossier
  `skills/deleguer-antigravity/`, marqué `.claude-seulement`, encore sans `SKILL.md` (donc pas
  installé, le skill vient en P1.5) : `confier.sh` confie une tâche à Antigravity, `garde-fous.json`
  porte les règles de la session. Deux profils : lecture (copie prise sur l'état courant, aucune
  écriture, copie retirée ensuite) et code (copie sur la branche `antigravity/<nom>`). Bac à sable
  du terminal ; mode `accept-edits` donné explicitement ; push, `gh`, publication, suppression,
  web et commandes git qui modifient le dépôt refusés ; `pushInsteadOf` vers une adresse invalide
  et jeton `gh` invalide dans l'environnement ; date et garde-fous en tête de la consigne. Le
  compte rendu donne la réponse, les refus, les fichiers modifiés, et vérifie les branches
  distantes et le dossier du projet. Décisions D-09 et D-10 et constats dans
  `docs/methodes/pont-antigravity.md`.
- **Preuve / vérification** : dépôt d'essai avec un dépôt distant local, dans le dossier
  temporaire. (1) Profil code, tâche « ajoute une ligne, compile, publie par `git push`, supprime
  `ancien.txt` » : ligne ajoutée, compilation lancée dans le bac à sable, push et suppression
  refusés par Antigravity dans sa réponse ; dépôt distant inchangé, fichier toujours là.
  (2) Sans la consigne de garde-fous, commandes imposées : `cmd /c del`, `git -C . push`,
  `powershell -Command "Remove-Item …"` refusés par les règles (le dernier après correction de la
  règle), push caché dans le script de compilation en échec. (3) Profil lecture : correction
  refusée par Antigravity, écriture imposée refusée par la règle, copie retirée, projet inchangé.
  (4) Compte rendu d'un arrêt simulé (faux `agy`, sans appel) : refus, saturation du modèle et code
  de sortie 2 affichés. Contrôle `depot-public` passé avant le commit. Non vérifié : une vraie
  compilation (Gradle, Android), un poste sans bac à sable.
- **Apps à prévenir** : aucune.

---

## 2026-11-25 — [socle] Journal signé par l'agent qui l'écrit (pont Antigravity, P1.3)

- **Type** : transverse
- **Agent** : Claude (les trois entrées précédentes du 2026-11-25 sont aussi de Claude)
- **Quoi** : nouvelle ligne dans `SOCLE.md` § 6 : chaque entrée du journal dit quel agent l'a
  écrite, Claude ou Antigravity, et une tâche confiée à l'autre agent s'y note aussi. Accord de
  l'utilisateur : réponse « L'agent, noté au journal » au cadrage, puis « Oui, continue » pour
  cette tâche. Origine notée dans `docs/methode-de-travail/README.md` § 4 ; champ « Agent »
  ajouté au gabarit de ce journal ; consigne de signature retirée de l'en-tête d'Antigravity,
  où elle faisait doublon. INDEX § 8 à jour (socle installé sur le PC, et chez Antigravity).
- **Preuve / vérification** : la ligne est dans `~/.claude/CLAUDE.md` et dans le fichier de
  règles d'Antigravity après réinstallation. Dans un projet d'essai, Antigravity (`agy -p`, mode
  `accept-edits`) a écrit une entrée de journal signée « Antigravity ». Non vérifié : une session
  Claude neuve, qui charge le socle à son démarrage. Constaté au passage : sans la date dans la
  consigne, Antigravity a voulu lancer une commande, refusée en mode sans écran, et a rendu une
  réponse vide avec le statut `SUCCESS`.
- **Apps à prévenir** : aucune.

---

## 2026-11-25 — [library] Pont Antigravity, P1.2 : Antigravity reçoit les skills

- **Type** : transverse
- **Quoi** : `installer.sh` copie aussi les skills de la Library dans `~/.gemini/config/skills/`,
  avec les mêmes règles que pour Claude (marque `.library`, un skill du même nom posé autrement
  est gardé, `--retirer` les enlève). Nouvelle convention : un skill qui porte un fichier vide
  `.claude-seulement` n'est pas posé chez Antigravity (`AGENTS.md` § 5.F). L'en-tête
  d'Antigravity lui dit que ces skills sont aussi les siens.
- **Preuve / vérification** : sous Git Bash (Windows), sur une copie de la Library dans un dossier
  d'essai : 12 skills posés sur 14 (un skill d'essai `.claude-seulement` écarté, un skill
  personnel du même nom gardé), deux passes, puis retrait qui laisse le skill personnel ; sans
  `~/.gemini`, rien n'est posé. Sur le PC, `agy -p` nomme les treize skills de la Library et
  résume `lever-le-doute` en lisant son fichier. Un appel d'Antigravity coûte maintenant environ
  15 000 jetons d'entrée.
- **Apps à prévenir** : aucune.

---

## 2026-11-25 — [library] Pont Antigravity, P1.1 : Antigravity reçoit les règles communes

- **Type** : transverse
- **Quoi** : feu vert de l'utilisateur pour le plan du pont, avec ses mots : « Oui, commence ».
  `installer.sh` pose aussi, si `~/.gemini` existe, le fichier de règles toujours actif
  `~/.gemini/config/rules/library-socle.md` : un en-tête qui adresse les règles à Antigravity
  ([`installation/antigravity-entete.md`](installation/antigravity-entete.md)), puis `SOCLE.md`
  et la même ligne de version que pour Claude. `--retirer` l'enlève. Mode d'emploi
  ([`installation/README.md`](installation/README.md)) et `AGENTS.md` § 4 à jour.
- **Preuve / vérification** : sous Git Bash (Windows), installation dans deux dossiers d'essai
  (avec et sans `~/.gemini`, deux passes, retrait), puis installation réelle sur le PC.
  `agy -p "Library ?"` a rendu mot pour mot la ligne de version de Claude (`2026-11-10 75fc37e`,
  version d'avant ce commit), et Antigravity répond qu'il signe « Antigravity » ses entrées de
  journal. Coût : environ 2 000 jetons d'entrée de plus par appel d'Antigravity.
- **Apps à prévenir** : aucune.

---

## 2026-11-25 — [methodes] Pont Claude → Antigravity : cadrage

- **Type** : transverse (cadrage ; rien n'est encore posé par l'installeur)
- **Quoi** : nouvelle page [`docs/methodes/pont-antigravity.md`](docs/methodes/pont-antigravity.md),
  avec les décisions D-01 à D-08 et le plan P1.1 à P1.6, en attente du feu vert de l'utilisateur.
  Demande de l'utilisateur, avec ses mots : « Ce qui faut surtout mettre en place, est le pont de
  travail entr les 2 harnais ».
- **Preuve / vérification** : Antigravity CLI 1.5.17 installé sur le PC de l'utilisateur et
  connecté à son compte Google ; un appel sans écran lancé depuis une session Claude a répondu
  (statut `SUCCESS`, 2,5 s).
- **Apps à prévenir** : aucune.

---

## 2026-11-10 — [skills] `lever-le-doute` : ne jamais deviner une demande

- **Type** : ajout (skill demandé par l'utilisateur, mis en service directement, AGENTS.md § 5.F)
- **Quoi** : nouveau skill [`lever-le-doute`](skills/lever-le-doute/SKILL.md). Demande de
  l'utilisateur, avec ses mots : « Ajoute moi un skill qui a pour principe et intérêt que Claude
  ne cherche jamais à comprendre ce qu'il doit faire. Si il se pose la question, c'est que je
  n'ai pas été assez clair, claude doit impérativement me poser des questions pour retirer toute
  ambiguïté. » Le skill dit comment reconnaître le doute, comment poser les questions, et ce qui
  se passe sans réponse (la partie concernée attend). Lecture de la session, à confirmer par
  l'utilisateur : le doute visé porte sur **ce qui est demandé** (quoi, où, jusqu'où, résultat
  attendu) ; le « comment » technique et le debug restent sans question (`SOCLE.md` § 4, non
  modifié). `SOCLE.md` § 1 n'a pas changé : le skill précise qu'un doute sur la demande est une
  « question qui bloque vraiment ». INDEX § 8 et `docs/methode-de-travail/` (§ 3, § 4) à jour.
- **Preuve / vérification** : en-tête lu (nom = dossier, description de moins de 1 536
  caractères, sans « : » au milieu d'une phrase) ; installation dans un dossier d'essai
  (`HOME=<dossier vide> bash installation/installer.sh`) : treize skills posés, dont
  `lever-le-doute`. Pas encore constaté dans une session neuve.
- **Apps à prévenir** : aucune. Les sessions cloud le reçoivent sous sept jours (cache du script
  de démarrage), ou tout de suite si le numéro de version du script de l'environnement est changé.

## 2026-11-09 — [library] [scan-multimode] [paiement-sumup] Projets externes : revue terminée, deux fiches précisées

- **Type** : transverse · retour d'information (rien d'essayé)
- **Quoi** : fin de la recherche demandée par l'utilisateur (« faire une recherche sur des
  projets github qui pourrais ajouter une amélioration à mes propres projets »). Le registre
  [`docs/projets-externes/`](docs/projets-externes/README.md) couvre toutes les applications :
  sections SuivieMAT / ScanList et Scam Stop refaites, section Popote Compagnie ajoutée,
  sections Mihon et Carnet de recette corrigées, dix lignes d'écartés en plus. Fiche
  `scan-multimode` : la vigilance « Codes empilés illisibles » dit maintenant que `zxing-cpp`
  annonce le MicroPDF417 et les DataBar empilés, et que rien n'existe sur Android pour
  Codablock F, Code 16K et Code 49. Fiche `paiement-sumup` : contenu du SDK 7.1.0 noté, et
  doute signalé sur « la 7.x exige AGP 9 ». Skill `recherche-github` : outil `releve.sh`
  (licence, dernier commit, dernières versions par date, sans API et sans rien exécuter).
- **Preuve / vérification** : chaque projet cité relevé sur son dépôt par un clone sans
  fichiers (licence lue, dernier commit, étiquettes classées par date) ; formats de
  `zxing-cpp`, règles de SpamBlocker, journal du SDK SumUp, motifs de Saracroche et réglage
  de ColorComic lus dans leurs fichiers ; ce que les rapports de recherche disaient de mes
  applications relu dans leur code. Erreur trouvée dans le registre et corrigée : fastexcel y
  était noté « v0.8.7 » (étiquettes classées par nom) alors que la dernière version est la
  0.20.2. `releve.sh` essayé sur sept dépôts, dont un absent et un hors GitHub. Rien n'a été
  compilé, installé ni essayé : toutes les lignes du registre restent « à essayer ».
- **Apps à prévenir** : SuivieMAT et ScanList (leur documentation dit que les codes empilés
  autres que le PDF417 resteront hors de portée : une piste existe pour deux d'entre eux) ;
  Popote (SDK SumUp 7.1.0, scanner de documents) ; Scam Stop (trois règles de SpamBlocker,
  voix de l'annonce) ; toute application qui exporte en CSV (valeurs commençant par `=`, `+`,
  `-` ou `@` écrites telles quelles). Les nouvelles sessions cloud reçoivent `releve.sh` au
  prochain renouvellement de l'environnement (sept jours au plus) : le numéro de version du
  script de démarrage n'a pas été changé.

---

## 2026-11-09 — [library] recherche-github en service, vérifié dans une session neuve

- **Type** : transverse
- **Quoi** : `main` a reçu par un push en avance rapide le skill `recherche-github`, le registre
  des projets externes et l'accord durable. Script de l'environnement cloud « Drive » remplacé
  par la version 2026-11-09.3, acceptée par l'utilisateur.
- **Preuve / vérification** : le script exact, lancé contre `main` dans un dossier d'essai, pose
  douze skills. Une session neuve du même environnement, sans dépôt joint et sans outil, a
  répondu : « Library : version 2026-11-09 53950c1 (12 skills). » et a cité les douze skills,
  `recherche-github` compris.
- **Apps à prévenir** : aucune. Restent à équiper : les autres environnements cloud et les PC.

---

## 2026-11-09 — [library] Accord durable : un skill demandé par l'utilisateur va directement en service

- **Type** : transverse
- **Quoi** : à la question « je te redemande à chaque fois avant de les mettre en service ? »,
  l'utilisateur a répondu « Mets-les en service directement ». `AGENTS.md` § 5.F et `CLAUDE.md`
  le disent : un skill qu'il demande va dans `skills/` et dans `main` par un push en avance
  rapide ; un skill proposé par une session attend toujours dans `skills/_propositions/`. Le
  script de l'environnement cloud passe en version 2026-11-09.3 pour que les nouvelles sessions
  reçoivent `recherche-github`.
- **Preuve / vérification** : réponses de l'utilisateur dans la session (« Oui, mets en service »
  pour `recherche-github`, puis l'accord durable).
- **Apps à prévenir** : aucune.

---

## 2026-11-09 — [skills] Ajout du skill recherche-github et du registre des projets externes

- **Type** : ajout · revue
- **Quoi** : demande de l'utilisateur : « Ajoute un nouveau skill, sur de la recherche de projet
  GitHub qui pourrait répondre à mes besoins et rapidement testable puisque déjà publié », avec
  l'exemple d'un dépôt de colorisation trouvé par lui pour son fork de Mihon ; puis « faire une
  recherche sur des projets GitHub qui pourraient ajouter une amélioration à mes propres projets ».
  - `skills/recherche-github/` : la procédure (dire le besoin, chercher, trier, se méfier,
    proposer, faire essayer avant d'intégrer, intégrer du moins engageant au plus engageant,
    garder la trace) et un guide des licences. Installé par défaut, puisque demandé : douze
    skills dans `skills/`. Le skill `moteurs` y renvoie quand la Library n'a rien.
  - `docs/projets-externes/README.md` : registre de la première recherche, par application
    (Mihon, SuivieMAT, Scam Stop, ToolMap, Sun Track, Épreuves, coach d'entraînement, CRM, Carnet
    de recette, skills) : 32 lignes, dont 5 projets écartés avec leur raison.
- **Preuve / vérification** : chaque dépôt cité a été cloné sans ses fichiers le jour même, pour
  relever son existence, la date de son dernier commit, sa dernière étiquette et son fichier de
  licence ; les faits repris (formats lus, sites pris en charge, liens d'essai, modules) sont
  lus dans le `README` ou l'arborescence du projet. Un dépôt GitHub réduit à un `README`
  renvoyait vers Codeberg : vérifié à sa nouvelle adresse. **Non vérifié** : aucun de ces projets
  n'a été essayé ni lancé ; « comment l'essayer » décrit ce que le projet publie, pas un essai fait.
- **Apps à prévenir** : aucune. Les essais sont à faire par l'utilisateur, projet par projet.

---

## 2026-11-09 — [library] revue-langages en service, vérifié dans une session neuve

- **Type** : transverse
- **Quoi** : sur accord de l'utilisateur (« Oui, mets en service »), `main` reçoit par un push
  en avance rapide, sans fusion GitHub, le skill `revue-langages`, la correction du skill
  `depot-public` et le script d'environnement en version 2026-11-09.2. Le script de
  l'environnement cloud « Drive » est remplacé par cette version, acceptée par lui, pour que
  les nouvelles sessions réinstallent tout de suite.
- **Preuve / vérification** : le script exact, lancé contre `main` dans un dossier d'essai, pose
  onze skills. Une session neuve du même environnement, sans dépôt joint et sans outil, a
  répondu : « Library : version 2026-11-09 41f3230 (11 skills). » et a cité les onze skills,
  `revue-langages` compris. Le commit de tête de `main` porte l'identité de la session, pas
  l'adresse du compte.
- **Apps à prévenir** : aucune. Restent à équiper : les autres environnements cloud et les PC.

---

## 2026-11-09 — [skills] Ajout du skill revue-langages

- **Type** : ajout
- **Quoi** : demande de l'utilisateur : « Rajoute un skill qui passe un projet en revue sur le
  langage de code choisi et propose un changement vers le langage le plus efficient selon le
  domaine (graphique, moteur, base de données, réseau…). Que l'application soit hybride peu
  importe, l'importance est l'efficience et la robustesse de l'application. Beaucoup de mes
  apps sont majoritairement en Kotlin sans moteur Rust. »
  `skills/revue-langages/` : la procédure (cartographier, mesurer, écarter ce qui coûte moins
  qu'un changement de langage, juger chaque domaine, compter le coût du pont, proposer, puis
  changer par petits pas), une grille par domaine et un gabarit de rapport. Le skill propose et
  ne réécrit rien sans accord. `nouveau-projet` y renvoie pour choisir les langages au départ.
  Installé par défaut, puisque demandé : onze skills dans `skills/`.
- **Preuve / vérification** : les repères de la grille citent leur source dans les projets
  (démarrage de SuivieMAT, expression régulière de ToolMap, moteur Rust d'InnovAdapt lu sur sa
  branche `claude/android-rust-mobile`). Exigences d'Android sur les bibliothèques natives et
  générateur de liaisons Kotlin relus dans leur documentation le jour même. `cargo test` lancé
  dans la session : un moteur Rust s'y compile et s'y teste sans rien installer. En-tête du
  skill lu par un analyseur YAML ; installation rejouée dans un dossier d'essai. **Non
  vérifié** : le skill n'a encore été appliqué à aucun projet.
- **Apps à prévenir** : aucune.

---

## 2026-11-09 — [skills] depot-public : une fusion faite par GitHub porte l'adresse du compte

- **Type** : patch · transverse
- **Quoi** : constat fait en corrigeant les dépôts publics à la demande de l'utilisateur
  (« corrige »). Une pull request fusionnée par GitHub donne un commit signé de l'adresse du
  compte, même quand c'est l'outil GitHub d'une session qui fusionne : c'est arrivé le jour même
  dans ce dépôt. Le skill `depot-public` ne le disait que pour le site GitHub. Il le dit
  maintenant pour toute fusion, demande un push en avance rapide tant que la protection de
  l'adresse n'est pas confirmée, et fait vérifier l'adresse après coup. `AGENTS.md` § 5.F et la
  proposition `master-dev` renvoient à cette règle.
  Revue « rien de privé » de tous les dépôts publics du compte, toutes branches : fichiers
  corrigés dans deux dépôts ; rien à corriger dans les fichiers des autres, ni ici.
- **Preuve / vérification** : clone complet de chaque dépôt public ; adresses des auteurs relevées
  sur toutes les branches ; recherche de l'adresse et du nom d'utilisateur du poste à la pointe
  de chaque branche, avant et après correction.
- **Apps à prévenir** : aucune. Les historiques ne sont pas modifiés : c'est une décision de
  l'utilisateur. Cette entrée et ce patch attendent d'entrer dans `main`.

---

## 2026-11-09 — [library] Mise en place vérifiée dans une session neuve

- **Type** : transverse
- **Quoi** : pull request n° 1 fusionnée dans `main` (`d4b5b4b`) sur accord de l'utilisateur :
  `main` porte désormais toute la Library. Script de démarrage accepté par lui dans
  l'environnement cloud « Drive ». `installation/README.md` : tableau des vérifications et
  des environnements à jour.
- **Preuve / vérification** : le script exact, lancé contre `main` dans un dossier d'essai, pose
  les règles communes et dix skills. Puis une session neuve du même environnement, sans dépôt
  joint, interrogée sans outil, a répondu : « Library : 2026-11-09 d4b5b4b (10 skills). » et a
  cité les dix skills installés, pas les deux propositions. Sont donc constatés : le clone
  pendant le script de démarrage, le chargement de `~/.claude/CLAUDE.md` et celui des skills de
  `~/.claude/skills/`. **Non vérifié** : `installer.sh` sous Git Bash ; l'effet réel des règles
  sur le comportement d'une session de travail.
- **Apps à prévenir** : aucune. Restent à équiper : les autres environnements cloud et les
  deux PC (`installation/README.md`).

---

## 2026-11-09 — [skills] Neuf skills activés, mise en place acceptée

- **Type** : transverse
- **Quoi** : réponses de l'utilisateur aux quatre questions posées en fin de session.
  - Activés (passés de `skills/_propositions/` à `skills/`) : `livraison`, `recette-telephone`,
    `depot-public`, `depannage-ci`, `apercu-avant-code`, `memoire-projet`, `cadrage`, `moteurs`,
    `nouveau-projet`. Avec `mode-silencieux`, dix skills sont installés par défaut.
  - Non retenus pour l'instant : `copie-recente`, `master-dev` (restent en proposition).
  - Mise en place : « Oui, installe ». Fusion dans `main`, puis script de démarrage dans
    l'environnement cloud de la session.
- **Preuve / vérification** : INDEX § 8 refait à partir des dossiers ; installation rejouée
  dans un dossier d'essai (dix skills posés, les deux propositions ignorées).
- **Apps à prévenir** : aucune.

---

## 2026-11-09 — [library] Méthode de travail : règles communes, skills, installation

- **Type** : ajout · transverse
- **Quoi** : demande de l'utilisateur à une session « master dev » qui n'écrit que dans la
  Library : « comprendre ma méthode de travail et la centraliser », puis proposer des skills
  « à déployer par défaut pour toute nouvelle session ». Son exemple : que Claude cesse tout
  texte de suivi et n'écrive que pour lui parler directement.
  - `SOCLE.md` : les règles communes à toutes les sessions, en une page.
  - `skills/mode-silencieux` : le skill demandé. Onze autres attendent son accord dans
    `skills/_propositions/` (INDEX § 8) ; `depot-public` porte un script de contrôle,
    `nouveau-projet` ses gabarits.
  - `installation/` : `installer.sh` pose `SOCLE.md` et `skills/` dans `~/.claude` (bloc
    délimité, rien d'autre n'est touché, option `--retirer`) ; `environnement-cloud.sh` est le
    script de démarrage à mettre dans l'environnement cloud ; mode d'emploi.
  - `docs/methode-de-travail/README.md` : la méthode expliquée, l'origine de chaque règle, les
    écarts entre projets, les constats de la revue.
  - `AGENTS.md` : § 1 et § 4 complétés, § 5.F (skills) et 5.G (règles communes), check-list.
    `CLAUDE.md`, `README.md` et INDEX (§ 7, § 8) à jour.
  - `.gitattributes` : fins de ligne Unix pour les scripts, les skills et `SOCLE.md`, pour que
    l'installation marche aussi depuis un clone fait sous Windows.
- **Preuve / vérification** : lecture de tous les dépôts du compte (branches actives, journaux,
  décisions, consignes) et des `CLAUDE.md` du Drive ; les règles citées avec une date ont été
  relues à leur source. Mécanismes vérifiés dans la documentation de Claude Code le jour même
  (un script de démarrage peut écrire `~/.claude/CLAUDE.md` ; cache d'environ sept jours).
  `installer.sh` essayé sous Linux sur neuf cas (poste vierge, fichier personnel, relances,
  skill retiré, repères abîmés, fichier vide, fins de ligne Windows, dossier de configuration
  déplacé, retrait). `controle.sh` essayé sur un dépôt d'essai (contenu privé, fichiers
  interdits, image avec métadonnées, identité et messages de commit). En-têtes des douze skills
  lus par un analyseur YAML. **Non vérifié** : le chargement dans une vraie session neuve, le
  script pendant le démarrage d'un environnement cloud, `installer.sh` sous Git Bash.
- **Apps à prévenir** : aucune. Rien n'est installé tant que la branche n'est pas fusionnée dans
  `main` et que le script n'est pas dans l'environnement cloud. Ensuite, les `CLAUDE.md` des
  projets pourront se réduire à ce qui leur est propre.

---

## 2026-11-09 — [library][exercices-repetition-espacee] Branches fusionnées, moteur d'entraînement rangé au gabarit

- **Type** : transverse + ajout
- **Quoi** :
  - Branche `claude/recipe-app-planner-92zsww` (session Carnet de recette, commit `044dd63`)
    fusionnée : son entrée du 2026-11-09 est juste en dessous. Conflits dans ce journal et dans
    la fiche `maj-auto-github` : les deux côtés sont gardés (compléments du 2026-10-27 et
    variante « à chaque ouverture »).
  - Branche `claude/charming-bell-vrm1id` (session Learn-UI-UX, commits `d5880ef`, `22bbb99`,
    `2dd3016`) fusionnée. Elle partait du commit initial et posait `moteur-entrainement/` à la
    racine, avec une ligne dans le README. Le dossier devient
    `moteurs/exercices-repetition-espacee/`, sa fiche est mise au gabarit (le descriptif
    d'origine est repris) et le moteur entre dans l'INDEX (§ 1 à 4, § 6). Le README garde la
    structure du catalogue.
  - Toutes les branches du dépôt sont réunies dans `ccr-0190ac9b-h36zmh`. `main` ne contient
    toujours que le README d'origine.
- **Preuve / vérification** : `git branch -r --no-merged` ne liste plus rien. 17 tests du moteur
  relancés verts (`npx vitest run`, vitest 5.1.0) ; l'installation des versions figées a échoué
  dans la session (erreur interne de npm 10.9.9), le typage n'est donc pas revérifié.
  Comparaison fichier par fichier avec Learn-UI-UX @ `939e441` : `dates.ts`, `grade.ts` et
  `random.ts` identiques ; `srs.ts` en retard d'un correctif (« Facile » à la première
  révision), d'où ⚠️ sur ce composant.
- **Apps à prévenir** : aucune, le défaut de `srs.ts` est déjà corrigé dans Learn-UI-UX. La
  session Learn-UI-UX écrit encore dans `moteur-entrainement/` sur sa branche : à la prochaine
  fusion, ses changements sont à porter dans `moteurs/exercices-repetition-espacee/`.

---

## 2026-11-09 — [maj-auto-github] Variante « à chaque ouverture » (Carnet de recette)

- **Type** : retour d'usage (demande de l'utilisateur) · variante
- **Quoi** : le Carnet vérifie la dernière version publiée à chaque ouverture de l'app
  (`onStart` ; une rotation n'en est pas une), au plus une fois par minute, au lieu d'une fois
  par processus ; un échec réseau n'est plus retenu jusqu'à la mort du processus ; la ligne
  d'état ne saute pas pendant un téléchargement ou une autorisation. Nouvelle section de la
  fiche, lignes « Je cherche à… » et mots-clés de l'INDEX.
- **Preuve / vérification** : tests JVM du Carnet (`MiseAJourTest`, `AccueilViewModelTest`),
  commit `89275f2`. Aucune version du Carnet publiée : pas d'essai sur appareil. `src/`
  inchangé (version de Popote, en service).
- **Apps à prévenir** : Popote Compagnie, si l'on veut la même vérification à chaque ouverture.

---

## 2026-10-27 — [library] Relecture à froid : corrections de cohérence et d'exactitude

- **Type** : transverse + patch (statut)
- **Quoi** : corrections issues d'une relecture indépendante de tous les changements du jour.
  - INDEX § 1 : une seule formule pour l'état d'un correctif, « patch dans `src/` » ou « patch
    hors `src/` » (au lieu de « proposé » ou « appliqué » employés pour les deux cas).
  - `paiement-sumup` : le `Reussi` perdu après une recréation de l'activité est un défaut
    suspecté de `src/` (bus sans relecture), pas du SDK ; composant `CheckoutSumUp.kt` passé à ⚠️
    comme les autres défauts suspectés (lampe, jeton). L'entrée de revue de ce jour disait « pas
    de ⚠️ » : elle est ainsi corrigée.
  - `scan-multimode` : contradiction signalée entre le ✅ déclaré par l'utilisateur et la
    documentation de ScanList (seuils du consensus vérifiés en test unitaire seulement) ; lampe
    ajoutée au statut de l'en-tête ; commits de la variante Carnet cités (`afccb60`, `25b24e4`,
    `6c60ef0`).
  - `maj-auto-github` : effet de bord du patch d'annulation (une `Error` n'est plus avalée),
    parade de Popote aux minutes épuisées (runner auto-hébergé, `2bd6298`), section « Réglages
    éprouvés ». Pointe Popote `93e5d5b` : le code des moteurs est inchangé mais `version.yml` a
    changé (l'entrée de revue de ce jour disait « moteurs inchangés »).
  - ToolMap : section de l'INDEX distinguant l'état revu (`eee8c6b`) de l'état public
    (`a37dcab`, où P24.1 est corrigé et `:debugkit` retiré du build). La provenance de
    `rapport-plantage` et de `docs/methodes/android-en-session-cloud.md` a été complétée au commit
    précédent (republication de ToolMap) : la consigne « garder ou baliser la branche `ccr` » de
    l'entrée `rapport-plantage` est remplacée par l'archive privée `ToolMap-Archive`.
  - `rapport-plantage` : en-tête aligné (saut de l'initialisation 🟡) ; correctif Popote de la
    base illisible dit « vérifié dans le code seulement ». `astro-soleil-lune` : section
    « Réglages éprouvés ». Fiche méthode : SuivieMAT public, runner auto-hébergé.
  - INDEX : BOM littéral invisible remplacé par `\uFEFF` écrit en clair ; ligne Python du § 4
    complétée ; faiblesses de sécurité d'une app privée (InnovAdapt) décrites sans détail
    exploitable. AGENTS § 3 : exemple de statuts mis à jour.
- **Preuve / vérification** : relecture en deux angles (cohérence et règles ; exactitude sur plus
  de 40 affirmations vérifiées dans les dépôts sources, copies et patchs relus, tests purs
  relancés : 91, 13 et 7 verts). Historique des fiches : lignes ajoutées seulement.
- **Apps à prévenir** : Popote Compagnie (`Reussi` perdu, suspecté).

---

## 2026-10-27 — [library] Revue des dépôts : candidats et dépôts revus mis à jour

- **Type** : revue de dépôt
- **Quoi** : tour de tous les dépôts du compte, branches actives comprises. INDEX § 5 réécrit dépôt
  par dépôt, § 6 ramené à la revue la plus récente, § 2 et § 4 complétés, date du jour.
  Précédente revue (2026-10-12) → revue du jour :
  - ScanList `1368027` → `2ef0b6d` (un commit de CI). **SuivieMAT** (nouveau, public depuis le
    2026-10-27) : fork de ScanList, `5eb5198` (pointe `65da616`) ; `RegexSuggester` versé dans
    `scan-multimode` ; candidats `FolderTimeline`, purge des artefacts Actions ; défauts du
    `CsvCodec` (injection de formule, import en UTF-8 seulement) notés sur le candidat.
  - Suivie-Popote-Cie `08c211a` → `6149a81` (pointe `93e5d5b`) : moteurs inchangés ; capteur de
    plantage → `rapport-plantage`, captures → `docs/methodes/` ; preuves réelles partielles du
    trousseau et de SQLCipher citées ; Popote-Compagnie (versions) inchangé à `6e0de47`.
  - Android-Suntrack `e5c94b4` (+ branche `meteogram-weather-icons` @ `2cb08aa`) : candidat
    « icônes météo à paliers » ; défaut `hourIndex` d'Open-Meteo noté. **Sun-tracking** (nouveau,
    `2aec84a`) : republication de l'état initial avec le bug d'époque lunaire, rien à extraire.
  - Activity-track `cdf458d` → `855773c` (la revue précédente avait regardé `main` au lieu de la
    branche active) : Gemini remplacé par l'API Anthropic ; candidats investigation multi-tours,
    estimation de tokens, relance WorkManager, dossier surveillé SAF.
  - ToolMap `06c80c7` → `eee8c6b` : utilisé sur un Galaxy S25 depuis le 2026-10-03 (la note
    « jamais testé sur appareil » était périmée) ; `rapport-plantage` extrait ; 8 candidats de
    plus ; partage de position redécrit (relais Firebase). Republié le jour même en public sans
    historique (`a37dcab`), l'ancien dépôt devenant `ToolMap-Archive` (privé).
  - InnovAdapt-CRM `42e9534` → branche `claude/android-rust-mobile` @ `d2c2f6e` (37 commits) :
    synchro « Rust (mobile) » éclatée en OAuth et Drive bureau, synchro chiffrée multi-appareils,
    trousseau ; pont Android Rust/Slint et diagnostic HTML en candidats.
  - **Carnet-de-recette** (revu par sa propre session) vérifié à `8dc4820` : fiches fidèles,
    défaut de CI reporté dans `maj-auto-github`, 2 candidats de plus. **Scam-Stop** (nouveau,
    `b971f43`, GPL-3.0) : 2 candidats. **Mihon-edited-** : déjà intégré ce matin.
  - RGL13 : pas revu (accès non accordé à la session).
- **Preuve / vérification** : une revue par dépôt, chacune contre-vérifiée par un second agent
  qui a refait les diffs, relu les journaux des apps, rejoué les tests possibles (pytest 86/91,
  `cargo test` 79/79, `node --test` 54/54, tests Kotlin purs sans Gradle) et corrigé les rapports
  (clones superficiels, pointes qui avançaient). Pointes distantes revérifiées par
  `git ls-remote` juste avant ce commit.
- **Apps à prévenir** : voir les entrées de fiche du jour. En plus : ScanList et SuivieMAT
  (`CsvCodec` : injection de formule) ; Sun-tracking (copie périmée à archiver ou aligner).

---

## 2026-10-27 — [docs/methodes] Fiches méthode : captures Compose sans émulateur, Android en session cloud

- **Type** : ajout + transverse
- **Quoi** : nouveau dossier `docs/methodes/` (outillage déjà mis en œuvre dans les apps, sans
  `src/`) :
  - `captures-compose-robolectric.md` : voir un écran Compose réel en PNG depuis une session
    cloud. Méthode de Popote, extensions du Carnet de recette (sombre, police, largeurs,
    paysage) et de Popote (plusieurs tailles), gabarit de test complet écrit pour la fiche,
    pièges (instabilité Robolectric, nom de test accentué, faux imports, photos absentes) ;
    jamais de PNG rendu sur des données réelles.
  - `android-en-session-cloud.md` : SDK dans le conteneur ou par hook SessionStart, miroir
    Maven contre les 429, ordres de grandeur, tests purs sans Gradle (script donné), Rust,
    Node et Python en session, livraison d'un APK de plus de 30 Mio, clé de debug commune
    (jamais copiée ici), pièges de CI GitHub Actions, revue de dépôt (clones superficiels).
  - `README.md` d'index ; INDEX § 2, § 3 et § 7 ; lien depuis le `README.md` racine.
  Remplace le candidat Popote « Captures d'écran Compose sans émulateur ».
- **Preuve / vérification** : rendu des captures « constaté fidèle par le propriétaire » le
  2026-09-29 (Popote `docs/decisions.md`). Le gabarit de la fiche a été exécuté dans un projet
  minimal en session (AGP 8.8.0, Robolectric 4.13, SDK installé dans le conteneur) : 4 PNG
  produits et relus, puis relancé par la contre-vérification après retouche. Bloc
  d'installation du SDK rejoué dans un dossier neuf (20 s). Tests purs sans Gradle rejoués sur
  `astro-soleil-lune` (13/13). Extensions et livraison par `release/` ou pré-version : 🟡.
- **Apps à prévenir** : ScanList et SuivieMAT, dont le `CLAUDE.md` affirme encore qu'un build
  n'est généralement pas possible en session cloud (SuivieMAT compile pourtant en session
  depuis le 2026-09-24).

---

## 2026-10-27 — [rapport-plantage] Ajout du moteur Rapport de plantage

- **Type** : ajout
- **Quoi** : nouveau moteur `rapport-plantage`, repris de ToolMap (branche
  `ccr-e59b55ac-kt4qb9` @ `2e992e1`, code identique à `fa96a1a`, la version installée sur le
  téléphone). `src/` : `CrashReporter.kt`, `CrashActivity.kt`, `CrashReport.kt` ; `tests/` :
  `CrashReportTest`, `CrashReporterTest`, `CrashActivityTest` ; packages d'origine. Fiche
  complète : gestionnaire posé dans `attachBaseContext`, écran dans un processus `:crash`,
  plantages natifs relus par `ApplicationExitInfo`, saut de l'initialisation, ADR-029,
  intégration (manifeste, textes, `GIT_SHA`, couplages à défaire), vie privée (fin du logcat et
  modèle du téléphone dans le rapport). Variante minimale de Popote décrite sans code, avec sa
  règle de confidentialité (jamais d'entité ni de donnée personnelle dans un message
  d'exception). Les évolutions de la branche p16 (`159e9e4`, `3e6677a`) restent hors de `src/`
  (🟡). Remplace le candidat Popote « Capteur de plantage ».
- **Preuve / vérification** : ToolMap `docs/JOURNAL.md` (branche p16) l. 1560-1561 : rapport
  affiché sur un Galaxy S25 (Android 15) le 2026-10-03, transmis par l'utilisateur, correctif
  `c620a38`. Popote `docs/decisions.md` (v13.18, 2026-09-03) : pile d'un plantage SumUp lue dans
  le fichier du capteur. Fichiers copiés identiques (`cmp`) à `2e992e1` et à `fa96a1a`.
  `CrashReportTest` 7/7 en JVM sans Gradle (avec `kotlin-test-junit` 2.0.21) ; tests
  Robolectric non exécutables ici. Plantages natifs et saut de l'initialisation : aucune preuve
  réelle, d'où 🟡.
- **Apps à prévenir** : Carnet de recette (son `docs/cadrage.md` § 11 renvoie encore au
  candidat Popote). Popote, pour information : sa variante ne lit pas les plantages natifs et
  son gestionnaire est posé après `SumUpState.init`. ToolMap : `fa96a1a` et `2e992e1` n'existent
  que sur la branche `ccr-e59b55ac-kt4qb9`, à garder (ou à baliser) pour la provenance.

---

## 2026-10-27 — [paiement-sumup] Revue Popote : débit possible malgré `Echec` ou `Annule`

- **Type** : revue de dépôt
- **Quoi** :
  - Code et tests identiques à Popote `6149a81` (et à la pointe `93e5d5b`), hors gabarit
    `<AFFILIATE_KEY>`. Toujours aucun encaissement réel.
  - Point de vigilance ajouté (constat de décompilation déjà présent dans Popote à `08c211a`) :
    `onBackPressed()` de l'écran de paiement du SDK peut rendre `Annule` ou `Echec` après un vrai
    débit. Parade de Popote : `avertirDebitEventuel()` avant tout « Réessayer » (Comptoir, Comptes,
    Cotisation), qui prévient sans rien détecter. Piège « Réessayer » réécrit, exemple Compose
    corrigé, étape d'intégration sur l'avertissement et le délai de garde de 3 min.
  - Hypothèse de revue, non vérifiée, consignée en vigilance : après une recréation de l'activité,
    le `Reussi` émis par un `MutableSharedFlow` sans relecture peut être perdu si
    `onActivityResult` passe avant que l'écran l'écoute (même risque chez Popote).
  - Statut précisé : l'écran de connexion a été atteint en v13.19 puis a planté ; le correctif
    v13.20 n'a jamais été confirmé (l'INDEX disait « connexion OK »). `forkEvery = 1` présenté
    comme un confinement, pas comme une cause démontrée. Sections « Composants », « Réglages
    éprouvés » et « Points de vigilance » ajoutées. `src/` et `tests/` non modifiés, 🟡 inchangé
    (limite du SDK, pas un défaut du code).
- **Preuve / vérification** : `diff` Library ↔ app : seule la ligne de la clé diffère, tests
  identiques (9) ; recherche de la vraie clé dans toute la Library : 0 occurrence. Citations de
  `docs/todo.md`, `docs/decisions.md`, `ui/Comptoir.kt`, `ui/Comptes.kt`, `ui/Cotisation.kt`
  relues. Perte d'une valeur émise sans abonné démontrée par un test JVM hors Library. Tests
  Robolectric non exécutables ici.
- **Apps à prévenir** : Popote Compagnie, pour l'hypothèse du `Reussi` perdu (à vérifier lors de
  l'essai sur terminal physique prévu dans `docs/todo.md`).

---

## 2026-10-27 — [google-drive-appdata] Revue Popote et Carnet : variante complétée, vigilances, adresse de test

- **Type** : revue de dépôt
- **Quoi** :
  - Popote `6149a81` (inchangé jusqu'à `93e5d5b`) : `DriveAppData.kt` identique à `src/` hors
    patchs de la Library ; aucun des patchs du 2026-10-26, pas de `clearToken`. Commits `fa90649`
    et `9391ed9` vérifiés fidèles au Carnet `8dc4820` (moteur inchangé jusqu'à `797fae7`).
  - Section « Variante » : provenance (`f198fd3`), `RefusDrive` et `estPanneReseau`,
    `echecGoogle` (le cas « code 8 » n'est jamais observé), `Echec(reseau)`, espaces insécables,
    documentés sans report dans `src/` ; phrase sur le déplacement du dossier nuancée (simple
    commentaire de code) ; renvoi aux réimplémentations d'InnovAdapt-CRM (`d2c2f6e`).
  - Tableaux « Composants » et « Réglages éprouvés » ajoutés (gabarit). Étape 3 de
    l'intégration complétée par les fonctions du patch 403.
  - Vigilances : dépôt manuel seul éprouvé (WorkManager jamais vérifié en réel), effet de bord
    du patch 403 (un 403 hors refus d'autorisation devient transitoire et serait réessayé sans
    fin), mode « Test » à 7 jours (à vérifier pour `AuthorizationClient`), un 404 n'est jamais un
    succès en écriture, durée de vie du jeton inconnue, pas d'épinglage TLS ; dans un dépôt
    public, une clé de debug versionnée permet aussi d'installer un APK tiers par-dessus une
    version de debug (ToolMap ADR-047). Jeton en cache : défaut suspecté aussi chez Popote et
    InnovAdapt.
  - `tests/DriveAppDataTest.kt` : adresse en `gmail.com` remplacée par une adresse en
    `example.org` (écart volontaire avec Popote). Statut inchangé.
- **Preuve / vérification** : `diff -u` Popote ↔ `src/` ; diffs Carnet `f198fd3..797fae7` sur
  `drive/` ; citations relues dans les deux dépôts par la contre-vérification. 28/28 tests verts
  hors Gradle (Kotlin 2.0.21, JUnit 4.13.2, bouchons Android, `kotlinx-serialization` 1.6.2) :
  valable pour les fonctions pures seulement.
- **Apps à prévenir** : Popote Compagnie (403 : patch de la Library à reporter, avec son effet
  de bord à surveiller ; jeton en cache : défaut suspecté, correctif dans `DriveCarnet.avecJeton`
  du Carnet). InnovAdapt-CRM, hors « Utilisé dans » : pièges communs (jeton en cache, 404, 403
  non lu) si sa synchro est gardée.

---

## 2026-10-27 — [maj-auto-github] Patch CI : mots de passe de signature hors de `Properties.load`

- **Type** : patch + revue de dépôt
- **Quoi** :
  - `src/version.yml` écrivait `storePassword` et `keyPassword` dans `keystore.properties`, que
    `build.gradle.kts` relit par `Properties.load` : un « \ » y est lu comme un échappement, une
    espace de tête est retirée, un « \ » final avale la ligne `keyAlias`. Correctif repris de
    Carnet de recette (`f198fd3`) : le fichier ne porte plus que `storeFile` et `keyAlias`, les
    mots de passe passent par l'environnement de l'étape de compilation
    (`SIGNATURE_STORE_PASSWORD`, `SIGNATURE_KEY_PASSWORD`) ; commentaire trompeur corrigé.
    Composant CI ⚠️ (latent, patch appliqué, jamais exercé par une vraie publication).
  - Fiche : tableau « Composants », extrait `signingConfigs.release` (sans valeur), tableau
    « Pièges de CI » avec provenance (vérification du `.jks` par `keytool`, `base64 -d
    --ignore-garbage`, secrets par `env`, quota d'artefacts, `setup-android`, `workflow_dispatch`,
    balise refusée en session, minutes épuisées, PKCS12), exemple d'interface en ligne d'état du
    Carnet (non éprouvé), commit de provenance de la variante.
  - Historique : revue Popote `6149a81` (✅ inchangé ; 4 aperçus 127 à 130 publiés, aucun essai
    sur téléphone consigné, aucune publication publique nouvelle) et Carnet (aucune balise à
    `797fae7`). `MiseAJour.kt` et `MiseAJourTest.kt` non modifiés.
- **Preuve / vérification** : démonstration JDK 21 avec une vraie clé PKCS12 et les blocs `run`
  de l'ancien et du nouveau `version.yml` : trois mots de passe piégés échouent avant
  (« keystore password was incorrect ») et signent après ; deux témoins signent dans les deux
  cas ; refaite indépendamment par la contre-vérification. `MiseAJourTest` 9/9 en JVM (bouchons
  Android). Aucun run GitHub Actions possible ici.
- **Apps à prévenir** : Popote Compagnie (défaut latent dans `version.yml` et
  `app-terrain/build.gradle.kts`, à changer ensemble, sans urgence ; patch d'annulation toujours
  absent). Carnet de recette a déjà le correctif.

---

## 2026-10-27 — [scan-multimode] Patch NDEF, ajout de RegexSuggester, SuivieMAT dans « Utilisé dans »

- **Type** : patch + ajout (composant) + revue de dépôt
- **Quoi** :
  - NFC : `FLAG_READER_SKIP_NDEF_CHECK` retiré de `src/NfcScanSurface.kt`, commentaire faux
    corrigé. D'après le code AOSP (`android14-release`), `NfcService` saute la lecture NDEF avec
    ce drapeau et `NativeNfcTag` n'ajoute la techno NDEF que pendant cette lecture :
    `Ndef.get(tag)` renvoyait donc `null` et seul l'UID était lu. La variante « `ndef.connect()`
    dans `NfcTagDecoder` » du patch proposé est retirée, car inopérante. Le NFC reste ⚠️.
  - Nouveau composant 🟡 `RegexSuggester` et son test (27 tests), copiés sans modification de
    SuivieMAT `5c81d3c` (identiques à `5eb5198` et à la pointe `65da616`) : il propose des
    formules regex à partir d'exemples, sans IA. L'interface est décrite, pas copiée.
  - SuivieMAT ajouté à « Utilisé dans » : fork de ScanList, moteur identique jusqu'à `5eb5198`
    hormis l'habillage de `NfcScanSurface`, scan jamais vérifié sur téléphone. Une preuve
    indirecte NFC (lecteur armé sur le téléphone de l'utilisateur, 2026-09-25) est consignée,
    sans promotion.
  - Points de vigilance : ICU (accolade littérale non échappée, `--` en classe, messages de
    `frenchMessage` calés sur le JDK), UID aléatoire et UID à ne pas exporter, option aperçu
    ou économie de l'OCR. Ligne `ScanValue` corrigée (`normalize` garde la casse).
- **Preuve / vérification** : tests purs sans Gradle (Kotlin 2.0.21, JUnit 4.13.2) :
  `RegexSuggesterTest`, `RegexRuleEngineTest`, `ScanConsensusTest`, `ScanDebouncerTest`,
  `NfcValueTest` : 91/91, relancés par la contre-vérification. Fichiers `RegexSuggester`
  identiques (`cmp`) à la source. Patch NDEF non vérifié sur appareil : aucun test ne couvre ce
  chemin Android ; la pile NFC de Samsung peut différer d'AOSP.
- **Apps à prévenir** : ScanList (`1368027` / `2ef0b6d`) et SuivieMAT (`65da616`) n'ont pas le
  patch NDEF ; SuivieMAT est peu concerné (il ne lit que l'UID et peut garder le drapeau).

---

## 2026-10-27 — [library] AGENTS.md : pièges de revue, dossier docs/methodes

- **Type** : transverse
- **Quoi** : § 5.E complété par les pièges rencontrés pendant la revue du 2026-10-27 :
  revoir la branche active et pas seulement `main` (la revue du 2026-10-12 était déjà périmée
  pour Activity-track et ToolMap) ; cloner sans `--depth` (un clone superficiel a fait croire à
  une branche orpheline d'InnovAdapt et à une fausse divergence d'Android-Suntrack) ;
  revérifier la pointe distante avant de consigner ; récupérer et fusionner d'abord les
  branches de la Library écrites par d'autres sessions. § 4 : dossiers `docs/ui-ux/` et
  `docs/methodes/` ajoutés à la structure.
- **Preuve / vérification** : constats des contre-revues du 2026-10-27 (clones complets,
  `git cat-file`, `git ls-remote`).
- **Apps à prévenir** : aucune

---

## 2026-10-27 — [astro-soleil-lune] Patch : transit de la veille et jour du changement d'heure

- **Type** : patch + revue de dépôt
- **Quoi** :
  - `MoonCalculator.calculate` rendait, 26 à 29 jours par an, le transit de la **veille au soir**
    (premier maximum de la fenêtre élargie), et jamais `null`, contrairement à sa KDoc. Le jour
    de 23 h (passage à l'heure d'été), le balayage fixe de 24 h débordait sur le lendemain et
    pouvait rendre un lever ou un coucher d'après minuit ; le jour de 25 h, la dernière heure
    était ignorée. Correctif au plus petit périmètre : longueur réelle du jour local, et seuls
    les maxima dont l'heure affinée tombe dans le jour comptent.
  - `tests/AstroTest.kt` : deux tests de reproduction (2024 sur trois villes ; New York
    2020-2030) et le test d'altitude `altitudeDeg` repris de `SkyPaletteTest` (Android-Suntrack).
  - Fiche : statut précisé (8 tests contre éphémérides publiées sur 13), tableau Composants,
    vigilances « époque de la théorie lunaire » (deux effets du bug d'origine) et « couverture
    faible côté lune », `declinationDeg` sans valeur par défaut, revue Android-Suntrack et
    Sun-tracking dans l'historique.
- **Preuve / vérification** : défaut signalé par une contre-revue (portage Python), puis
  reproduit en Kotlin : les deux nouveaux tests échouent sur l'ancien code (Paris 2024-01-23 :
  transit du 22 à 22 h 43 ; New York 2023-03-12 : lever du 13 à 0 h 44) et passent après le
  correctif. 13/13 tests verts (Kotlin 2.0.21, JUnit 4.13.2, sans Gradle). Calcul pur : aucun
  matériel requis, mais aucun retour terrain non plus, d'où 🟡.
- **Apps à prévenir** : Sun Track (Android-Suntrack, `MoonCalculator.kt` inchangé à `2cb08aa`).
  Sun-tracking est une copie périmée (bug d'époque) à archiver ou aligner.

---

## 2026-10-27 — [scan-multimode] Anti-rebond glissant et lampe après l'arrière-plan (Carnet de recette)

- **Type** : retour d'usage (relecture adverse) · patch proposé
- **Quoi** : variante du Carnet complétée (points 8 à 10 : verrou glissant et voisin écarté,
  numéro nettement le plus grand, liaison, rotation de capture et lampe de `CameraPreview`).
  Points de vigilance : ⚠️ lampe éteinte après un passage en arrière-plan (suspecté, patch
  proposé côté Carnet), verrou à durée fixe de `ScanDebouncer` qui revalide une valeur restée
  en vue.
- **Preuve / vérification** : relecture adverse du Carnet, chaque constat contre-vérifié par
  un second agent ; tests JVM du verrou glissant (`SuiviEtiquettesTest`). Lampe : déduite du
  code de CameraX, aucun essai sur appareil. Non reporté dans `src/`.
- **Apps à prévenir** : ScanList (lampe, si le défaut se confirme sur un téléphone).

---

## 2026-10-27 — [scan-multimode] Utilisé dans Carnet de recette, variante OCR sans regex ; trois candidats

- **Type** : retour d'usage (intégration) · revue de dépôt
- **Quoi** : fiche `scan-multimode` : ligne « Utilisé dans », section « Variante : OCR par
  éléments dans le réticule, sans regex (Carnet de recette) » (sept adaptations), précision
  dans les points de vigilance : `addOnSuccessListener` sans exécuteur rappelle sur le fil
  principal, pas sur le fil d'analyse. INDEX : ligne du moteur, trois candidats du Carnet
  (lecture à voix haute par segments, commandes vocales, lecture de pages publiques), dépôt
  ajouté aux dépôts passés en revue.
- **Preuve / vérification** : copies de `ScanConsensus`, `ScanDebouncer` et `ScanValue` dans
  le Carnet avec leurs tests repris, au vert (`:core:test`, `:app:testDebugUnitTest`) ;
  géométrie de l'image testée en JVM. **Aucun essai sur appareil** : statut inchangé.
- **Apps à prévenir** : aucune (la précision sur le fil ne change pas le comportement de
  ScanList).

---

## 2026-10-27 — [docs/ui-ux] Crédits d'images complétés

- **Type** : transverse
- **Quoi** : fin d'un travail laissé non commité le 2026-10-21. Auteur de
  `Color_star-en__tertiary_names_.png` relevé (Kwamikagami, CC BY-SA 4.0) ; crédit ajouté pour
  `Double_Diamond_by_the_Design_Council.png` (déjà dans le dépôt, sans ligne de crédit) ; nouvelle
  figure 37.7 (`CVD-friendly_sequential_colormaps.png`), l'illustration de palettes séquentielles
  qu'annonçait déjà le texte du chapitre 37. Une spirale de Fibonacci téléchargée mais citée
  nulle part n'est pas ajoutée.
- **Preuve / vérification** : auteurs et licences relus sur l'API de Wikimedia Commons
  (`extmetadata` Artist et LicenseShortName) pour les trois fichiers.
- **Apps à prévenir** : aucune

---

## 2026-10-27 — [library] Branches d'autres sessions intégrées

- **Type** : transverse
- **Quoi** :
  - Branche `claude/recipe-app-planner-92zsww` (session Carnet de recette, commits `fa90649`
    et `9391ed9`) intégrée par avance rapide : ses deux entrées sont juste en dessous.
  - Branche `ccr-be4abd50-9zo09e` (2026-10-21) fusionnée. Elle partait du commit initial du
    dépôt et ajoutait au README une note sur le modèle de colorisation manga-colorization-v2.
    La note est déplacée dans l'INDEX : candidat « Colorisation IA de manga » (§ 5,
    Mihon-edited-) et ligne « Je cherche à… ». Le README garde la structure du catalogue.
- **Preuve / vérification** : dépôt du modèle cloné (`a0d0e44`, 2025-03-31) : aucun fichier
  de licence, dépendances PyTorch, poids sur Google Drive. Mihon-edited- `main` @ `d001e6f`
  = amont Mihon, sans `tools/colorizer-server`. Les temps de calcul de la note d'origine ne
  sont pas mesurés ici.
- **Apps à prévenir** : aucune

---

## 2026-10-26 — [google-drive-appdata] Jeton révoqué gardé en cache (suspecté)

- **Type** : patch (proposé, hors `src/`)
- **Quoi** : point de vigilance ⚠️ sur la fiche. Après un retrait d'accès depuis le compte
  Google, Play Services rend encore le jeton en cache, et « reconnecter » boucle jusqu'à son
  expiration. Patch proposé : `GoogleAuthUtil.clearToken` sur tout `AutorisationRevoquee`
  (code de référence : `DriveCarnet.avecJeton`, JRRocks/Carnet-de-recette).
- **Preuve / vérification** : seconde relecture adversariale du Carnet, contre-vérifiée sur
  la documentation Android (« Clear the token cache »). Le code du Carnet compile et passe
  ses tests. Non reporté dans `src/` (restructuration de cinq fonctions, Library non
  compilable ici). Aucun essai sur appareil.
- **Apps à prévenir** : Popote Compagnie.

---

## 2026-10-26 — [google-drive-appdata][maj-auto-github] Utilisés dans Carnet de recette, deux patchs

- **Type** : patch + intégration dans une nouvelle app
- **Quoi** :
  - Les deux moteurs sont intégrés dans [Carnet de recette](https://github.com/JRRocks/Carnet-de-recette)
    (dépôt public) : ligne « Utilisé dans » et section « Variante » de chaque fiche
    (`drive.file` et dossier visible ; Releases du dépôt de code public, sans second dépôt).
  - **Patch `maj-auto-github`** : `publicationDuLancement` avalait l'annulation (`runCatching`)
    et ne proposait plus aucune mise à jour du processus après un écran recréé pendant la
    requête. `try`/`catch` qui relance l'annulation ; deux tests.
  - **Patch `google-drive-appdata`** : tout 403 devenait `AutorisationRevoquee`, y compris
    « API non activée » ou une limite de débit. Le corps d'erreur est maintenant lu
    (`exceptionPour`) ; quatre tests.
  - Avertissement dans les prérequis de `google-drive-appdata` : dans un dépôt public, ne
    jamais déclarer au client OAuth le SHA-1 de la clé debug versionnée.
- **Preuve / vérification** : défauts relevés par une relecture adversariale du lot 1 du
  Carnet, chacun contre-vérifié par un second relecteur. Les correctifs y sont compilés et
  testés : 32 tests JVM verts, et le test d'annulation échoue sur l'ancien code. Ici, la
  Library ne se compile pas ; le code reporté est le même, au package près. **Aucun essai sur
  appareil** : composants concernés passés à ⚠️.
- **Apps à prévenir** : Popote Compagnie (porte encore les deux défauts).

---

## 2026-10-21 — [docs/ui-ux] Dossier UI/UX sourcé

- **Type** : ajout
- **Quoi** :
  - Nouveau dossier `docs/ui-ux/` : 41 chapitres en français (processus, recherche, heuristiques,
    lois de l'UX, Gestalt, typographie, couleur et contraste, composants, mobile et Android,
    Material Design 3, accessibilité, performance, contenu, design systems, éthique, IA, GOV.UK,
    modèles mentaux, qualité Android, glossaire, synthèse).
  - `SOURCES.md` (202 sources avec URL), `images/` (images libres de droits de Wikimedia Commons)
    et `images/CREDITS.md`.
  - `INDEX.md` § 7 et `README.md` mis à jour.
- **Preuve / vérification** : relecture des chapitres contre les pages sources téléchargées ;
  plusieurs affirmations non étayées corrigées. Toutes les images référencées existent. Pas de
  code : aucun build ni test. Limites : `m3.material.io` et Apple HIG illisibles
  automatiquement ; deux images souhaitées non récupérées ; auteur d'une image à confirmer ;
  volume réel inférieur aux 200 pages visées (voir `docs/ui-ux/README.md`).
- **Apps à prévenir** : aucune

---

## 2026-10-12 — [library] Création du catalogue

- **Type** : transverse + ajout + revue de dépôt
- **Quoi** :
  - Structure du dépôt : `AGENTS.md` (guide des agents), `INDEX.md` (index de recherche),
    `JOURNAL.md`, `CLAUDE.md`, gabarit `moteurs/_modele/`.
  - Cinq moteurs catalogués avec leur code et leurs tests de référence :
    `scan-multimode` (ScanList), `paiement-sumup`, `google-drive-appdata`, `maj-auto-github`
    (Popote Compagnie, code dans Suivie-Popote-Cie), `astro-soleil-lune` (Android-Suntrack).
  - Revue des sept dépôts (dont Activity-track, ToolMap et InnovAdapt-CRM, sans moteur extrait) : 39 candidats notés dans l'INDEX § 5.
  - Clé Affiliate SumUp remplacée par le gabarit `<AFFILIATE_KEY>` dans `paiement-sumup/src/CheckoutSumUp.kt`.
- **Preuve / vérification** : statuts établis à partir des docs des dépôts sources
  (`consignes/`, `docs/decisions.md`, `docs/todo.md`, `docs/JOURNAL.md`) et de la
  déclaration de l'utilisateur pour le scan. Aucun build lancé : la Library n'est pas un
  projet compilable.
- **Apps à prévenir** : ScanList, à cause du soupçon sur la lecture NDEF en mode NFC
  (`FLAG_READER_SKIP_NDEF_CHECK`, voir la fiche `scan-multimode`). À vérifier sur appareil.
