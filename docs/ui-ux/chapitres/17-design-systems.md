# Chapitre 17 — Design systems, composants et cohérence

> Un **design system** est ce qui permet de produire cent écrans cohérents comme on en produit un
> seul. Pour une petite équipe ou un seul développeur, c'est surtout le **thème** et un petit
> ensemble de composants réutilisés.

## 17.1 Définition (NN/g) [S114][S115]

Un design system est un **ensemble complet de standards** destiné à gérer la conception **à
l'échelle**, au moyen de **composants et de motifs réutilisables**. Il contient (relation
parent-enfant) :

| Pièce | Rôle |
|---|---|
| **Guide de style** (style guide) | Documentation de directives précises, références visuelles et principes ; trois formes : **contenu** (ton, grammaire, formats), **marque** (palettes, typographie, logos) et **conception visuelle / front-end** |
| **Bibliothèque de composants** | Éléments d'interface réutilisables (boutons…), chacun avec **nom unique, description, attributs** (couleur, taille, forme, texte), **états** (activé, survol, focus, désactivé) et **extraits de code** |
| **Bibliothèque de motifs** | Groupes de composants réutilisables (ex. **en-tête de page** = titre + fil d'Ariane + recherche + boutons) |
| **Ressources** | Fichier de design (Figma), logos, polices, icônes |

Exemples cités : le Material Design de Google, le Carbon d'IBM [S115] ; le guide de style de
la NASA (Graphics Standards Manual, 1976) et le guide de style de contenu de Mailchimp pour la
voix [S114].

### Pourquoi en utiliser un ? [S114]

1. **Répliquer** vite et à l'échelle (composants préfabriqués).
2. **Libérer** les ressources de design pour des problèmes plus complexes (priorisation de
   l'information, optimisation des flux, parcours).
3. **Langage commun** : le mot « menu déroulant » désigne un élément défini, qu'on ne rediscute
   pas.
4. **Cohérence visuelle** entre produits, canaux et services ; un changement de marque se gère
   à l'échelle.
5. **Outil d'apprentissage** pour les débutants et les contributeurs de contenu.

### Pourquoi ne pas en utiliser ? [S114]
- Création et maintenance **longues** (« jamais terminé »), qui exigent une équipe dédiée ;
- il faut **apprendre aux autres** à l'utiliser ;
- l'idée que les projets seraient des **créations uniques**, sans besoin de composants réutilisables.

> **Pour un petit projet** : adopter un design system **existant** (Material 3 pour Android) et
> le **thématiser**, au lieu d'en inventer un (chap. 13).

## 17.2 Atomic design (Brad Frost) [S116]

Méthodologie en **cinq étapes** inspirée de la chimie, destinée à construire des systèmes de
design d'interface de façon **délibérée et hiérarchique**. Ce n'est **pas** un processus linéaire :
c'est un **modèle mental** pour voir l'interface à la fois comme un tout cohérent et comme une
collection de parties.

| Niveau | Définition | Exemple |
|---|---|---|
| **Atomes** | Éléments de base qui ne peuvent plus se diviser sans perdre leur fonction : étiquette, champ, bouton… | Un bouton |
| **Molécules** | Groupes simples d'éléments fonctionnant comme une unité | Formulaire de recherche = étiquette + champ + bouton |
| **Organismes** | Assemblages de molécules formant une section complexe et distincte | En-tête, carte de photo (auteur + horodatage + photo + actions + légende) |
| **Gabarits** (templates) | Mise en page : les composants dans leur contexte, avec un **squelette** du contenu | Page de flux sans contenu réel |
| **Pages** | Instances de gabarits avec du **contenu réel** : vérifient que le système tient | La page finale |

Précisions de Frost [S116] : l'atomic design **n'a rien à voir avec** CSS ou JavaScript en
particulier — il concerne la conception de systèmes d'interface *quelle que soit la
technologie* ; il met en évidence le **lien étroit entre contenu et design** ; et il donne un
**vocabulaire commun** pour parler de modularité.

## 17.3 Les principes de Fluent (Microsoft) [S117]

Autre exemple de principes, utiles pour fixer ses propres valeurs :

- **Naturel sur chaque plateforme** : s'adapter à l'appareil et s'appuyer sur le familier ;
  réutiliser les composants natifs environ **80 % du temps** pour concentrer l'énergie sur les
  expériences signature.
- **Conçu pour la concentration** : moins de bruit visuel, l'utilisateur reste concentré, calme et
  confiant.
- **Un pour tous, tous pour un** : inclure tôt une diversité de capacités et de points de vue.
- **Indiscutablement Microsoft** : un caractère reconnaissable (couleur, son, illustrations,
  icônes).

## 17.4 Les « jetons » de design (design tokens)

Un jeton est une **valeur de conception nommée** (une couleur, un espacement, un rayon, une
durée) utilisée à la place d'une valeur brute. La documentation Material illustre le principe sans
le nommer ainsi : on ne code pas `#476810`, on utilise le **rôle** `colorPrimary` ; on ne code pas
`16dp`, on utilise `shapeAppearanceCornerLarge` ; on ne code pas `300 ms`, on utilise
`motionDurationMedium2` [S85][S86][S87].

Avantages : **thèmes clair/sombre** et couleur dynamique gratuits ; **cohérence** ; **changement
global** par un seul point ; **accessibilité** (les couples *primary/onPrimary* sont conçus pour
être lisibles).

| Famille de jetons | Exemples Material | Chapitre |
|---|---|---|
| Couleur | `colorPrimary`, `colorOnSurface`, `colorError` | 9, 13 |
| Typographie | `textAppearanceBodyMedium` | 8, 13 |
| Forme | `shapeAppearanceCornerMedium` (12 dp) | 13 |
| Mouvement | `motionDurationMedium2` (300 ms), `motionEasingStandard` | 13, 15 |
| Espacement | échelle à pas régulier (GOV.UK : 10–60 px) | 10 |

## 17.5 Règles de gouvernance minimales

1. **Un nom, un composant.** Nommer chaque composant et chaque état ; ne pas dupliquer.
2. **Un seul endroit pour le thème** ; aucune couleur ou taille « en dur » dans les écrans.
3. **Documenter** : quand l'utiliser, quand ne pas l'utiliser, états, exemples.
4. **Étendre plutôt que dupliquer** : si un composant manque, l'ajouter à la bibliothèque.
5. **Tester le système** avec du contenu réel (pages) [S116].
6. **Faire évoluer** à partir des retours [S114].

---
*Sources du chapitre : S85–S87, S114–S117.*
