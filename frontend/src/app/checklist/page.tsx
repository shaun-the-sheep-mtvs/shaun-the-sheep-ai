// src/app/page.checklist.tsx
'use client';

import React, { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import styles from './page.checklist.module.css';
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
  const [qs, setQs]                   = useState<Question[]>([]);
  const [idx, setIdx]                 = useState(0);
  const [answers, setAnswers]         = useState<{ cat: Category; score: number }[]>([]);
  const [done, setDone]               = useState(false);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const router = useRouter();

  // 페이지 로드 시: 카테고리별로 5문항 중 랜덤 3개씩 골라 총 12문제 세팅
  useEffect(() => {
    const byCat: Record<Category, Question[]> = {
      moisture:    [],
      oil:         [],
      sensitivity: [],
      tension:     [],
    };
    for (const q of QUESTIONS) {
      byCat[q.category].push(q);
    }
    const picked: Question[] = (Object.keys(byCat) as Category[])
      .flatMap(cat => shuffle(byCat[cat]).slice(0, 3));
    setQs(shuffle(picked));
  }, []);

  // 1) 카테고리별 점수 합계
  const sums = answers.reduce<Record<Category, number>>((acc, { cat, score }) => {
    acc[cat] = (acc[cat] || 0) + score;
    return acc;
  }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 });

  // 2) 백분율 계산
  const percent = (cat: Category) => {
  // ① 해당 카테고리에서 실제 뽑힌 문항 수 × 2(최대 점수)
  const maxForCat = qs.filter(q => q.category === cat).length * 2;
  if (maxForCat === 0) return 0;
  // ② 백분율 계산 후, 100 이하로 클램프
  const raw = Math.round((sums[cat] / maxForCat) * 100);
  return Math.min(raw, 100);
};

  // 로딩 상태
  if (!qs.length) {
    return <div className={styles.page}>로딩 중…</div>;
  }

  // 완료 화면
  if (done) {
    const BAR_COLOR: Record<Category, string> = {
      moisture:    '#4caf50',
      oil:         '#2196f3',
      sensitivity: '#f44336',
      tension:     '#ff9800',
    };
    const getLabel = (p: number) =>
      p >= 80 ? '좋음' :
      p >= 60 ? '보통' :
      p >= 40 ? '주의' : '개선 필요';

    return (
      <div className={styles.page}>
        <div className={styles.container}>
          <h1 className={styles.title}>진단 결과</h1>
          {(['moisture','oil','sensitivity','tension'] as Category[]).map(cat => {
            const p     = percent(cat);
            const color = BAR_COLOR[cat];
            return (
              <div key={cat} className={styles.row}>
                <span className={styles.label}>
                  {{
                    moisture: '수분',
                    oil: '유분',
                    sensitivity: '민감도',
                    tension: '탄력',
                  }[cat]} {p}% — {getLabel(p)}
                </span>
                <div
                  className={styles.resultProgress}
                  style={{
                    background: `linear-gradient(to right, ${color} ${p}% , #eee ${p}%)`
                  }}
                />
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  // 질문 화면
  const q = qs[idx];
  const progress = Math.round((idx / qs.length) * 100);

  const onSelect = (score: number, optionIdx: number) => {
    // 답안 추가
    setAnswers(a => [...a, { cat: q.category, score }]);
    // 선택 표시
    setSelectedOption(optionIdx);
    // 다음 문제로
    setTimeout(() => {
      setSelectedOption(null);
      if (idx + 1 >= qs.length) {
        // 마지막 문제인 경우 서버에 저장
        const token = localStorage.getItem('accessToken');
        if (!token) {
          console.error('No access token');
          return;
        }

        fetch('http://localhost:8080/api/checklist', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          body: JSON.stringify({
            moisture:    percent('moisture'),
            oil:         percent('oil'),
            sensitivity: percent('sensitivity'),
            tension:     percent('tension'),
          }),
        })
        .then(res => {
          if (!res.ok) throw new Error(`status ${res.status}`);
          return res.json();
        })
        .then(() => {
          window.location.href = '/';  // 저장 성공 시 바로 홈으로 리다이렉트
        })
        .catch(err => {
          console.error(err);
          setDone(true);  // 에러 발생 시에만 done 상태 변경
        });
      } else {
        setIdx(i => i + 1);
      }
    }, 150);
  };

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <h1 className={styles.title}>
          질문. {idx + 1} / {qs.length} {q.text}
        </h1>

        <div className={styles.progress}>
          <input
            type="range"
            min={0}
            max={100}
            value={progress}
            disabled
            style={{
              background: `linear-gradient(to right, #ff8fab ${progress}%, #eee ${progress}%)`
            }}
          />
        </div>

        <div className={styles.options}>
          {q.options.map((opt, i) => (
            <div key={i} className={styles.optionWrapper}>
              <button
                type="button"
                className={`
                  ${styles.option}
                  ${selectedOption === i ? styles.selected : ''}
                `}
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

















