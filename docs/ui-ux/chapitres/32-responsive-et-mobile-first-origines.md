# Chapitre 32 — Responsive design et mobile-first : origines et principes

> Le chapitre 10 donne les recettes. Celui-ci explique **pourquoi** elles existent, d'après les textes
> fondateurs (Marcotte, Wroblewski) et la documentation MDN.

## 32.1 Le texte fondateur : *Responsive Web Design* (Marcotte, 2010) [S188]

Ethan Marcotte ouvre sur une citation de John Allsopp (*A Dao of Web Design*) : le contrôle que les
designers connaissent dans l'imprimé est la **fonction d'une limitation** du papier ; le web n'a pas ces
contraintes, il faut **embrasser cette flexibilité** et « accepter le flux et le reflux des choses ».

Idée centrale : plutôt que de **tailler des designs déconnectés** pour une liste croissante d'appareils
(« un site iPhone », puis « un site iPad »…), traiter les appareils comme **des facettes d'une même
expérience**. Marcotte propose une analogie avec l'**architecture réactive** (*responsive
architecture*) où l'espace répond aux personnes qui le traversent.

**Trois ingrédients** (cités dans [S58]) :
1. **Grille fluide** (largeurs relatives, en pourcentages plutôt qu'en pixels) ;
2. **Images flexibles** (dimensionnées en unités relatives, pour ne pas déborder) ;
3. **Requêtes de média** (*media queries*, CSS3).

Constats de l'époque : un design **fluide** ne s'adapte pas bien aux changements de **résolution** ou
de fenêtre : en dessous de 800 × 600, l'illustration de l'en-tête se recadre, la navigation passe à la
ligne de façon disgracieuse ; sur écran large, les images deviennent démesurées. **« Aucun design,
fixe ou fluide, ne s'adapte parfaitement au-delà du contexte pour lequel il a été conçu. »** La
requête de média est « un commentaire conditionnel pour le reste d'entre nous » : on corrige
chirurgicalement la mise en page quand elle sort de sa résolution idéale initiale.

## 32.2 Mobile first (Wroblewski, 2009) [S189]

Luke Wroblewski donne trois raisons de concevoir **d'abord** pour le mobile :

1. **Le mobile explose** (chiffres de l'époque : croissance considérable du trafic de données mobile ;
   adoption d'Internet mobile huit fois plus rapide que celle du fixe ; plus de smartphones vendus que
   de PC fin 2011 — *statistiques de 2009, à lire comme un contexte historique, non comme des données
   actuelles*) ;
2. **Le mobile force à se concentrer** : « il n'y a pas de place sur un écran de 320 × 480 pour des
   éléments superflus » ; on **priorise** données et actions essentielles, sans « débris d'interface » ;
3. **Le mobile étend les capacités** : GPS, boussole, multitouch, accéléromètre — des capacités que
   les navigateurs de PC n'avaient pas.

> **Nuance de NN/g** : « *mobile-first n'est pas mobile-only* » — ne pas porter tel quel sur le bureau
> des solutions mobiles qui y fonctionnent moins bien (navigation cachée, chap. 11, [S66]).

## 32.3 Ce que dit MDN aujourd'hui [S190][S191]

- **Définition** : le *responsive web design* est une approche pour que les pages s'affichent bien sur
  **toutes les tailles et résolutions d'écran** tout en garantissant une bonne usabilité ; la « façon
  de concevoir pour un web multi-appareils ».
- **Le HTML est fondamentalement fluide** : sans CSS, le texte se réorganise dans la fenêtre. Mais de
  longues lignes en plein écran sont difficiles à lire (limiter la longueur de ligne) ; une largeur fixe
  entraîne des barres de défilement. D'où le **balisage `viewport`** (chap. 10).
- **Les appareils d'aujourd'hui** (bureau, portable, mobile, tablette, montre…) : il faut concevoir de
  façon **défensive** pour les tailles courantes **et les inconnues** ; les contraintes de **batterie et de
  bande passante** subsistent ; l'**expérience utilisateur** reste centrale (un voyageur veut l'heure
  d'un vol, pas un globe 3D animé).
- **Requêtes de média** : types `screen`, `print`, `all` (les autres types sont dépréciés) ;
  **caractéristiques** : `width`, `height`, `orientation`, `resolution`, `hover`, `pointer`,
  `any-pointer`, `prefers-color-scheme`, `prefers-contrast`, `prefers-reduced-motion`,
  `prefers-reduced-transparency`, `forced-colors`, `display-mode`, etc. Opérateurs : `and`, `not`,
  `only`, virgule (= « ou »). Elles servent aussi à adapter aux **préférences de l'utilisateur**
  (mouvement réduit, contraste, thème) — qui sont des **réglages d'accessibilité**.

## 32.4 Équivalents sur Android

| Web | Android (Compose) |
|---|---|
| Point de rupture `@media (min-width: …)` | **Classes de taille de fenêtre** (compact < 600 dp, medium, expanded…) [S55] |
| `prefers-color-scheme` | Thème **DayNight** / `isSystemInDarkTheme()` [S83][S89] |
| `prefers-reduced-motion` | Réglage système « Supprimer les animations » [S103] |
| `any-pointer: coarse` | Toucher par défaut ; clavier/souris à prendre en charge sur grands écrans [S56] |
| Grille fluide (flex/grid) | `Row`/`Column`/`LazyGrid`/mises en page adaptatives (panneaux liste-détail) [S56] |

## 32.5 Principes à retenir

1. **Un seul contenu, plusieurs mises en page** : pas de version « mobile » et de version « bureau »
   dupliquées.
2. **Priorités d'abord** : concevoir pour la plus petite contrainte oblige à hiérarchiser (Wroblewski).
3. **Mesurer en fonction de la fenêtre disponible**, pas de l'appareil (classes de taille).
4. **Respecter les préférences utilisateur** (thème, mouvement, contraste, taille de texte).
5. **Tester aux extrêmes** : plus petit écran, plus grand, paysage, texte 200 %, écran partagé.

---
*Sources du chapitre : S55, S56, S58, S66, S83, S89, S103, S188–S191.*
