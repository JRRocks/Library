import { addDays, dayKey, daysBetween } from './dates';
import type { GradeResult, Verdict } from './grade';
import { scheduleCard, scheduleExercise, type CardGrade, type CardState } from './srs';
import type { Exercise } from './types';

export interface ItemState {
  seen: number;
  correct: number;
  last?: Verdict;
  lastSeen?: string;
  box: number;
  due: string;
}

export interface SessionRecord {
  id: string;
  /** ISO date-time */
  at: string;
  kind: string;
  label: string;
  total: number;
  correct: number;
  partial: number;
  xp: number;
  /** Exercise ids answered wrong, for the "mes erreurs" list. */
  missed: string[];
}

export interface Settings {
  theme: 'system' | 'light' | 'dark';
  /** Daily XP goal. */
  dailyGoal: number;
}

export interface DayStats {
  xp: number;
  answered: number;
  correct: number;
}

export interface ProgressState {
  version: 1;
  xp: number;
  days: Record<string, DayStats>;
  items: Record<string, ItemState>;
  cards: Record<string, CardState>;
  lessons: Record<string, string>;
  badges: Record<string, string>;
  sessions: SessionRecord[];
  settings: Settings;
  /** Best scores of timed challenges, by challenge id. */
  records: Record<string, number>;
}

export const XP = {
  correct: 10,
  partial: 4,
  wrong: 1,
  card: 2,
  lesson: 15,
} as const;

export function emptyProgress(): ProgressState {
  return {
    version: 1,
    xp: 0,
    days: {},
    items: {},
    cards: {},
    lessons: {},
    badges: {},
    sessions: [],
    settings: { theme: 'system', dailyGoal: 60 },
    records: {},
  };
}

/** Accept anything parsed from storage or an imported file; fill gaps, drop garbage. */
export function normalizeProgress(raw: unknown): ProgressState {
  const base = emptyProgress();
  if (!raw || typeof raw !== 'object') return base;
  const r = raw as Partial<ProgressState>;
  const obj = <T>(v: unknown, fallback: T): T => (v && typeof v === 'object' && !Array.isArray(v) ? (v as T) : fallback);
  return {
    version: 1,
    xp: typeof r.xp === 'number' && Number.isFinite(r.xp) ? r.xp : 0,
    days: obj(r.days, base.days),
    items: obj(r.items, base.items),
    cards: obj(r.cards, base.cards),
    lessons: obj(r.lessons, base.lessons),
    badges: obj(r.badges, base.badges),
    sessions: Array.isArray(r.sessions) ? r.sessions.slice(-60) : [],
    settings: { ...base.settings, ...obj(r.settings, {}) },
    records: obj(r.records, base.records),
  };
}

export function xpFor(ex: Exercise, verdict: Verdict): number {
  if (verdict === 'correct') return XP.correct * ex.difficulty;
  if (verdict === 'partial') return XP.partial * ex.difficulty;
  return XP.wrong;
}

function bumpDay(days: Record<string, DayStats>, today: string, xp: number, answered: number, correct: number) {
  const d = days[today] ?? { xp: 0, answered: 0, correct: 0 };
  return { ...days, [today]: { xp: d.xp + xp, answered: d.answered + answered, correct: d.correct + correct } };
}

export function recordAttempt(
  state: ProgressState,
  ex: Exercise,
  res: GradeResult,
  now: Date = new Date(),
): { state: ProgressState; xp: number } {
  const today = dayKey(now);
  const prev = state.items[ex.id];
  const sched = scheduleExercise(prev, res.verdict, today);
  const item: ItemState = {
    seen: (prev?.seen ?? 0) + 1,
    correct: (prev?.correct ?? 0) + (res.verdict === 'correct' ? 1 : 0),
    last: res.verdict,
    lastSeen: today,
    ...sched,
  };
  const xp = xpFor(ex, res.verdict);
  return {
    xp,
    state: {
      ...state,
      xp: state.xp + xp,
      items: { ...state.items, [ex.id]: item },
      days: bumpDay(state.days, today, xp, 1, res.verdict === 'correct' ? 1 : 0),
    },
  };
}

export function recordCard(state: ProgressState, key: string, g: CardGrade, now: Date = new Date()): ProgressState {
  const today = dayKey(now);
  return {
    ...state,
    xp: state.xp + XP.card,
    cards: { ...state.cards, [key]: scheduleCard(state.cards[key], g, today) },
    days: bumpDay(state.days, today, XP.card, 0, 0),
  };
}

export function markLessonRead(state: ProgressState, moduleId: string, now: Date = new Date()): ProgressState {
  if (state.lessons[moduleId]) return state;
  const today = dayKey(now);
  return {
    ...state,
    xp: state.xp + XP.lesson,
    lessons: { ...state.lessons, [moduleId]: today },
    days: bumpDay(state.days, today, XP.lesson, 0, 0),
  };
}

export function addSession(state: ProgressState, rec: SessionRecord): ProgressState {
  return { ...state, sessions: [...state.sessions, rec].slice(-60) };
}

/** Keep the best score of a timed challenge (`blitz`…); unchanged state when not beaten. */
export function setRecord(state: ProgressState, id: string, score: number): ProgressState {
  if ((state.records[id] ?? 0) >= score) return state;
  return { ...state, records: { ...state.records, [id]: score } };
}

/** Consecutive active days ending today, or yesterday when today has no activity yet. */
export function streak(state: ProgressState, now: Date = new Date()): { current: number; best: number; activeToday: boolean } {
  const active = Object.keys(state.days)
    .filter((k) => (state.days[k]?.xp ?? 0) > 0)
    .sort();
  const today = dayKey(now);
  const set = new Set(active);
  const activeToday = set.has(today);
  let current = 0;
  let cursor = activeToday ? today : addDays(today, -1);
  while (set.has(cursor)) {
    current++;
    cursor = addDays(cursor, -1);
  }
  let best = 0;
  let run = 0;
  let prev: string | null = null;
  for (const k of active) {
    run = prev && daysBetween(prev, k) === 1 ? run + 1 : 1;
    best = Math.max(best, run);
    prev = k;
  }
  return { current, best: Math.max(best, current), activeToday };
}

/** 0–1 mastery of one exercise, from its Leitner box and last verdict. */
export function itemMastery(s: ItemState | undefined): number {
  if (!s) return 0;
  if (s.last === 'wrong') return s.correct > 0 ? 0.3 : 0.1;
  if (s.last === 'partial') return 0.4;
  return Math.min(1, 0.4 + s.box * 0.2);
}

export function mastery(state: ProgressState, ids: string[]): number {
  if (ids.length === 0) return 0;
  return ids.reduce((sum, id) => sum + itemMastery(state.items[id]), 0) / ids.length;
}

export function masteryLabel(m: number, touched: boolean): string {
  if (!touched) return 'Pas commencé';
  if (m < 0.35) return 'Découverte';
  if (m < 0.6) return 'En progrès';
  if (m < 0.85) return 'Solide';
  return 'Maîtrisé';
}

export function dueExerciseIds(state: ProgressState, today: string = dayKey()): string[] {
  return Object.entries(state.items)
    .filter(([, s]) => s.due <= today)
    .sort(([, a], [, b]) => (a.due < b.due ? -1 : a.due > b.due ? 1 : a.box - b.box))
    .map(([id]) => id);
}

export function dueCardKeys(state: ProgressState, today: string = dayKey()): string[] {
  return Object.entries(state.cards)
    .filter(([, s]) => s.due <= today)
    .map(([k]) => k);
}
