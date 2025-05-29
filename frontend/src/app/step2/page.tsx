'use client'

import React, { useState, useEffect } from 'react';
import styles from './page.module.css';
import '../globals.css';
import axios from 'axios';
import { usePathname, useRouter } from 'next/navigation';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X, Sparkles, FileText } from "lucide-react";
import Link from 'next/link';
import apiConfig from '@/config/api';

// recommend/page.tsx에서 Product 타입을 가져옵니다.
interface Product {
    제품명: string;
    추천타입: string;
    성분: string[];
}

// src/types/user.ts 에서 User 타입을 가져온다고 가정합니다.
// 실제 경로와 파일 구조에 따라 import 경로를 확인해야 합니다.
// 예시: import { User } from '@/types/user'; 
// 우선은 여기에 직접 User 인터페이스를 정의합니다.
interface User {
    id: string;
    username: string; // "username" 컬럼으로 변경
    email: string;
    // 필요한 다른 필드들...
}

interface UserSkinData {
    skinType: string;
    troubles: string[];
}

interface ApiRoutineItem {
    name: string;
    kind: string;
    method: string;
    orders: number;
    time: 'MORNING' | 'NIGHT';
    routineGroupId: number;
}

interface ApiRecommendedRoutineItem {
    routineName: string;
    routineKind: string;
    routineTime: 'MORNING' | 'NIGHT';
    routineOrders: number;
    changeMethod: string;
    routineGroupId: number;
}

interface DisplayRoutineItem {
    title: string; // 이전 title 역할 (name)
    kind: string;
    method: string;
    desc: string;  // 이전 desc 역할 (kind + method)
    time: 'morning' | 'night'; // 소문자로 통일
    orders: number;
    // 필요하다면 routineGroupId도 포함 가능
}

// 제품 변경 및 추가 추천 API (deep-recommend) 응답 형식
interface ApiDeepRecommendItem {
    action: 'Add' | 'Replace'; // API 실제 값인 소문자로 수정
    routineName?: string;      
    suggestProduct: string;   
    reason: string;
}

// 화면 표시용 인터페이스
interface ProductChangeItem {
    currentProduct: string;
    suggest_product: string; // JSX와 일치시키기 위해 이 필드명 사용
    reason: string;
}

interface ProductAdditionItem {
    addProduct: string;
    reason: string;
}

export default function Step2() {
    

    // 탭 상태: 'morning' 또는 'night'
    const [routineTab, setRoutineTab] = useState<'morning' | 'night'>('morning');
    const [userSkinData, setUserSkinData] = useState<UserSkinData | null>(null);
    const [morningRoutine, setMorningRoutine] = useState<DisplayRoutineItem[]>([]);
    const [nightRoutine, setNightRoutine] = useState<DisplayRoutineItem[]>([]);
    const [recommendedMorningRoutine, setRecommendedMorningRoutine] = useState<DisplayRoutineItem[]>([]);
    const [recommendedNightRoutine, setRecommendedNightRoutine] = useState<DisplayRoutineItem[]>([]);
    const [userData, setUserData] = useState<User | null>(null); // 사용자 정보 상태 추가
    const router = useRouter();
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false); // 초기값 false 유지
    const pathname = usePathname();
    
    // 제품 변경 및 추가 추천 상태
    const [productChanges, setProductChanges] = useState<ProductChangeItem[]>([]);
    const [productAdditions, setProductAdditions] = useState<ProductAdditionItem[]>([]);
    
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
           setError('로그인이 필요합니다.');
           setIsLoggedIn(false); // 토큰 없으면 로그아웃 상태
           return;
        }


        const fetchUserData = axios.get<User>(apiConfig.endpoints.auth.me, { // 타입 User로 수점

          headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchSkinData = axios.get<UserSkinData>(apiConfig.endpoints.user.skinData, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchExistingRoutines = axios.get<ApiRoutineItem[]>(apiConfig.endpoints.routine.existing, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchRecommendedRoutines = axios.get<ApiRecommendedRoutineItem[]>(apiConfig.endpoints.deep.routineChange, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchDeepRecommendations = axios.get<ApiDeepRecommendItem[]>(apiConfig.endpoints.deep.productRecommend, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });


        Promise.all([fetchSkinData, fetchExistingRoutines, fetchRecommendedRoutines, fetchDeepRecommendations, fetchUserData])
            .then(([skinDataResponse, existingRoutinesResponse, recommendedRoutinesResponse, deepRecommendationsResponse, userDataResponse]) => {
                setUserSkinData(skinDataResponse.data);
                console.log('User Data Response:', userDataResponse.data); 
                
                if (userDataResponse.data) {
                    setUserData(userDataResponse.data); 
                    setIsLoggedIn(true); // 사용자 데이터 성공적으로 불러오면 로그인 상태로 변경
                } else {
                    setIsLoggedIn(false); // 사용자 데이터가 없으면 로그아웃 상태
                    setError('사용자 정보를 불러올 수 없습니다.');
                }

                // 기존 루틴
                const existingRoutinesFromApi = existingRoutinesResponse.data;
                const transformedExistingRoutines: DisplayRoutineItem[] = existingRoutinesFromApi.map(apiItem => ({
                    title: apiItem.name,
                    kind: apiItem.kind,
                    method: apiItem.method,
                    desc: `${apiItem.kind} (${apiItem.method})`,
                    time: apiItem.time.toLowerCase() as 'morning' | 'night',
                    orders: apiItem.orders,
                }));

                setMorningRoutine(transformedExistingRoutines.filter(r => r.time === 'morning').sort((a, b) => a.orders - b.orders));
                setNightRoutine(transformedExistingRoutines.filter(r => r.time === 'night').sort((a, b) => a.orders - b.orders));
                
                // 맞춤 루틴
                const recommendedRoutinesFromApi = recommendedRoutinesResponse.data;
                const transformedRecommendedRoutines: DisplayRoutineItem[] = recommendedRoutinesFromApi.map(apiItem => ({
                    title: apiItem.routineName,
                    kind: apiItem.routineKind,
                    method: apiItem.changeMethod,
                    desc: `${apiItem.routineKind} (${apiItem.changeMethod})`,
                    time: apiItem.routineTime.toLowerCase() as 'morning' | 'night',
                    orders: apiItem.routineOrders,
                }));
                setRecommendedMorningRoutine(transformedRecommendedRoutines.filter(r => r.time === 'morning').sort((a, b) => a.orders - b.orders));
                setRecommendedNightRoutine(transformedRecommendedRoutines.filter(r => r.time === 'night').sort((a, b) => a.orders - b.orders));


                const deepRecommendationsFromApi = deepRecommendationsResponse.data;
                console.log(deepRecommendationsFromApi);
                const changes: ProductChangeItem[] = [];
                const additions: ProductAdditionItem[] = [];

                deepRecommendationsFromApi.forEach(item => {
                    if (item.action === 'Replace') { // 소문자로 비교 수정
                        changes.push({
                            currentProduct: item.routineName || '', 
                            suggest_product: item.suggestProduct, 
                            reason: item.reason,
                        });
                    } else if (item.action === 'Add') { // 소문자로 비교 수정
                        additions.push({
                            addProduct: item.suggestProduct,
                            reason: item.reason,
                        });
                    }
                });
                setProductChanges(changes);
                setProductAdditions(additions);

            })
            .catch(err => {
                console.error(err);
                const msg =
                    err.response?.data?.message || err.message || '데이터를 불러오는데 실패했습니다.';
                setError(msg);
                setIsLoggedIn(false); // 에러 발생 시 로그아웃 상태로 간주
            });
    }, []);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
      };

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        window.location.reload();
    }

    const handleOverlayClick = () => {
        setIsSidebarOpen(false);
      };

    const handleDoneButtonClick = () => { // 완료 버튼 핸들러 추가
        router.push('/'); // 홈 화면으로 이동
    };

    const handleBuyButtonClick = (productName: string) => {
        window.open(`https://www.coupang.com/np/search?component=&q=${productName}`, '_blank ');
    }

    // 현재 선택된 루틴
    const currentExistingRoutine = routineTab === 'morning' ? morningRoutine : nightRoutine;
    const currentRecommendedRoutine = routineTab === 'morning' ? recommendedMorningRoutine : recommendedNightRoutine;

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
            <Link href="/ai-chat" className={styles.menuLink}>
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
            {/* 메인 컨텐츠 */}
            <main className={styles['step2-main-content']}>
                {/* STEP 표시 */}
                <div className={styles['step2-step-bar']}>
                    <span className={styles['step2-step-inactive']}>STEP 1</span>
                    <span className={styles['step2-step-active']}>STEP 2</span>
                </div>
                {/* 제품 사용법 추천 */}
                <div className={styles['step2-recommend-title']}>루틴 분석 추천</div>
                <div className={styles['step2-recommend-desc']}>AI가 분석한 당신의 피부 타입에 맞는 루틴과 제품을 추천해 드립니다.</div>
                {/* 피부 타입 박스 */}
                <div className={styles['step2-skin-type-box']}>
                    {userSkinData ? (
                        <>
                            <div className={styles['step2-skin-type-main']}>{userSkinData.skinType}</div>
                            <div className={styles['step2-skin-type-tags']}>
                                {userSkinData.troubles.map((trouble, index) => (
                                    <span key={index}>{trouble}</span>
                                ))}
                            </div>
                        </>
                    ) : error ? (
                        <div className={styles['step2-skin-type-main']}>{error}</div>
                    ) : (
                        <div className={styles['step2-skin-type-main']}>피부 정보 로딩 중...</div>
                    )}
                </div>
                {/* 기존 루틴 */}
                <div className={styles['step2-section-title']}>기존 루틴</div>
                <div className={styles['step2-tabs']}>
                    <div
                        className={`${styles['step2-tab']} ${routineTab === 'morning' ? styles['step2-tab-active'] : ''}`}
                        onClick={() => setRoutineTab('morning')}
                    >
                        아침 루틴
                    </div>
                    <div
                        className={`${styles['step2-tab']} ${routineTab === 'night' ? styles['step2-tab-active'] : ''}`}
                        onClick={() => setRoutineTab('night')}
                    >
                        저녁 루틴
                    </div>
                </div>
                <div className={styles['step2-routine-box']}>
                    <div className={styles['step2-routine-header']}>
                        <span>{routineTab === 'morning' ? '아침 루틴' : '저녁 루틴'}</span>
                        <span className={styles['step2-routine-total']}>총 {currentExistingRoutine.length}단계</span>
                    </div>
                    <ol className={styles['step2-routine-list']}>
                        {currentExistingRoutine.map((item) => (
                            <li key={`existing-${item.time}-${item.orders}-${item.title}`}> 
                                <div className={styles['step2-routine-num']}>{item.orders}</div>
                                <div className={styles['step2-routine-info']}>
                                    <div className={styles['step2-routine-title']}>{item.title}</div>
                                    <div className={styles['step2-routine-desc']}>
                                        {item.desc}
                                    </div>
                                </div>
                                <button onClick={() => handleBuyButtonClick(item.title)} className={styles['buy-btn']} >
                                    구매하기
                                </button>
                            </li>
                        ))}
                    </ol>
                </div>
                {/* 맞춤 루틴 추천 */}
                <div className={styles['step2-section-title']}>제품 사용법 추천</div>
                <div className={styles['step2-tabs']}>
                    <div
                        className={`${styles['step2-tab']} ${routineTab === 'morning' ? styles['step2-tab-active'] : ''}`}
                        onClick={() => setRoutineTab('morning')}
                    >
                        아침 루틴
                    </div>
                    <div
                        className={`${styles['step2-tab']} ${routineTab === 'night' ? styles['step2-tab-active'] : ''}`}
                        onClick={() => setRoutineTab('night')}
                    >
                        저녁 루틴
                    </div>
                </div>
                <div className={styles['step2-routine-box']}>
                    <div className={styles['step2-routine-header']}>
                        <span>{routineTab === 'morning' ? '아침 루틴' : '저녁 루틴'}</span>
                        <span className={styles['step2-routine-total']}>총 {currentRecommendedRoutine.length}단계</span>
                    </div>
                    <ol className={styles['step2-routine-list']}>
                        {currentRecommendedRoutine.map((item) => (
                             <li key={`recommended-${item.time}-${item.orders}-${item.title}`}> 
                                <div className={styles['step2-routine-num']}>{item.orders}</div>
                                <div className={styles['step2-routine-info']}>
                                    <div className={styles['step2-routine-title']}>{item.title}</div>
                                    <div className={styles['step2-routine-desc']}>
                                        {item.kind}
                                        {item.method.split('\n').map((line, index) => (
                                            <div key={index}>{`${index + 1}. ${line}`}</div>
                                        ))}
                                    </div>
                                </div>
                                <button onClick={() => handleBuyButtonClick(item.title)} className={styles['buy-btn']}>구매하기</button>
                            </li>
                        ))}
                    </ol>
                </div>
                {/* 제품 변경 및 추가 추천 */}
                <div className={styles['step2-section-title']} style={{marginTop: 48}}>제품 변경 및 추가 추천</div>
                <div className={styles['step2-change-box']}>
                  <div className={styles['change-recommend-title']}>기존 루틴에서 변경하면 좋을 제품</div>
                  {productChanges.length === 0 ? (
                    <div className={styles['empty-message-card']}>
                      <p>변경할 제품이 없습니다.</p>
                    </div>
                  ) : (
                    productChanges.map((item, idx) => (
                      <div className={styles['change-recommend-card']} key={`change-${idx}-${item.suggest_product}`}>
                        <div className={styles['change-product-row']}>
                          <div className={styles['change-product-col']}>
                            <div className={styles['change-product-row']}>
                              <div className={styles['change-product-icon']}>
                                <svg width="24" height="24" fill="none" stroke="#bdbdbd" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                              </div>
                              <div>
                                <div className={styles['change-product-label']}>현재 사용 중</div>
                                <div className={styles['change-product-title']}>{item.currentProduct}</div>
                              </div>
                            </div>
                            <div className={styles['change-arrow']}>
                              <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19" /><polyline points="19 12 12 19 5 12" /></svg>
                            </div>
                            <div className={styles['change-product-row']}>
                              <div className={`${styles['change-product-icon']} ${styles['green']}`}>
                                <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                              </div>
                              <div>
                                <div className={`${styles['change-product-label']} ${styles['green']}`}>추천 제품</div>
                                <div className={styles['change-product-title']}>{item.suggest_product}</div>
                              </div>
                            </div>
                          </div>
                          <div>
                            <button onClick={() => handleBuyButtonClick(item.suggest_product)} className={styles['buy-btn']}>구매하기</button>
                          </div>
                        </div>
                        <div className={styles['change-reason-box']}>
                          <p className={styles['change-reason-text']}>{item.reason}</p>
                        </div>
                      </div>
                    ))
                  )}
                  <div className={styles['change-recommend-title']} style={{marginTop: 32}}>추가하면 좋을 제품</div>
                  <div className={styles['add-recommend-grid']}>
                    {productAdditions.length === 0 ? (
                      <div className={styles['empty-message-card']}>
                        <p>추가할 추천 제품이 없습니다.</p>
                      </div>
                    ) : (
                      productAdditions.map((item, idx) => (
                        <div className={styles['add-recommend-card']} key={`add-${idx}-${item.addProduct}`}>
                          <div className={styles['add-recommend-header']}>
                            <div style={{display: 'flex', alignItems: 'center'}}>
                              <div className={styles['add-product-icon']}>
                                <svg width="20" height="20" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" /></svg>
                              </div>
                              <div className={styles['add-product-title']}>{item.addProduct}</div>
                            </div>
                            <button onClick={() => handleBuyButtonClick(item.addProduct)} className={styles['buy-btn']}>구매하기</button>
                          </div>
                          <div className={styles['add-reason']}>{item.reason}</div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
                {/* 완료 버튼 */}
                <div className={styles['step2-bottom-btns']}>
                    <button className={styles['step2-done-btn']} onClick={handleDoneButtonClick}>완료</button> {/* onClick 핸들러 연결 */}
                </div>
            </main>
        </div>
    );
}