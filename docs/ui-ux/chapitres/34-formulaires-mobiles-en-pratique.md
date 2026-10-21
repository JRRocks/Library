# Chapitre 34 — Formulaires mobiles en pratique (Smashing Magazine)

> Complément des chapitres 11, 19 et 25. Source : Stéphanie Walter, *Better Mobile Form UX* [S197].
> « Testez-vous vos formulaires auprès de vrais utilisateurs et sur de vrais appareils ? »

## 34.1 Lisibilité et « scannabilité »

- **Ne pas servir la structure de la base de données** : Luke Wroblewski décrit un formulaire comme
  « des couples nom-valeur d'une structure de stockage vomis en libellés et champs ». Se rapprocher du
  **modèle mental** : demander à un testeur de **penser à voix haute** pour savoir quelles étapes il attend.
- **Grouper visuellement** les champs liés et **balises `fieldset`** pour les lecteurs d'écran.
- **Formulaire long** : ne pas tout montrer par défaut ; **embranchements** (n'afficher que les champs
  de l'option de livraison choisie) ; **optionnel en fin de formulaire** (l'attention baisse) ; ne demander
  que le nécessaire.
- **Prévoir la localisation** (l'allemand tiendra-t-il dans la mise en page ?).

## 34.2 Libellés, champs et placement

| Règle | Détail |
|---|---|
| **Une seule colonne** | Pas de place sur mobile (et peu de raisons sur ordinateur) |
| **Portrait** : libellé **au-dessus** du champ | On voit le libellé pendant la saisie |
| **Paysage** : libellé à **gauche**, champ à droite | La hauteur est réduite — **tester** |
| Libellés **clairs et compréhensibles hors contexte** | Dès qu'un champ a le focus, le **clavier occupe au moins un tiers de l'écran** : on perd le contexte ; « Adresse de livraison » plutôt que « Adresse » |
| **Éviter jargon, abréviations, MAJUSCULES** | **Cohérence** : « client » partout, pas « client » puis « utilisateur » |
| **Courts et scannables** | Si un champ a besoin d'éclaircissement, utiliser une **description de champ**, pas le libellé |
| **Taille de champ ≈ contenu attendu** | Aide à scanner et à comprendre |
| **Ne pas fractionner un champ** pour le format (téléphone en petits champs) | Sur mobile, impossible de naviguer au clavier d'un champ à l'autre ; le saut de focus automatique **prend le contrôle** de l'interface de façon imprévisible ; utiliser des **masques de saisie** |
| **Formats variables d'un pays à l'autre** | Prévoir (téléphone, dates) |

## 34.3 Les descriptions de champs efficaces

À utiliser pour dire :
1. **Ce que l'on demande exactement** (ex. « Adresse 1 » et « Adresse 2 » obscurs ; acronymes de secteur) ;
2. **Pourquoi on le demande** (date de naissance pour la vente d'alcool ; téléphone pour le livreur) :
   les utilisateurs hésitent à donner des données personnelles sans raison ;
3. **Où trouver l'information** (numéro de série d'un appareil : bouton « ? » ouvrant une modale avec photo
   et indications ; code promo) ;
4. **Quel format** (indicatif international ? séparateur de milliers ? ordre jour/mois — « un
   cauchemar » ; compteur de caractères restants).

**Comment les afficher** : **descriptions en ligne**, directement visibles près du champ ; contenus lourds
→ **infobulles** (survol sur desktop, toucher sur mobile) ou **modales** ouvertes par une icône d'aide.

## 34.4 Les placeholders sur mobile

La spécification HTML5 : le placeholder est « **une courte indication** (un mot ou une courte phrase)
destinée à aider la saisie » — non une étiquette. Raisons de s'en méfier :

- il **disparaît** à la saisie (mémoire à court terme) ;
- difficile de **vérifier** le formulaire avant l'envoi et de **corriger les erreurs** ;
- on **confond champ rempli et placeholder** (gris clair, contraste faible) ;
- le placeholder n'est **pas obligatoire** en HTML5 ; les frameworks (Bootstrap) en inscrivent par
  copier-coller (« Veuillez renseigner votre — libellé — ici » = mauvais).

**Exception raisonnable** : des **formulaires très courts et prévisibles** (connexion), avec un **vrai
libellé** déplacé par CSS/JS — pas le placeholder HTML5. **Libellé flottant** (Material) : gagne de la
hauteur, mais ne résout pas la confusion placeholder/valeur [S197][S109].

## 34.5 Réduire le coût d'interaction

- **Moins de champs ≠ toujours plus de conversion** : l'étude classique « de 11 à 4 champs : +160 % » est
  contrebalancée par une autre — de 9 à 6 champs : **−14 %** (on avait retiré les champs les plus
  engageants) ; en les remettant avec les plus importants **en haut** : **+19,21 %**. « Ces sites ne sont
  pas votre site » : **tester**.
- **Ne demander que l'utile, et en contexte** : l'adresse à l'étape de livraison, pas à l'inscription ;
  pas de champ « genre » sans raison ; pas de deuxième ligne d'adresse pour un service en ligne.

## 34.6 Application dans une app Android
- Libellé visible, aide sous le champ, erreur sous le champ avec texte explicite (chap. 11, 19).
- Clavier adapté au type de saisie (numérique, e-mail, téléphone) ; bouton d'action IME **Suivant/OK**.
- Pas de champ fractionné ; **masque** pour les formats ; **valeur par défaut sûre**.
- **Enregistrement automatique** des saisies (pour ne rien perdre si le clavier ou un appel interrompt).
- Tester avec le clavier ouvert sur le plus petit écran (le champ actif reste **visible**).

---
*Sources du chapitre : S60, S109, S197.*
