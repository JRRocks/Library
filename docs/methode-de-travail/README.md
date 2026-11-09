# Ma méthode de travail

| Champ | Valeur |
|---|---|
| **Statut** | 🟡 Relevée le 2026-11-09 par lecture de tous les dépôts du compte et des consignes du Drive. Les règles citées sont déjà appliquées dans au moins un projet ; leur réunion en règles communes attend d'être éprouvée |
| **Sources** | `CLAUDE.md`, journaux, décisions et consignes de ToolMap, SuivieMAT, ScanList, Suivie-Popote-Cie, Activity-track, Scam-Stop, InnovAdapt-CRM, Learn-UI-UX, Android-Suntrack ; `CLAUDE.md` du dossier Drive des projets ; `AGENTS.md` de cette Library |
| **Dernière mise à jour** | 2026-11-09 |

## À quoi sert ce document

Mes projets suivent tous la même façon de travailler, mais chacun la réécrit dans son coin, avec des variantes. Ce document la rassemble une fois, dit d'où vient chaque règle, et montre où les projets divergent.

Elle se lit à trois niveaux :

| Niveau | Fichier | Usage |
|---|---|---|
| L'essentiel, une page | [`SOCLE.md`](../../SOCLE.md) | Chargé au début de **chaque** session, sur tous les projets |
| Les procédures | [`skills/`](../../skills/) | Chargées quand la situation se présente (livrer, montrer un aperçu, démarrer un projet) |
| L'explication | ce document | Pour comprendre, décider d'un changement, tenir la méthode à jour |

Les consignes d'un projet passent avant les règles communes quand elles sont plus précises. Une demande faite dans la conversation passe avant tout.

## 1. Qui fait quoi

- **Moi** : je décide, je choisis entre des variantes, j'essaie sur mon téléphone. Je ne lis pas le code. Mes demandes tiennent en une ligne ou un court paragraphe, souvent avec une capture ou un exemple, et un résultat que je peux observer.
- **La session** : elle fait, vérifie, consigne et livre. Elle me parle peu : une question, un choix, un résultat.
- **Le master dev** : une session qui ne code dans aucune application. Elle fait le tour des projets, tient cette Library et me propose des évolutions.

## 2. Où se fait le travail

| Lieu | Ce qu'il permet | Ce qu'il ne permet pas |
|---|---|---|
| Session cloud (téléphone, navigateur) | Coder, tester la logique, compiler après installation du SDK, lancer la CI, produire des aperçus et des rendus d'écran | Pas d'émulateur. Pas de miroir Drive. Un APK trop lourd ne passe pas en pièce jointe : il faut un lien |
| PC Windows (deux postes) | Android Studio, émulateur, compilation locale, miroir Drive | Les deux postes n'ont pas les mêmes chemins ni le même nom de lecteur Drive |
| Mon téléphone | La seule vérité pour le GPS, la caméra, le NFC, les capteurs, deux appareils ensemble, l'écran éteint | Je n'y vois que ce qu'on m'a livré |

Un projet vit donc à trois endroits : GitHub (toutes ses branches), le miroir Drive, le dossier du PC.

## 3. Le déroulé d'un changement

| Étape | Ce qui se passe | Skill |
|---|---|---|
| 1 | Partir de la copie la plus récente, toutes branches comprises | `copie-recente` |
| 2 | Lire la mémoire du projet : avancement, dernières entrées du journal, décisions | `memoire-projet` |
| 3 | Demande importante ou floue : des questions numérotées, puis un plan, sans coder | `cadrage` |
| 4 | Tout ce qui se voit : un aperçu, des variantes, mon « oui » avant le code | `apercu-avant-code` |
| 5 | Construire par petits pas, en reprenant les moteurs déjà éprouvés | `moteurs` |
| 6 | Vérifier tout ce qui peut l'être, et dire ce qui ne l'a pas été | `recette-telephone` |
| 7 | Monter la version, compiler, me remettre un lien ou un fichier | `livraison` |
| 8 | Écrire le journal, l'avancement, les décisions | `memoire-projet` |
| 9 | Sauvegarder : GitHub, puis miroir Drive depuis le PC | `copie-recente`, `depot-public` |
| 10 | Un message final court | `mode-silencieux` |

Autour de ce déroulé : `nouveau-projet` pour démarrer, `revue-langages` pour choisir ou remettre en cause le langage de chaque domaine, `depannage-ci` quand la compilation casse, `master-dev` pour tenir l'ensemble.

Onze de ces skills sont installés (dix depuis le 2026-11-09, `revue-langages` ajouté le même jour). `copie-recente` et `master-dev` restent des propositions (`skills/_propositions/`) : leurs règles tiennent pour l'instant dans [`SOCLE.md`](../../SOCLE.md) § 7 et dans [`AGENTS.md`](../../AGENTS.md).

## 4. Les règles et leur origine

| Règle | D'où elle vient |
|---|---|
| Rien dans la conversation pendant le travail ; seulement une question, un choix, un résultat | Demande du 2026-11-09. Déjà notée par la session longue de Learn-UI-UX : « aucun message chat sauf demande de validation » |
| Un fichier ou un lien à installer après chaque changement, sans que je le demande | ToolMap (2026-10-03, après dix étapes codées sans appareil : aucune version ne s'ouvrait), Activity-track, SuivieMAT (2026-10-28), Scam-Stop |
| Jamais deux fichiers avec le même numéro de version | ToolMap, demandé deux fois (2026-10-11, 2026-10-17). La règle manuelle avait été oubliée : la version monte seule depuis. Dossier Drive des APK : un nom par version |
| Une mise à jour s'installe par-dessus, sans désinstaller ; la clé de signature ne change pas | Suivie-Popote-Cie, SuivieMAT, ToolMap : une clé différente à chaque compilation imposait de désinstaller, donc de perdre les données |
| Aperçu validé avant le code pour tout ce qui se voit | ToolMap (2026-10-18) |
| Cinq variantes à chaque changement de design, choix demandé par notification | ToolMap et SuivieMAT (2026-10-26) |
| Le debug se fait sans aperçu et sans question | ToolMap (2026-10-24) |
| Petits pas : une tâche, une compilation. Idées pour plus tard hors des plans | ToolMap (2026-10-24) |
| Une phase ne démarre que sur demande ; rien n'est entamé d'avance | Suivie-Popote-Cie (`consigne/README.md`) |
| Une décision ne se modifie pas sans mon accord ; elle se révise par ajout | ScanList et SuivieMAT (décisions numérotées), ToolMap (ADR), cette Library (`AGENTS.md`) |
| Journal jamais réécrit, entrée la plus récente en haut | ToolMap, cette Library |
| Les documents terminés ou périmés se suppriment | ToolMap (2026-10-12, redemandé le 2026-10-16), Learn-UI-UX |
| Ne pas présenter comme vérifié ce qui ne l'a pas été ; rappeler la limite de test dans le message de fin | ScanList, SuivieMAT, Suivie-Popote-Cie, cette Library (« statut honnête ») |
| Un test instable se répare : ni relance, ni désactivation | ToolMap, Suivie-Popote-Cie |
| Copie la plus récente d'abord ; ne jamais écraser une copie plus récente | `CLAUDE.md` du dossier Drive et ToolMap (2026-10-28) |
| Miroir Drive du PC vers le Drive, jamais l'inverse ; un seul dossier pour tous les APK | `CLAUDE.md` du dossier Drive (2026-10-17) |
| Dépôt public : rien de privé, identité de commit `noreply` | ToolMap (2026-10-27), cette Library |
| Application en service : privé par défaut, publication sur feu vert donné pour cette version | Suivie-Popote-Cie |
| Chercher un moteur dans la Library avant d'en écrire un | `AGENTS.md` de cette Library |

## 5. Là où mes projets divergent

Ces écarts ne sont pas des erreurs : chaque projet a pris l'habitude du moment. Ils sont à trancher par moi, une fois, pour les projets à venir.

| Sujet | Ce qui existe aujourd'hui | Valeur par défaut proposée |
|---|---|---|
| Langue des messages de commit | Français dans ToolMap, SuivieMAT, Suivie-Popote-Cie et cette Library ; anglais dans Scam-Stop, Activity-track, Learn-UI-UX, Android-Suntrack | Celle du projet ; à choisir au démarrage d'un nouveau |
| Push | Géré par la session (ToolMap) ; sur demande (Scam-Stop) ; à chaque tour (sessions cloud) | Session cloud : toujours sur sa branche. PC : la règle du projet |
| Remise de l'APK | Fichier dans la conversation ; lien d'artefact ou de pré-version ; page du fichier déposé dans la branche ; dossier Drive | Un lien dès que le fichier dépasse la taille d'une pièce jointe |
| Type de fichier livré | Debug ; debug réduit ; version signée de publication | Le même d'une version à l'autre, toujours installable par-dessus |
| Numéro de version | À la main ; automatique ; nombre de commits | Automatique |
| Mémoire du projet | `docs/` (plan, journal, décisions) ; `consignes/` ; fiches de phase ; rien | `docs/` pour un nouveau projet ; l'existant reste tel quel |
| Branche livrée | `main` ; une branche de session jamais fusionnée | À dire dans le `CLAUDE.md` du projet |
| Langue de l'interface | Français ; anglais dans deux applications | Français |

## 6. Ce que la revue du 2026-11-09 a montré

- **La mémoire gonfle et se contredit.** Un même fait est recopié dans plusieurs fichiers et se périme dans la plupart. Certains `CLAUDE.md` dépassent largement la taille conseillée par Claude Code (moins de 200 lignes) alors qu'ils sont chargés en entier à chaque session. Une fois les règles communes en place, chaque `CLAUDE.md` peut se réduire à ce qui est propre au projet.
- **Ce qui attend un essai sur le téléphone n'a pas de liste.** Des dizaines de tâches restent « à vérifier », et des défauts visibles au premier essai sont trouvés tard. D'où le skill `recette-telephone`.
- **Chaque session cloud réinstalle le SDK Android.** Deux projets seulement ont un script ou un automatisme pour cela. Le script de démarrage de l'environnement cloud pourrait l'installer une fois pour tous les projets.
- **Le travail reste sur des branches jamais fusionnées.** Cette Library en avait trois ; plusieurs applications se livrent depuis une branche de session.
- **Une règle écrite ne suffit pas.** Celles qui tiennent sont devenues des automatismes : version qui monte seule, miroir qui refuse d'écraser plus récent, contrôle dans la CI.

## 7. Faire évoluer la méthode

- Une règle change parce que je le demande, ou parce qu'une correction revient dans deux projets.
- Elle s'écrit d'abord ici, avec sa date et son origine, puis dans [`SOCLE.md`](../../SOCLE.md) si elle vaut pour toute session, ou dans un skill si c'est une procédure.
- `SOCLE.md` reste court : il coûte du contexte à chaque session.
- Un skill nouveau attend dans `skills/_propositions/` jusqu'à mon accord.
- Procédures : [`AGENTS.md`](../../AGENTS.md), § 5.F et 5.G.
