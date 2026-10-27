import { addDays } from './dates';
import type { Verdict } from './grade';

/** Leitner intervals in days, indexed by box. Box 0 means "due again today". */
export const BOX_INTERVALS = [0, 1, 3, 7, 16, 35, 90] as const;
export const MAX_BOX = BOX_INTERVALS.length - 1;

export interface ScheduleState {
  box: number;
  due: string;
}

/** Exercises: a right answer moves the item up a box, a wrong one sends it back to box 0. */
export function scheduleExercise(prev: ScheduleState | undefined, verdict: Verdict, today: string): ScheduleState {
  const box = prev?.box ?? 0;
  const next = verdict === 'correct' ? Math.min(box + 1, MAX_BOX) : verdict === 'partial' ? Math.max(1, Math.min(box, 2)) : 0;
  return { box: next, due: addDays(today, BOX_INTERVALS[next]) };
}

/** Self-assessment on a flashcard. */
export type CardGrade = 0 | 1 | 2 | 3;

export const CARD_GRADES: { grade: CardGrade; label: string; hint: string }[] = [
  { grade: 0, label: 'À revoir', hint: 'Je ne savais pas' },
  { grade: 1, label: 'Difficile', hint: 'Retrouvé avec effort' },
  { grade: 2, label: 'Bien', hint: 'Retrouvé sans aide' },
  { grade: 3, label: 'Facile', hint: 'Évident' },
];

export interface CardState {
  ease: number;
  interval: number;
  reps: number;
  due: string;
}

/** A compact SM-2 variant. */
export function scheduleCard(prev: CardState | undefined, g: CardGrade, today: string): CardState {
  let ease = prev?.ease ?? 2.5;
  let reps = prev?.reps ?? 0;
  let interval = prev?.interval ?? 0;

  if (g === 0) {
    reps = 0;
    interval = 0;
    ease = Math.max(1.3, ease - 0.2);
  } else if (g === 1) {
    interval = Math.max(1, Math.round(interval * 1.2));
    ease = Math.max(1.3, ease - 0.15);
    reps += 1;
  } else {
    interval = reps === 0 ? 1 : reps === 1 ? 3 : Math.round(interval * ease);
    if (g === 3) {
      interval = Math.round(interval * 1.3) || 2;
      ease += 0.15;
    }
    reps += 1;
  }
  return { ease: Math.round(ease * 100) / 100, interval, reps, due: addDays(today, interval) };
}
