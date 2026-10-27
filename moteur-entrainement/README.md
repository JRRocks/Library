# Moteur d’entraînement

Un moteur d’exercices et de répétition espacée en TypeScript, **sans dépendance et sans
framework** : il ne fait que des calculs sur des données. L’interface (React, Vue, natif…)
reste à l’application qui l’utilise.

Extrait de l’application [Learn-UI-UX](https://github.com/JRRocks/Learn-UI-UX)
(« Épreuves »), où il fait tourner les leçons et les épreuves d’UI/UX.

## Ce qu’il fait

- **8 types d’exercices** décrits en données (`src/types.ts`) :
  `choice` (choix unique, texte ou visuel), `multi` (plusieurs bonnes réponses),
  `truefalse`, `hotspot` (cliquer sur les zones fautives d’une maquette), `order` (remettre
  dans l’ordre), `match` (associer), `slider` (calibrer une valeur avec une cible), `grid`
  (noter des critères et comparer à un expert).
- **Correction** (`grade.ts`) : verdict `correct` / `partial` / `wrong` et score de 0 à 1 —
  les mauvaises cases annulent les bonnes, l’ordre est noté par paires concordantes, un
  curseur proche de la cible obtient un crédit partiel. Bonus : `contrastRatio()` WCAG.
- **Répétition espacée** (`srs.ts`) : boîtes de Leitner pour les exercices (0, 1, 3, 7, 16,
  35, 90 jours) et une variante de SM-2 pour les fiches (« À revoir », « Difficile »,
  « Bien », « Facile »).
- **Progression** (`progress.ts`) : XP, jours d’activité, séries, maîtrise par lot
  d’exercices, éléments à réviser, meilleurs scores des défis chronométrés (`setRecord`),
  normalisation d’une sauvegarde importée.
- **Sessions** (`session.ts`) : session du jour (révisions dues + nouveautés réparties sur
  plusieurs modules), par module, par type, par tag, révisions, erreurs, examen stratifié ;
  les types d’exercices sont alternés.
- **Utilitaires** : dates locales `YYYY-MM-DD` (`dates.ts`), mélange déterministe pour les
  tests (`random.ts`).

## Utilisation

Copie le dossier `src/` dans ton projet, puis :

```ts
import {
  buildSession,
  emptyProgress,
  grade,
  recordAttempt,
  type Exercise,
} from './moteur-entrainement/src';

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

let state = emptyProgress(); // à sauvegarder où tu veux (localStorage, serveur…)
const session = buildSession({ kind: 'daily' }, exercises, state);

const result = grade(session[0], { type: 'truefalse', value: false });
({ state } = recordAttempt(state, session[0], result)); // XP, boîte de Leitner, série
```

Le moteur ne stocke rien : l’application garde `state` (un objet JSON) et le persiste.

## Tests

```bash
npm install
npm test        # 14 tests : correction, dates, Leitner, SM-2, séries, sessions
npm run typecheck
```
