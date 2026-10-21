# Chapitre 4 — La recherche utilisateur

> « Si vous ne vérifiez pas, vous devinez. » (NN/g, à propos du tree testing [S12].)

## 4.1 Le cadre à trois dimensions de NN/g

Christian Rohrer (NN/g) [S7] classe **20 méthodes** de recherche selon trois axes :

| Axe | Pôle A | Pôle B |
|---|---|---|
| **Attitudinal ↔ comportemental** | « ce que les gens *disent* » | « ce que les gens *font* » |
| **Qualitatif ↔ quantitatif** | observation directe (*comment corriger ?*) | mesure indirecte par un instrument (*combien ?*) |
| **Contexte d'usage** | usage naturel / scripté / limité / sans usage du produit | |

### Attitudinal vs comportemental

Les deux diffèrent souvent. L'objectif de l'attitudinal est de comprendre ou mesurer les
croyances déclarées, ce qui est limité par ce dont les gens sont conscients et ce qu'ils
acceptent de dire. La plupart des **études d'usabilité doivent s'appuyer sur le
comportement** [S7]. Exemples :

- **Card sorting** : révèle le *modèle mental* des utilisateurs sur un espace d'information.
- **Sondages** : mesurent et catégorisent des attitudes.
- **Focus groups** : peu utiles pour l'usabilité, mais donnent une vue « à chaud » d'une marque.
- **A/B testing** : compare des variantes sur des échantillons aléatoires, tout le reste étant
  constant → comportement pur.
- **Eye-tracking** : comment les yeux parcourent une interface.
- **Tests d'usabilité** et **études de terrain** : mélange des deux, à pencher vers le
  comportement.

### Qualitatif vs quantitatif

- **Qualitatif** : on observe ou entend directement ; on peut poser des questions, creuser,
  adapter le protocole. Mieux adapté à *comment corriger*.
- **Quantitatif** : les données sont prédéterminées (temps de tâche, succès, clic sur un élément
  donné) et analysées mathématiquement. Mieux adapté à *combien* et à la priorisation.

### Contexte d'usage

1. **Usage naturel** : minimiser l'interférence, plus de validité externe, moins de contrôle.
2. **Usage scripté** : cibler un parcours précis ; un *benchmark* est très scripté pour
   produire des métriques fiables.
3. **Usage limité** : une version partielle du produit (design participatif, tests de concept,
   card sorting, tree testing).
4. **Sans usage** : études de marque, attributs esthétiques.

### La dimension temporelle

- Début de projet : méthodes **génératives** (quelle direction ?).
- Phase de conception : méthodes **formatives** (comment améliorer ?).
- Produit mesurable : méthodes **sommatives** (a-t-on progressé par rapport à avant ?).

## 4.2 Le test d'usabilité

Wikipédia [S16] : technique du design centré utilisateur consistant à tester le produit **sur
des utilisateurs**. Ce n'est pas du recueil d'avis : on **observe des gens essayer de faire
quelque chose**, au lieu de demander « Comprenez-vous ceci ? ». Exemple : pour tester une
notice de montage, on donne la notice et la boîte de pièces, et on regarde le montage.

Historique [S16] : né de l'étude des commandes d'avions durant la Seconde Guerre mondiale ;
standard de l'IHM dans les années 1980 ; laboratoires dédiés dans les années 1990 ; tests à
distance avec le web et les smartphones (années 2000-2010).

### Les trois composantes du test (Nielsen) [S2]

1. des **utilisateurs représentatifs** ;
2. des **tâches représentatives** ;
3. l'**observation** (« taisez-vous et laissez-les parler »).

Tester **individuellement**, laisser chacun résoudre seul ses problèmes ; l'aider contamine le
résultat.

## 4.3 Combien d'utilisateurs ? La règle des cinq

Nielsen (2000) [S8], reprenant Nielsen & Landauer (INTERCHI'93) : le nombre de problèmes
d'usabilité trouvés avec *n* utilisateurs suit `N × (1 − (1 − L)ⁿ)`, où *N* est le nombre total de
problèmes et *L* la proportion découverte par un seul utilisateur — **31 % en moyenne** sur les
projets étudiés.

| Utilisateurs testés | Part des problèmes trouvés (L = 31 %) |
|---|---|
| 0 | 0 % (« zéro utilisateur donne zéro information ») |
| 1 | ≈ 31 % |
| 3 | ≈ 67 % |
| 5 | ≈ 85 % |
| 15 | ≈ 99,6 % |

*(Valeurs calculées avec la formule de [S8] ; l'article donne explicitement 85 % pour cinq
utilisateurs.)*

Les conclusions pratiques de [S8] :

- **Ne pas tout dépenser dans une grosse étude.** Avec le budget de 15 utilisateurs, faire
  **3 études de 5**, en corrigeant entre chaque. L'objectif réel est d'*améliorer* le design,
  pas de documenter ses faiblesses.
- La deuxième étude vérifie que les corrections fonctionnent (on peut introduire de nouveaux
  problèmes en corrigeant) et permet d'aller plus profond (architecture d'information, flux).
- **Pas un seul utilisateur** : risque d'être trompé par un comportement atypique ; le coût fixe
  de préparation se répartit mieux sur plusieurs personnes.
- **Plusieurs groupes très différents** (ex. enfants et parents) : tester chaque groupe —
  3 à 4 utilisateurs par catégorie pour deux groupes, 3 par catégorie à partir de trois groupes.

## 4.4 Les personas

Définition NN/g [S10] : un persona est une description **fictive mais réaliste** d'un
utilisateur type ou cible. Il exploite la tendance humaine à être plus captivé par des cas
concrets que par des généralités.

- Il doit être **fondé sur la recherche** (études de terrain, sondages, études longitudinales,
  entretiens) — jamais inventé.
- À créer **le plus tôt possible**, idéalement en équipe.
- Contenu courant : nom, âge, photo, accroche, niveau d'expérience, contexte d'usage (choisi
  ou imposé ? fréquence ? appareil ?), objectifs et préoccupations, citations.
- **Règle d'or** : chaque détail doit avoir une raison. Le vin préféré d'un persona n'aide pas
  à concevoir un intranet, mais peut servir pour une place de marché de sommeliers.
- Usages ultérieurs : vocabulaire commun (« le prénom du persona sert de raccourci »), relecture
  d'experts (sans remplacer les tests), critères de recrutement, segmentation des analytics.

## 4.5 La carte de parcours (journey map)

NN/g [S9] : visualisation du processus par lequel passe une personne pour atteindre un objectif.
Cinq éléments clés :

1. **Acteur** — un persona, un seul point de vue par carte ;
2. **Scénario + attentes** ;
3. **Phases du parcours** (ex. e-commerce : découvrir, essayer, acheter, utiliser, demander de
   l'aide) ;
4. **Actions, état d'esprit, émotions** — l'émotion se trace en **une courbe** des hauts et bas ;
5. **Opportunités** — insights, responsables, métriques.

Variantes : la *carte d'expérience* est plus générale (indépendante d'un produit) ; la carte de
parcours est spécifique à un acteur et un scénario [S9].

## 4.6 Card sorting : découvrir les modèles mentaux

NN/g [S11] : les participants classent des cartes étiquetées en groupes qui ont du sens pour
eux, puis nomment les groupes. Utile **pour concevoir** une architecture d'information (le tree
testing sert à **évaluer** une architecture existante).

Processus en quatre étapes : créer les cartes → faire regrouper → faire nommer les groupes →
analyser.

Bonnes pratiques de [S11] :

- **30 à 50 cartes** : assez pour être riche, pas assez pour fatiguer.
- **Éviter des mots identiques** dans plusieurs étiquettes (les participants les regrouperaient
  mécaniquement : « Toyota Camry », « Toyota Matrix »…).
- Faire un **test pilote**.
- Formats : en présentiel (fiches) ou **à distance** (outils web, plus courant car l'analyse est
  automatisée).
- Exemple : un loueur de voitures dont les catégories techniques (« économique »,
  « intermédiaire », « pleine taille ») sont obscures pour les clients.

## 4.7 Tree testing : vérifier la trouvabilité

NN/g [S12] : test par tâches sur une **arborescence nue** (sans design ni contenu) présentée
en accordéons ; le participant clique jusqu'à l'endroit où il pense trouver la ressource. Deux
ingrédients : l'arbre et les tâches.

Types de tâches : recherche de ressources clés ; tâches « de passe-passe » pour évaluer
l'*information scent* d'une catégorie ; zones à risque ; comparaison d'étiquettes ; tâche
d'échauffement (qui sert aussi à écarter les participants inattentifs). Pour chaque tâche on
définit la ou les bonnes réponses, ce qui permet de calculer automatiquement le taux de
réussite.

## 4.8 Mesurer : SUS, A/B, Kano

### System Usability Scale (SUS) [S13]

Questionnaire de **10 énoncés** (échelle de Likert) créé par John Brooke (Digital Equipment
Corporation, 1986). Un score unique de **0 à 100**, qui permet de comparer des systèmes
différents. Calcul : `SUS = 2,5 × (20 + Σ items impairs − Σ items pairs)` avec chaque item noté de
0 à 4 (items impairs : positifs ; pairs : négatifs). Limite : très général, donc il dit *si*
mais pas *pourquoi*. Il existe une lecture en deux facteurs (usabilité et apprenabilité) [S13].

Énoncés (version standard) : envie d'utiliser fréquemment ; complexité inutile ; facilité
d'utilisation ; besoin d'aide technique ; fonctions bien intégrées ; trop d'incohérences ;
apprentissage rapide ; système lourd à utiliser ; confiance ; besoin d'apprendre beaucoup avant
de commencer.

### A/B testing [S15]

Expérience aléatoire contrôlée à deux variantes (ou plus) ; on compare l'effet d'**une seule
variable** (ex. bouton bleu vs vert). Exemple de [S15] : deux e-mails de 1 000 destinataires,
5 % contre 3 % de réponse — mais la rigueur exige un **test statistique** pour savoir si l'écart
est significatif et non dû au hasard. Inadapté aux situations non expérimentales.

### Modèle de Kano [S14]

Développé dans les années 1980 par Noriaki Kano ; classe les attributs selon leur effet sur la
satisfaction :

| Catégorie | Effet | Exemple (de la source) |
|---|---|---|
| **Indispensable** (must-be) | Neutre si bien fait, très négatif si absent | Freins d'une voiture |
| **Unidimensionnelle** | Satisfait si présent, insatisfait si absent | Accélération |
| **Attractive** | Ravit si présente, sans mécontentement si absente (souvent non exprimée) | Radar de stationnement |
| **Indifférente** | Aucun effet | Épaisseur de la cire d'un carton de lait |
| **Inverse** | Un haut niveau déplaît à certains | Trop de technologie pour qui veut le modèle de base |

Utile pour **prioriser** : sécuriser les « indispensables » avant d'investir dans les
« attractives ».

## 4.9 Tableau de synthèse : quelle méthode, quand ?

| Besoin | Méthode | Effort | Source |
|---|---|---|---|
| Comprendre un contexte réel | Étude de terrain, entretiens | moyen | S7 |
| Trouver les défauts d'une maquette | Test d'usabilité, 5 personnes | faible | S2, S8 |
| Concevoir un menu | Card sorting | moyen | S11 |
| Vérifier un menu | Tree testing | moyen | S12 |
| Suivre un parcours | Journey map | moyen | S9 |
| Mesurer une satisfaction globale | SUS | faible | S13 |
| Choisir entre deux variantes en production | A/B test | élevé (trafic) | S15 |
| Prioriser des fonctions | Kano | moyen | S14 |

> **Pour une app hors-ligne mono-utilisateur** (cas des apps JRRocks) : le test d'usabilité à
> 3-5 personnes avec le matériel réel (téléphone, gants, luminosité) est de loin le meilleur
> rapport effort/information. L'A/B test suppose un trafic que ces apps n'ont pas.

---
*Sources du chapitre : S2, S7–S16.*
