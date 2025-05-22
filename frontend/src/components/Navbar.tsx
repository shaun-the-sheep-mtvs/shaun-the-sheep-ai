// src/components/Navbar.tsx
'use client';

import React, { useState, useEffect } from 'react';
import { Menu, X } from "lucide-react";
import styles from "@/app/page.module.css";
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useCurrentUser } from '@/data/useCurrentUser';

export default function Navbar() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const router = useRouter();
    const { user, loading } = useCurrentUser();

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };

    const handleLogout = async () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        router.push('/login');
        router.refresh();
    };

    return (
        <nav className={styles.navbar}>
            <button className={styles.mobileMenuToggle} onClick={toggleSidebar}>
                {isSidebarOpen ? <X className={styles.menuToggleIcon} /> : <Menu className={styles.menuToggleIcon} />}
            </button>
            <div className={styles.logoContainer}>
                <Link href="/" className={styles.logo}>Shaun</Link>
            </div>

            <div className={styles.navRight}>
                {loading ? (
                    // Loading state
                    <div>Loading...</div>
                ) : user ? (
                    // Logged in state
                    <>
                        <span className={styles.userName}>{user.username}</span>
                        <button onClick={handleLogout} className={styles.loginButton}>로그아웃</button>
                    </>
                ) : (
                    // Logged out state
                    <>
                        <Link href="/register" className={styles.authButton}>회원가입</Link>
                        <Link href="/login" className={styles.loginButton}>로그인</Link>
                    </>
                )}
            </div>
        </nav>
    );
}