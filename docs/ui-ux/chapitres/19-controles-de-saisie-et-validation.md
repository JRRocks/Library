# Chapitre 19 — Contrôles de saisie et validation

> Choisir le bon contrôle (case, bouton radio, interrupteur, liste déroulante, champ) et valider
> au bon moment. Complète les chapitres 11 (formulaires) et 14 (accessibilité).

## 19.1 Quel contrôle pour quel besoin ?

Règle historique depuis *Inside Macintosh* (1984), reprise par tous les standards [S128] :

| Contrôle | Sélections possibles | Relation entre options | Prise d'effet |
|---|---|---|---|
| **Boutons radio** | **Exactement une** | Mutuellement exclusives | À la validation |
| **Cases à cocher** (liste) | 0 à toutes | **Indépendantes** | À la validation |
| **Case seule** | Coché / non coché | — | À la validation |
| **Interrupteur (toggle)** | 2 (activé/désactivé) | Mutuellement exclusives | **Immédiate** |
| **Liste déroulante** | Une parmi beaucoup | Mutuellement exclusives | À la validation |

Résumé de NN/g [S125][S127][S128].

## 19.2 Boutons radio et cases à cocher

Règles de [S128] :
- **Représentation standard** : case = **petit carré** avec coche ou croix ; radio = **petit cercle**
  avec point plein. **Jamais de cases rondes** [S125].
- **Grouper visuellement** les choix d'un même groupe, bien séparer des autres groupes ; **sous-titres**
  possibles pour les longues listes de cases (pas pour une liste radio, qui doit rester unifiée).
- **Disposition verticale**, un choix par ligne ; si horizontale, bien espacer pour que le lien
  étiquette-bouton soit évident.
- **Libellés positifs et actifs** pour les cases (« Envoyez-moi… » plutôt que « Ne m'envoyez plus… ») ;
  dire ce qui se passe si on coche **et** si on ne coche pas ; sinon, deux boutons radio.
- **Boutons radio plutôt que menus déroulants** quand c'est possible : options toujours visibles,
  comparables, plus faciles à utiliser pour qui a des difficultés motrices.
- **Toujours une sélection par défaut** pour une liste radio ; si l'on peut ne rien choisir, prévoir
  une option explicite « Aucune ».
- **Options exhaustives et distinctes** (exemple : un formulaire sans « retraité » bloquait des
  seniors) ; sinon proposer « Autre » + champ de saisie.
- **Étiquette cliquable** (zone plus grande = plus rapide, Fitts).
- Pour une case d'**abonnement** (opt-in), la recommandation issue des tests : **la laisser
  décochée par défaut**.

Cases à cocher imbriquées [S125] : une case parent « Tous les passagers » et des cases enfants ;
quand certaines (pas toutes) sont cochées, le parent affiche un **état indéterminé** (trait).
Utile quand beaucoup d'utilisateurs coche(ro)nt tout. **Zone cliquable minimale : 1 cm × 1 cm**
(étiquette comprise) [S125].

## 19.3 Interrupteurs (toggle switches) [S127]

- **Remplacent** deux boutons radio ou une case unique pour changer **l'état d'une fonction ou
  d'une préférence**.
- **Effet immédiat**, sans « Enregistrer » ; si ce n'est pas possible, choisir une case ou des
  boutons radio. **Ne pas mêler** interrupteurs et champs qui exigent un bouton *Envoyer* dans le
  même formulaire (confusion sur l'effet).
- **Libellés concis et non neutres** : « Notifications par SMS » (mots-clés d'abord) plutôt qu'une
  question ; **test** : dire le libellé à voix haute en ajoutant « activé/désactivé » ; si cela ne
  fait pas sens, le réécrire (exemple d'infraction : « Inclure les articles hors maternité ? »).
- **Aspect standard** : l'interrupteur **change de position** ; **couleur à fort contraste** pour
  l'état ; attention aux différences culturelles (le rouge pour « activé » est contre-intuitif).
- **Cohérence** d'implémentation partout.

## 19.4 Listes déroulantes : en dernier recours (NN/g, 2026) [S126]

| Avantage invoqué | Coût réel |
|---|---|
| Compacte | **Faible découvrabilité** : options cachées ; champ facile à prendre pour rempli ; la valeur par défaut devient un choix critique |
| Entrée contrainte | **Coût d'interaction élevé** : ouvrir, faire défiler, re-cliquer ; sur mobile, glissements et fermetures accidentelles |
| Familière | **Barrières d'accessibilité** : GOV.UK a documenté des difficultés persistantes (fermer la liste, essayer d'y taper, confondre élément focalisé et sélectionné, ne pas voir qu'on peut défiler, pincer-zoomer) et la recommande **en dernier recours** |
| « Données propres » | **Illusion** : plusieurs façons de nommer la même réponse (« Royaume-Uni », « Grande-Bretagne »…) ; l'option attendue peut être **absente** |

Alternatives : boutons radio pour peu d'options ; champ avec **autocomplétion** pour de longues
listes ; segmentation en deux étapes. Une liste déroulante garde un sens dans des cas
**étroits et précis** (voir l'article) [S126].

## 19.5 Quand valider ? (Smashing, Baymard, NN/g)

### Pourquoi valider « en ligne »
Baymard (tests à grande échelle) [S130] : recevoir une erreur **à l'envoi** force un arrêt complet,
fait perdre du temps, voire provoque l'**abandon**, et beaucoup de formulaires se réinitialisent.
La validation **en ligne** (pendant la saisie du champ) corrige les erreurs **quand l'entrée est
encore fraîche en mémoire**. Pourtant **31 % des sites** n'ont aucune validation en ligne (32 %
selon un autre passage de l'article) et **4 %** la font mal.

### Trois détails critiques [S130]
1. **Éviter la validation prématurée** : ne pas signaler « invalide » dès l'entrée dans un champ vide
   ni dès la première frappe ; valider **à la sortie du champ** (`onblur`) et, pour certains
   champs à longueur connue (code postal, téléphone, carte), **une fois la longueur atteinte**.
2. **Retirer le message dès que le champ est corrigé** (et non à la prochaine entrée dans le champ).
3. **Validation positive** pour tous les champs : une coche verte quand c'est bon.

### Les formes de validation (Friedman) [S129]
| Forme | Moment | Remarque |
|---|---|---|
| **Prématurée** | Dès le focus sur un champ vide | À **éviter** |
| **Temps réel** | Pendant la frappe, au-delà d'un seuil de caractères | Retarde la frustration sans la supprimer |
| **Tardive** (late) | **À la sortie du champ** (blur) | La plus courante ; évite les clignotements ; suppose un ordre de progression |
| **À l'envoi** | Au clic sur *Envoyer* | Souvent oubliée ; signal clair de fin de saisie |
| **« Récompenser tôt, punir tard »** | Succès immédiat, erreur différée | Équilibre recommandé |

Constat : « nous ne pouvons pas savoir avec certitude qu'un utilisateur a fini sa saisie sauf s'il
nous le dit ». **Chaque champ demande une conversation** (date : valider jour+mois ensemble pour
éviter les erreurs prématurées). Conclusion de l'article : mieux vaut **simplifier massivement**
le formulaire (3-4 champs) — il y aura moins d'erreurs à signaler.

## 19.6 Moins de champs = meilleure conversion (Baymard) [S131]
- Le parcours d'achat moyen en 2024 : **5,1 étapes** et **11,3 champs**, et **17 %** des utilisateurs
  ont abandonné à cause de la complexité.
- Ce qui compte le plus, ce n'est **pas** le nombre d'étapes mais **le nombre de champs** ; la
  plupart des sites n'auraient besoin que de **8 champs**.
- Cohérent avec NN/g : « chaque champ supprimé augmente la conversion » [S60].

## 19.7 Validation et saisie automatique côté web (web.dev)

- **Validation native HTML** [S132] : attributs `required`, `type="email"`, `minlength`/`maxlength`,
  `min`/`max`, `pattern` ; **toujours aussi valider côté serveur**. Les messages des navigateurs
  dépendent de la langue du navigateur ; on peut les personnaliser en JavaScript. **Communiquer
  les règles avant la saisie** (ex. « mot de passe : 8 caractères minimum »).
- **Remplissage automatique** [S133] : `autocomplete` avec des jetons standard (`email`,
  `postal-code`, `country`, `billing postal-code`, `shipping …`…) ; permet les gestionnaires de mots
  de passe (lié à WCAG 3.3.8) ; pseudo-classe CSS `:autofill`.

## 19.8 Application Android (Compose / Material)

*(Les noms d'API Compose ci-dessous relèvent de l'usage courant de la bibliothèque ; les règles de conception viennent des sources citées.)*

- Champs **outlined ou filled** avec **`label`** et, au besoin, **placeholder** ; **`supportingText`**
  pour l'aide et l'erreur ; `isError`; clavier adapté (`KeyboardType`) ; actions IME (suivant/OK).
- **Interrupteur (`Switch`)** pour un réglage à effet immédiat ; **cases** pour sélections
  multiples ; **boutons radio** pour un choix unique ; **chips filtre** pour filtres compacts [S90].
- **Date** : sélecteur de date Material (ancré, modal ou saisie) [S90].
- **Tests** : TalkBack lit l'étiquette et l'erreur ; l'ordre de focus suit l'ordre visuel [S80].

## 19.9 Check-list
- [ ] Chaque contrôle correspond à son usage (radio = exclusif ; case = indépendant ; toggle = immédiat).
- [ ] Pas de liste déroulante pour 2 ou 3 options (boutons radio) ; recherche/autocomplétion pour les longues listes.
- [ ] Validation à la sortie du champ ; erreur retirée dès correction ; coche positive.
- [ ] Étiquettes visibles, règles de format annoncées avant, saisie conservée en cas d'erreur.
- [ ] Autocomplétion/gestionnaire de mots de passe autorisés.
- [ ] Formulaire le plus court possible.

---
*Sources du chapitre : S60, S80, S90, S125–S133.*
