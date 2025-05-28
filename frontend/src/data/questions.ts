// src/data/questions.ts
export type Category = 'moisture' | 'oil' | 'sensitivity' | 'tension';

export interface Question {
  id: number;
  category: Category;
  text: string;
  options: { label: string; score: number }[];
}

// 4개 카테고리 × 5문제씩 = 총 20문항
export const QUESTIONS: Question[] = [
  // --- moisture (수분) ---
   {
    id:  1,
    category: 'moisture',
    text: '하루 평균 물 섭취량은 어느 정도인가요?',
    options: [
      { label: '1L 미만',      score: 4 },
      { label: '1~1.5L',      score: 3 },
      { label: '1.5~2L',      score: 2 },
      { label: '2~2.5L',      score: 1 },
      { label: '2.5L 이상',   score: 0 },
    ],
  },
  {
    id:  2,
    category: 'moisture',
    text: '피부가 당기거나 건조함을 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '자주 그렇다', score: 2 },
      { label: '거의 항상 그렇다', score: 3 },
      { label: '항상 그렇다',   score: 4 },
    ],
  },
  {
    id:  3,
    category: 'moisture',
    text: '세안 후 피부 표면이 거칠게 느껴지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '보통이다',     score: 2 },
      { label: '매우 그렇다', score: 3 },
      { label: '극심하다',     score: 4 },
    ],
  },
  {
    id:  4,
    category: 'moisture',
    text: '하루에 몇 번 보습제를 덧발라 주시나요?',
    options: [
      { label: '전혀 안 바른다',      score: 4 },
      { label: '1번',                  score: 3 },
      { label: '2번',                  score: 2 },
      { label: '3~4번',                score: 1 },
      { label: '5번 이상',             score: 0 },
    ],
  },
  {
    id:  5,
    category: 'moisture',
    text: '실내 난방/냉방 환경에서 피부가 건조해지나요?',
    options: [
      { label: '전혀 아니다',          score: 0 },
      { label: '가끔 그렇다',          score: 1 },
      { label: '보통이다',              score: 2 },
      { label: '매우 그렇다',          score: 3 },
      { label: '항상 그렇다',          score: 4 },
    ],
  },
  {
    id:  6,
    category: 'moisture',
    text: '아침과 저녁, 스킨(토너)으로 피부를 정돈하나요?',
    options: [
      { label: '전혀 사용하지 않는다',  score: 4 },
      { label: '가끔만 사용한다',        score: 3 },
      { label: '대부분 사용한다',        score: 2 },
      { label: '항상 사용한다',          score: 1 },
      { label: '추가 보습 토너도 함께 사용', score: 0 },
    ],
  },
  {
    id:  7,
    category: 'moisture',
    text: '피부 각질이 일어나거나 벗겨지는 경험이 있나요?',
    options: [
      { label: '전혀 없다',            score: 0 },
      { label: '가끔 있다',            score: 1 },
      { label: '보통이다',              score: 2 },
      { label: '자주 있다',            score: 3 },
      { label: '항상 그렇다',          score: 4 },
    ],
  },
  {
    id:  8,
    category: 'moisture',
    text: '수분 마스크 팩(시트 마스크)을 얼마나 자주 사용하나요?',
    options: [
      { label: '전혀 사용하지 않는다', score: 4 },
      { label: '월 1~2회',             score: 3 },
      { label: '주 1회',               score: 2 },
      { label: '주 2~3회',             score: 1 },
      { label: '거의 매일 사용',       score: 0 },
    ],
  },

  // --- oil (유분) 8문항 ---
{
  id: 9,
  category: 'oil',
  text: '오후나 저녁에 얼굴이 번들거림을 느끼나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '약간 있다',        score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 10,
  category: 'oil',
  text: '코 주위나 이마에 모공이 도드라지나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '약간 그렇다',      score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 11,
  category: 'oil',
  text: '피부 표면에 기름기가 오래 남아있나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '약간 그렇다',      score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 12,
  category: 'oil',
  text: '화장한 부위가 쉽게 무너지거나 번지나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '가끔 그렇다',      score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 13,
  category: 'oil',
  text: '매트한 제품을 사용해도 유분이 가시지 않나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '약간 그렇다',      score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 14,
  category: 'oil',
  text: '유분 조절 토너나 오일 블로팅 페이퍼를 사용하나요?',
  options: [
    { label: '전혀 사용하지 않는다', score: 0 },
    { label: '가끔만 사용한다',       score: 1 },
    { label: '보통 사용한다',         score: 2 },
    { label: '자주 사용한다',         score: 3 },
    { label: '항상 사용한다',         score: 4 },
  ],
},
{
  id: 15,
  category: 'oil',
  text: '야외 활동 후 피부에 기름기가 더 많아지나요?',
  options: [
    { label: '전혀 아니다',      score: 0 },
    { label: '약간 그렇다',      score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 그렇다',      score: 3 },
    { label: '항상 그렇다',      score: 4 },
  ],
},
{
  id: 16,
  category: 'oil',
  text: '피지 과다로 인해 트러블(여드름 등)이 생긴 적이 있나요?',
  options: [
    { label: '전혀 없다',        score: 0 },
    { label: '가끔 있다',        score: 1 },
    { label: '보통이다',          score: 2 },
    { label: '자주 있다',        score: 3 },
    { label: '항상 생긴다',      score: 4 },
  ],
},

  // --- sensitivity (민감도) ---
  {
    id: 11,
    category: 'sensitivity',
    text: '화장품·비누 사용 후 붉어짐이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '1~2회',    score: 1 },
      { label: '3회 이상', score: 2 },
    ],
  },
  {
    id: 12,
    category: 'sensitivity',
    text: '날씨(추위·더위) 변화에 피부가 예민해지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },
  {
    id: 13,
    category: 'sensitivity',
    text: '알레르기·발진 등의 피부 자극 반응이 있나요?',
    options: [
      { label: '전혀 없다', score: 0 },
      { label: '가끔 있다', score: 1 },
      { label: '자주 있다', score: 2 },
    ],
  },
  {
    id: 14,
    category: 'sensitivity',
    text: '새로운 스킨케어 제품을 바르면 따가움을 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '뚜렷하게 느낀다', score: 2 },
    ],
  },
  {
    id: 15,
    category: 'sensitivity',
    text: '햇빛 노출 후 얼굴이 쉽게 붉어지거나 자극 받나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '항상 그렇다', score: 2 },
    ],
  },

  // --- tension (탄력) ---
  {
    id: 16,
    category: 'tension',
    text: '수면 부족으로 피부 탄력이 떨어지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 그렇다', score: 1 },
      { label: '자주 그렇다', score: 2 },
    ],
  },
  {
    id: 17,
    category: 'tension',
    text: '피부 표면이 울퉁불퉁하거나 탄력이 없나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 그렇다', score: 1 },
      { label: '매우 그렇다', score: 2 },
    ],
  },
  {
    id: 18,
    category: 'tension',
    text: '얼굴에 잔주름이나 탄력 저하가 느껴지나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '가끔 있다',   score: 1 },
      { label: '뚜렷하다',   score: 2 },
    ],
  },
  {
    id: 19,
    category: 'tension',
    text: '목이나 눈가 피부도 탄력 저하를 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 느낀다', score: 1 },
      { label: '매우 느낀다', score: 2 },
    ],
  },
  {
    id: 20,
    category: 'tension',
    text: '얼굴 근육 운동(마사지)을 해 보면 탄력 개선을 느끼나요?',
    options: [
      { label: '전혀 아니다', score: 0 },
      { label: '약간 효과 있다', score: 1 },
      { label: '뚜렷한 효과 있다', score: 2 },
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
