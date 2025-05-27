// src/app/checklist/layout.tsx
'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { Home as HomeIcon, Menu as MenuIcon } from 'lucide-react';
import Sidebar from '@/components/Sidebar';
import styles from './ChecklistLayout.module.css';

export default function ChecklistLayout({ children }: { children: React.ReactNode }) {
  // ① 사이드바 열림 상태
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [activeNav, setActiveNav]     = useState('examination');

  const toggleSidebar = () => setIsSidebarOpen(v => !v);

  const handleNavClick = (navId: string) => {
    setActiveNav(navId);
    setIsSidebarOpen(false);
  };

  return (
    <>
      {/* ② Sidebar */}
      <Sidebar
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
        activeNav={activeNav}
        onNavClick={handleNavClick}
      />

      {/* ③ 헤더: 메뉴 버튼 / 홈 아이콘 / 타이틀 / 다시 검사하기 */}
      <nav className={styles.navbar}>
        <button className={styles.menuButton} onClick={toggleSidebar}>
          <MenuIcon size={24} />
        </button>

        <div className={styles.logo}>피부 진단 검사</div>

        <button
          type="button"
          className={styles.navButton}
          onClick={() => { window.location.href = '/checklist'; }}
        >
          다시 검사하기
        </button>
      </nav>

      {/* ④ 본문 */}
      <main>{children}</main>
    </>
  );
}




