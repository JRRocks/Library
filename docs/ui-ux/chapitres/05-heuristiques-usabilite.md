# Chapitre 5 — Les 10 heuristiques de Nielsen et l'évaluation heuristique

> Ce sont les **règles du pouce** les plus utilisées du design d'interface. Elles ne sont pas des
> guidelines précises : ce sont des critères larges pour *inspecter* une interface [S20].

## 5.1 Origine et statut

- Premières versions dans deux articles de Jakob Nielsen et Rolf Molich (1989-1990) ; version
  mise à jour par Nielsen en 1994 ; ensemble final en usage publié en 2005 [S17].
- Selon NN/g [S20], elles ont été affinées par analyse factorielle de **249 problèmes
  d'usabilité réels** (1994) ; le libellé a été légèrement actualisé mais les dix heuristiques
  sont restées inchangées. Dernière révision de l'article : 30 janvier 2024.

## 5.2 Les dix heuristiques, expliquées

Pour chaque heuristique : le principe [S20], l'analogie du monde réel donnée par NN/g, des
conseils et — en encadré — une **application concrète** (synthèse du dossier, pas citation).

![Un schéma de cognition : réification (le cerveau complète ce qui manque)](../images/Multistability.png)
*Figure 5.1 — Multistabilité (cube de Necker, vase de Rubin) : l'interprétation d'une image dépend
du modèle mental de l'observateur. Illustration Wikimedia Commons (domaine public).*

### 1. Visibilité de l'état du système

**Principe.** Le design doit tenir l'utilisateur informé de ce qui se passe, avec un
« retour approprié dans un délai raisonnable ». Connaître l'état courant aide à comprendre les
résultats des actions précédentes et à décider de la suite. Un comportement prévisible
construit la confiance dans le produit et la marque.

**Analogie.** Les panneaux « Vous êtes ici » des plans de centres commerciaux.

**Conseils.** Rendre l'état clair ; prévenir avant toute action ayant des conséquences ;
répondre le plus vite possible, idéalement immédiatement ; bâtir la confiance par une
communication ouverte et continue.

> **Application.** Après un scan : bip, vibration, changement visuel immédiat. Pendant un
> export : indicateur de progression. Hors-ligne : état de connexion explicite.

### 2. Correspondance entre le système et le monde réel

**Principe.** Parler le langage de l'utilisateur — mots, phrases, concepts familiers —
plutôt que le jargon interne ; suivre les conventions du monde réel et présenter
l'information dans un ordre naturel et logique. Les termes, icônes et images évidents pour
l'équipe peuvent dérouter ; le bon choix dépend du public. Quand les commandes suivent les
conventions du réel et correspondent à leurs effets (« mapping naturel »), elles sont plus
faciles à apprendre et à retenir.

**Analogie.** Les boutons d'une cuisinière disposés comme les foyers.

**Conseils.** Rendre le sens clair sans glossaire ; ne jamais supposer que ses mots sont ceux
des utilisateurs ; la recherche utilisateur révèle leur vocabulaire et leurs modèles mentaux.

> **Application.** Dans un inventaire, dire « Dossier », « Comparer », « Exporter par mail »
> (mots du métier) et non « Collection », « Diff », « Share intent ».

### 3. Contrôle et liberté de l'utilisateur

**Principe.** Les utilisateurs agissent souvent par erreur et ont besoin d'une « sortie de
secours » clairement signalée, sans processus long. Pouvoir annuler ou revenir en arrière donne
confiance et contrôle.

**Conseils.** Prendre en charge **Annuler / Rétablir** ; offrir une sortie claire de
l'interaction courante (bouton *Annuler*) ; étiqueter et rendre les sorties faciles à découvrir.

> **Application.** Suppression d'un dossier : snackbar « Annuler » plutôt que boîte
> « Êtes-vous sûr ? » systématique (voir chap. 11).

### 4. Cohérence et standards

**Principe.** L'utilisateur ne doit pas se demander si des mots, situations ou actions
différents signifient la même chose. Suivre les conventions de la plateforme et de
l'industrie. Selon la **loi de Jakob**, les gens passent l'essentiel de leur temps sur *d'autres*
produits dont l'expérience fixe leurs attentes ; l'incohérence augmente la charge cognitive.

**Analogie.** Le comptoir d'enregistrement d'un hôtel est à l'avant du hall.

**Conseils.** Cohérence **interne** (au sein d'un produit ou d'une famille, souvent grâce à un
design system) et **externe** (conventions du secteur). Les deux améliorent l'apprenabilité.

> **Application.** Sur Android, la flèche « retour » en haut à gauche, le bouton retour
> système, un FAB pour l'action principale : conventions de Material (chap. 13).

### 5. Prévention des erreurs

**Principe.** De bons messages d'erreur comptent, mais les meilleurs designs *empêchent* le
problème : supprimer les conditions propices à l'erreur, ou les vérifier et demander
confirmation avant l'engagement. Deux types d'erreurs :

- **Les glissements (slips)** : erreurs inconscientes dues à l'inattention ;
- **Les méprises (mistakes)** : erreurs conscientes dues à un écart entre le modèle mental de
  l'utilisateur et le design.

**Analogie.** Les glissières de sécurité des routes de montagne.

**Conseils.** Prioriser : empêcher d'abord les erreurs coûteuses ; éviter les glissements par
des contraintes utiles et de bonnes valeurs par défaut ; prévenir les méprises en réduisant la
charge mémoire, en permettant l'annulation et en avertissant.

> **Application.** Champ de saisie avec clavier adapté (numérique, e-mail) ; bouton de
> suppression éloigné des actions fréquentes ; valeur par défaut sûre.

### 6. Reconnaissance plutôt que rappel

**Principe.** Minimiser la charge mémoire en rendant éléments, actions et options visibles.
Ne pas obliger à se souvenir d'une information d'une partie de l'interface à l'autre. Les
libellés de champs, éléments de menu et informations utiles doivent être visibles ou faciles à
retrouver. La mémoire à court terme est limitée ; les interfaces fondées sur la
reconnaissance demandent moins d'effort.

**Analogie.** On répond plus juste à « Lisbonne est-elle la capitale du Portugal ? » qu'à
« Quelle est la capitale du Portugal ? ».

**Conseils.** Laisser reconnaître plutôt que se rappeler ; aider en contexte plutôt que par de
longs tutoriels ; réduire ce que l'utilisateur doit retenir.

> **Application.** Barre de navigation inférieure avec **icône + libellé** ; suggestions de
> saisie ; dossiers récents.

### 7. Flexibilité et efficacité d'utilisation

**Principe.** Des raccourcis invisibles aux novices accélèrent le travail des experts : un
même design sert les deux. Permettre d'adapter les actions fréquentes ; plusieurs chemins
pour une même tâche.

**Analogie.** Les cartes montrent les routes ordinaires, mais les habitants prennent des
raccourcis.

**Conseils.** Accélérateurs (raccourcis clavier, gestes tactiles) ; personnalisation du
contenu et des fonctions ; réglages configurables.

> **Application.** Appui long = sélection multiple ; glisser pour supprimer **et** menu
> contextuel équivalent (WCAG 2.5.7 exige une alternative au glisser, chap. 14).

### 8. Design esthétique et minimaliste

**Principe.** Ne pas inclure d'informations hors sujet ou rarement utiles : chaque élément
supplémentaire **concurrence** l'information pertinente et en réduit la visibilité. Cela
n'exige pas un design « plat » : cela demande que contenu et visuels restent centrés sur
l'essentiel, au service des objectifs principaux.

**Analogie.** Une théière ornementée dont les décorations gênent l'usage.

**Conseils.** Se concentrer sur l'essentiel ; ne pas laisser d'éléments inutiles distraire ;
prioriser contenu et fonctions des objectifs principaux.

### 9. Aider à reconnaître, diagnostiquer et corriger les erreurs

**Principe.** Messages d'erreur en **langage clair, sans code d'erreur**, qui identifient
précisément le problème et **proposent une solution** constructive. Ils doivent aussi être
visuellement remarquables.

**Analogie.** Le panneau « sens interdit ».

**Conseils.** Visuels d'erreur traditionnels (texte rouge, gras) ; expliquer ce qui n'a pas
marché sans jargon ; proposer un raccourci qui résout l'erreur immédiatement.

> **Nuance projet.** Certaines apps (ScanList) imposent *« ne rien faire »* sur échec de
> décodage pendant le scan en continu : c'est une règle métier validée par l'utilisateur, pas
> une violation de l'heuristique — le coût d'un message à chaque frame illisible dépasse son
> bénéfice. À réserver aux cas où l'échec est *attendu et fréquent*.

### 10. Aide et documentation

**Principe.** Le système idéal n'a pas besoin d'explication. Si la documentation est
nécessaire, elle doit aider à accomplir des tâches : **consultable, centrée sur la tâche,
concise, en étapes concrètes**.

**Analogie.** Les bornes d'information des aéroports, reconnaissables et utiles sur place.

**Conseils.** Aide facile à rechercher ; présentée **en contexte**, au moment du besoin ;
étapes concrètes.

## 5.3 Mener une évaluation heuristique

Définition [S17] : méthode d'**inspection** où des évaluateurs examinent l'interface et jugent
sa conformité à des principes reconnus. Développée par Molich et Nielsen. Atouts : rapide,
sans recruter d'utilisateurs, utile **tôt** et **avant** les tests utilisateurs. Limites : dépend
de l'expertise des évaluateurs ; « pas de données dures » ; elle ne remplace pas le test (chaque
méthode révèle des problèmes différents).

Règles de bonne pratique [S17] :

| Règle | Détail |
|---|---|
| **3 à 5 évaluateurs** | Au-delà de cinq, peu de gain pour un coût croissant. Nielsen a constaté que les évaluateurs isolés sont « plutôt mauvais » : il faut agréger |
| **D'abord seul, ensuite en groupe** | Examiner individuellement avant la discussion, pour limiter le biais de confirmation |
| **Dans le contexte de tâches typiques** | Cas d'usage réels |
| **Gradation** | Noter chaque problème selon son impact estimé sur la performance ou l'acceptation |
| **Avec ou sans observateur** | Avec : l'évaluateur parle, l'observateur transcrit (moins de charge) ; sans : rapports écrits (plus long mais moins coûteux) |

Procédure simple pour un projet solo (adaptation) : parcourir chaque écran avec les dix
heuristiques comme grille, noter 0 à 4 la gravité (0 = pas un problème, 4 = catastrophe), puis
corriger par gravité décroissante.

## 5.4 Tableau de contrôle rapide

| # | Heuristique | Question de contrôle |
|---|---|---|
| 1 | Visibilité de l'état | Après chaque action, l'écran change-t-il en moins d'une seconde ? |
| 2 | Monde réel | Un utilisateur du métier comprend-il chaque libellé sans aide ? |
| 3 | Contrôle | Puis-je annuler / revenir à tout moment ? |
| 4 | Cohérence | Un même mot désigne-t-il toujours la même chose ? |
| 5 | Prévention | L'interface empêche-t-elle les erreurs coûteuses ? |
| 6 | Reconnaissance | Tout ce dont j'ai besoin est-il visible, sans mémoriser ? |
| 7 | Flexibilité | Existe-t-il un chemin rapide pour l'habitué ? |
| 8 | Minimalisme | Chaque élément sert-il un objectif principal ? |
| 9 | Erreurs | Chaque message dit-il quoi, pourquoi et comment corriger ? |
| 10 | Aide | L'aide est-elle contextuelle et concise ? |

---
*Sources du chapitre : S17, S20 (texte NN/g relu le 20 octobre 2026), S1.*
