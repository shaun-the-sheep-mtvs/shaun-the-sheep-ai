'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiConfig } from '@/config/api';
import styles from './page.module.css';
import { Home, User, Mail, AlertCircle, ChevronRight, Loader } from 'lucide-react';
import Navbar from '@/components/Navbar';
import axios from 'axios';
import step2Styles from '../step2/page.module.css';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface RoutinesDto {
  id: number;
  name: string;
  type?: 'MORNING' | 'NIGHT';
  time?: 'MORNING' | 'NIGHT';
  kind: string;
  method: string;
  orders: number;
}

interface ResponseProfileDTO {
  email: string;
  username: string;
  troubles: string[];
  routines: RoutinesDto[];
  createdAt: string;
}

interface ExistingRoutine {
  id: number;
  name: string;
  type: 'MORNING' | 'NIGHT';
  kind: string;
  method: string;
  orders: number;
  routineGroupId: number;
}

interface RecommendedRoutine {
  id: number;
  name: string;
  type: 'MORNING' | 'NIGHT';
  kind: string;
  method: string;
  orders: number;
  routineGroupId: number;
  reason: string;
}

interface Routine {
  id: number;
  name: string;
  kind: string;
  method: string;
  orders: number;
  time: 'MORNING' | 'NIGHT';
  routineGroupId: number;
}

const ProfilePage = () => {
  const router = useRouter();
  const [profile, setProfile] = useState<ResponseProfileDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [showPwEdit, setShowPwEdit] = useState(false);
  const [pwForm, setPwForm] = useState({ current: '', new: '', confirm: '' });
  const [tab, setTab] = useState<'info' | 'history'>('info');
  const [history, setHistory] = useState<any[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedHistory, setSelectedHistory] = useState<any | null>(null);
  const [routineTimeTab, setRoutineTimeTab] = useState<'MORNING' | 'NIGHT'>('MORNING');
  const [historicalData, setHistoricalData] = useState<any[]>([]);
  const [modalLoading, setModalLoading] = useState(false);
  const [modalRecommendData, setModalRecommendData] = useState<any | null>(null);
  const [routineChanges, setRoutineChanges] = useState<any[]>([]);
  const [currentGroupId, setCurrentGroupId] = useState<number | null>(null);
  const [existingRoutines, setExistingRoutines] = useState<ExistingRoutine[]>([]);
  const [recommendedRoutines, setRecommendedRoutines] = useState<RecommendedRoutine[]>([]);
  const [routines, setRoutines] = useState<Routine[]>([]);
  const allRecommendEndpoint = apiConfig.endpoints.deep.analysisHistory;

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

    // 루틴 데이터 가져오기
    fetch(`${apiConfig.baseURL}/api/routine/all-existing`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch routines');
        return res.json();
      })
      .then(data => {
        setRoutines(data);
      })
      .catch(error => {
        console.error('Failed to fetch routines:', error);
      });
  }, []);

  useEffect(() => {
    if (tab === 'history') {
      setLoadingHistory(true);
      const token = localStorage.getItem('accessToken');
      axios.get(apiConfig.endpoints.deep.analysisHistory, {
        headers: { Authorization: `Bearer ${token}` },
        withCredentials: true,
      })
        .then(res => setHistory(res.data))
        .catch(() => setHistory([]))
        .finally(() => setLoadingHistory(false));
    }
  }, [tab]);

  useEffect(() => {
    if (tab === 'history') {
      const token = localStorage.getItem('accessToken');
      if (!token) return;
      fetch(apiConfig.endpoints.checklist.base, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
        .then(res => res.ok ? res.json() : [])
        .then(data => setHistoricalData(data || []));
    }
  }, [tab]);

  if (error) {
    return (
      <div className={styles.errorContainer}>
        <p className={styles.errorMessage}>{error}</p>
      </div>
    );
  }

  // 루틴 구분
  const morningRoutines = profile?.routines?.filter(r => (r.type || r.time) === 'MORNING') || [];
  const nightRoutines = profile?.routines?.filter(r => (r.type || r.time) === 'NIGHT') || [];

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

  // 비밀번호 항목 클릭 핸들러
  const handlePwClick = () => setShowPwEdit(v => !v);

  // 탭 변경 시 분석기록으로 가면 비밀번호 변경 패널 닫기
  const handleTabChange = (tabName: 'info' | 'history') => {
    setTab(tabName);
    if (tabName === 'history') setShowPwEdit(false);
  };

  // 모달 오픈 시 분석 결과 fetch
  const handleOpenModal = async (item: any) => {
    setSelectedHistory(item);
    setModalOpen(true);
    setModalLoading(true);
    setModalRecommendData(null);
    setRoutineChanges([]);
    const token = localStorage.getItem('accessToken');
    try {
      // 기존 루틴 데이터 가져오기
      const existingRes = await fetch(`${apiConfig.baseURL}/api/routine/all-existing`, {
        headers: { Authorization: `Bearer ${token}` },
        credentials: 'include'
      });
      if (existingRes.ok) {
        const existingData = await existingRes.json();
        setExistingRoutines(existingData);
      }

      // 분석 결과 데이터 가져오기
      const res = await axios.get(`${allRecommendEndpoint}?id=${item.id}`, {
        headers: { Authorization: `Bearer ${token}` },
        withCredentials: true,
      });
      setModalRecommendData(res.data);
      
      // 루틴 변경 데이터 가져오기
      const routineChangeRes = await fetch(`${apiConfig.baseURL}/api/deep/all-routine-change?id=${item.id}`, {
        headers: { Authorization: `Bearer ${token}` },
        credentials: 'include'
      });
      if (routineChangeRes.ok) {
        const routineChangeData = await routineChangeRes.json();
        setRoutineChanges(routineChangeData);
      }
    } catch (error) {
      console.error('Failed to fetch data:', error);
      setModalRecommendData(null);
      setRoutineChanges([]);
    } finally {
      setModalLoading(false);
    }
  };

  const handleGroupClick = (groupId: number) => {
    setCurrentGroupId(groupId);
  };

  // routineGroupId별로 그룹핑
  const groupedChanges = selectedHistory?.productChanges
    ? selectedHistory.productChanges.reduce((acc: any, item: any) => {
        acc[item.routineGroupId] = acc[item.routineGroupId] || [];
        acc[item.routineGroupId].push(item);
        return acc;
      }, {})
    : {};

  return (
    <>
      <Navbar user={profile && {
        username: profile.username,
        id: 1,
        routines: profile.routines.map(r => ({
          id: r.id,
          name: r.name,
          time: (r.type || r.time || 'MORNING') as string
        }))
      }} />
      <div className={showPwEdit ? `${styles.profilePage} ${styles.pwEditOpen}` : styles.profilePage}>
        <div className={styles.profileContainer}>
          <div className={styles.profileHeader}>
          </div>

          {/* 탭 버튼 추가 */}
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', justifyContent: 'center' }}>
            <button
              onClick={() => handleTabChange('info')}
              style={{
                padding: '0.5rem 2rem',
                fontWeight: 600,
                fontSize: '1.1rem',
                borderRadius: '6px',
                border: tab === 'info' ? '2px solid #388e3c' : '1px solid #ccc',
                background: tab === 'info' ? '#e8f5e9' : '#fff',
                color: tab === 'info' ? '#222' : '#888',
                cursor: 'pointer',
                transition: 'all 0.2s'
              }}
            >내정보</button>
            <button
              onClick={() => handleTabChange('history')}
              style={{
                padding: '0.5rem 2rem',
                fontWeight: 600,
                fontSize: '1.1rem',
                borderRadius: '6px',
                border: tab === 'history' ? '2px solid #388e3c' : '1px solid #ccc',
                background: tab === 'history' ? '#e8f5e9' : '#fff',
                color: tab === 'history' ? '#222' : '#888',
                cursor: 'pointer',
                transition: 'all 0.2s'
              }}
            >분석 기록</button>
          </div>

          {/* 탭별 내용 */}
          {tab === 'info' ? (
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
                    <h5>피부 고민 수정은 체크리스트 다시 검사하기 이용하세요 </h5>
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
                  <div className={styles.profileInfo1}>
                    <h3> 최근 등록된 루틴</h3>
                    <div className={styles.routineSplit1}>
                      <div>
                        <b>아침</b>
                        {morningRoutines.length > 0 ? (
                          <ul className={styles.routineList}>
                            {morningRoutines.map((routine) => (
                              <li key={routine.id}>
                                <div><b>{routine.orders}. {routine.name}</b></div>
                                <div style={{ fontSize: '0.9rem', color: '#666', marginTop: '0.3rem' }}>
                                  {routine.kind} ({routine.method})
                                </div>
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
                                <div style={{ fontSize: '0.9rem', color: '#666', marginTop: '0.3rem' }}>
                                  {routine.kind} ({routine.method})
                                </div>
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

                <div className={styles.profileItem} style={{ cursor: 'pointer' }} onClick={handlePwClick}>
                  <div className={styles.profileIcon}>
                    <User size={24} />
                  </div>
                  <div className={styles.profileInfo} style={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                    <h3 style={{ marginRight: '1.5rem' }}>비밀번호</h3>
                    <span style={{ flex: 1, color: '#888', fontSize: '1.1rem', letterSpacing: '0.2em' }}>*********</span>
                    <ChevronRight size={24} color="#aaa" />
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div style={{ padding: '2rem 0', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              {/* 피부 상태 변화 추이 그래프 */}
              <div className={styles.graphContainer}>
                <h2 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: 16 }}>피부 상태 변화 추이</h2>
                <ResponsiveContainer width="100%" height={320}>
                  <LineChart
                    data={historicalData.map(data => ({
                      date: new Date(data.createdAt).toLocaleDateString(),
                      moisture: data.moisture,
                      oil: data.oil,
                      sensitivity: data.sensitivity,
                      tension: data.tension
                    })).reverse()}
                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
                    <XAxis dataKey="date" tick={{ fill: '#666' }} tickLine={{ stroke: '#666' }} />
                    <YAxis tick={{ fill: '#666' }} tickLine={{ stroke: '#666' }} domain={[0, 100]} />
                    <Tooltip contentStyle={{ backgroundColor: 'white', border: 'none', borderRadius: '8px', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)' }} />
                    <Line type="monotone" dataKey="moisture" name="수분" stroke="#4FC3F7" strokeWidth={2} dot={{ fill: '#4FC3F7', r: 4 }} activeDot={{ r: 6 }} />
                    <Line type="monotone" dataKey="oil" name="유분" stroke="#FFB74D" strokeWidth={2} dot={{ fill: '#FFB74D', r: 4 }} activeDot={{ r: 6 }} />
                    <Line type="monotone" dataKey="sensitivity" name="민감도" stroke="#F06292" strokeWidth={2} dot={{ fill: '#F06292', r: 4 }} activeDot={{ r: 6 }} />
                    <Line type="monotone" dataKey="tension" name="탄력" stroke="#81C784" strokeWidth={2} dot={{ fill: '#81C784', r: 4 }} activeDot={{ r: 6 }} />
                  </LineChart>
                </ResponsiveContainer>
                <div className={styles.graphLegend}>
                  <div className={styles.legendItem}><span className={styles.legendColor} style={{ backgroundColor: '#4FC3F7' }}></span><span>수분</span></div>
                  <div className={styles.legendItem}><span className={styles.legendColor} style={{ backgroundColor: '#FFB74D' }}></span><span>유분</span></div>
                  <div className={styles.legendItem}><span className={styles.legendColor} style={{ backgroundColor: '#F06292' }}></span><span>민감도</span></div>
                  <div className={styles.legendItem}><span className={styles.legendColor} style={{ backgroundColor: '#81C784' }}></span><span>탄력</span></div>
                </div>
              </div>

              {loadingHistory ? (
                <div className={styles.emptyState}>
                  <Loader size={48} className={styles.loadingSpinner} />
                  <p>분석 기록을 불러오는 중...</p>
                </div>
              ) : history.length === 0 ? (
                profile ? (
                  <div className={styles.historyCard}>
                    <div className={styles.analysisCardContent}>
                      <div style={{ marginBottom: 8 }}><b>피부 고민:</b> {profile.troubles ? profile.troubles.join(', ') : '-'}</div>
                      <div style={{ display: 'flex', gap: 32 }}>
                        <div>
                          <div style={{ fontWeight: 600, marginBottom: 4 }}>아침 루틴</div>
                          <ul style={{ margin: 0, paddingLeft: 18 }}>
                            {profile.routines?.filter((r: any) => (r.type || r.time) === 'MORNING').map((r: any) => (
                              <li key={r.id}>{r.orders ? `${r.orders}. ` : ''}{r.name}</li>
                            ))}
                          </ul>
                        </div>
                        <div>
                          <div style={{ fontWeight: 600, marginBottom: 4 }}>저녁 루틴</div>
                          <ul style={{ margin: 0, paddingLeft: 18 }}>
                            {profile.routines?.filter((r: any) => (r.type || r.time) === 'NIGHT').map((r: any) => (
                              <li key={r.id}>{r.orders ? `${r.orders}. ` : ''}{r.name}</li>
                            ))}
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className={styles.emptyState}>
                    <div className={styles.emptyStateIcon}>📊</div>
                    <p>분석 기록이 없습니다.</p>
                  </div>
                )
              ) : (
                history.map((item, idx) => {
                  const morningRoutines = routines
                    .filter(r => r.time === 'MORNING' && r.routineGroupId === item.routineGroupId)
                    .sort((a, b) => a.orders - b.orders);
                  
                  const nightRoutines = routines
                    .filter(r => r.time === 'NIGHT' && r.routineGroupId === item.routineGroupId)
                    .sort((a, b) => a.orders - b.orders);

                  return (
                    <div
                      key={item.id || idx}
                      className={styles.historyCard}
                      onClick={() => handleOpenModal(item)}
                    >
                      <div className={styles.analysisCardContent}>
                        <div style={{ marginBottom: 16 }}>
                          <b>피부 고민:</b> {item.troubles ? Object.values(item.troubles).join(', ') : '-'}
                        </div>
                        <div style={{ display: 'flex', gap: 32 }}>
                          <div style={{ flex: 1 }}>
                            <div style={{ 
                              fontWeight: 600, 
                              marginBottom: 8,
                              color: '#388e3c',
                              fontSize: '1.1rem'
                            }}>
                              아침 루틴
                            </div>
                            {morningRoutines.length > 0 ? (
                              <ul className={styles.routineList}>
                                {morningRoutines.map((routine) => (
                                  <li key={routine.id}>
                                    <div className={styles.routineItem}>
                                      <div className={styles.routineOrder}>{routine.orders}</div>
                                      <div className={styles.routineInfo}>
                                        <div className={styles.routineName}>{routine.name}</div>
                                        <div className={styles.routineDetail}>
                                          {routine.kind} ({routine.method})
                                        </div>
                                      </div>
                                    </div>
                                  </li>
                                ))}
                              </ul>
                            ) : (
                              <p className={styles.routineEmpty}>등록된 아침 루틴이 없습니다.</p>
                            )}
                          </div>
                          <div style={{ flex: 1 }}>
                            <div style={{ 
                              fontWeight: 600, 
                              marginBottom: 8,
                              color: '#388e3c',
                              fontSize: '1.1rem'
                            }}>
                              저녁 루틴
                            </div>
                            {nightRoutines.length > 0 ? (
                              <ul className={styles.routineList}>
                                {nightRoutines.map((routine) => (
                                  <li key={routine.id}>
                                    <div className={styles.routineItem}>
                                      <div className={styles.routineOrder}>{routine.orders}</div>
                                      <div className={styles.routineInfo}>
                                        <div className={styles.routineName}>{routine.name}</div>
                                        <div className={styles.routineDetail}>
                                          {routine.kind} ({routine.method})
                                        </div>
                                      </div>
                                    </div>
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
                  );
                })
              )}

              {/* 모달 */}
              {modalOpen && selectedHistory && (
                <div style={{
                  position: 'fixed',
                  top: 0, left: 0, right: 0, bottom: 0,
                  background: 'rgba(0,0,0,0.25)',
                  zIndex: 3000,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'auto',
                  padding: '2rem'
                }}
                  onClick={() => setModalOpen(false)}
                >
                  <div
                    className={styles.modalContent}
                    onClick={e => e.stopPropagation()}
                  >
                    <div className={styles.modalHeader}>
                      <h2 className={styles.modalTitle}>분석 상세 결과</h2>
                      <button className={styles.modalClose} onClick={() => setModalOpen(false)}>✕</button>
                    </div>

                    {modalLoading ? (
                      <div className={styles.emptyState}>
                        <Loader size={48} className={styles.loadingSpinner} />
                        <p>분석 결과를 불러오는 중...</p>
                      </div>
                    ) : modalRecommendData ? (
                      <div className={styles.analysisCard}>
                        <div className={styles.productsSection}>
                          <div className={styles.sectionHeader}>
                            <div className={styles.sectionMainTitle}>제품 변경 및 추가 추천</div>
                          </div>

                          {/* 루틴 탭 */}
                          <div className={styles.routineTabs}>
                            <button 
                              className={`${styles.routineTab} ${routineTimeTab === 'MORNING' ? styles.active : ''}`}
                              onClick={() => setRoutineTimeTab('MORNING')}
                            >
                              아침 루틴
                            </button>
                            <button 
                              className={`${styles.routineTab} ${routineTimeTab === 'NIGHT' ? styles.active : ''}`}
                              onClick={() => setRoutineTimeTab('NIGHT')}
                            >
                              저녁 루틴
                            </button>
                          </div>

                          <div className={styles.changeBox}>
                            {(() => {
                              const recommendations = modalRecommendData.filter((item: any) => item.routineGroupId === selectedHistory.routineGroupId);
                              const groupRoutines = routines.filter(r => r.routineGroupId === selectedHistory.routineGroupId);
                              const currentRoutines = groupRoutines.filter(r => r.time === routineTimeTab).sort((a, b) => a.orders - b.orders);
                              const filteredRoutineChanges = routineChanges.filter((r: any) => r.routineTime === routineTimeTab).sort((a: any, b: any) => a.routineOrders - b.routineOrders);

                              return (
                                <div className={styles.routineGroupSection}>
                                  {/* 현재 루틴 */}
                                  <div className={styles.currentRoutines}>
                                    <div className={styles.routineTimeSection}>
                                      <h4>현재 {routineTimeTab === 'MORNING' ? '아침' : '저녁'} 루틴</h4>
                                      <ul className={styles.routineList}>
                                        {currentRoutines.map((routine) => (
                                          <li key={routine.id} className={styles.routineItem}>
                                            <div className={styles.routineOrder}>{routine.orders}</div>
                                            <div className={styles.routineInfo}>
                                              <div className={styles.routineName}>{routine.name}</div>
                                              <div className={styles.routineDetail}>
                                                {routine.kind} ({routine.method})
                                              </div>
                                            </div>
                                          </li>
                                        ))}
                                      </ul>
                                    </div>
                                  </div>

                                  {/* 루틴 변경 추천 */}
                                  <div className={styles.recommendationsSection}>
                                    <h4>{routineTimeTab === 'MORNING' ? '아침' : '저녁'} 루틴 변경 추천</h4>
                                    <div className={styles.routineTimeSection}>
                                      <ul className={styles.routineList}>
                                        {filteredRoutineChanges.map((routine: any) => (
                                          <li key={routine.routineChangeId} className={styles.routineItem}>
                                            <div className={styles.routineOrder}>{routine.routineOrders}</div>
                                            <div className={styles.routineInfo}>
                                              <div className={styles.routineName}>{routine.routineName}</div>
                                              <div className={styles.routineDetail}>
                                                {routine.routineKind}
                                              </div>
                                              <div className={styles.changeMethod}>
                                                {routine.changeMethod}
                                              </div>
                                            </div>
                                          </li>
                                        ))}
                                      </ul>
                                    </div>
                                  </div>

                                  {/* 추천 제품 */}
                                  <div className={styles.recommendationsSection}>
                                    <h4>추천 제품</h4>
                                    {recommendations.map((item: any, idx: number) => (
                                      <div className={styles.changeRecommendCard} key={`change-${idx}-${item.suggestProduct}`}>
                                        <div className={styles.changeProductRow}>
                                          <div className={styles.changeProductCol}>
                                            <div className={styles.changeProductRow}>
                                              <div className={styles.changeProductIcon}>
                                                <svg width="24" height="24" fill="none" stroke="#bdbdbd" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                                              </div>
                                              <div>
                                                <div className={styles.changeProductLabel}>현재 사용 중</div>
                                                <div className={styles.changeProductTitle}>
                                                  {item.name}
                                                </div>
                                                <div style={{ color: '#888', fontSize: 14 }}>
                                                  {item.kind}
                                                </div>
                                              </div>
                                            </div>
                                            <div className={styles.changeArrow}>
                                              <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19" /><polyline points="19 12 12 19 5 12" /></svg>
                                            </div>
                                            <div className={styles.changeProductRow}>
                                              <div className={`${styles.changeProductIcon} ${styles.green}`}>
                                                <svg width="24" height="24" fill="none" stroke="#4CD3A5" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /></svg>
                                              </div>
                                              <div>
                                                <div className={`${styles.changeProductLabel} ${styles.green}`}>추천 제품</div>
                                                <div className={styles.changeProductTitle}>{item.suggestProduct}</div>
                                              </div>
                                            </div>
                                          </div>
                                          <div>
                                            <button onClick={() => window.open(`https://www.coupang.com/np/search?component=&q=${item.suggestProduct}`, '_blank')} className={styles.buyBtn}>구매하기</button>
                                          </div>
                                        </div>
                                        <div className={styles.changeReasonBox}>
                                          <p className={styles.changeReasonText}>{item.reason}</p>
                                        </div>
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              );
                            })()}
                          </div>
                        </div>
                      </div>
                    ) : null}
                  </div>
                </div>
              )}
            </div>
          )}
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
    </>
  );
}

export default ProfilePage;