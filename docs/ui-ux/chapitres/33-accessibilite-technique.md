# Chapitre 33 — Accessibilité technique : structure, focus, ARIA, tests

> Le chapitre 14 donne les critères ; celui-ci donne **comment les construire** : sémantique, ordre de
> focus, ARIA, et méthode de test à trois niveaux.

## 33.1 Pourquoi : l'accessibilité en chiffres [S192]

- L'OMS estime que **plus de 15 % de la population mondiale (1,3 milliard de personnes)** s'identifie
  comme ayant un handicap — la plus grande minorité mondiale, en croissance (vieillissement,
  maladies chroniques).
- **Déficiences visuelles** : 253 millions de personnes dans le monde (36 millions aveugles, 217 millions
  de déficience modérée à sévère) ; **1 homme sur 12 et 1 femme sur 200 sont daltoniens**.
  Outils : lecteurs d'écran, loupes, afficheurs braille. **Points de douleur** : produits incompatibles
  avec les lecteurs d'écran, sites mobiles **sans pincer-zoomer**, graphiques distingués **par la couleur
  seule**, contrastes insuffisants.
- L'**accessibilité numérique (a11y)** : concevoir et construire pour que, quel que soit le handicap,
  on puisse interagir de façon **significative et équivalente**.
- Une vraie inclusion exige de **faire participer des personnes handicapées** à tout le cycle de vie du
  produit (planification, conception, code…).

## 33.2 La structure du contenu (HTML sémantique) [S193]

Les éléments structurants donnent un **plan** du contenu et servent de **points d'ancrage** pour la
navigation. Avec des éléments sémantiques, leur sens est transmis à l'**arbre d'accessibilité** utilisé par
les technologies d'assistance.

### Points de repère (landmarks)
Au moins **un repère par page**. Correspondance HTML ↔ rôle ARIA :

| Élément HTML | Rôle ARIA |
|---|---|
| `<header>` | `banner` |
| `<nav>` | `navigation` |
| `<main>` | `main` |
| `<aside>` | `complementary` |
| `<footer>` | `contentinfo` |
| `<form>` | `form` |
| `<section>` | `region` (**exige un nom accessible**) |

En cas de doute, préférer **les éléments HTML de repère** (très bien pris en charge) ; tester au lecteur
d'écran.

### Titres
Les niveaux de titres (`h1` à `h6`) forment un **plan concis** du contenu ; **ne pas sauter de niveau**
(ex. `h1` puis `h5`) car c'est un des moyens principaux de navigation des utilisateurs de lecteurs
d'écran.

## 33.3 Le focus clavier [S194]

Beaucoup de gens naviguent **au clavier** (ou avec un dispositif qui l'imite) : préférence, efficacité,
matériel cassé, entorse, troubles moteurs fins, malvoyance associée à un lecteur d'écran.

- **Ordre du focus** : logique, intuitif, **identique à l'ordre visuel** (haut vers bas, gauche vers
  droite — inverse pour les langues RTL).
- **`tabindex`** : positif = ordre forcé (**à éviter**, brouille l'ordre) ; `0` = ajoute un élément
  non focalisable à l'ordre naturel (mais il faut alors fournir le **support clavier**) ; `-1` = retire de
  l'ordre naturel (focus programmatique possible).
- **Liens d'évitement (skip links)** : permettent de sauter les menus répétés vers le contenu principal.
- **Indicateur de focus visible** : critère WCAG 2.4.7 (chap. 14) ; ne jamais le supprimer sans
  alternative.

## 33.4 ARIA : les cinq règles [S195]

ARIA (*Accessible Rich Internet Applications*, créé en **2008** par le WAI) : un ensemble
d'**attributs** (pas un langage) qui communiquent **rôle, état et propriété** aux technologies
d'assistance via l'**arbre d'accessibilité** ; ARIA **ne change ni la fonction ni l'apparence** d'un
élément — le développeur reste responsable du code et du style.

Trois fonctions : **rôles** (ce qu'est l'élément), **propriétés** (caractéristiques/relations,
ex. `aria-describedby`), **états/valeurs** (conditions actuelles, ex. `aria-pressed="false"`).

| Règle | Énoncé | Exemple |
|---|---|---|
| **1** | **Ne pas utiliser ARIA** si un élément HTML natif convient | `<button>` plutôt que `<a role="button">` |
| **2** | **Ne pas ajouter d'ARIA inutile ou trompeur** à du HTML | `<h2 role="tab">` est trompeur |
| **3** | **Toujours prendre en charge le clavier** pour les contrôles ARIA actifs | `tabindex="0"`, pas de `tabindex` positif |
| **4** | **Ne pas cacher d'élément focalisable** | pas de `aria-hidden="true"` sur un `button` |
| **5** | **Donner un nom accessible** aux éléments interactifs | libellé, `aria-label`… |

Chiffre cité : d'après le rapport **WebAIM Million**, les pages d'accueil **avec** ARIA affichaient en
moyenne **70 % d'erreurs détectées de plus** que celles sans, surtout par mauvaise implémentation [S195].

## 33.5 Tester l'accessibilité : trois niveaux [S196]

1. **Automatisé** (ex. Lighthouse) : rapide, répétable, peu de connaissances requises ; mais **n'attrape
   pas toutes les erreurs** et produit des **faux positifs**. Plus de **100 outils** existent
   (extensions, linters, applis, API) ; choisir selon le référentiel (WCAG 2.2, 2.1, Section 508), le type
   de produit, le moment du cycle, le rôle de l'utilisateur.
2. **Manuel** : clavier, zoom, contraste, ordre de focus, etc.
3. **Avec technologies d'assistance** : lecteur d'écran (TalkBack sur Android), loupe, commande vocale.

En plus de la conformité : **tests d'usabilité avec des personnes handicapées**, **recruter des
personnes handicapées dans l'équipe**, **consulter un expert**. En l'absence de norme imposée, suivre
la **dernière version de WCAG** [S196].

## 33.6 Équivalents natifs sur Android [S80]

| Web | Android |
|---|---|
| `<button>` natif | `Button` / composants Material (sémantique intégrée) |
| `alt` / `aria-label` | `contentDescription` ; `semantics { … }` |
| Regroupement (`fieldset`) | `Modifier.semantics(mergeDescendants = true)` |
| Repères (`main`, `nav`) | Titres (`heading()`), régions, `traversalIndex` |
| Ordre du focus | Ordre de la hiérarchie de composables ; `focusProperties` |
| Lecteur d'écran | **TalkBack** ; **Switch Access** pour les troubles moteurs |
| Test automatisé | **Accessibility Scanner**, tests Compose d'accessibilité |

*(Les noms d'API Compose relèvent de l'usage courant ; la règle de conception vient de [S80].)*

## 33.7 Check-list technique
- [ ] Éléments natifs d'abord ; ARIA seulement si nécessaire (règle 1).
- [ ] Un plan de titres, sans niveau sauté ; repères nommés.
- [ ] Ordre de focus = ordre visuel ; pas de `tabindex` positif ; indicateur visible.
- [ ] Nom accessible sur tout contrôle ; pas d'élément focalisable caché.
- [ ] Trois niveaux de tests : automatisé, manuel, lecteur d'écran.

---
*Sources du chapitre : S80, S192–S196.*
