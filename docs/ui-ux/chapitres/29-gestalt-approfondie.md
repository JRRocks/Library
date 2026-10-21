# Chapitre 29 — La Gestalt en détail : histoire, lois et figure-fond

> Le chapitre 7 donne les règles d'usage ; celui-ci donne l'origine scientifique et la précision
> des lois, utiles pour juger quand elles s'appliquent et quand elles s'affrontent.

## 29.1 Origine et idée centrale [S181][S37]

La **psychologie de la forme** (Gestalt) est une école et une théorie de la perception qui souligne
que l'on traite **des ensembles entiers** (des « configurations »), non des composants isolés. Née
au début du XXᵉ siècle en Allemagne et en Autriche en **rejet** du structuralisme de Wundt et
Titchener (qui décomposait la conscience en éléments simples).

- Fondateurs : **Max Wertheimer, Kurt Koffka, Wolfgang Köhler**. Le concept de *Gestalt-qualität*
  vient de **Christian von Ehrenfels** (1890) : une mélodie est plus que la somme de ses notes ;
  elle reste la même transposée dans une autre tonalité.
- **Mouvement phi** : Wertheimer a montré (1912) qu'on perçoit un **mouvement apparent** entre deux
  barres de lumière qui s'allument tour à tour ; cette publication marque le début de la
  Gestalt.
- Idée-clé : « *le tout est autre chose que la somme de ses parties* » (Koffka) — « parce que
  sommer est une procédure dénuée de sens, tandis que la relation tout-partie est signifiante ». Le
  tout est **perceptivement premier** : on voit le cercle avant les points qui le composent.
- **Principe de totalité** : l'expérience consciente doit être considérée globalement.
- **Insight** : Köhler a montré que des animaux peuvent apprendre par « soudaine compréhension » de
  la **structure d'un problème**.

## 29.2 Les lois de l'organisation perceptive [S181]

Wertheimer a défini des principes expliquant comment on perçoit les objets selon la
**similarité, la proximité et la continuité**. Autres lois s'y sont ajoutées :

| Loi | Énoncé (d'après la source) | Exemple de la source | Application d'interface |
|---|---|---|---|
| **Proximité** | Les objets proches sont perçus comme un groupe | 72 cercles perçus comme **un groupe de 36 à gauche** et **trois groupes de 12 à droite** ; très utilisé dans les logos | Espaces entre groupes de champs |
| **Similarité** | Les éléments semblables (forme, couleur, taille…) sont regroupés | 36 cercles équidistants, 18 sombres et 18 clairs : on voit **six lignes horizontales** | Même style pour toutes les actions de même rang |
| **Fermeture** | On perçoit comme complets des objets incomplets | Cercle et rectangle aux contours interrompus ; **logo IBM** formé de barres horizontales | Icônes en pointillé, logos |
| **Symétrie** | L'esprit perçoit des formes symétriques autour d'un centre ; deux éléments symétriques non reliés sont connectés mentalement | Six crochets perçus comme **trois paires** | Mises en page équilibrées |
| **Destin commun** | Les éléments qui se déplacent dans la même direction sont groupés | Points allant vers le haut / vers le bas = deux unités | Animations de groupe |
| **Continuité** (bonne continuation) | Les éléments alignés sont intégrés en un tout ; deux objets qui se croisent restent **deux entités ininterrompues** | Deux clés croisées | Alignement ; lignes qui guident l'œil |
| **Expérience passée** | La catégorisation visuelle selon l'expérience | Les lettres « L » et « I » côte à côte lues comme deux lettres, pas un « U » | Conventions apprises (loi de Jakob) |

D'autres lois s'ajoutent en design d'interface : **région commune** et **connexité uniforme**
(chap. 6 et 7) [S32].

## 29.3 Organisation figure-fond [S181]

Forme d'organisation perceptive qui interprète les éléments en termes de **formes et de positions
relatives** dans les surfaces : le champ est structuré en **figure** (qui ressort, au premier plan) et
**fond** (qui recule). Travail pionnier du danois **Edgar Rubin** (vase de Rubin — figure 29.1). On perçoit
comme figures les parties **convexes, symétriques, petites et fermées**.

![Cube de Necker et vase de Rubin (multistabilité)](../images/Multistability.png)
*Figure 29.1 — Le vase de Rubin : figure et fond s'inversent. Wikimedia Commons (domaine public).*

> **Pour le design** : une **modale** doit être perçue comme figure (voile sur le fond, ombre,
> contraste) ; un **bouton principal** est une figure (petit, fermé, plein) sur un fond calme. Si
> figure et fond s'inversent (par exemple un champ de texte sans bordure sur fond de même couleur),
> l'utilisateur ne sait plus où se trouve l'élément actif.

## 29.4 Quand les lois se contredisent (NN/g) [S33]

La **proximité peut l'emporter sur les autres indices** (similarité de couleur ou de forme). Dans une
interface, si deux boutons de même couleur (similarité) sont éloignés (proximité) d'un autre groupe,
la proximité décide du groupement perçu. Règle retenue : **la proximité l'emporte sur la similarité** (NN/g) ; en cas de doute, tester au « squint test » (chap. 7).

## 29.5 Gestalt et accessibilité

- Les groupements **ne doivent pas reposer sur la couleur seule** (chap. 9).
- Les groupements doivent aussi être **déterminables par programme** (WCAG 1.3.1 : structure et
  relations véhiculées visuellement disponibles aux technologies d'assistance — par ex. `fieldset` /
  `legend`, regroupements `mergeDescendants` sur Android) [S46][S80].

## 29.6 Check-list Gestalt

- [ ] L'espace *dans* un groupe est plus petit que l'espace *entre* groupes.
- [ ] Des éléments de même rôle ont le même style ; des éléments de rôles différents, des styles différents.
- [ ] Les conteneurs (cartes, bordures) sont utilisés avec modération.
- [ ] L'élément actif/principal est clairement la « figure ».
- [ ] Le groupement visuel correspond au groupement structurel (lecteurs d'écran).

---
*Sources du chapitre : S32, S33, S37, S46, S80, S181.*
