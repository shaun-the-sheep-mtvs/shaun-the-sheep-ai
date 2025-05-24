'use client';

import { useEffect, useState } from 'react';

export interface CurrentUser {
  id: number;
  username: string;
  email: string;
}

export function useCurrentUser() {
  const [user, setUser]     = useState<CurrentUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    console.log('token:', token);
    if (!token) {
      setLoading(false);
      return;
    }

    fetch('http://localhost:8080/auth/me', {
      headers: {
        'Authorization': `Bearer ${token}`,
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('Unauthorized');
        return res.json() as Promise<CurrentUser>;
      })
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false));
  }, []);

  return { user, loading };
}