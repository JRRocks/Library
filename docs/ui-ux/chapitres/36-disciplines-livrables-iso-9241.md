# Chapitre 36 — Disciplines, rôles, livrables et normes (ISO 9241)

## 36.1 Les composantes de l'UX [S180]

L'UX design « définit l'expérience qu'un utilisateur vivra en interagissant avec une entreprise, ses
services et ses produits » ; la **recherche, l'analyse de données et les tests** pilotent les décisions
**plutôt que les préférences esthétiques** et les opinions. Contrairement au design d'interface, il couvre
**tous les aspects de l'expérience perçue** : usabilité, utilité, désirabilité, perception de la marque,
performance globale. Il s'enracine dans les **facteurs humains et l'ergonomie** (depuis la fin des années
1940) ; le terme « expérience utilisateur » a été forgé par **Don Norman** (« j'ai inventé le terme parce
que *interface homme-machine* et *usabilité* étaient trop étroits […] il commence à perdre son sens »).

| Discipline | Rôle |
|---|---|
| **Recherche utilisateur** | Étudier systématiquement utilisateurs et besoins : entretiens, sondages, études de terrain, enquête contextuelle ; réduit le risque de produits qui échouent |
| **Design visuel** | Esthétique et « look and feel » : couleurs, images, symboles ; s'appuie sur la Gestalt et la perception visuelle |
| **Architecture de l'information** | « Art et science de structurer et organiser l'information pour soutenir l'usabilité et la **trouvabilité** » ; métadonnées, pages et navigation |
| **Design d'interaction (IxD)** | Spécifier **comment le produit se comporte** ; produire une expérience efficace et agréable ; communique **la priorité de l'information, le flux et la fonctionnalité** |
| **Usabilité** | Mesure dans laquelle des utilisateurs spécifiés atteignent des objectifs spécifiés avec **efficacité, efficience et satisfaction** dans un contexte donné ; **sous-ensemble de l'UX** : « la bonne usabilité est essentielle à une bonne UX mais ne la garantit pas à elle seule » |
| **Accessibilité** | Facilité d'accès, d'usage et de compréhension ; WCAG et POUR (chap. 14) |
| **IHM** | Discipline scientifique sous-jacente (chap. 28) |

### Déroulement type [S180]
1. **Recherche** → **modélisation** (personas, modèles de flux, d'artefacts) ;
2. **Cadre d'interaction** (wireframes : structure, flux, comportement) en parallèle du **cadre visuel**
   (attributs d'expérience, langage et style visuel) ;
3. **Maquettes pleine résolution** ; collaboration étroite avec l'équipe de programmation ;
4. **Après le lancement** : tests d'usabilité, tests A/B, architecture de l'information (plans de site,
   flux), nouveaux wireframes.

### Livrables [S180]
Wireframes, prototypes, diagrammes de flux utilisateur, spécifications et documents techniques,
maquettes, présentations, personas, profils, vidéos, et dans une moindre mesure rapports. Quatre
éléments majeurs d'une **spécification UX** : **page de titre, introduction de la fonction, wireframes,
historique des versions** ; selon les cas : modèles de flux, personas, user stories, scénarios, recherche
antérieure. **Documenter les décisions** sous forme de wireframes **annotés** donne au développeur
l'information nécessaire.

### Rôles [S180]
Chercheur UX, architecte de l'information, designer d'interaction, ingénieur facteurs humains, analyste,
consultant, directeur de la création, spécialiste de l'usabilité ; **communicants techniques** et
**UX writers** (rédacteurs UX : maintiennent le texte d'interface, mènent des recherches pour tester
l'usabilité).

### « Pixel perfect »
Terme du développement web : le produit apparaît **exactement comme prévu** par le designer [S180].

## 36.2 Le design d'interface (UI) [S179]

« La conception d'interfaces pour machines et logiciels, dans le but de maximiser **l'usabilité et
l'expérience utilisateur**. » Elle porte surtout sur l'**architecture de l'information** : construire des
interfaces qui communiquent clairement ce qui est important. Trois types : **GUI**, interfaces
**vocales**, interfaces **gestuelles** (3D, VR). « Une bonne interface facilite l'accomplissement de la
tâche **sans attirer l'attention inutilement sur elle-même** » ; les choix esthétiques peuvent aider
**ou nuire** à l'usage. L'UI fixe le **ton visuel** de l'expérience ; l'UX est le **processus entier**.

### EDIPT (Kelley, 2004) [S179]
Cadre de design thinking de David M. Kelley (d.school de Stanford) : **E**mpathize, **D**efine,
**I**deate, **P**rototype, **T**est — **non linéaire**.
- **Empathiser** : recherche (entretiens semi-structurés) ; questions : que veut l'utilisateur que le
  système fasse ? comment s'intègre-t-il à son flux de travail ? quel est son niveau technique ? quels
  systèmes connaît-il ? quels styles lui plaisent ?
- **Définir** : un énoncé de problème **d'une phrase** : l'utilisateur, son besoin spécifique, son objectif.
- **Idéer**, **prototyper** (basse, moyenne, haute fidélité), **tester**.

### Inspection et test d'usabilité [S179]
Inspection (moins chère, utile tôt, sur prototypes) : **cognitive walkthrough**, **évaluation
heuristique**, **walkthrough pluraliste** ; test sur utilisateurs avec **protocole think-aloud**.

## 36.3 Les normes ISO 9241 [S179]

### Partie 11 — l'usabilité
**Efficacité** (atteindre les objectifs), **efficience** (ressources dépensées), **satisfaction**
(acceptabilité).

### Partie 110 (ex-10) — les sept principes de dialogue (« le *feel* »)
| Principe | Définition |
|---|---|
| **Adéquation à la tâche** | Le dialogue soutient l'accomplissement effectif et efficient de la tâche |
| **Auto-descriptivité** | Chaque étape est immédiatement compréhensible (retour du système) ou expliquée à la demande |
| **Contrôlabilité** | L'utilisateur initie et contrôle la direction et le rythme |
| **Conformité aux attentes** | Cohérent, adapté à ses connaissances et aux conventions |
| **Tolérance aux erreurs** | Malgré des erreurs de saisie évidentes, le résultat voulu est atteint avec peu ou pas d'action |
| **Individualisation** | Le logiciel peut être adapté aux besoins, préférences, compétences |
| **Aptitude à l'apprentissage** | Le dialogue soutient et guide l'apprentissage |

### Partie 12 — la présentation de l'information (le *look*) : sept attributs
**Clarté** (contenu transmis vite et exactement), **discriminabilité**, **concision** (pas de surcharge),
**cohérence**, **détectabilité** (l'attention est dirigée vers l'information requise), **lisibilité**
(legibility), **compréhensibilité**.

### Partie 13 — le guidage de l'utilisateur : cinq moyens
**Invites** (prompts), **retour d'information** (opportun, perceptible, non intrusif), **informations
d'état**, **gestion des erreurs** (prévention, correction, support, messages), **aide en ligne**
(contextuelle).

### Modèle IFIP : quatre dimensions
Entrées/sorties (le *look*), dialogue (le *feel*), technique/fonctionnelle (accès aux outils), organisationnelle
(communication et coopération).

## 36.4 Correspondance avec les heuristiques

| ISO 9241-110 | Nielsen | Shneiderman |
|---|---|---|
| Auto-descriptivité | 1 Visibilité de l'état ; 10 Aide | 3 Retour informatif |
| Contrôlabilité | 3 Contrôle et liberté | 7 Lieu de contrôle interne ; 6 Annulation |
| Conformité aux attentes | 4 Cohérence ; 2 Monde réel | 1 Cohérence |
| Tolérance aux erreurs | 5 Prévention ; 9 Récupération | 5 Gestion simple |
| Individualisation | 7 Flexibilité | 2 Raccourcis |
| Aptitude à l'apprentissage | 6 Reconnaissance ; 10 Aide | 8 Mémoire |
| Adéquation à la tâche | 8 Minimalisme | 4 Clôture |

## 36.5 À retenir pour une petite équipe
- Une personne peut tenir **tous** les rôles ; il faut **distinguer les casquettes** (chercheur, designer
  d'interaction, designer visuel, rédacteur, testeur) pour ne rien oublier.
- Les **livrables minimaux** : énoncé de problème en une phrase, wireframes annotés, liste des états,
  historique des décisions (qui répond à « quatre éléments d'une spécification »).
- S'appuyer sur **ISO 9241** comme **grille d'audit supplémentaire**.

---
*Sources du chapitre : S2, S5, S17, S20, S178–S180.*
