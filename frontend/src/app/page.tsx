"use client";

import React, { useState, useEffect } from "react";
import styles from "./page.module.css";
import Link from 'next/link';
import Navbar from "@/components/Navbar";
import { ClipboardCheck, ShoppingBag, Sparkles, FileText, Search } from "lucide-react";
import { usePathname, useRouter } from 'next/navigation';
import { useCurrentUser } from '@/data/useCurrentUser';
import { mbtiList } from '@/data/mbtiList';
import { apiConfig } from '@/config/api';
import jwtDecode from 'jwt-decode';

// 서버가 내려주는 타입 (영문 키)
interface CheckListResponse {
  id: number;
  moisture: number;     // 수분
  oil: number;          // 유분
  sensitivity: number;  // 민감도
  tension: number;      // 탄력
  createdAt: string;
}

// Add interface for guest data
interface GuestChecklistData {
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
  troubles: string[];
  timestamp: number;
}

const products = [
  { name: "수분 에센스", description: "진정효과 수분공급 민감피부용 에센스", category: "수분" },
  { name: "진정 세럼", description: "피부 진정케어 세럼 민감 피부용", category: "진정" },
  { name: "보습 크림", description: "저자극 수분 크림 민감 피부용", category: "보습" },
];


export default function Home() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, loading } = useCurrentUser();

  const [checklist, setChecklist] = useState<CheckListResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [mbti, setMbti] = useState<string>("default");
  const [mbtiError, setMbtiError] = useState<string | null>(null);
  const [products, setProducts] = useState<any[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [searchType, setSearchType] = useState<'all' | 'brand' | 'productName' | 'ingredient'>('all');
  const [token, setToken] = useState<string | null>(null);
  const [tokenLoaded, setTokenLoaded] = useState(false);
  const [isGuest, setIsGuest] = useState(false);

  // Fetch Naver data
  const fetchNaverData = async (token: string) => {
    try {
      const response = await fetch(`${apiConfig.baseURL}/api/naver`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        } as Record<string, string>
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('Error fetching Naver data:', error);
    }
  };

  // On mount: get token from localStorage
  useEffect(() => {
    const APP_VERSION = '2024-06-05-b';
    if (typeof window !== 'undefined') {
      if (localStorage.getItem('app_version') !== APP_VERSION) {
        localStorage.clear();
        localStorage.setItem('app_version', APP_VERSION);
        window.location.reload();
      }
      const t = localStorage.getItem('accessToken');
      setToken(t);
      setTokenLoaded(true);
    }
  }, []);

  // After token is loaded, determine if guest or user
  useEffect(() => {
    if (!tokenLoaded) return;
    if (!token) {
      router.replace('/landing');
      return;
    }
    try {
      const decoded: any = jwtDecode(token);
      if (decoded && (decoded.role === 'GUEST' || decoded.isGuest || decoded.guest)) {
        setIsGuest(true);
      } else {
        setIsGuest(false);
      }
    } catch (e) {
      // If decoding fails, treat as guest for safety
      setIsGuest(true);
    }
  }, [token, tokenLoaded]);

  // Redirect if no token, fetch checklist and MBTI if token exists
  useEffect(() => {
    if (!tokenLoaded) return;
    if (!token) {
      router.replace('/landing');
      return;
    }
    // Fetch checklist
    fetch(apiConfig.endpoints.checklist.base, {
      headers: { 'Authorization': `Bearer ${token}` } as Record<string, string>
    })
      .then(res => {
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.json() as Promise<CheckListResponse[]>;
      })
      .then(data => {
        if (data.length === 0) {
          setError('저장된 체크리스트가 없습니다.');
        } else {
          setChecklist(data[0]);
        }
      })
      .catch(() => setError('체크리스트를 불러오는 데 실패했습니다.'));
    // Fetch MBTI
    fetch(apiConfig.endpoints.checklist.mbti, {
      headers: { 'Authorization': `Bearer ${token}` } as Record<string, string>
    })
      .then(res => {
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.text();
      })
      .then(data => {
        if (!data || data.trim() === '' || data === 'null') {
          setMbtiError('MBTI 결과가 없습니다.');
        } else {
          setMbti(data.trim());
        }
      })
      .catch(error => {
        console.error('MBTI fetch error:', error);
        setMbtiError('MBTI 불러오는 데 실패했습니다.');
      });
  }, [token, tokenLoaded, router]);

  // Fetch Naver data on mount
  useEffect(() => {
    if (!tokenLoaded) return;
    if (token) {
      fetchNaverData(token);
    }
  }, [token, tokenLoaded]);

  // Fetch products using session
  useEffect(() => {
    if (!tokenLoaded) return;
    if (!token) return;
    fetch(apiConfig.endpoints.recommend.sessionRandom, {
      headers: { 'Authorization': `Bearer ${token}` } as Record<string, string>
    })
      .then(res => {
        if (res.status === 204) {
          setProducts(products); // Use static fallback
          return null;
        }
        if (!res.ok) throw new Error(`status ${res.status}`);
        return res.json();
      })
      .then(data => {
        if (data && data.products) {
          const transformedProducts = (data.products as any[]).map((product: any) => ({
            name: product.productName || product.제품명 || '추천 제품',
            description: `${product.recommendedType || product.추천타입 || '피부 케어'} - ${(product.ingredients || product.성분 || []).join(', ')}`,
            imageUrl: product.imageUrl || ''
          }));
          setProducts(transformedProducts);
        } else if (data === null) {
          return;
        } else {
          setProducts(products);
        }
      })
      .catch(error => {
        console.error('Error fetching session products:', error);
        setProducts(products);
      });
  }, [token, tokenLoaded]);

  const handleLogout = () => {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('accessToken');
      window.location.reload();
    }
  };

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

  const handleSearch = async (query: string) => {
    if (!query.trim()) {
      return;
    }
    const searchParams = new URLSearchParams({
      q: query,
      type: searchType
    });
    router.push(`/search?${searchParams.toString()}`);
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleSearch(searchQuery);
  };

  const handlePopularTagClick = (tag: string) => {
    const searchParams = new URLSearchParams({
      q: tag,
      type: 'ingredient'
    });
    router.push(`/search?${searchParams.toString()}`);
  };

  // Use checklist as displayData
  const displayData = checklist;

  if (!tokenLoaded) {
    return <div>Loading...</div>;
  }

  return (
    <div className={styles.wrapper}>
      <Navbar
        isGuest={isGuest}
        onLogout={!isGuest ? handleLogout : undefined}
      />

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

            {/* 검색 히어로 섹션 */}
            <section className={styles.searchHeroSection}>
              <div className={styles.searchContainer}>
                <form onSubmit={handleSearchSubmit}>
                  <div className={styles.searchInputContainer}>
                    <Search className={styles.searchIcon} />
                    <input
                      type="text"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder="제품명, 브랜드, 성분으로 검색하세요"
                      className={styles.searchInput}
                    />
                    <button 
                      type="submit" 
                      className={styles.searchButton}
                    >
                      검색
                    </button>
                  </div>
                </form>

                {/* 인기 검색어 */}
                <div className={styles.popularSearches}>
                  <span className={styles.popularLabel}>인기 검색어:</span>
                  <div className={styles.popularTags}>
                    {['비타민C', '히알루론산', '레티놀', '나이아신아마이드', '세라마이드'].map((tag) => (
                      <button
                        key={tag}
                        className={styles.popularTag}
                        onClick={() => handlePopularTagClick(tag)}
                      >
                        {tag}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </section>

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
                      <div>수분 지수 <span>{displayData?.moisture ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${displayData?.moisture ?? 0}%`}} className={styles.barGold}></div></div>

                      <div>유분 지수 <span>{displayData?.oil ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${displayData?.oil ?? 0}%`}} className={styles.barGoldLight}></div></div>

                      <div>민감도 지수 <span>{displayData?.sensitivity ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${displayData?.sensitivity ?? 0}%`}} className={styles.barRed}></div></div>

                      <div>탄력 지수 <span>{displayData?.tension ?? 0}%</span></div>
                      <div className={styles.bar}><div style={{width: `${displayData?.tension ?? 0}%`}} className={styles.barGray}></div></div>
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
                    <div className={styles.analysisType}>{mbtiList[mbti as keyof typeof mbtiList]?.type || '일반'}</div>
                    <div className={styles.analysisDesc}>
                      {mbtiList[mbti as keyof typeof mbtiList]?.description || '피부 상태를 분석해주세요.'}
                    </div>
                    <div className={styles.analysisAdvice}>
                      <div className={styles.adviceLabel}>
                        💡 추천 관리법
                      </div>
                      <div className={styles.adviceContent}>
                        {mbtiList[mbti as keyof typeof mbtiList]?.advice || '기본적인 스킨케어 루틴을 유지해주세요.'}
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
                            onError={async (e) => {
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