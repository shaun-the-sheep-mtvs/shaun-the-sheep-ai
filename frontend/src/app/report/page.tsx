'use client';

import React, { useEffect, useState } from 'react';
import styles from './page.module.css';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import Image from 'next/image';
import { Info, Home, BarChart3, Droplet, Shield, Lightbulb } from 'lucide-react';

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

type MeasurementType = 'moisture' | 'oil' | 'sensitivity' | 'tension';

interface MeasurementThreshold {
  good: number;
  caution: number;
}

interface MeasurementDescription {
  good: string;
  normal: string;
  caution: string;
}

interface MeasurementStatus {
  status: '좋음' | '보통' | '주의' | '충분' | '부족';
  color: string;
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
      "미세주름이 생기기 쉬움",
      "가끔씩 유발하는 간지러움"
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

const mbtiList = {
  "MOST": {
    "type": "민감성",
    "description": "수분과 유분이 충분하며 피부 탄력이 좋아 건강한 상태입니다.",
    "advice": "기본 보습·자외선 차단 루틴을 유지하고, 콜라겐·펩타이드 성분으로 탄력을 계속 관리하세요."
  },
  "MOSL": {
    "type": "민감성",
    "description": "수분과 유분은 충분하지만 탄력이 떨어지고 자극에 민감할 수 있어요.",
    "advice": "저자극 탄력 강화 세럼을 사용하고, 충분한 수분과 영양 공급을 해주세요."
  },
  "MBST": {
    "type": "민감성",
    "description": "수분은 풍부하나 유분이 부족해 건조함이 느껴질 수 있으며, 탄력은 좋은 편입니다.",
    "advice": "부드러운 오일 제품으로 유수분 균형을 맞추고, 탄력 증진 크림을 병행하세요."
  },
  "MBSL": {
    "type": "민감성",
    "description": "수분은 충분하지만 유분과 탄력 모두 부족해 피부가 당기고 민감해질 수 있어요.",
    "advice": "고보습·장벽 강화 오일·세럼으로 영양을 채우고, 탄력 케어를 병행하세요."
  },
  "MOIT": {
    "type": "지성",
    "description": "수분·유분·탄력 모두 좋은 균형 상태로, 안정적인 편입니다.",
    "advice": "기본 보습·자외선 차단 루틴을 유지하며, 탄력 유지 제품을 가볍게 사용하세요."
  },
  "MOIL": {
    "type": "지성",
    "description": "수분과 유분은 충분하나 탄력만 떨어져 피부 처짐이 느껴질 수 있어요.",
    "advice": "탄력 강화 세럼과 마사지로 리프팅 관리하세요."
  },
  "MBIT": {
    "type": "복합성",
    "description": "수분은 충분하나 유분이 부족해 건조함이 느껴지며, 탄력은 좋은 상태입니다.",
    "advice": "보습 오일·크림으로 유수분 밸런스를 맞추고, 탄력 유지를 위해 펩타이드 제품을 사용하세요."
  },
  "MBIL": {
    "type": "복합성",
    "description": "수분은 충분하지만 유분·탄력 모두 부족해 당김과 처짐이 동시에 나타날 수 있어요.",
    "advice": "고보습·탄력 강화 오일 세럼을 집중적으로 사용하세요."
  },
  "DOST": {
    "type": "수분부족지성",
    "description": "유분과 탄력은 좋으나 수분이 부족해 민감 반응이 나타날 수 있어요.",
    "advice": "저자극 수분 세럼과 마스크로 수분을 보충하고, 탄력 유지 루틴을 병행하세요."
  },
  "DOSL": {
    "type": "수분부족지성",
    "description": "수분·탄력 모두 부족해 처짐이 느껴지며 자극에도 민감해요.",
    "advice": "고보습·탄력 강화 크림과 진정 세럼을 동시에 사용하세요."
  },
  "DBST": {
    "type": "건성",
    "description": "수분·유분 부족으로 건조함이 심하나 탄력은 유지되고 있어요.",
    "advice": "고보습 크림과 오일로 영양을 채우고, 탄력 유지 제품을 함께 사용하세요."
  },
  "DBSL": {
    "type": "건성",
    "description": "수분·유분·탄력이 모두 부족해 피부가 거칠고 처짐이 심할 것으로 예상됩니다다.",
    "advice": "장벽 강화·고보습·탄력 케어 제품을 함께 사용하여 집중 관리하세요."
  },
  "DOIT": {
    "type": "수분부족지성",
    "description": "유분과 탄력은 좋으나 수분이 부족해 당김이 느껴져요.",
    "advice": "수분 에센스와 마스크로 즉각적인 수분을 보충하세요."
  },
  "DOIL": {
    "type": "수분부족지성",
    "description": "유분은 충분하나 수분·탄력 모두 부족해 피부가 당기고 처짐이 느껴져요.",
    "advice": "고보습 세럼과 탄력 강화 오일을 함께 사용하세요."
  },
  "DBIT": {
    "type": "건성",
    "description": "수분·유분 부족으로 건조함이 있지만 탄력은 유지되고 있어요.",
    "advice": "보습 크림과 수분 세럼으로 피부 결을 개선하세요."
  },
  "DBIL": {
    "type": "건성",
    "description": "수분·유분·탄력이 모두 부족해 피부가 건조하고 처짐이 심할 것으로 예상됩니다.",
    "advice": "고보습·탄력 강화 루틴을 집중적으로 적용하세요."
  },
  "default": {
    "type": "복합성",
    "description": "피부 균형이 잘 잡힌 상태입니다.",
    "advice": "기본 보습과 탄력 관리 루틴을 꾸준히 지켜주세요."
  }
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

const measurementColors = {
  moisture: '#3b82f6', // 수분 - 바뀐 색상 (barGoldLight)
  oil: '#f59e0b',     // 유분 - 바뀐 색상 (barGray)
  sensitivity: '#ef4444', // 민감도 - 바뀐 색상 (barRed)
  tension: '#10b981'    // 탄력 - 바뀐 색상 (barGold)
};

const getMeasurementStatus = (type: MeasurementType, value: number): MeasurementStatus => {
  const thresholds: Record<MeasurementType, MeasurementThreshold> = {
    moisture: { good: 60, caution: 40 },
    oil: { good: 50, caution: 30 },
    sensitivity: { good: 30, caution: 50 },
    tension: { good: 70, caution: 50 }
  };

  const threshold = thresholds[type];
  
  if (type === 'sensitivity') {
    // 민감도는 낮을수록 좋음
    if (value <= threshold.good) return { status: '좋음', color: '#4CAF50' };
    if (value <= threshold.caution) return { status: '보통', color: '#FFC107' };
    return { status: '주의', color: '#F44336' };
  } else if (type === 'moisture' || type === 'oil') {
    // 수분과 유분은 부족/보통/충분으로 표시
    if (value >= threshold.good) return { status: '충분', color: '#4CAF50' };
    if (value >= threshold.caution) return { status: '보통', color: '#FFC107' };
    return { status: '부족', color: '#F44336' };
  } else {
    // 나머지는 높을수록 좋음
    if (value >= threshold.good) return { status: '좋음', color: '#4CAF50' };
    if (value >= threshold.caution) return { status: '보통', color: '#FFC107' };
    return { status: '주의', color: '#F44336' };
  }
};

const getStatusDescription = (type: MeasurementType, value: number) => {
  const status = getMeasurementStatus(type, value);
  const descriptions: Record<MeasurementType, MeasurementDescription> = {
    moisture: {
      good: "피부가 충분한 수분을 가지고 있어 건강한 상태입니다. 피부 장벽이 튼튼하게 유지되어 외부 자극으로부터 피부를 보호하고 있습니다. 피부 결이 매끄럽고 탄력이 있으며, 메이크업이 자연스럽게 발립니다. 각질이 잘 일어나지 않고 피부 톤이 밝고 건강한 광채를 냅니다.",
      normal: "수분이 약간 부족한 상태입니다. 피부가 가끔 당기는 느낌이 있고, 각질이 간헐적으로 발생할 수 있습니다. 메이크업이 부분적으로 들뜨는 현상이 나타나며, 피부 톤이 다소 칙칙해 보일 수 있습니다. 보습 관리가 필요한 상태입니다.",
      caution: "피부가 심하게 건조한 상태입니다. 피부 장벽이 약화되어 외부 자극에 취약한 상태이며, 각질이 자주 발생하고 피부가 거칠어집니다. 메이크업이 들뜨고 가루가 일어나며, 피부 톤이 칙칙하고 피로해 보입니다. 집중적인 보습 관리가 시급한 상태입니다."
    },
    oil: {
      good: "피부의 유분이 적절한 상태입니다. 피부 장벽이 건강하게 유지되어 수분 증발을 막고 피부를 보호하고 있습니다. 피부가 촉촉하고 윤기가 나며, 모공이 깨끗하게 유지됩니다. 피부 톤이 균일하고 건강한 광채를 냅니다.",
      normal: "유분이 약간 부족하거나 과다한 상태입니다. T존이 번들거리거나 반대로 건조한 느낌이 있을 수 있으며, 모공이 약간 확장되어 보입니다. 피부 톤이 불균일하고 피부 결이 다소 거칠어 보일 수 있습니다. 유분 조절이 필요한 상태입니다.",
      caution: "유분이 심하게 부족하거나 과다한 상태입니다. 피부 장벽이 불안정하여 트러블이 자주 발생하고, 모공이 크게 확장되어 있습니다. 피부가 번들거리거나 매우 건조하며, 여드름이나 염증이 쉽게 생길 수 있습니다. 적절한 유분 조절이 시급한 상태입니다."
    },
    sensitivity: {
      good: "피부가 안정적이고 건강한 상태입니다. 피부 장벽이 튼튼하게 유지되어 외부 자극에 강한 상태입니다. 제품 교체나 환경 변화에도 피부가 잘 적응하며, 트러블이 잘 생기지 않습니다. 피부가 매끄럽고 건강한 광채를 냅니다.",
      normal: "피부가 약간 민감한 상태입니다. 자극성 제품 사용 시 따끔거림이 있고, 날씨 변화나 환경 변화에 피부가 반응할 수 있습니다. 가끔 붉은 기가 올라오거나 가려움증이 발생할 수 있습니다. 진정 케어가 필요한 상태입니다.",
      caution: "피부가 매우 민감한 상태입니다. 피부 장벽이 약화되어 외부 자극에 과민하게 반응합니다. 제품 사용 시 따끔거림과 붉은 기가 심하고, 트러블이 자주 발생합니다. 피부가 쉽게 자극받고 회복이 느리며, 가려움증이 지속될 수 있습니다. 집중적인 진정 케어가 시급한 상태입니다."
    },
    tension: {
      good: "피부 탄력이 좋은 상태입니다. 콜라겐과 엘라스틴이 충분히 유지되어 피부가 탄탄하고 단단합니다. 주름이 거의 없고 피부 결이 매끄럽습니다. 피부가 촉촉하고 건강한 광채를 냅니다.",
      normal: "피부 탄력이 약간 저하된 상태입니다. 미세한 주름이 보이고 피부가 약간 처지는 느낌이 있습니다. 피부 톤이 다소 칙칙해 보이며, 피부 결이 다소 거칠어 보일 수 있습니다. 탄력 케어가 필요한 상태입니다.",
      caution: "피부 탄력이 많이 저하된 상태입니다. 콜라겐과 엘라스틴이 부족하여 주름이 뚜렷하게 보이고 피부가 처졌습니다. 피부 톤이 칙칙하고 피로해 보이며, 피부 결이 거칠어졌습니다. 집중적인 탄력 케어가 시급한 상태입니다."
    }
  };

  const descriptionKey = status.status === '좋음' ? 'good' : 
                        status.status === '보통' ? 'normal' : 'caution';
  
  return descriptions[type][descriptionKey];
};

export default function ReportPage() {
  const router = useRouter();
  const [checklist, setChecklist] = useState<CheckListResponse | null>(null);
  const [historicalData, setHistoricalData] = useState<CheckListResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [mbti, setMbti] = useState<string>("default");
  const [skinInfo, setSkinInfo] = useState<SkinTypeInfo>(skinTypeInfoMap["default"]);
  const [selectedMeasurement, setSelectedMeasurement] = useState<MeasurementType>("moisture");
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
    <div className={styles.wrapper}>
      <div className={styles.reportContainer}>
        <div className={styles.reportHeader}>
          <button 
            className={styles.homeButton}
            onClick={() => router.push('/')}
          >
            <Home size={18} />
            <span>홈으로</span>
          </button>
          <h1 className={styles.reportTitle}>
            <span className={styles.userName}>
              {userName || 'Guest'}
            </span>
            님의 상세 피부 리포트
          </h1>
          <div className={styles.headerInfo}>
            <div className={styles.skinTypeIndicator}>
              <span className={styles.skinTypeName}>
                {mbtiList[mbti as keyof typeof mbtiList]?.type || '일반'} 피부
              </span>
              <p className={styles.skinTypeDescription}>
                {mbtiList[mbti as keyof typeof mbtiList]?.description || '피부 상태를 분석해주세요.'}
              </p>
            </div>
            <div className={styles.mbtiIndicator}>
              <span className={styles.mbtiType}>{mbti}</span>
              <p className={styles.mbtiDescription}>
                {mbtiList[mbti as keyof typeof mbtiList]?.advice || '피부 상태를 분석해주세요.'}
              </p>
            </div>
          </div>
        </div>

        <div className={styles.analysisContainer}>
          <section className={`${styles.pageSection} ${styles.measurementSection}`}>
            <div className={styles.sectionHeader}>
              <h2 className={styles.sectionMainTitle}>종합 분석 결과</h2>
              <p className={styles.sectionSubtitle}>
                피부 상태를 4가지 주요 지표로 분석한 결과입니다
              </p>
            </div>
            <div className={styles.measurementBars}>
              {[
                { key: 'moisture', label: '수분', value: checklist?.moisture ?? 0, icon: <Droplet size={18} /> },
                { key: 'oil', label: '유분', value: checklist?.oil ?? 0, icon: <Shield size={18} /> },
                { key: 'sensitivity', label: '민감도', value: checklist?.sensitivity ?? 0, icon: <Info size={18} /> },
                { key: 'tension', label: '탄력도', value: checklist?.tension ?? 0, icon: <Lightbulb size={18} /> }
              ].map(item => {
                const status = getMeasurementStatus(item.key as MeasurementType, item.value);
                return (
                  <div 
                    key={item.key} 
                    className={`${styles.measurementBar} ${selectedMeasurement === item.key ? styles.selected : ''}`}
                    onClick={() => setSelectedMeasurement(item.key as MeasurementType)}
                  >
                    <div className={styles.barLabel}>
                      <div className={styles.labelContent}>
                        <span className={styles.labelIcon} style={{ color: measurementColors[item.key as keyof typeof measurementColors] }}>
                          {item.icon}
                        </span>
                        <span>{item.label}</span>
                      </div>
                      <div className={styles.valueContainer}>
                        <span className={styles.value}>{item.value}%</span>
                        <span 
                          className={styles.statusIndicator}
                          style={{ 
                            backgroundColor: status.color,
                            color: '#fff'
                          }}
                        >
                          {status.status}
                        </span>
                      </div>
                    </div>
                    <div className={styles.barContainer}>
                      <div 
                        className={styles.barFill} 
                        style={{ 
                          width: `${item.value}%`,
                          backgroundColor: measurementColors[item.key as keyof typeof measurementColors]
                        }}
                      />
                    </div>
                    <div className={styles.measurementDescription}>
                      <p className={styles.idealRange}>
                        {measurementInfo[item.key as keyof typeof measurementInfo].ideal}
                      </p>
                      <p className={styles.description}>
                        {measurementInfo[item.key as keyof typeof measurementInfo].description}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </section>

          <section className={`${styles.pageSection} ${styles.detailSection}`}>
            <div className={styles.sectionHeader}>
              <h2 className={styles.sectionMainTitle}>상세 분석 결과</h2>
              <p className={styles.sectionSubtitle}>
                선택한 지표에 대한 상세 분석 결과입니다
              </p>
            </div>
            <div className={styles.detailView}>
              <div 
                className={styles.detailHeader}
                style={{ 
                  backgroundColor: `${measurementColors[selectedMeasurement]}15`,
                  borderLeft: `4px solid ${measurementColors[selectedMeasurement]}`
                }}
              >
                <span className={styles.detailIcon} style={{ color: measurementColors[selectedMeasurement] }}>
                  {selectedMeasurement === 'moisture' && <Droplet size={22} />}
                  {selectedMeasurement === 'oil' && <Shield size={22} />}
                  {selectedMeasurement === 'sensitivity' && <Info size={22} />}
                  {selectedMeasurement === 'tension' && <Lightbulb size={22} />}
                </span>
                <div className={styles.detailTitleContainer}>
                  <h3 style={{ color: measurementColors[selectedMeasurement] }}>
                    {detailInfoMap[selectedMeasurement].title} 분석
                  </h3>
                  <span 
                    className={styles.statusBadge}
                    style={{ 
                      backgroundColor: getMeasurementStatus(selectedMeasurement, Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0).color,
                      color: '#fff'
                    }}
                  >
                    {getMeasurementStatus(selectedMeasurement, Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0).status}
                  </span>
                </div>
              </div>
              <div className={styles.detailContent}>
                <div className={styles.detailSection}>
                  <h4>현재 상태</h4>
                  <p className={styles.detailDescription}>
                    {getStatusDescription(selectedMeasurement, Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0)}
                  </p>
                </div>
                <div className={styles.detailSection}>
                  <h4>주요 특징</h4>
                  <div className={styles.characteristicsList}>
                    {getAnalysisContent(
                      selectedMeasurement,
                      Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0
                    ).characteristics.map((item, index) => (
                      <div 
                        key={index} 
                        className={styles.characteristicItem}
                        style={{ 
                          borderLeft: `3px solid ${measurementColors[selectedMeasurement]}`,
                          backgroundColor: `${measurementColors[selectedMeasurement]}08`
                        }}
                      >
                        <span 
                          className={styles.characteristicNumber}
                          style={{ backgroundColor: measurementColors[selectedMeasurement] }}
                        >
                          {index + 1}
                        </span>
                        <p>{item}</p>
                      </div>
                    ))}
                  </div>
                </div>
                <div className={styles.detailSection}>
                  <h4>개선 방안</h4>
                  <div className={styles.solutionsList}>
                    {getAnalysisContent(
                      selectedMeasurement,
                      Number(checklist?.[selectedMeasurement as keyof CheckListResponse]) || 0
                    ).solutions.map((item, index) => (
                      <div 
                        key={index} 
                        className={styles.solutionItem}
                      >
                        <span 
                          className={styles.solutionNumber}
                          style={{ backgroundColor: measurementColors[selectedMeasurement] }}
                        >
                          {index + 1}
                        </span>
                        <p>{item}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>

        <section className={`${styles.pageSection} ${styles.skinTypeSection}`}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionMainTitle}>종합 피부 분석</h2>
            <p className={styles.sectionSubtitle}>
              피부 타입별 특징과 관리법 가이드
            </p>
          </div>
          <div className={styles.skinTypeAnalysis}>
            <div className={styles.skinTypeHeader}>
              <h3>{mbtiList[mbti as keyof typeof mbtiList]?.type || '일반'} 피부에 대해서</h3>
              <p className={styles.skinTypeDescription}>
                {mbtiList[mbti as keyof typeof mbtiList]?.description || '피부 상태를 분석해주세요.'}
              </p>
            </div>
            <div className={styles.skinTypeContent}>
              <div className={styles.skinTypeSection}>
                <h4>피부 특징</h4>
                <ul className={styles.featureList}>
                  {skinTypeAnalysis[skinType].characteristics.map((item, index) => (
                    <li key={index}>{item}</li>
                  ))}
                </ul>
              </div>
              <div className={styles.skinTypeSection}>
                <h4>추천 성분</h4>
                <ul className={styles.ingredientList}>
                  {skinType === 'dry' && (
                    <>
                      <li>
                        <span className={styles.ingredientName}>세라마이드</span>
                        <div className={styles.ingredientInfo}>
                          <Info size={16} />
                          <span>피부 장벽을 강화하고 수분 손실을 방지하는 필수 지질 성분</span>
                        </div>
                      </li>
                      <li>
                        <span className={styles.ingredientName}>히알루론산</span>
                        <div className={styles.ingredientInfo}>
                          <Info size={16} />
                          <span>피부에 수분을 공급하고 보습력을 높이는 천연 보습 성분</span>
                        </div>
                      </li>
                      <li>
                        <span className={styles.ingredientName}>스쿠알란</span>
                        <div className={styles.ingredientInfo}>
                          <Info size={16} />
                          <span>피부에 수분을 공급하고 보습력을 높이는 천연 보습 성분</span>
                        </div>
                      </li>
                      <li>
                        <span className={styles.ingredientName}>판테놀</span>
                        <div className={styles.ingredientInfo}>
                          <Info size={16} />
                          <span>피부 진정과 재생을 돕는 비타민 B5 유도체</span>
                        </div>
                      </li>
                    </>
                  )}
                  {/* 나머지 스킨 타입별 추천 성분 유지 */}
                </ul>
              </div>
            </div>
          </div>
        </section>

        <section className={`${styles.pageSection} ${styles.careSection}`}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionMainTitle}>종합 맞춤 관리법</h2>
            <p className={styles.sectionSubtitle}>
              피부 타입에 맞는 일상 관리와 제품 추천
            </p>
          </div>
          <div className={styles.careGuide}>
            <div className={styles.careGuideSection}>
              <h3>일상 관리</h3>
              <ul className={styles.careList}>
                {skinType === 'dry' && (
                  <>
                    <li>미온수로 부드럽게 세안하기</li>
                    <li>보습 제품 덧바르기</li>
                    <li>수분감 있는 제품 사용</li>
                    <li>주 1회 수분팩</li>
                    <li>수분 섭취 (하루1.5L~2L)</li>
                    <li>자극적인 성분 피하기</li>
                    <li>아침 물세안 추천</li>
                  </>
                )}
                {/* 나머지 스킨 타입별 관리법 유지 */}
              </ul>
            </div>
            <div className={styles.careGuideSection}>
              <h3>추천 제품 타입</h3>
              <ul className={styles.productList}>
                {skinType === 'dry' && (
                  <>
                    <li>크림형 클렌저</li>
                    <li>고보습 토너</li>
                    <li>영양 크림</li>
                    <li>보습 마스크</li>
                  </>
                )}
                {/* 나머지 스킨 타입별 제품 추천 유지 */}
              </ul>
            </div>
          </div>
        </section>

        <section className={`${styles.pageSection} ${styles.chartSection}`}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionMainTitle}>피부 상태 변화 추이</h2>
            <p className={styles.sectionSubtitle}>
              시간에 따른 피부 지표 변화를 확인하세요
            </p>
          </div>
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
                  stroke={measurementColors.moisture}
                  strokeWidth={2}
                  dot={{ fill: measurementColors.moisture, r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="oil"
                  name="유분"
                  stroke={measurementColors.oil}
                  strokeWidth={2}
                  dot={{ fill: measurementColors.oil, r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="sensitivity"
                  name="민감도"
                  stroke={measurementColors.sensitivity}
                  strokeWidth={2}
                  dot={{ fill: measurementColors.sensitivity, r: 4 }}
                  activeDot={{ r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="tension"
                  name="탄력"
                  stroke={measurementColors.tension}
                  strokeWidth={2}
                  dot={{ fill: measurementColors.tension, r: 4 }}
                  activeDot={{ r: 6 }}
                />
              </LineChart>
            </ResponsiveContainer>
            <div className={styles.graphLegend}>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: measurementColors.moisture }}></span>
                <span>수분</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: measurementColors.oil }}></span>
                <span>유분</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: measurementColors.sensitivity }}></span>
                <span>민감도</span>
              </div>
              <div className={styles.legendItem}>
                <span className={styles.legendColor} style={{ backgroundColor: measurementColors.tension }}></span>
                <span>탄력</span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
} 