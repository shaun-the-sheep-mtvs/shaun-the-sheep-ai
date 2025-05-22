// src/data/questions.ts
export type Category = 'moisture' | 'oil' | 'sensitivity' | 'tension';

export interface Question {
  id: number;
  category: Category;
  text: string;
  options: { label: string; score: number }[];
}

// 4개 카테고리 × 3문제씩 = 총 12문항
export const QUESTIONS: Question[] = [
  // --- moisture (수분) ---
  {
    id: 1,
    category: 'moisture',
    text: '하루 평균 물 섭취량은 어느 정도인가요?',
    options: [
      { label: '1L 미만', score: 2 },
      { label: '1~2L',   score: 1 },
      { label: '2L 이상', score: 0 },
    ],
  },
  {
    id: 2,
    category: 'moisture',
    text: '피부가 당기거나 건조함을 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '자주 그렇다', score: 2 },
    ],
  },
  {
    id: 3,
    category: 'moisture',
    text: '세안 후 피부가 거칠게 느껴지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },

  // --- oil (유분) ---
  {
    id: 4,
    category: 'oil',
    text: '오후나 저녁에 얼굴이 번들거림을 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 있다', score: 1 },
      { label: '뚜렷하다', score: 2 },
    ],
  },
  {
    id: 5,
    category: 'oil',
    text: '코 주위나 이마에 모공이 도드라지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },
  {
    id: 6,
    category: 'oil',
    text: '피부 표면에 기름기가 오래 남아있나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '자주 그렇다', score: 2 },
    ],
  },

  // --- sensitivity (민감도) ---
  {
    id: 7,
    category: 'sensitivity',
    text: '화장품·비누 사용 후 붉어짐이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '1~2회',    score: 1 },
      { label: '3회 이상', score: 2 },
    ],
  },
  {
    id: 8,
    category: 'sensitivity',
    text: '날씨(추위·더위) 변화에 피부가 예민해지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },
  {
    id: 9,
    category: 'sensitivity',
    text: '알레르기·발진 등의 피부 자극 반응이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '가끔 있다', score: 1 },
      { label: '자주 있다', score: 2 },
    ],
  },

  // --- tension (탄력) ---
  {
    id: 10,
    category: 'tension',
    text: '수면 부족으로 피부 탄력이 떨어지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '자주 그렇다', score: 2 },
    ],
  },
  {
    id: 11,
    category: 'tension',
    text: '피부 표면이 울퉁불퉁하거나 탄력이 없나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },
  {
    id: 12,
    category: 'tension',
    text: '얼굴에 잔주름이나 탄력 저하가 느껴지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 있다',   score: 1 },
      { label: '뚜렷하다',   score: 2 },
    ],
  },
];

// CATEGORY_MAP: 각 카테고리별로 해당하는 questionId 배열을 만듭니다.
export const CATEGORY_MAP: Record<Category, number[]> = {
  moisture: QUESTIONS.filter(q => q.category === 'moisture').map(q => q.id),
  oil: QUESTIONS.filter(q => q.category === 'oil').map(q => q.id),
  sensitivity: QUESTIONS.filter(q => q.category === 'sensitivity').map(q => q.id),
  tension: QUESTIONS.filter(q => q.category === 'tension').map(q => q.id),
};
