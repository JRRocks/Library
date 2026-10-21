# Chapitre 20 — Motifs d'interface : dialogues, tableaux, recherche, défilement

> Les composants du chapitre 11 se combinent en **motifs** : dialogues, tableaux, listes
> filtrables, carrousels, en-têtes collants… Chaque motif a ses règles et ses pièges.

## 20.1 Dialogues modaux et non modaux (NN/g, 2017) [S134]

**Dialogue modal** : s'affiche **par-dessus** le contenu principal et place le système dans un
**mode spécial** qui exige une interaction ; le contenu principal est **désactivé** jusqu'à ce
qu'on réponde. **Non modal** : ne désactive pas le contenu principal (on peut continuer à
interagir).

Historique : les modales servaient à alerter d'une erreur ou d'un état exigeant une action
immédiate ; elles sont aujourd'hui souvent utilisées pour **attirer l'attention**, parfois de
façon contestable.

**Inconvénients** : elles exigent une **attention immédiate** et **interrompent** le flux de
travail.

**Quand les utiliser** :
1. **Avertissements importants** pour prévenir ou corriger des erreurs critiques (risque de perte
   de travail, action destructrice **irréversible**). Test : le problème serait-il plus facile ou
   plus difficile à corriger si l'attention est détournée de la tâche ? Une erreur de formulaire
   se signale **sur la page**, près de l'erreur ; l'annonce d'un redémarrage dans 10 s peut être
   modale. L'erreur est-elle **irréversible** ?
2. **Demander une information critique** pour continuer un processus (ex. connexion exigée pour
   ajouter un favori).
3. **Fractionner un flux complexe** en étapes digestes (assistants/wizards) — mais une modale à
   plusieurs étapes prolonge l'éloignement de la tâche principale.

> **Alternative** : préférer un élément **non modal** (snackbar, bandeau, panneau) quand
> l'interruption n'est pas indispensable (chap. 11, 13).

## 20.2 Dialogues de confirmation (Nielsen, 2018) [S135]

« Le vrai coupable de l'erreur dite “utilisateur” est le **concepteur**, qui a rendu trop facile
de se mettre en difficulté. » Un dialogue de confirmation demande si l'on est sûr de vouloir
poursuivre une commande ; c'est **une interruption initiée par le système** qui ralentit
la tâche : **temps bien dépensé si elle empêche une erreur, gênante sinon**.

Principe : **pas une seconde chance si l'utilisateur répond machinalement**.

Huit consignes de NN/g :

1. **Réserver** aux actions à **conséquences graves** (détruire le travail, coûter beaucoup
   d'argent), en particulier **non annulables** — tout en essayant d'**offrir l'annulation**
   (contrôle et liberté).
2. **Pas de confirmation pour les actions courantes** (« au loup ! ») : les gens cessent de lire.
3. **Être spécifique** et dire la conséquence : pas « Êtes-vous sûr ? » mais ce qui sera fait, en
   termes compréhensibles, avec identification de l'objet (nom du fichier, nombre
   d'éléments ; mieux : vignette). Exemple d'infraction : « Ces 2 éléments » ne dit pas lesquels.
4. **Boutons qui résument l'effet**, pas Oui/Non : « **Supprimer le fichier** » / « **Conserver le
   fichier** ».
5. **Divulgation progressive** pour en dire plus sans alourdir le message.
6. **Pas de « Oui » par défaut** ; mieux : aucune réponse par défaut.
7. Pour les opérations **très dangereuses**, exiger une **action non standard** (ex. taper le mot
   « DELETE » avant de supprimer une liste de diffusion) — à réserver aux cas rares, sinon cela
   devient automatique.
8. **Permettre de désactiver** les confirmations routinières (case « Ne plus demander »).
Meilleure solution encore : **permettre d'annuler** l'action (garder une copie temporaire) [S135].

## 20.3 Infobulles et aides contextuelles (NN/g, 2019) [S136]

**Infobulle (tooltip)** : message bref qui apparaît quand l'utilisateur interagit (survol souris
ou **survol clavier** = focus prolongé) avec un élément ; **déclenchée par l'utilisateur**,
donc **inexistante sur écran tactile** (on y utilise des **« popup tips »**, déclenchés par un
toucher sur « ? » ou « i »).

Consignes :
1. **Pas pour l'information vitale** à la tâche (ex. règles de mot de passe) : les infobulles
   disparaissent, l'utilisateur devrait les mémoriser.
2. **Contenu bref et utile** ; pas de texte redondant (« Ajouter une ligne » sur le bouton « Ajouter
   une ligne »). Si rien d'utile à dire, ne pas en mettre.
3. **Prendre en charge souris et clavier**.
4. **Flèches** quand plusieurs éléments voisins.
5. **Cohérence** : toutes les icônes ou aucune (sinon non découvertes).
6. **Étiquettes pour les icônes** : un tooltip n'est qu'un pis-aller (mieux : un libellé
   visible) ; contraste suffisant.

## 20.4 Fil d'Ariane (NN/g, 2018) [S137]

Liste de liens représentant la page courante et ses « ancêtres », jusqu'à l'accueil ; NN/g les
recommande **depuis 1995**. Onze consignes dont :

1. **Ne remplace pas** la navigation globale ni locale.
2. Montre la **position hiérarchique**, pas l'**historique de session**.
3. Sites polyhiérarchiques : **un seul chemin**.
4. Inclure la **page courante** en dernier élément ; elle **n'est pas un lien**.
5. Seulement des **pages réelles**, pas des catégories logiques.
6. Commencer par un lien vers l'**accueil**.
7. **Pas de retour à la ligne** ; pas de texte **trop petit ou trop serré** (mobile) ; envisager de ne
   garder que les derniers niveaux sur mobile.
8. Séparateur recommandé : « > ».

## 20.5 Menus géants (mega menus, NN/g) [S146]

Grands panneaux **bidimensionnels** regroupant les options : adaptés aux sites riches ; **tout est
visible** sans défilement (comparaison visuelle, moins de mémoire). Bonnes pratiques :
**regrouper** par typographie/mise en page, **mots-clés en tête** de libellé (éviter « Pre-Owned »
répété), icônes ou images pour aider à choisir, **gérer le délai** au survol, **rester simples**,
accessibles. (Voir l'article pour le détail du chronométrage et de l'accessibilité.)

## 20.6 Tableaux de données (NN/g) [S138][S139]

**Tableau vs cartes** : le tableau est **évolutif** (lignes et colonnes) et favorise la
**comparaison** (deux données adjacentes visibles ensemble, sans réorientation spatiale ni
mémoire de travail).

**Quatre tâches principales** :
1. **Trouver** des enregistrements selon des critères ;
2. **Comparer** des données ;
3. **Voir, modifier ou ajouter** une ligne ;
4. **Agir** sur des enregistrements.

Consignes de conception [S138] :
- **Première colonne** = identifiant **lisible** (pas un ID « mystère ») ; **ordre des colonnes**
  par importance, colonnes liées **adjacentes**.
- **Filtres** découvrables, rapides, puissants, à syntaxe transparente ; **indication visible**
  qu'un filtre est actif.
- **Comparer** : **geler les en-têtes** (lignes et colonnes) ; bordures, **zébrage**, mise en
  évidence au survol ; **masquer/réordonner** les colonnes facilement (alternative au
  glisser-déposer) avec **indicateur d'état** (« 15 colonnes masquées »).

**Sur mobile** [S139] :
- colonnes **assez larges pour rester lisibles** (2 colonnes pour contenu verbeux ; plus pour des
  chiffres) ;
- **faire pivoter le téléphone = dernier recours** (on gagne en largeur, on perd en hauteur) ;
- **en-têtes collants** (et colonne de gauche collée) ;
- **indiquer clairement** qu'un défilement horizontal est possible ;
- laisser l'utilisateur **choisir les données** à afficher ; accordéons pour groupes de données.
- Commencer par un **tableau utilisable en grand écran** avant de l'adapter.

## 20.7 Filtres, facettes et recherche

**Filtre** : tout ce qui analyse un ensemble et exclut certains éléments. **Navigation à facettes**
= **plusieurs filtres** décrivant toutes les dimensions du contenu (cuisine, ingrédient principal,
régime…), plus flexible et plus utile pour les très grands ensembles, mais **plus coûteuse à créer
et maintenir** [S140].

**Pages « Aucun résultat »** [S145] :
1. **Dire clairement** qu'il n'y a pas de résultat (typographie et espacement : un message petit et
   léger est manqué ; les utilisateurs ont des modèles mentaux solides de la page de résultats) ;
2. **Proposer une suite** (suggestions, corrections, élargir la recherche, parcourir) ;
3. **Ne pas se moquer** de l'utilisateur.

## 20.8 Défilement infini, pagination, « Charger plus » (NN/g, 2022) [S141]

Défilement infini = chargement continu lors du défilement.

| Avantages | Inconvénients |
|---|---|
| **Moins d'interruptions** ; **moindre coût d'interaction** ; bien adapté au **mobile** | **Difficulté à retrouver** un contenu (pas de repères) ; **illusion d'exhaustivité** ; **impossible d'atteindre le pied de page** ; **accessibilité** ; **charge de page** ; **SEO** |

Variantes : bouton **« Charger plus »** (compromis) ; défilement infini **avec pagination
intégrée** (repères). Il convient surtout aux sites à **structure plate** où le contenu défile
sans cesse et est également pertinent (réseaux sociaux, actualités). À retenir :
**sauvegarder la position** quand on revient d'un détail (sinon retour tout en haut de la liste).

## 20.9 Carrousels (NN/g, 2018) [S142]

Avantage : beaucoup de contenu dans peu d'espace. Inconvénients : **accès séquentiel**
(inefficace) et **faible découvrabilité** ; commandes mal implémentées au toucher.
Règles : la plupart des gens s'arrêtent après **3–4 pages** → **atteindre le dernier élément en
3 ou 4 gestes** ; sinon **utiliser une liste** ; **prioriser** les éléments (et personnaliser) ;
le balayage doit réellement piloter le carrousel.

## 20.10 En-têtes collants et pieds de page

**En-têtes collants** [S143] : gardent navigation, recherche et utilitaires accessibles. Cinq
conseils : (1) **petit** (maximiser le rapport contenu/« chrome ») — sur tactile : cibles ≥ 1 cm ×
1 cm, texte ≈ 16 pt ; (2) **contraste** avec le contenu ; (3) mouvement **minimal, naturel,
réactif** ; (4) envisager un en-tête **partiellement collant** (réapparaît au défilement vers le
haut) ; (5) se demander **s'il est nécessaire**.

**Pieds de page** [S144] : sous-estimés mais **utilisés** comme **seconde chance** d'être
convaincu et **dernier recours** pour du contenu difficile à trouver ; éléments : liens utilitaires,
navigation « paillasson », tâches secondaires, plan du site, etc.

## 20.11 Check-list
- [ ] Modale seulement pour l'irréversible ou l'information critique ; sinon snackbar/bandeau.
- [ ] Confirmation : spécifique, boutons à verbes, pas de « Oui » par défaut, annulation préférée.
- [ ] Infobulles jamais vitales ; icônes avec libellé.
- [ ] Tableaux : identifiant lisible, en-têtes figés, filtres visibles, colonnes masquables.
- [ ] « Aucun résultat » explicite avec piste de sortie.
- [ ] Liste longue : « Charger plus » ou pagination plutôt que défilement infini sans repères.

---
*Sources du chapitre : S134–S146.*
