'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from './page.module.css';

export default function RegisterPage() {
    const [username, setUsername] = useState('');
    const [email,    setEmail]    = useState('');
    const [password, setPassword] = useState('');
    const [error,    setError]    = useState<string | null>(null);
    const router = useRouter();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        try {
            const res = await fetch('http://localhost:8080/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password }),
            });

            console.log('status:', res.status);

            if (!res.ok) {
                const errorText = await res.text();
                setError(errorText || '회원가입에 실패했습니다.');
                return;
            }

            // 가입 성공 후 로그인 페이지로 이동
            router.push('/login');
        } catch (error) {
            console.error('Register error:', error);
            setError('네트워크 오류가 발생했습니다.');
        }
    };

    return (
  <div className={styles.page}>
    <main className={styles.main}>
      {/* 프레임 */}
      <div className={styles.registerContainer}>
        <h1>Sign Up</h1>
        {error && <p className={styles.error}>{error}</p>}

        {/* 폼 전체에 registerForm 클래스 */}
        <form onSubmit={handleSubmit} className={styles.registerForm}>
          <div className={styles.formGroup}>
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className={styles.registerButton}>
            Create Account
          </button>
        </form>
      </div>
    </main>
  </div>
);
}
