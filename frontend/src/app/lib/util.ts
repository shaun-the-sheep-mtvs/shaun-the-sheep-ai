import { CATEGORY_MAP } from '@/data/questions';

export function calcCategoryScore(
  category: keyof typeof CATEGORY_MAP,
  answers: { questionId: number; score: number }[]
): number {
  return answers
    .filter(a => CATEGORY_MAP[category].includes(a.questionId))
    .reduce((sum, a) => sum + a.score, 0);
}
