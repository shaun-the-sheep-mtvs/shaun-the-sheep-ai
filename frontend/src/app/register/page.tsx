'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import { useAuth } from '@/contexts/AuthContext';
import styles from './page.module.css';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email,    setEmail]    = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState<string | null>(null);
  const router = useRouter();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      
      // 1) 회원가입
      const signupRes = await fetch(apiConfig.endpoints.auth.signup, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          username, 
          email, 
          password
        }),
        credentials: 'include',
      });

      if (!signupRes.ok) {
        const text = await signupRes.text();
        setError(text || '회원가입에 실패했습니다.');
        return;
      }


      // 2) 바로 로그인 using AuthContext
      await login({ username, password });

      // 3) 체크리스트 페이지로 이동
      router.push('/checklist');

    } catch (err) {
      console.error('Register/Login error:', err);
      setError('네트워크 오류가 발생했습니다.');
    }
  };

  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <div className={styles.registerContainer}>
          <h1>Shaun</h1>
          {error && <p className={styles.error}>{error}</p>}

          <form onSubmit={handleSubmit} className={styles.registerForm}>
            <div className={styles.formGroup}>
              <label htmlFor="username">사용자 이름</label>
              <input
                id="username"
                type="text"
                placeholder="사용자를 입력해주세요"
                value={username}
                onChange={e => setUsername(e.target.value)}
                required
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="email">이메일</label>
              <input
                id="email"
                type="email"
                placeholder="이메일을 입력해주세요"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="password">패스워드</label>
              <input
                id="password"
                type="password"
                placeholder="패스워드를 입력해주세요"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className={styles.registerButton}>
              가입하기
            </button>
            <div
              className={styles.loginLink}
              onClick={() => router.push('/login')}
            >
              로그인
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}


