// src/app/checklist/layout.tsx
'use client';

import React from 'react';
import Link from 'next/link';
import { Home as HomeIcon } from 'lucide-react';
import styles from './ChecklistLayout.module.css';

export default function ChecklistLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <nav className={styles.navbar}>
        {/* 왼쪽: 홈 아이콘 */}
        <Link href="/" className={styles.homeButton}>
          <HomeIcon size={24} />
        </Link>

        {/* 가운데: 로고 텍스트 */}
        <div className={styles.logo}>피부 진단 검사</div>

        {/* 오른쪽: 다시 검사하기 (브라우저 리로드로 완전 초기화) */}
        <button
          type="button"
          className={styles.navButton}
          onClick={() => {
            // 퀴즈 페이지를 새로 로드해서 state 초기화
            window.location.href = '/checklist';
          }}
        >
          다시 검사하기
        </button>
      </nav>
      <main>{children}</main>
    </>
  );
}



