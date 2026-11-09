# Chapitre 23 — Concevoir des interfaces avec de l'IA (People + AI Guidebook)

> Dès qu'une app **prédit, reconnaît ou recommande** — et un moteur de scan OCR en est un —
> son comportement est **probabiliste**. Le *People + AI Guidebook* de Google (PAIR) fournit un
> cadre pour concevoir autour de cette incertitude.

## 23.0 Pourquoi ce chapitre concerne aussi l'OCR et le scan

Un OCR ou un lecteur de code ne « sait » pas : il **estime**. Le consensus de N lectures
identiques de ScanList est une réponse de conception à cette incertitude : on ne montre un
résultat que lorsqu'il est **suffisamment fiable**. Les principes ci-dessous généralisent cette
logique.

## 23.1 Modèles mentaux et IA [S157]

Un modèle mental est la compréhension qu'a une personne du fonctionnement d'un produit et de
l'effet de ses actions. **Des modèles mentaux décalés** mènent à **attentes déçues, frustration,
mauvais usages et abandon**. Quatre considérations :

1. **Préparer à l'adaptation** : les systèmes qui s'adaptent à l'usage sont courants ; **s'appuyer
   sur les modèles mentaux existants** (comment fait-on aujourd'hui sans IA ?) et enseigner la
   **relation dynamique** entre ce que l'on fait et ce que le produit répond. Exemple : si l'on
   étiquette soi-même ses mails, on croit que l'IA fait « comme moi » alors qu'elle peut utiliser
   d'autres signaux (heure d'envoi, longueur).
2. **Accompagner par étapes** (onboarding progressif) : expliquer **ce qu'il peut faire, ce qu'il
   ne peut pas faire, comment il peut évoluer, comment l'améliorer**. **Éviter la « magie de
   l'IA »** dans la communication : elle fait surestimer les capacités. Mettre en avant le **bénéfice**
   pour l'utilisateur plutôt que la technologie.
3. **Prévoir la co-apprentissage** : les retours des utilisateurs changent le modèle, qui change
   leurs interactions, donc leurs modèles mentaux.
4. **Tenir compte des attentes d'interaction « humaine »** : plus un produit paraît humain, plus
   les attentes sont irréalistes ; **communiquer la nature algorithmique et les limites** pour
   éviter la tromperie involontaire.

## 23.2 Explicabilité et confiance [S158]

Parce que les systèmes d'IA reposent sur la **probabilité et l'incertitude**, « le bon niveau
d'explication » aide à comprendre ; l'**explicabilité et la confiance sont liées**.

**Confiance** = « disposition à prendre un risque dans l'attente d'un bénéfice ». Ses facteurs :
- **Capacité** (ability) : le produit répond-il au besoin ? ;
- **Fiabilité** (reliability) : tient-il ses promesses **de façon constante** ? Ne lancer que si l'on
  peut atteindre le niveau annoncé et décrit honnêtement ;
- **Bienveillance** (benevolence) : croyance que l'autre veut le bien de l'utilisateur ; être
  **honnête sur ce que chacun y gagne**.

Quatre considérations :
1. **Aider à calibrer la confiance** : l'utilisateur ne doit ni se méfier de tout (« aversion
   algorithmique ») ni **sur-faire confiance** ; indiquer qu'une prédiction peut être fausse peut
   la rendre moins crue, mais renforce la confiance à long terme ;
2. **Planifier la calibration tout au long de l'expérience** (la confiance se gagne lentement) ;
3. **Optimiser pour la compréhension** : parfois pas d'explication complète possible ; utiliser
   des **explications partielles** ;
4. **Maîtriser l'influence sur les décisions** : quand et comment afficher la **confiance du
   modèle** (score, fourchette…) pèse sur ce que fera l'utilisateur.

## 23.3 Les données (collecte et évaluation) [S159]

- Les données d'entraînement et leurs étiquettes **déterminent** le résultat et l'expérience.
- **Cascades de données** : effets retardés d'un décalage entre données d'entraînement et données
  réelles (exemple : une appli de reconnaissance de plantes entraînée surtout sur la flore
  nord-américaine, qui échoue en Amérique du Sud).
- Six points : **planifier des données de qualité dès le début** ; **traduire les besoins des
  utilisateurs en besoins de données** (pouvoir prédictif, pertinence, équité, vie privée, sécurité) ;
  **sourcer de façon responsable** ; **préparer et documenter** ; **concevoir pour les annotateurs**
  (consignes et interfaces d'étiquetage) ; **régler le modèle** en interprétant les sorties.
- Question à se poser : **l'IA est-elle vraiment la bonne approche ?**

## 23.4 Application aux fonctions de reconnaissance (scan, OCR, voix)

| Principe | Application concrète (synthèse du dossier) |
|---|---|
| Ne pas surpromettre | Libellés honnêtes : « Lecture » plutôt que « Reconnaissance parfaite » |
| Consensus avant affichage | N lectures identiques avant enregistrement (voir fiche *moteur de scan* de la Library) |
| Retour continu | Réticule, mise en évidence de la zone lue, compteur de confirmations |
| Échec silencieux ou explicite ? | Décision métier : *silencieux* en scan continu (coût d'un message à chaque image), *explicite* quand l'utilisateur déclenche une lecture unique |
| Correction facile | Permettre de **modifier** une valeur lue (Dietvorst : on utilise mieux un algorithme imparfait si on peut le modifier — référence citée dans [S158]) |
| Mesurer | Taux de fausses lectures sur le terrain ; ne promouvoir un réglage que sur preuve |

## 23.5 Check-list
- [ ] Les limites du système sont dites à l'utilisateur.
- [ ] La confiance est calibrée (pas de promesse magique).
- [ ] Les résultats incertains sont marqués comme tels ou filtrés par un seuil.
- [ ] L'utilisateur peut corriger et reprendre la main.
- [ ] Les données d'essai couvrent le terrain réel (lumière, matériaux, polices, langues).

---
*Sources du chapitre : S157–S159.*
