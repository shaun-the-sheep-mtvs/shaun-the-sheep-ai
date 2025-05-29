'use client';

import React, { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import { CONCERNS } from '@/data/concerns';
import styles from './page.checklist.module.css';
import apiConfig from '@/config/api';
import { ArrowLeft } from 'lucide-react';
import { useRouter } from 'next/navigation';

// Fisher–Yates 셔플 함수
function shuffle<T>(arr: T[]): T[] {
  const a = arr.slice();
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

export default function ChecklistPage() {
  const [stage, setStage] = useState<'quiz' | 'concerns'>('quiz');
  const [qs, setQs] = useState<Question[]>([]);
  const [idx, setIdx] = useState(0);
  const [answers, setAnswers] = useState<{ cat: Category; score: number; weight: number }[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [selectedConcerns, setSelectedConcerns] = useState<string[]>([]);
  const [progress, setProgress] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (!token) return

     fetch(`${apiConfig.endpoints.checklist.base}/latest`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => res.json())
      .then((data: { troubles?: string[] }) => {
        if (Array.isArray(data.troubles) && data.troubles.length > 0) {
          const ids = data.troubles
          .map(label =>
            CONCERNS.find(c => c.label === label)?.id
          )
          .filter((id): id is string => !!id)
          setSelectedConcerns(ids)
        }
      })
      .catch(err => {
        console.warn('최신 체크리스트 조회 실패, quiz부터 시작', err)
      })
  }, [])

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
  // 카테고리별 최대 가중치 합계 = ∑(q.weight * maxScoreForQ)
  const maxWeighted = qsOfCat.reduce((sum, q) => {
    const maxScore = Math.max(...q.options.map(o => o.score));
    return sum + q.weight * maxScore;
  }, 0);
  if (maxWeighted === 0) return 0;
  const raw = Math.round((weightedSums[cat] / maxWeighted) * 100);
  return Math.min(raw, 100);
};

  // 체크리스트 서버 제출 (setSubmitting 은 handleSubmit에서 관리)
  const submitAll = async (concernIds: string[]): Promise<boolean> => {
    try {
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

      const token = localStorage.getItem('accessToken');
      const res = await fetch(apiConfig.endpoints.checklist.base, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        console.error('체크리스트 제출 실패:', await res.text());
        return false;
      }

      await res.json();
      return true;
    } catch (err) {
      console.error('제출 중 오류:', err);
      return false;
    }
  };

  // 네이버 연동
  const fetchNaverData = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        console.log('No token found');
        return;
      }

      const response = await fetch(`${apiConfig.baseURL}/api/naver`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
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
      const success = await submitAll(selectedConcerns);
      if (!success) {
        alert('제출에 실패했습니다. 다시 시도해주세요.');
        return;
      }

      await fetchNaverData();
      router.push('/');
    } catch (error) {
      console.error('처리 중 오류 발생:', error);
      alert('처리 중 오류가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setSubmitting(false);
    }
  };

  // 1단계: 퀴즈
  if (stage === 'quiz') {
    if (!qs.length) return <div className={styles.page}>로딩 중…</div>;
    const q = qs[idx];

    const handleBack = () => {
      if (idx === 0) return;
      setIdx(i => i - 1);
      setAnswers(a => a.slice(0, -1));
      setSelectedOption(null);
    };

    const onSelect = (score: number, optIdx: number) => {
      setAnswers(a => [
         ...a,
         { cat: q.category, score, weight: q.weight }
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
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.navRow}>
            <button
              onClick={handleBack}
              disabled={idx === 0}
              className={styles.backButton}
            >
              <ArrowLeft className={styles.backIcon} />
            </button>
            <h1 className={styles.title}>
              질문 {idx + 1} / {qs.length}: {q.text}
            </h1>
          </div>
          <div
            className={styles.resultProgress}
            style={{
              '--bar-color': '#ff8fab',
              '--range-percentage': `${progress}%`,
              backgroundSize: `${progress}% 100%`
            } as React.CSSProperties}
          >
            <input type="range" min={0} max={100} value={progress} readOnly />
          </div>
          <div className={styles.options}>
            {q.options.map((opt, i) => (
              <div key={i} className={styles.optionWrapper}>
                <button
                  className={`${styles.option} ${selectedOption === i ? styles.selected : ''}`}
                  onClick={() => onSelect(opt.score, i)}
                />
                <span className={styles.optionLabel}>{opt.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  // 2단계: 고민 선택 & 제출
  return (
    <div className={styles.page}>
      {submitting && (
        <div className={styles.loadingOverlay}>
          <div className={styles.spinner}>체크리스트 분석 중…</div>
        </div>
      )}
      <div className={styles.container}>
        <h1 className={styles.title}>
          피부 고민을 선택해주세요
          </h1>
        <div className={styles.concernsGrid}>
          {CONCERNS.map(c => (
            <button
              key={c.id}
              type="button"
              className={`${styles.concernBtn} ${selectedConcerns.includes(c.id) ? styles.active : ''}`}
              onClick={() =>
                setSelectedConcerns(prev =>
                  prev.includes(c.id) ? prev.filter(x => x !== c.id) : [...prev, c.id]
                )
              }
            >
              {c.label}
            </button>
          ))}
        </div>
        <div className={styles.submitWrapper}>
          <button
            className={styles.submitBtn}
            disabled={!selectedConcerns.length || submitting}
            onClick={handleSubmit}
          >
            제출
          </button>
        </div>
      </div>
    </div>
  );
}
















