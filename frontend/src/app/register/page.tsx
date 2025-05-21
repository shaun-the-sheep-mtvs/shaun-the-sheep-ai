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
            const res = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password }),
            });

            console.log('status:', res.status);
           const data = await res.json();
           console.log('response data:', data);

            if (!res.ok) {
               // 백엔드에서 { error: "메시지" } 형태로 내려온다고 가정
              setError(data.error || data.message || '회원가입에 실패했습니다.');
              return;
           }

            // 가입 성공 후 체크리스트트 페이지로 이동
            router.push('/checklist');
        } catch {
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
