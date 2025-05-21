// src/components/AnalysisBox.tsx
'use client';

import React, { useMemo } from 'react';
import pageStyles from '../app/page.module.css'; // ← use your page.module.css

export interface CheckListResponse {
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
}

interface Analysis {
  type: string;
  description: string;
  advice: string;
}

interface Props {
  checklist: CheckListResponse;
}

export default function AnalysisBox({ checklist }: Props) {
  const analysis = useMemo<Analysis>(() => {
    const c = checklist;
    const B = c.moisture >= 60 ? 'B' : 'T';
    const C = c.sensitivity < 60 ? 'C' : 'A';
    const D = c.oil >= 60 ? 'D' : 'O';
    const E = (c.tension < 60 || c.sensitivity >= 60) ? 'S' : 'R';
    const code = `${B}${C}${D}${E}`;

    const map: Record<string, Analysis> = {
      BCDS: {
        type: '수분충만·민감낮음·건조·스트레스',
        description: '수분은 충분하지만 환경 스트레스가 높아 피부 보호막이 약할 수 있어요.',
        advice: '항산화·보습 제품과 자외선 차단을 꼭 병행하세요!'
      },
      BCDR: {
        type: '수분충만·민감낮음·건조·안정',
        description: '수분도 많고 환경 스트레스도 낮아 균형 잡힌 피부 상태입니다.',
        advice: '기본 보습·자외선 차단 루틴을 그대로 유지하세요.'
      },
      BCOS: {
        type: '수분충만·민감낮음·유분·스트레스',
        description: '유분 과잉과 스트레스 환경이 결합되어 모공·트러블 케어가 필요해요.',
        advice: '가벼운 유수분 밸런스 제품과 항염·항산화 관리 병행을 권합니다.'
     },
  // 4. B C O R
      BCOR: {
       type: '수분충만·민감낮음·유분·안정',
    description: '수분·유분 밸런스가 잘 잡히고 환경 스트레스도 낮은 상태입니다.',
    advice: '수분-유분 균형 제품과 규칙적 세안 루틴을 유지하세요.'
  },

  // 5. B A D S
  BADS: {
    type: '수분충만·민감높음·건조·스트레스',
    description: '수분은 충분하지만 민감도가 높고 스트레스 환경이 겹쳐 자극에 취약해요.',
    advice: '저자극 보습·진정 제품과 함께 자외선 차단을 반드시 해주세요.'
  },
  // 6. B A D R
  BADR: {
    type: '수분충만·민감높음·건조·안정',
    description: '수분은 충분하나 민감도가 높아 약한 자극에도 트러블이 생길 수 있어요.',
    advice: '진정·장벽 강화 제품을 사용하고, 물리적 자극을 최소화하세요.'
  },
  // 7. B A O S
  BAOS: {
    type: '수분충만·민감높음·유분·스트레스',
    description: '유분 과잉과 민감 높음, 환경 스트레스까지 겹쳐 트러블 위험이 높아요.',
    advice: '저자극 모공 케어·진정·항염 제품으로 피부 진정을 우선하세요.'
  },
  // 8. B A O R
  BAOR: {
    type: '수분충만·민감높음·유분·안정',
    description: '유분과 민감도가 높지만, 환경 스트레스는 낮아 안정적인 편입니다.',
    advice: '모공 밸런싱·진정 제품을 사용해 유분 과잉을 조절하세요.'
  },

  // 9. T C D S
  TCDS: {
    type: '수분부족·민감낮음·건조·스트레스',
    description: '수분 부족·건조함에 스트레스 환경까지 겹쳐 피부 장벽이 약해요.',
    advice: '고보습·장벽 강화 및 항산화 제품을 집중적으로 사용하세요.'
  },
  // 10. T C D R
  TCDR: {
    type: '수분부족·민감낮음·건조·안정',
    description: '건조함은 있으나 민감도·환경 스트레스는 낮아 관리가 수월해요.',
    advice: '집중 보습 크림과 규칙적 수분 공급 루틴을 유지하세요.'
  },
  // 11. T C O S
  TCOS: {
    type: '수분부족·민감낮음·유분·스트레스',
    description: '건조함과 유분 과잉이 공존하며 스트레스 환경으로 인한 트러블 가능성이 있어요.',
    advice: '유수분 밸런싱 제품과 항염 관리, 가벼운 수분 세럼을 병행하세요.'
  },
  // 12. T C O R
  TCOR: {
    type: '수분부족·민감낮음·유분·안정',
    description: '유분은 과하지만 민감도·스트레스는 낮아 비교적 관리가 편한 편입니다.',
    advice: '유수분 균형 제품과 토너·에센스로 수분을 채우세요.'
  },

  // 13. T A D S
  TADS: {
    type: '수분부족·민감높음·건조·스트레스',
    description: '건조·민감·환경 스트레스가 모두 겹쳐 피부 장벽이 크게 손상돼있어요.',
    advice: '장벽 강화·저자극·고보습 제품을 집중적으로 사용하세요.'
  },
  // 14. T A D R
  TADR: {
    type: '수분부족·민감높음·건조·안정',
    description: '건조·민감은 높으나 환경 스트레스는 적어, 자극 관리를 우선하세요.',
    advice: '진정·장벽 강화 세럼과 리치 크림으로 피부를 진정시키세요.'
  },
  // 15. T A O S
  TAOS: {
    type: '수분부족·민감높음·유분·스트레스',
    description: '유분·민감·스트레스가 모두 높아 트러블과 자극 반응이 잦아요.',
    advice: '모공 클리어·진정·장벽 강화 제품을 함께 사용하세요.'
  },
  // 16. T A O R
  TAOR: {
    type: '수분부족·민감높음·유분·안정',
    description: '유분·민감이 높으나 환경은 안정적이어서, 자극 관리와 밸런싱이 필요합니다.',
    advice: '저자극 유수분 밸런스 크림과 진정 세럼을 병행하세요.'
  },

  // 기본값
  default: {
    type: '표준형',
    description: '피부 균형이 잘 잡힌 상태입니다.',
    advice: '기본 보습과 자외선 차단 루틴을 꾸준히 지켜주세요.'
  }
};

    return map[code] ?? map.default;
  }, [checklist]);

  return (
    <div className={pageStyles.analysisBox}>
      <h3>분석 결과</h3>
      <div className={pageStyles.analysisType}>{analysis.type}</div>
      <div className={pageStyles.analysisDesc}>{analysis.description}</div>
      <div className={pageStyles.analysisAdvice}>{analysis.advice}</div>
    </div>
  );
}



