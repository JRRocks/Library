# Chapitre 25 — Le GOV.UK : principes de service et catalogue de composants

> Le design system du gouvernement britannique est une des références les plus **documentées et
> testées** : chaque composant précise **quand l'utiliser, quand ne pas l'utiliser, comment il
> fonctionne**, avec la recherche à l'appui. C'est un excellent modèle pour documenter les composants
> d'une app.

## 25.1 Les dix principes de conception [S162]

| N° | Principe | En clair |
|---|---|---|
| 1 | **Partir des besoins des utilisateurs** | Sans les connaître, on construit la mauvaise chose ; ce qu'ils demandent n'est pas toujours ce dont ils ont besoin |
| 2 | **En faire moins** | Ne faire que ce que seul le gouvernement peut faire ; rendre réutilisable plutôt que réinventer ; « le noyau irréductible » |
| 3 | **Concevoir avec les données** | Apprendre du comportement réel ; analytics intégrées et lisibles |
| 4 | **Faire le dur travail pour que ce soit simple** | Rendre simple à utiliser est plus difficile que rendre simple en apparence ; ne pas accepter « ça a toujours été comme ça » |
| 5 | **Itérer, puis itérer encore** | Commencer petit, produits minimaux testés avec de vrais utilisateurs ; l'itération réduit le risque ; ne pas craindre de jeter un prototype |
| 6 | **C'est pour tout le monde** | « Un design accessible est un bon design » ; inclusif, lisible ; si l'on doit sacrifier l'élégance, qu'il en soit ainsi ; ceux qui ont le plus besoin des services sont souvent ceux qui les utilisent le plus difficilement |
| 7 | **Comprendre le contexte** | On ne conçoit pas pour un écran mais pour des personnes (bibliothèque ? téléphone ? jamais utilisé le web ?) |
| 8 | **Construire des services numériques, pas des sites web** | Un service aide les gens à faire quelque chose ; le numérique doit se relier au réel |
| 9 | **Être cohérent, pas uniforme** | Mêmes mots et motifs partout quand c'est possible ; ce n'est pas un carcan |
| 10 | **Rendre les choses ouvertes : cela les améliore** | Partager le code, les motifs et les raisons |

*(Un principe sur l'impact environnemental a été ajouté le 2 avril 2025 [S162].)*

## 25.2 Structurer un formulaire (Service Manual) [S163]

1. **Concevoir pour le format** : papier et numérique ont des forces différentes ; saisir
   l'occasion de **transformer** le service, pas de simplement mettre un formulaire en ligne.
2. **Savoir pourquoi on pose chaque question** : tenir un **protocole de questions** — pour chaque
   donnée : nécessaire pour délivrer le service ? pourquoi ? qu'en fera-t-on ? quels utilisateurs doivent la
   fournir ? comment la vérifier ? comment la tenir à jour et sécurisée ?
3. **Concevoir d'abord pour les scénarios les plus courants** : commencer par les questions qui
   révèlent une **inéligibilité** (ne pas faire perdre de temps) ; **questions à embranchement** pour
   ne montrer que le pertinent.
4. **Une chose par page** : une information, une décision, une question. Avantages : comprendre,
   se concentrer, se repérer dans un processus inconnu, utiliser le mobile, **se remettre d'une erreur
   facilement** ; permet aussi **l'enregistrement automatique**, l'analytique par question, les
   embranchements.
5. **Structurer pour aider** : une question n'exige pas forcément un seul champ (date de naissance :
   **3 champs texte**) ; titres de page en question ou en énoncé, **de façon cohérente**.

## 25.3 Accessibilité dans le Service Manual [S164]

- Exigences : **WCAG 2.2 AA minimum** ; fonctionner avec les technologies d'assistance les plus
  courantes (loupes, lecteurs d'écran, reconnaissance vocale) ; **inclure des personnes handicapées
  dans la recherche** ; publier une **déclaration d'accessibilité**.
- **1 personne sur 5** au Royaume-Uni a une déficience durable (visuelle, auditive, motrice,
  cognitive). Les besoins varient selon le **lieu** (café bruyant, parc ensoleillé, wifi lent), la
  **santé** (fatigue, bras cassé), l'**équipement** (mobile, vieux navigateur).
- **Penser l'accessibilité dès le départ** (les problèmes coûtent moins cher tôt) ; **budgéter**
  (formation, audit) ; **c'est le travail de toute l'équipe**.

## 25.4 Catalogue de composants et règles de décision

### Boutons radio [S166]
- **Pour un choix unique** parmi une liste. Placer le bouton **à gauche** de l'étiquette (plus facile à
  repérer avec une loupe d'écran).
- Ajouter une aide « Sélectionnez une option » si besoin.
- **Ne pas présélectionner** (on risque de ne pas voir la question ou d'envoyer la mauvaise réponse).
- Une fois choisi, on ne peut pas revenir à « rien » : inclure « Aucune de ces réponses » ou « Je ne
  sais pas » si valides.
- Ordre **alphabétique** par défaut (ou du plus au moins courant, **avec une extrême prudence** : cela
  peut renforcer un biais).
- Grouper dans un `<fieldset>` avec `<legend>` (la question).

### Cases à cocher [S165]
- Pour **plusieurs options** ou **activer/désactiver** une option unique. À gauche de l'étiquette.
- Ne pas supposer qu'on sait combien d'options on peut cocher : aide « Sélectionnez toutes les
  réponses applicables ». **Ne pas précocher.** Alphabétique par défaut.
- Grouper dans un fieldset/legend ; si une seule question par page : le legend peut servir de titre
  de page (lu une seule fois par les lecteurs d'écran).

### Liste de sélection (select) [S167]
- **« À n'utiliser qu'en dernier recours »** dans les services grand public : la recherche montre
  que certains utilisateurs les trouvent très difficiles.
- Avant de l'utiliser, poser des **questions** pour présenter moins d'options ; envisager des boutons
  radio.
- Pour des **réglages**, on peut présélectionner ; pour des **questions**, ne rien présélectionner.
- Aide : une phrase courte, sans point final, sans lien.

### Saisie de date [S168]
- **Trois champs** (jour, mois, année) pour une date que l'utilisateur connaît ou peut retrouver sans
  calendrier ; sinon voir le motif « Demander des dates ».
- Grouper en fieldset ; exemples d'aide **valides** ; **accepter** les noms de mois écrits en
  toutes lettres ou abrégés.

### Pagination [S169]
- À envisager si tout afficher sur une page ralentit trop ou si la plupart n'ont besoin que des
  premières pages.
- **Éviter le défilement infini** : il pose des problèmes aux utilisateurs du clavier.
- **Pas de pagination pour les parcours linéaires** (formulaires) : utiliser **Continuer** et un lien
  **Retour**.
- Style « bloc » pour des contenus liés, **liens empilés verticalement** (plus visibles pour les
  loupes) ; ne pas afficher la pagination s'il n'y a qu'une page ; rediriger vers la première page si
  l'URL n'existe plus.

### Onglets [S170]
- Utiles si le contenu se sépare en sections **clairement étiquetées**, que **la première section
  est la plus pertinente** pour la plupart et qu'on **n'a pas besoin de tout voir à la fois** ;
  adaptés aux **utilisateurs réguliers**.
- **À éviter** : pour la navigation de page ; si l'on doit tout lire dans l'ordre ou **comparer**
  (mémoriser et basculer est pénible) ; si le contenu ralentit la page.
- **Tester d'abord sans onglets** : simplifier, découper en pages, garder une page avec titres, ou
  ajouter une table des matières.
- **Onglets, accordéon ou « détails » ?** Onglets : voir une section à la fois, basculer vite,
  peu de sections. Accordéon : plusieurs sections ouvertes, nombreuses sections (disposées
  verticalement). Détails : un ou deux contenus courts et peu importants.

### Accordéon [S171]
- **Seulement si la preuve montre** que l'utilisateur a intérêt à voir un aperçu de plusieurs
  sections, afficher/masquer celles qui le concernent, comparer de l'information.
- **Pas pour du contenu que tous doivent voir**, pas pour **une série de questions** (utiliser des
  pages), **pas d'accordéon dans un accordéon**.
- Un contenu bien écrit et structuré peut dispenser d'accordéon (liens d'ancrage en haut de page).

### Liste récapitulative (summary list) [S172]
- Présente des **faits clés** (clé + valeur) : métadonnées, ou **résumé des réponses** en fin de
  formulaire (motif « Vérifier les réponses »). Variante : cartes récapitulatives.
- **Pas** pour des données tabulaires ni une simple liste de tâches.

### Bannière de notification [S173]
- Pour une information **non directement liée** à la tâche en cours : problème touchant le service,
  échéance qui concerne l'utilisateur, **résultat d'une action précédente** (« e-mail envoyé »).
- **Avec parcimonie** : les gens les manquent souvent. **Pas pour les erreurs de validation**
  (utiliser message d'erreur + résumé d'erreurs) ; ne pas afficher bannière et résumé d'erreurs
  ensemble. Placée **avant le h1**, de la même largeur que le contenu ; **une seule** par page.

## 25.5 Ce que l'on retient d'un bon document de composant

Pour chaque composant, GOV.UK répond toujours à :
1. **Quand l'utiliser ?** 2. **Quand ne pas l'utiliser ?** (et par quoi le remplacer) 3. **Comment
il fonctionne ?** (états, ordre, libellés, accessibilité) 4. **Quelles recherches l'étayent ?**

> **Modèle à copier pour un moteur de la Library** : une fiche composant avec ces quatre rubriques,
> plus l'état « éprouvé / à patcher ».

---
*Sources du chapitre : S162–S173.*
