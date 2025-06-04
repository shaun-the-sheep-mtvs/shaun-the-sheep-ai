'use client'

import React, { useState, useEffect } from 'react';
import styles from './page.module.css';
import '../globals.css';
import axios from 'axios';
import { usePathname, useRouter } from 'next/navigation';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X, Sparkles, FileText } from 'lucide-react';
import Link from 'next/link';
import apiConfig from '@/config/api';
import Navbar from '../../components/Navbar';

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
    action: 'Add' | 'Replace';
    routineName?: string;      
    suggestProduct: string;   
    reason: string;
    existingProductId?: string; // Add this field
    name?: string;
}

// 화면 표시용 인터페이스
interface ProductChangeItem {
    currentProduct: string; // Change from existingProductId to currentProduct
    suggest_product: string;
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
    const [user, setUser] = useState<User | null>(null);
    const router = useRouter();
    const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const pathname = usePathname();
    
    // 제품 변경 및 추가 추천 상태
    const [productChanges, setProductChanges] = useState<ProductChangeItem[]>([]);
    const [productAdditions, setProductAdditions] = useState<ProductAdditionItem[]>([]);

    // Add this helper function before the useEffect
    const findRoutineNameById = (routineId: string, routines: DisplayRoutineItem[]): string => {
        const routine = routines.find(r => r.title === routineId || r.kind === routineId);
        return routine ? routine.title : routineId; // fallback to routineId if not found
    };

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
                    setUser(userDataResponse.data); 
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
                    if (item.action === 'Replace') {
                        // Combine all routines to search from
                        const allRoutines = [...transformedExistingRoutines];
                        const currentProductName = item.name || '알 수 없는 제품';
                            
                        changes.push({
                            currentProduct: currentProductName,
                            suggest_product: item.suggestProduct, 
                            reason: item.reason,
                        });
                    } else if (item.action === 'Add') {
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

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        window.location.reload();
    }

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
      <Navbar />

            {/* 메인 컨텐츠 */}
            <main className={styles['step2-main-content']}>
                <div className={styles['step2-content-container']}>
                    {/* 섹션 1: 피부 타입 정보 */}
                    <div className={styles['step2-skin-type-section']}>
                        <div className={styles['step2-recommend-title']}>Step 3. 루틴 분석 추천</div>
                        <div className={styles['step2-recommend-desc']}>AI가 분석한 당신의 피부 타입에 맞는 루틴과 제품을 추천해 드립니다.</div>
                        <div className={styles['step2-skin-type-box']}>
                            {userSkinData && user ? (
                                <>
                                    <div className={styles['user-greeting']}>
                                        안녕하세요, <span className={styles['user-name']}>{user.username}</span>님! 👋
                                    </div>
                                    <div className={styles['skin-type-result']}>
                                        <div className={styles['skin-type-label']}>분석된 피부 타입</div>
                                        <div className={styles['skin-type-value']}>{userSkinData.skinType}</div>
                                    </div>
                                    <div className={styles['troubles-section']}>
                                        <div className={styles['troubles-label']}>주요 피부 고민</div>
                                        <div className={styles['step2-skin-type-tags']}>
                                            {userSkinData.troubles.map((trouble, index) => (
                                                <span key={index}>{trouble}</span>
                                            ))}
                                        </div>
                                    </div>
                                </>
                            ) : error ? (
                                <div className={styles['step2-skin-type-main']}>{error}</div>
                            ) : (
                                <div className={styles['step2-skin-type-main']}>피부 정보 로딩 중...</div>
                            )}
                        </div>
                    </div>

                    {/* 섹션 2: 루틴 추천 */}
                    <div className={styles['step2-routines-section']}>
                        <div className={styles['section-header']}>
                            <div className={styles['section-main-title']}>루틴 비교 분석</div>
                            <div className={styles['section-description']}>
                                현재 사용 중인 루틴과 AI가 추천하는 맞춤형 루틴을 비교해보세요.
                                <br />
                                각 단계별로 어떤 제품을 언제, 어떻게 사용해야 하는지 자세한 가이드를 제공합니다.
                            </div>
                        </div>
                        <div className={styles['routine-sections-container']}>
                            {/* 기존 루틴 */}
                            <div className={styles['routine-section']}>
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
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                            </div>

                            {/* 맞춤 루틴 추천 */}
                            <div className={styles['routine-section']}>
                                <div className={styles['step2-section-title']}>AI 맞춤 루틴</div>
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
                                                        {typeof item?.method === 'string' &&
                                                        item.method.split('\n').map((line, index) => (
                                                          <div key={index}>{`${index + 1}. ${line}`}</div>
                                                      ))}
                                                    </div>
                                                </div>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* 섹션 3: 제품 변경 및 추가 추천 */}
                    <div className={styles['step2-products-section']}>
                        <div className={styles['section-header']}>
                            <div className={styles['section-main-title']}>제품 변경 및 추가 추천</div>
                            <div className={styles['section-description']}>
                                현재 사용 중인 제품 중 더 나은 대안이 있는 제품과 추가로 사용하면 좋을 제품을 추천합니다.
                                <br />
                                각 추천에는 선택 이유와 기대 효과가 함께 제공됩니다.
                            </div>
                        </div>
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
                          {productAdditions.length === 0 ? (
                            <div className={styles['empty-message-card']}>
                              <p>추가할 추천 제품이 없습니다.</p>
                            </div>
                          ) : (
                            <div className={styles['add-recommend-grid']}>
                              {productAdditions.map((item, idx) => (
                                <div className={styles['add-recommend-card']} key={`add-${idx}-${item.addProduct}`}>
                                  <div className={styles['add-recommend-header']}>
                                    <div style={{display: 'flex', alignItems: 'center'}}>
                                      <div className={styles['add-product-icon']}>
                                        {idx + 1}
                                      </div>
                                      <div className={styles['add-product-title']}>{item.addProduct}</div>
                                    </div>
                                    <button onClick={() => handleBuyButtonClick(item.addProduct)} className={styles['buy-btn']}>구매하기</button>
                                  </div>
                                  <div className={styles['add-reason']}>{item.reason}</div>
                                </div>
                              ))}
                            </div>
                          )}
                        </div>

                        {/* 완료 버튼 */}
                        <div className={styles['step2-bottom-btns']}>
                            <button className={styles['step2-done-btn']} onClick={handleDoneButtonClick}>완료</button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}