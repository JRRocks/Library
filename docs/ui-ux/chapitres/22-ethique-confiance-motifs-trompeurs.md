# Chapitre 22 — Éthique, confiance et motifs trompeurs

> Le design est un pouvoir : il peut aider l'utilisateur ou le piéger. Cette frontière est à la
> fois **éthique et légale**.

## 22.1 Qu'est-ce qu'un motif trompeur (deceptive / dark pattern) ?

Définition NN/g [S154] : un motif de design qui pousse l'utilisateur à une action qui **profite
à l'entreprise** en le **trompant, l'égarant, le faisant culpabiliser ou en entravant** sa capacité à
faire un autre choix, moins rentable. Ils nuisent par la **perte financière**, la **perte de vie
privée** et la perte de contrôle juridique ; ils réussissent davantage auprès des utilisateurs
**vulnérables** (peu de temps, faible littératie).

- Le concept (« dark patterns ») a été forgé par **Harry Brignull en 2010** (darkpatterns.org,
  aujourd'hui deceptive.design) ; livre *Deceptive Patterns* en 2023 [S154][S156].
- **Prévalence** : une étude (Princeton / Chicago, 2019) en trouve sur **plus de 10 %** de 11 000
  sites de commerce populaires ; une étude de Zurich (2019) sur **95 %** de 240 applis gratuites
  du Play Store, la moitié avec en moyenne **7** motifs [S154].
- **Causes** : l'**A/B testing** orienté conversion et la **copie des concurrents** [S154].
- **Sludge** (« vase ») : contraire du *nudge* ; toute friction qui empêche d'obtenir un résultat
  meilleur pour soi (ex. refuser les cookies en traversant de nombreux écrans) [S154].

## 22.2 Typologie

| Type | Définition | Exemple |
|---|---|---|
| **Obstruction** | Rendre difficile un choix qui ne profite pas à l'entreprise | Désabonnement long (poursuite de la FTC contre Amazon pour Prime) [S154] |
| **Astuces visuelles ou verbales** | Exploiter les limites cognitives ou casser les habitudes | Doubles négations ; bouton « Fermer » déplacé loin de la modale [S154] |
| **Harcèlement (nagging)** | Redemander après un refus | Demande d'autorisation répétée [S154] |
| **Manipulation émotionnelle** | Effrayer, culpabiliser, faire honte | **Confirmshaming** : « Non merci, je n'aime pas faire des économies » [S154][S156] |
| **Ajout furtif / présélection** | Ajouter ou précocher des options non demandées | Articles ajoutés au panier [S154] |
| **Appât et substitution (bait-and-switch)** | Annoncer un produit gratuit/très bas qui n'est pas réellement disponible | Affaire Intuit/TurboTax : règlement de **141 millions de dollars** en 2022 [S156] |
| **Prix goutte-à-goutte (drip pricing)** | Prix d'appel, frais révélés au fil du parcours | Taxes et frais tardifs [S156] |
| **Détournement (misdirection)** | Bouton « continuer » qui accepte un logiciel tiers | Installateurs [S156] |
| **« Privacy Zuckering »** | Faire partager plus d'informations que voulu | Options de confidentialité obscures [S156] |

Beaucoup de ces pratiques sont **illégales** (RGPD : traiter des données sans consentement explicite) ;
les régulateurs rattrapent leur retard [S154][S156].

## 22.3 Persuasion légitime ou tromperie ?

NN/g [S154] illustre la frontière : social proof (« vos contacts l'utilisent »), ancrage (prix barré) et
rareté (« plus que 2 chambres ») sont **persuasifs** — non trompeurs — **si les informations sont
vraies** (pas de fausse rareté ni d'avis achetés). L'exemple de Booking.com montre une **tromperie
douce** : le lien « Lire plus » montre les avis **négatifs** qu'on ne voit pas d'emblée, associé à la
rareté, il pousse des utilisateurs pressés à réserver sans réfléchir. « Certains designs trompeurs
ne se voient qu'en y réfléchissant. »

## 22.4 Comment les éviter : un parcours cognitif « éthique » [S154]

Les tests utilisateurs révèlent les motifs **évidents** (colère, dégoût) mais **pas** les motifs
**doux**, qui passent souvent inaperçus (surtout chez les moins instruits — Luguri et Strahilevitz).
NN/g propose de **modifier un cognitive walkthrough** avec des questions du type :

- l'utilisateur pourrait-il **dépenser ou donner plus de données** qu'il n'a voulu ou dû ?
- lorsqu'il consent à quelque chose en échange d'une fonction, **l'échange est-il juste** ?
- l'information sur chaque choix est-elle **exacte** ?
- peut-il **mal interpréter** les choix ? **manquer** un autre choix (caché, mal placé) ou une
  information essentielle ?
- peut-il **accéder vite** à toutes les informations et **appliquer vite** le choix voulu (sans
  étapes inutiles) ?

## 22.5 La confiance : quatre facteurs de crédibilité (NN/g, 2016) [S155]

Nielsen les listait déjà en **1999** ; l'étude de Singapour a confirmé qu'ils restent valables et
**identiques selon les cultures** : « les tendances de design changent, le comportement humain non ».

1. **Qualité du design** : organisation du site ; **libellés de navigation explicites** (les noms
   « malins » ou vagues font douter de l'existence du contenu) ; couleurs cohérentes avec le
   service (vert/blanc = propre, naturel) ; **espace blanc** (impression d'organisation) ;
   **fautes, liens cassés** minent la crédibilité (« c'est leur vitrine »).
2. **Transparence préalable** : coordonnées visibles, **ce qui est inclus dans le prix**, **frais
   annexes**, politiques de retour et garanties, **frais de livraison avant** la facturation.
3. **Contenu complet, exact et à jour** : une information abondante et pertinente montre
   l'expertise ; montrer **toutes les étapes du service** (pas seulement le résultat) ; couvrir toute
   la gamme offerte.
4. **Lien avec le reste du web** : avis, références, liens sortants.

## 22.6 Principes d'une conception honnête (synthèse)

1. **Dire la vérité** sur ce que fait l'interface (Rams n°6 « honnête » [S153]).
2. **Faire du bon choix le choix facile** ; ne jamais rendre un choix légitime pénible (désabonnement,
   refus de partage de données).
3. **Pas de présélection défavorable**, pas de frais cachés, pas de fausse urgence.
4. **Libellés symétriques** : « Accepter » / « Refuser » de même poids visuel.
5. **Consentement explicite et réversible** ; permission demandée **en contexte**, une fois.
6. **Pas de culpabilisation** (« confirmshaming »).
7. **Tester l'éthique** avec le parcours de § 22.4 à chaque version.
8. Même pour une app **hors ligne, sans publicité** : ne pas collecter de données non nécessaires ;
   s'interdire les démarches « growth hacking » qui détourneraient la confiance acquise.

---
*Sources du chapitre : S153–S156.*
