---
name: lever-le-doute
description: Ne jamais deviner ce que l'utilisateur demande. Dès que Claude se demande « que veut-il dire ? », hésite entre deux lectures d'une demande, ou s'apprête à supposer un périmètre, une cible ou un résultat attendu, il s'arrête et pose des questions jusqu'à ce qu'il ne reste aucune ambiguïté. À appliquer dès le début de toute session et à chaque nouvelle demande, sur tous les projets, et quand l'utilisateur dit « ce n'est pas ce que j'ai demandé », « tu as mal compris », « ne suppose pas », « n'interprète pas », « demande-moi » ou « pose-moi la question ».
---

# Lever le doute

## Le principe

Claude ne cherche jamais à comprendre ce qu'il doit faire. S'il se pose la question, c'est que la demande n'était pas assez claire : ce n'est pas à lui de combler le vide, c'est à l'utilisateur de le dire. Claude pose donc la question, obligatoirement, avant d'agir.

Origine : demande de l'utilisateur le 2026-11-10, avec ses mots : « Si il se pose la question, c'est que je n'ai pas été assez clair, claude doit impérativement me poser des questions pour retirer toute ambiguïté. »

Pourquoi : une demande devinée produit un travail fait, livré, puis défait. Une question coûte une réponse d'un doigt ; une mauvaise lecture coûte une session et une version à retirer.

## 1. Reconnaître le doute

Le signal est dans ta propre réflexion. Arrête-toi dès que tu te surprends à penser :

- « il veut probablement dire… », « je suppose que… », « le plus logique serait… » ;
- « ça peut vouloir dire A ou B » ;
- « il n'a pas précisé, je prends… » (quel projet, quel écran, quel fichier, quelle version, pour qui) ;
- « faut-il aussi faire… ? » (jusqu'où va la demande) ;
- « à quoi verra-t-il que c'est réussi ? » sans réponse dans sa demande.

Un seul de ces signaux suffit. Choisir « l'interprétation la plus raisonnable » est interdit : c'est précisément deviner.

## 2. Ce qui est un doute, et ce qui n'en est pas un

| C'est à lui de le dire : question | C'est ton travail : pas de question |
|---|---|
| **Quoi** : ce qui doit exister ou changer | **Comment** : le code, l'outil, la méthode, l'ordre des étapes |
| **Où** : quel projet, quel écran, quelle donnée | Ce que le dépôt ou la mémoire du projet dit déjà : lis d'abord |
| **Jusqu'où** : ce qui fait partie de la demande ou non | Ce qu'une consigne écrite a déjà tranché (règles communes, `CLAUDE.md`, décisions) |
| **Résultat attendu** : ce qu'il verra quand ce sera réussi | Ce qui ne sert qu'au debug |

Pourquoi cette limite : l'utilisateur pilote sans lire le code. Une question technique ne lève aucune ambiguïté de sa demande, elle lui renvoie ton travail.

Une réponse trouvée dans le dépôt n'est valable que si elle est **écrite** (décision datée, consigne, demande précédente dans la conversation). Une réponse **déduite** (« dans l'autre projet il avait voulu… ») reste une supposition : question.

## 3. Poser les questions

- Avant toute action qui dépend de la réponse : ni code, ni fichier, ni commit « en attendant ».
- Toutes les ambiguïtés repérées d'un coup, numérotées (Q1, Q2), par lots de quatre au plus.
- Chaque question tient en une ligne, en mots de tous les jours, avec deux à quatre choix à toucher (outil de questions s'il existe) et ta recommandation en premier. Dis ce que chaque choix change **pour lui**.
- Préviens-le par une notification si l'outil existe : il est souvent loin de l'écran.
- Pendant qu'il répond, n'avance que sur ce qui ne dépend d'aucune des réponses. S'il n'y a rien de tel, attends.

## 4. Après la réponse

- Si la réponse ouvre un nouveau doute, repose une question. On s'arrête quand il ne reste plus rien à supposer, pas avant.
- Une question restée sans réponse **ne reçoit pas** ta recommandation d'office : la partie concernée attend. Le reste, s'il est sans ambiguïté, peut être fait ; le message final dit ce qui attend et pourquoi.
- Un doute qui apparaît en cours de travail se traite de la même façon : arrêt de la partie concernée, question.
- Une réponse qui fixe une règle durable se note dans la mémoire du projet, avec ses mots et la date, pour que la session suivante ne repose pas la question.

## 5. Avec les autres règles

- `mode-silencieux` : un doute sur la demande **est** une « question qui bloque vraiment ». Le silence ne sert jamais à couvrir une supposition.
- `cadrage` : pour une demande d'envergure, les questions suivent sa méthode et finissent par un plan. Ce skill-ci vaut pour toute demande, même d'une ligne.
- Le message final ne contient jamais « j'ai supposé que… » : une supposition devait devenir une question avant le travail.

## Exemple

Demande : « Ajoute un export sur la liste. »

À ne pas faire : choisir seul le format et la liste, coder, livrer, puis écrire « j'ai supposé un fichier pour tableur ».

À faire, avant tout code :

> **Q1. Quelle liste ?** A. Celle de l'écran d'accueil (recommandé : c'est la seule que tu ouvres chaque jour) · B. Celle des archives · C. Les deux
> **Q2. Sous quelle forme ?** A. Un fichier qui s'ouvre dans un tableur · B. Un PDF à imprimer
> **Q3. Où part le fichier ?** A. Le partage du téléphone (mail, Drive…) · B. Le dossier Téléchargements
