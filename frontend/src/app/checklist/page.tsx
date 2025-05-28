// src/app/page.checklist.tsx
'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { QUESTIONS, Question, Category } from '@/data/questions';
import { CONCERNS } from '@/data/concerns';
import styles from './page.checklist.module.css';
import apiConfig from '@/config/api';

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
  const [answers, setAnswers] = useState<{ cat: Category; score: number }[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [selectedConcerns, setSelectedConcerns] = useState<string[]>([]);
  const [progress, setProgress] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const router = useRouter();

  // 문제 로딩 및 섞기
  useEffect(() => {
    const byCat: Record<Category, Question[]> = {
      moisture: [],
      oil: [],
      sensitivity: [],
      tension: [],
    };
    for (const q of QUESTIONS) {
      byCat[q.category].push(q);
    }
    const picked = (Object.keys(byCat) as Category[])
      .flatMap(cat => shuffle(byCat[cat]).slice(0, 3));
    setQs(shuffle(picked));
  }, []);

  // 진행도 계산
  useEffect(() => {
    setProgress(qs.length ? Math.round((idx / qs.length) * 100) : 0);
  }, [idx, qs.length]);

  // 카테고리별 총점
  const sums = answers.reduce<Record<Category, number>>((acc, { cat, score }) => {
    acc[cat] = (acc[cat] || 0) + score;
    return acc;
  }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 });

  // 백분율 계산
  const percent = (cat: Category) => {
    const maxForCat = qs.filter(q => q.category === cat).length * 2;
    return maxForCat
      ? Math.min(100, Math.round((sums[cat] / maxForCat) * 100))
      : 0;
  };

  // ▶ 1단계: 퀴즈
  if (stage === 'quiz') {
    if (!qs.length) return <div className={styles.page}>로딩 중…</div>;

    const q = qs[idx];
    const onSelect = (score: number, optIdx: number) => {
      setAnswers(a => [...a, { cat: q.category, score }]);
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
          <h1 className={styles.title}>
            질문 {idx + 1} / {qs.length}: {q.text}
          </h1>
          <div
            className={styles.resultProgress}
            style={{
              '--bar-color': '#ff8fab',
              '--range-percentage': `${progress}%`,
              backgroundSize: `${progress}% 100%`,
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

  // ▶ 2단계: 고민 선택 & 제출
  const submitAll = (concernIds: string[]) => {
    setSubmitting(true);

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
    fetch(apiConfig.endpoints.checklist.base, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(body),
    })
      .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
      })
      .then(() => router.push('/'))
      .catch(() => alert('제출 실패, 다시 시도해주세요.'))
      .finally(() => setSubmitting(false));
  };

  const fetchNaverData = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      console.log('Current token:', token); // 토큰 확인용 로그

      if (!token) {
        console.log('No token found');
        return;
      }

      const response = await fetch(`http://localhost:8080/api/naver`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log('Response status:', response.status); // 응답 상태 확인용 로그

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

  return (
    <div className={styles.page}>
      {/* 제출 중 오버레이 */}
      {submitting && (
        <div className={styles.loadingOverlay}>
          <div className={styles.spinner}>제출 중…</div>
        </div>
      )}

      <div className={styles.container}>
        <h1 className={styles.title}>피부 고민을 선택해주세요</h1>
        <div className={styles.concernsGrid}>
          {CONCERNS.map(c => (
            <button
              key={c.id}
              type="button"
              className={`${styles.concernBtn} ${
                selectedConcerns.includes(c.id) ? styles.active : ''
              }`}
              onClick={() =>
                setSelectedConcerns(prev =>
                  prev.includes(c.id)
                    ? prev.filter(x => x !== c.id)
                    : [...prev, c.id]
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
           disabled={!selectedConcerns.length}
           onClick={async () => {
             await submitAll(selectedConcerns);
             await fetchNaverData();
           }}
         >
           제출
         </button>
       </div>
      </div>
    </div>
  );
}

















