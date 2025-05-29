// src/data/questions.ts
export type Category = 'moisture' | 'oil' | 'sensitivity' | 'tension';

export interface Question {
  id: number;
  category: Category;
  text: string;
  weight: number;                // ← 여기를 추
  options: { label: string; score: number }[];
}

// 4개 카테고리 × 5문제씩 = 총 20문항
export const QUESTIONS: Question[] = [
  // --- moisture ---
  {
    id: 1,
    category: 'moisture',
    weight: 1.5,
    text: '하루에 물(물 대용 음료 제외)을 얼마나 섭취하시나요?',
    options: [
      { label: '1L 미만', score: 4 },
      { label: '1~1.5L', score: 3 },
      { label: '1.5~2L', score: 2 },
      { label: '2~2.5L', score: 1 },
      { label: '모름', score: 0 },
    ],
  },
  {
    id: 2,
    category: 'moisture',
    weight: 1.4,
    text: '세안 직후 피부가 당기거나 건조함을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 3,
    category: 'moisture',
    weight: 1.2,
    text: '세안 후 피부가 거칠게 느껴지시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 4,
    category: 'moisture',
    weight: 1.1,
    text: '하루에 수분이나 크림(로션)을 하루에 몇 번 바르시나요?',
    options: [
      { label: '해당 없음', score: 4 },
      { label: '1회', score: 3 },
      { label: '2회', score: 2 },
      { label: '3회', score: 1 },
      { label: '4회 이상', score: 0 },
    ],
  },
  {
    id: 5,
    category: 'moisture',
    weight: 1.0,
    text: '에어컨·히터 사용 시 실내에서 피부 건조를 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 6,
    category: 'moisture',
    weight: 0.9,
    text: '밤에 피부가 건조해서 가렵거나 당기는 감각이 있나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 7,
    category: 'moisture',
    weight: 0.8,
    text: '하루 동안 입술이 건조하거나 트는 경험이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '가끔 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 있다', score: 4 },
    ],
  },
  {
    id: 8,
    category: 'moisture',
    weight: 0.7,
    text: '일주일에 수분 마스크팩을 얼마나 자주 사용하시나요?',
    options: [
      { label: '전혀 사용하지 않음', score: 4 },
      { label: '월 1회 미만', score: 3 },
      { label: '월 1~2회', score: 2 },
      { label: '주 1~2회', score: 1 },
      { label: '주 3회 이상', score: 0 },
    ],
  },

  // --- oil ---
  {
    id: 9,
    category: 'oil',
    weight: 1.0,
    text: '오후에 얼굴에 번들거림을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 번들거린다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 번들거린다', score: 3 },
      { label: '항상 번들거린다', score: 4 },
    ],
  },
  {
    id: 10,
    category: 'oil',
    weight: 1.2,
    text: '코 주변과 이마 모공이 눈에 띄게 확대되어 보이나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '뚜렷하다', score: 3 },
      { label: '매우 뚜렷하다', score: 4 },
    ],
  },
  {
    id: 11,
    category: 'oil',
    weight: 1.3,
    text: '세안 후 3시간 이내에 피부 표면에 유분이 남아 있나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 12,
    category: 'oil',
    weight: 1.0,
    text: '파운데이션 등 화장한 부위가 쉽게 무너지거나 번지시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 번진다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 번진다', score: 3 },
      { label: '항상 번진다', score: 4 },
    ],
  },
  {
    id: 13,
    category: 'oil',
    weight: 0.8,
    text: '매트 타입 제품 사용 후에도 번들거림이 남아 있나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 14,
    category: 'oil',
    weight: 0.8, 
    text: '블러셔나 파우더 사용 후에도 유분감이 느껴지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 있다', score: 4 },
    ],
  },
  {
    id: 15,
    category: 'oil',
    weight: 1.2,
    text: '하루 중 특정 부위(이마, 콧볼 등)에 유분이 더 몰리나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '뚜렷하게 그렇다', score: 3 },
      { label: '매우 그렇다', score: 4 },
    ],
  },
  {
    id: 16,
    category: 'oil',
    weight: 1.0,
    text: '얼굴을 만졌을 때 손에 유분감이 묻어나나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 있다', score: 4 },
    ],
  },

  // --- sensitivity ---
  {
    id: 17,
    category: 'sensitivity',
    weight: 1.5,
    text: '스킨케어 제품·비누 사용 직후 피부가 붉어지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 붉어진다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 18,
    category: 'sensitivity',
    weight: 1.3, 
    text: '온도·습도 변화 시 피부에 자극을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 19,
    category: 'sensitivity',
    weight: 1.5, 
    text: '알레르기 반응(발진·가려움 등)을 경험한 적이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '가끔 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 있다', score: 4 },
    ],
  },
  {
    id: 20,
    category: 'sensitivity',
    weight: 1.4, 
    text: '새 제품 사용 시 피부에 찌릿함·따가움을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 21,
    category: 'sensitivity',
    weight: 1.2,
    text: '자외선 노출 후 피부가 붉어지거나 따가움을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 22,
    category: 'sensitivity',
    weight: 1.1,
    text: '피부 트러블 시 가려움이나 통증을 느끼시나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 23,
    category: 'sensitivity',
    weight: 1.0,
    text: '미세먼지 많은 날 피부가 예민해지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '조금 그렇다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 그렇다', score: 3 },
      { label: '항상 그렇다', score: 4 },
    ],
  },
  {
    id: 24,
    category: 'sensitivity',
    weight: 0.8,
    text: '새로운 음식이나 향수 등 자극 원인 후 반응이 있나요?',
    options: [
      { label: '모름/해당 없음', score: 0 },
      { label: '조금 있다', score: 1 },
      { label: '보통이다', score: 2 },
      { label: '자주 있다', score: 3 },
      { label: '항상 있다', score: 4 },
    ],
  },

  // --- tension ---
   {
    id: 25,
    category: 'tension',
    weight: 1.5,
    text: '최근 1개월 동안 눈가나 입가에 잔주름이 눈에 띄나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다", score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 26,
    category: 'tension',
    weight: 1.5,
    text: '최근 1개월 동안 거울로 볼 때 턱선이나 볼이 처져 보이나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다",   score:  2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 27,
    category: 'tension',
    weight: 1.2,
    text: '최근 1개월 동안 아침에 일어났을 때 베개 자국이 얼굴에 오래 남나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다", score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 28,
    category: 'tension',
    weight: 1.2,
    text: '최근 1개월 동안 얼굴이 무겁거나 아래로 쳐진 느낌이 드나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다", score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 29,
    category: 'tension',
    weight: 1.2,
    text: '최근 1개월 동안 화장하거나 거울 볼 때 피부가 덜 탱탱하고 늘어져 보이나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다",  score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 30,
    category: 'tension',
    weight: 1.2,
    text: '최근 1개월 동안 사진이나 영상에서 얼굴 윤곽이 흐릿해 보이나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다", score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 31,
    category: 'tension',
    weight: 1.0,
    text: "최근 1개월 동안 얼굴 피부가 얇아지거나 힘없이 늘어진 느낌이 드나요?",
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다",  score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
    ],
  },
  {
    id: 32,
    category: 'tension',
    weight: 1.0,
    text: '최근 1개월 동안 웃을 때 얼굴이 덜 탱탱하고 늘어져 보이나요?',
    options: [
      { label: "전혀 아니다", score: 0 },
      { label: "약간 그렇다", score: 1 },
      { label: "보통이다",  score: 2 },
      { label: "자주 그렇다", score: 3 },
      { label: "항상 그렇다", score: 4 }
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
