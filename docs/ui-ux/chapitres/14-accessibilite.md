# Chapitre 14 — Accessibilité (WCAG 2.2 et Android)

> L'accessibilité n'est pas une option : c'est la condition pour que **tout le monde** — et chacun
> dans des situations difficiles (soleil, bruit, main occupée, fatigue) — puisse utiliser
> l'interface. Le référentiel international est **WCAG 2.2** (W3C, Recommandation du 5 octobre
> 2023) [S46][S92].

## 14.1 Les quatre principes (POUR)

Les WCAG s'organisent en quatre principes [S46][S98] :

| Principe | Énoncé | Exemples |
|---|---|---|
| **Perceptible** | L'information et les composants doivent pouvoir être **perçus** | Alternatives textuelles, sous-titres, contraste, redimensionnement |
| **Utilisable** (Operable) | Les composants et la navigation doivent être **utilisables** | Clavier, temps suffisant, pas de flashs, cibles assez grandes |
| **Compréhensible** | Information et fonctionnement **compréhensibles** | Langue, prévisibilité, aide à la saisie |
| **Robuste** | Contenu **interprétable** par un large éventail d'agents utilisateurs, dont les technologies d'assistance | Nom/rôle/valeur, messages d'état |

Le W3C souligne que l'accessibilité repose sur plusieurs composants qui fonctionnent ensemble :
**contenu web, agents utilisateurs** (navigateurs, lecteurs d'écran…) et **outils de création**
[S98]. Il recommande d'**impliquer des utilisateurs tôt et tout au long du projet**.

## 14.2 Niveaux de conformité

- **A** : le minimum ; **AA** : le niveau visé par la plupart des réglementations ; **AAA** : le
  plus exigeant (non requis dans son ensemble).
- WCAG 2.2 conserve le même modèle de conformité que 2.0 ; un site conforme à 2.2 est conforme à
  2.0 et 2.1 [S46].
- Le critère **4.1.1 Parsing** est **obsolète et supprimé** dans 2.2 [S46].

## 14.3 Les neuf nouveautés de WCAG 2.2 [S92]

| N° | Intitulé | Niveau | En clair |
|---|---|---|---|
| 2.4.11 | Focus Not Obscured (Minimum) | AA | Un élément qui a le focus clavier ne doit pas être **entièrement caché** par du contenu ajouté par le site (bandeau collant) |
| 2.4.12 | Focus Not Obscured (Enhanced) | AAA | Aucune partie de l'élément focalisé ne doit être couverte |
| 2.4.13 | Focus Appearance | AAA | L'indicateur de focus doit être **assez grand** et **assez contrasté** par rapport à l'état non focalisé |
| 2.5.7 | Dragging Movements | AA | Toute action par **glisser** doit aussi être possible par **simple clic/toucher**, sauf si le glisser est essentiel |
| 2.5.8 | Target Size (Minimum) | AA | Cibles ≥ **24 × 24 px CSS**, ou espacées pour ne pas se chevaucher, ou une exception |
| 3.2.6 | Consistent Help | A | Si une aide est proposée sur plusieurs pages, elle reste **au même endroit relatif** |
| 3.3.7 | Redundant Entry | A | Ne pas obliger à **ressaisir** ce qui a déjà été donné dans le même processus ; préremplir ou proposer le choix |
| 3.3.8 | Accessible Authentication (Minimum) | AA | La connexion ne doit pas exiger de **mémoriser, résoudre un casse-tête ou retaper un code**, sauf alternative ou aide (gestionnaire de mots de passe, collage autorisé) |
| 3.3.9 | Accessible Authentication (Enhanced) | AAA | Pas de test cognitif, même reconnaître des images fournies par l'utilisateur, sauf alternative |

## 14.4 Les critères WCAG 2.2, regroupés (référence)

Légende : **A**, **AA**, **AAA**. Les intitulés anglais sont ceux de la spécification [S46] ; les
résumés sont des paraphrases.

### Principe 1 — Perceptible

| N° | Intitulé | Niv. | Résumé |
|---|---|---|---|
| 1.1.1 | Non-text Content | A | Tout contenu non textuel a une **alternative textuelle** équivalente (sauf décoration, etc.) |
| 1.2.1 | Audio-only and Video-only (Prerecorded) | A | Alternatives pour audio seul / vidéo seule préenregistrés |
| 1.2.2 | Captions (Prerecorded) | A | **Sous-titres** pour l'audio préenregistré |
| 1.2.3 | Audio Description or Media Alternative (Prerecorded) | A | Audiodescription ou alternative pour la vidéo préenregistrée |
| 1.2.4 | Captions (Live) | AA | Sous-titres pour l'audio en direct |
| 1.2.5 | Audio Description (Prerecorded) | AA | Audiodescription de la vidéo préenregistrée |
| 1.2.6 | Sign Language (Prerecorded) | AAA | Langue des signes pour l'audio préenregistré |
| 1.2.7 | Extended Audio Description (Prerecorded) | AAA | Audiodescription étendue quand les pauses sont insuffisantes |
| 1.2.8 | Media Alternative (Prerecorded) | AAA | Alternative textuelle aux médias temporels |
| 1.2.9 | Audio-only (Live) | AAA | Alternative pour l'audio en direct |
| 1.3.1 | Info and Relationships | A | Structure et relations véhiculées visuellement déterminables par programme (titres, listes, labels) |
| 1.3.2 | Meaningful Sequence | A | Ordre de lecture correct déterminable |
| 1.3.3 | Sensory Characteristics | A | Les instructions ne reposent pas seulement sur forme, couleur, taille, position, orientation ou son |
| 1.3.4 | Orientation | AA | Ne pas restreindre à **une seule orientation** (portrait/paysage), sauf essentiel |
| 1.3.5 | Identify Input Purpose | AA | Le but des champs de données personnelles est déterminable (autocomplétion) |
| 1.3.6 | Identify Purpose | AAA | Le but des composants, icônes et régions est déterminable |
| 1.4.1 | Use of Color | A | La couleur n'est **pas le seul** moyen de transmettre une information |
| 1.4.2 | Audio Control | A | Audio automatique > 3 s : pouvoir le **mettre en pause ou régler le volume** |
| 1.4.3 | Contrast (Minimum) | AA | **4,5 : 1** (texte) ; 3 : 1 pour grand texte |
| 1.4.4 | Resize Text | AA | Texte **agrandissable jusqu'à 200 %** sans perte de contenu ni de fonction |
| 1.4.5 | Images of Text | AA | Utiliser du texte plutôt que des images de texte |
| 1.4.6 | Contrast (Enhanced) | AAA | **7 : 1** |
| 1.4.7 | Low or No Background Audio | AAA | Peu ou pas de bruit de fond sur la parole |
| 1.4.8 | Visual Presentation | AAA | Mécanismes pour choisir couleurs, largeur (≤ 80 caractères), interlignage… |
| 1.4.9 | Images of Text (No Exception) | AAA | Images de texte uniquement décoratives ou essentielles |
| 1.4.10 | Reflow | AA | Contenu sans défilement **à deux dimensions** à **320 px CSS** de large (≈ 1280 px à 400 % de zoom) |
| 1.4.11 | Non-text Contrast | AA | Composants d'interface et graphiques utiles ≥ **3 : 1** |
| 1.4.12 | Text Spacing | AA | Aucune perte si l'utilisateur impose interligne 1,5, paragraphes 2, lettres 0,12, mots 0,16 |
| 1.4.13 | Content on Hover or Focus | AA | Contenu apparaissant au survol/focus : fermable, survolable, persistant |

### Principe 2 — Utilisable

| N° | Intitulé | Niv. | Résumé |
|---|---|---|---|
| 2.1.1 | Keyboard | A | Toute fonction utilisable **au clavier** |
| 2.1.2 | No Keyboard Trap | A | Le focus ne doit pas rester **piégé** |
| 2.1.3 | Keyboard (No Exception) | AAA | Clavier, sans exception |
| 2.1.4 | Character Key Shortcuts | A | Raccourcis à une seule touche : désactivables/modifiables |
| 2.2.1 | Timing Adjustable | A | Limites de temps réglables |
| 2.2.2 | Pause, Stop, Hide | A | Contenu animé/défilant/clignotant > 5 s : pouvoir le mettre en pause |
| 2.2.3 | No Timing | AAA | Pas de contrainte de temps |
| 2.2.4 | Interruptions | AAA | Interruptions reportables ou supprimables |
| 2.2.5 | Re-authenticating | AAA | Reprise sans perte de données après réauthentification |
| 2.2.6 | Timeouts | AAA | Avertir de la durée d'inactivité pouvant causer une perte de données |
| 2.3.1 | Three Flashes or Below Threshold | A | Pas plus de **3 flashs par seconde** |
| 2.3.2 | Three Flashes | AAA | Pas plus de 3 flashs par seconde (sans seuil) |
| 2.3.3 | Animation from Interactions | AAA | Animation déclenchée par interaction : **désactivable** |
| 2.4.1 | Bypass Blocks | A | Moyen de **sauter** les blocs répétés |
| 2.4.2 | Page Titled | A | Pages **titrées** |
| 2.4.3 | Focus Order | A | Ordre de focus qui préserve le sens |
| 2.4.4 | Link Purpose (In Context) | A | But du lien déterminable (texte seul ou en contexte) |
| 2.4.5 | Multiple Ways | AA | Plusieurs moyens de trouver une page |
| 2.4.6 | Headings and Labels | AA | Titres et étiquettes décrivent le sujet ou le but |
| 2.4.7 | Focus Visible | AA | **Indicateur de focus visible** |
| 2.4.8 | Location | AAA | Indication de la position dans l'ensemble |
| 2.4.9 | Link Purpose (Link Only) | AAA | But du lien déterminable par le texte seul |
| 2.4.10 | Section Headings | AAA | Intertitres pour organiser |
| 2.4.11 | Focus Not Obscured (Minimum) | AA | *nouveau 2.2* — voir § 14.3 |
| 2.4.12 | Focus Not Obscured (Enhanced) | AAA | *nouveau 2.2* |
| 2.4.13 | Focus Appearance | AAA | *nouveau 2.2* |
| 2.5.1 | Pointer Gestures | A | Gestes à plusieurs doigts ou à trajet : alternative à **un seul pointeur** |
| 2.5.2 | Pointer Cancellation | A | On peut **annuler** une action au pointeur |
| 2.5.3 | Label in Name | A | Le nom accessible **contient le texte visible** |
| 2.5.4 | Motion Actuation | A | Fonctions par mouvement de l'appareil : alternative et désactivation |
| 2.5.5 | Target Size (Enhanced) | AAA | Cibles ≥ **44 × 44 px CSS** |
| 2.5.6 | Concurrent Input Mechanisms | AAA | Ne pas restreindre les modes de saisie disponibles |
| 2.5.7 | Dragging Movements | AA | *nouveau 2.2* |
| 2.5.8 | Target Size (Minimum) | AA | *nouveau 2.2* |

### Principe 3 — Compréhensible

| N° | Intitulé | Niv. | Résumé |
|---|---|---|---|
| 3.1.1 | Language of Page | A | **Langue** de la page déterminable |
| 3.1.2 | Language of Parts | AA | Langue des passages |
| 3.1.3 | Unusual Words | AAA | Définition des mots inhabituels |
| 3.1.4 | Abbreviations | AAA | Forme développée des abréviations |
| 3.1.5 | Reading Level | AAA | Niveau de lecture (secondaire inférieur) |
| 3.1.6 | Pronunciation | AAA | Prononciation des mots ambigus |
| 3.2.1 | On Focus | A | Le focus ne provoque **pas de changement de contexte** |
| 3.2.2 | On Input | A | Modifier un réglage ne change pas le contexte sans avertissement |
| 3.2.3 | Consistent Navigation | AA | Navigation répétée **dans le même ordre** |
| 3.2.4 | Consistent Identification | AA | Même fonction = même identification |
| 3.2.5 | Change on Request | AAA | Changements de contexte seulement à la demande |
| 3.2.6 | Consistent Help | A | *nouveau 2.2* |
| 3.3.1 | Error Identification | A | L'erreur est **identifiée et décrite en texte** |
| 3.3.2 | Labels or Instructions | A | **Étiquettes ou instructions** pour toute saisie |
| 3.3.3 | Error Suggestion | AA | **Suggestion de correction** quand elle est connue |
| 3.3.4 | Error Prevention (Legal, Financial, Data) | AA | Transactions légales/financières ou données : annulable, vérifiable ou confirmable |
| 3.3.5 | Help | AAA | Aide contextuelle |
| 3.3.6 | Error Prevention (All) | AAA | Prévention d'erreur pour toute soumission |
| 3.3.7 | Redundant Entry | A | *nouveau 2.2* |
| 3.3.8 | Accessible Authentication (Minimum) | AA | *nouveau 2.2* |
| 3.3.9 | Accessible Authentication (Enhanced) | AAA | *nouveau 2.2* |

### Principe 4 — Robuste

| N° | Intitulé | Niv. | Résumé |
|---|---|---|---|
| 4.1.2 | Name, Role, Value | A | Nom et rôle déterminables ; états, propriétés et valeurs modifiables par programme |
| 4.1.3 | Status Messages | AA | Les **messages d'état** sont annoncés aux technologies d'assistance **sans prendre le focus** |

## 14.5 Critères détaillés à connaître par cœur

### 1.4.10 Reflow — la mise en page se recompose [S93]
Contenu présentable **sans perte d'information** et sans défilement à deux dimensions à une
largeur de **320 px CSS** (défilement vertical) ou une hauteur de **256 px CSS** (défilement
horizontal). Exceptions : contenus qui exigent une mise en page bidimensionnelle (cartes,
schémas, vidéos, jeux, tableaux de données, barres d'outils à garder visibles). Intention :
quand les lignes dépassent le bord, on perd sa place et l'effort physique et cognitif augmente.

### 1.4.11 Non-text Contrast [S94]
Les composants d'interface (informations visuelles nécessaires pour identifier leurs **états**)
et les parties de graphiques nécessaires à la compréhension : **3 : 1** contre les couleurs
adjacentes. Exception : composants inactifs ; apparence imposée par le navigateur non modifiée.
Comme pour le texte, ne pas arrondir (2,999 : 1 échoue) ; les lignes très fines peuvent être
rendues plus pâles par l'anticrénelage.

### 2.4.7 Focus Visible [S95]
Tout élément utilisable au clavier a un **mode où l'indicateur de focus est visible** ; il **ne
doit pas être limité dans le temps**. L'indicateur est un contenu non textuel soumis au 1.4.11.
Le critère 2.4.13 (AAA) précise taille et forme d'un indicateur cohérent. Bénéficient : tous les
utilisateurs du clavier, personnes à troubles de l'attention ou de la mémoire à court terme.

### 3.3.1 Error Identification et 3.3.3 Error Suggestion [S97]
- L'erreur est **identifiée** (l'élément fautif) et **décrite en texte**. Simplement ré-afficher
  le formulaire sans indice ne suffit pas.
- « *E-mail non valide* » passe 3.3.1 ; « *Saisissez une adresse e-mail valide, au format
  nom@domaine.fr* » passe **aussi** 3.3.3 car elle dit comment corriger.
- Une « erreur de saisie » inclut l'information **omise** et l'information **hors format ou hors
  valeurs admises**.
- Si le code **corrige automatiquement** une valeur hors limites, l'utilisateur doit en être
  informé.

### 3.3.2 Labels or Instructions [S96]
Fournir des étiquettes ou instructions pour toute saisie ; chaque **option** d'un groupe radio
ou de cases doit avoir son étiquette ; indiquer les **formats de données** inhabituels. « Trop
d'information peut être aussi nuisible que trop peu » — l'objectif n'est pas d'encombrer la
page.

### 2.5.8 Target Size (Minimum) [S78]
Voir chapitre 12 : 24 × 24 px CSS, ou espacement ; exceptions (spacing, équivalent, en ligne,
contrôle du navigateur, essentiel).

### 1.4.3 / 1.4.1 / 1.4.12
Voir chapitres 8 et 9 (contraste, couleur, espacement du texte).

## 14.6 Accessibilité sur Android

Voir chapitre 12 § 12.3 [S80] : TalkBack, Switch Access, étiquettes
(`contentDescription`, `semantics`), regroupements (`mergeDescendants`), actions
d'accessibilité, repères autres que la couleur, médias. S'y ajoutent :

- **48 dp** de cible minimale ; **contraste 4,5 : 1 / 3 : 1** (qualité Google Play [S79]).
- **Taille de police système** : utiliser `sp` ; tester à la plus grande taille.
- **Thème sombre et contraste élevé** : ne pas perdre l'information quand les subtilités de
  couleur disparaissent (l'article web.dev donne l'exemple d'un onglet actif signalé seulement
  par un fond léger, qui disparaît en mode contraste élevé [S47]).

## 14.7 Méthode : intégrer l'accessibilité dès la conception

1. **Concevoir avec** : inclure des utilisateurs en situation de handicap dans les tests [S98].
2. **Contrôles automatiques** (Lighthouse, Accessibility Scanner Android) : ils ne couvrent
   qu'une partie des critères ; ils ne remplacent pas un test **manuel** (clavier, lecteur
   d'écran).
3. **Tester au clavier** : Tab, Maj+Tab, Entrée, Espace, flèches ; focus **visible** partout [S95].
4. **Tester avec un lecteur d'écran** (TalkBack sur Android).
5. **Tester avec agrandissement du texte (200 %)**, zoom, contraste élevé, mode sombre.
6. **Documenter** les exceptions et les décisions.

## 14.8 Check-list accessibilité (extrait AA)

- [ ] Alternatives textuelles pour toute image porteuse de sens (1.1.1).
- [ ] Contraste texte 4,5 : 1 ; éléments d'interface 3 : 1 (1.4.3, 1.4.11).
- [ ] Information jamais portée par la couleur seule (1.4.1).
- [ ] Texte agrandissable à 200 %, mise en page qui se recompose à 320 px (1.4.4, 1.4.10).
- [ ] Tout au clavier, focus visible, pas de piège (2.1.1, 2.1.2, 2.4.7).
- [ ] Alternative au glisser (2.5.7) ; cibles ≥ 24 px (≥ 48 dp visé) (2.5.8).
- [ ] Étiquettes, erreurs en texte avec suggestion (3.3.1 – 3.3.3).
- [ ] Pas de ressaisie inutile (3.3.7) ; authentification accessible (3.3.8).
- [ ] Messages d'état annoncés sans prendre le focus (4.1.3).
- [ ] Orientation libre (1.3.4) ; langue déclarée (3.1.1).

---
*Sources du chapitre : S46–S47, S78–S80, S92–S98.*
