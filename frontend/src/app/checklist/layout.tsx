// src/app/checklist/layout.tsx  (파일 위치는 프로젝트 구조에 따라 조정하세요)
'use client';

import React from 'react';
import Link from 'next/link';
import styles from './ChecklistLayout.module.css';

export default function ChecklistLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <nav className={styles.navbar}>
        <div className={styles.logo}>Shaun</div>
        <div className={styles.buttonGroup}>
          <Link href="/"    className={styles.navButton}>홈</Link>
          <Link href="/checklist" className={styles.navButton}>검사하기</Link>
        </div>
      </nav>
      <main>{children}</main>
    </>
  );
}

