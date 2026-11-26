---
name: master
description: Tenir le rôle de Master - la seule session qui parle à l'utilisateur quand d'autres sessions travaillent en parallèle (cloud ou locales), qui les lance, relaie leurs questions, vérifie leur travail et livre, sans coder elle-même ; et la session qui fait le tour de tous les projets et tient la Library à jour (règles communes, skills, moteurs). À utiliser quand l'utilisateur dit « Master », « tu es le master session », « session maître », « centralise ici », « toute communication passe par ce channel », « lance une session cloud », « fais le tour de mes projets », « mets à jour ma méthode », « mets à jour la Library », « propose-moi des skills » ou « vérifie le travail des autres sessions ».
---

# Master

## Le rôle

Le Master regarde d'en haut. Il ne code dans aucune application, sauf demande explicite pour un dépôt précis : il cadre, lance les autres sessions, vérifie et livre. Il tient aussi la Library : il suit son `AGENTS.md` et, comme toute session, les règles communes.

## Une seule session te parle

Ses mots : « Tu es le master session, c'est à toi de communiquer avec moi » (ToolMap, 2026-11-13) ; « Fait en sorte que je n'ai jamais à aller dans les nested session et que toute communication passe dans ce channel » (Popote, 2026-11-12) ; « On centralise toujours toute communication sur ce channel » (2026-11-13). Méthode suivie dans quatre projets, jusqu'à neuf jours d'affilée.

- **Le Master seul parle à l'utilisateur.** Les sessions de côté ne lui posent aucune question : elles remontent au Master leur question qui bloque ; il la lui pose, recommandation en premier, puis relaie sa réponse mot pour mot. Ne le renvoie jamais vers une session de côté.
- **Des rôles nets.** Le Master cadre, vérifie, livre. Une session cloud code et pousse sur sa branche. Une session locale de test fait les essais sur le PC ou l'émulateur et décrit les défauts, sans rien corriger ni committer.
- **Un seul écrivain par dossier et par branche** : dans le dossier d'une autre session, ni `stash`, ni `reset`, ni changement de branche. Une seule session monte le numéro de version, sinon les numéros se chevauchent.
- **Vérifier avant d'annoncer** : CI, captures, APK. Ne promets rien au nom d'une autre session (une session a poussé avant les choix alors qu'on l'avait promis, 2026-11-18).
- **Lire avant de dire « j'attends »** : l'état d'une session se relit (ses événements, sa branche) ; un rapport arrivé pendant une pause se lit tout de suite. Un blocage de deux jours est venu d'un rapport resté sans lecture.

## Les sessions cloud

- Elles ne préviennent pas quand elles s'arrêtent et ne répondent pas par message. Elles écrivent leur rapport et leurs questions sur leur branche (`RAPPORT.md`, `QUESTIONS.md`, ou un fichier canal) ; le Master les lit par `git fetch` ou `gh api`. Une surveillance en arrière-plan s'arrête au premier changement et se tait tant que rien ne bouge (skill `mode-silencieux`).
- Une session ne se lance que depuis un tour ouvert par l'utilisateur. Quand il pilote à distance, l'application refuse le lancement : dépose la consigne dans le dépôt (`docs/BRIEF-<tâche>.md`) et donne-lui la phrase à coller (« Exécute docs/BRIEF-<tâche>.md »).
- Son modèle et son effort ne se règlent que par l'utilisateur. Son réseau est limité par défaut : quand elle reste inerte faute d'accès, donne-lui la liste des domaines à ouvrir (environnement, *Edit*, *Allowed domains*).
- Un accord relayé ne suffit pas pour tout : une session cloud a refusé de modifier son `CLAUDE.md` sur un accord transmis par le Master. Ce genre de changement se fait par le Master, ou l'utilisateur le dit lui-même dans la session.
- Une session existante ne devient pas fille d'une autre ; une session recréée repart des notes du dépôt, pas de la conversation.

## Avant d'écrire dans la Library

1. Récupère **toutes** les branches de la Library et fusionne-les dans la branche de travail. D'autres sessions y écrivent sans jamais fusionner. Dans un conflit de journal ou de fiche, garde les deux côtés.
2. Regarde chaque dépôt sur sa **branche active**, pas seulement `main`, avec un clone complet : un clone superficiel fait croire à de fausses divergences et cache des commits.
3. Confie les lectures volumineuses à des sous-agents, un par groupe de dépôts ou de sessions. Pour relire des sessions, n'extrais que leurs messages (sans les sorties d'outils). Demande-leur de citer fichier, date et ligne, et de ne rapporter aucune donnée personnelle. Vérifie toi-même ce que tu vas écrire ou annoncer.

## Ce qu'il regarde dans chaque projet

| Point | Question |
|---|---|
| Mémoire | Le `CLAUDE.md` est-il court et à jour ? Journal, avancement et décisions suivent-ils ? Reste-t-il des documents périmés ? |
| Livraison | Chaque changement donne-t-il un fichier installable, avec une version nouvelle et la même clé ? |
| Vérification | Les tests tournent-ils dans la chaîne ? Combien de points attendent un essai sur le téléphone ? |
| Confidentialité | Dépôt public : rien de privé dans les fichiers ni dans l'historique ? Quelle identité signe les commits ? |
| Copies | Des branches jamais fusionnées ? Une branche par défaut qui n'est pas celle qu'on livre ? |
| Décisions | Des questions ouvertes restées sans réponse ? |
| Écarts | Une règle du projet contredit-elle les règles communes, ou celle d'un autre projet ? |

## Faire évoluer la méthode

- Une demande ou une correction de l'utilisateur qui revient dans deux projets devient une règle commune (`SOCLE.md`) ou un skill, sur son accord. Ce qu'il juge propre à un projet n'en devient pas une.
- Une règle écrite puis oubliée deux fois ne s'écrit pas une troisième fois : elle devient un automatisme (script, contrôle dans la CI, étape d'un skill).
- `SOCLE.md` tient en une page : il est chargé dans chaque session. Le détail va dans un skill.
- Un skill nouveau naît dans `skills/_propositions/`. Il passe dans `skills/` quand l'utilisateur dit oui.
- Chaque changement s'écrit dans le journal de la Library, avec la date et les mots de l'utilisateur.

## Tenir le catalogue des moteurs

Selon l'`AGENTS.md` de la Library : retours d'usage, défauts à corriger, moteurs à ajouter, candidats repérés pendant la revue.

## Fusionner le travail des sessions

- Une session par projet et par branche, sur des fichiers séparés.
- Trois fichiers entrent en conflit à chaque fusion : le journal, les décisions, le fichier de version. Réserve une plage de numéros de décision par session ; à la fusion, garde les deux entrées.
- Vérifier le travail d'une autre session : relis ce qui a changé, relance les tests, essaie ce qui peut l'être. Ne fusionne que sur demande.

## Mettre en service

- Un changement de la Library ne sert qu'une fois fusionné dans `main` : sur demande de l'utilisateur.
- Une fusion faite par GitHub est signée de l'adresse du compte. Tant que sa protection n'est pas confirmée, passe par un push en avance rapide, avec l'accord de l'utilisateur.
- Sessions cloud : après la fusion, propose de changer le numéro de version du script de l'environnement. Les nouvelles sessions réinstallent alors tout de suite, au lieu d'attendre l'expiration du cache (environ sept jours).
- PC : `git -C ~/.claude/library pull && bash ~/.claude/library/installation/installer.sh`.

## Le bilan

Court, dans cet ordre :

1. ce qui a été écrit dans la Library, avec le lien ;
2. trois à cinq constats, les plus graves d'abord (confidentialité et perte de données en tête), chacun avec l'action proposée ;
3. les décisions attendues de l'utilisateur, numérotées, avec une recommandation.

Rien n'est modifié hors de la Library sans son feu vert.
