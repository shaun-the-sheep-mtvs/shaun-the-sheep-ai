// components/AuthGuard.tsx
'use client';

import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';

const PUBLIC_PATHS = ['/login','/register'];

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router   = useRouter();
  const pathname = usePathname();
  const [ready, setReady] = useState(false);

  useEffect(() => {
    console.log('AuthGuard 실행:', { pathname });

    async function checkAuth() {
      let token = localStorage.getItem('accessToken');
      console.log('기존 토큰:', token);

      // 만료 검사 등 로직…

      const isPublic = PUBLIC_PATHS.includes(pathname);
      if (!token && !isPublic) {
        console.log('→ 토큰 없음, 로그인 페이지로');
        return router.replace('/login');
      }
      if (token && isPublic) {
        console.log('→ 토큰 있음, 홈으로');
        return router.replace('/');
      }
      setReady(true);
    }
    checkAuth();
  }, [pathname, router]);

  if (!ready) return null;
  return <>{children}</>;
}


