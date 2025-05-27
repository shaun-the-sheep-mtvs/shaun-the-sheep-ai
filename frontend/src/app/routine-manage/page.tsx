"use client";

import React, { useState } from 'react';
import styles from './page.module.css';
import { useRouter } from 'next/navigation';

const ROUTINE_TIMES = [
  { label: '아침', value: 'MORNING' },
  { label: '저녁', value: 'EVENING' },
  { label: '둘다', value: 'BOTH' },
];

const getButtonClass = (routineTime: string, selected: string) => {
  let base = styles['routine-btn'];
  if (routineTime === 'MORNING' && selected === 'MORNING') return base + ' ' + styles['selected-morning'];
  if (routineTime === 'EVENING' && selected === 'EVENING') return base + ' ' + styles['selected-evening'];
  if (routineTime === 'BOTH' && selected === 'BOTH') return base + ' ' + styles['selected-both'];
  return base;
};

const getInputRowClass = (routineTime: string) => {
  let base = styles['input-row'];
  if (routineTime === 'MORNING') return base + ' ' + styles['morning'];
  if (routineTime === 'EVENING') return base + ' ' + styles['evening'];
  return base;
};

// 제품 객체 타입 정의
interface Product {
  name: string;
  kind: string;
  method: string;
  orders: number;
}

const RoutineManagePage = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const router = useRouter();
  const [selectedTime, setSelectedTime] = useState<'MORNING' | 'EVENING' | 'BOTH'>('MORNING');
  const [products, setProducts] = useState<Product[]>([
    { name: '', kind: '', method: '', orders: 1 },
    { name: '', kind: '', method: '', orders: 2 },
  ]);
  const [registeredRoutines, setRegisteredRoutines] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  // 사이드바 외부 클릭시 닫기
  const handleOverlayClick = () => {
    setIsSidebarOpen(false);
  };

  // POST 요청 함수
  const handleAddRoutine = async () => {
    // 공백 입력 방지
    const isAnyFieldEmpty = products.some(product => 
      !product.name.trim() || !product.kind.trim() || !product.method.trim()
    );

    if (isAnyFieldEmpty) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/routine/create', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          routines: products.map(product => ({
            name: product.name,
            kind: product.kind,
            time: selectedTime,
            method: product.method,
            orders: product.orders
          }))
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Success:', data);
      alert('등록되었습니다.');
    } catch (error) {
      console.error('Error:', error);
      alert('요청 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
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

  const handleComplete = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/recommend', {
        method: 'POST', 
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          routines: products.map(product => ({
            name: product.name,
            kind: product.kind,
            time: selectedTime,
            method: product.method,
            orders: product.orders
          }))
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: '추천 생성 중 오류가 발생했습니다.'}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }
      
      const recommendData = await response.json();
      console.log('Recommendation data received (and saved in DB by backend):', recommendData);
      alert('추천 데이터가 생성(저장)되었습니다. 다음 페이지로 이동합니다.');
      router.push('/step2');
    } catch (error: any) {
      console.error('Error in handleComplete:', error);
      alert(error.message || '요청 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className={styles.loadingOverlay}>
        <div className={styles.loadingSpinner}></div>
        <p>처리 중입니다. 잠시만 기다려주세요...</p>
      </div>
    );
  }

  return (
    <div className={styles.wrapper}>
      
      <div className={styles['page-layout']}>
        {/* 왼쪽: 루틴 등록 폼 */}
        <div className={styles['form-section']}>
          <div className={styles.card}>
            {/* 상단 네비게이션 */}
            <div className={styles.topnav}>
              <span className={styles['topnav-title']}>루틴관리</span>
            </div>
            {/* STEP 1, STEP 2 */}
            <div className={styles.steps}>
              <span className={styles.step}>STEP 1</span>
              <span className={styles.step + ' ' + styles.inactive}>STEP 2</span>
              <div className={styles['step-divider']} />
            </div>
            {/* 1. 루틴 시간 선택 */}
            <div style={{ marginBottom: 32 }}>
              <div className={styles['section-title']}>1. 루틴 시간을 선택해주세요.</div>
              <div className={styles['routine-times']}>
                {ROUTINE_TIMES.map((t) => (
                  <button
                    key={t.value}
                    className={getButtonClass(t.value, selectedTime)}
                    onClick={() => setSelectedTime(t.value as any)}
                    type="button"
                  >
                    {t.label}
                  </button>
                ))}
              </div>
            </div>
            {/* 2. 단계별 제품 입력 */}
            <div style={{ marginBottom: 32 }}>
              <div className={styles['section-title']}>2. 단계에 해당하는 제품을 입력해주세요.</div>
              <div className={styles.inputs}>
                {products.map((product, idx) => (
                  <div
                    key={idx}
                    className={getInputRowClass(selectedTime)}
                  >
                    <div className={styles['input-top']}>
                      <input
                        className={styles.input}
                        placeholder="제품명"
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
                        -
                      </button>
                    </div>
                    <input
                      className={styles['input-method']}
                      placeholder="방법"
                      value={product.method}
                      onChange={(e) => handleProductChange(idx, 'method', e.target.value)}
                    />
                  </div>
                ))}
              </div>
              {/* 플러스 버튼 */}
              <div className={styles['add-btn-row']}>
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
            {/* 버튼 그룹 */}
            <div className={styles['button-group']}>
              <button
                className={styles['submit-btn']}
                onClick={handleAddRoutine}
                type="button"
                disabled={isLoading}
              >
                등록
              </button>
              <button
                className={styles['complete-btn']}
                onClick={handleComplete}
                type="button"
                disabled={isLoading}
              >
                다음
              </button>
            </div>
          </div>
        </div>

      </div>
      
    </div>
    
  );
};

export default RoutineManagePage; 