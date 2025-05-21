'use client';

import React, { useEffect, useState } from 'react';
import styles from "./page.module.css";
import Link from 'next/link';
import AnalysisBox from '@/components/AnalysisBox';
import Greeting from '@/components/Greeting';

// 서버가 내려주는 타입 (영문 키)
interface CheckListResponse {
  id: number;
  moisture: number;     // 수분
  oil: number;          // 유분
  sensitivity: number;  // 민감도
  tension: number;      // 탄력
  createdAt: string;
}

// 분석·제품 정보 (예시)
const analysis = {
  type: "민감형",
  description: "당신의 피부는 외부 자극에 민감하게 반응하는 타입입니다.",
  advice: "자극이 적은 제품을 사용해보세요!",
};
const products = [
  { name: "수분", description: "진정효과 수분공급 민감피부용 에센스" },
  { name: "진정", description: "피부 진정케어 세럼 민감 피부용" },
  { name: "보습", description: "저자극 수분 크림 민감 피부용" },
];

export default function Home() {
  const [checklist, setChecklist] = useState<CheckListResponse | null>(null);
  const [error, setError]         = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    fetch('http://localhost:8080/api/checklist', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.json() as Promise<CheckListResponse[]>;
      })
      .then(data => {
        if (data.length === 0) {
          setError('저장된 체크리스트가 없습니다.');
        } else {
          setChecklist(data[0]);  // 최신 결과
        }
      })
      .catch(() => setError('체크리스트를 불러오는 데 실패했습니다.'));
  }, []);

  if (error) {
    return <div className={styles.page}><p className={styles.error}>{error}</p></div>;
  }
  if (!checklist) {
    return <div className={styles.page}>로딩 중…</div>;
  }

  // 한글 레이블 매핑
  const labels = {
    moisture:    '수분',
    oil:         '유분',
    sensitivity: '민감도',
    tension:     '탄력',
  } as const;

  // 바 색상 매핑
  const barClasses = {
    moisture:    styles.barGold,
    oil:         styles.barGoldLight,
    sensitivity: styles.barRed,
    tension:     styles.barGray,
  } as const;

  return (
    <div className={styles.wrapper}>
      {/* 사이드바 */}
      <aside className={styles.sidebar}>
        <h2 className={styles.logo}>스킨케어</h2>
        <nav>
          <ul className={styles.menu}>
            <li className={styles.menuActive}>홈화면</li>
            <li><Link href="/checklist">검사하기</Link></li>
            <li>AI 채팅</li>
            <li>회원정보</li>
          </ul>
        </nav>
      </aside>

      {/* 메인 */}
      <main className={styles.mainContent}>
        <Greeting />
        <section className={styles.resultSection}>
          {/* 체크리스트 결과 */}
          <div className={styles.checklistBox}>
            <h3>체크리스트 결과</h3>
            <div className={styles.barWrap}>
              {(Object.keys(labels) as (keyof typeof labels)[])
                .map(key => {
                  const value = checklist[key];
                  return (
                    <React.Fragment key={key}>
                      <div>
                        {labels[key]} <span>{value}%</span>
                      </div>
                      <div className={styles.bar}>
                        <div
                          className={barClasses[key]}
                          style={{ width: `${value}%` }}
                        />
                      </div>
                    </React.Fragment>
                  );
                })
              }
            </div>
          </div>
           <AnalysisBox checklist={checklist} />
        </section>
        {/* 추천 제품 */}
        <section className={styles.productSection}>
          <h4>회원님에게 어떤 제품이 좋을까요?</h4>
          <div className={styles.productList}>
            {products.map((p, i) => (
              <div key={i} className={styles.productCard}>
                <div className={styles.productImg}></div>
                <div className={styles.productName}>{p.name}</div>
                <div className={styles.productDesc}>{p.description}</div>
                <button className={styles.buyBtn}>자세히 보기</button>
              </div>
            ))}
          </div>
        </section>
        {/* 더 자세한 추천 */}
        <section className={styles.recommendSection}>
          <div className={styles.recommendTitle}>더 자세한 추천을 원한다면?</div>
          <button className={styles.goBtn}>검사 받으러 GO!</button>
          <div className={styles.concernTitle}>고민별 추천</div>
          <div className={styles.concernDesc}>어떤 부분을 개선하고 싶으신가요?</div>
          <div className={styles.concernList}>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>😅</div>
              <div className={styles.concernLabel}>유분</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>😬</div>
              <div className={styles.concernLabel}>여드름</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥲</div>
              <div className={styles.concernLabel}>건조</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥵</div>
              <div className={styles.concernLabel}>열감</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥹</div>
              <div className={styles.concernLabel}>홍조</div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}