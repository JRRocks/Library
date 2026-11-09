# Chapitre 3 — Les processus de conception

> Un bon design ne sort pas d'une inspiration : il sort d'un **processus** répété. Ce chapitre
> compare les cadres reconnus (design thinking, Double Diamond, design centré utilisateur) et
> en tire une méthode utilisable seul ou en petite équipe.


## 3.1 Le design thinking (NN/g)

Sarah Gibbons (NN/g) [S5] présente le design thinking comme **une idéologie soutenue par un
processus**. L'idéologie : une approche concrète centrée sur l'utilisateur mène à
l'innovation, et l'innovation à la différenciation. Le processus : **six phases** regroupées en
trois mouvements — *comprendre, explorer, matérialiser*.

| Phase | Ce qu'on fait | Question directrice |
|---|---|---|
| **1. Empathiser** | Recherche pour savoir ce que les utilisateurs *font, disent, pensent, ressentent* | Qu'est-ce qui les motive ou les décourage ? Où sont les frustrations ? |
| **2. Définir** | Synthétiser, repérer où se situent les problèmes, nommer les besoins non satisfaits | Quel point de douleur est commun à plusieurs utilisateurs ? |
| **3. Idéer** | Brainstorming : la quantité prime sur la qualité, aucune idée n'est trop folle | Quelles idées répondent aux besoins identifiés ? |
| **4. Prototyper** | Représentations tangibles d'un sous-ensemble d'idées | Quelles composantes marchent, lesquelles non ? Impact vs faisabilité ? |
| **5. Tester** | Retour devant de vrais utilisateurs | La solution répond-elle au besoin ? Améliore-t-elle leur façon de faire ? |
| **6. Implémenter** | Mettre la vision en œuvre | La solution touche-t-elle réellement la vie des utilisateurs ? |

Points clés de [S5] :

- L'étape **implémenter** est « la plus importante, mais la plus souvent oubliée ». Le design
  thinking ne dispense pas du *design doing* (Don Norman : « nous avons besoin de plus de
  design *faisant* »). Citation de Milton Glaser reprise par l'article : la créativité est « un
  verbe, un verbe qui prend beaucoup de temps » ; si c'est bien fait, ça ressemble à du travail.
- **Itératif et cyclique** : on revient souvent à *empathiser* et *définir* après un premier
  prototype testé. « Soyez un chef, pas un cuisinier à la chaîne : prenez la recette comme un
  cadre, puis adaptez. »
- Nielsen : « une interface merveilleuse qui résout le mauvais problème échouera. »
- Le design thinking n'est **pas nouveau** : Charles et Ray Eames pratiquaient déjà
  « apprendre en faisant » dans la première moitié du XXᵉ siècle.

## 3.2 Le Double Diamond (Design Council, 2005)

Modèle popularisé en 2005 par le Design Council britannique, adapté du modèle
divergence-convergence de Béla H. Bánáthy (1996) [S6]. Deux diamants = deux fois « explorer
largement (pensée **divergente**) puis agir de façon ciblée (pensée **convergente**) ».

| Phase | Mouvement | Contenu [S6] |
|---|---|---|
| **Découvrir** (Discover) | diverger | Comprendre le problème sans présumer de sa nature ; parler aux personnes concernées ; **partir du problème, pas de la solution** |
| **Définir** (Define) | converger | Distiller l'information, identifier les chaînes de cause à effet, formuler le problème |
| **Développer** (Develop) | diverger | Imaginer plusieurs solutions, s'inspirer d'ailleurs, co-concevoir avec des profils variés |
| **Livrer** (Deliver) | converger | Tester à petite échelle, itérer, rejeter ce qui ne marche pas ou ne passe pas à l'échelle |

Le Design Council a publié en 2023 (20 ans du modèle) une représentation sous **licence
ouverte** [S6]. Le premier diamant traite *le bon problème*, le second *la bonne solution*.

## 3.3 Le processus du design centré utilisateur (ISO)

Voir chapitre 2, § 2.4 : spécifier le contexte → spécifier les exigences → concevoir →
évaluer, en boucle [S4].

## 3.4 Comparaison et choix

| Cadre | Force | Limite | Quand l'utiliser |
|---|---|---|---|
| Design thinking | Vocabulaire commun, idéation et empathie | Peut rester « dans la salle » sans livrer | Problème flou, équipe qui ne partage pas la même vision |
| Double Diamond | Distingue problème et solution | Schéma très général | Cadrer un projet, expliquer la démarche à un client |
| UCD (ISO 9241-210) | Évaluation à chaque étape | Plus formel | Produits régulés, projets longs |

## 3.5 Une boucle minimale pour une petite équipe (synthèse)

À partir de [S2], [S5], [S6] :

1. **Écrire le problème en une phrase** (« les techniciens doivent relever dix marquages sans
   reposer la main sur l'écran »). Si on ne peut pas, on n'a pas fini *Découvrir*.
2. **Observer 3 à 5 vraies personnes** faire la tâche aujourd'hui (voir chap. 4).
3. **Esquisser plusieurs solutions** sur papier avant d'ouvrir l'éditeur.
4. **Prototyper la plus prometteuse** à bas coût (papier, maquette cliquable).
5. **La tester avec 5 personnes** ; corriger ; **retester** (chap. 4, règle des 5 utilisateurs).
6. **Implémenter**, puis **retester la version réelle**, car « des problèmes subtils s'insinuent
   toujours à l'implémentation » [S2].
7. **Consigner les décisions** (pourquoi ceci, pas cela) — c'est la mémoire du projet.

---
*Sources du chapitre : S2, S4, S5, S6.*
