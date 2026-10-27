/**
 * Content contract for the training engine.
 *
 * Exercises, lessons and flashcard rules are plain data typed against these
 * interfaces. Text fields are free-form (the reference app renders a small
 * Markdown subset). `Visual` describes an HTML/CSS mock rendered by the host app.
 */

export type Difficulty = 1 | 2 | 3;

/** A rendered mini-interface, displayed inside an isolated Shadow DOM. */
export interface Visual {
  /** Markup of the mock. Elements with `data-hotspot="id"` become clickable in hotspot exercises. */
  html: string;
  /** CSS scoped to this mock only. */
  css?: string;
  /** Optional device chrome drawn around the mock. */
  frame?: 'none' | 'phone' | 'browser';
  /** Minimum height of the mock area, in px. */
  height?: number;
  /** Text alternative describing what the mock shows (read by screen readers). */
  alt: string;
  dir?: 'ltr' | 'rtl';
  lang?: string;
}

export interface SourceRef {
  label: string;
  url?: string;
}

/** A source reference: a key resolved by the host app, or an inline reference. */
export type Source = string | SourceRef;

export type ExerciseType =
  | 'choice'
  | 'multi'
  | 'truefalse'
  | 'hotspot'
  | 'order'
  | 'match'
  | 'slider'
  | 'grid';

interface ExerciseBase {
  /** Globally unique, kebab-case, prefixed by the module id: `typography-measure-duel`. */
  id: string;
  /** Module id the exercise belongs to (set automatically by the registry). */
  module?: string;
  difficulty: Difficulty;
  /** The question, short and direct. */
  prompt: string;
  /** Optional brief or scenario shown above the question. */
  context?: string;
  /** Optional visual shown with the question (required for hotspot and slider). */
  visual?: Visual;
  /** "Pourquoi" — the full explanation revealed after answering. */
  explanation: string;
  /** "À retenir" — the rule in one sentence. */
  takeaway: string;
  sources?: Source[];
  /** Free tags, usable to build themed sessions (`{ kind: 'tag', tag }`). */
  tags?: string[];
}

export interface ChoiceOption {
  id: string;
  /** Text of the option, or the neutral caption of a visual option (must not give the answer away). */
  label?: string;
  visual?: Visual;
  /** Feedback shown for this option after answering. */
  why?: string;
}

/** Single answer among 2–6 options (text or visuals). A duel is a choice with two visual options. */
export interface ChoiceExercise extends ExerciseBase {
  type: 'choice';
  options: ChoiceOption[];
  /** Id of the recommended option. */
  answer: string;
  /** Keep the authored order instead of shuffling (for scales, sequences…). */
  fixedOrder?: boolean;
}

/** Several correct answers among the options ("coche tout ce qui s'applique"). */
export interface MultiExercise extends ExerciseBase {
  type: 'multi';
  options: ChoiceOption[];
  answers: string[];
  fixedOrder?: boolean;
}

/** The prompt is a statement; the learner says if it is true. */
export interface TrueFalseExercise extends ExerciseBase {
  type: 'truefalse';
  answer: boolean;
}

export interface Hotspot {
  /** Matches a `data-hotspot` attribute in the visual. */
  id: string;
  /** Short name of the zone, revealed after answering. */
  label: string;
  /** True when this zone is one of the problems to find. */
  correct: boolean;
  why: string;
}

/** Click the problem(s) in the visual. The learner picks as many zones as there are correct ones. */
export interface HotspotExercise extends ExerciseBase {
  type: 'hotspot';
  visual: Visual;
  hotspots: Hotspot[];
}

/** Put the items in order. Items are authored in the CORRECT order and shuffled on display. */
export interface OrderExercise extends ExerciseBase {
  type: 'order';
  items: { id: string; label: string }[];
}

/** Pair each left item with its right item. Rights are shuffled on display. */
export interface MatchExercise extends ExerciseBase {
  type: 'match';
  leftTitle: string;
  rightTitle: string;
  pairs: { id: string; left: string; right: string }[];
}

/**
 * Calibrate a value with a live preview. The slider writes `--v` (value + unit)
 * on the mock host, so the visual's CSS can use `var(--v)`. `--n` holds the raw number.
 */
export interface SliderExercise extends ExerciseBase {
  type: 'slider';
  visual: Visual;
  min: number;
  max: number;
  step: number;
  initial: number;
  /** Unit appended to the value for `--v` and for display (`px`, `ms`, `ch`, `rem`, ''). */
  unit: string;
  /** Inclusive accepted range. */
  target: [number, number];
  /** The value an expert would pick, shown in the feedback. */
  ideal: number;
  /** Label of the slider control (`Interlignage`, `Durée`…). */
  control: string;
  /**
   * Extra live readout. `contrast`: the slider value is the HSL lightness (0–100, unit `%`)
   * of a neutral gray text, e.g. `color: hsl(0 0% var(--v))`; the WCAG ratio against
   * `background` (hex) is shown live.
   */
  readout?: { kind: 'contrast'; background: string };
  /** Animated previews get a "Rejouer" button that remounts the mock. */
  replayable?: boolean;
}

export interface GridCriterion {
  id: string;
  label: string;
  /** Expert score on the scale. */
  expert: number;
  why: string;
}

/** Score a screen on several criteria (e.g. Nielsen's 10 heuristics, 0–4) and compare with an expert. */
export interface GridExercise extends ExerciseBase {
  type: 'grid';
  visual: Visual;
  scale: { min: number; max: number; labels: string[] };
  criteria: GridCriterion[];
  /** Max distance from the expert score that still counts as agreement (default 1). */
  tolerance?: number;
}

export type Exercise =
  | ChoiceExercise
  | MultiExercise
  | TrueFalseExercise
  | HotspotExercise
  | OrderExercise
  | MatchExercise
  | SliderExercise
  | GridExercise;

/* ---------- Lessons ---------- */

/** A do/don't pair shown inside a lesson section. */
export interface LessonExample {
  title: string;
  bad?: Visual;
  badNote?: string;
  good?: Visual;
  goodNote?: string;
}

export interface LessonSection {
  /** Anchor id, kebab-case, unique within the module. */
  id: string;
  title: string;
  body: string;
  examples?: LessonExample[];
}

/** A key rule; doubles as a flashcard (front = question, back = answer). */
export interface KeyRule {
  id: string;
  front: string;
  back: string;
}

/** Free-form category id chosen by the host app. */
export type ModuleCategory = string;

export interface ModuleContent {
  /** kebab-case id, also used in URLs. */
  id: string;
  title: string;
  category: ModuleCategory;
  /** One or two sentences: what the learner will be able to do. */
  summary: string;
  /** Estimated reading time in minutes. */
  minutes: number;
  /** Optional external references (commands, chapters…). */
  commands?: string[];
  sources: Source[];
  sections: LessonSection[];
  rules: KeyRule[];
  exercises: Exercise[];
}
