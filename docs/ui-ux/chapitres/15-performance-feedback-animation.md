# Chapitre 15 — Performance perçue, feedback et animation

> La performance est une **fonction d'interface** : une app lente est une app qui, pour
> l'utilisateur, ne marche pas. Ce chapitre regroupe les seuils de temps qui découlent de la
> perception humaine, les métriques modernes (Core Web Vitals, RAIL) et les règles d'animation.

## 15.1 Les seuils de perception du temps

Les limites viennent de la perception humaine — **les mêmes depuis plus de 50 ans** [S75][S100].

| Durée | Perception | Conséquence |
|---|---|---|
| **0 – 16 ms** | Le mouvement est suivi à la perfection ; **60 images/s** = animation fluide (16 ms par image, dont ~6 ms de rendu navigateur ⇒ ~10 ms pour produire l'image) | Viser **10 ms** par image [S100] |
| **0 – 100 ms** | Réaction **instantanée** ; au-delà, le lien action-réaction se rompt [S100] ; Nielsen : 0,1 s = limite de la manipulation directe [S75] | Traiter les entrées en **moins de 50 ms** pour garantir une réponse visible en 100 ms [S100] |
| **100 – 1000 ms** | Progression **naturelle et continue** (charger une page, changer de vue) [S100] ; Nielsen : le flot de pensée n'est pas interrompu mais on perd le sentiment d'agir directement [S75] | Aucun feedback spécial requis, sauf pour > 50 ms de traitement [S100] |
| **≥ 1 s** | L'utilisateur **perd le fil** de sa tâche [S100] | Montrer un indicateur |
| **≥ 10 s** | **Frustration**, abandon probable [S100] ; limite d'attention focalisée [S75] | Indicateur de **progression en pourcentage** [S75] ; permettre de faire autre chose |
| **400 ms** | Seuil de Doherty : productivité maximale | Voir chap. 6 [S27] |

Les attentes varient avec le matériel et le réseau : sur mobile en 3G lente, charger en 5 s est
« un objectif plus réaliste » [S100].

### Le modèle RAIL (Google) [S100]

Quatre aspects du cycle de vie : **R**esponse, **A**nimation, **I**dle, **L**oad.

| Aspect | Objectif | Consignes |
|---|---|---|
| **Response** (réponse) | Compléter la transition lancée par l'utilisateur en **100 ms** | Traiter les événements en **< 50 ms** ; pour toute action > 50 ms, **donner un retour** ; pas toujours répondre immédiatement : on peut utiliser les 100 ms pour du travail coûteux — sans bloquer |
| **Animation** | **10 ms** par image | « Ne rien faire quand c'est possible, le minimum absolu sinon » ; l'animation inclut défilement, glissement, pincement |
| **Idle** (repos) | **Maximiser le temps de repos** | Travail différé par tranches de **≤ 50 ms**, interruptible par l'utilisateur |
| **Load** (chargement) | Contenu **interactif en moins de 5 s** | Charger le minimum, le reste en temps libre |

Google précise que **Core Web Vitals** est désormais l'approche recommandée pour fixer des
objectifs (RAIL conserve sa valeur pédagogique) [S100].

## 15.2 Core Web Vitals (web) [S99]

Trois métriques de qualité d'expérience, mesurées **sur le terrain** :

| Métrique | Mesure | Seuil « bon » |
|---|---|---|
| **LCP** — Largest Contentful Paint | Performance de **chargement** | ≤ **2,5 s** après le début du chargement |
| **INP** — Interaction to Next Paint | **Interactivité** | ≤ **200 ms** |
| **CLS** — Cumulative Layout Shift | **Stabilité visuelle** | ≤ **0,1** |

Évaluation au **75ᵉ percentile** des chargements de page, séparément mobile et ordinateur ; la
page « passe » si les trois seuils sont atteints. INP a remplacé FID comme métrique stable en
2024. Un indicateur stable n'est pas éternel : il peut être retiré au profit d'un meilleur [S99].

> **Lien avec l'UX** : un CLS élevé, c'est un bouton qui bouge sous le doigt au moment du
> toucher. Réserver les dimensions d'images et de zones qui chargent (chap. 10, [S57]).

## 15.3 Android : les mêmes principes, côté application [S79]

- **Démarrage** : l'app charge vite ou affiche un retour (indicateur) si le démarrage dépasse
  **2 s**.
- **Rendu** : **60 images/s** (16 ms ou moins par image).
- **Pas de blocage du fil d'interface** (ANR) ; surveiller *Android Vitals* dans la console Play.
- Retour audio dans la seconde suivant la demande (lecture).

> **Règle projet.** Les opérations lourdes (écriture en base, export, décodage) se font **hors du
> fil d'interface** ; l'UI reste réactive et confirme l'action immédiatement. C'est la logique
> du chemin critique de scan (« jamais suspendu »).

## 15.4 Donner un retour : choisir l'indicateur

Résumé des règles (chap. 11 pour le détail) :

| Durée attendue | Retour approprié | Source |
|---|---|---|
| < 100 ms | Aucun retour, juste le résultat | [S75] |
| 0,1 – 1 s | Changement d'état immédiat (pressé, couleur) | [S62][S75] |
| 1 – 10 s | Indicateur d'activité (spinner), **squelette** pour une page entière | [S74][S75] |
| > 10 s | **Barre de progression en pourcentage** + possibilité d'annuler/faire autre chose | [S75] |
| Durée inconnue | Progression en quantité absolue ; en dernier recours, indicateur non spécifique | [S75] |
| Entre 2 et 10 s | Un vrai pourcentage peut être excessif (**inertie d'affichage**) | [S75] |

## 15.5 Microinteractions (NN/g, 2018) [S104]

Définition : **paires déclencheur-retour** (*trigger-feedback*) où le déclencheur est une action
de l'utilisateur ou un changement d'état du système, et le retour est une réponse **étroitement
ciblée**, petite et contextuelle (généralement visuelle). Le concept et son modèle de conception
viennent de Dan Saffer (livre *Microinteractions*, 2014).

- Elles servent à **montrer l'état du système**, **prévenir les erreurs** et **communiquer la
  marque**, et encouragent l'engagement.
- Exemples : barre de défilement, alarme numérique, animation « tirer pour rafraîchir »,
  animation de balayage.
- **Ne sont pas** des microinteractions : éléments statiques sans déclencheur, GIF, parcours à
  plusieurs actions, un lecteur vidéo entier (mais son contrôle du volume l'est). Un bouton
  **sans retour** n'est pas une microinteraction.

## 15.6 Animer sans nuire

### Quand et pourquoi animer
- L'animation sert à **expliquer** (d'où vient cet élément, où va-t-il), **confirmer** une action
  et **guider l'attention** — non à décorer.
- Material fixe des durées de **50 à 1000 ms**, croissantes avec la **distance/surface**, et des
  courbes qui décélèrent à l'entrée et accélèrent à la sortie (chap. 13, [S87]).

### Performance des animations web [S101]
- Animer de préférence **`transform`** (translate, rotate, scale) et **`opacity`** ; **éviter les
  propriétés qui déclenchent mise en page ou peinture** (`left`, `top`, `width`…) sinon la
  fluidité est compromise.
- `will-change` : à n'utiliser que **en dernier recours**, sur des éléments qui changent
  réellement, et à retirer ensuite.
- Vérifier dans les outils de développement : une valeur « Rendering » non nulle indique du
  travail de mise en page.

### Accessibilité du mouvement [S102][S103]
- **Troubles vestibulaires** : plus de 35 % des adultes en connaissent une forme à 40 ans ;
  les animations de grands objets (zoom, panoramique) peuvent déclencher des malaises.
- **Photosensibilité** : jamais plus de **3 flashs par seconde** (WCAG 2.3.1) ; tester avec PEAT
  (Photosensitive Epilepsy Analysis Tool) si clignotements ou alternances clair/sombre rapides.
- **Mouvement non essentiel** : le supprimer ; s'il reste, offrir **pause/arrêt/masquage** pour ce
  qui démarre seul, dure plus de **5 s** et fait partie d'autres éléments (WCAG 2.2.2).
- **Requête `prefers-reduced-motion`** : détecte le réglage système (macOS/iOS « Réduire les
  animations », Android 9+ « Supprimer les animations », Windows « Effets d'animation ») ;
  valeur `reduce` → remplacer le mouvement par un **fondu discret** plutôt qu'un zoom ou
  panoramique [S103].
- **Sous Android** : lire le réglage d'échelle d'animation du système et le respecter.

## 15.7 Check-list

- [ ] Retour visible < 100 ms sur toute action ; indicateur au-delà de 1 s.
- [ ] Pas de travail lourd sur le fil d'interface ; 60 images/s.
- [ ] Squelette ou indicateur sur tout chargement perceptible ; jamais d'écran vide.
- [ ] Dimensions réservées ; pas de saut de mise en page (CLS ≤ 0,1 sur le web).
- [ ] Animations de `transform`/`opacity`, durées proportionnées, `prefers-reduced-motion` respecté.
- [ ] Rien ne clignote plus de 3 fois par seconde.

---
*Sources du chapitre : S27, S57, S62, S74, S75, S79, S87, S99–S104.*
