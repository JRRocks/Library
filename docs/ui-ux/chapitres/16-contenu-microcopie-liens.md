# Chapitre 16 — Le contenu : écrire pour l'écran, libellés, liens, messages

> Une interface est **faite de mots** : boutons, étiquettes, titres, erreurs, aides. Le design
> visuel est « à quoi ressemble l'entreprise » ; l'écriture est « comment elle parle » [S110].

## 16.1 Comment les gens lisent : ils balayent

Étude NN/g (Nielsen, 1997) [S105] : **79 % des utilisateurs testés balayaient toujours** toute
nouvelle page ; **16 %** seulement lisaient mot à mot. Les pages doivent donc offrir un **texte
« scannable »** :

- **mots-clés mis en évidence** (liens, variations de caractères, couleur) ;
- **intertitres explicites** (pas « malins ») ;
- **listes à puces** ;
- **une idée par paragraphe** (les autres idées sont sautées si les premiers mots n'accrochent pas) ;
- **pyramide inversée** : commencer par la conclusion ;
- **moitié moins de mots** (ou moins) qu'un texte conventionnel.

**Crédibilité** : sur le web, on ne sait pas qui est derrière l'information ; la crédibilité
augmente avec de bons graphismes, une bonne écriture et des liens sortants. Les utilisateurs
**détestent le « marketese »** (style promotionnel aux affirmations subjectives : « le plus
chaud jamais vu ») [S105].

### Mesure de l'effet d'une meilleure écriture [S105]

Cinq versions d'un même site (mêmes informations, mots différents, même navigation) :

| Version | Amélioration de l'usabilité mesurée |
|---|---|
| Promotionnelle (témoin) | — |
| **Concise** (moitié moins de mots) | **+ 58 %** |
| **Scannable** (mise en page facilitant le balayage) | **+ 47 %** |
| **Objective** (langage neutre) | **+ 27 %** |
| **Combinée** (concise + scannable + objective) | **+ 124 %** |

Hypothèse de NN/g : le langage promotionnel impose une **charge cognitive** (il faut démêler le
fait de l'enflure). Même les mesures de performance (temps, erreurs, mémoire, structure du site)
étaient meilleures avec le langage objectif.

## 16.2 Lisibilité, légibilité, compréhension (Nielsen, 2015) [S110]

Le contenu doit survivre à une analyse **coût-bénéfice** de l'utilisateur (coût : peine ;
bénéfice : « qu'est-ce que j'y gagne ? »). Trois barrières :

| Barrière | Définition | Règles principales |
|---|---|---|
| **Légibilité** | Peut-on **voir, distinguer, reconnaître** les caractères et mots ? (surtout typographie) | Taille par défaut raisonnable **et modifiable** ; **fort contraste** ; fond uni plutôt que texturé ; police sobre (les polices fantaisie manuscrites/gothiques réduisent la légibilité) |
| **Lisibilité** | **Complexité** des mots et de la structure des phrases (niveau d'études requis) | **Mots simples et courts** ; **phrases courtes** ; éviter les subordonnées empilées (la mémoire à court terme est faible) ; **voix active** ; cibler un niveau de lecture nettement inférieur au niveau d'études de l'audience |
| **Compréhension** | Comprend-on réellement le sens ? | Se tester (voir la source) |

Test de légibilité : mesurer la **vitesse de lecture** (mots par minute) en intra-sujets ; plus
de 20 % plus lent que la référence signale une mauvaise légibilité [S110].

## 16.3 Écrire pour les lecteurs de faible niveau de lecture [S108]

Les lecteurs peu à l'aise **« labourent »** le texte mot à mot, avec un champ de vision étroit, et
**ne balayent pas** ; ils se contentent de « suffisamment bon » (*satisficing*), sautent dès que
le texte est dense, et peinent avec la recherche (orthographe, extraits hors contexte).
Conseils : texte de **niveau 6ᵉ année** sur l'accueil et les pages clés, **8ᵉ année** ailleurs ;
point principal **tout en haut** ; éviter les textes qui bougent ; **une seule colonne** ;
**menus linéaires** ; recherche **tolérante aux fautes**.

## 16.4 Mettre en forme les contenus longs (NN/g, 2023) [S107]

Pour tout contenu de plus de 1 000 mots, l'étude a retenu :

- **Planifier et éditer d'abord** : le contenu est-il essentiel ? bon niveau de granularité ?
  condensable ? reformulable plus simplement ?
- **Structurer avant de formater** : **aperçu** (table des matières), **blocs digestes**,
  **information en couches** (accordéons), **accès direct** aux sections (liens internes).
- **Cinq techniques de mise en forme** : **résumés**, **puces**, **encadrés**, **gras/mise en
  évidence**, **visuels utiles**. Le **résumé** doit être distinct (titre descriptif, bordure ou
  fond) ; au **début**, il sert de feuille de route.

## 16.5 Libellés de liens et de boutons

### Un lien est une promesse [S112]
Les mots d'un lien suggèrent fortement la page cible ; **la page doit tenir la promesse**
(un lien « Produits et services » qui ouvre une page d'inscription trahit l'utilisateur).
Comme les liens attirent le regard (schéma « en tache », *spotted*), **ils doivent se
comprendre isolément** : éviter « plus », « lire la suite » ; préférer des expressions
descriptives (« Discuter avec un conseiller »). À l'arrivée, **confirmer** l'attente : contenu
attendu visible sans défilement, titre de page visible, premier pas de l'applet affiché, image
liée au sujet.

### Écrire un lien [S106]
- **Saillant** : le texte souligné et bleu reste l'indice le plus évident.
- **Descriptif et unique** : se demander « qu'obtient-on en cliquant ? » ; mentionner le format
  (ex. PDF) ; **commencer par le mot-clé** ; éviter « ici » (inutile pour un lecteur d'écran qui
  liste les liens).
- Les liens vagues nuisent à l'**usabilité, l'accessibilité et le référencement**.

### L'odeur de l'information (information scent) [S113]
Théorie de la quête d'information : face à des liens, on choisit celui dont **l'odeur de
l'information** est la plus forte, c'est-à-dire une estimation de **(1) la probabilité que la
page réponde à la question** et **(2) le temps nécessaire**. Elle se compose du libellé du lien,
du contexte et de l'expérience préalable ; elle est **relative** au besoin (un lien « Alimentation »
a une forte odeur pour qui cherche du fromage, faible pour un soin du visage). **Le clic
trompeur (« click bait ») ne fonctionne pas à long terme.**

### Libellés de boutons
- Dire **l'action** : « Enregistrer et continuer », « Confirmer et envoyer » [S63] ; éviter
  « OK », « Continuer », « Suivant » seuls [S62].
- **Casse de phrase**, sans deux-points final pour les étiquettes de champs [S64].

## 16.6 Placeholders : pourquoi les éviter (NN/g) [S109]

Un placeholder est un indice **dans** le champ qui disparaît à la saisie ; ce n'est pas une
étiquette. NN/g liste sept problèmes lorsqu'il **remplace** l'étiquette :

1. il disparaît et **sollicite la mémoire à court terme** (utilisateurs interrompus, mobiles) ;
2. impossible de **vérifier** son formulaire avant l'envoi (ni détecter une saisie
   automatique erronée) ;
3. en cas d'erreur, on ne sait pas comment la corriger ;
4. il gêne les utilisateurs **au clavier** (Tab) ;
5. les champs **remplis attirent moins l'œil** que les champs vides ;
6. on le **prend pour une valeur déjà saisie** et on saute le champ ;
7. il faut parfois l'effacer **à la main**.

Conclusion : **étiquette visible au-dessus du champ**, texte d'aide séparé si besoin (voir
chap. 11 et [S64]). *(Android : Material propose un `label` flottant et un `placeholder` ; le
guide d'accessibilité Android recommande d'utiliser **les deux** — libellé descriptif + exemple
de saisie [S80].)*

## 16.7 Content design à la GOV.UK [S111]

Le contenu **bien conçu** aide les gens à trouver vite ce qu'ils doivent savoir ou faire. Il :
1. **part des besoins des utilisateurs** (« vérifier mon éligibilité », « m'inscrire sur les
   listes électorales ») ;
2. considère la **quantité, le format et le meilleur endroit** de publication : réduire, **découper**,
   changer de format, supprimer les doublons ou contradictions, publier ailleurs si nécessaire ;
3. suit un **style** (anglais simple, optimisé pour le web, accessible) ;
4. est **tenu à jour** : soit actuel, soit conservé comme archive.

## 16.8 Messages : récapitulatif

| Situation | Bon message | Mauvais message |
|---|---|---|
| Champ vide | « Saisissez votre adresse » | « Champ vide » |
| Format e-mail | « Saisissez une adresse e-mail au format nom@domaine.fr » | « E-mail invalide » |
| Rupture de stock | « Disponible le 4 novembre — être prévenu » | « Erreur » |
| Suppression | « Dossier supprimé. **Annuler** » | « Êtes-vous sûr de vouloir… ? » à chaque fois |
| Lecture illisible (scan continu) | *(rien — règle métier ScanList)* | Message à chaque image |

*(Exemples inspirés de [S61], [S97], [S72] ; formulations françaises propres au dossier.)*

## 16.9 Check-list contenu

- [ ] Un titre explicite par écran ; l'essentiel en premier.
- [ ] Phrases courtes, voix active, mots simples ; pas de jargon ni de marketese.
- [ ] Listes et intertitres ; une idée par paragraphe.
- [ ] Libellés de boutons = verbes d'action ; liens compréhensibles hors contexte.
- [ ] Étiquettes visibles ; pas de placeholder en guise d'étiquette.
- [ ] Erreurs : quoi, pourquoi, comment corriger ; ton neutre ; saisie conservée.
- [ ] Même mot pour la même chose partout (vocabulaire contrôlé).

---
*Sources du chapitre : S61–S64, S80, S97, S105–S113.*
