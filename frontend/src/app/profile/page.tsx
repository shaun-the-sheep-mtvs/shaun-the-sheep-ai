'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import styles from './page.module.css';
import { Home, User, Mail, AlertCircle } from 'lucide-react';

interface RoutinesDto {
  id: number;
  name: string;
}

interface ResponseProfileDTO {
  email: string;
  username: string;
  troubles: string[];
  routines: RoutinesDto[];
  createdAt: string;
}

export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<ResponseProfileDTO | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
      return;
    }

    // 사용자 정보 가져오기
    fetch(`${apiConfig.baseURL}/api/profile`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch user data');
        return res.json();
      })
      .then(data => {
        setProfile(data);
      })
      .catch(error => {
        console.error('Failed to fetch profile:', error);
        setError('프로필 정보를 불러오는데 실패했습니다.');
      });
  }, []);

  if (error) {
    return (
      <div className={styles.errorContainer}>
        <p className={styles.errorMessage}>{error}</p>
      </div>
    );
  }

  return (
    <div className={styles.profilePage}>
      <div className={styles.profileContainer}>
        <div className={styles.profileHeader}>
          <button 
            className={styles.homeButton}
            onClick={() => router.push('/')}
          >
            <Home size={20} />
            이전
          </button>
          <h1>회원 정보</h1>
        </div>

        <div className={styles.profileContent}>
          <div className={styles.profileSection}>
            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <User size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>이름</h3>
                <p>{profile?.username || '로딩 중...'}</p>
              </div>
            </div>

            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <Mail size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>이메일</h3>
                <p>{profile?.email || '로딩 중...'}</p>
              </div>
            </div>

            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <AlertCircle size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>피부 고민</h3>
                {profile?.troubles && profile.troubles.length > 0 ? (
                  <ul className={styles.concernsList}>
                    {profile.troubles.map((trouble, index) => (
                      <li key={index}>{trouble}</li>
                    ))}
                  </ul>
                ) : (
                  <p>등록된 피부 고민이 없습니다.</p>
                )}
              </div>
            </div>

            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <User size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>루틴</h3>
                {profile?.routines && profile.routines.length > 0 ? (
                  <ul className={styles.routineList}>
                    {profile.routines.map((routine) => (
                      <li key={routine.id}>{routine.name}</li>
                    ))}
                  </ul>
                ) : (
                  <p>등록된 루틴이 없습니다.</p>
                )}
              </div>
            </div>

            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <User size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>가입일</h3>
                <p>{profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString() : '로딩 중...'}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 