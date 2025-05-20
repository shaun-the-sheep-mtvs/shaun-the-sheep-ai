"use client";

import React, { useState } from 'react';
import styles from './page.module.css';

const ROUTINE_TIMES = [
  { label: '아침', value: 'morning' },
  { label: '저녁', value: 'evening' },
  { label: '둘다', value: 'both' },
];

const getButtonClass = (routineTime: string, selected: string) => {
  let base = styles['routine-btn'];
  if (routineTime === 'morning' && selected === 'morning') return base + ' ' + styles['selected-morning'];
  if (routineTime === 'evening' && selected === 'evening') return base + ' ' + styles['selected-evening'];
  if (routineTime === 'both' && selected === 'both') return base + ' ' + styles['selected-both'];
  return base;
};

const getInputRowClass = (routineTime: string) => {
  let base = styles['input-row'];
  if (routineTime === 'morning') return base + ' ' + styles['morning'];
  if (routineTime === 'evening') return base + ' ' + styles['evening'];
  return base;
};

const RoutineManagePage = () => {
  const [selectedTime, setSelectedTime] = useState<'morning' | 'evening' | 'both'>('morning');
  const [products, setProducts] = useState<string[]>(['', '']);

  // POST 요청 함수
  const handleAddRoutine = async () => {
    await fetch('/api/routine', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        time: selectedTime,
        products,
      }),
    });
  };

  const handleProductChange = (idx: number, value: string) => {
    setProducts((prev) => prev.map((p, i) => (i === idx ? value : p)));
  };

  const handleRemoveProduct = (idx: number) => {
    setProducts((prev) => prev.filter((_, i) => i !== idx));
  };

  const handleAddProduct = () => {
    setProducts((prev) => [...prev, '']);
  };

  return (
    <div className={styles.container}>
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
                <input
                  className={styles.input}
                  placeholder="바르는 제품을 입력하세요"
                  value={product}
                  onChange={(e) => handleProductChange(idx, e.target.value)}
                />
                <button
                  className={styles['remove-btn']}
                  onClick={() => handleRemoveProduct(idx)}
                  type="button"
                  aria-label="삭제"
                >
                  -
                </button>
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
        {/* 등록 버튼 */}
        <button
          className={styles['submit-btn']}
          onClick={handleAddRoutine}
          type="button"
        >
          등록
        </button>
        {/* 3. 추가 루틴 입력 */}
        <div className={styles['section-bottom']}>
          3. 추가 루틴이 있다면 입력해주세요 (보류)
        </div>
      </div>
    </div>
  );
};

export default RoutineManagePage; 