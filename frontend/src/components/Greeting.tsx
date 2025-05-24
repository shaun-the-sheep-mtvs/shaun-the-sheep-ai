// src/components/Greeting.tsx
'use client';

import React, { useEffect, useState } from 'react';
import pageStyles from '@/app/page.module.css';
import { parseJwt } from '@/data/jwt';

export default function Greeting() {
  const [name, setName] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    const payload = parseJwt<{ username?: string; sub?: string }>(token);
    if (payload) {
      // username 클레임 또는 sub 클레임
      setName(payload.username ?? payload.sub ?? null);
    }
  }, []);

  if (!name) return null;
  return (
    <div className={pageStyles.greetingRight}>
      {name}님 안녕하세요
    </div>
  );
}

