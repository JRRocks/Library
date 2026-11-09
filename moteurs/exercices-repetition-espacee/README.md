# Exercices corrigés et répétition espacée, en données, sans dépendance

| Champ | Valeur |
|---|---|
| **Identifiant** | `exercices-repetition-espacee` (= nom du dossier ; arrivé sous le nom `moteur-entrainement/`, à la racine de la branche `claude/charming-bell-vrm1id`) |
| **Statut** | 🟡 **Testé.** 17 tests automatisés, relancés verts le 2026-11-09. Aucun retour d'usage réel consigné. ⚠️ `srs.ts` : un correctif fait dans l'app n'est pas repris ici (voir « Points de vigilance ») |
| **Plateforme** | TypeScript 5.9, sans framework : navigateur, Node, application empaquetée |
| **Dépendances** | Aucune à l'exécution. Développement : `typescript` 5.9.3, `vitest` 5.0.3 |
| **Provenance** | [JRRocks/Learn-UI-UX](https://github.com/JRRocks/Learn-UI-UX) (public), dossier `src/engine/`. Versé ici par la session de l'app (commits de la Library `d5880ef`, `22bbb99` du 2026-10-27 et `2dd3016` du 2026-11-08). Comparé le 2026-11-09 à l'app au commit `939e441` (dernier commit touchant `src/engine/` : `ed72538`, 2026-11-08) : `dates.ts`, `grade.ts` et `random.ts` sont identiques ; `progress.ts`, `session.ts`, `srs.ts` et `types.ts` diffèrent ; `index.ts` n'existe qu'ici |
| **Utilisé dans** | Learn-UI-UX (« Épreuves »), l'app d'origine, qui garde sa propre copie dans `src/engine/` |
| **Fichiers d'origine** | `src/engine/dates.ts`, `grade.ts`, `progress.ts`, `random.ts`, `session.ts`, `srs.ts`, `types.ts`, `engine.test.ts` |
| **Dernière mise à jour de la fiche** | 2026-11-09 |

## À quoi ça sert

Faire tourner des leçons et des épreuves : des exercices décrits en données, corrigés avec
crédit partiel, reproposés au bon moment par répétition espacée, avec le suivi de la
progression. Le moteur ne fait que des calculs sur des données : l'interface (React, Vue,
natif) reste à l'application qui l'utilise, et il ne stocke rien.

À utiliser pour une application d'apprentissage, de révision ou de quiz. À ne pas utiliser
si l'on cherche des écrans tout faits : il n'en contient aucun.

## Comment ça marche

- **9 types d'exercices** décrits en données (`src/types.ts`) : `choice` (choix unique, texte
  ou visuel), `multi` (plusieurs bonnes réponses), `truefalse`, `hotspot` (cliquer sur les
  zones fautives d'une maquette), `order` (remettre dans l'ordre), `match` (associer),
  `slider` (calibrer une valeur avec une cible), `grid` (noter des critères et comparer à un
  expert), `scenario` (mise en situation : plusieurs réglages sur un écran réaliste, tailles,
  espacements, nuances d'une rampe de couleurs, évalués critère par critère).
- **Correction** (`grade.ts`) : verdict `correct` / `partial` / `wrong` et score de 0 à 1. Les
  mauvaises cases annulent les bonnes, l'ordre est noté par paires concordantes, un curseur
  proche de la cible obtient un crédit partiel, une mise en situation vaut la part de ses
  critères respectés (une valeur dans une plage, un contraste WCAG minimal entre deux
  couleurs, un rapport entre deux réglages). En plus : `contrastRatio()` WCAG, `measure()` et
  `criterionMet()` pour afficher l'évaluation critère par critère.
- **Répétition espacée** (`srs.ts`) : boîtes de Leitner pour les exercices et une variante de
  SM-2 pour les fiches (« À revoir », « Difficile », « Bien », « Facile »).
- **Progression** (`progress.ts`) : XP, jours d'activité, séries, maîtrise par lot
  d'exercices, éléments à réviser, meilleurs scores des défis chronométrés (`setRecord`),
  normalisation d'une sauvegarde importée.
- **Sessions** (`session.ts`) : session du jour (révisions dues et nouveautés réparties sur
  plusieurs modules), par module, par thème (plusieurs modules à tour de rôle), évaluation
  d'un thème (examen stratifié sur ses modules, corrigé à la fin : `isExam`), par type, par
  tag, révisions, erreurs, examen stratifié ; les types d'exercices sont alternés.
- **Utilitaires** : dates locales `YYYY-MM-DD` (`dates.ts`), mélange déterministe pour les
  tests (`random.ts`).

Cette copie est une version générique : ce qui est propre à l'app n'y figure pas (profils de
culture par marché, références de sources, résumés d'exercices), et les textes sont libres.

## Composants

| Fichier (`src/`) | Rôle | Dépend d'une plateforme ? | Tests | Statut |
|---|---|---|---|---|
| `types.ts` | Types des exercices, leçons, fiches et maquettes | Non | indirects | 🟡 |
| `grade.ts` | Correction et score, contraste WCAG | Non | `engine.test.ts` | 🟡 |
| `srs.ts` | Boîtes de Leitner, variante de SM-2 | Non | `engine.test.ts` | ⚠️ correctif de l'app non repris |
| `progress.ts` | XP, séries, maîtrise, records, sauvegarde importée | Non | `engine.test.ts` | 🟡 |
| `session.ts` | Constructeur de sessions | Non | `engine.test.ts` | 🟡 |
| `dates.ts`, `random.ts` | Dates locales, mélange déterministe | Non | `engine.test.ts` | 🟡 |
| `index.ts` | Point d'entrée | Non | Aucun | 🟡 |

## Intégration dans une nouvelle app

1. Dépendances : aucune. Pour les tests : `typescript` et `vitest`.
2. Configuration : aucune.
3. Copiez le dossier `src/` dans le projet (le fichier `engine.test.ts` avec lui).
4. Exemple d'appel minimal :

```ts
import {
  buildSession,
  emptyProgress,
  grade,
  recordAttempt,
  type Exercise,
} from './exercices-repetition-espacee/src';

const exercises: Exercise[] = [
  {
    id: 'typo-capitales',
    module: 'typo',
    type: 'truefalse',
    difficulty: 1,
    prompt: 'Un paragraphe en capitales se lit plus vite.',
    answer: false,
    explanation: 'On reconnaît les mots à leur silhouette, que les capitales effacent.',
    takeaway: 'Les capitales servent aux libellés courts.',
  },
];

let state = emptyProgress(); // à sauvegarder où l'on veut (localStorage, serveur)
const session = buildSession({ kind: 'daily' }, exercises, state);

const result = grade(session[0], { type: 'truefalse', value: false });
({ state } = recordAttempt(state, session[0], result)); // XP, boîte de Leitner, série
```

Le moteur ne stocke rien : l'application garde `state` (un objet JSON) et le persiste.

Lancer les tests depuis ce dossier :

```bash
npm install
npm test            # 17 tests : correction, dates, Leitner, SM-2, séries, sessions
npm run typecheck
```

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi |
|---|---|---|
| Intervalles des boîtes de Leitner | 0, 1, 3, 7, 16, 35, 90 jours | Valeurs de l'app d'origine. Aucun retour d'usage consigné : à tenir pour un point de départ |

## Points de vigilance et limites connues

- **Correctif de l'app non repris (`srs.ts`).** Ici, « Facile » calcule
  `Math.round(interval * 1.3) || 2` : à la première révision (intervalle de 1 jour), le
  résultat reste 1 jour, comme « Bien ». L'app a corrigé ce point
  (`Math.max(interval + 1, Math.round(interval * 1.3))`). À reprendre ici selon
  AGENTS.md § 5.B, étape 4.
- **Deux copies qui avancent séparément.** L'app garde son propre `src/engine/`, plus riche
  (profils de culture, `parseBackup`, historique des sessions) et suivi par 26 tests. Avant
  de réutiliser, comparez avec l'app.
- **Disposition différente du gabarit.** Les tests sont dans `src/engine.test.ts`, pas dans
  `tests/`, et le dossier porte un `package.json` pour les lancer sur place. Le paquet
  s'appelle encore `moteur-entrainement`.
- **Typage non revérifié le 2026-11-09.** `npm install` a échoué dans la session (erreur
  interne de npm 10.9.9) ; les tests ont été lancés par `npx vitest` (version 5.1.0, pas la
  5.0.3 figée).
- Jamais revu par une session de la Library avant le 2026-11-09 : la fiche reprend le
  descriptif écrit par la session de l'app.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-10-27 | Session Learn-UI-UX (`d5880ef`, `22bbb99`) | Moteur extrait de l'app et versé dans la Library, puis records des défis chronométrés (`setRecord`) | Création, hors gabarit |
| 2026-11-08 | Session Learn-UI-UX (`2dd3016`) | Mises en situation évaluées par critères, sessions par thème | Descriptif complété |
| 2026-11-09 | Fusion de la branche dans la Library | Dossier rangé sous `moteurs/`, fiche mise au gabarit. 17 tests relancés verts (`npx vitest run`). Comparaison avec l'app au commit `939e441` : un correctif de `srs.ts` manque ici | Statut 🟡, `srs.ts` ⚠️ |
