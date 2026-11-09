export type Rng = () => number;

/** Deterministic PRNG (mulberry32) for tests and reproducible shuffles. */
export function seeded(seed: number): Rng {
  let a = seed >>> 0;
  return () => {
    a = (a + 0x6d2b79f5) >>> 0;
    let t = a;
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

export function shuffle<T>(items: readonly T[], rng: Rng = Math.random): T[] {
  const out = items.slice();
  for (let i = out.length - 1; i > 0; i--) {
    const j = Math.floor(rng() * (i + 1));
    [out[i], out[j]] = [out[j], out[i]];
  }
  return out;
}

/** Shuffle, but never return the original order when there is another one (avoids "already solved" orderings). */
export function shuffleAway<T>(items: readonly T[], rng: Rng = Math.random): T[] {
  if (items.length < 2) return items.slice();
  for (let attempt = 0; attempt < 8; attempt++) {
    const out = shuffle(items, rng);
    if (out.some((v, i) => v !== items[i])) return out;
  }
  const out = items.slice();
  out.push(out.shift() as T);
  return out;
}
