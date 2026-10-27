import { describe, expect, it } from 'vitest';
import { addDays, dayKey, daysBetween } from './dates';
import { contrastRatio, grade, grayHex, orderConcordance } from './grade';
import { emptyProgress, mastery, recordAttempt, recordCard, streak } from './progress';
import { seeded, shuffleAway } from './random';
import { buildSession, interleaveTypes } from './session';
import { scheduleCard, scheduleExercise } from './srs';
import type { Exercise } from './types';

const base = { difficulty: 1 as const, prompt: 'q', explanation: 'e', takeaway: 't' };

const choice: Exercise = {
  ...base,
  id: 'm-choice',
  module: 'm',
  type: 'choice',
  options: [{ id: 'a', label: 'A' }, { id: 'b', label: 'B' }],
  answer: 'b',
};

describe('grade', () => {
  it('grades a choice', () => {
    expect(grade(choice, { type: 'choice', optionId: 'b' }).verdict).toBe('correct');
    expect(grade(choice, { type: 'choice', optionId: 'a' }).verdict).toBe('wrong');
  });

  it('lets wrong picks cancel right ones in multi', () => {
    const ex: Exercise = {
      ...base,
      id: 'm-multi',
      type: 'multi',
      options: ['a', 'b', 'c', 'd'].map((id) => ({ id, label: id })),
      answers: ['a', 'b'],
    };
    expect(grade(ex, { type: 'multi', optionIds: ['a', 'b'] }).verdict).toBe('correct');
    expect(grade(ex, { type: 'multi', optionIds: ['a'] }).verdict).toBe('partial');
    expect(grade(ex, { type: 'multi', optionIds: ['a', 'c'] }).verdict).toBe('wrong');
  });

  it('scores order by pair concordance', () => {
    expect(orderConcordance(['a', 'b', 'c'], ['a', 'b', 'c'])).toBe(1);
    expect(orderConcordance(['c', 'b', 'a'], ['a', 'b', 'c'])).toBe(0);
    expect(orderConcordance(['b', 'a', 'c'], ['a', 'b', 'c'])).toBeCloseTo(2 / 3);
  });

  it('gives partial credit to close slider values', () => {
    const ex: Exercise = {
      ...base,
      id: 'm-slider',
      type: 'slider',
      visual: { html: '<p>x</p>', alt: 'x' },
      min: 1,
      max: 2.4,
      step: 0.05,
      initial: 1,
      unit: '',
      target: [1.4, 1.7],
      ideal: 1.55,
      control: 'Interlignage',
    };
    expect(grade(ex, { type: 'slider', value: 1.5 }).verdict).toBe('correct');
    expect(grade(ex, { type: 'slider', value: 1.3 }).verdict).toBe('partial');
    expect(grade(ex, { type: 'slider', value: 1.05 }).verdict).toBe('wrong');
    expect(grade(ex, { type: 'slider', value: 2.4 }).verdict).toBe('wrong');
  });

  it('computes WCAG contrast', () => {
    expect(contrastRatio('#000000', '#ffffff')).toBeCloseTo(21, 0);
    expect(contrastRatio('#767676', '#ffffff')).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#777777', '#ffffff')).toBeLessThan(4.5);
    expect(grayHex(100)).toBe('#ffffff');
  });
});

describe('dates', () => {
  it('adds days across months', () => {
    expect(addDays('2026-01-31', 1)).toBe('2026-02-01');
    expect(daysBetween('2026-02-27', '2026-03-01')).toBe(2);
    expect(dayKey(new Date(2026, 9, 27))).toBe('2026-10-27');
  });
});

describe('scheduling', () => {
  it('moves exercises through Leitner boxes', () => {
    const a = scheduleExercise(undefined, 'correct', '2026-10-27');
    expect(a).toEqual({ box: 1, due: '2026-10-28' });
    const b = scheduleExercise(a, 'correct', '2026-10-28');
    expect(b).toEqual({ box: 2, due: '2026-10-31' });
    expect(scheduleExercise(b, 'wrong', '2026-10-31')).toEqual({ box: 0, due: '2026-10-31' });
  });

  it('spaces flashcards out with SM-2', () => {
    const one = scheduleCard(undefined, 2, '2026-10-27');
    expect(one.interval).toBe(1);
    const two = scheduleCard(one, 2, '2026-10-28');
    expect(two.interval).toBe(3);
    const three = scheduleCard(two, 2, '2026-10-31');
    expect(three.interval).toBeGreaterThan(3);
    expect(scheduleCard(three, 0, '2026-11-08').interval).toBe(0);
  });
});

describe('progress', () => {
  it('records attempts, xp and streaks', () => {
    let s = emptyProgress();
    s = recordAttempt(s, choice, { verdict: 'correct', score: 1 }, new Date(2026, 9, 25)).state;
    s = recordCard(s, 'm/r1', 2, new Date(2026, 9, 26));
    s = recordAttempt(s, choice, { verdict: 'wrong', score: 0 }, new Date(2026, 9, 27)).state;
    expect(s.items['m-choice'].seen).toBe(2);
    expect(s.items['m-choice'].box).toBe(0);
    expect(streak(s, new Date(2026, 9, 27))).toMatchObject({ current: 3, best: 3, activeToday: true });
    expect(streak(s, new Date(2026, 9, 28)).current).toBe(3);
    expect(streak(s, new Date(2026, 9, 30)).current).toBe(0);
    expect(mastery(s, ['m-choice'])).toBeLessThan(0.5);
  });
});

describe('sessions', () => {
  const pool: Exercise[] = Array.from({ length: 30 }, (_, i) => ({
    ...choice,
    id: `ex-${i}`,
    module: `mod-${i % 3}`,
    type: i % 2 ? 'choice' : 'truefalse',
    ...(i % 2 ? {} : { answer: true }),
  })) as Exercise[];

  it('builds a daily session of the right size without duplicates', () => {
    const s = buildSession({ kind: 'daily' }, pool, emptyProgress(), seeded(1));
    expect(s).toHaveLength(10);
    expect(new Set(s.map((e) => e.id)).size).toBe(10);
  });

  it('puts due mistakes first in review', () => {
    let st = emptyProgress();
    st = recordAttempt(st, pool[4], { verdict: 'wrong', score: 0 }, new Date(2026, 9, 20)).state;
    const s = buildSession({ kind: 'review' }, pool, st, seeded(2), new Date(2026, 9, 27));
    expect(s.map((e) => e.id)).toEqual(['ex-4']);
  });

  it('interleaves exercise types', () => {
    const mixed = interleaveTypes(pool.slice(0, 6).sort((a, b) => a.type.localeCompare(b.type)));
    for (let i = 1; i < mixed.length; i++) expect(mixed[i].type).not.toBe(mixed[i - 1].type);
  });

  it('never returns an already-solved order', () => {
    const items = ['a', 'b', 'c'];
    for (let seed = 0; seed < 20; seed++) expect(shuffleAway(items, seeded(seed))).not.toEqual(items);
  });
});
