'use client';

import React, { useEffect, useState } from 'react';
import styles from './page.module.css';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import Image from 'next/image';
import { Info } from 'lucide-react';

interface CheckListResponse {
  id: number;
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
  createdAt: string;
}

interface SkinTypeInfo {
  type: string;
  characteristics: string[];
  ingredients: string[];
  improvements: string[];
}

interface DetailInfo {
  title: string;
  icon: string;
  description: string;
  characteristics: string[];
  solutions: string[];
  products: string[];
}

const measurementInfo = {
  moisture: {
    title: "수분 지수",
    description: "피부의 수분 보유량을 측정한 값입니다.",
    ideal: "적정 수준: 60-70%",
    icon: "💧",
    highCharacteristics: [
      "피부가 촉촉하고 윤기가 있음",
      "피부 결이 매끄러움",
      "피부가 탄력있게 느껴짐",
      "메이크업이 잘 먹음"
    ],
    lowCharacteristics: [
      "피부가 건조하고 각질이 일어남",
      "피부가 당기고 가려움",
      "미세주름이 눈에 띔",
      "메이크업이 들뜸"
    ],
    ingredients: [
      "히알루론산 ⭐",
      "세라마이드 🛡️",
      "글리세린 💧",
      "판테놀 🌱"
    ],
    improvements: [
      "수분크림 충분히 사용",
      "실내 습도 관리",
      "하루 8잔 이상 물 섭취",
      "미스트 수시로 사용"
    ]
  },
  oil: {
    title: "유분 지수",
    description: "피부 표면의 유분량을 측정한 값입니다.",
    ideal: "적정 수준: 50-60%",
    icon: "🫧",
    highCharacteristics: [
      "피부가 번들거리는 편",
      "모공이 잘 보이는 편",
      "여드름이 자주 발생",
      "화장이 잘 지워짐"
    ],
    lowCharacteristics: [
      "피부가 건조하고 푸석함",
      "각질이 자주 일어남",
      "피부가 얇게 느껴짐",
      "주름이 생기기 쉬움"
    ],
    ingredients: [
      "나이아신아마이드 ⭐",
      "살리실산 🧪",
      "징크 🔮",
      "티트리 추출물 🌿"
    ],
    improvements: [
      "꼼꼼한 클렌징",
      "가벼운 수분 공급",
      "유분 조절 제품 사용",
      "모공 관리"
    ]
  },
  sensitivity: {
    title: "민감도 지수",
    description: "피부의 민감한 정도를 나타내는 값입니다.",
    ideal: "적정 수준: 30-40%",
    icon: "🌡️",
    highCharacteristics: [
      "자극에 민감한 반응",
      "피부가 쉽게 붉어짐",
      "가려움증 발생",
      "따가운 느낌"
    ],
    lowCharacteristics: [
      "자극에 강한 편",
      "피부 장벽이 튼튼함",
      "트러블이 적은 편",
      "회복력이 좋음"
    ],
    ingredients: [
      "판테놀 ⭐",
      "마데카소사이드 🌿",
      "알로에베라 🪴",
      "카렌듈라 🌼"
    ],
    improvements: [
      "자극 없는 제품 사용",
      "피부 장벽 강화",
      "온도 자극 피하기",
      "저자극 선크림 사용"
    ]
  },
  tension: {
    title: "탄력 지수",
    description: "피부의 탄력도를 측정한 값입니다.",
    ideal: "적정 수준: 70-80%",
    icon: "✨",
    highCharacteristics: [
      "피부가 탄탄하고 단단함",
      "피부 복원력이 좋음",
      "주름이 잘 생기지 않음",
      "얼굴 윤곽이 또렷함"
    ],
    lowCharacteristics: [
      "피부가 처진 느낌",
      "볼과 턱선이 늘어짐",
      "잔주름이 생기기 쉬움",
      "피부 탄성 저하"
    ],
    ingredients: [
      "펩타이드 ⭐",
      "레티놀 🌟",
      "콜라겐 💫",
      "아데노신 ✨"
    ],
    improvements: [
      "탄력 강화 제품 사용",
      "마사지 관리",
      "충분한 영양 공급",
      "자외선 차단"
    ]
  }
};

const skinTypeInfoMap: { [key: string]: SkinTypeInfo } = {
  "DBST": {
    type: "건성",
    characteristics: [
      "수분 부족으로 인한 당김과 각질",
      "모공이 작고 피부결이 고운 편",
      "피부가 얇고 민감한 편",
      "외부 자극에 민감하게 반응"
    ],
    ingredients: [
      "세라마이드 - 피부 장벽 강화",
      "히알루론산 - 수분 공급과 보습",
      "스쿠알란 - 피부 보습과 진정",
      "판테놀 - 피부 재생과 진정"
    ],
    improvements: [
      "충분한 수분 공급을 위한 스킨케어 루틴 구축",
      "자극이 적은 순한 성분의 제품 사용",
      "보습 크림을 충분히 사용하여 수분 증발 방지",
      "실내 습도 관리와 충분한 물 섭취"
    ]
  },
  "DBSL": {
    type: "건성",
    characteristics: [
      "수분과 유분 모두 부족",
      "피부 당김과 각질이 심함",
      "주름이 잘 생기는 편",
      "칙칙하고 탄력이 부족"
    ],
    ingredients: [
      "펩타이드 - 탄력 개선",
      "히알루론산 - 수분 공급",
      "세라마이드 - 장벽 강화",
      "레티놀 - 탄력 강화"
    ],
    improvements: [
      "고보습 제품으로 수분과 영양 공급",
      "탄력 케어를 위한 마사지와 관리",
      "자외선 차단제 꼭 사용하기",
      "영양가 있는 식단 관리"
    ]
  },
  "DBIL": {
    type: "건성",
    characteristics: [
      "수분과 유분의 균형이 맞지 않음",
      "피부가 건조하고 각질이 일어남",
      "주름이 잘 생기는 편",
      "탄력이 부족함"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "세라마이드 - 장벽 강화",
      "레티놀 - 탄력 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유분 조절 제품 사용",
      "영양 공급"
    ]
  },
  "DBIH": {
    type: "건성",
    characteristics: [
      "수분과 유분의 균형이 맞지 않음",
      "피부가 건조하고 각질이 일어남",
      "주름이 잘 생기는 편",
      "탄력이 부족함"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "세라마이드 - 장벽 강화",
      "레티놀 - 탄력 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유분 조절 제품 사용",
      "영양 공급"
    ]
  },
  "OBST": {
    type: "지성",
    characteristics: [
      "유분이 많고 번들거리는 편",
      "모공이 크고 잘 보이는 편",
      "여드름이 자주 발생",
      "메이크업이 잘 지워짐"
    ],
    ingredients: [
      "나이아신아마이드 - 피지 조절",
      "징크 - 진정 효과",
      "티트리 추출물 - 트러블 케어"
    ],
    improvements: [
      "꼼꼼한 클렌징",
      "유분 조절 제품 사용",
      "모공 관리",
      "메이크업 지속 시간 연장"
    ]
  },
  "OBSL": {
    type: "지성",
    characteristics: [
      "유분이 많고 번들거리는 편",
      "모공이 크고 잘 보이는 편",
      "여드름이 자주 발생",
      "메이크업이 잘 지워짐"
    ],
    ingredients: [
      "나이아신아마이드 - 피지 조절",
      "징크 - 진정 효과",
      "티트리 추출물 - 트러블 케어"
    ],
    improvements: [
      "꼼꼼한 클렌징",
      "유분 조절 제품 사용",
      "모공 관리",
      "메이크업 지속 시간 연장"
    ]
  },
  "OBIL": {
    type: "지성",
    characteristics: [
      "유분이 많고 번들거리는 편",
      "모공이 크고 잘 보이는 편",
      "여드름이 자주 발생",
      "메이크업이 잘 지워짐"
    ],
    ingredients: [
      "나이아신아마이드 - 피지 조절",
      "징크 - 진정 효과",
      "티트리 추출물 - 트러블 케어"
    ],
    improvements: [
      "꼼꼼한 클렌징",
      "유분 조절 제품 사용",
      "모공 관리",
      "메이크업 지속 시간 연장"
    ]
  },
  "OBIH": {
    type: "지성",
    characteristics: [
      "유분이 많고 번들거리는 편",
      "모공이 크고 잘 보이는 편",
      "여드름이 자주 발생",
      "메이크업이 잘 지워짐"
    ],
    ingredients: [
      "나이아신아마이드 - 피지 조절",
      "징크 - 진정 효과",
      "티트리 추출물 - 트러블 케어"
    ],
    improvements: [
      "꼼꼼한 클렌징",
      "유분 조절 제품 사용",
      "모공 관리",
      "메이크업 지속 시간 연장"
    ]
  },
  "CBST": {
    type: "복합성",
    characteristics: [
      "T존은 지성, 볼은 건성",
      "부위별 관리가 필요",
      "계절에 따라 변화가 큼",
      "피부 밸런스가 불안정"
    ],
    ingredients: [
      "판테놀 - 진정 효과",
      "알로에베라 - 수분 공급",
      "티트리 - 트러블 케어"
    ],
    improvements: [
      "부위별 맞춤 케어",
      "수분/유분 밸런스 맞추기",
      "저자극 제품으로 관리",
      "정기적인 각질 관리"
    ]
  },
  "CBSL": {
    type: "복합성",
    characteristics: [
      "T존은 지성, 볼은 건성",
      "부위별 관리가 필요",
      "계절에 따라 변화가 큼",
      "피부 밸런스가 불안정"
    ],
    ingredients: [
      "판테놀 - 진정 효과",
      "알로에베라 - 수분 공급",
      "티트리 - 트러블 케어"
    ],
    improvements: [
      "부위별 맞춤 케어",
      "수분/유분 밸런스 맞추기",
      "저자극 제품으로 관리",
      "정기적인 각질 관리"
    ]
  },
  "CBIL": {
    type: "복합성",
    characteristics: [
      "T존은 지성, 볼은 건성",
      "부위별 관리가 필요",
      "계절에 따라 변화가 큼",
      "피부 밸런스가 불안정"
    ],
    ingredients: [
      "판테놀 - 진정 효과",
      "알로에베라 - 수분 공급",
      "티트리 - 트러블 케어"
    ],
    improvements: [
      "부위별 맞춤 케어",
      "수분/유분 밸런스 맞추기",
      "저자극 제품으로 관리",
      "정기적인 각질 관리"
    ]
  },
  "CBIH": {
    type: "복합성",
    characteristics: [
      "T존은 지성, 볼은 건성",
      "부위별 관리가 필요",
      "계절에 따라 변화가 큼",
      "피부 밸런스가 불안정"
    ],
    ingredients: [
      "판테놀 - 진정 효과",
      "알로에베라 - 수분 공급",
      "티트리 - 트러블 케어"
    ],
    improvements: [
      "부위별 맞춤 케어",
      "수분/유분 밸런스 맞추기",
      "저자극 제품으로 관리",
      "정기적인 각질 관리"
    ]
  },
  "HBST": {
    type: "수분부족 지성",
    characteristics: [
      "수분이 부족하고 유분이 많은 피부",
      "겉은 번들, 속은 건조",
      "모공이 잘 막힘",
      "피부가 예민한 편"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "판테놀 - 진정 효과",
      "베타인 - 보습 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유수분 밸런스 맞추기",
      "진정 케어 병행"
    ]
  },
  "HBSL": {
    type: "수분부족 지성",
    characteristics: [
      "수분이 부족하고 유분이 많은 피부",
      "겉은 번들, 속은 건조",
      "모공이 잘 막힘",
      "피부가 예민한 편"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "판테놀 - 진정 효과",
      "베타인 - 보습 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유수분 밸런스 맞추기",
      "진정 케어 병행"
    ]
  },
  "HBIL": {
    type: "수분부족 지성",
    characteristics: [
      "수분이 부족하고 유분이 많은 피부",
      "겉은 번들, 속은 건조",
      "모공이 잘 막힘",
      "피부가 예민한 편"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "판테놀 - 진정 효과",
      "베타인 - 보습 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유수분 밸런스 맞추기",
      "진정 케어 병행"
    ]
  },
  "HBIH": {
    type: "수분부족 지성",
    characteristics: [
      "수분이 부족하고 유분이 많은 피부",
      "겉은 번들, 속은 건조",
      "모공이 잘 막힘",
      "피부가 예민한 편"
    ],
    ingredients: [
      "히알루론산 - 수분 공급",
      "판테놀 - 진정 효과",
      "베타인 - 보습 강화"
    ],
    improvements: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유수분 밸런스 맞추기",
      "진정 케어 병행"
    ]
  },
  "default": {
    type: "일반",
    characteristics: [
      "비교적 건강한 피부 상태",
      "수분과 유분의 균형이 잘 잡힘",
      "특별한 피부 고민이 적음"
    ],
    ingredients: [
      "히알루론산 - 보습 유지",
      "비타민C - 피부 활력",
      "나이아신아마이드 - 피부 컨디션 개선"
    ],
    improvements: [
      "현재의 피부 상태 유지를 위한 관리",
      "기본적인 수분 공급과 자외선 차단",
      "규칙적인 생활 습관 유지"
    ]
  }
};

const detailInfoMap: Record<string, DetailInfo> = {
  moisture: {
    title: "수분",
    icon: "💧",
    description: "피부의 수분 보유량을 측정한 결과입니다.",
    characteristics: [
      "피부가 건조하고 당김",
      "각질이 자주 일어남",
      "피부 결이 거칠어짐"
    ],
    solutions: [
      "충분한 수분 섭취",
      "가습기 사용으로 실내 습도 유지",
      "수분 크림 충분히 사용",
      "미스트 수시로 사용"
    ],
    products: [
      "히알루론산 세럼",
      "세라마이드 크림",
      "판테놀 함유 제품",
      "수분 마스크팩"
    ]
  },
  oil: {
    title: "유분",
    icon: "🫧",
    description: "피부 표면의 유분량을 측정한 결과입니다.",
    characteristics: [
      "T존 부위 번들거림",
      "모공이 눈에 띔",
      "여드름이 자주 발생"
    ],
    solutions: [
      "꼼꼼한 클렌징",
      "유분 조절 스킨케어 사용",
      "모공 관리 주기적으로 하기",
      "유분기 없는 제품 선택"
    ],
    products: [
      "살리실산 토너",
      "무유분 수분크림",
      "클레이 마스크",
      "피지 조절 세럼"
    ]
  },
  sensitivity: {
    title: "민감도",
    icon: "🛡️",
    description: "피부의 민감한 정도를 측정한 결과입니다.",
    characteristics: [
      "자극에 쉽게 반응",
      "피부가 쉽게 붉어짐",
      "가려움증이 자주 발생"
    ],
    solutions: [
      "자극 없는 순한 제품 사용",
      "피부 장벽 강화 관리",
      "자외선 차단제 꼭 사용",
      "알레르기 유발 성분 피하기"
    ],
    products: [
      "시카 크림",
      "무향 스킨케어",
      "판테놀 세럼",
      "진정 마스크"
    ]
  },
  tension: {
    title: "탄력도",
    icon: "✨",
    description: "피부의 탄력도를 측정한 결과입니다.",
    characteristics: [
      "피부가 처진 느낌",
      "볼과 턱선이 늘어짐",
      "피부 탄성이 감소"
    ],
    solutions: [
      "탄력 강화 제품 사용",
      "탄력 운동 꾸준히 하기",
      "영양 크림 사용하기",
      "마사지로 혈액순환 촉진",
      "콜라겐 보충제 섭취"
    ],
    products: [
      "펩타이드 세럼",
      "리프팅 크림",
      "콜라겐 앰플",
      "탄력 마스크"
    ]
  }
};

const skinTypeAnalysis = {
  sensitive: {
    name: "민감성",
    characteristics: [
      "외부 자극에 민감한 반응",
      "피부가 쉽게 붉어지고 자극받음",
      "트러블이 자주 발생",
      "피부 장벽이 약한 편"
    ],
    care: [
      "자극이 적은 순한 제품 사용",
      "피부 장벽 강화에 집중",
      "자외선 차단제 꼼꼼히 사용",
      "진정 케어 제품 활용"
    ],
    products: [
      "저자극 클렌저",
      "진정 에센스",
      "보습 크림",
      "무기자차"
    ]
  },
  dry: {
    name: "건성",
    characteristics: [
      "수분과 유분이 모두 부족",
      "피부가 건조하고 당김",
      "각질이 자주 일어남",
      "미세주름이 생기기 쉬움"
    ],
    care: [
      "충분한 수분 공급",
      "보습 제품 덧바르기",
      "각질 관리하기",
      "보습 마스크팩 활용"
    ],
    products: [
      "크림형 클렌저",
      "고보습 토너",
      "영양 크림",
      "보습 마스크"
    ]
  },
  oily: {
    name: "지성",
    characteristics: [
      "과다한 유분 분비",
      "번들거리는 피부",
      "모공이 잘 보이는 편",
      "여드름이 잘 생김"
    ],
    care: [
      "꼼꼼한 클렌징",
      "유분 조절 관리",
      "모공 관리하기",
      "가벼운 수분 공급"
    ],
    products: [
      "폼 클렌저",
      "유분 조절 토너",
      "가벼운 로션",
      "클레이 마스크"
    ]
  },
  combination: {
    name: "복합성",
    characteristics: [
      "T존은 지성, 볼은 건성",
      "부위별로 다른 관리 필요",
      "계절에 따라 변화가 큼",
      "피부 밸런스가 불안정"
    ],
    care: [
      "부위별 맞춤 관리",
      "수분/유분 밸런스 맞추기",
      "저자극 제품 사용",
      "정기적인 각질 관리"
    ],
    products: [
      "약산성 클렌저",
      "밸런싱 토너",
      "수분/영양 크림",
      "부위별 마스크"
    ]
  },
  dehydrated: {
    name: "수분부족지성",
    characteristics: [
      "수분 부족, 유분 과다",
      "겉은 번들, 속은 건조",
      "모공이 잘 막힘",
      "피부가 예민한 편"
    ],
    care: [
      "수분 공급에 집중",
      "순한 클렌징",
      "유수분 밸런스 맞추기",
      "진정 케어 병행"
    ],
    products: [
      "저자극 클렌저",
      "수분 에센스",
      "수분 크림",
      "진정 마스크"
    ]
  }
};

const mbtiDescriptionMap: { [key: string]: string } = {
  "MOSL": "수분이 풍부하고 유분도 많은 피부지만 민감하고 탄력이 부족한 타입",
  "MOSH": "수분과 유분이 풍부하면서 민감도와 탄력 저하까지 있는 타입",
  "MOBL": "수분은 풍부하나 유분이 부족하고, 피부는 민감하지 않으며 탄력이 떨어진 타입",
  "MOBH": "수분은 풍부하지만 유분 부족, 민감하지 않음, 탄력 저하를 모두 가진 타입",

  // 건성(Dry) 타입
  "DBST": "수분과 유분이 모두 부족하며 피부가 민감하고 탄력은 양호한 타입",
  "DBSL": "수분과 유분이 모두 부족하고 민감하며 탄력도 떨어진 타입",
  "DBIL": "수분과 유분이 부족하지만 민감하지 않고 탄력이 떨어진 타입",
  "DBIH": "수분과 유분이 부족하고 민감하지 않으며 탄력도 저하된 타입",

  // 지성(Oily) 타입
 "OBST": "수분은 부족하지만 유분은 과다하고, 피부가 민감하며 탄력은 양호한 타입",
  "OBSL": "수분은 부족하고 유분은 과다하며 민감하고 탄력도 부족한 타입",
  "OBIL": "수분은 부족하고 유분은 과다하지만 민감하지 않고 탄력이 떨어진 타입",
  "OBIH": "수분 부족, 유분 과다, 민감하지 않음, 탄력 저하가 복합된 타입",

  // 복합성(Combination) 타입
  "CBST": "수분과 유분이 균형 잡힌 복합성 피부로, 민감하고 탄력은 양호한 타입",
  "CBSL": "복합성 피부이며 민감하고 탄력이 부족한 타입",
  "CBIL": "복합성 피부로 민감하지 않지만 탄력이 부족한 타입",
  "CBIH": "복합성 피부로 민감하지 않고 탄력도 저하된 타입",

  // 수분부족 지성(Hydrated) 타입
  "HBST": "수분부족 지성",
  "HBSL": "수분부족 지성",
  "HBIL": "수분부족 지성",
  "HBIH": "수분부족 지성",

  "default": "아직 피부 타입이 분석되지 않았습니다."
};

const getAnalysisContent = (type: string, value: number) => {
  const contents = {
    moisture: {
      low: {
        range: "0-40%",
        characteristics: [
          "심한 건조함과 당김",
          "각질이 자주 일어남",
          "미세주름이 눈에 띔"
        ],
        solutions: [
          "집중 보습 케어 필요",
          "수분 크림 덧바르기",
          "보습 마스크팩 활용"
        ]
      },
      medium: {
        range: "41-60%",
        characteristics: [
          "약간의 건조함",
          "간헐적인 각질",
          "부분적인 당김"
        ],
        solutions: [
          "꾸준한 보습 관리",
          "수분 에센스 사용",
          "충분한 수분 섭취"
        ]
      },
      high: {
        range: "61-100%",
        characteristics: [
          "촉촉하고 건강한 상태",
          "윤기있는 피부결",
          "탄력있는 피부"
        ],
        solutions: [
          "현재 상태 유지",
          "가벼운 수분 케어",
          "수분 밸런스 유지"
        ]
      }
    },
    oil: {
      low: {
        range: "0-40%",
        characteristics: [
          "유분이 부족한 상태",
          "푸석하고 건조함",
          "메이크업이 들뜸"
        ],
        solutions: [
          "오일 기반 제품 사용",
          "영양 크림 활용",
          "보습 베리어 강화"
        ]
      },
      medium: {
        range: "41-60%",
        characteristics: [
          "적절한 유분량",
          "건강한 피부 상태",
          "균형잡힌 피부"
        ],
        solutions: [
          "현재 상태 유지",
          "적절한 클렌징",
          "밸런싱 케어"
        ]
      },
      high: {
        range: "61-100%",
        characteristics: [
          "과다 유분 분비",
          "번들거리는 피부",
          "모공 확장"
        ],
        solutions: [
          "유분 조절 제품 사용",
          "꼼꼼한 클렌징",
          "모공 관리"
        ]
      }
    },
    sensitivity: {
      low: {
        range: "0-30%",
        characteristics: [
          "건강한 피부 장벽",
          "안정적인 피부",
          "자극에 강한 피부"
        ],
        solutions: [
          "현재 상태 유지",
          "순한 제품 사용",
          "기본 케어 유지"
        ]
      },
      medium: {
        range: "31-60%",
        characteristics: [
          "간헐적인 민감 반응",
          "약간의 자극감",
          "일시적인 홍조"
        ],
        solutions: [
          "진정 케어 필요",
          "자극 최소화",
          "저자극 제품 사용"
        ]
      },
      high: {
        range: "61-100%",
        characteristics: [
          "민감한 피부 상태",
          "쉽게 자극받는 피부",
          "빈번한 트러블"
        ],
        solutions: [
          "집중 진정 케어",
          "자극 회피",
          "피부 장벽 강화"
        ]
      }
    },
    tension: {
      low: {
        range: "0-40%",
        characteristics: [
          "탄력이 부족한 상태",
          "피부가 처진 느낌",
          "주름이 눈에 띔"
        ],
        solutions: [
          "탄력 강화 제품 사용",
          "마사지 케어",
          "영양 공급"
        ]
      },
      medium: {
        range: "41-70%",
        characteristics: [
          "보통의 탄력도",
          "약간의 탄력 저하",
          "부분적 처짐"
        ],
        solutions: [
          "탄력 케어 유지",
          "가벼운 마사지",
          "수분 공급"
        ]
      },
      high: {
        range: "71-100%",
        characteristics: [
          "탄력있는 피부",
          "탱탱한 피부결",
          "건강한 피부"
        ],
        solutions: [
          "현재 상태 유지",
          "기본 케어 유지",
          "자외선 차단"
        ]
      }
    }
  };

  const getLevel = (value: number, type: string) => {
    if (type === 'sensitivity') {
      if (value <= 30) return 'low';
      if (value <= 60) return 'medium';
      return 'high';
    }
    if (value <= 40) return 'low';
    if (type === 'tension' ? value <= 70 : value <= 60) return 'medium';
    return 'high';
  };

  const level = getLevel(value, type);
  return contents[type as keyof typeof contents][level];
};

export default function ReportPage() {
  const router = useRouter();
  const [checklist, setChecklist] = useState<CheckListResponse | null>(null);
  const [historicalData, setHistoricalData] = useState<CheckListResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [mbti, setMbti] = useState<string>("default");
  const [skinInfo, setSkinInfo] = useState<SkinTypeInfo>(skinTypeInfoMap["default"]);
  const [selectedMeasurement, setSelectedMeasurement] = useState<string>("moisture");
  const [skinType, setSkinType] = useState<keyof typeof skinTypeAnalysis>('combination');
  const [userName, setUserName] = useState<string>("");

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
      return;
    }

    // 사용자 이름 가져오기
    fetch(`${apiConfig.endpoints.auth.me}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch user data');
        return res.json();
      })
      .then(data => {
        if (data && data.username) {
          setUserName(data.username);
        }
      })
      .catch(error => {
        console.error('Failed to fetch user name:', error);
      });

    // 체크리스트 데이터 가져오기
    fetch(apiConfig.endpoints.checklist.base, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.json() as Promise<CheckListResponse[]>;
      })
      .then(data => {
        if (data.length > 0) {
          setChecklist(data[0]);
          setHistoricalData(data);
        }
      })
      .catch(error => console.error('Failed to fetch checklist:', error));

    // MBTI 데이터 가져오기
    fetch(apiConfig.endpoints.checklist.mbti, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.text();
      })
      .then(data => {
        if (!data || data.trim() === '' || data === 'null') {
          setError('MBTI 결과가 없습니다.');
        } else {
          const mbtiResult = data.trim();
          setMbti(mbtiResult);
          setSkinInfo(skinTypeInfoMap[mbtiResult] || skinTypeInfoMap["default"]);

          // MBTI 결과에 따른 피부 타입 설정 - 메인 페이지와 동일하게 수정
          if (mbtiResult.startsWith('DB')) {
            setSkinType('dry');
          } else if (mbtiResult.startsWith('OB')) {
            setSkinType('oily');
          } else if (mbtiResult.startsWith('CB')) {
            setSkinType('combination');
          } else if (mbtiResult.startsWith('HB') || mbtiResult.startsWith('MO')) {
            setSkinType('dehydrated');
          } else {
            setSkinType('combination'); // 기본값
          }
        }
      })
      .catch(() => setError('MBTI를 불러오는 데 실패했습니다.'));
  }, []);

  const getCurrentCharacteristics = (measurement: keyof typeof measurementInfo) => {
    const value = checklist?.[measurement] ?? 0;
    const info = measurementInfo[measurement];
    const threshold = parseInt(info.ideal.match(/\d+/)?.[0] ?? "0");
    return value >= threshold ? info.highCharacteristics : info.lowCharacteristics;
  };

  return (
    <div className={styles.reportPage}>
      <div className={styles.reportContainer}>
        <div className={styles.reportHeader}>
          <h1>
            <span className={styles.userName}>
              {userName || 'Guest'}
            </span>
            님의 상세 리포트
          </h1>
          <div className={styles.headerInfo}>
            <div className={styles.skinTypeIndicator}>
              <span className={styles.skinTypeName}>{skinTypeAnalysis[skinType].name} 피부</span>
              <p className={styles.skinTypeDescription}>
                {skinType === 'dry' && "수분과 유분이 모두 부족한 건성 피부입니다. 충분한 보습 관리가 필요합니다."}
                {skinType === 'oily' && "유분이 많고 번들거리는 지성 피부입니다. 적절한 유분 조절이 필요합니다."}
                {skinType === 'combination' && "부위별로 다른 특성을 보이는 복합성 피부입니다. 맞춤 관리가 필요합니다."}
                {skinType === 'dehydrated' && "수분은 부족하고 유분은 많은 수분지 피부입니다. 수분 공급이 중요합니다."}
              </p>
            </div>
            <div className={styles.mbtiIndicator}>
              <span className={styles.mbtiType}>{mbti}</span>
              <p className={styles.mbtiDescription}>
                {mbtiDescriptionMap[mbti] || mbtiDescriptionMap["default"]}
              </p>
            </div>
          </div>
        </div>

        <div className={styles.analysisContainer}>
          <section className={`${styles.section} ${styles.measurementSection}`}>
            <h2>종합 분석 결과</h2>
            <div className={styles.measurementBars}>
              {[
                { key: 'moisture', label: '수분', value: checklist?.moisture ?? 0 },
                { key: 'oil', label: '유분', value: checklist?.oil ?? 0 },
                { key: 'sensitivity', label: '민감도', value: checklist?.sensitivity ?? 0 },
                { key: 'tension', label: '탄력도', value: checklist?.tension ?? 0 }
              ].map(item => (
                <div 
                  key={item.key} 
                  className={`${styles.measurementBar} ${selectedMeasurement === item.key ? styles.selected : ''}`}
                  onClick={() => setSelectedMeasurement(item.key)}
                >
                  <div className={styles.barLabel}>
                    <span>{item.label}</span>
                    <span>{item.value}%</span>
                  </div>
                  <div className={styles.barContainer}>
                    <div 
                      className={styles.barFill} 
                      style={{ width: `${item.value}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </section>

          <section className={`${styles.section} ${styles.detailSection}`}>
            <h2>상세 분석 결과</h2>
            <div className={styles.detailView}>
              <div className={styles.detailHeader}>
                <span className={styles.detailIcon}>{detailInfoMap[selectedMeasurement].icon}</span>
                <h3>{detailInfoMap[selectedMeasurement].title} 분석</h3>
              </div>
              <p className={styles.detailDescription}>
                {detailInfoMap[selectedMeasurement].description}
                <br />
                현재 수치: {checklist?.[selectedMeasurement as keyof CheckListResponse]}%
                <br />
                측정 범위: {getAnalysisContent(
                  selectedMeasurement,
                  Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0
                ).range}
              </p>
              <div className={styles.detailContent}>
                {getAnalysisContent(
                  selectedMeasurement,
                  Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0
                ).characteristics.map((item, index) => (
                  <div key={index} className={styles.characteristicItem}>
                    <span className={styles.characteristicNumber}>{index + 1}</span>
                    <p>{item}</p>
                  </div>
                ))}
              </div>
            </div>
          </section>
        </div>

        <section className={styles.section}>
          <h2>피부 타입 분석</h2>
          <div className={styles.skinTypeAnalysis}>
            <div className={styles.skinTypeHeader}>
              <h3>{skinTypeAnalysis[skinType].name} 피부</h3>
              <p className={styles.skinTypeDescription}>
                측정된 수분, 유분, 민감도를 종합적으로 분석한 결과입니다.
              </p>
            </div>
            <div className={styles.skinTypeContent}>
              <div className={styles.skinTypeSection}>
                <h4>피부 특징</h4>
                <ul>
                  {skinTypeAnalysis[skinType].characteristics.map((item, index) => (
                    <li key={index}>{item}</li>
                  ))}
                </ul>
              </div>
              <div className={styles.skinTypeSection}>
                <h4>추천 성분</h4>
                <ul>
                  {skinType === 'dry' && (
                    <>
                      <li>세라마이드 (보습력 강화)</li>
                      <li>히알루론산 (수분 공급)</li>
                      <li>스쿠알란 (보습 유지)</li>
                      <li>판테놀 (진정 효과)</li>
                    </>
                  )}
                  {skinType === 'oily' && (
                    <>
                      <li>살리실산 (각질 관리)</li>
                      <li>나이아신아마이드 (피지 조절)</li>
                      <li>징크 (진정 효과)</li>
                      <li>티트리 (트러블 케어)</li>
                    </>
                  )}
                  {skinType === 'combination' && (
                    <>
                      <li>판테놀 (진정 효과)</li>
                      <li>알로에베라 (수분 공급)</li>
                      <li>티트리 (트러블 케어)</li>
                      <li>히알루론산 (보습 균형)</li>
                    </>
                  )}
                  {skinType === 'dehydrated' && (
                    <>
                      <li>히알루론산 (수분 공급)</li>
                      <li>판테놀 (진정 효과)</li>
                      <li>베타인 (보습 강화)</li>
                      <li>세라마이드 (장벽 강화)</li>
                    </>
                  )}
                  {skinType === 'sensitive' && (
                    <>
                      <li>마데카소사이드 (진정)</li>
                      <li>판테놀 (진정 효과)</li>
                      <li>알란토인 (피부 재생)</li>
                      <li>세라마이드 (장벽 강화)</li>
                    </>
                  )}
                </ul>
              </div>
            </div>
          </div>
        </section>

        <section className={styles.section}>
          <h2>맞춤 관리법</h2>
          <div className={styles.careGuide}>
            <div className={styles.careSection}>
              <h3>일상 관리</h3>
              <ul>
                {skinType === 'dry' && (
                  <>
                    <li>미온수로 부드럽게 세안하기</li>
                    <li>보습 제품 덧바르기</li>
                    <li>실내 습도 50% 이상 유지</li>
                    <li>자극적인 성분 피하기</li>
                  </>
                )}
                {skinType === 'oily' && (
                  <>
                    <li>미지근한 물로 꼼꼼히 세안</li>
                    <li>과도한 유분 제거</li>
                    <li>모공 관리 주기적으로 하기</li>
                    <li>가벼운 수분 크림 사용</li>
                  </>
                )}
                {skinType === 'combination' && (
                  <>
                    <li>부위별 맞춤 세안법 적용</li>
                    <li>T존/U존 다른 제품 사용</li>
                    <li>수분 공급 꾸준히 하기</li>
                    <li>부위별 맞춤 마스크팩</li>
                  </>
                )}
                {skinType === 'dehydrated' && (
                  <>
                    <li>순한 클렌징으로 세안</li>
                    <li>수분 에센스 충분히 사용</li>
                    <li>유수분 밸런스 맞추기</li>
                    <li>수분 크림 덧바르기</li>
                  </>
                )}
                {skinType === 'sensitive' && (
                  <>
                    <li>자극없는 순한 세안</li>
                    <li>진정 케어 집중하기</li>
                    <li>자외선 차단 꼼꼼히</li>
                    <li>저자극 제품 사용</li>
                  </>
                )}
              </ul>
            </div>
            <div className={styles.careSection}>
              <h3>추천 제품 타입</h3>
              <ul>
                {skinType === 'dry' && (
                  <>
                    <li>크림형 클렌저</li>
                    <li>고보습 토너</li>
                    <li>영양 크림</li>
                    <li>보습 마스크</li>
                  </>
                )}
                {skinType === 'oily' && (
                  <>
                    <li>폼 클렌저</li>
                    <li>유분 조절 토너</li>
                    <li>가벼운 로션</li>
                    <li>클레이 마스크</li>
                  </>
                )}
                {skinType === 'combination' && (
                  <>
                    <li>약산성 클렌저</li>
                    <li>밸런싱 토너</li>
                    <li>수분/영양 크림</li>
                    <li>부위별 마스크</li>
                  </>
                )}
                {skinType === 'dehydrated' && (
                  <>
                    <li>저자극 클렌저</li>
                    <li>수분 에센스</li>
                    <li>수분 크림</li>
                    <li>진정 마스크</li>
                  </>
                )}
                {skinType === 'sensitive' && (
                  <>
                    <li>저자극 클렌저</li>
                    <li>진정 에센스</li>
                    <li>보습 크림</li>
                    <li>무기자차</li>
                  </>
                )}
              </ul>
            </div>
          </div>
        </section>

        <section className={styles.section}>
          <h2>피부 상태 변화 추이</h2>
          <div className={styles.graphContainer}>
            <ResponsiveContainer width="100%" height={400}>
              <LineChart
                data={historicalData.map(data => ({
                  date: new Date(data.createdAt).toLocaleDateString(),
                  moisture: data.moisture,
                  oil: data.oil,
                  sensitivity: data.sensitivity,
                  tension: data.tension
                })).reverse()}
                margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
              >
                <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
                <XAxis
                  dataKey="date"
                  tick={{ fill: '#666' }}
                  tickLine={{ stroke: '#666' }}
                />
                <YAxis
                  tick={{ fill: '#666' }}
                  tickLine={{ stroke: '#666' }}
                  domain={[0, 100]}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'white',
                    border: 'none',
                    borderRadius: '8px',
                    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                  }}
                />
                <Line
                  type="monotone"
                  dataKey="moisture"
                  name="수분"
                  stroke="#4FC3F7"
                  strokeWidth={2}
                  dot={{ fill: '#4FC3F7', r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="oil"
                  name="유분"
                  stroke="#FFB74D"
                  strokeWidth={2}
                  dot={{ fill: '#FFB74D', r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="sensitivity"
                  name="민감도"
                  stroke="#F06292"
                  strokeWidth={2}
                  dot={{ fill: '#F06292', r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="tension"
                  name="탄력"
                  stroke="#81C784"
                  strokeWidth={2}
                  dot={{ fill: '#81C784', r: 4 }}
                  activeDot={{ r: 6 }}
                />
              </LineChart>
            </ResponsiveContainer>
            <div className={styles.graphLegend}>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: '#4FC3F7' }}></span>
                <span>수분</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: '#FFB74D' }}></span>
                <span>유분</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: '#F06292' }}></span>
                <span>민감도</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: '#81C784' }}></span>
                <span>탄력</span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
} 