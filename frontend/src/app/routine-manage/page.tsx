"use client";

import React, { useState } from 'react';
import styles from './page.module.css';
import { useRouter, usePathname } from 'next/navigation';
import Link from 'next/link';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X, Copy, Trash2, LightbulbIcon } from "lucide-react";
import apiConfig from '../../config/api';

const ROUTINE_TIMES = [
  { label: '아침', value: 'MORNING' },
  { label: '저녁', value: 'NIGHT' },
];

const METHOD_OPTIONS = [
  { value: '', label: '방법 선택' },
  { value: 'CUSTOM', label: '직접 입력' },
  { value: '문지르기', label: '문지르기' },
  { value: '두드리기', label: '두드리기' },
  { value: '듬뿍', label: '듬뿍' },
  { value: '조금', label: '조금' }
];

export default function RoutineManagePage() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();
  const router = useRouter();
  const [selectedTime, setSelectedTime] = useState<'MORNING' | 'NIGHT' | null>(null);
  const [currentStep, setCurrentStep] = useState<'time' | 'product'>('time');
  const [products, setProducts] = useState<Product[]>([
    { name: '', kind: '', method: '', orders: 1 },
    { name: '', kind: '', method: '', orders: 2 },
  ]);
  const [previewGroups, setPreviewGroups] = useState<PreviewProductGroup[]>([]);
  const [registeredRoutines, setRegisteredRoutines] = useState<any[]>([]);
  

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  // 사이드바 외부 클릭시 닫기
  const handleOverlayClick = () => {
    setIsSidebarOpen(false);
  };

  const handleAddToPreview = () => {
    if (!selectedTime) {
      alert('시간을 선택해주세요.');
      return;
    }

    // 공백 입력 방지
    const isAnyFieldEmpty = products.some(product => 
      !product.name.trim() || !product.kind.trim() || !product.method.trim()
    );

    if (isAnyFieldEmpty) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    // 미리보기에 현재 시간대와 함께 추가
    setPreviewGroups(prev => [...prev, {
      time: selectedTime as 'MORNING' | 'NIGHT',
      products: [...products]
    }]);
    
    // 입력창 초기화
    setProducts([
      { name: '', kind: '', method: '', orders: 1 },
      { name: '', kind: '', method: '', orders: 2 },
    ]);

    // 시간 선택 단계로 돌아가기
    setCurrentStep('time');
  };

  const handleComplete = async () => {
    // 미리보기 제품이 없으면 경고
    if (previewGroups.length === 0) {
      alert('등록할 제품이 없습니다.');
      return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    try {
      // 1. 첫 번째 API 호출: 루틴 생성 (사용자가 입력한 루틴 정보 저장)
      const responseCreate = await fetch('http://localhost:8080/api/routine/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          routines: previewGroups.flatMap((group) =>
            group.products.map((product, productIndex) => ({
              name: product.name,
              kind: product.kind,
              time: group.time,
              method: product.method,
              orders: productIndex + 1
            }))
          )
        }),
      });

      if (!responseCreate.ok) {
        const errorText = await responseCreate.text();
        throw new Error(`루틴 생성 실패! status: ${responseCreate.status}, message: ${errorText}`);
      }

      const resultCreate = await responseCreate.text();
      console.log('루틴 생성 성공:', resultCreate);

      // 2. 두 번째 API 호출: AI 추천 요청 트리거
      // 서버가 @AuthenticationPrincipal을 통해 사용자 ID를 얻고,
      // 해당 ID로 루틴 정보와 피부 고민 정보를 DB에서 조회하여 AI 프롬프트를 구성합니다.
      // 따라서 프론트엔드는 요청 body를 보낼 필요가 없습니다.
      try {
        const responseRecommend = await fetch('http://localhost:8080/api/deep/recommend', {
          method: 'POST', // 컨트롤러가 @PostMapping이므로 POST 유지
          headers: {
            // 'Content-Type': 'application/json', // 본문이 없으므로 Content-Type 불필요
            'Authorization': `Bearer ${token}` // 사용자 식별을 위해 인증 토큰 전송
          },
          // body는 서버에서 처리하므로 프론트에서 보낼 필요 없음
        });

        if (!responseRecommend.ok) {
          const errorTextRecommend = await responseRecommend.text();
          console.error(`AI 추천 요청 실패! status: ${responseRecommend.status}, message: ${errorTextRecommend}`);
          // 필요시 사용자에게 알림: alert('루틴은 등록되었으나, AI 추천을 받는 데 실패했습니다.');
        } else {
          const resultRecommend = await responseRecommend.text(); // AI 서비스의 응답 (JSON 문자열)
          console.log('AI 추천 요청 성공:', resultRecommend);
          // 여기서 resultRecommend (AI의 응답 JSON 문자열)를 파싱하여
          // 필요한 경우 상태를 업데이트하거나 사용자에게 보여줄 수 있습니다.
          // 예를 들어, localStorage에 저장하거나, 상태 변수에 저장 후 step2 페이지로 전달 등.
        }
      } catch (recommendError) {
        console.error('AI 추천 API 호출 중 네트워크 오류 또는 기타 문제 발생:', recommendError);
        // 필요시 사용자에게 알림: alert('루틴은 등록되었으나, AI 추천 요청 중 오류가 발생했습니다.');
      }

      // 첫 번째 API(루틴 생성)가 성공했으므로 알림 및 페이지 이동
      alert('등록되었습니다.');
      router.push('/step2'); // step2 페이지로 이동하여 추천 결과를 보여줄 수 있음

    } catch (error) { // 주로 첫 번째 API 호출의 에러를 처리
      console.error('Error in handleComplete:', error);
      alert('요청 중 오류가 발생했습니다.');
    }
  };

  const handleProductChange = (idx: number, field: keyof Product, value: string) => {
    setProducts((prev) => prev.map((p, i) => (i === idx ? { ...p, [field]: value } : p)));
  };

  const handleRemoveProduct = (idx: number) => {
    setProducts((prev) => {
      const newProducts = prev.filter((_, i) => i !== idx);
      // orders 재정렬
      return newProducts.map((product, index) => ({
        ...product,
        orders: index + 1
      }));
    });
  };

  const handleAddProduct = () => {
    setProducts((prev) => [...prev, { 
      name: '', 
      kind: '', 
      method: '', 
      orders: prev.length + 1 
    }]);
  };

  const handleTimeSelect = (time: 'MORNING' | 'NIGHT') => {
    if (isTimeSlotUsed(time)) {
      return; // 이미 추가된 시간대는 선택 불가
    }
    setSelectedTime(time);
    setCurrentStep('product');
  };

  const handleBack = () => {
    setCurrentStep('time');
  };

  const handleRemovePreviewGroup = (groupIndex: number) => {
    setPreviewGroups(prev => prev.filter((_, index) => index !== groupIndex));
  };

  const handleDragStart = (e: React.DragEvent<HTMLDivElement>, index: number) => {
    e.currentTarget.classList.add(styles.dragging);
    e.dataTransfer.setData('text/plain', index.toString());
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleDragEnd = (e: React.DragEvent<HTMLDivElement>) => {
    e.currentTarget.classList.remove(styles.dragging);
    // 모든 드래그 오버 표시 제거
    document.querySelectorAll(`.${styles['drag-over-top']}, .${styles['drag-over-bottom']}`)
      .forEach(el => {
        el.classList.remove(styles['drag-over-top'], styles['drag-over-bottom']);
      });
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>, idx: number) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    
    const draggedIndex = parseInt(e.dataTransfer.getData('text/plain'));
    if (draggedIndex === idx) return;

    const items = document.querySelectorAll(`.${styles['input-row']}`);
    const draggedItem = items[draggedIndex] as HTMLElement;
    const currentItem = items[idx] as HTMLElement;
    
    if (!draggedItem || !currentItem) return;

    // 이전 표시 제거
    items.forEach(item => {
      item.classList.remove(styles['drag-over-top'], styles['drag-over-bottom']);
    });

    const rect = currentItem.getBoundingClientRect();
    const midY = rect.top + rect.height / 2;

    if (e.clientY < midY) {
      // 마우스가 아이템의 위쪽 절반에 있을 때
      currentItem.classList.add(styles['drag-over-top']);
    } else {
      // 마우스가 아이템의 아래쪽 절반에 있을 때
      currentItem.classList.add(styles['drag-over-bottom']);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>, dropIndex: number) => {
    e.preventDefault();
    const dragIndex = parseInt(e.dataTransfer.getData('text/plain'));
    if (dragIndex === dropIndex) return;

    const rect = e.currentTarget.getBoundingClientRect();
    const midY = rect.top + rect.height / 2;
    const insertAfter = e.clientY > midY;

    const newProducts = [...products];
    const [draggedItem] = newProducts.splice(dragIndex, 1);
    
    // 드롭 위치 조정
    const actualDropIndex = insertAfter ? dropIndex + 1 : dropIndex;
    newProducts.splice(actualDropIndex, 0, draggedItem);

    // 순서 재정렬
    const reorderedProducts = newProducts.map((product, index) => ({
      ...product,
      orders: index + 1
    }));

    setProducts(reorderedProducts);

    // 모든 드래그 오버 표시 제거
    document.querySelectorAll(`.${styles['drag-over-top']}, .${styles['drag-over-bottom']}`)
      .forEach(el => {
        el.classList.remove(styles['drag-over-top'], styles['drag-over-bottom']);
      });
  };

  const handleMethodChange = (idx: number, value: string) => {
    if (value === 'CUSTOM') {
      // 직접 입력을 선택한 경우 method를 빈 문자열로 설정
      handleProductChange(idx, 'method', '');
    } else {
      handleProductChange(idx, 'method', value);
    }
  };

  // 각 시간대의 루틴이 이미 추가되었는지 확인하는 함수
  const isTimeSlotUsed = (time: 'MORNING' | 'NIGHT') => {
    return previewGroups.some(group => group.time === time);
  };

  // 버튼 클래스를 결정하는 함수
  const getButtonClass = (routineTime: string, selected: string | null) => {
    let base = styles['routine-btn'];
    if (isTimeSlotUsed(routineTime as 'MORNING' | 'NIGHT')) {
      return `${base} ${styles.disabled}`;
    }
    if (routineTime === selected) {
      if (routineTime === 'MORNING') return `${base} ${styles['selected-morning']}`;
      if (routineTime === 'NIGHT') return `${base} ${styles['selected-evening']}`;
    }
    return base;
  };

  const getInputRowClass = (routineTime: string) => {
    let base = styles['input-row'];
    if (routineTime === 'MORNING') return base + ' ' + styles['morning'];
    if (routineTime === 'NIGHT') return base + ' ' + styles['evening'];
    return base;
  };

  const getCompleteButtonClass = (selectedTime: string) => {
    let base = styles['complete-btn'];
    if (selectedTime === 'MORNING') return base + ' ' + styles['morning'];
    if (selectedTime === 'NIGHT') return base + ' ' + styles['evening'];
    return base;
  };

  // 제품 객체 타입 정의
  interface Product {
    name: string;
    kind: string;
    method: string;
    orders: number;
  }

  // 미리보기 제품 그룹 타입 정의
  interface PreviewProductGroup {
    time: 'MORNING' | 'NIGHT';
    products: Product[];
  }

  // 아침 루틴을 찾는 함수
  const getMorningRoutine = () => {
    return previewGroups.find(group => group.time === 'MORNING');
  };

  // 아침 루틴을 저녁 루틴으로 복사하는 함수
  const copyMorningRoutine = () => {
    const morningRoutine = getMorningRoutine();
    if (!morningRoutine) return;

    // 아침 루틴의 제품들을 복사
    const copiedProducts = morningRoutine.products.map(product => ({
      ...product,
      orders: product.orders // 순서는 그대로 유지
    }));

    // 현재 입력 중인 제품들을 복사된 제품들로 교체
    setProducts(copiedProducts);
  };

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
      
      <div className={styles['page-layout']}>
        {/* 왼쪽: 루틴 등록 폼 */}
        <div className={styles['form-section']}>
          <div className={styles.card}>
            {/* 상단 네비게이션 */}
            <div className={styles.topnav}>
              <span className={styles['topnav-title']}>루틴 분석</span>
            </div>
            {/* STEP 1, STEP 2 */}
            <div className={styles.steps}>
              <div className={styles.step}>
                {currentStep === 'time' ? ' STEP 1' : 'STEP 2'}
              </div>
              <div className={styles['step-divider']} />
            </div>
            
            {currentStep === 'time' ? (
              <div style={{ marginBottom: 32 }}>
                <div className={styles['section-title']}>1. 루틴 시간을 선택해주세요.</div>
                <div className={styles['section-subtitle']}>
                  하루 중 스킨케어 제품을 사용하는 시간대를 선택해주세요
                </div>
                <div className={styles['routine-times']}>
                  {ROUTINE_TIMES.map((t) => (
                    <button
                      key={t.value}
                      className={getButtonClass(t.value, selectedTime)}
                      onClick={() => handleTimeSelect(t.value as 'MORNING' | 'NIGHT')}
                      type="button"
                      disabled={isTimeSlotUsed(t.value as 'MORNING' | 'NIGHT')}
                    >
                      {t.label}
                    </button>
                  ))}
                </div>
              </div>
            ) : (
              <>
                <div style={{ marginBottom: 48 }}>
                  <div className={styles['section-title']}>
                    <div className={styles['time-selection-header']}>
                      <button 
                        className={styles['back-button']}
                        onClick={handleBack}
                        type="button"
                      >
                        ← 이전
                      </button>
                      <span className={styles.selectedTime}>선택된 시간: {
                        selectedTime === 'MORNING' ? '아침' : selectedTime === 'NIGHT' ? '저녁' : '선택 안됨'
                      }</span>
                    </div>
                  </div>
                  <div className={styles['section-title']} style={{ marginTop: 32 }}>2. 제품을 바르는 순서대로 입력해주세요.</div>
                  {selectedTime === 'NIGHT' && getMorningRoutine() && (
                    <button
                      className={styles['copy-routine-btn']}
                      onClick={copyMorningRoutine}
                      type="button"
                      style={{ marginTop: 16, marginBottom: 24 }}
                    >
                      <Copy size={16} />
                      아침 루틴과 동일하게 설정
                    </button>
                  )}
                  <div className={styles['input-guidance']}>
                    <LightbulbIcon className={styles['guidance-icon']} size={18} />
                    <div className={styles['guidance-content']}>
                      <div className={styles['guidance-title']}>
                        꿀팁
                      </div>
                      <div className={styles['guidance-description']}>
                        자세한 사용 방법을 입력할수록 더 정확한 분석이 가능해요
                      </div>
                      <div className={styles['guidance-example']}>
                        예시) 토너를 화장솜에 듬뿍 묻혀 3번 정도 두드리며 흡수시켜요
                      </div>
                    </div>
                  </div>
                  <div className={styles.inputs} style={{ marginTop: 32 }}>
                    {products.map((product, idx) => (
                      <div
                        key={idx}
                        className={getInputRowClass(selectedTime as string)}
                        draggable
                        onDragStart={(e) => handleDragStart(e, idx)}
                        onDragEnd={handleDragEnd}
                        onDragOver={(e) => handleDragOver(e, idx)}
                        onDrop={(e) => handleDrop(e, idx)}
                        style={{ marginBottom: 24 }}
                      >
                        <div className={styles['drag-handle']} />
                        <div className={styles['input-top']}>
                          <div className={styles['order-number']}>{idx + 1}</div>
                          <input
                            className={styles.input}
                            placeholder="제품명을 입력하세요"
                            value={product.name}
                            onChange={(e) => handleProductChange(idx, 'name', e.target.value)}
                          />
                          <select
                            className={styles['input-select']}
                            value={product.kind}
                            onChange={(e) => handleProductChange(idx, 'kind', e.target.value)}
                          >
                            <option value="">종류 선택</option>
                            <option value="토너">토너</option>
                            <option value="앰플">앰플</option>
                            <option value="크림">크림</option>
                            <option value="스킨">스킨</option>
                            <option value="세럼">세럼</option>
                            <option value="로션">로션</option>
                          </select>
                          <button
                            className={styles['remove-btn']}
                            onClick={() => handleRemoveProduct(idx)}
                            type="button"
                            aria-label="삭제"
                          >
                            <Trash2 size={18} />
                          </button>
                        </div>
                        <div className={styles['method-container']}>
                          <select
                            className={styles['input-select']}
                            value={product.method === '' ? 'CUSTOM' : product.method}
                            onChange={(e) => handleMethodChange(idx, e.target.value)}
                          >
                            {METHOD_OPTIONS.map((method, methodIdx) => (
                              <option key={methodIdx} value={method.value}>
                                {method.label}
                              </option>
                            ))}
                          </select>
                          {product.method === '' && (
                            <input
                              className={styles['input-method']}
                              placeholder="사용 방법을 입력해주세요"
                              value={product.method}
                              onChange={(e) => handleProductChange(idx, 'method', e.target.value)}
                            />
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                  <div className={styles['add-btn-row']} style={{ marginTop: 32 }}>
                    <button
                      className={styles['add-btn']}
                      onClick={handleAddProduct}
                      type="button"
                      aria-label="추가"
                    >
                      +
                    </button>
                  </div>
                </div>
                <div className={styles['button-group']} style={{ marginTop: 48 }}>
                  <button
                    className={getCompleteButtonClass(selectedTime as string)}
                    onClick={handleAddToPreview}
                    type="button"
                  >
                    저장
                  </button>
                </div>
              </>
            )}
          </div>
        </div>

        {/* 오른쪽: 미리보기 섹션 */}
        {previewGroups.length > 0 && (
          <div className={styles['preview-section']}>
            <div className={styles.card}>
              <div className={styles['preview-header']}>
                <h3>등록 예정 루틴</h3>
              </div>
              {previewGroups.map((group, groupIndex) => (
                <div key={groupIndex} className={styles['preview-group']}>
                  <div className={styles['preview-header']}>
                    <div className={styles['preview-time']}>
                      {group.time === 'MORNING' ? '아침' : '저녁'} 루틴
                    </div>
                    <button
                      className={styles['remove-preview-btn']}
                      onClick={() => handleRemovePreviewGroup(groupIndex)}
                      type="button"
                      aria-label="삭제"
                    >
                      ×
                    </button>
                  </div>
                  <div className={styles['preview-list']}>
                    {group.products.map((product, idx) => (
                      <div key={idx} className={styles['preview-item']}>
                        <div className={styles['preview-order']}>{idx + 1}</div>
                        <div className={styles['preview-content']}>
                          <div className={styles['preview-name']}>
                            {product.name}
                          </div>
                          <div className={styles['preview-kind']}>
                            {product.kind}
                          </div>
                          <div className={styles['preview-method']}>
                            {product.method}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
              {/* 안내 메시지 추가 */}
              {previewGroups.length === 1 && (
                <div className={styles['preview-guide']}>
                  <div className={styles['guide-message']}>
                    {previewGroups[0].time === 'MORNING' ? (
                      <>
                        <span>💡 저녁 루틴도 입력해 주시면</span>
                        <span>더 정확한 분석이 가능합니다.</span>
                      </>
                    ) : (
                      <>
                        <span>💡 아침 루틴도 입력해 주시면</span>
                        <span>더 정확한 분석이 가능합니다.</span>
                      </>
                    )}
                  </div>
                </div>
              )}
              <div className={styles['preview-actions']}>
                <button
                  className={styles['complete-btn']}
                  onClick={handleComplete}
                  type="button"
                >
                  분석 시작
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

    </div>

  );
} 