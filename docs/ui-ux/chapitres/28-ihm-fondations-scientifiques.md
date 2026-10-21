# Chapitre 28 — L'interaction homme-machine (IHM / HCI) : fondations scientifiques

> L'UX/UI est l'application pratique d'une discipline de recherche : l'**IHM** (*Human–Computer
> Interaction*, HCI). Connaître ses idées donne un cadre pour juger les « règles » des chapitres
> précédents.

## 28.1 Définition et origine [S178]

- L'IHM est le processus par lequel les gens utilisent et engagent des systèmes informatiques ; la
  recherche couvre la **conception et l'usage** des technologies, avec les **interfaces entre
  personnes et ordinateurs**. Elle se situe à l'intersection de l'informatique, des **sciences du
  comportement**, du **design** et des études des médias.
- Le terme a été popularisé par **Stuart Card, Allen Newell et Thomas P. Moran** dans *The Psychology
  of Human–Computer Interaction* (1983) ; première utilisation connue en **1975** (Carlisle).
- Il traduit l'idée d'un **dialogue ouvert** entre l'utilisateur et l'ordinateur, par analogie avec
  l'interaction entre humains.
- **Définition de l'ACM** : « une discipline qui s'intéresse à la conception, l'évaluation et la mise
  en œuvre de systèmes informatiques interactifs pour un usage humain, et à l'étude des principaux
  phénomènes qui les entourent ». La **satisfaction de l'utilisateur** est un aspect clé.
- Connaissances mobilisées : côté machine (graphisme, systèmes d'exploitation, langages…) ; côté
  humain (théorie de la communication, design graphique et industriel, linguistique, sciences
  sociales, **psychologie cognitive**, psychologie sociale, facteurs humains).
- Elle garantit une interaction **sûre et efficace** avec des technologies complexes (aviation,
  santé).

## 28.2 La boucle d'interaction et les modalités [S178]

La **boucle d'interaction** inclut notamment le **feedback** : des boucles à travers l'interface qui
**évaluent, modèrent et confirment** les processus qui vont de l'humain à l'ordinateur et retour.

Les modalités étudiées :
| Famille | Exemples |
|---|---|
| **Visuelles** | Analyse des expressions faciales, suivi du mouvement du corps, **reconnaissance de gestes**, **détection du regard** (eye-tracking) |
| **Audio** | Reconnaissance de la parole et du locuteur, analyse des émotions auditives, bruits humains (soupirs, rires), interaction musicale |
| **Capteurs** | Interaction au stylet, souris/clavier, manettes, capteurs de mouvement, **capteurs haptiques**, capteurs de pression, goût/odorat |

Les interfaces dominantes aujourd'hui : **GUI** (graphiques), **CLI** (ligne de commande, désormais
surtout pour administrateurs et programmeurs), **VUI** (vocales), **GUI multimodales** [S178].

## 28.3 Objectifs et débats [S178]

- Améliorer l'**usabilité** : **apprenabilité, facilité de repérage, efficacité d'usage** ; méthodes de
  conception, d'implémentation, d'évaluation et de comparaison d'interfaces.
- Deux visions : **cognitiviste** (aligner les interfaces sur les **modèles mentaux** des activités) ;
  **post-cognitiviste** (les aligner sur les **pratiques et valeurs socioculturelles**).
- On débat de plus en plus de **ce qu'est l'usabilité**, de son rapport aux valeurs sociales et
  culturelles et de savoir **quand elle n'est pas souhaitable**. Doug Engelbart : « *Si la facilité
  d'usage était le seul critère valable, les gens s'en tiendraient aux tricycles et n'essaieraient
  jamais les bicyclettes.* » [S178]
- **Quand une interface défaillante tue** : la **catastrophe de Three Mile Island** (fusion nucléaire)
  a été imputée au moins en partie à la conception de l'interface homme-machine ; certains accidents
  d'aviation sont liés au choix d'instruments ou de manettes de gaz **non standard**, même théoriquement
  meilleurs, car les pilotes avaient intériorisé la disposition « standard » [S178] — illustration
  directe de la **loi de Jakob** (chap. 6).

## 28.4 Principes de conception et méthodologies [S178]

Principes expérimentaux à appliquer en évaluant ou en concevant :

1. **Se concentrer tôt sur les utilisateurs et les tâches** : combien d'utilisateurs, lesquels
   (quelqu'un qui n'utilisera jamais l'interface n'est pas un utilisateur valide), quelles tâches et à
   quelle fréquence.
2. **Mesurer empiriquement** : tester avec de **vrais utilisateurs**, avec des mesures
   **quantitatives** (nombre d'utilisateurs, temps de tâche, nombre d'erreurs).
3. **Concevoir de façon itérative** : *concevoir → tester → analyser → recommencer*, jusqu'à obtenir
   une interface raisonnablement conviviale.

Méthodologies :
- **Théorie de l'activité** : cadre pour penser le contexte où se déroulent les interactions.
- **Conception centrée utilisateur** (UCD) et **conception participative** : voir chap. 2 et 4.
- **Principes de conception d'interface** : résistance (contraintes), simplicité, visibilité,
  affordance, cohérence, structure, feedback [S178].
- **Conception sensible aux valeurs** (*value sensitive design*) : tient compte de **ceux qui
  utilisent le design** et de **ceux qu'il touche** directement ou indirectement ; trois types
  d'examens : conceptuels, empiriques, techniques.

## 28.5 Les forces de changement [S178]

Baisse des coûts de matériel, **miniaturisation** (portabilité), moindre consommation, nouvelles
technologies d'affichage, matériel spécialisé, réseaux et calcul distribué, **usage par des
personnes extérieures à la profession informatique**, innovation des modes d'entrée (voix, geste,
stylet), accès des populations défavorisées.

## 28.6 Sujets actuels [S178]

- **AR / VR / MR / XR** : réalité augmentée, virtuelle, mixte ; recherche sur la présence, les
  techniques d'interaction, l'effet de l'immersion sur la **charge cognitive**.
- **Interaction humain-IA** : interfaces **transparentes, explicables, éthiquement responsables** ;
  **humain dans la boucle** (chap. 23).
- **Accessibilité** : l'accessibilité bénéficie aussi à l'usabilité pour tous.
- **Informatique sociale**, **interfaces cerveau-ordinateur**.
- **Interactions de sécurité (HCISec)** : améliorer l'usabilité des fonctions de sécurité ; la
  mauvaise usabilité vient souvent de fonctions **ajoutées après coup**, **bricolées en urgence**, sans
  assistant ou conçues sans expertise en sécurité.

## 28.7 Ce qu'il faut en retenir pour un projet d'app

1. **L'usabilité n'est pas une préférence mais une exigence de sûreté** dans les contextes critiques ;
   la nôtre est moins grave, mais le raisonnement est le même.
2. **Ne pas innover sur ce que l'utilisateur a déjà intériorisé** (loi de Jakob, accidents d'aviation).
3. **Mesurer** (temps, erreurs) et **itérer**.
4. **Documenter les valeurs** de conception (accessibilité, vie privée, honnêteté).

---
*Sources du chapitre : S2, S26, S178.*
