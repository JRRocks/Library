import type { Exercise, ScenarioCriterion, ScenarioExercise } from './types';

export type Verdict = 'correct' | 'partial' | 'wrong';

export interface GradeResult {
  verdict: Verdict;
  /** 0–1 */
  score: number;
}

export type Answer =
  | { type: 'choice'; optionId: string }
  | { type: 'multi'; optionIds: string[] }
  | { type: 'truefalse'; value: boolean }
  | { type: 'hotspot'; ids: string[] }
  | { type: 'order'; ids: string[] }
  /** leftId → pair id chosen on the right */
  | { type: 'match'; pairs: Record<string, string> }
  | { type: 'slider'; value: number }
  | { type: 'grid'; scores: Record<string, number> }
  /** Setting id → value set (a number for a `range`, a rank for a `ramp`). */
  | { type: 'scenario'; values: Record<string, number> };

const result = (score: number, partialFloor: number): GradeResult => ({
  score,
  verdict: score >= 1 ? 'correct' : score >= partialFloor ? 'partial' : 'wrong',
});

/** Precision-weighted set score: wrong picks cancel right ones. */
function setScore(picked: string[], expected: string[]): number {
  const want = new Set(expected);
  const got = new Set(picked);
  let hits = 0;
  let misses = 0;
  for (const id of got) (want.has(id) ? hits++ : misses++);
  if (hits === want.size && misses === 0) return 1;
  return Math.max(0, (hits - misses) / want.size);
}

/** Share of item pairs kept in the right relative order (1 = identical order). */
export function orderConcordance(given: string[], expected: string[]): number {
  const pos = new Map(given.map((id, i) => [id, i]));
  let good = 0;
  let total = 0;
  for (let i = 0; i < expected.length; i++) {
    for (let j = i + 1; j < expected.length; j++) {
      total++;
      if ((pos.get(expected[i]) ?? 0) < (pos.get(expected[j]) ?? 0)) good++;
    }
  }
  return total === 0 ? 1 : good / total;
}

/** WCAG 2.x relative luminance of an sRGB hex color. */
export function luminance(hex: string): number {
  const h = hex.replace('#', '');
  const full = h.length === 3 ? h.split('').map((c) => c + c).join('') : h;
  const channels = [0, 2, 4].map((i) => parseInt(full.slice(i, i + 2), 16) / 255);
  const [r, g, b] = channels.map((c) => (c <= 0.04045 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4));
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

export function contrastRatio(a: string, b: string): number {
  const [hi, lo] = [luminance(a), luminance(b)].sort((x, y) => y - x);
  return (hi + 0.05) / (lo + 0.05);
}

/** Hex of a neutral gray with the given HSL lightness (0–100). */
export function grayHex(lightness: number): string {
  const v = Math.round((Math.min(100, Math.max(0, lightness)) / 100) * 255);
  const hh = v.toString(16).padStart(2, '0');
  return `#${hh}${hh}${hh}`;
}

/* ---------------------------------------------------------------- scenario */

/** The values of a scenario before any change. */
export function initialValues(ex: ScenarioExercise): Record<string, number> {
  return Object.fromEntries(ex.controls.map((c) => [c.id, c.initial]));
}

/** The values of its expert version. */
export function idealValues(ex: ScenarioExercise): Record<string, number> {
  return Object.fromEntries(ex.controls.map((c) => [c.id, c.ideal]));
}

/** The value of a setting: the one set, or its initial value if it was not given. */
function valueOf(ex: ScenarioExercise, id: string, values: Record<string, number>): number {
  const control = ex.controls.find((c) => c.id === id);
  return values[id] ?? control?.initial ?? 0;
}

/** A color of a scenario: the shade picked on a `ramp` setting named by its id, or a hex color as is. */
export function scenarioColor(ex: ScenarioExercise, ref: string, values: Record<string, number>): string {
  const control = ex.controls.find((c) => c.id === ref);
  if (control?.kind !== 'ramp') return ref;
  const rank = Math.round(valueOf(ex, ref, values));
  return control.colors[Math.min(control.colors.length - 1, Math.max(0, rank))];
}

/**
 * What a criterion measures on the values set: a setting's value (`range`), a quotient (`ratio`) or
 * a contrast ratio (`contrast`).
 */
export function measure(ex: ScenarioExercise, c: ScenarioCriterion, values: Record<string, number>): number {
  const check = c.check;
  switch (check.kind) {
    case 'range':
      return valueOf(ex, check.control, values);
    case 'ratio': {
      const b = valueOf(ex, check.b, values);
      return b === 0 ? Infinity : valueOf(ex, check.a, values) / b;
    }
    case 'contrast':
      return contrastRatio(scenarioColor(ex, check.a, values), scenarioColor(ex, check.b, values));
  }
}

/**
 * Whether a criterion holds. A ratio is compared at its second decimal, as it is shown (1,5 holds
 * for « 1,5 au moins »), a contrast ratio as WCAG does, without rounding: 4,49:1 fails 4,5:1.
 */
export function criterionMet(ex: ScenarioExercise, c: ScenarioCriterion, values: Record<string, number>): boolean {
  const v = measure(ex, c, values);
  const check = c.check;
  if (check.kind === 'contrast') return v >= check.min;
  const x = check.kind === 'ratio' ? Math.round(v * 100) / 100 : v;
  return (check.min === undefined || x >= check.min) && (check.max === undefined || x <= check.max);
}

export function grade(ex: Exercise, answer: Answer): GradeResult {
  switch (ex.type) {
    case 'choice':
      return result(answer.type === 'choice' && answer.optionId === ex.answer ? 1 : 0, 1);
    case 'multi':
      return result(answer.type === 'multi' ? setScore(answer.optionIds, ex.answers) : 0, 0.5);
    case 'truefalse':
      return result(answer.type === 'truefalse' && answer.value === ex.answer ? 1 : 0, 1);
    case 'hotspot': {
      const expected = ex.hotspots.filter((h) => h.correct).map((h) => h.id);
      return result(answer.type === 'hotspot' ? setScore(answer.ids, expected) : 0, 0.5);
    }
    case 'order':
      return result(answer.type === 'order' ? orderConcordance(answer.ids, ex.items.map((i) => i.id)) : 0, 0.7);
    case 'match': {
      if (answer.type !== 'match') return result(0, 0.5);
      const good = ex.pairs.filter((p) => answer.pairs[p.id] === p.id).length;
      return result(good / ex.pairs.length, 0.5);
    }
    case 'slider': {
      if (answer.type !== 'slider') return result(0, 0.5);
      const [lo, hi] = ex.target;
      const v = answer.value;
      if (v >= lo && v <= hi) return result(1, 0.5);
      // Close misses (within half the target width, or 5% of the range) earn partial credit.
      const width = Math.max((hi - lo) / 2, (ex.max - ex.min) * 0.05);
      const dist = v < lo ? lo - v : v - hi;
      return result(dist <= width ? 0.5 : 0, 0.5);
    }
    case 'grid': {
      if (answer.type !== 'grid') return result(0, 0.5);
      const tol = ex.tolerance ?? 1;
      const agree = ex.criteria.filter((c) => Math.abs((answer.scores[c.id] ?? -99) - c.expert) <= tol).length;
      const share = agree / ex.criteria.length;
      return { score: share, verdict: share >= 0.8 ? 'correct' : share >= 0.5 ? 'partial' : 'wrong' };
    }
    case 'scenario': {
      // Every criterion counts as much as the others; all of them must hold for « Juste ».
      if (answer.type !== 'scenario') return result(0, 0.5);
      const met = ex.criteria.filter((c) => criterionMet(ex, c, answer.values)).length;
      return result(met / ex.criteria.length, 0.5);
    }
  }
}
