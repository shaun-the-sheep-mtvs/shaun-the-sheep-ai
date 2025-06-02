'use client';

import React, { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import { CONCERNS } from '@/data/concerns';
import styles from './page.checklist.module.css';
import apiConfig from '@/config/api';
import { ArrowLeft, RotateCcw } from 'lucide-react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/Navbar';

// Fisher–Yates 셔플 함수
function shuffle<T>(arr: T[]): T[] {
  const a = arr.slice();
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function getAccessToken() {
  return localStorage.getItem('accessToken');
}

function buildAuthHeaders() {
  const token = getAccessToken();
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  };
}

function getChecklistEndpoint() {
  const token = getAccessToken();
  return token
    ? apiConfig.endpoints.checklist.base
    : apiConfig.endpoints.checklist.guest;
}

async function apiPost<T>(url: string, data: any): Promise<T> {
  const res = await fetch(url, {
    method: 'POST',
    headers: buildAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    throw new Error(await res.text());
  }
  return res.json();
}

// Types for results
type GuestChecklistResult = { mbti: string; skinType: string };
type UserChecklistResult = { mbti: string; skinType: string };

type ChecklistInitResponse = { troubles?: string[] };

export default function ChecklistPage() {
  const [stage, setStage] = useState<'quiz' | 'concerns'>('quiz');
  const [qs, setQs] = useState<Question[]>([]);
  const [idx, setIdx] = useState(0);
  const [answers, setAnswers] = useState<{ cat: Category; score: number; weight: number }[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [selectedConcerns, setSelectedConcerns] = useState<string[]>([]);
  const [guestResult, setGuestResult] = useState<GuestChecklistResult | null>(null);
  const [progress, setProgress] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const router = useRouter();

  // Fetch latest checklist for user or guest (if available)
  useEffect(() => {
    const endpoint = getChecklistEndpoint();
    apiPost<ChecklistInitResponse>(endpoint, {})
      .then((data) => {
        if (Array.isArray(data.troubles) && data.troubles.length > 0) {
          const ids = data.troubles
            .map(label => CONCERNS.find(c => c.label === label)?.id)
            .filter((id): id is string => !!id);
          setSelectedConcerns(ids);
        }
      })
      .catch(err => {
        console.warn('최신 체크리스트 조회 실패, quiz부터 시작', err);
      });
  }, []);

  // 로그인 상태 초기화
  useEffect(() => {
    const token = getAccessToken();
    setIsLoggedIn(!!token);
  }, []);

  // 진행도 계산
  useEffect(() => {
    setProgress(qs.length ? Math.round((idx / qs.length) * 100) : 0);
  }, [idx, qs.length]);

  // 퀴즈 셋업: 카테고리별 3문항씩 랜덤 선택
  useEffect(() => {
    const byCat: Record<Category, Question[]> = {
      moisture: [], oil: [], sensitivity: [], tension: [],
    };
    for (const q of QUESTIONS) {
      byCat[q.category].push(q);
    }
    const picked: Question[] = (Object.keys(byCat) as Category[])
      .flatMap(cat => shuffle(byCat[cat]).slice(0, 3));
    setQs(shuffle(picked));
  }, []);

  // 카테고리별 총점 & 백분율 계산
  const weightedSums = answers.reduce<Record<Category, number>>((acc, { cat, score, weight }) => {
    acc[cat] = (acc[cat] || 0) + score * weight;
    return acc;
  }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 });

  const percent = (cat: Category) => {
    const qsOfCat = qs.filter(q => q.category === cat);
    const maxWeighted = qsOfCat.reduce((sum, q) => {
      const maxScore = Math.max(...q.options.map(o => o.score));
      return sum + q.weight * maxScore;
    }, 0);
    if (maxWeighted === 0) return 0;
    const raw = Math.round((weightedSums[cat] / maxWeighted) * 100);
    return Math.min(raw, 100);
  };

  // Checklist submission for both guest and user
  const submitAll = async (concernIds: string[]): Promise<GuestChecklistResult | UserChecklistResult> => {
    const labels = concernIds
      .map(id => CONCERNS.find(c => c.id === id)?.label)
      .filter((l): l is string => !!l);

    const body = {
      moisture: percent('moisture'),
      oil: percent('oil'),
      sensitivity: percent('sensitivity'),
      tension: percent('tension'),
      troubles: labels,
    };

    const endpoint = getChecklistEndpoint();
    return apiPost(endpoint, body);
  };

  // 네이버 연동 
  const fetchNaverData = async () => {
    try {
      const endpoint = getChecklistEndpoint();
      const response = await fetch(endpoint, {
        method: 'GET',
        headers: buildAuthHeaders()
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Naver API response:', data);
      return data;
    } catch (error) {
      console.error('Error fetching Naver data:', error);
    }
  };

  // 제출 핸들러 
  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      const result = await submitAll(selectedConcerns);
      if (result && typeof result === 'object' && 'mbti' in result && 'skinType' in result) {
        setGuestResult(result as GuestChecklistResult);
      } else {
        await fetchNaverData();
        router.push('/');
      }
    } catch (error) {
      alert('제출에 실패했습니다. 다시 시도해주세요.');
      console.error(error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.push('/login');
  };

  const restartTest = () => {
    setStage('quiz');
    setIdx(0);
    setAnswers([]);
    setSelectedOption(null);
    setSelectedConcerns([]);
    setProgress(0);
    setSubmitting(false);
    // 퀴즈 재설정
    const byCat: Record<Category, Question[]> = {
      moisture: [], oil: [], sensitivity: [], tension: [],
    };
    for (const q of QUESTIONS) {
      byCat[q.category].push(q);
    }
    const picked: Question[] = (Object.keys(byCat) as Category[])
      .flatMap(cat => shuffle(byCat[cat]).slice(0, 3));
    setQs(shuffle(picked));
  };

  // 1단계: 퀴즈
  if (stage === 'quiz') {
    if (!qs.length) return (
      <div className={styles.wrapper}>
        <Navbar
          isLoggedIn={isLoggedIn}
          onLogout={handleLogout}
        />
        <div className={styles.page}>로딩 중…</div>
      </div>
    );
    const q = qs[idx];

    const handleBack = () => {
      if (idx === 0) return;
      setIdx(i => i - 1);
      setAnswers(a => a.slice(0, -1));
      setSelectedOption(null);
    };

    const onSelect = (opt: { label: string; score: number }, optIdx: number) => {
      // "모름"/"해당 없음"인 경우: 문항 교체만, idx 증가 X → progress 유지
      if (opt.label === '모름' || opt.label === '해당 없음') {
        setSelectedOption(optIdx);
        const pool = QUESTIONS
          .filter(x => x.category === q.category)
          // 해당 옵션이 없는 문항만
          .filter(x => !x.options.some(o => o.label === '모름' || o.label === '해당 없음'))
          // 아직 qs에 없는 문항만
          .filter(x => !qs.some(existing => existing.id === x.id));
        if (pool.length) {
          setQs(prev => {
            const next = [...prev];
            next[idx] = pool[0];
            return next;
          });
        }
        // 잠깐 선택 표시 후 해제
        setTimeout(() => setSelectedOption(null), 150);
        return;
      }

      // 일반 답변: 기록 & 다음으로
      setAnswers(a => [
        ...a,
        { cat: q.category, score: opt.score, weight: q.weight }
      ]);
      setSelectedOption(optIdx);
      setTimeout(() => {
        setSelectedOption(null);
        if (idx + 1 >= qs.length) {
          setStage('concerns');
        } else {
          setIdx(i => i + 1);
        }
      }, 150);
    };

    return (
      <div className={styles.wrapper}>
        <Navbar
          isLoggedIn={isLoggedIn}
          onLogout={handleLogout}
        />
        <div className={styles.page}>
          <div className={styles.container}>
            <div className={styles.restartSection}>
              <button
                onClick={restartTest}
                className={styles.restartButton}
                title="다시 검사하기"
              >
                <RotateCcw className={styles.restartIcon} />
                다시 검사하기
              </button>
            </div>
            <div className={styles.navRow}>
              <button
                onClick={handleBack}
                disabled={idx === 0}
                className={styles.backButton}
              >
                <ArrowLeft className={styles.backIcon} />
              </button>
              <h1 className={styles.title}>
                {q.text}
              </h1>
              <div className={styles.placeholder}></div>
            </div>
            <div
              className={styles.resultProgress}
              style={{
                '--bar-color': '#40DDBA',
                '--range-percentage': `${progress}%`,
                backgroundSize: `${progress}% 100%`
              } as React.CSSProperties}
            >
              <input type="range" min={0} max={100} value={progress} readOnly />
            </div>
            <div className={styles.options}>
              {q.options.map((opt, i) => (
                <div key={i} className={styles.optionCard}>
                  <button
                    className={`${styles.optionButton} ${selectedOption === i ? styles.selected : ''}`}
                    onClick={() => onSelect(opt, i)}
                  >
                    <div className={styles.optionContent}>
                      <div className={styles.optionNumber}>{i + 1}</div>
                      <div className={styles.optionText}>{opt.label}</div>
                    </div>
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 2단계: 고민 선택 & 제출
  return (
    <div className={styles.wrapper}>
      <Navbar
        isLoggedIn={isLoggedIn}
        onLogout={handleLogout}
      />
      {submitting && (
        <div className={styles.loadingOverlay}>
          <div className={styles.spinner}>체크리스트 분석 중…</div>
        </div>
      )}
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.restartSection}>
            <button
              onClick={restartTest}
              className={styles.restartButton}
              title="다시 검사하기"
            >
              <RotateCcw className={styles.restartIcon} />
              다시 검사하기
            </button>
          </div>
          <h1 className={styles.title}>
            피부 고민을 선택해주세요
          </h1>
          <p className={styles.subtitle}>
            현재 가장 신경 쓰이는 피부 고민들을 3가지만 선택해주세요
          </p>
          <div className={styles.concernsGrid}>
            {CONCERNS.map(c => {
              // 각 고민별 아이콘 매핑
              const getIcon = (id: string) => {
                const iconMap: { [key: string]: string } = {
                  'dryness': '💧',
                  'oiliness': '✨', 
                  'sensitivity': '🌸',
                  'elasticity': '💪',
                  'redness': '🔴',
                  'unevenTone': '🎨',
                  'hyperpigment': '☀️',
                  'fineLines': '📏',
                  'pores': '🔍',
                  'breakouts': '🚫',
                  'dullness': '🌙',
                  'darkCircles': '👁️',
                  'roughTexture': '🪨'
                };
                return iconMap[id] || '🤔';
              };
              
              const isSelected = selectedConcerns.includes(c.id);
              const isDisabled = !isSelected && selectedConcerns.length >= 3;
              
              return (
                <div key={c.id} className={styles.concernCard}>
                  <button
                    type="button"
                    className={`${styles.concernBtn} ${isSelected ? styles.active : ''} ${isDisabled ? styles.disabled : ''}`}
                    disabled={isDisabled}
                    onClick={() => {
                      if (isSelected) {
                        // 선택 해제
                        setSelectedConcerns(prev => prev.filter(x => x !== c.id));
                      } else if (selectedConcerns.length < 3) {
                        // 선택 추가 (3개 미만일 때만)
                        setSelectedConcerns(prev => [...prev, c.id]);
                      }
                    }}
                  >
                    <div className={styles.concernIcon}>
                      {getIcon(c.id)}
                    </div>
                    <div className={styles.concernText}>
                      {c.label}
                    </div>
                    {isSelected && (
                      <div className={styles.selectedIndicator}>✓</div>
                    )}
                  </button>
                </div>
              );
            })}
          </div>
          {selectedConcerns.length > 0 && (
            <div className={styles.selectionInfo}>
              <span className={styles.selectionCount}>
                {selectedConcerns.length}/3개 선택됨
              </span>
              {selectedConcerns.length < 3 && (
                <span className={styles.selectionHint}>
                  최대 3개까지 선택할 수 있습니다
                </span>
              )}
            </div>
          )}
          <div className={styles.submitWrapper}>
            <button
              className={styles.submitBtn}
              disabled={selectedConcerns.length === 0 || submitting}
              onClick={handleSubmit}
            >
              {submitting ? (
                <div className={styles.submitLoading}>
                  <div className={styles.loadingSpinner}></div>
                  분석 중...
                </div>
              ) : (
                `${selectedConcerns.length > 0 ? `${selectedConcerns.length}개 고민` : '고민을'} 선택하고 제출`
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
