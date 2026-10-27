import { dayKey } from './dates';
import type { ProgressState } from './progress';
import { shuffle, type Rng } from './random';
import type { Exercise, ExerciseType } from './types';

/** "duel" = a choice between visuals; it is a mode, not a separate exercise type. */
export type PracticeType = ExerciseType | 'duel';

export type SessionSpec =
  | { kind: 'daily' }
  | { kind: 'module'; moduleId: string }
  | { kind: 'type'; type: PracticeType }
  | { kind: 'review' }
  | { kind: 'mistakes' }
  | { kind: 'exam' }
  | { kind: 'tag'; tag: string };

export const SESSION_SIZE: Record<SessionSpec['kind'], number> = {
  daily: 10,
  module: 12,
  type: 10,
  review: 20,
  mistakes: 20,
  exam: 30,
  tag: 12,
};

export function isDuel(ex: Exercise): boolean {
  return ex.type === 'choice' && ex.options.length >= 2 && ex.options.every((o) => o.visual);
}

/** "choice" here means the non-visual or 3+ option choices; visual 2-way choices are duels. */
export function matchesType(ex: Exercise, type: PracticeType): boolean {
  if (type === 'duel') return isDuel(ex);
  if (type === 'choice') return ex.type === 'choice' && !isDuel(ex);
  return ex.type === type;
}

/** 0 = wrong and due, 1 = due, 2 = never seen, 3 = scheduled later. */
function priority(ex: Exercise, state: ProgressState, today: string): number {
  const s = state.items[ex.id];
  if (!s) return 2;
  if (s.due <= today) return s.last === 'wrong' ? 0 : 1;
  return 3;
}

function byPriority(pool: Exercise[], state: ProgressState, today: string, rng: Rng): Exercise[] {
  const shuffled = shuffle(pool, rng);
  return shuffled
    .map((ex, i) => ({ ex, i, p: priority(ex, state, today), box: state.items[ex.id]?.box ?? 0 }))
    .sort((a, b) => a.p - b.p || a.box - b.box || a.i - b.i)
    .map((x) => x.ex);
}

/** Take items round-robin across modules so a session is not one topic in a row. */
function spreadAcrossModules(items: Exercise[], size: number): Exercise[] {
  const groups = new Map<string, Exercise[]>();
  for (const ex of items) {
    const key = ex.module ?? '';
    groups.set(key, [...(groups.get(key) ?? []), ex]);
  }
  const queues = [...groups.values()];
  const out: Exercise[] = [];
  while (out.length < size && queues.some((q) => q.length)) {
    for (const q of queues) {
      const next = q.shift();
      if (next) out.push(next);
      if (out.length >= size) break;
    }
  }
  return out;
}

/** Avoid two exercises of the same type back to back when the mix allows it. */
export function interleaveTypes(items: Exercise[]): Exercise[] {
  const rest = items.slice();
  const out: Exercise[] = [];
  while (rest.length) {
    const prev = out[out.length - 1];
    const idx = prev ? rest.findIndex((e) => e.type !== prev.type) : 0;
    out.push(...rest.splice(idx === -1 ? 0 : idx, 1));
  }
  return out;
}

export function buildSession(
  spec: SessionSpec,
  all: Exercise[],
  state: ProgressState,
  rng: Rng = Math.random,
  now: Date = new Date(),
): Exercise[] {
  const today = dayKey(now);
  const size = SESSION_SIZE[spec.kind];

  switch (spec.kind) {
    case 'module':
      return interleaveTypes(byPriority(all.filter((e) => e.module === spec.moduleId), state, today, rng).slice(0, size));
    case 'type':
      return byPriority(all.filter((e) => matchesType(e, spec.type)), state, today, rng).slice(0, size);
    case 'tag': {
      const pool = all.filter((e) => e.tags?.includes(spec.tag));
      return interleaveTypes(byPriority(pool, state, today, rng).slice(0, size));
    }
    case 'review': {
      const due = all.filter((e) => priority(e, state, today) <= 1);
      return interleaveTypes(byPriority(due, state, today, rng).slice(0, size));
    }
    case 'mistakes': {
      const missed = all.filter((e) => state.items[e.id]?.last === 'wrong');
      return interleaveTypes(shuffle(missed, rng).slice(0, size));
    }
    case 'exam': {
      // Stratified: one pass per module, mixed difficulties.
      return interleaveTypes(shuffle(spreadAcrossModules(shuffle(all, rng), size), rng));
    }
    case 'daily': {
      const due = byPriority(all.filter((e) => priority(e, state, today) <= 1), state, today, rng).slice(0, 4);
      const taken = new Set(due.map((e) => e.id));
      const read = new Set(Object.keys(state.lessons));
      const fresh = all.filter((e) => !taken.has(e.id) && !state.items[e.id]);
      // Prefer modules the learner has read, then the rest; easy items first for new learners.
      const freshSorted = shuffle(fresh, rng).sort(
        (a, b) => Number(read.has(b.module ?? '')) - Number(read.has(a.module ?? '')) || a.difficulty - b.difficulty,
      );
      const picked = [...due, ...spreadAcrossModules(freshSorted, size - due.length)];
      if (picked.length < size) {
        const ids = new Set(picked.map((e) => e.id));
        picked.push(...byPriority(all.filter((e) => !ids.has(e.id)), state, today, rng).slice(0, size - picked.length));
      }
      return interleaveTypes(picked);
    }
  }
}
