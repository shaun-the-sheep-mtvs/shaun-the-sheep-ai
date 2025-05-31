'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import styles from './page.module.css';
import { Home, User, Mail, AlertCircle } from 'lucide-react';

interface RoutinesDto {
  id: number;
  name: string;
  kind: string;
  method: string;
  orders: number;
  time: 'MORNING' | 'NIGHT';
  routineGroupId: number;
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
  const [showPwEdit, setShowPwEdit] = useState(false);
  const [pwForm, setPwForm] = useState({ current: '', new: '', confirm: '' });

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

  // 루틴 구분
  const morningRoutines = profile?.routines?.filter(r => r.time === 'MORNING') || [];
  const nightRoutines = profile?.routines?.filter(r => r.time === 'NIGHT') || [];

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    const token = localStorage.getItem('accessToken');
    if (!token) return;
    try {
      const res = await fetch(`${apiConfig.baseURL}/api/profile/password`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          currentPassword: pwForm.current,
          newPassword: pwForm.new,
          confirmPassword: pwForm.confirm
        })
      });
      if (!res.ok) throw new Error('비밀번호 변경 실패');
      // 성공 시 처리 (예: 알림, 폼 닫기 등)
      alert('비밀번호가 성공적으로 변경되었습니다.');
      setShowPwEdit(false);
      setPwForm({ current: '', new: '', confirm: '' });
    } catch (err) {
      alert('비밀번호 변경에 실패했습니다.');
    }
  };

  return (
    <div className={showPwEdit ? `${styles.profilePage} ${styles.pwEditOpen}` : styles.profilePage}>
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

            {/* 루틴 아침/저녁 구분 */}
            <div className={styles.profileItem}>
              <div className={styles.profileIcon}>
                <User size={24} />
              </div>
              <div className={styles.profileInfo}>
                <h3>루틴</h3>
                <div className={styles.routineSplit}>
                  <div>
                    <b>아침</b>
                    {morningRoutines.length > 0 ? (
                      <ul className={styles.routineList}>
                        {morningRoutines.map((routine) => (
                          <li key={routine.id}>
                            <div><b>{routine.orders}. {routine.name}</b></div>
                         
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className={styles.routineEmpty}>등록된 아침 루틴이 없습니다.</p>
                    )}
                  </div>
                  <div>
                    <b>저녁</b>
                    {nightRoutines.length > 0 ? (
                      <ul className={styles.routineList}>
                        {nightRoutines.map((routine) => (
                          <li key={routine.id}>
                            <div><b>{routine.orders}. {routine.name}</b></div>
                         
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className={styles.routineEmpty}>등록된 저녁 루틴이 없습니다.</p>
                    )}
                  </div>
                </div>
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

            {/* 비밀번호 수정 버튼 */}
            <div className={styles.profileItem}>
              <button
                className={styles.pwEditBtn}
                onClick={() => setShowPwEdit((v) => !v)}
              >
                비밀번호 수정
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* 비밀번호 변경 폼 (오른쪽에 띄움) */}
      {showPwEdit && (
        <div className={styles.pwEditPanel}>
          <h3>비밀번호 변경</h3>
          <form className={styles.pwEditForm} onSubmit={handlePasswordChange}>
            <label>
              기존 비밀번호
              <input
                type="password"
                value={pwForm.current}
                onChange={e => setPwForm(f => ({ ...f, current: e.target.value }))}
              />
            </label>
            <label>
              신규 비밀번호
              <input
                type="password"
                value={pwForm.new}
                onChange={e => setPwForm(f => ({ ...f, new: e.target.value }))}
              />
            </label>
            <label>
              비밀번호 재확인
              <input
                type="password"
                value={pwForm.confirm}
                onChange={e => setPwForm(f => ({ ...f, confirm: e.target.value }))}
              />
            </label>
            <button type="submit" className={styles.pwEditSubmitBtn}>
              변경
            </button>
          </form>
        </div>
      )}
    </div>
  );
} 