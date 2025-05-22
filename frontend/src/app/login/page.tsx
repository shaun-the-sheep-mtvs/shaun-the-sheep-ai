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
      console.log('로그인 시도:', { email, password });
      
      const res = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({email, password})
      });

      console.log('body:', res.body);
      console.log('응답 상태:', res.status);
      console.log('응답 헤더:', res);
      const data = await res.json();

      console.log('응답 데이터:', data);

      if (!res.ok) {
        setError(data.error || data.message || '로그인에 실패했습니다.');
        return;
      }

      // 토큰 저장 전에 값 확인
      console.log('저장할 토큰:', data.accessToken, data.refreshToken);
      
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);

      router.push('/');
    } catch (err) {
      console.error('로그인 에러:', err);
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

