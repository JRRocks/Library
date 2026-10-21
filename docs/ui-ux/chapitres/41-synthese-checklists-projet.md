# Chapitre 41 — Synthèse : plan d'action et check-lists de conception

> **Chapitre de synthèse (rédaction propre)** : il ne rapporte pas une source unique mais
> **recoupe** les chapitres précédents sous forme d'ordre de travail. Chaque ligne renvoie au
> chapitre qui la fonde ; les chiffres viennent des sources citées dans ces chapitres.

## 41.1 Ordre de travail recommandé pour une nouvelle interface

| # | Étape | Sortie attendue | Chapitres |
|---|---|---|---|
| 1 | Cadrer : qui, pour quelle tâche, dans quel contexte (debout, une main, en plein soleil…) | Une phrase de problème + 3 à 5 tâches clés | 3, 4 |
| 2 | Écouter ou observer 5 utilisateurs réels | Notes, citations, obstacles | 4, 18 |
| 3 | Ordonner l'information (tri de cartes, arborescence) | Plan de navigation | 4, 11, 20 |
| 4 | Esquisser, puis wireframe | Écrans sans style | 18 |
| 5 | Poser les fondations visuelles : grille, échelle typographique, palette, espacement | Jetons de design | 7–10, 17, 27 |
| 6 | Assembler avec des composants standard | Écrans fidèles | 11, 13, 19 |
| 7 | Vérifier accessibilité et contraste | Liste de défauts corrigés | 9, 14, 33, 37 |
| 8 | Tester sur appareil réel avec la tâche, à voix haute | Liste priorisée de problèmes | 5, 18 |
| 9 | Corriger, retester, mesurer (SUS, taux de réussite, temps) | Indicateurs avant/après | 4, 36 |

## 41.2 Check-list « avant de montrer un écran »

**Clarté et hiérarchie** (ch. 7, 27)
- [ ] Un seul élément principal par écran ; test des yeux plissés réussi.
- [ ] L'espace dans un groupe est plus petit que l'espace entre groupes.
- [ ] Au plus deux familles de polices ; une échelle de tailles définie ; interlignage suffisant.

**Interaction** (ch. 5, 6, 11)
- [ ] Le système montre ce qui se passe ; l'utilisateur peut annuler ; Retour fait ce qu'on attend.
- [ ] Cibles tactiles ≥ 48 dp, espacées, dans la zone du pouce pour les actions fréquentes (ch. 12).
- [ ] Libellés de boutons en verbes concrets ; pas de vocabulaire interne (ch. 16).

**Formulaires** (ch. 19, 34)
- [ ] Étiquette visible hors du champ ; type de clavier adapté ; autofill pris en charge.
- [ ] Erreur à côté du champ, dite simplement, avec la façon de corriger ; saisie conservée.

**États et retours** (ch. 11, 15)
- [ ] État vide, chargement, erreur et succès conçus ; indicateur au-delà d'environ 1 s.
- [ ] Animations courtes ; respect de la réduction des animations.

**Accessibilité** (ch. 9, 14, 33)
- [ ] Contraste 4,5:1 (texte) / 3:1 (grand texte, icônes) ; information jamais portée par la couleur seule.
- [ ] Descriptions de contenu ; ordre de focus logique ; test avec grossissement du texte.

**Confiance** (ch. 22, 35)
- [ ] Aucune case précochée, aucun coût caché, aucune sortie rendue difficile.
- [ ] Connexion exigée seulement quand elle est nécessaire.

## 41.3 Pièges fréquents et chapitre correspondant

| Piège | Remède | Chapitre |
|---|---|---|
| « Ça me paraît évident » (biais du concepteur) | Tester avec des gens extérieurs | 39, 18 |
| Placeholder utilisé comme étiquette | Étiquette visible | 16, 19, 34 |
| Icônes sans libellé | Ajouter un texte | 21 |
| Tout est mis en avant | Choisir une action principale | 7, 20 |
| Texte gris clair sur fond blanc | Mesurer le contraste | 9 |
| Rouge/vert seuls pour signaler un état | Ajouter forme, icône ou texte | 37 |
| Demander les permissions au lancement | Demander en contexte | 40 |
| Écrans qui perdent la saisie à la rotation | Préserver l'état | 40 |
| Menu caché pour les actions essentielles | Navigation visible | 11 |
| Chargement sans retour | Indicateur, squelette | 11, 15 |

## 41.4 Quand deux règles s'opposent

Aucune règle n'est absolue ; les sources signalent elles-mêmes des arbitrages :

- **Proximité contre similarité** : la proximité l'emporte (ch. 29).
- **Innover contre rester conventionnel** : innover seulement si l'avantage est net (ch. 39).
- **Densité contre lisibilité** : choisir selon la tâche et le contexte, puis tester (ch. 27, 20).
- **Persuader contre respecter** : un nudge qui sert l'utilisateur est acceptable ; celui qui le trompe est un motif trompeur (ch. 22, 31).

En cas de doute : **tester** avec de vrais utilisateurs plutôt que trancher en débat (ch. 18).

---
*Sources : voir les chapitres cités ; ce chapitre n'introduit pas de nouvelle source.*
