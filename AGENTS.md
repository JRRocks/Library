# Guide des agents : faire vivre la Library

> **Tout agent (IA ou humain) qui passe par ce dépôt lit ce fichier en entier avant d'agir.**
> Pour chercher un moteur, ouvrez ensuite [`INDEX.md`](INDEX.md).

---

## 1. À quoi sert ce dépôt

**JRRocks/Library** est la **bibliothèque ouverte des moteurs déjà construits** dans les
autres applications de JRRocks. On y trouve des briques de code :

- **déjà éprouvées** (testées en conditions réelles, corrigées après des retours terrain) ;
- **réutilisables** dans une autre app sans tout réécrire ;
- **documentées** : ce qu'elles font, comment les intégrer, les pièges déjà rencontrés.

L'exemple fondateur : le moteur de scan de ScanList. L'OCR était imprécis, des tests en réel
l'ont montré, et le problème a été corrigé par un **consensus de N lectures identiques
consécutives**. Ce savoir (le code, mais surtout le *pourquoi* et les réglages) est rangé
ici pour que la prochaine app qui a besoin de scanner ne refasse pas le même chemin.

**La Library n'est pas figée.** Chaque utilisation d'un moteur est une occasion de
l'améliorer. Votre rôle, en tant qu'agent de passage, est de la **faire évoluer** :

| Situation | Ce que vous faites | Procédure |
|---|---|---|
| On a besoin d'une brique dans une app | Chercher dans l'INDEX avant d'écrire du code | § 5.A |
| Un moteur a bien fonctionné dans une app | **Mettre à jour sa fiche** (retour positif, statut, réglages) | § 5.B |
| Un moteur a un défaut | **Le patcher** ici et le signaler aux apps concernées | § 5.C |
| Une app contient un moteur éprouvé absent d'ici | **L'ajouter** | § 5.D |
| On passe en revue un dépôt | Noter les candidats dans l'INDEX | § 5.E |

---

## 2. Règles absolues

1. **Ce dépôt est PUBLIC.** On n'y met **jamais** de secret (clé d'API, jeton, mot de passe,
   keystore, empreinte SHA-1 de signature, clé Affiliate…), ni de **donnée personnelle**
   (noms, e-mails, téléphones, données clients), ni de fichier de données métier réel. Les
   sources viennent souvent de dépôts **privés** : relisez chaque fichier copié et
   remplacez toute valeur sensible par un gabarit `<NOM_DE_LA_VALEUR>`. En cas de doute, ne
   copiez pas : décrivez, et renvoyez au dépôt source.
2. **Statut honnête.** On ne promeut jamais un moteur sans preuve (§ 3). « Ça compile » ou
   « les tests JVM passent » ne vaut pas « éprouvé ». Ce qui n'a jamais tourné sur un vrai
   appareil le dit clairement.
3. **Provenance obligatoire.** Chaque fiche cite le dépôt, le commit et les fichiers
   d'origine. Sans provenance, le code est inutilisable pour une mise à jour future.
4. **On n'efface pas l'histoire.** Les tableaux « Historique des retours d'usage » et
   `JOURNAL.md` ne se réécrivent pas : **on ajoute une ligne**. Une erreur se corrige par une
   nouvelle ligne.
5. **Tout changement de fiche met à jour l'INDEX et le JOURNAL**, dans le même commit.
6. **Le code de `src/` reste fidèle à une version réellement utilisée.** Si vous le modifiez
   (patch, adaptation générique), dites-le dans la fiche (historique) et dans le JOURNAL, et
   indiquez si l'app source a reçu le même changement.
7. **Français** pour toute la documentation. Les identifiants de code gardent leur langue
   d'origine.

---

## 3. Statuts et cycle de vie

| Statut | Sens | Critère pour l'attribuer |
|---|---|---|
| 🔬 **Candidat** | Repéré dans un dépôt, pas encore extrait ni fiché | Il apparaît seulement dans la section « Candidats » de l'INDEX |
| 🟡 **Testé** | Extrait et documenté, couvert par des tests automatisés, **pas encore validé en conditions réelles** (ou seulement en partie) | Fiche complète + tests présents |
| ✅ **Éprouvé** | Utilisé en conditions réelles (terrain, production, appareil réel) **avec succès** | Un retour d'usage réel positif consigné dans l'historique de la fiche |
| ⚠️ **À patcher** | Défaut connu ou fortement suspecté, pas encore corrigé | Le défaut est décrit dans « Points de vigilance », avec un patch proposé si possible |
| ⛔ **Obsolète** | Remplacé par un autre moteur, ou API morte | La fiche renvoie vers le remplaçant |

```
🔬 Candidat ──(extraction + fiche + tests)──► 🟡 Testé ──(retour réel positif)──► ✅ Éprouvé
                                                 ▲                                  │
                                                 └──── patch validé ◄── ⚠️ À patcher ◄┘ (défaut constaté)
```

Un moteur peut avoir des **composants de statuts différents** (ex. le scan : OCR ✅, `RegexSuggester` 🟡, NFC ⚠️).
Le tableau « Composants » de la fiche porte alors le détail, et le statut global le résume.

---

## 4. Structure du dépôt

```
README.md               Présentation courte, renvoie ici et vers l'INDEX
AGENTS.md               Ce guide (règles et procédures)
CLAUDE.md               Point d'entrée Claude Code, renvoie ici
INDEX.md                Index de recherche : moteurs, besoins, mots-clés, candidats, dépôts revus
JOURNAL.md              Journal des évolutions de la Library (le plus récent en haut)
docs/
  ui-ux/                Dossier de référence UI/UX (sources, chapitres, check-lists)
  methodes/             Fiches méthode : outillage éprouvé, sans code d'exécution (captures, build en session cloud…)
moteurs/
  _modele/README.md     Gabarit de fiche, à copier pour tout nouveau moteur
  <identifiant>/
    README.md           LA FICHE : statut, provenance, intégration, pièges, historique
    src/                Code de référence copié depuis l'app source (packages d'origine)
    tests/              Tests d'origine, à reprendre avec le code
```

- `identifiant` : kebab-case, décrit la fonction, pas l'app (`paiement-sumup`, pas `popote-sumup`).
- Le code de `src/` garde ses **packages d'origine** : on les renomme à l'intégration, pas ici.
  C'est ce qui permet de comparer facilement avec le dépôt source.
- La Library ne se compile pas : c'est un **dépôt de référence**, pas une bibliothèque Gradle.

---

## 5. Procédures

### 5.A Utiliser un moteur dans une app

1. Ouvrez [`INDEX.md`](INDEX.md), section « Je cherche à… » ou « Mots-clés ».
2. Lisez la fiche **en entier**, surtout « Points de vigilance » : c'est là que se trouvent
   les heures déjà perdues par d'autres.
3. Copiez `src/` et `tests/` dans l'app, renommez les packages, suivez « Intégration ».
4. Faites passer les tests repris **dans l'app cible**.
5. Ajoutez l'app à la ligne **« Utilisé dans »** de la fiche (procédure 5.B pour le commit).
6. Après les essais réels, revenez ici avec le retour : 5.B s'il est positif, 5.C s'il est négatif.

### 5.B Retour positif : mettre à jour la fiche

À faire **dès qu'un moteur a fonctionné en réel** dans une app, y compris son app d'origine.

1. Ajoutez une ligne à « Historique des retours d'usage » : date, app, ce qui a été vérifié
   et sur quel matériel ou dans quel contexte.
2. **Promotion de statut** si le critère du § 3 est rempli (ex. 🟡 → ✅). Mettez aussi à jour
   les statuts du tableau « Composants ».
3. Si de **nouveaux réglages** ont été validés (seuil, délai, résolution…), mettez à jour
   « Réglages éprouvés » avec leur raison.
4. Si l'app a amélioré le code, **resynchronisez `src/`** depuis le commit de l'app, puis
   mettez à jour « Provenance » (nouveau commit) et « Dernière mise à jour ».
5. Mettez à jour la ligne du moteur dans l'INDEX (statut, apps), puis ajoutez une entrée
   en haut de `JOURNAL.md`.
6. Commit : `[<identifiant>] Retour positif <app> : <résumé>`.

### 5.C Défaut constaté : patcher

1. **Décrivez le défaut** dans « Points de vigilance » : symptôme, conditions, cause si elle
   est connue. Passez le statut (ou celui du composant) à ⚠️ et mettez à jour l'INDEX.
2. **Reproduisez-le par un test** dans `tests/` quand c'est possible : le test doit échouer
   avant le correctif.
3. **Corrigez dans `src/`**, au plus petit périmètre possible. Le test doit passer.
   - Si le correctif est validé en réel : statut restauré (✅ ou 🟡 selon la preuve).
   - S'il ne peut pas être vérifié ici (matériel, compte, SDK natif) : laissez ⚠️ et
     écrivez « patch proposé, non vérifié sur appareil ».
4. **Propagez** : listez dans l'historique les apps de « Utilisé dans » qui portent encore
   le défaut. Si vous avez accès à leur dépôt et que l'utilisateur le demande, appliquez-y
   le même patch ; sinon, signalez-le dans votre compte rendu.
5. Ajoutez une ligne à l'historique (cause, correctif, état de la vérification) et une
   entrée au JOURNAL.
6. Commit : `[<identifiant>] Patch : <résumé>`.

Un défaut découvert **dans une app** qui utilise le moteur se corrige **aussi ici**. Sinon,
la prochaine app héritera du bug.

### 5.D Ajouter un moteur

**Critères d'entrée** (tous requis) :
- il résout un problème qui reviendra dans d'autres apps ;
- il est **isolable** : son cœur peut sortir de l'app sans tirer toute sa logique métier ;
- il a des tests, ou au moins un usage réel documenté ;
- il peut être publié **sans secret ni donnée personnelle**.

**Étapes :**
1. Copiez `moteurs/_modele/` vers `moteurs/<identifiant>/`.
2. Copiez dans `src/` les fichiers du moteur, et **seulement eux**, puis dans `tests/` leurs
   tests. Passez en revue chaque fichier selon la règle 1 (`grep -niE 'key|token|secret|password|@.*\.(com|fr)'`).
3. Remplissez **toutes** les sections de la fiche. Les plus précieuses sont « Comment ça
   marche » (le *pourquoi*), « Points de vigilance » (les pièges) et l'historique des retours
   (d'où vient la confiance). Cherchez-les dans le dépôt source : `CLAUDE.md`,
   `docs/decisions.md`, `consignes/`, `JOURNAL.md`, messages de commit, issues, PR.
4. Choisissez le statut selon le § 3, **preuve à l'appui**.
5. Ajoutez le moteur dans **toutes** les sections concernées de l'INDEX (tableau, besoins,
   mots-clés, plateforme). Retirez-le des « Candidats » s'il y figurait.
6. Ajoutez une entrée au JOURNAL. Commit : `[<identifiant>] Ajout du moteur <nom>`.

### 5.E Passer en revue un dépôt

1. Clonez-le, puis lisez son `README`, son `CLAUDE.md` et ses docs de décisions ou son journal.
   Pièges vus lors des revues précédentes :
   - revoyez la **branche active**, pas seulement `main` (`git ls-remote --heads origin`) : plusieurs
     apps se développent sur une branche jamais fusionnée ;
   - clonez **sans `--depth`** (ou `git fetch --unshallow`) : un clone superficiel fait croire à des
     branches orphelines ou à de fausses divergences ;
   - juste avant de consigner, revérifiez la pointe distante : elle a pu avancer pendant la revue ;
   - avant d'écrire ici, récupérez les branches de la Library (`git fetch origin`) : une autre
     session a pu la mettre à jour en parallèle. Fusionnez-les d'abord.
2. Repérez les briques qui remplissent les critères du 5.D.
3. Pour chacune : soit vous l'ajoutez (5.D), soit vous la notez dans « Candidats » de
   l'INDEX avec son chemin, son intérêt et ce qui manque pour la promouvoir.
4. Mettez à jour le tableau « Dépôts passés en revue » de l'INDEX (date, commit).

---

## 6. Synchronisation avec les apps sources

- La Library et l'app source peuvent diverger. La **fiche fait foi** pour savoir quelle
  version est en bibliothèque : c'est le commit de « Provenance ».
- Quand une app source corrige ou améliore un moteur catalogué, resynchronisez (5.B, étape 4).
- Quand la Library corrige un moteur (5.C), notez quelles apps n'ont pas encore le patch.

---

## 7. Check-list avant chaque commit

- [ ] Aucun secret ni donnée personnelle dans les fichiers ajoutés (règle 1).
- [ ] Fiche complète, statut justifié, provenance à jour, date de mise à jour.
- [ ] Ligne ajoutée à l'historique de la fiche (pas de réécriture).
- [ ] `INDEX.md` à jour (tableau principal, besoins, mots-clés, candidats, dépôts revus).
- [ ] Entrée en haut de `JOURNAL.md`.
- [ ] Message de commit préfixé `[<identifiant>]` (ou `[library]` pour un changement transverse).
