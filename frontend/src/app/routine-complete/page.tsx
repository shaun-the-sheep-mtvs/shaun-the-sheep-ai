"use client";

import React from 'react';
import styles from './page.module.css';

const RoutineCompletePage = () => {
  return (
    <div className={styles.container}>
      <div className={styles.card}>
        {/* 상단 네비게이션 */}
        <div className={styles.topnav}>
          <span className={styles['topnav-title']}>루틴관리</span>
        </div>
        {/* STEP 1, STEP 2 */}
        <div className={styles.steps}>
          <span className={styles.step + ' ' + styles.inactive}>STEP 1</span>
          <span className={styles.step}>STEP 2</span>
          <div className={styles['step-divider']} />
        </div>

        {/* 완료 메시지 */}
        <div className={styles['complete-message']}>
          <h1>루틴이 등록되었습니다!</h1>
          <p>이제 루틴을 시작할 수 있습니다.</p>
        </div>

        {/* 홈으로 버튼 */}
        <button
          className={styles['home-btn']}
          onClick={() => window.location.href = '/'}
          type="button"
        >
          홈으로
        </button>
      </div>
    </div>
  );
};

export default RoutineCompletePage; 