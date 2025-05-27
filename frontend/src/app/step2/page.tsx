'use client'

import React, { useState, useEffect } from 'react';
import styles from './step2.module.css';
import '../globals.css';
import axios from 'axios';

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
    
    // 제품 변경 및 추가 추천 상태
    const [productChanges, setProductChanges] = useState<ProductChangeItem[]>([]);
    const [productAdditions, setProductAdditions] = useState<ProductAdditionItem[]>([]);
    
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
           setError('로그인이 필요합니다.');
           return;
        }

        const fetchUserData = axios.get<User>('http://localhost:8080/api/auth/me', { // 타입 User로 수정
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchSkinData = axios.get<UserSkinData>('http://localhost:8080/api/user/skin-data', {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchExistingRoutines = axios.get<ApiRoutineItem[]>('http://localhost:8080/api/routine/existing', {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchRecommendedRoutines = axios.get<ApiRecommendedRoutineItem[]>('http://localhost:8080/api/deep/routine-change', {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });

        const fetchDeepRecommendations = axios.get<ApiDeepRecommendItem[]>('http://localhost:8080/api/deep/product-recommend', {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        });


        Promise.all([fetchSkinData, fetchExistingRoutines, fetchRecommendedRoutines, fetchDeepRecommendations, fetchUserData])
            .then(([skinDataResponse, existingRoutinesResponse, recommendedRoutinesResponse, deepRecommendationsResponse, userDataResponse]) => {
                setUserSkinData(skinDataResponse.data);
                console.log('User Data Response:', userDataResponse.data); // 사용자 데이터 응답 확인
                setUserData(userDataResponse.data); // 사용자 정보 상태 설정

                // 기존 루틴
                const existingRoutinesFromApi = existingRoutinesResponse.data;
                const transformedExistingRoutines: DisplayRoutineItem[] = existingRoutinesFromApi.map(apiItem => ({
                    title: apiItem.name,
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
            });
    }, []);

    const handleBuyButtonClick = (productName: string) => {
        window.open(`https://www.coupang.com/np/search?component=&q=${productName}`, '_blank ');
    }

    // 현재 선택된 루틴
    const currentExistingRoutine = routineTab === 'morning' ? morningRoutine : nightRoutine;
    const currentRecommendedRoutine = routineTab === 'morning' ? recommendedMorningRoutine : recommendedNightRoutine;

    return (
        <div className={styles['step2-wrapper']}>
            {/* 사이드바 */}
            <aside className={styles['step2-sidebar']}>
                <div className={styles['step2-logo']}>스킨케어</div>
                <ul className={styles['step2-menu']}>
                    <li><span className={styles['step2-menu-icon']}>🏠</span>홈화면</li>
                    <li className={styles['step2-menu-active']}><span className={styles['step2-menu-icon']}>📝</span>검사하기</li>
                    <li><span className={styles['step2-menu-icon']}>👤</span>회원정보</li>
                </ul>
            </aside>
            {/* 메인 컨텐츠 */}
            <main className={styles['step2-main-content']}>
                {/* 상단 헤더 */}
                <div className={styles['step2-header']}>
                    <div className={styles['step2-header-profile']}>
                        <span className={styles['step2-header-profile-icon']}>👤</span>
                        <span className={styles['step2-header-profile-name']}>
                            <span style={{color:'#ff7eb3'}}>
                                {userData === null ? 'Loading...' : userData ? userData.username : '고객'}
                            </span>

                            님 안녕하세요
                        </span>
                    </div>
                    <div className={styles['step2-header-alarm']}>
                        <span className={styles['step2-header-alarm-icon']}>🔔</span>
                    </div>
                </div>
                {/* STEP 표시 */}
                <div className={styles['step2-step-bar']}>
                    <span className={styles['step2-step-inactive']}>STEP 1</span>
                    <span className={styles['step2-step-active']}>STEP 2</span>
                </div>
                {/* 맞춤 루틴 추천 */}
                <div className={styles['step2-recommend-title']}>맞춤 루틴 추천</div>
                <div className={styles['step2-recommend-desc']}>AI가 분석한 당신의 피부 타입에 맞는 루틴과 제품을 추천해 드립니다</div>
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
                                    <div className={styles['step2-routine-desc']}>{item.desc}</div>
                                </div>
                                <button onClick={() => handleBuyButtonClick(item.title)} className={styles['buy-btn']} >
                                    구매하기
                                </button>
                            </li>
                        ))}
                    </ol>
                </div>
                {/* 맞춤 루틴 추천 */}
                <div className={styles['step2-section-title']}>맞춤 루틴 추천</div>
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
                                    <div className={styles['step2-routine-desc']}>{item.desc}</div>
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
                    <button className={styles['step2-prev-btn']}>이전 단계</button>
                    <button className={styles['step2-done-btn']}>완료</button>
                </div>
            </main>
        </div>
    );
}