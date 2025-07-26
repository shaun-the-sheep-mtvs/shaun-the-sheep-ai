'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { apiConfig } from '@/config/api';
import { useAuth } from '@/contexts/AuthContext';
import styles from "./page.module.css";

export default function Home() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState<string | null>(null);
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login, isLoggedIn, loading } = useAuth();

  // Redirect to main page if already logged in
  useEffect(() => {
    if (!loading && isLoggedIn) {
      router.replace('/');
    }
  }, [loading, isLoggedIn, router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      // Use AuthContext login method with correct interface
      await login({ username, password });
      
      // Check if user came from landing page
      const fromLanding = searchParams.get('from') === 'landing';
      
      if (fromLanding) {
        // New user from landing page should go to checklist
        router.push('/checklist');
      } else {
        // Existing user should go to main page
        router.push('/');
      }
    } catch (error) {
      console.error('Login error:', error);
      setError(error instanceof Error ? error.message : '로그인에 실패했습니다.');
    }
  };
  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <div className={styles.loginContainer}>
          <h1>Shaun</h1>
          <form className={styles.loginForm} onSubmit={handleSubmit}>
            <div className={styles.formGroup}>
              <label htmlFor="username">사용자명</label>
              <input
                type="text"
                id="username"
                name="username"
                placeholder="사용자명을 입력해주세요"
                value={username}
                onChange={e => setUsername(e.target.value)}
                required
              />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="password">패스워드</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="패스워드를 입력해주세요"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>
            {error && <p className={styles.error}>{error}</p>}
            <button type="submit" className={styles.loginButton}>
              로그인
            </button>
            <div className={styles.registerLink} onClick={() => router.push('/register')}>
              회원가입
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}

