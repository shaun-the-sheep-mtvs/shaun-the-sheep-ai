"use client";

import React, { useState } from 'react';
import styles from './page.module.css';
import { useRouter, usePathname } from 'next/navigation';
import Link from 'next/link';
import { User, MessageCircle, ClipboardCheck, ShoppingBag, HomeIcon, Menu, X } from "lucide-react";

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
  if (routineTime === 'BOTH') return base + ' ' + styles['both'];
  return base;
};

const getCompleteButtonClass = (selectedTime: string) => {
  let base = styles['complete-btn'];
  if (selectedTime === 'MORNING') return base + ' ' + styles['morning'];
  if (selectedTime === 'EVENING') return base + ' ' + styles['evening'];
  if (selectedTime === 'BOTH') return base + ' ' + styles['both'];
  return base;
};

// 제품 객체 타입 정의
interface Product {
  name: string;
  kind: string;
  method: string;
  orders: number;
}

export default function RoutineManagePage() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();
  const router = useRouter();
  const [selectedTime, setSelectedTime] = useState<'MORNING' | 'EVENING' | 'BOTH'>('MORNING');
  const [products, setProducts] = useState<Product[]>([
    { name: '', kind: '', method: '', orders: 1 },
    { name: '', kind: '', method: '', orders: 2 },
  ]);
  const [registeredRoutines, setRegisteredRoutines] = useState<any[]>([]);
  
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  // 사이드바 외부 클릭시 닫기
  const handleOverlayClick = () => {
    setIsSidebarOpen(false);
  };

  const handleComplete = async () => {
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
        const errorText = await response.text();
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
  
      const result = await response.text();
    console.log('Success:', result);
    alert('등록되었습니다.');
       router.push('/step2'); // 등록 성공 후 다음 페이지로 이동
      
    } catch (error) {
      console.error('Error:', error);
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
              <span className={styles['topnav-title']}>정밀 피부검사</span>
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
                className={getCompleteButtonClass(selectedTime)}
                onClick={handleComplete}
                type="button"
              >
                완료
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 