# Chapitre 31 — Comportement, nudge, persuasion et gamification

> Comment une interface influence-t-elle ce que l'on fait ? Trois cadres : le **modèle de Fogg**
> (motivation, capacité, déclencheur), la **théorie du nudge** (architecture du choix) et la
> **gamification** (éléments de jeu). Chacun est puissant — donc à manier avec l'éthique du chap. 22.

## 31.1 Le modèle comportemental de Fogg [S187]

B. J. Fogg (Stanford Behavior Design Lab, ex-*Persuasive Technology Lab*) a publié en **2009** son
modèle FBM : **trois conditions** sont nécessaires pour qu'un comportement se produise :

| Condition | Contenu | Leviers |
|---|---|---|
| **Motivation** | Envie de faire | plaisir/douleur, espoir/peur, acceptation/rejet social |
| **Capacité** (ability) | Facilité de faire | temps, argent, effort physique, « cycles cérébraux », déviance sociale, non-routine |
| **Déclencheur** (prompt / trigger) | Signal pour agir **maintenant** | notification, bouton, rappel |

Les comportements se produisent quand les trois convergent. **Règle d'application** : s'il y a un
problème, **rendre l'action plus facile (capacité)** est presque toujours plus efficace et plus
respectueux que d'augmenter la pression (motivation). Fogg a aussi proposé en 2011 la méthode des
**« Tiny Habits »** : démarrer par de **tout petits pas** pour installer des habitudes durables.
Historique : il a fondé la **captologie** (étude des ordinateurs comme technologies persuasives, livre
de 2003 : *macrosuasion* et *microsuasion*) et proposé dès 1998 des **responsabilités éthiques** pour
les concepteurs de systèmes persuasifs [S187].

## 31.2 La théorie du nudge [S185]

- **Concept** : concevoir l'**environnement de décision** (« architecture du choix ») pour influencer
  le comportement **sans interdire d'option ni modifier significativement les incitations
  économiques**. Popularisé en 2008 par Richard Thaler et Cass Sunstein (*Nudge*).
- **Définition** : « un nudge est tout aspect de l'architecture du choix qui modifie le comportement des
  gens de façon prévisible, sans interdire aucune option ni changer significativement leurs incitations
  économiques ; pour compter comme simple nudge, l'intervention doit être **facile et peu coûteuse à
  éviter**. Les nudges ne sont pas des obligations. » Exemples : mettre les fruits à hauteur des yeux
  (nudge) ≠ interdire la malbouffe (non) ; la **mouche gravée dans les urinoirs** de Schiphol.
- **Fondement** : Kahneman — **Système 1** (rapide, automatique, très sensible à l'environnement) et
  **Système 2** (lent, réflexif). Quand une situation est complexe ou pressée, le Système 1 décide ;
  un nudge aménage l'environnement pour que, quand on utilise des heuristiques, le choix résultant soit
  le meilleur.
- **Écart valeur-action** : le comportement ne correspond pas toujours aux intentions.
- **Efficacité controversée** : après correction du **biais de publication**, une méta-analyse ne
  trouve plus d'effet moyen ; des nudges (comme l'**effet de défaut**) peuvent être très efficaces,
  d'autres presque nuls ; une méta-analyse d'études **non publiées** (plus de 23 millions de
  personnes) montre des effets **nettement plus faibles** que dans la littérature publiée ; les
  nudges **personnalisés** semblent plus efficaces [S185].

### Nudge, sludge et motif trompeur
| Notion | Définition | Lien |
|---|---|---|
| **Nudge** | Encourager un comportement positif sans contrainte | Légitime si **transparent**, **facile à éviter**, au bénéfice de l'utilisateur |
| **Sludge** | « Friction » qui empêche d'obtenir un résultat meilleur pour soi | Voir chap. 22 [S154] |
| **Motif trompeur** | Tromper, égarer, faire honte, entraver | Voir chap. 22 |

> **Test éthique du nudge** : *l'utilisateur serait-il d'accord s'il comprenait ce que je fais et
> pourquoi ?* Si non, c'est une manipulation.

## 31.3 La gamification [S186]

**Définition** : intégrer des **éléments et principes de conception de jeu** (points, badges,
classements) dans des contextes **non ludiques** pour accroître **engagement et motivation**.
Usages : productivité, flow, apprentissage, financement participatif, usabilité, exercice
physique…

**Éléments de jeu** [S186] :

| Élément | Rôle | Remarques de la source |
|---|---|---|
| **Points** | Représentent numériquement la progression ; **retour continu et immédiat** | Types : expérience, échangeables, réputation |
| **Badges** | Représentations visuelles de réussites ; objectifs ou symboles de statut ; influencent le parcours | Peuvent exercer une **influence sociale** |
| **Classements** (leaderboards) | Classent les joueurs | **Motivation mitigée** : motivants quand il reste peu de points au niveau suivant, **démotivants** en bas de classement ; l'effet positif est plus probable entre joueurs de **niveau voisin** |
| **Graphiques de performance** | Performance par rapport à ses propres résultats précédents | Favorisent l'**orientation vers la maîtrise** |
| **Histoires signifiantes** | Contexte narratif | Donnent du sens au-delà des points |
| **Avatars** | Représentation du joueur | Identité, communauté |
| **Coéquipiers** | Coopération | Engagement |

Autres ressorts : **choix significatifs**, **tutoriels d'accueil**, **difficulté croissante**,
**narration** [S186]. Techniques liées aux envies de socialisation, apprentissage, **maîtrise,
compétition, accomplissement, statut, expression de soi, altruisme, clôture**.

### Lien avec les lois de l'UX
- **Goal-gradient** : l'effort augmente à l'approche du but → barre de progression (chap. 6).
- **Zeigarnik** : le travail inachevé reste en mémoire → indicateurs d'avancement.
- **Peak-end** : soigner le moment de réussite et la fin (chap. 6).
- **Flow** : équilibre défi/compétence (chap. 6).

### Quand gamifier une app d'inventaire ?
- **Oui, avec modération** : un **compteur d'avancement** (« 37/50 relevés »), un **retour sonore
  et visuel** à chaque validation, un écran de **fin de session** satisfaisant. Ce sont des
  **retours d'information** utiles avant d'être des jeux.
- **Non** : classements entre techniciens (démotivants pour le bas du classement [S186]), badges
  gratuits, pression à la rapidité qui augmenterait les erreurs.

## 31.4 Check-list

- [ ] Chaque « incitation » repose sur une **capacité accrue** avant une motivation accrue.
- [ ] Les nudges sont **transparents et faciles à éviter**.
- [ ] La progression est visible (goal-gradient) sans classement humiliant.
- [ ] Les déclencheurs (notifications) sont **rares, utiles, désactivables**.
- [ ] On teste l'effet réel (A/B ou comparaison) : un nudge n'est pas efficace par principe [S185].

---
*Sources du chapitre : S32, S154, S185–S187.*
