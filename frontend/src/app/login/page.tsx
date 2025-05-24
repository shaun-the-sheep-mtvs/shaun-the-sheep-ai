'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
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
              const res = await fetch(apiConfig.endpoints.auth.login, {
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

              // JSON 응답에서 토큰 추출
              const authResponse = await res.json();
              console.log('authResponse:', authResponse);
              
              if (authResponse.accessToken) {
                  // localStorage에 토큰 저장
                  localStorage.setItem('accessToken', authResponse.accessToken);
                  localStorage.setItem('refreshToken', authResponse.refreshToken);
                  console.log('token:', authResponse.accessToken);
              } else {
                  console.log('token: null');
                  setError('토큰을 받지 못했습니다.');
                  return;
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
          <h1>Shaun</h1>
          <form className={styles.loginForm} onSubmit={handleSubmit}>
            <div className={styles.formGroup}>
              <label htmlFor="email">이메일</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="이메일을 입력해주세요"
                value={email}
                onChange={e => setEmail(e.target.value)}
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

