# Chapitre 11 — Les composants d'interface

> Ce chapitre rassemble les bonnes pratiques éprouvées pour les composants récurrents : boutons,
> formulaires, messages d'erreur, navigation, onglets, accordéons, états vides, notifications,
> indicateurs de chargement. Chaque section cite sa source.

---

## 11.1 Boutons

### Les cinq états de base (NN/g, 2025) [S62]

| État | Rôle | Caractéristiques typiques |
|---|---|---|
| **Activé** (enabled / par défaut) | Le bouton peut être actionné | Contraste élevé avec le fond ; libellé lisible ; ombre optionnelle |
| **Désactivé** (disabled) | L'action n'est pas disponible | Couleur grisée/désaturée, contraste réduit **mais lisible** ; ne pas tromper en paraissant cliquable ; ajouter `aria-disabled="true"` pour que le bouton garde le focus clavier et soit annoncé |
| **Survol** (hover) | Indique qu'il est cliquable (souris seulement) | Léger assombrissement ; curseur « main » ; léger délai (≈ 150–200 ms) pour éviter les survols involontaires |
| **Focus** (clavier) | Indique l'élément actif au clavier | Apparaître en ≈ 100–150 ms ; garder **contour/trait** plutôt qu'un simple changement de couleur (attendu par les utilisateurs et perceptible par les personnes malvoyantes) |
| **Pressé** (pressed) | Confirme que l'action est enregistrée | Léger changement de couleur ou micro-animation en 100–150 ms ; sans retour rapide, on appuie plusieurs fois |

États complémentaires [S62] : **chargement** (spinner à gauche du libellé, parfois transformé en
coche à la fin pour confirmer le succès) et **sélectionné** (cases à cocher, boutons radio — à ne
pas confondre avec *pressé*).

### États ≠ styles

Le **style** donne l'emphase visuelle ; l'**état** informe sur l'interaction. Un bouton a un seul
style mais plusieurs états au fil du temps [S62] :

| Style | Emphase | Usage | Forme typique |
|---|---|---|---|
| **Primaire** | Maximale | Action principale/courante (créer, enregistrer, valider) | Plein, rempli |
| **Secondaire** | Moyenne | Actions moins importantes (annuler, répondre) | Contour |
| **Tertiaire** | Minimale | Actions optionnelles/supplémentaires (voir plus) | Texte seul |

### Libellés

Le libellé doit décrire **ce que fera** le bouton, être substantiel et succinct, et pouvoir se
lire seul. Éviter les libellés génériques comme *Continuer* ou *Suivant* [S62].

### Les règles du GOV.UK Design System [S63]

- **Libellé en minuscules (casse de phrase), décrivant l'action** : « Commencer », « Se
  connecter », « Continuer » (le service n'enregistre pas), « Enregistrer et continuer » (il
  enregistre), « Enregistrer et revenir plus tard », « Ajouter un autre », « Payer »,
  « Confirmer et envoyer », « Se déconnecter » — avec plus de mots si nécessaire (« Ajouter une
  autre adresse »).
- **Aligner le bouton principal sur le bord gauche** du formulaire.
- **Un seul bouton par défaut (principal) par page** : plusieurs appels à l'action principaux
  réduisent leur impact et compliquent la décision suivante.
- **Boutons secondaires** pour les actions secondaires ; si une page en réclame beaucoup,
  *simplifier la page ou la découper* plutôt que d'ajouter des boutons.
- **Bouton d'avertissement** (danger) : conçu pour faire réfléchir, il ne fonctionne que **utilisé
  très rarement** ; uniquement pour des actions destructrices graves et difficilement
  réversibles (supprimer définitivement un compte), idéalement avec une **étape de confirmation**.
- Bouton « Commencer » (start) : sur la page de départ ; c'est un lien (`<a role="button">`)
  quand il ne soumet pas de données.

---

## 11.2 Formulaires

Les formulaires sont l'endroit où se perdent le plus de conversions et d'utilisateurs. Une étude
publiée à CHI (Seckler et collègues), rapportée par NN/g [S60], montre que lorsque les
formulaires suivent les guidelines d'usabilité de base, le **temps de réalisation baisse
sensiblement** et les utilisateurs ont presque **deux fois plus de chances** de soumettre sans
erreur du premier coup.

### Dix recommandations de NN/g (2016) [S60]

1. **Faire court.** Pascal : « J'ai fait celle-ci plus longue que d'habitude parce que je n'ai
   pas eu le temps de la faire plus courte. » Retirer les champs dont l'information peut être
   (a) déduite autrement, (b) recueillie plus tard, ou (c) omise. NN/g a réduit l'un de ses
   formulaires de 6 à 2 champs. *Chaque champ supprimé augmente le taux de conversion.*
2. **Grouper visuellement étiquettes et champs liés.** Étiquette immédiatement **au-dessus** du
   champ (mobile et formulaires courts) ou à côté (formulaires très longs) ; éviter l'espacement
   ambigu (étiquette à égale distance de deux champs) ; poser l'attribut `label` pour les
   lecteurs d'écran ; sectionner en groupes distincts (et les baliser) si deux sujets.
3. **Une seule colonne.** Plusieurs colonnes cassent l'élan vertical. Exceptions : champs courts
   et liés (Ville / État / Code postal).
4. **Séquence logique.** Ordre standard des champs (numéro de carte → expiration → code) et
   des valeurs (livraison standard → 2 jours → 1 jour) ; mettre les valeurs les plus fréquentes
   en premier ; tester la navigation à la touche **Tab**.
5. **Éviter le texte indicatif (placeholder).** Il cause de nombreux problèmes d'usabilité.
6. **Adapter le champ au type et à la taille** de la saisie : pas de liste déroulante pour 2 ou 3
   options (boutons radio : un seul clic) ; largeur de champ proche de la saisie attendue.
   Exemple : sur 2 130 participants, les villes allaient de 3 à 22 caractères ; **99,9 %**
   faisaient 19 caractères ou moins → 19 est une largeur raisonnable.
7. **Distinguer champs obligatoires et facultatifs.** Éliminer d'abord un maximum de champs
   facultatifs ; en garder 1 ou 2 au plus, **clairement étiquetés**.
8. **Expliquer les exigences de saisie/format** *avant* l'erreur (ne pas faire deviner des règles
   de mot de passe). Mieux : supprimer les règles de format arbitraires.
9. **Éviter les boutons Réinitialiser / Effacer.** Le risque de perte accidentelle dépasse le
   besoin rare de tout recommencer. Pour des saisies très sensibles, un bouton **Annuler** à
   prominence **nettement moindre** que *Envoyer*.
10. **Messages d'erreur très visibles et précis** : plusieurs indices, **pas seulement la
    couleur** — contour du champ **et** texte rouge **et** graisse plus forte. **Conserver la
    saisie erronée** pour qu'on la corrige, avec une explication précise.

Exemple cité [S60] : un formulaire de réclamation proposait *Aperçu* et *Effacer le formulaire*,
ce dernier **plus proche des champs** que le bouton principal — ce qui viole à la fois la règle
« pas de bouton Effacer » et celle de **proximité entre un objet et son action principale**.

### Champs de saisie : règles du GOV.UK [S64][S65]

- **Tout champ a une étiquette**, visible dans la plupart des cas, **alignée au-dessus** du
  champ, **courte, directe, en casse de phrase, sans deux-points final**.
- **Pas de texte indicatif (placeholder) à la place d'une étiquette, d'une aide ou d'un
  exemple**, car : il disparaît dès qu'on tape (problème pour les troubles de la mémoire ou la
  relecture) ; tous les lecteurs d'écran ne le lisent pas ; ses styles par défaut ne respectent
  souvent pas le contraste WCAG 1.4.3.
- **Dimensionner le champ selon le contenu** : un champ de code postal a la taille d'un code
  postal ; largeurs fixes de 2, 3, 4, 5, 10, 20 caractères pour les contenus de longueur connue ;
  largeur fluide sinon. Un champ multi-lignes se fait avec une zone de texte (*textarea*).
- **Une question par page** recommandée : l'étiquette peut alors servir de titre de page, ce qui
  évite que les lecteurs d'écran l'annoncent deux fois.
- **Message d'erreur** [S65] : expliquer **ce qui ne va pas et comment le corriger** ; le placer en
  **rouge après la question et l'aide**, relié par une **bordure rouge** ; si l'erreur concerne un
  sous-champ (ex. l'année), le signaler par sa propre bordure et le nommer dans le message.
  **Ne jamais vider les champs** : garder les réponses valides *et* fautives. Un préfixe masqué
  « Erreur : » est lu par les lecteurs d'écran. **Résumer toutes les erreurs en haut de page**
  (composant « résumé d'erreurs »). Ne pas utiliser un message d'erreur pour dire à
  l'utilisateur qu'il n'est pas éligible ou qu'il manque de droits : l'emmener plutôt vers une
  page qui explique le problème et la marche à suivre.

---

## 11.3 Messages d'erreur (NN/g, 2023) [S61]

Définition : une interruption générée par le système pour informer d'une situation incomplète,
incompatible ou indésirable. « Qualité et messages d'erreur vont rarement ensemble » : les
équipes se concentrent sur le chemin idéal et traitent les écarts comme une réflexion après coup.

### Visibilité
- **Afficher l'erreur près de sa source** (proximité = association immédiate).
- **Indicateurs visibles, redondants, accessibles** : texte et surbrillance en gras, contraste
  élevé, rouge — pour le message *et* l'élément à corriger ; **jamais la couleur ou l'animation
  seules** (environ 350 millions de personnes dans le monde ont une déficience de la vision des
  couleurs).
- **Dimensionner selon l'impact** : étiquettes conditionnelles, toasts ou bannières pour les
  avertissements légers ; **dialogues modaux réservés aux erreurs graves** qui exigent une
  résolution.
- **Ne pas afficher trop tôt** : montrer une erreur avant que l'utilisateur ait pu répondre est un
  motif hostile (« noter l'élève avant qu'il ait répondu »). Un champ quitté sans être rempli
  n'est pas forcément une erreur ; le temps réel convient aux interactions sujettes à erreur
  (règles de mot de passe).

### Communication
- **Langage humain**, sans jargon ni codes obscurs (réservés au diagnostic technique) — la page
  404 est l'exemple d'infraction courant.
- **Décrire concisément et précisément** : « Une erreur s'est produite » n'aide pas. Attention à
  l'excès de précision technique (le modèle mental de l'utilisateur diffère du modèle de
  conception).
- **Offrir un conseil constructif** : pour « rupture de stock », donner la date de retour ou une
  alerte de réapprovisionnement.
- **Ton positif, sans accuser l'utilisateur** : éviter *invalide*, *illégal*, *incorrect* ; éviter
  l'humour (il se périme si l'erreur est fréquente).

### Efficacité
- **Prévenir les erreurs probables** : « les pires messages d'erreur sont ceux qui n'existent pas »
  (exemple : l'alerte « pièce jointe oubliée » des messageries).
- **Préserver la saisie** de l'utilisateur.
- **Réduire l'effort de correction** : proposer une courte liste de corrections plutôt que
  « Ville et code postal ne correspondent pas ».
- **Éduquer brièvement** sur le fonctionnement du système, avec un lien vers l'explication
  complète si nécessaire.
- Dans les situations désespérées (le système ne peut plus rien servir), la **nouveauté**
  peut adoucir l'échec.


---

## 11.4 Navigation

### Information architecture ≠ navigation [S70]

- L'**architecture de l'information (AI)** est l'ossature : identification et définition du
  contenu et des fonctions, et l'organisation, la structure et la **nomenclature** qui définissent
  leurs relations. Elle **n'est pas** dans l'interface : elle se documente en tableurs et
  diagrammes. « L'AI informe l'UI ». Analogie de NN/g : on ne voit pas le squelette d'un cheval
  ou d'un poulet, mais ils en dépendent — « n'essayez pas de monter le poulet ».
- Activités : inventaire de contenu, audit de contenu, regroupement centré utilisateur,
  taxonomie (vocabulaire contrôlé), métadonnées descriptives.
- La **navigation** est l'ensemble des composants d'interface qui permettent d'atteindre
  l'information : navigation globale, locale, utilitaire, fil d'Ariane, filtres, facettes,
  liens connexes, pieds de page… Pour chacun, décider priorité d'usage, placement, etc.
- **Définir l'AI avant de concevoir la navigation** (et la valider par card sorting et tree
  testing, chap. 4).

### Navigation mobile : les motifs de base (NN/g) [S67]

Principe central : **prioriser le contenu sur le « chrome »** (les éléments d'interface) tout en
gardant la navigation accessible et découvrable.

| Motif | Avantages | Inconvénients | Convient à |
|---|---|---|---|
| **Barre de navigation supérieure** | Efficace, visible | Peu d'options ; consomme de la place au-dessus du pli | Peu de rubriques |
| **Barre d'onglets (tab bar)** — en haut (Android surtout) ou **en bas (iOS surtout)** | **Persistante** : toujours visible, même en défilant | Même limite de nombre d'options | **4 à 5** options principales ; au-delà, difficile de garder des cibles tactiles correctes |
| **Menu de navigation (hamburger et variantes)** | Beaucoup d'options dans peu d'espace ; sous-menus | **Moins découvrable** (« loin des yeux, loin du cœur ») | Sites/apps de lecture et navigation dense (actualités) |
| **Carrousel d'onglets** | Plus d'options | Les options cachées sont peu découvertes si les catégories sont très différentes | Catégories similaires et prévisibles |
| **Hub de navigation** (la page d'accueil liste tout) | Distribue efficacement | Gaspille la page d'accueil ; une étape de retour au hub à chaque usage | Apps orientées **tâche**, où l'on fait rarement plus d'une tâche par session |

Notes [S67] : le mot **« Menu »** semble légèrement préféré à l'icône hamburger (recherche tierce) ;
un menu sans signifiant, découvert par geste (balayage au bord), est **très peu découvert** et
peut entrer en conflit avec le geste « retour » d'iOS ; les barres de navigation doivent
être « la » zone de chrome principale — peu d'espace pour la navigation utilitaire et la
recherche. Même avec seulement 4 catégories, un logo, un panier, un lien compte et une recherche
peuvent occuper trop de place.

### Navigation cachée : ce que montrent les chiffres [S66]

Étude quantitative NN/g, **179 participants, 6 sites, mobile et ordinateur** (partenaire :
WhatUsersDo) : la **navigation cachée** (hamburger) est **nettement moins découvrable** que la
navigation visible ou partiellement visible ; elle est **utilisée moins souvent et plus
tard** dans la tâche ; l'expérience est **moins bonne** (difficulté perçue, temps, réussite).
**L'effet est plus fort sur ordinateur que sur téléphone.**

Recommandations [S66] :

- **Ordinateur** : ne pas utiliser de navigation cachée ; afficher les options de premier niveau
  (haut de page ou colonne de gauche).
- **Téléphone** : pas de règle absolue. **4 liens de premier niveau ou moins → les afficher
  visibles** ; au-delà de 4, masquer une partie est la seule solution raisonnable (le menu
  extensible reste bien meilleur que les alternatives) ; **fournir des liens dans la page** vers
  les informations importantes. « **Mobile-first n'est pas mobile-only.** »

## 11.5 Onglets (NN/g, 2024) [S68]

Définition : les onglets permettent de voir **un seul panneau** de contenu parmi une liste.
Éléments : **liste**, **libellé** concis, **panneau**, **indicateur de sélection**.

**Quand les utiliser** : contenu long à regroupements clairs ; **peu de regroupements** (au
trop-plein, la barre devient un carrousel et les onglets cachés sont moins découverts — « moins
il y a d'onglets, mieux c'est ») ; contenu d'importance **inégale** (l'onglet par défaut reçoit
plus d'attention : le contenu des autres doit être **complémentaire**, non critique) ;
libellés **courts** ; l'utilisateur n'a **pas besoin de voir simultanément** des informations de
plusieurs onglets (sinon il sollicite sa mémoire à court terme et mieux vaut une grande page).

**Onglets vs accordéons** : sur **mobile**, les accordéons fonctionnent mieux (place limitée,
libellés plus longs, FAQ) ; sur ordinateur, les onglets conviennent mieux et gèrent des contenus
plus longs et des mises en page plus complexes [S68].

**Deux types d'onglets — ne pas les mélanger** [S68] :

| | Onglets de navigation | Onglets « dans la page » |
|---|---|---|
| Contenu | Étendue large, contenus **différents** | Étendue étroite, contenus **similaires** |
| Comportement attendu | Aller vers une nouvelle vue (léger délai accepté) | Rester dans la vue (chargement instantané) |
| Onglet sélectionné par défaut | Souvent un seul ; peut être aucun | **Toujours un** |

Mélanger les deux dans un même contrôle **désoriente**.

**Conception visuelle** [S68] :

- **Indiquer nettement l'onglet sélectionné** avec **au moins deux indicateurs** (région commune
  = même fond que le panneau ; trait de soulignement — **pas de trait d'1 px ni de couleur peu
  contrastée** ; graisse/couleur du texte ; taille ; icône). Deux indicateurs sont cruciaux
  quand il n'y a que deux onglets.
- **Garder les onglets non sélectionnés visibles et lisibles** (pas trop pâles, contraste suffisant).
- **Relier l'onglet à son panneau** (région commune ou proximité).
- **Une seule rangée d'onglets** (les rangées empilées détruisent la mémoire spatiale).
- **Liste d'onglets au-dessus du panneau** (les listes verticales ou en bas sont négligées).
- Mettre le contenu **le plus utilisé en premier** et sélectionné par défaut.

## 11.6 Accordéons (NN/g, 2020) [S69]

- Très utiles sur mobile (réduisent la longueur de page) ; sur ordinateur, ils atténuent la
  complexité visuelle.
- Étude : parmi caret (chevron vers le bas), plus, flèche droite, aucun signe, **seul le caret a
  fait mieux** que l'absence d'icône ou une icône absurde pour signaler un accordéon.
- Les utilisateurs touchent **aussi souvent l'icône que le libellé** : **ne pas leur donner des
  actions différentes** (pas de « bouton scindé » : libellé → page, icône → accordéon).
- Décider : un élément de menu **ouvre un sous-menu** *ou* **mène à une page** ; dans ce
  dernier cas, pas d'icône alignée à droite.

## 11.7 Divulgation progressive (Nielsen, 2006) [S71]

**Principe** : montrer d'abord quelques options **essentielles** ; offrir les options spécialisées
**sur demande**. Concilie *puissance* et *simplicité*. Exemple classique : la boîte de dialogue
d'impression (nombre de copies d'abord, options avancées derrière un bouton).

**Bénéfices** : le fait qu'un élément soit affiché d'emblée dit qu'il est important ; les
novices évitent les erreurs et perdent moins de temps ; les experts balayent moins de choses
rares. Améliore **3 des 5 composantes de l'usabilité** : apprenabilité, efficacité, taux d'erreur.
La crainte d'un modèle mental limité est **infondée** d'après la recherche.

**Deux conditions de réussite** :
1. **Bien répartir** initial/secondaire : tout ce qui est fréquemment nécessaire doit être
   d'emblée visible ; la liste primaire ne doit pas non plus être trop longue.
2. **Rendre évident le passage** au niveau secondaire.

Autre application : le **site web/mobile** : informations clés sur la page principale, détails sur
une page secondaire ; différer le secondaire est aussi une règle clé du design mobile.

## 11.8 États vides (NN/g, 2021) [S72]

Un **état vide** est un conteneur, un écran ou un panneau pour lequel le contenu n'existe pas
encore ou ne peut être affiché (aucun favori, aucune alerte configurée, résultats de recherche
vides…). Le laisser **vide par défaut** crée de la confusion et de la méfiance et rate une
occasion. Un état vide **conçu** sert à :

1. **Communiquer l'état du système** — éviter « le système a-t-il terminé ? y a-t-il eu une
   erreur ? ai-je mal réglé les filtres ? ». Exemple : « Aucun enregistrement pour la période
   choisie ». *Pire* : afficher « Aucun enregistrement » pendant le chargement, puis le remplacer
   par le contenu (message trompeur) ;
2. **Fournir des repères d'apprentissage** (découvrir des fonctions inutilisées) ;
3. **Offrir un chemin direct** vers les tâches clés (bouton « Créer votre premier… »).

## 11.9 Indicateurs, validations, notifications (NN/g, 2024) [S73]

Choisir selon le **type d'information**, son **urgence** et **l'action requise**.

| Moyen | Définition | Caractéristiques | Exemple |
|---|---|---|---|
| **Indicateur** | Signe visuel qui fait ressortir un élément dynamique | **Contextuel** (près de l'élément), **conditionnel** (n'apparaît que sous condition), **passif** (aucune action) ; peut ajouter du bruit | Texte en gras pour un mail non lu ; flèche verte/rouge d'un cours |
| **Validation** | Message d'erreur lié à une saisie | **L'utilisateur doit agir** ; contextuel ; doit dire *comment corriger* (« Veuillez saisir votre adresse », pas « Champ vide ») ; icône conseillée | Champ de carte bancaire incomplet |
| **Notification** | Message informatif sur un événement du système | Non déclenché par l'action immédiate ; **contextuelle** ou **globale** ; **à action requise** (urgente, intrusive, ex. modale) ou **passive** (badge, petite fenêtre non modale, moins intrusive mais facile à manquer) | « Mise à jour disponible » |

Écueils signalés [S73] : une notification passive d'ajout au panier **loin de la zone de
focus** est manquée, d'où des **ajouts multiples** ; une notification doit établir **plus de
contexte** qu'une validation (l'utilisateur n'est pas en train d'y penser).

## 11.10 Chargement et attente

### Les trois limites de temps (Nielsen, 1993) [S75]

| Limite | Signification | Conséquence de conception |
|---|---|---|
| **0,1 s** | Sensation de **réaction instantanée** et de **manipulation directe** | Pas de feedback particulier, sauf l'affichage du résultat |
| **1,0 s** | Le **flot de pensée** n'est pas interrompu, bien que le délai soit perçu | Pas de feedback spécial, mais on perd le sentiment d'opérer directement sur les données |
| **10 s** | Limite pour garder l'**attention** sur le dialogue | Au-delà, l'utilisateur veut faire autre chose : donner un **indicateur de progression** (pourcentage) |

Compléments [S75] :

- Les indicateurs de progression **en pourcentage** pour les opérations de **plus de ~10 s** ;
  trois avantages : rassurer (pas de plantage), indiquer la durée approximative, **donner quelque
  chose à regarder** (d'où la préférence pour une barre graphique plutôt qu'un nombre).
- Charge inconnue : afficher la progression en **quantité absolue** (ex. nom de chaque base
  interrogée) ou, en dernier recours, un indicateur non spécifique (roue qui tourne).
- Entre 2 et 10 s, une vraie barre peut être excessive (principe d'**inertie de l'affichage** :
  ne pas faire clignoter des changements trop rapides).
- Les animations doivent être cadencées sur une horloge réelle, pas sur la vitesse de calcul.
- Depuis 1993, ces seuils sont restés les mêmes : ils sont dictés par la **perception humaine**,
  pas par la technologie (note de 2014 de Nielsen : mêmes règles pour les applications web que
  pour les autres).

### Écrans squelettes (NN/g, 2023) [S74]

Un **squelette** (skeleton screen) imite en blocs gris la mise en page de la page en cours de
chargement ; **réservé aux chargements pleine page**.

- Trois types : **statique** (le plus courant : blocs gris pour textes et images) ;
  **animé** (pulsation, dégradé « shimmer » — peut distraire, agacer, poser des problèmes
  d'accessibilité) ; **« cadre seul »** (en-tête, pied de page, fond, sans maquette du contenu) —
  **déconseillé** : équivalent à un spinner, l'utilisateur croit que la page ne marche pas.
- Bénéfices : empêche de croire que le site est en panne ; **illusion d'une attente plus
  courte** ; réduit la charge cognitive (modèle mental de la structure avant l'arrivée du
  contenu).

## 11.11 Bonnes pratiques transversales des composants

1. **Un seul appel à l'action principal par écran** [S63].
2. **Tout composant interactif a des états visibles** : activé, désactivé, focus, pressé,
   chargement [S62].
3. **Chaque composant dit ce qui se passe** : retour sous 0,1 s si possible, indicateur au-delà
   de 1 s, progression au-delà de 10 s [S75].
4. **Jamais de couleur seule** pour porter un état [S60][S61][S48].
5. **Peu de choix visibles, le reste sur demande** (divulgation progressive) [S71].
6. **Un nom, un composant, une règle** : cohérence interne (heuristique 4, chap. 5).

---
*Sources du chapitre : S48, S60–S75.*
