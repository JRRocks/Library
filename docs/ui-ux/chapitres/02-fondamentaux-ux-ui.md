# Chapitre 2 — Fondamentaux : UX, UI, usabilité, utilité

> Ce chapitre pose le vocabulaire. Tout le reste du dossier s'appuie sur ces définitions.
> Les sources sont numérotées `[Sx]` et listées dans [`../SOURCES.md`](../SOURCES.md).

## 2.1 Qu'est-ce que l'expérience utilisateur (UX) ?

La définition de référence vient de Don Norman et Jakob Nielsen (Nielsen Norman Group, 1998)
[S1] : l'**expérience utilisateur** « englobe tous les aspects de l'interaction de
l'utilisateur final avec l'entreprise, ses services et ses produits ». Trois idées en
découlent :

1. **La première exigence est de répondre exactement au besoin du client, sans fioriture ni
   complication.** Vient ensuite seulement la simplicité et l'élégance qui rendent un produit
   « agréable à posséder, agréable à utiliser ».
2. **La vraie UX va au-delà de ce que le client dit vouloir** et de la liste de
   fonctionnalités cochées.
3. **Une bonne UX exige la fusion de plusieurs disciplines** : ingénierie, marketing, design
   graphique, design industriel et design d'interface.

### UX ≠ UI

Les mêmes auteurs insistent pour distinguer l'expérience globale de l'interface (UI) [S1].
L'exemple donné : un site de critiques de films dont l'interface de recherche est parfaite
offre pourtant une mauvaise UX à qui cherche un petit film indépendant, si la base de données
ne contient que les films des grands studios. **L'UI est une partie, essentielle, de l'UX ;
elle n'en est pas le tout.**

| Notion | Question à laquelle elle répond | Exemple dans une app d'inventaire |
|---|---|---|
| **UX** (expérience) | Le produit résout-il mon vrai problème, de bout en bout ? | Relever dix marquages en quelques secondes, sans reposer la main sur l'écran |
| **UI** (interface) | Ce que je vois et touche est-il clair, lisible, cohérent ? | Boutons assez grands, liste rouge/verte lisible en plein soleil |
| **Usabilité** | Est-ce facile à apprendre, rapide, sûr, agréable ? | Un nouvel utilisateur scanne sa première étiquette sans notice |
| **Utilité** | Le produit fait-il ce dont j'ai besoin ? | Export `.csv` exploitable dans un tableur |

## 2.2 L'usabilité : cinq composantes

Jakob Nielsen, *Usability 101* [S2], définit l'usabilité comme un **attribut de qualité** qui
mesure la facilité d'utilisation d'une interface (le mot désigne aussi les méthodes pour
l'améliorer). Elle se décompose en cinq composantes :

| Composante | Question |
|---|---|
| **Apprenabilité** (learnability) | Quelle facilité pour accomplir les tâches de base dès la première rencontre ? |
| **Efficacité** (efficiency) | Une fois appris, à quelle vitesse accomplit-on les tâches ? |
| **Mémorabilité** (memorability) | Après une pause, avec quelle facilité retrouve-t-on son aisance ? |
| **Erreurs** (errors) | Combien d'erreurs, de quelle gravité, et avec quelle facilité s'en remet-on ? |
| **Satisfaction** (satisfaction) | L'usage est-il agréable ? |

À cela s'ajoute l'**utilité** (utility) : le produit offre-t-il les fonctions nécessaires ?
Nielsen résume : *usabilité = facilité et agrément d'usage des fonctions ; utilité = présence
des bonnes fonctions ; usefulness (« être utile ») = usabilité + utilité.* Une chose facile à
utiliser mais qui ne fait pas ce qu'on veut est inutile ; une chose qui pourrait tout faire
mais reste impossible à manipuler l'est aussi.

### Pourquoi investir dans l'usabilité ?

D'après [S2] :

- Sur le web, « l'usabilité est une condition de survie » : si c'est difficile, les gens
  **partent**. Personne ne lit un manuel ; quitter est la première ligne de défense.
- Première loi du e-commerce : si l'utilisateur ne trouve pas le produit, il ne l'achète pas.
- Sur un intranet, l'usabilité est une question de **productivité** des employés.
- Bonne pratique citée : consacrer environ **10 % du budget** d'un projet de conception à
  l'usabilité ; en moyenne, cela **double** les indicateurs de qualité visés pour un site
  web (score d'amélioration de 2,6).

### Comment l'améliorer ?

La méthode la plus simple et la plus utile est le **test utilisateur** [S2] :

1. recruter des **utilisateurs représentatifs** ;
2. leur faire exécuter des **tâches représentatives** ;
3. **observer** où ils réussissent et où ils butent — sans les aider, sans orienter leur
   regard : toute aide contamine le test.

Nielsen précise : *écouter ce que les gens disent est trompeur ; il faut regarder ce qu'ils
font.* Les groupes de discussion (focus groups) sont une mauvaise façon d'évaluer
l'usabilité d'une interface.

### Quand travailler l'usabilité ?

À **chaque étape** [S2] : tester l'ancienne version avant de refaire ; tester des concurrents ;
mener une étude de terrain ; faire des **prototypes papier** ; affiner par itérations
(basse puis haute fidélité) en testant chaque version ; contrôler le design par rapport aux
guidelines établies ; retester la version finale (« des problèmes subtils s'insinuent
toujours à l'implémentation »). Ne **pas** différer les tests jusqu'à l'implémentation
complète : les problèmes critiques seraient alors structurels, donc impossibles à corriger
sans refonte.

Où tester ? Une salle de réunion fermée suffit ; « un bloc-notes est le seul équipement
nécessaire ». Si l'on mène au moins une étude par semaine, un laboratoire dédié devient
rentable.

## 2.3 Les principes de Don Norman

Le livre *The Design of Everyday Things* (1988) [S3] décrit la psychologie du « bon » et du
« mauvais » design. D'après la synthèse de Wikipédia sur le design centré utilisateur [S4],
ses recommandations principales sont :

- **simplifier la structure des tâches** pour que les actions possibles à chaque instant
  soient intuitives ;
- **rendre les choses visibles** : le modèle conceptuel du système, les actions, leurs
  résultats et le retour d'information ;
- obtenir de **bonnes correspondances** (mappings) entre le résultat voulu et l'action requise ;
- **adopter et exploiter les contraintes** du système.

Norman reviendra plus tard, dans *Emotional Design*, sur ce qu'il jugeait trop réducteur
(en particulier la mise à l'écart de l'esthétique) [S4].

### Vocabulaire à retenir (Norman)

| Terme | Sens | Application |
|---|---|---|
| **Affordance** | Ce que l'objet « permet » de faire | Un bouton en relief invite à appuyer |
| **Signifiant** | L'indice perceptible qui révèle l'affordance | Un libellé, une ombre, une icône |
| **Mapping** | Lien entre commande et effet | Interrupteurs alignés comme les lampes |
| **Contrainte** | Ce qui limite les actions possibles | Champ numérique qui refuse les lettres |
| **Feedback** | Retour immédiat après l'action | Bip + changement de couleur après un scan |
| **Modèle conceptuel** | L'image mentale du fonctionnement | « La liste rouge devient verte quand je lis » |

*(Définitions usuelles du domaine, cohérentes avec [S3] ; Wikipédia consacre une page à
chaque notion.)*

## 2.4 Le design centré utilisateur (UCD)

Le terme *user-centered design* a été forgé par Rob Kling en 1977 puis adopté dans le
laboratoire de Norman à l'université de Californie à San Diego ; il s'est diffusé avec les
livres de Norman de 1986 et 1988 [S4]. L'UCD est un cadre où les **objectifs d'usabilité, les
caractéristiques des utilisateurs, l'environnement, les tâches et le flux de travail** reçoivent
une attention soutenue à chaque étape, avec des **tests à chaque étape**.

Les six principes qui garantissent une conception centrée utilisateur [S4] :

1. la conception repose sur une **compréhension explicite** des utilisateurs, des tâches et
   des environnements ;
2. les utilisateurs sont **impliqués** tout au long ;
3. la conception est **pilotée et affinée par l'évaluation** auprès des utilisateurs ;
4. le processus est **itératif** ;
5. la conception traite **l'expérience globale** ;
6. l'équipe réunit des **compétences pluridisciplinaires**.

Les quatre phases générales du processus [S4] :

```
Spécifier le contexte d'usage → Spécifier les exigences → Concevoir des solutions
        ▲                                                         │
        └──────────── Évaluer le produit (tests, retours) ◄──────┘
```

Le cycle se répète pour affiner. Des modèles voisins : la **conception coopérative**
(tradition scandinave, depuis 1970, designers et utilisateurs à égalité), la **conception
participative** (Amérique du Nord, conférences depuis 1990) et la **conception contextuelle**
(recueillir des données auprès de vrais clients en situation réelle) [S4].

## 2.5 Qualité d'une interface : ce que disent les standards

Les références de ce dossier convergent sur une même hiérarchie de besoins :

1. **Ça marche et ça répond à un besoin réel** (utilité).
2. **C'est compréhensible sans effort** (apprenabilité, cohérence).
3. **C'est efficace et sûr** (rapidité, prévention des erreurs).
4. **C'est accessible à tous** (voir chapitre 14).
5. **C'est agréable** (esthétique, émotion) — non en dernier par importance, mais parce que
   l'effet esthétique-usabilité (chap. 6) ne compense jamais une utilité absente.

> **À retenir pour les apps JRRocks.** La règle de ScanList — « le chemin critique du scan ne
> doit jamais être suspendu » — est une application directe de la composante *efficacité* et
> du *feedback immédiat* : la qualité UX se mesure à la tâche vitale de l'application, pas à
> la décoration.

---
*Sources du chapitre : S1, S2, S3, S4 — voir `../SOURCES.md`.*
