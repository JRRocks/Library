# Chapitre 39 — Modèles mentaux : ce que l'utilisateur croit savoir

> Concept central de l'IHM : la plupart des erreurs d'usage viennent d'un écart entre ce que
> l'utilisateur croit du système et ce que le système fait. Sources : NN/g [S19], Google PAIR [S157],
> Laws of UX [S32].

## 39.1 Définition [S19]

Un **modèle mental** est ce que l'utilisateur **croit** à propos d'un système (site, application,
produit). Il lui sert à **prédire** le comportement du système et donc à planifier ses actions.

- Il repose sur la **croyance, pas sur les faits** : l'utilisateur peut se tromper.
- **Chaque utilisateur a le sien**, selon son expérience et ses connaissances antérieures.
- Il **évolue** avec l'expérience du système ou d'autres systèmes.
- Il se construit en grande partie avec ce que l'on sait du monde et **d'autres sites** : c'est la loi de
  Jakob (« les utilisateurs passent la plupart de leur temps sur d'autres sites que le vôtre »)
  (ch. 6).
- **Dilemme majeur** de l'usabilité : l'écart entre le modèle mental du **concepteur** (très détaillé,
  qui trouve chaque fonction évidente) et celui de l'**utilisateur** (moins développé, donc plus
  d'erreurs).

PAIR donne une définition voisine : la compréhension qu'a une personne du fonctionnement d'une chose
et de l'effet de ses actions sur elle. Un modèle mental fixe les attentes (ce que le produit peut ou ne
peut pas faire) et sert de **pont entre expériences** [S157].

## 39.2 Quatre exemples documentés par NN/g [S19]

| Situation | Modèle mental attendu | Violation et conséquence |
|---|---|---|
| **Panier d'achat en ligne** | Comme en magasin : c'est *moi* qui décide de ce que je mets dans le panier | Une assurance de livraison ajoutée automatiquement (cas Nomad Lane) : il faut la repérer puis la refuser. C'est un motif trompeur (*sneaking / preselection*, ch. 22) |
| **Bouton Retour** | Revenir à l'écran précédent, comme une annulation | Certaines applis mobiles faisaient remonter dans la hiérarchie au lieu de revenir en arrière (de l'article B vers la liste, et non vers l'article A) ; une superposition (*overlay*) plein écran ressentie comme une nouvelle page : Retour saute deux étapes |
| **Barre de recherche** | Une seule barre, qui cherche partout | Deux barres sur une même page (cas d'une bibliothèque universitaire : logistique vs recherche académique) : on tape dans la mauvaise et on conclut que le site n'a pas la réponse |
| **Retour Android sur l'écran d'accueil** | — | Reste, selon NN/g, un « vestige » : Retour y fait sortir de l'appli |

## 39.3 Inertie des modèles mentaux [S19]

- Ce que les gens connaissent bien **persiste, même quand ça ne les aide pas**.
- Conséquence : **être conservateur** sur les styles d'interaction. N'innover que si la nouvelle approche
  est **clairement supérieure** à l'ancienne.
- Une seule idée fausse peut faire mal interpréter **toute** une session ; l'utilisateur ne remet pas ses
  hypothèses de base en question.
- Si l'on innove : expliquer, en sachant que les gens **ne lisent pas** les aides.
- **Exploiter l'inertie plutôt que la combattre** : le skeuomorphisme (éléments du monde physique)
  aide à apprivoiser de nouvelles interactions. Cas des applications pour enfants, qui n'ont pas encore de
  modèles de navigation numérique : métaphores de cartes, de portes (exemple cité : l'appli Duo ABC).

## 39.4 Que faire en cas d'écart ? [S19]

Deux options :

1. **Adapter le système au modèle mental des utilisateurs** (si la plupart partagent un modèle proche) :
   c'est ce que NN/g recommande d'ordinaire pour les problèmes d'architecture de l'information — si
   l'on cherche au mauvais endroit, **déplacer** l'élément là où on le cherche. Outil : le **tri de cartes**
   (ch. 4) pour découvrir le modèle mental de l'espace d'information.
2. **Améliorer le modèle mental de l'utilisateur** : mieux expliquer, clarifier les **libellés**, même si le
   système reste identique.

Même si l'on ne peut pas changer l'interface, on peut **enseigner un modèle plus juste plus tôt** dans
l'expérience, ou **cesser de faire des distinctions** que les utilisateurs ne comprendront pas.

**Comment les découvrir** : la méthode du **protocole à voix haute** (*think-aloud*, ch. 18) est la plus
simple : en verbalisant ce qu'ils pensent et prédisent, les participants révèlent leur modèle. Des
méthodes d'élicitation des connaissances plus avancées existent ; pour la plupart des équipes, quelques
séances suffisent [S19].

Les modèles mentaux sont aussi la clé des **instructions, documentations, tutoriels, démos** : courts,
mais enseignant les concepts essentiels dont l'utilisateur a besoin [S19].

## 39.5 Modèles mentaux et IA (PAIR) [S157]

Les systèmes à base d'IA **s'adaptent dans le temps**. PAIR retient quatre considérations clés :

1. **Préparer à l'adaptation** : la plupart des produits sont statiques (le marteau d'aujourd'hui est celui de
   demain) ; les produits adaptatifs exigent d'enseigner la relation dynamique entre entrée de
   l'utilisateur et sortie. S'appuyer sur les modèles existants.
2. **Introduire par étapes (onboarding)** : expliquer ce que le produit peut faire, ne peut pas faire,
   comment il peut évoluer et comment l'améliorer. L'expérience commence avant le téléchargement
   (marketing, publicités, mode d'emploi) : coordonner le message avec le marketing. Promettre de la
   « magie IA » fait surestimer les capacités ; cacher le fonctionnement crée confusion et perte de
   confiance.
3. **Prévoir le co-apprentissage** : les retours des utilisateurs ajustent les modèles, qui changent les
   interactions, qui changent les modèles… et les modèles mentaux changent en parallèle.
4. **Tenir compte des attentes d'interaction « humaine »** : un produit supposé avoir des capacités
   humaines suscite des attentes irréalistes ; communiquer sa nature algorithmique et ses limites pour
   éviter la tromperie involontaire.

Questions de départ proposées par PAIR : que cherche à faire l'utilisateur ? quels modèles mentaux
apporte-t-il ? quelle est la démarche pas à pas des novices aujourd'hui ? quelle uniformité entre les
utilisateurs ? [S157]

## 39.6 Check-list

- [ ] Quel modèle mental l'utilisateur apporte-t-il (autres applis, monde physique) ?
- [ ] Le comportement du bouton Retour correspond-il à l'attente (annuler / revenir) ?
- [ ] Une seule fonction de recherche par page, ou des libellés sans ambiguïté ?
- [ ] Rien n'est ajouté ou coché à l'insu de l'utilisateur ?
- [ ] Toute nouveauté est-elle meilleure *nettement* que la convention, et expliquée ?
- [ ] Un test à voix haute a-t-il été mené pour déceler les modèles erronés ?

---
*Sources du chapitre : S19, S32, S157.*
