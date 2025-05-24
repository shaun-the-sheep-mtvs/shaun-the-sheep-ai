'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from "./page.module.css";

export default function Home() {
      const [email,    setEmail]    = useState('');
      const [password, setPassword] = useState('');
      const [error,    setError]    = useState<string | null>(null);
      const router = useRouter();

      const handleSubmit = async (e: React.FormEvent) => {
          e.preventDefault();
          setError(null);

          try {
              const res = await fetch('http://localhost:8080/api/auth/login', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ email, password }),
              });

             console.log('status:', res.status);

              if (!res.ok) {
                  const errorText = await res.text();
                  setError(errorText || '로그인에 실패했습니다.');
                  return;
              }

              // JWT 토큰은 Authorization 헤더에 있음
              const authHeader = res.headers.get('Authorization');
              if (authHeader && authHeader.startsWith('Bearer ')) {
                  const token = authHeader.substring(7);
                  // 쿠키에 토큰 저장 (Path=/ 반드시 포함)
                  document.cookie = `token=${token}; Path=/; SameSite=Lax; max-age=3600`;
                  console.log('쿠키에 저장된 값:', document.cookie);
              }

              // 로그인 성공 후 홈페이지로 이동
              router.push('/');
          } catch (error) {
              console.error('Login error:', error);
              setError('네트워크 오류가 발생했습니다.');
          }
      };
  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <div className={styles.loginContainer}>
          <h1>Login</h1>
          <form className={styles.loginForm} onSubmit={handleSubmit}>
            <div className={styles.formGroup}>
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
              />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>
            {error && <p className={styles.error}>{error}</p>}
            <button type="submit" className={styles.loginButton}>
              Sign In
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}
