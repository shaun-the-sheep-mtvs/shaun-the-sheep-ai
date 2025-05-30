'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter, usePathname } from 'next/navigation';
import { Menu, X, HomeIcon, ClipboardCheck, MessageCircle, User } from 'lucide-react';
import styles from './Navbar.module.css';

interface User {
  username: string;
  id: number;
}

interface NavbarProps {
  user?: User | null;
  loading?: boolean;
  isLoggedIn?: boolean;
  onLogout?: () => void;
}

export default function Navbar({ user, loading = false, isLoggedIn: propIsLoggedIn, onLogout }: NavbarProps) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    // props에서 isLoggedIn이 전달되면 그것을 사용, 아니면 localStorage에서 확인
    if (propIsLoggedIn !== undefined) {
      setIsLoggedIn(propIsLoggedIn);
    } else {
      const token = localStorage.getItem('accessToken');
      setIsLoggedIn(!!token);
    }
  }, [propIsLoggedIn]);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleOverlayClick = () => {
    setIsSidebarOpen(false);
  };

  const handleLogout = () => {
    if (onLogout) {
      onLogout();
    } else {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      setIsLoggedIn(false);
      router.push('/login');
    }
  };

  return (
    <>
      {/* 네비게이션 바 */}
      <nav className={styles.navbar}>
        <button className={styles.mobileMenuToggle} onClick={toggleSidebar}>
          {isSidebarOpen ? <X className={styles.menuToggleIcon} /> : <Menu className={styles.menuToggleIcon} />}
        </button>
        <div className={styles.logoContainer}>
          <h1 className={styles.logo}>Shaun</h1>
        </div>

        <div className={styles.navRight}>
          {!isLoggedIn ? (
            <>
              <button 
                className={styles.authButton}
                onClick={() => router.push('/register')}
              >
                회원가입
              </button>
              <button 
                className={styles.loginButton}
                onClick={() => router.push('/login')}
              >
                로그인
              </button>
            </>
          ) : (
            <button 
              className={styles.logoutButton}
              onClick={handleLogout}
            >
              로그아웃
            </button>
          )}
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
          <li className={pathname === '/ai-chat' ? styles.menuActive : ''}>
            <Link href="/ai-chat" className={styles.menuLink}>
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
    </>
  );
} 