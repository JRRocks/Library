# Chapitre 13 — Material Design 3 en détail

> Material Design est le système de design de Google, base de l'interface Android moderne.
> Ce chapitre s'appuie sur la **documentation officielle de la bibliothèque Material Components
> pour Android** (dépôt GitHub) et sur la documentation Android Developers. Le site
> `m3.material.io` est rendu côté client et n'a pas pu être lu automatiquement ; ses guides
> sont donc cités par lien, pas paraphrasés [S91].


## 13.1 Les trois sous-systèmes du thème

Un thème M3 comprend **trois sous-systèmes** : **palette de couleurs**, **typographie** et
**formes** [S89]. Les composants Material lisent ces valeurs ; en changeant le thème, on change
**tous** les composants qui l'utilisent. Compose l'implémente avec le composable
`MaterialTheme(colorScheme, typography, shapes)` [S89]. S'y ajoutent le **mouvement** (motion)
et l'**élévation** [S87].

Les termes « Material Design 3 », « Material 3 » et « M3 » sont interchangeables ; **M3
Expressive** est une extension qui ajoute des mises à jour de thématisation, composants, mouvement
et typographie, et qui complète le style visuel d'Android 16 [S89].

## 13.2 Couleur

### Principes [S85][S89]

- La base d'un schéma de couleurs est un jeu de **cinq couleurs clés**, chacune reliée à une
  **palette tonale de 13 tons** utilisée par les composants.
- Les **trois groupes d'accent** : **Primary**, **Secondary**, **Tertiary**, chacun avec 4 à 5
  **rôles** (ex. `colorPrimary`, `colorOnPrimary`, `colorPrimaryContainer`,
  `colorOnPrimaryContainer`).
- Les **rôles « on-… »** sont les couleurs des textes et icônes posés **sur** la couleur
  correspondante : l'accessibilité des combinaisons est garantie par construction.
- La **couleur dynamique** (Material You) dérive un schéma du fond d'écran de l'utilisateur
  [S89]. Générer un schéma depuis une couleur de marque est possible via l'outil
  **Material Theme Builder**, qui exporte les fichiers `Color.kt` et `Theme.kt` [S89].

### Rôles supplémentaires qui structurent l'interface [S85]

| Famille | Rôles | Usage |
|---|---|---|
| **Erreur** | `error`, `onError`, `errorContainer`, `onErrorContainer` | États d'erreur |
| **Contours** | `outline`, `outlineVariant` | Bordures, séparateurs |
| **Surfaces** | `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`, `surfaceInverse`, `onSurfaceInverse` | Fonds et textes généraux |
| **Conteneurs de surface** | `surfaceContainerLowest`, `…Low`, `surfaceContainer`, `…High`, `…Highest`, `surfaceDim`, `surfaceBright` | Niveaux de **profondeur tonale** |
| **Inverse** | `primaryInverse`… | Éléments à contraste inversé (snackbar) |
| **Fixed** | `primaryFixed`, `primaryFixedDim`… | Couleurs identiques en clair et sombre |

**Valeurs de base (palette « baseline »)** [S85] : en clair, `primary` = ton 40, `onPrimary` =
blanc, `primaryContainer` = ton 90, `onPrimaryContainer` = ton 10 ; en sombre, `primary` = ton 80,
`onPrimary` = ton 20, `primaryContainer` = ton 30, `onPrimaryContainer` = ton 90. Autrement dit
**le même rôle change de ton entre clair et sombre** — c'est ce qui préserve le contraste.

**Surfaces tonales plutôt qu'ombres.** À partir de la version 1.11.0-alpha02, les composants M3
utilisent par défaut les **rôles de surface tonale** (`colorSurfaceContainer*`) au lieu des
« superpositions d'élévation » qui mélangeaient surface et primaire [S85]. La profondeur se lit
donc par des **teintes**, pas seulement par des ombres.

## 13.3 Typographie

L'échelle typographique de M3 (détail au chapitre 8) a **15 styles** : display (57/45/36 sp),
headline (32/28/24), title (22/16/14), body (16/14/12), label (14/12/11). Une échelle
« emphasized » existe pour les actions et titres [S42]. Les composants lisent des attributs
comme `textAppearanceBodyMedium` ; **on change une police pour toute l'app en la définissant dans
le thème**, non composant par composant [S42]. Android O+ permet les **polices téléchargeables**
(collection Google Fonts) sans les embarquer dans l'APK [S42].

## 13.4 Formes

L'échelle de formes M3 définit des **rayons d'angle** [S86] :

| Nom | Rayon |
|---|---|
| None | 0 dp |
| Extra small | 4 dp |
| Small | 8 dp |
| Medium | 12 dp |
| Large | 16 dp |
| Large increased | 20 dp |
| Extra large | 28 dp |
| Extra large increased | 32 dp |
| Extra extra large | 48 dp |
| Full | 50 % (cercle / pilule) |

Familles de coins : **arrondi** (rounded) ou **coupé** (cut). Chaque composant a une forme par
défaut (carte = *medium* 12 dp ; boîte de dialogue = *extra large* 28 dp) ; on la change au
niveau du thème, pas à la main [S86][S90].

## 13.5 Mouvement (motion)

Le système de mouvement Material fournit des **durées**, des **courbes d'accélération** (easing),
des **ressorts** et des **transitions** pour que les animations soient cohérentes [S87].

### Durées (16 valeurs) [S87]

| Groupe | Valeurs |
|---|---|
| Short 1–4 | 50, 100, 150, 200 ms |
| Medium 1–4 | 250, 300, 350, 400 ms |
| Long 1–4 | 450, 500, 550, 600 ms |
| Extra long 1–4 | 700, 800, 900, 1000 ms |

**Règle** : la durée doit **croître avec la surface ou la distance parcourue** par l'animation.

### Courbes (7) [S87]

| Courbe | Valeur | Usage |
|---|---|---|
| Standard | `cubic-bezier(0,2 ; 0 ; 0 ; 1)` | Animations utilitaires qui commencent et finissent à l'écran |
| Standard decelerate | `(0 ; 0 ; 0 ; 1)` | Animations utilitaires qui **entrent** à l'écran |
| Standard accelerate | `(0,3 ; 0 ; 1 ; 1)` | Animations utilitaires qui **sortent** |
| Emphasized | chemin spécifique | Animations « M3 » courantes sur place |
| Emphasized decelerate | `(0,05 ; 0,7 ; 0,1 ; 1)` | Entrées stylisées |
| Emphasized accelerate | `(0,3 ; 0 ; 0,8 ; 0,15)` | Sorties stylisées |
| Linear | `(0 ; 0 ; 1 ; 1)` | Mouvement simple, non stylisé |

**Logique** : ce qui **entre** décélère (arrive vite, s'installe doucement) ; ce qui **sort**
accélère ; ce qui se déplace sur l'écran utilise la courbe standard.

### Ressorts (physique) [S87]

Six ressorts, en **trois vitesses** (rapide, par défaut, lent) × **deux types** :
- **spatial** : déplace quelque chose à l'écran (position x/y) — peut osciller ;
- **effects** : anime couleur ou opacité — **ne doit pas dépasser** la valeur cible (l'alpha ne
  doit pas rebondir au-delà de 100 %).

| Attribut | Amortissement | Raideur | Usage |
|---|---|---|---|
| `motionSpringFastSpatial` | 0,9 | 1400 | Petits composants (interrupteurs, boutons) |
| `motionSpringFastEffects` | 1 | 3800 | Effets de petits composants |
| `motionSpringDefaultSpatial` | 0,9 | 700 | Animations couvrant une partie de l'écran (feuille inférieure, tiroir) |
| `motionSpringDefaultEffects` | 1 | 1600 | Effets correspondants |
| `motionSpringSlowSpatial` | 0,9 | 300 | Animations plein écran |
| `motionSpringSlowEffects` | 1 | 800 | Effets plein écran |

Exemple de la doc : pour l'animation d'un bouton pressé, utiliser deux ressorts —
`FastSpatial` pour la forme/taille et `FastEffects` pour la couleur [S87].

### Transitions de navigation : quatre motifs [S87]

| Transition | Rôle |
|---|---|
| **Container transform** | Relie deux éléments (ex. carte → écran de détail) en transformant le conteneur partagé |
| **Shared axis** (axe partagé, « avant/arrière ») | Relation spatiale ou de navigation entre éléments : glissement sur un axe (x, y, z) |
| **Fade through** (fondu enchaîné, « niveau supérieur ») | Entre éléments **sans relation forte** les uns avec les autres |
| **Fade** (« entrée et sortie ») | Éléments qui apparaissent ou disparaissent dans les limites de l'écran |

## 13.6 Thème sombre dans Material

D'après [S88] : hériter de `Theme.Material3.DayNight` (dynamique) ou définir `Light`/`Dark` dans
`values`/`values-night`. Les fonds et surfaces du thème sombre **de base sont gris foncé, pas
noirs**, ce qui augmente la visibilité des ombres et réduit la fatigue visuelle. Bénéfices :
économie de batterie sur OLED, moindre fatigue visuelle, meilleure visibilité en faible lumière.

## 13.7 Catalogue de composants (bibliothèque Android) [S90]

| Composant | Rôle (selon la documentation) | Variantes |
|---|---|---|
| **Boutons** | Actions et choix « d'un seul toucher » | Commun, groupes de boutons, bouton scindé, groupes à bascule, icône, **FAB** |
| **FAB** (Floating action button) | **Action principale ou la plus courante d'un écran**, devant tout le contenu | FAB, medium, large ; M3 Expressive : ajout du medium, **le petit FAB est déprécié** |
| **Cartes** | Contenu et actions sur **un seul sujet** | Surélevée, remplie, contournée |
| **Chips** | Éléments compacts représentant une saisie, un attribut ou une action ; aident à saisir, choisir, **filtrer**, déclencher | Assist, Filter, Input, Suggestion |
| **Champs de texte** | Saisir et modifier du texte | Remplie, contournée |
| **Boîtes de dialogue** | Informer sur une tâche ; contenir une information critique, exiger une décision ou impliquer plusieurs tâches | Basique, plein écran |
| **Snackbar** | Brefs messages sur un processus, en bas d'écran ; **n'interrompent pas**, n'exigent aucune action pour disparaître (délai, interaction ailleurs ou balayage) ; peuvent offrir une **action** (annuler, réessayer) | — |
| **Barre de navigation** (ex. « Bottom navigation ») | Basculer entre vues sur petits appareils ; **3 à 5 destinations**, icône + libellé | M3 Expressive : plus basse (64 dp au lieu de 80), éléments horizontaux ≥ 600 dp |
| **Onglets** | Organiser le contenu entre écrans, jeux de données, interactions | **Primaires** (haut du volet, destinations principales) ; **secondaires** (dans une zone de contenu) |
| **Listes** | Index verticaux continus de texte ou d'images | Éléments avec tête, support, icônes, avatars |
| **Indicateurs de progression** | Exprimer une attente indéterminée ou la durée d'un processus | Linéaire, circulaire |
| **Interrupteur (Switch)** | Basculer **un réglage unique** activé/désactivé ; les effets doivent prendre effet **immédiatement, sans enregistrer** | — |
| **Barres d'application** | Navigation en haut d'écran | Recherche, petite, medium flexible, large flexible |
| **Feuilles inférieures** | Contenu secondaire ancré en bas | Standard (sans voile), modale (avec voile) |
| **Recherche** | Méthode de navigation pour trouver rapidement | Barre de recherche (persistante) ; vue de recherche (plein écran modal) |
| **Sélecteurs de date** | Choisir une date ou une plage | Ancré, modal, saisie modale |
| **Menus** | Liste de choix sur surfaces temporaires, moins proéminents que les contrôles de sélection | Déroulant, déroulant exposé |
| **Curseurs (Slider)** | Choisir dans une plage de valeurs | Standard, centré, plage |

Valeurs par défaut utiles [S90] : le conteneur d'une boîte de dialogue utilise
`colorSurfaceContainerHigh` et la forme *extra large* ; le texte du snackbar utilise
`textAppearanceBodyMedium` et `colorOnSurfaceInverse` ; une carte utilise la forme *medium*,
avec `colorSurfaceContainerLow` (surélevée, 1 dp), `colorSurfaceContainerHighest` (remplie) ou
`colorSurface` + contour 1 dp `colorOutline` (contournée).

## 13.8 Conseils d'usage

1. **Un seul FAB par écran**, pour l'action principale [S90].
2. **Snackbar pour les retours légers et l'annulation** ; dialogue seulement pour les décisions
   critiques [S90][S61].
3. **Interrupteur = effet immédiat** ; pas de bouton « Enregistrer » à côté [S90].
4. **Barre de navigation : 3 à 5 destinations** avec icônes **et** libellés [S90][S67].
5. **Ne jamais coder une couleur en dur** : passer par les rôles (`colorPrimary`,
   `colorOnSurface`…) pour que clair, sombre et couleur dynamique fonctionnent [S83][S85].
6. **Changer le thème, pas les composants** : couleurs, typographie, formes, mouvement se
   personnalisent au niveau du thème [S42][S85][S86][S87].
7. **Durée proportionnelle à la distance** ; courbes *decelerate* à l'entrée, *accelerate* à la
   sortie [S87].

---
*Sources du chapitre : S42, S61, S67, S83, S85–S91.*
