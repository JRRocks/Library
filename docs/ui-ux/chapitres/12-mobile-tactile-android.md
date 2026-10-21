# Chapitre 12 — Mobile, tactile et Android

> Sur téléphone, l'interface se touche, se tient à une main, se regarde en marchant, en plein
> soleil, parfois avec des gants. Ce chapitre rassemble les règles physiques (cibles tactiles),
> les conventions Android, et les pièges propres aux apps mobiles.

## 12.1 Les cibles tactiles

### Taille

| Source | Taille minimale recommandée |
|---|---|
| NN/g (Parhi, Karlson, Bederson) [S76] | **1 cm × 1 cm** (0,4 in) — mesure **physique**, valable quelle que soit la densité d'écran |
| Material / Android [S79] | **48 dp** (critère de qualité *Touch_Target_Size* de Google Play) |
| web.dev [S77] | **≈ 48 px indépendants de l'appareil** (≈ 9 mm, taille d'un bout de doigt), avec **≈ 8 px d'espacement** horizontal et vertical |
| WCAG 2.2, critère 2.5.8 (AA) [S78] | **24 × 24 pixels CSS** minimum — *ou* espacement suffisant (cercle de 24 px de diamètre centré sur chaque cible ne coupant aucune autre cible) |

Le WCAG fixe un **plancher réglementaire** ; les 48 dp / 1 cm sont la **cible de confort**. Le
critère 2.5.8 admet des exceptions : espacement, contrôle équivalent sur la même page, cible en
ligne dans une phrase, contrôle du navigateur non modifié, cas essentiel (ex. épingles d'une
carte) [S78].

**Faits physiques** [S76] : le bout du doigt mesure en moyenne 1,6 à 2 cm de large (MIT Touch
Lab) ; la surface d'impact du pouce ≈ 2,5 cm.

**Comment obtenir 48 dp avec une icône de 24 dp ?** Ajouter du **padding** : l'icône reste à
24 px, la zone tactile fait 48 px [S77].

### « Le gros doigt n'est pas le coupable »

NN/g [S76] : ce ne sont pas les « gros doigts » mais les **cibles trop petites** qui sont fautives ;
trop de designers privilégient l'esthétique sur la fonction.

### Espacement et erreurs

- Des cibles trop petites **prennent plus de temps** (Fitts, chap. 6) et augmentent les
  **glissements** (toucher la cible voisine) [S76].
- Pour éviter les taps accidentels : **d'abord assez grandes, ensuite bien espacées** (exemple
  d'un bouton de rejet de 2 mm au Instagram, jugé trop petit même avec 2 mm d'espacement).
- Listes de liens et boutons empilés : suffisamment espacés ; ou **côte à côte** (plus de
  largeur de tolérance qu'une ligne basse) [S76].
- Les **cartes** avec de nombreux marqueurs sont sources d'erreurs ; proposer une **vue liste**
  (idéalement par défaut) [S76].

### Asymétrie vue–toucher (view–tap asymmetry) [S76]

Un élément assez grand pour être **vu** (lire son libellé) mais trop petit ou trop dense pour être
**touché** : par exemple les points d'indicateur de carrousel, ou de minuscules pastilles de
couleur de 1 mm sur une fiche produit. Souvent causée par des designs de bureau mal adaptés au
tactile.

### Quand faire plus grand que le minimum [S76]

- **Action principale** (CTA) : plus de prominence = cible plus grande.
- **Usage en mouvement** (marche, conduite) : cibles plus grandes pour tolérer l'imprécision.
  Exemple : l'app Target donne aux boutons *Rechercher* et *Scanner un code-barres* environ
  **2 cm × 2 cm** car ils sont utilisés en magasin, en marchant.
- **Public** : jeunes enfants, personnes âgées (dextérité), personnes qui ont les mains
  occupées (exemple : minuteur d'allaitement de l'app Glow Baby, cibles de 2,3 cm).
- **Limiter le nombre d'éléments** à l'écran permet des cibles plus grandes et mieux espacées.
- **Taille d'écran** : très grands écrans → cibles plus grandes ; petits écrans (montres) ne se
  contentent pas de cibles plus petites.

### Détecter un écran tactile côté web [S77]

Plutôt que de deviner le téléphone par la largeur, utiliser les requêtes de média
`pointer` / `any-pointer` : `coarse` = écran tactile, `fine` = souris ou trackpad (même Bluetooth
sur un mobile). On y augmente la taille des cibles.

## 12.2 Qualité minimale d'une app Android (Google Play) [S79]

Le document *Core App Quality* définit le niveau minimal. Extraits pertinents pour l'interface :

| Critère | Exigence |
|---|---|
| **Taille des cibles** | ≥ **48 dp** |
| **Contraste visuel** | **3 : 1** pour grand texte et graphiques ; **4,5 : 1** pour petit texte (< 18 pt, ou < 14 pt gras) |
| **Description de contenu** | Décrire chaque élément d'interface (hors `TextView`) via `contentDescription` |
| **Longueur de ligne** | Limiter à **45–75 caractères** (espaces compris) pour chaque langue prise en charge |
| **Thème clair/sombre** | Le contenu de l'app et le contenu web accédé prennent en charge les deux |
| **Orientations/pliables** | Parité fonctionnelle selon les orientations et états de pli ; transitions rapides sans perte d'état |
| **Démarrage** | L'app charge vite, ou affiche un **retour à l'écran** (indicateur de progression) si le démarrage dépasse **2 secondes** |
| **Rendu** | Une image toutes les **16 ms** ou moins (au moins 60 images/s) |
| **Stabilité** | Pas de plantage ni de blocage du fil d'interface (ANR) |
| **Autorisations** | Demander le strict minimum de permissions |
| **Lecture audio** | Démarrage dans la seconde suivant la demande |

## 12.3 Accessibilité Android : principes [S80]

Android propose **TalkBack** (lecteur d'écran pour personnes aveugles ou malvoyantes) et
**Switch Access** (pilotage par un ou deux boutons pour les troubles moteurs). Cinq pratiques :

1. **Étiqueter les éléments** : chaque élément interactif ou porteur de sens doit avoir un
   libellé qui explique sa **signification et son but**. Dans Compose et Material, la plupart des
   composants ont un support par défaut ; sinon utiliser `semantics` et `contentDescription`.
   - **Éléments éditables** : indiquer un exemple de saisie valide (placeholder/hint) *en plus*
     d'un **libellé descriptif** (`label`) du champ.
   - **Éléments d'une collection** : chaque libellé doit être **unique**.
   - **Groupes de contenu liés** (avatar + nom + durée de lecture) : regrouper dans un
     conteneur avec `mergeDescendants = true`, et ne rendre **interactif que le conteneur**.
2. **Ajouter des actions d'accessibilité** pour que les flux critiques soient réalisables avec
   un service d'accessibilité (ex. alternative à un balayage).
3. **Utiliser les fonctions d'accessibilité intégrées** de Compose.
4. **Utiliser des repères autres que la couleur** : motifs et position, en plus de la couleur.
5. **Rendre les médias plus accessibles** : descriptions des contenus audio/vidéo.

## 12.4 Navigation et gestes Android

- **Geste « retour prédictif »** (*predictive back*) [S81] : l'utilisateur **prévisualise** où mène
  le balayage de retour (ex. aperçu animé de l'écran d'accueil). Depuis Android 15, l'option
  développeur disparaît : les animations système (retour à l'accueil, entre tâches, entre
  activités) apparaissent pour les apps qui ont **opté** pour le geste. Il faut remplacer
  l'interception de `KeyEvent.KEYCODE_BACK` et `onBackPressed` par `OnBackPressedCallback`
  (AndroidX Activity 1.6.0+) ou `OnBackInvokedCallback`. En Compose : `PredictiveBackHandler`
  (`androidx.activity:activity-compose` 1.8.0+).
- **Bord à bord (edge-to-edge)** [S82] : depuis **Android 15 (API 35)**, une app qui cible le SDK
  35 dessine **sous les barres système et les découpes d'écran**. Il faut gérer les **insets**
  pour que boutons et contenus importants ne soient pas masqués (un bouton derrière la barre de
  navigation ne peut plus être touché). Le composant `Scaffold` de Material 3 réduit le travail.
- **Thème sombre** [S83] (Android 10+, API 29) : réduit la consommation (selon la technologie
  d'écran), améliore la visibilité pour la basse vision et les personnes sensibles à la lumière
  vive. Hériter d'un thème **DayNight**, **éviter les couleurs codées en dur** destinées au
  thème clair ; utiliser les attributs de thème (`colorSurface`, `colorOnSurface`,
  `textColorPrimary`…) ou des ressources qualifiées « nuit ». Proposer en option : clair, sombre,
  suivre le système.

## 12.5 Onboarding : l'éviter autant que possible (NN/g, 2020) [S84]

Onboarding = familiariser avec une interface par des **flux dédiés** (pas l'interface normale),
y compris la configuration nécessaire. Il n'est pas réservé au premier lancement : on peut
accompagner une refonte.

**Pourquoi l'éviter quand c'est possible :**
1. **coût d'interaction** accru (même pour le passer) ;
2. **tension sur la mémoire** : on retient peu ;
3. **gain de performance non démontré** : les tutoriels « paquet de cartes » n'améliorent pas la
   réussite des tâches.

**Il peut être utile seulement si :**
- il faut **recueillir des informations** pour démarrer (banque : identité) ;
- l'app est très **personnalisée** (régime : poids actuel) ;
- des **fonctions ou flux sont inhabituels** ou nouveaux (le dépôt de chèque mobile à ses débuts).

**Trois composantes :**

| Composante | Conseils |
|---|---|
| **Promotion de fonctions** | **À éviter au premier lancement** (perçue comme du marketing, on la saute) ; présenter plutôt sur la fiche de la boutique ou **en contexte, quand c'est utile** (ex. statistiques d'habitude après 7 jours de données) ; l'utiliser pour de **vraies nouveautés** auprès des utilisateurs existants |
| **Personnalisation** | Ne pas demander de choix de **style visuel** à l'onboarding (on ne sait pas encore ce qu'on préfère ; les gens gardent les valeurs par défaut) ; faire l'étude et choisir un bon défaut |
| **Instructions** | Préférer des **astuces en contexte** aux tutoriels ; les messages d'erreur sont des « moments pédagogiques » |

> Test recommandé : lancer l'app **sans onboarding** ; si les gens peinent, d'abord améliorer
> l'apprenabilité du design ; ne prototyper un onboarding que si le test prouve qu'il aide.

## 12.6 Check-list mobile

- [ ] Cibles ≥ 48 dp, espacement ≥ 8 dp ; action principale plus grande.
- [ ] Actions fréquentes à portée de pouce ; destructives éloignées.
- [ ] Texte en `sp` ; mise en page testée à la plus grande taille de police.
- [ ] Retour prédictif géré ; bord à bord et insets traités.
- [ ] Thème clair et sombre ; pas de couleurs codées en dur.
- [ ] Démarrage < 2 s ou indicateur ; 60 images/s.
- [ ] `contentDescription` partout où nécessaire ; regroupements `mergeDescendants`.
- [ ] Pas d'onboarding sans preuve de son utilité.
- [ ] Test **en conditions réelles** (en marchant, plein soleil, gants).

---
*Sources du chapitre : S76–S84.*
