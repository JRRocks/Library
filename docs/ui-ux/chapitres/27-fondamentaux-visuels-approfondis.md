# Chapitre 27 — Fondamentaux visuels approfondis : espace négatif, couleur, typographie

> Compléments aux chapitres 7 à 10, d'après les sections de l'Interaction Design Foundation (IxDF,
> licence CC BY-SA 4.0 pour ses propres contenus) et de Wikipédia.

## 27.1 L'espace négatif (espace blanc)

**Définition** [S175] : les zones **non marquées** entre et dans les éléments visuels d'un design ;
il peut avoir n'importe quelle couleur, texture, motif — voire une image de fond. Il crée un
**équilibre** et un **espace de respiration**. « *Le blanc doit être considéré comme un élément actif,
non comme un fond passif.* » (Jan Tschichold, cité dans [S175]).

- **Actif** : améliore la structure et guide l'utilisateur à travers le contenu.
- **Passif** : esthétique, soulagement visuel.

### Cinq raisons de s'en servir [S175]
1. **Meilleure compréhension** : lisibilité et légibilité ; augmenter l'espace entre paragraphes et dans les
   marges peut améliorer la compréhension jusqu'à **20 %** (d'après Nick Babich, cité).
2. **Concentration et attention** : l'espace autour d'un élément le met en valeur (boutons d'appel
   à l'action).
3. **Clarté et organisation** : séparer sections et éléments ; éviter l'encombrement.
4. **Hiérarchie visuelle** : niveaux d'emphase par l'espace ; aide à balayer en **F** ou en **Z**.
5. **Marque et style** : élégance, sophistication (Apple, Mercedes-Benz, IKEA).

### Conseils d'application [S175]
1. **Adopter le minimalisme** (antidote à l'*horror vacui*) ;
2. **Espacement cohérent** : il aide à construire un modèle mental de l'interface ;
3. **Prioriser le contenu** par l'espace ;
4. **Micro et macro espace négatif** : micro = petits écarts internes (interlignage, marges) ; macro =
   grands écarts entre les blocs principaux ;
5. **Garder l'équilibre** : trop peu = encombré ; trop = vide ;
6. **L'espace négatif peut prendre plusieurs formes** (une image peu chargée) ; il joue un rôle dans
   les lois gestaltistes (figure-fond, fermeture…) ; vase de Rubin (figure 5.1) ;
7. **Tester** (eye-tracking, tests utilisateurs).

Exemples cités : Apple (espace autour des produits), Medium (espace entre paragraphes), Google
(page d'accueil) [S175]. Bénéfices supplémentaires : **moins de charge cognitive**, meilleure
lisibilité sur mobile, aide à retrouver le début de ligne dans les longues lignes.

## 27.2 Couleur : compléments

![Étoile des couleurs primaires, secondaires et tertiaires](../images/Color_star-en_(tertiary_names).png)
*Figure 27.1 — Primaires, secondaires et tertiaires. Wikimedia Commons. Image ajoutée si le
téléchargement a abouti.*

### Fondamentaux [S176]
- Isaac Newton a établi la théorie des couleurs en inventant le **cercle chromatique en 1666**,
  comprenant les couleurs comme des **perceptions humaines** de longueurs d'onde (et non comme des
  qualités absolues). Trois groupes : **primaires**, **secondaires** (mélanges de primaires),
  **tertiaires** (mélanges de primaire et secondaire).
- **Teinte** (hue) : ce qui distingue le rouge du bleu ; **valeur** : clair/foncé (niveaux de gris) —
  essentielle pour le contraste et la profondeur ; **saturation** (chroma, intensité) : pureté et
  éclat.
- Pour l'**écran**, le modèle est **additif** (rouge, vert, bleu).
- **Température** : couleurs chaudes (rouge à jaune, bruns) et froides (vert-bleu à bleu-violet) ; la
  distinction remonte au moins à la fin du XVIIIᵉ siècle [S52].
- **Teintes et ombres** : une *teinte* (tint) s'éclaircit avec du blanc, une *ombre* (shade) s'assombrit
  avec du noir [S52].

### Schémas de couleurs (harmonies) [S176]
| Schéma | Principe | Usage |
|---|---|---|
| **Monochromatique** | Une teinte, déclinée en nuances et teintes | Sobre, cohérent |
| **Analogue** | Trois couleurs voisines sur le cercle | Harmonie douce |
| **Complémentaire** | Couleurs opposées (bleu/jaune) | **Contraste maximal** |
| **Complémentaire divisé** | Ajouter des couleurs de part et d'autre de la complémentaire | Contraste adouci |
| **Triadique** | Trois couleurs à 120° | Harmonie et fort contraste, plus simple que le complémentaire |
| **Tétradique** | Deux paires complémentaires, choisir une dominante | Riche ; équilibrer chaud/froid |
| **Carré** | Quatre couleurs à 90° | À utiliser de façon égale |

### Psychologie et culture [S176]
Les réactions à la couleur dépendent du **genre, de l'expérience, de l'âge et de la culture**. Le
bleu, standard bancaire en Occident, a des associations positives ailleurs ; le rouge évoque la
**bonne fortune** en Chine, le **deuil** en Afrique du Sud, le **danger/séduction** aux États-Unis.
**Toujours concevoir pour l'accessibilité** (daltonisme rouge-vert) et **confirmer par des tests
d'usabilité**. Le *contraste* est vital pour capter l'attention ; la *vivacité* provoque les
réponses émotionnelles voulues.

> **Application.** Pour une app d'inventaire : une palette sobre, **une couleur d'accent**, des
> **états sémantiques** (rouge = à valider, vert = validé, comme dans ScanList) accompagnés **d'un
> second signal** (icône, libellé) — chap. 9.

## 27.3 Typographie : compléments

### Pourquoi c'est central [S177]
La typographie est la discipline qui organise les polices dans une interface pour que le texte soit
**légible, lisible et adaptable**, et visuellement agréable. Elle **communique l'information
essentielle**, **améliore lisibilité et accessibilité**, **établit le ton de la marque**,
**différencie le produit** et peut **favoriser la conversion**.

### Vocabulaire [S177]
| Terme | Définition |
|---|---|
| **Typeface vs font** | On choisit une **famille** (typeface, ex. Arial) ; on utilise une **fonte** (font : un poids ou style, ex. Arial gras) |
| **Crénage (kerning) / interlignage (leading)** | Espacement des lettres / des lignes — influent sur la lisibilité |
| **Ligne de base (baseline)** | Ligne invisible sur laquelle reposent les lettres ; peut servir à bâtir une grille |
| **Hauteur d'x (x-height)** | Distance entre la ligne de base et le haut du « x » minuscule ; une hauteur d'x inhabituelle peut **casser la mise en page** |
| **Ascendante / descendante** | Traits dépassant au-dessus de la hauteur d'x / sous la ligne de base |
| **Empattement / sans empattement** | Les empattements guident parfois l'œil mais rendent mal à petite taille ; les sans-serif sont souvent préférés à l'écran (à nuancer avec les écrans modernes) |
| **Alignement** | Gauche, droite, centré, justifié |
| **Hiérarchie** | Police, graisse, taille, espacement, alignement et blanc ensemble |

### Bonnes pratiques [S177]
- **Cohérence** : un jeu défini de polices ; **pas plus de 3 polices** par site, deux qui se
  complètent ; peu de tailles et de styles.
- **Hiérarchie claire** : styles distincts pour titres, sous-titres, texte.
- Longueurs de ligne, interlignage et tailles adaptés (voir chap. 8).

> **Synthèse des contradictions entre sources** : la préférence serif/sans-serif à l'écran n'est pas
> tranchée ; tous les textes s'accordent en revanche sur **la hiérarchie, la cohérence, un nombre
> limité de polices et de styles, et un interlignage suffisant**.

## 27.4 Check-list visuelle
- [ ] Espacement : une échelle, plus serré dans le groupe que entre les groupes.
- [ ] Palette : couleur de marque + neutres + sémantiques ; schéma choisi consciemment.
- [ ] Contrastes vérifiés (WCAG) ; aucun signal par la couleur seule.
- [ ] 1 à 2 polices, 3 tailles, hiérarchie visible au « squint test ».

---
*Sources du chapitre : S34, S45, S52, S175–S177.*
