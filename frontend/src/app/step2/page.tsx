'use client'

import React, { useState } from 'react';
import styles from './step2.module.css';
import '../globals.css';

export default function Step2() {
    // 탭 상태: 'morning' 또는 'night'
    const [routineTab, setRoutineTab] = useState<'morning' | 'night'>('morning');

    // 아침 루틴 데이터
    const morningRoutine = [
        {
            title: '클렌징',
            desc: '저자극 젠틀 폼 클렌저<br/>순한 세정력으로 피부 자극 없이 노폐물 제거',
        },
        {
            title: '토너',
            desc: '진정 토너<br/>피부 진정 및 수분 공급',
        },
        {
            title: '에센스',
            desc: '히알루론산 에센스<br/>깊은 수분 공급으로 건조함 해소',
        },
        {
            title: '크림',
            desc: '수분 젤 크림<br/>가볍게 흡수되는 수분 크림',
        },
        {
            title: '선크림',
            desc: '민감성 피부용 선크림 SPF50<br/>자외선 차단 및 피부 보호',
        },
    ];

    // 저녁 루틴 데이터 (예시)
    const nightRoutine = [
        {
            title: '클렌징',
            desc: '저자극 젠틀 폼 클렌저<br/>메이크업 및 노폐물 제거',
        },
        {
            title: '토너',
            desc: '진정 토너<br/>피부 진정 및 수분 공급',
        },
        {
            title: '에센스',
            desc: '히알루론산 에센스<br/>깊은 수분 공급',
        },
        {
            title: '크림',
            desc: '수분 젤 크림<br/>가볍게 흡수되는 수분 크림',
        },
        {
            title: '시카 세럼',
            desc: '시카 세럼<br/>손상된 피부 장벽 회복',
        },
    ];

    // 현재 선택된 루틴
    const currentRoutine = routineTab === 'morning' ? morningRoutine : nightRoutine;

    // 제품 변경/추가 추천 데이터
    const recommendedChanges = [
        {
            currentProduct: '일반 폼 클렌저',
            recommendedProduct: '저자극 젠틀 폼 클렌저',
            reason: '현재 사용 중인 클렌저는 피부 장벽을 약화시킬 수 있는 성분이 포함되어 있습니다. 저자극 제품으로 대체하세요.',
        },
        {
            currentProduct: '알코올 함유 토너',
            recommendedProduct: '진정 토너',
            reason: '알코올이 함유된 토너는 민감한 피부를 자극할 수 있습니다. 무알코올 진정 토너로 변경하세요.',
        },
        {
            addProduct: '히알루론산 에센스',
            reason: '수분 부족 문제 해결을 위해 히알루론산 에센스를 추가하세요.',
        },
        {
            addProduct: '시카 세럼',
            reason: '저녁 루틴에 시카 세럼을 추가하여 손상된 피부 장벽을 회복하세요.',
        },
    ];

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
                        <span className={styles['step2-header-profile-name']}><b style={{color:'#ff7eb3'}}>○○</b>님 안녕하세요</span>
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
                    <div className={styles['step2-skin-type-main']}>민감성 + 지성</div>
                    <div className={styles['step2-skin-type-tags']}>
                        <span>수분 부족</span>
                        <span>유분 과다</span>
                        <span>민감성</span>
                    </div>
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
                        <span className={styles['step2-routine-total']}>총 {currentRoutine.length}단계</span>
                    </div>
                    <ol className={styles['step2-routine-list']}>
                        {currentRoutine.map((item, idx) => (
                            <li key={idx}>
                                <div className={styles['step2-routine-num']}>{idx + 1}</div>
                                <div className={styles['step2-routine-info']}>
                                    <div className={styles['step2-routine-title']}>{item.title}</div>
                                    <div
                                        className={styles['step2-routine-desc']}
                                        dangerouslySetInnerHTML={{ __html: item.desc }}
                                    />
                                </div>
                                <button className={styles['step2-buy-btn']}>구매하기</button>
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
                        <span className={styles['step2-routine-total']}>총 {currentRoutine.length}단계</span>
                    </div>
                    <ol className={styles['step2-routine-list']}>
                        {currentRoutine.map((item, idx) => (
                            <li key={idx}>
                                <div className={styles['step2-routine-num']}>{idx + 1}</div>
                                <div className={styles['step2-routine-info']}>
                                    <div className={styles['step2-routine-title']}>{item.title}</div>
                                    <div
                                        className={styles['step2-routine-desc']}
                                        dangerouslySetInnerHTML={{ __html: item.desc }}
                                    />
                                </div>
                                <button className={styles['step2-buy-btn']}>구매하기</button>
                            </li>
                        ))}
                    </ol>
                </div>
                {/* 제품 변경 및 추가 추천 */}
                <div className={styles['step2-section-title']} style={{marginTop: 48}}>제품 변경 및 추가 추천</div>
                <div className={styles['step2-change-box']}>
                  <div className={styles['change-recommend-title']}>기존 루틴에서 변경하면 좋을 제품</div>
                  {recommendedChanges.filter(item => item.currentProduct).map((item, idx) => (
                    <div className={styles['change-recommend-card']} key={idx}>
                      <div className={styles['change-product-row']}>
                        <div className={styles['change-product-col']}>
                          <div className={styles['change-product-row']}>
                            <div className={styles['change-product-icon']}>
                              {/* 현재 제품 아이콘 (예시: SVG) */}
                              <svg width="24" height="24" fill="none" stroke="#bdbdbd" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                            </div>
                            <div>
                              <div className={styles['change-product-label']}>현재 사용 중</div>
                              <div className={styles['change-product-title']}>{item.currentProduct}</div>
                            </div>
                          </div>
                          <div className={styles['change-arrow']}>
                            {/* 아래 화살표 SVG */}
                            <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19" /><polyline points="19 12 12 19 5 12" /></svg>
                          </div>
                          <div className={styles['change-product-row']}>
                            <div className={`${styles['change-product-icon']} ${styles['green']}`}> 
                              {/* 추천 제품 아이콘 (예시: SVG) */}
                              <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                            </div>
                            <div>
                              <div className={`${styles['change-product-label']} ${styles['green']}`}>추천 제품</div>
                              <div className={styles['change-product-title']}>{item.recommendedProduct}</div>
                            </div>
                          </div>
                        </div>
                        <div>
                          <button className={styles['buy-btn']}>구매하기</button>
                        </div>
                      </div>
                      <div className={styles['change-reason-box']}>
                        <p className={styles['change-reason-text']}>{item.reason}</p>
                      </div>
                    </div>
                  ))}
                  <div className={styles['change-recommend-title']} style={{marginTop: 32}}>추가하면 좋을 제품</div>
                  <div className={styles['add-recommend-grid']}>
                    {recommendedChanges.filter(item => item.addProduct).map((item, idx) => (
                      <div className={styles['add-recommend-card']} key={idx}>
                        <div className={styles['add-recommend-header']}>
                          <div style={{display: 'flex', alignItems: 'center'}}>
                            <div className={styles['add-product-icon']}>
                              {/* + 아이콘 SVG */}
                              <svg width="20" height="20" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" /></svg>
                            </div>
                            <div className={styles['add-product-title']}>{item.addProduct}</div>
                          </div>
                          <button className={styles['buy-btn']}>구매하기</button>
                        </div>
                        <div className={styles['add-reason']}>{item.reason}</div>
                      </div>
                    ))}
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