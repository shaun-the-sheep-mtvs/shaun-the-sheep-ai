"use client";

import React, { useState, useEffect } from "react";
'use client';

import React, { useEffect, useState } from 'react';
import styles from "./page.module.css";
import Link from 'next/link';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X } from "lucide-react";
import { usePathname } from 'next/navigation';
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
  { name: "수분 에센스", description: "진정효과 수분공급 민감피부용 에센스", category: "수분" },
  { name: "진정 세럼", description: "피부 진정케어 세럼 민감 피부용", category: "진정" },
  { name: "보습 크림", description: "저자극 수분 크림 민감 피부용", category: "보습" },
];

export default function Home() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();

  // 사이드바 상태가 변경될 때 body 스크롤 제어
  useEffect(() => {
    if (typeof document !== 'undefined') {
      document.body.style.overflow = isSidebarOpen ? 'hidden' : '';
    }
  }, [isSidebarOpen]);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  // 사이드바 외부 클릭시 닫기
  const handleOverlayClick = () => {
    setIsSidebarOpen(false);
  };

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
      {/* 네비게이션 바 */}
      <nav className={styles.navbar}>
        <button className={styles.mobileMenuToggle} onClick={toggleSidebar}>
          {isSidebarOpen ? <X className={styles.menuToggleIcon} /> : <Menu className={styles.menuToggleIcon} />}
        </button>
        <div className={styles.logoContainer}>
          <h1 className={styles.logo}>Shaun</h1>
        </div>

        <div className={styles.navRight}>
          <button className={styles.authButton}>회원가입</button>
          <button className={styles.loginButton}>로그인</button>
        </div>
      </nav>

      {/* 메뉴 오버레이 */}
      <div
        className={`${styles.menuOverlay} ${isSidebarOpen ? styles.show : ''}`}
        onClick={handleOverlayClick}
      />

      {/* 사이드바 */}
      <aside className={`${styles.sidebar} ${isSidebarOpen ? styles.sidebarOpen : ''}`}>
        <div className={styles.sidebarHeader}>
          <h2 className={styles.sidebarLogo}>Shaun</h2>
          <button className={styles.closeButton} onClick={() => setIsSidebarOpen(false)}>
            <X className={styles.closeIcon} />
          </button>
        </div>

        <ul className={styles.sidebarMenu}>
          <li className={pathname === '/' ? styles.menuActive : ''}>
            <Link href="/" className={styles.menuLink}>
              <HomeIcon className={styles.menuIcon} />
              홈화면
            </Link>
          </li>
          <li className={pathname === '/checklist' ? styles.menuActive : ''}>
            <Link href="/checklist" className={styles.menuLink}>
              <ClipboardCheck className={styles.menuIcon} />
              검사하기
            </Link>
          </li>
          <li className={pathname === '/chat' ? styles.menuActive : ''}>
            <Link href="/chat" className={styles.menuLink}>
              <MessageCircle className={styles.menuIcon} />
              AI 채팅
            </Link>
          </li>
          <li className={pathname === '/profile' ? styles.menuActive : ''}>
            <Link href="/profile" className={styles.menuLink}>
              <User className={styles.menuIcon} />
              회원정보
            </Link>
          </li>
        </ul>
      </aside>


      {/* 메인 */}
      <main className={styles.mainContent}>
        <div className={styles.contentContainer}>
          {/* 오른쪽 컨텐츠 영역 */}
          <div className={styles.contentRight}>
            {/* 환영 메시지 */}
            <div className={styles.greetingRight}>
              <div className={styles.userAvatar}>
                <span>👋</span>
              </div>
              <div className={styles.greetingText}>
                <span className={styles.greetingLabel}>환영합니다!</span>
                <span>
                  <span className={styles.userName}>Guest</span> 님, 맞춤형 스킨케어를 시작해보세요!
                </span>
              </div>
            </div>

            {/* 체크리스트 결과 섹션 */}
            <h2 className={styles.sectionTitle}>
              <span>피부 분석 리포트</span>
              <div className={styles.sectionDesc}>피부 상태를 분석하여 맞춤형 케어 솔루션을 제안합니다</div>
            </h2>
            <section className={styles.resultSection}>
              <div className={styles.checklistBox}>
                <h3>진단 측정 결과</h3>
                <div className={styles.barWrap}>
                  <div>수분 지수 <span>{checklist.수분}%</span></div>
                  <div className={styles.bar}><div style={{width: `${checklist.수분}%`}} className={styles.barGold}></div></div>

                  <div>유분 지수 <span>{checklist.유분}%</span></div>
                  <div className={styles.bar}><div style={{width: `${checklist.유분}%`}} className={styles.barGoldLight}></div></div>

                  <div>민감도 지수 <span>{checklist.민감도}%</span></div>
                  <div className={styles.bar}><div style={{width: `${checklist.민감도}%`}} className={styles.barRed}></div></div>

                  <div>탄력 지수 <span>{checklist.탄력}%</span></div>
                  <div className={styles.bar}><div style={{width: `${checklist.탄력}%`}} className={styles.barGray}></div></div>
                </div>
              </div>

              <div className={styles.analysisBox}>
                <h3>피부 타입 분석</h3>
                <div className={styles.analysisType}>{analysis.type}</div>
                <div className={styles.analysisDesc}>{analysis.description}</div>
                <div className={styles.analysisAdvice}>{analysis.advice}</div>
              </div>
            </section>

            {/* 추천 제품 */}
            <section className={styles.productSection}>
              <h4>
                <span style={{ color: '#333333', background: 'none', WebkitBackgroundClip: 'initial', WebkitTextFillColor: '#333333', backgroundClip: 'initial' }}>맞춤형 제품 추천</span>
                <div className={styles.sectionDesc}>
                  사용자의 피부 타입과 고민에 맞는 최적의 제품을 선별했습니다.<br/>
                  더 많은 제품을 확인해보세요!</div>
              </h4>
              <div className={styles.productList}>
                {products.map((p, i) => (
                  <div key={i} className={styles.productCard}>
                    <div className={styles.productImg}></div>
                    <div className={styles.productName}>{p.name}</div>
                    <div className={styles.productDesc}>{p.description}</div>
                    <button className={styles.buyBtn}>
                      <ShoppingBag className={styles.buttonIcon} />
                      자세히 보기
                    </button>
                  </div>
                ))}
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
              <div className={styles.moreProductsContainer}>
                <Link href="/recommend" className={styles.moreProductsBtn}>
                  <ShoppingBag className={styles.buttonIcon} />
                  더 많은 제품 확인하기
                </Link>
              </div>
            </section>

            {/* 더 자세한 추천 */}
            <section className={styles.recommendSection}>
              <div className={styles.recommendTitle}>
                <span>더 정확한 분석이 필요하신가요?</span>
                <div className={styles.sectionDesc}>
                  전문적인 피부 진단으로 더 정확한 결과를 확인하세요</div>
              </div>
              <Link href="/checklist" className={styles.goBtn}>
                <ClipboardCheck className={styles.buttonIcon} />
                정밀 피부 검사 받기
              </Link>

              <div className={styles.concernTitle}>
                <span>피부 고민별 솔루션</span>
                <div className={styles.sectionDesc}>
                  특정 피부 고민에 맞는 맞춤형 솔루션을 찾아보세요.<br/>
                  어떤 부분을 개선하고 싶으신가요?
                </div>
              </div>

              <div className={styles.concernList}>
                <div className={styles.concernItem}>
                  <div className={styles.concernIcon}>💧</div>
                  <div className={styles.concernLabel}>유·수분 밸런스</div>
                </div>
                <div className={styles.concernItem}>
                  <div className={styles.concernIcon}>🔬</div>
                  <div className={styles.concernLabel}>트러블 케어</div>
                </div>
                <div className={styles.concernItem}>
                  <div className={styles.concernIcon}>🌊</div>
                  <div className={styles.concernLabel}>보습 강화</div>
                </div>
                <div className={styles.concernItem}>
                  <div className={styles.concernIcon}>🔥</div>
                  <div className={styles.concernLabel}>진정 케어</div>
                </div>
                <div className={styles.concernItem}>
                  <div className={styles.concernIcon}>🌡️</div>
                  <div className={styles.concernLabel}>민감성 개선</div>
                </div>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}