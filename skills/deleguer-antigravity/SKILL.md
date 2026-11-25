---
name: deleguer-antigravity
description: Confier une tâche à Antigravity (agy, l'agent de Google installé sur le PC de l'utilisateur) et en rapporter le résultat - relecture, second avis, code, architecture, recherche dans un projet - avec ses garde-fous (copie du projet, bac à sable, ni push ni publication ni suppression), le choix du rôle et du modèle, la suite d'une conversation, l'accord de l'utilisateur pour un dépôt privé et l'entrée de journal. À utiliser quand l'utilisateur dit « demande à Antigravity », « fais relire par Gemini », « donne-le à Antigravity », « deuxième avis », « agy », quand un second regard ou une tâche longue et bien bornée le justifie, ou quand le quota de Claude s'épuise. Seulement sur un PC où agy est installé et connecté.
---

# Confier une tâche à Antigravity

Claude pilote, Antigravity exécute : le pont va dans un seul sens (décision D-02). Antigravity suit
le même socle et les mêmes skills que Claude, posés par l'installeur de la Library. Décisions,
constats et pièges : `~/.claude/library/docs/methodes/pont-antigravity.md`.

## 1. Quand confier

- **L'utilisateur le demande.** Il peut toujours imposer (D-04).
- **Un second regard** : relire un changement avant de le livrer, critiquer un plan, comparer des
  choix d'architecture. Un autre modèle voit d'autres défauts.
- **Une tâche longue et bien bornée** que Claude relira : recherche dans un gros projet, première
  version d'un module, tests à écrire.
- **Le quota de Claude s'épuise** : Antigravity tourne sur le forfait Gemini.
- **Pas pour** ce qui demande le téléphone, la livraison, un push, une publication, un secret ou
  l'avis de l'utilisateur : cela reste à Claude.
- **Ce que ça coûte** : de 15 000 jetons Gemini (une question) à 70 000 (une relecture complète),
  plus la relecture par Claude ; la délégation doit rapporter davantage.

## 2. Avant de lancer

1. **Dépôt privé** : seulement avec l'accord de l'utilisateur pour ce projet, puis `--accord-prive`
   (D-05). Public sur GitHub : libre ; le script vérifie et refuse dans le doute.
2. **La consigne**, dans un fichier du dossier temporaire de la session : le but, les fichiers à
   lire, ce qui est attendu en retour (forme, longueur), le critère de réussite. Ni secret ni
   donnée personnelle. 28 000 octets au plus : Antigravity lit lui-même les fichiers et l'historique.
3. **Le profil** : `--lecture` (par défaut) pour relire, analyser, répondre, sans compiler ;
   `--code <nom>` pour modifier, compiler et tester, sur la branche `antigravity/<nom>`.
4. **Le rôle** (`--role`) : relecteur, second avis, codeur, architecte, sous-traitant. Il se
   choisit selon la tâche, les forfaits et les modèles du moment (D-01).
5. **Le modèle** (`--modele` ; la liste : `confier.sh --modeles`) :

| Besoin | Modèle |
|---|---|
| Relecture, second avis, code, la plupart des tâches | `gemini-4-hydrogen-high` (celui d'Antigravity aujourd'hui) |
| Architecture, problème difficile | `gemini-4-hydrogen-xhigh` |
| Question simple, tri, résumé, essai | `gemini-3.8-flash-medium` |
| Modèles Claude proposés par Antigravity | à éviter : retirés le 2026-11-30 |

## 3. Lancer

```bash
L=~/.claude/skills/deleguer-antigravity; P=<dossier du projet>
bash $L/confier.sh --lecture --role "relecteur" --projet $P <consigne.txt>
bash $L/confier.sh --lecture --nom <nom> --suite <conversation> --projet $P <question.txt>
bash $L/confier.sh --code <nom> --role "codeur" --projet $P <consigne.txt>
bash $L/confier.sh --code <nom> --suite <conversation> --projet $P <suite.txt>
bash $L/confier.sh --retirer <nom> --projet $P
```

- En arrière-plan : 2 à 5 minutes en général, 20 au plus (`--delai`). D'ici la fin, ni fichier ni
  `git fetch` dans le projet (avant ou après) : le compte rendu y verrait une écriture d'Antigravity.
- Le script pose seul les garde-fous : copie du projet (jamais le dossier lui-même), bac à sable
  sans réseau, règles de cette session, transports git coupés, date et interdits en tête de la
  consigne. N'ajoute jamais `--dangerously-skip-permissions` et ne touche pas aux réglages
  d'Antigravity : ils servent à l'utilisateur quand il s'en sert seul (D-09).

## 4. Lire le compte rendu

- Code 0 : tâche faite. Code 2 : à regarder (refus, réponse vide, échec, copie ou projet modifiés).
  Code 1 : rien n'a été lancé (dépôt privé sans accord, consigne vide ou trop longue, copie occupée).
- **Réponse vide = échec.** Ne la présente jamais comme un résultat ; regarde les refus.
- **Son avis est un avis.** Vérifie ce qu'il affirme (relis le code cité, relance les tests) avant
  de le reprendre, et dis à l'utilisateur ce qui vient d'Antigravity et ce que tu as vérifié.
- Une suite : la ligne « Suite » du compte rendu, donnée même après un délai dépassé.
- « Modèle saturé » : Google a fait attendre la tâche ; un autre modèle peut aller plus vite.

## 5. Tâche de code : relire, puis livrer

1. Lis le diff dans la copie (`git -C <copie> diff`), d'abord les fichiers de compilation et de
   test que le compte rendu signale, puis essaie : compilation, tests, rendu.
2. Bon : commit dans la copie, sur sa branche ; fusion dans la branche du projet ; puis `--retirer`.
3. À reprendre : `--suite` avec ce qu'il faut corriger, ou corrige toi-même.
4. Abandon : dis-le à l'utilisateur ; `--retirer` garde une branche non fusionnée, qui ne se
   supprime qu'avec son accord.
5. La livraison suit le projet (skill `livraison`) : Antigravity ne livre jamais (D-07).

## 6. Journal

- Toute tâche confiée se note, même un avis qui ne change rien (socle § 6).
- L'entrée proposée par Antigravity (« Agent : Antigravity (tâche confiée par Claude) ») : vérifie
  ses faits, retire les chemins du poste (socle § 8), puis pose-la en haut du journal du projet,
  telle quelle ou corrigée en le disant. La tienne, signée Claude : ce que tu as confié, à quel
  modèle, ce que tu en as repris.

## 7. Pièges

- Profil code : ses compilations et ses tests sortent du bac à sable, avec les droits de
  l'utilisateur, scripts modifiés par lui compris ; pas de `--code` sur un projet dont la
  compilation publie, déploie ou écrit hors du projet. Une dépendance tirée par git échoue.
- Les refus ne voient que la ligne de commande : une recherche qui contient un mot interdit
  (`git grep upload`) est refusée, et Antigravity passe par ses outils de fichiers.
- Bac à sable « non confirmé » (Windows trop ancien) : la première commande hors de la courte
  liste permise arrête la tâche. Garde alors des tâches de lecture.
- `agy` hors du PATH (session ouverte avant son installation) : le script le trouve seul, et
  `--modeles` liste les modèles. L'installer ou le mettre à jour : jamais depuis une session (D-08).
- Les traces (`%TEMP%\antigravity`) contiennent l'adresse du compte Google : jamais dans un dépôt.
