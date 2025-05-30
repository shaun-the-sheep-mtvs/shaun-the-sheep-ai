"use client";

import React, { useState, useEffect } from "react";
import styles from "./page.module.css";
import Link from 'next/link';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X, Sparkles, FileText } from "lucide-react";
import { usePathname, useRouter } from 'next/navigation';
import { useCurrentUser } from '@/data/useCurrentUser';
import { apiConfig } from '@/config/api';

// 서버가 내려주는 타입 (영문 키)
interface CheckListResponse {
  id: number;
  moisture: number;     // 수분
  oil: number;          // 유분
  sensitivity: number;  // 민감도
  tension: number;      // 탄력
  createdAt: string;
}


const products = [
  { name: "수분 에센스", description: "진정효과 수분공급 민감피부용 에센스", category: "수분" },
  { name: "진정 세럼", description: "피부 진정케어 세럼 민감 피부용", category: "진정" },
  { name: "보습 크림", description: "저자극 수분 크림 민감 피부용", category: "보습" },
];

export const mbtiList = {
  MBST: {
    type: "민감성",
    description: "피부가 건조해 당김이 느껴지고, 유분 분비가 안정적이어서 번들거림이 적으며, 탄력은 좋지만 자극에 민감한 상태입니다.",
    advice: "고보습 크림과 오일로 유수분 균형을 맞추고 가벼운 수분젤로 촉촉함을 유지하면서 저자극·진정 포뮬러로 장벽을 강화하고 콜라겐·펩타이드 성분으로 탄력을 관리하세요."
  },
  MBSL: {
    type: "민감성",
    description: "피부가 건조해 당김이 느껴지고, 유분 분비가 안정적이어서 번들거림이 적으며, 자극에 민감하고 처짐이 느껴져 탄력 저하가 있습니다.",
    advice: "고보습 크림과 오일로 유수분 균형을 맞추고 가벼운 수분젤로 촉촉함을 유지하며, 저자극·진정 포뮬러로 장벽을 강화하고 리프팅 세럼과 마사지를 병행해 탄력을 높이세요."
  },
  MOIT: {
    type: "지성",
    description: "수분과 유분이 모두 충분해 번들거림이 적고, 자극에도 안정적이며, 탄력도 좋은 균형 상태입니다.",
    advice: "히알루론산 세럼으로 수분 장벽을 강화하고 가벼운 수분젤로 촉촉함을 유지하며, 기본 보습·톤업 케어를 꾸준히 하면서 콜라겐·펩타이드 제품으로 탄력을 유지하세요."
  },
  MOIL: {
    type: "지성",
    description: "수분과 유분이 충분해 번들거림이 적지만, 탄력은 다소 떨어져 처짐이 느껴집니다.",
    advice: "히알루론산 세럼으로 수분 장벽을 강화하고 가벼운 수분젤로 촉촉함을 유지하면서, 기본 보습·톤업 케어를 하되 리프팅 세럼과 마사지를 병행해 탄력을 높이세요."
  },
  MBIT: {
    type: "복합성",
    description: "수분은 충분하나 유분이 부족해 거칠어지고, 자극에 민감하면서도 탄력은 좋은 상태입니다.",
    advice: "히알루론산 세럼으로 수분을 보충하고 리치 오일·크림으로 유분을 채우며, 저자극·진정 포뮬러로 장벽을 강화하고 콜라겐·펩타이드 성분으로 탄력을 관리하세요."
  },
  MBIL: {
    type: "복합성",
    description: "수분은 충분하지만 유분과 탄력이 모두 부족해 당김과 처짐이 함께 나타납니다.",
    advice: "히알루론산 세럼으로 수분을 보충하고 리치 오일·크림으로 유분을 채운 뒤, 저자극·진정 포뮬러로 장벽을 강화하고 리프팅 세럼·마사지를 통해 탄력을 높이세요."
  },
  DOST: {
    type: "수분부족지성",
    description: "수분이 부족해 당김이 느껴지지만 유분과 탄력은 안정적인 상태입니다.",
    advice: "고보습 크림과 오일로 수분을 채우고, 저자극 수분 세럼과 마스크로 수분 장벽을 강화하면서 콜라겐·펩타이드 성분으로 탄력을 유지하세요."
  },
  DOSL: {
    type: "수분부족지성",
    description: "수분과 탄력이 부족해 당김과 처짐이 동시에 느껴집니다.",
    advice: "고보습 크림과 오일로 수분을 채우고 저자극 진정 세럼으로 장벽을 강화하며 리프팅 세럼·마사지로 탄력을 개선하세요."
  },
  DBST: {
    type: "건성",
    description: "수분과 유분이 모두 부족해 건조함이 심하지만 탄력은 비교적 좋은 상태입니다.",
    advice: "고보습 크림과 오일로 집중 영양을 공급하고, 기본 보습·톤업 케어를 유지하며 콜라겐·펩타이드 성분으로 탄력을 관리하세요."
  },
  DBSL: {
    type: "건성",
    description: "수분·유분·탄력이 모두 부족해 피부가 거칠고 처짐이 심합니다.",
    advice: "장벽 강화·고보습 크림과 오일로 영양을 채우고, 리프팅 세럼·마사지로 탄력을 강화하세요."
  },
  DOIT: {
    type: "수분부족지성",
    description: "수분이 부족해 당김이 느껴지지만 유분과 탄력은 좋은 상태입니다.",
    advice: "수분 에센스와 마스크로 즉각적인 수분을 보충하고, 콜라겐·펩타이드 성분으로 탄력을 유지하세요."
  },
  DOIL: {
    type: "수분부족지성",
    description: "유분은 충분하지만 수분과 탄력 모두 부족해 당김과 처짐이 느껴집니다.",
    advice: "고보습 세럼으로 수분을 보충하고 탄력 강화 오일을 함께 사용해 탄력을 회복하세요."
  },
  DBIT: {
    type: "건성",
    description: "수분·유분이 부족해 건조함이 느껴지지만 탄력은 비교적 유지되는 상태입니다.",
    advice: "보습 크림과 수분 세럼으로 영양을 보충하고 콜라겐·펩타이드 성분으로 탄력을 관리하세요."
  },
  DBIL: {
    type: "건성",
    description: "수분·유분·탄력이 모두 부족해 건조하고 처짐이 심한 상태입니다.",
    advice: "장벽 강화·고보습 크림과 탄력 세럼을 병행해 집중 관리하세요."
  },
  default: {
    type: "표준형",
    description: "피부 균형이 잘 잡힌 상태입니다.",
    advice: "기본 보습과 탄력 관리 루틴을 꾸준히 지켜주세요."
  }
};


export default function Home() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();
  const router = useRouter();
  const { user, loading } = useCurrentUser();

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
  const [error, setError] = useState<string | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [mbti, setMbti] = useState<string>("default");
  const [mbtiError, setMbtiError] = useState<string | null>(null);
  const [products, setProducts] = useState<any[]>([]);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
    
    if (token) {
      fetch(apiConfig.endpoints.checklist.base, {
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
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
    
    if (token) {
      fetch(apiConfig.endpoints.checklist.mbti, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
        .then(res => {
          if (!res.ok) throw new Error(`status ${res.status}`);
          // String 응답을 처리하기 위해 text()로 변경
          return res.text();
        })
        .then(data => {
          console.log('Received data:', data);
          // 빈 문자열이나 null 체크
          if (!data || data.trim() === '' || data === 'null') {
            setMbtiError('MBTI 결과가 없습니다.');
          } else {
            console.log('MBTI result:', data);
            setMbti(data.trim());  // 앞뒤 공백 제거 후 설정
          }
        })
        .catch(error => {
          console.error('MBTI fetch error:', error);
          setMbtiError('MBTI 불러오는 데 실패했습니다.');
        });
    }
  }, []);


  // 서버에서 보내는 3개 제품 데이터 가져오기
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      fetch(apiConfig.endpoints.recommend.random, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
        .then(res => {
          if (!res.ok) throw new Error(`status ${res.status}`);
          return res.json() as Promise<any[]>;
        })
        .then(data => {
          console.log('API Response:', data); // 실제 데이터 구조 확인
          const transformedProducts = data.map(product => ({
            name: product.productName,
            description: `${product.recommendedType} - ${product.ingredients.join(', ')}`,
            imageUrl: product.imageUrl
          }));
          setProducts(transformedProducts);
        })
        .catch(error => {
          console.error('Error fetching products:', error);
        });
    }
  }, []);
  

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
    }
  }, [isLoggedIn]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    window.location.reload();
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
          {!isLoggedIn ? (
            <>
              <button 
                className={styles.authButton}
                onClick={() => router.push('/register')}
              >
                회원가입
              </button>
              <button 
                className={styles.loginButton}
                onClick={() => router.push('/login')}
              >
                로그인
              </button>
            </>
          ):(
            <button 
              className={styles.logoutButton}
              onClick={handleLogout}
            >
              로그아웃
            </button>
          )}
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
          <li className={pathname === '/ai-chat' ? styles.menuActive : ''}>
            <Link href="/" className={styles.menuLink}>
              <MessageCircle className={styles.menuIcon} />
              AI 채팅
            </Link>
          </li>
          <li className={pathname === '/profile' ? styles.menuActive : ''}>
            <Link href="/" className={styles.menuLink}>
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
                  <span className={styles.userName}>
                    {loading ? 'Loading...' : user ? user.username : 'Guest'}
                  </span> 
                    {" "}님, 맞춤형 스킨케어를 시작해보세요!
                </span>
              </div>
            </div>

            {/* 체크리스트 결과 섹션 */}
            <section className={`${styles.pageSection} ${styles.analysisReportSection}`}>
              <div className={styles.sectionContent}>
                <div className={styles.sectionHeader}>
                  <h2 className={styles.sectionMainTitle}>피부 분석 리포트</h2>
                  <div className={styles.sectionSubtitle}>피부 상태를 분석하여 맞춤형 케어 솔루션을 제안합니다</div>
                </div>


                <div className={styles.resultSection}>
                  <div className={styles.checklistBox}>
                    <h3>진단 측정 결과</h3>
                    <div className={styles.barWrap}>
                      <div>수분 지수 <span>{checklist?.moisture ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${checklist?.moisture ?? 0}%`}} className={styles.barGold}></div></div>

                      <div>유분 지수 <span>{checklist?.oil ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${checklist?.oil ?? 0}%`}} className={styles.barGoldLight}></div></div>

                      <div>민감도 지수 <span>{checklist?.sensitivity ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${checklist?.sensitivity ?? 0}%`}} className={styles.barRed}></div></div>

                      <div>탄력 지수 <span>{checklist?.tension ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${checklist?.tension ?? 0}%`}} className={styles.barGray}></div></div>
                    </div>
                  </div>

                  <div className={styles.analysisBox}>
                    <h3>피부 타입 분석</h3>
                    <div className={styles.mbtiResult}>
                      <div className={styles.mbtiTitle}>피부 MBTI</div>
                      <div className={styles.mbtiCode}>
                        <span>{mbti.charAt(0)}</span>
                        <span>{mbti.charAt(1)}</span>
                        <span>{mbti.charAt(2)}</span>
                        <span>{mbti.charAt(3)}</span>
                      </div>
                      <div className={styles.mbtiDesc}>
                        <div>Moisture</div>
                        <div>Oily</div>
                        <div>Sensitive</div>
                        <div>Tension</div>
                      </div>
                    </div>
                    <div className={styles.analysisType}>{mbtiList[mbti as keyof typeof mbtiList]?.type}</div>
                    <div className={styles.analysisDesc}>
                      {mbtiList[mbti as keyof typeof mbtiList]?.description}
                    </div>
                    <div className={styles.analysisAdvice}>
                      <div className={styles.adviceLabel}>
                        💡 추천 관리법
                      </div>
                      <div className={styles.adviceContent}>
                        {mbtiList[mbti as keyof typeof mbtiList].advice}
                      </div>
                    </div>

                    {/* 상세 리포트 버튼 추가 */}
                    <Link href="/report" className={styles.reportButton}>
                      <FileText className={styles.buttonIcon} />
                      피부 분석 상세 리포트 보기
                    </Link>
                  </div>
                </div>
              </div>
            </section>

            {/* 추천 제품 */}
            <section className={`${styles.pageSection} ${styles.productRecommendSection}`}>
              <div className={styles.sectionContent}>
                <div className={styles.sectionHeader}>
                  <h2 className={styles.sectionMainTitle}>맞춤형 제품 추천</h2>
                  <div className={styles.sectionSubtitle}>
                    사용자의 피부 타입과 고민에 맞는 최적의 제품을 선별했습니다.<br/>
                    더 많은 제품을 확인해보세요!
                  </div>
                </div>

                <div className={styles.productList}>
                  {products.map((p, i) => (
                    <div key={i} className={styles.productCard}>
                      <div className={styles.productImg}>
                        {p.imageUrl ? (

                          <img
                            src={p.imageUrl} 
                            alt={p.name}
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.style.display = 'none';
                              const parent = target.parentElement;
                              if (parent) {
                                parent.classList.add(styles.noImage);
                              }
                            }}
                          />
                        ) : (
                          <div className={styles.placeholderImage}>
                            <div className={styles.placeholderIcon}>📦</div>
                            <div className={styles.placeholderText}>제품 이미지</div>
                          </div>
                        )}
                      </div>
                      <div className={styles.productName}>{p.name}</div>
                      <div className={styles.productDesc}>{p.description}</div>
                      <button className={styles.buyBtn}>
                        <ShoppingBag className={styles.buttonIcon} />
                        자세히 보기
                      </button>
                    </div>
                  ))}
                </div>
                <Link href="/recommend" className={styles.viewMoreBtn}>
                  <Sparkles className={styles.buttonIcon} />
                  맞춤형 제품 추천 받기
                </Link>
              </div>
            </section>

            {/* 더 자세한 추천 */}
            <section className={`${styles.pageSection} ${styles.moreAnalysisSection}`}>
              <div className={styles.sectionContent}>
                <div className={styles.sectionHeader}>
                  <h2 className={styles.sectionMainTitle}>더 정확한 분석이 필요하신가요?</h2>
                  <div className={styles.sectionSubtitle}>
                    전문적인 피부 진단으로 더 정확한 결과를 확인하세요
                  </div>
                </div>

                <Link href="/routine-manage" className={styles.goBtn}>
                  <ClipboardCheck className={styles.buttonIcon} />
                  정밀 피부 검사 받기
                </Link>
              </div>
            </section>

            <section className={`${styles.pageSection} ${styles.skinSolutionsSection}`}>
              <div className={styles.sectionContent}>
                <div className={styles.sectionHeader}>
                  <h2 className={styles.sectionMainTitle}>피부 고민별 솔루션</h2>
                  <div className={styles.sectionSubtitle}>
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
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}