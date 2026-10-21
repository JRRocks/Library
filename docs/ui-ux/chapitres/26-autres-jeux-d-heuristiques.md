# Chapitre 26 — Autres jeux d'heuristiques : Shneiderman, Gerhardt-Powals, Weinschenk-Barker

> Les dix heuristiques de Nielsen (chap. 5) sont les plus célèbres, mais pas les seules. Plusieurs
> jeux **se recoupent** ; en comparer plusieurs aide à ne rien oublier lors d'une inspection.
> D'après Wikipédia [S17] : « il existe de nombreux jeux d'heuristiques d'usabilité ; ils ne
> s'excluent pas mutuellement et couvrent beaucoup des mêmes aspects ».

## 26.1 Les huit règles d'or de Shneiderman (1986) [S17][S174]

Ben Shneiderman (université du Maryland, laboratoire d'IHM), *Designing the User Interface:
Strategies for Effective Human-Computer Interaction* (1986). Ces règles sont antérieures à celles de
Nielsen et se retrouvent dans les guides de style d'Apple, Google et Microsoft [S174].

| N° | Règle | Contenu | Exemple de [S174] |
|---|---|---|---|
| 1 | **Viser la cohérence** | Séquences d'actions cohérentes dans des situations similaires : icônes, couleurs, hiérarchie de menus, appels à l'action, parcours | Le menu de Mac OS reste cohérent des années 1980 aux années 2010 |
| 2 | **Permettre aux utilisateurs fréquents d'utiliser des raccourcis** | Plus on utilise, plus on veut réduire le nombre d'interactions | Raccourcis clavier copier-coller |
| 3 | **Offrir un retour informatif** | Pour chaque action, un retour **lisible par un humain** dans un délai raisonnable | Indiquer l'étape dans un questionnaire à plusieurs pages ; contre-exemple : un message avec un code d'erreur |
| 4 | **Concevoir des dialogues qui produisent une clôture** | Séquences d'actions organisées avec **début, milieu et fin** ; dire où l'action a mené | Message de remerciement et reçu après un achat |
| 5 | **Offrir une gestion simple des erreurs** | Concevoir pour qu'on **ne puisse pas commettre d'erreur grave** ; sinon, instructions simples pas à pas | Signaler les champs oubliés |
| 6 | **Permettre l'annulation facile des actions** | Soulage l'**anxiété** et **encourage l'exploration** | Revenir à l'étape précédente d'une installation |
| 7 | **Soutenir le lieu de contrôle interne** | L'utilisateur doit être **l'initiateur** des actions, non le répondant ; il doit sentir qu'il est aux commandes | — |
| 8 | **Réduire la charge de la mémoire à court terme** | Affichages simples, pages multiples consolidées, peu de mouvement de fenêtres ; **reconnaissance plutôt que rappel** | Choix multiples plutôt que réponse ouverte |

## 26.2 Les principes d'ingénierie cognitive de Gerhardt-Powals [S17]

Jill Gerhardt-Powals propose une approche **plus holistique** et centrée sur la **performance
cognitive** :

| N° | Principe | Idée |
|---|---|---|
| 1 | **Automatiser la charge indésirable** | Supprimer calculs mentaux, estimations, comparaisons inutiles |
| 2 | **Réduire l'incertitude** | Afficher les données de façon claire et évidente |
| 3 | **Fusionner les données** | Regrouper des données de bas niveau en une synthèse de haut niveau |
| 4 | **Présenter les nouveautés avec des aides à l'interprétation** | Cadres familiers (schémas, métaphores, termes courants) |
| 5 | **Utiliser des noms conceptuellement liés à la fonction** | Libellés dépendants du contexte, qui améliorent reconnaissance et rappel |
| 6 | **Regrouper les données de façon cohérente et signifiante** | Logique sur l'écran, cohérente d'un écran à l'autre |
| 7 | **Limiter les tâches pilotées par les données** | Couleur et graphiques pour réduire le temps d'assimilation |
| 8 | **N'inclure que l'information nécessaire à un moment donné** | Exclure l'extraneous |
| 9 | **Fournir un codage multiple des données quand c'est approprié** | Formats ou niveaux de détail variés |
| 10 | **Pratiquer une redondance judicieuse** | Résout le conflit possible entre les principes 6 et 8 |

## 26.3 La classification de Weinschenk et Barker (2000) [S17]

Vingt types d'heuristiques, issues de plusieurs grands fournisseurs :

| Catégorie | Énoncé |
|---|---|
| Contrôle de l'utilisateur | L'interface lui permet de percevoir qu'il contrôle |
| Limites humaines | Ne surcharge pas ses limites cognitives, visuelles, auditives, tactiles, motrices |
| Intégrité modale | Adapte les tâches à la modalité utilisée |
| Accommodation | S'adapte à la façon dont chaque groupe travaille et pense |
| Clarté linguistique | Communique le plus efficacement possible |
| Intégrité esthétique | Design attrayant et approprié |
| Simplicité | Présente les éléments simplement |
| Prévisibilité | Comportement dont on peut prédire la suite |
| Interprétation | Fait des suppositions raisonnables sur l'intention |
| Exactitude | Sans erreur |
| Clarté technique | Fidélité la plus haute |
| Flexibilité | Réglable pour des usages personnalisés |
| Accomplissement | Expérience satisfaisante |
| Convenance culturelle | Respecte coutumes et attentes sociales |
| Tempo adapté | Fonctionne à un rythme convenable |
| Cohérence | Cohérente |
| Soutien à l'utilisateur | Aide supplémentaire à la demande |
| Précision | Permet d'exécuter une tâche exactement |
| Indulgence | Actions récupérables |
| Réactivité | Informe des résultats des actions et de l'état |

## 26.4 Heuristiques propres à un domaine ou à une culture [S17]

Pour une application à **domaine** ou **culture** spécifiques, les heuristiques génériques peuvent
**ne pas identifier** les problèmes propres au contexte ; on complète alors par des heuristiques
du domaine (par exemple pour un moteur de **scan en continu**, un critère « le chemin critique n'est
jamais suspendu »).

## 26.5 Tableau de correspondance

| Thème | Nielsen | Shneiderman | Gerhardt-Powals |
|---|---|---|---|
| État / retour | 1 Visibilité de l'état | 3 Retour informatif | 2 Réduire l'incertitude |
| Langage | 2 Monde réel | — | 4, 5 |
| Contrôle | 3 Contrôle et liberté | 6 Annulation ; 7 Contrôle interne | — |
| Cohérence | 4 Cohérence | 1 Cohérence | 6 Groupement cohérent |
| Erreurs | 5 Prévention ; 9 Récupération | 5 Gestion simple | — |
| Mémoire | 6 Reconnaissance | 8 Charge mémoire | 1 Automatiser ; 3 Fusionner |
| Efficacité | 7 Flexibilité | 2 Raccourcis | 7 Limiter les tâches pilotées par les données |
| Minimalisme | 8 Esthétique et minimalisme | — | 8 Information nécessaire |
| Clôture | — | 4 Clôture | — |

## 26.6 Méthode pratique

1. **Choisir un jeu principal** (Nielsen) pour la grille d'inspection (chap. 5).
2. **Compléter** par les règles de Shneiderman 4 (clôture) et 7 (lieu de contrôle), absentes ailleurs.
3. Pour les écrans riches en données, ajouter **Gerhardt-Powals** (fusion, réduction d'incertitude).
4. **Ajouter 2 à 3 heuristiques de domaine** spécifiques à l'application.
5. Noter chaque constat avec une gravité et le principe violé ; corriger par gravité décroissante.

---
*Sources du chapitre : S5, S17, S20, S174.*
