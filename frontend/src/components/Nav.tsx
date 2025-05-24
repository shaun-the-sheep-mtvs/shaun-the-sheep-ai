// src/components/Nav.tsx
'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter, usePathname } from 'next/navigation';

export default function Nav() {
  const [loggedIn, setLoggedIn] = useState(false);
  const router   = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    let token = localStorage.getItem('accessToken');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp < Date.now() / 1000) {
          localStorage.removeItem('accessToken');
          token = null;
        }
      } catch {
        localStorage.removeItem('accessToken');
        token = null;
      }
    }
    setLoggedIn(!!token);
  }, [pathname]);  // ← pathname이 바뀔 때마다 로그인 상태 재검사

  const handleLogout = async () => {
    await fetch('/auth/logout', { method: 'POST', credentials: 'include' });
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.replace('/login');
  };

  return (
    <nav style={{ padding: '1rem', textAlign: 'right' }}>
      {loggedIn ? (
        <button
          onClick={handleLogout}
          style={{ all: 'unset', cursor: 'pointer', color: 'blue' }}
        >
          Logout
        </button>
      ) : (
        <>
          <Link href="/login">Login</Link> | <Link href="/register">Sign Up</Link>
        </>
      )}
    </nav>
  );
}


