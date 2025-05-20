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
              const res = await fetch('http://localhost:8080/auth/login', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ email, password }),
              });

             console.log('status:', res.status);
             const data = await res.json();
             console.log('response data:', data);

              if (!res.ok) {
              setError(data.error || data.message || '로그인에 실패했습니다.');
              return;
              }

              if (data.token) {
                localStorage.setItem('token', data.token);
              }

              // 가입 성공 후 로그인 페이지로 이동
              router.push('/register');
          } catch (err) {
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
