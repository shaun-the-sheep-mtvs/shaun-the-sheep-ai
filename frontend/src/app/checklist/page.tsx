'use client';

import { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import styles from './page.checklist.module.css';

const MAX_SCORE_PER_CATEGORY = 2 * 3; // 문항당 최대 2점 × 3문제

export default function ChecklistPage() {
  const [qs, setQs]       = useState<Question[]>([]);
  const [idx, setIdx]     = useState(0);
  const [answers, setAns] = useState<{ cat: Category; score: number }[]>([]);
  const [done, setDone]   = useState(false);

  useEffect(() => {
    setQs(QUESTIONS);
  }, []);

  if (qs.length === 0) {
    return <div className={styles.page}>로딩 중…</div>;
  }

  const onSelect = (score: number) => {
    setAns([...answers, { cat: qs[idx].category, score }]);
    if (idx + 1 >= qs.length) {
      setDone(true);
    } else {
      setIdx(idx + 1);
    }
  };

  // 완료 시 결과 UI
  if (done) {
    // 카테고리별 합산
    const sum = answers.reduce<Record<Category, number>>((acc, { cat, score }) => {
      acc[cat] = (acc[cat] || 0) + score;
      return acc;
    }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 });
    
    // % 환산
    const percent = (cat: Category) =>
      Math.round((sum[cat] / MAX_SCORE_PER_CATEGORY) * 100);

    const q = qs[idx];

    return (
      <div className={styles.result}>
        <h1>체크리스트 결과</h1>
        {(['moisture','oil','sensitivity','tension'] as Category[]).map(cat => (
          <div key={cat} className={styles.row}>
            <span className={styles.label}>
              {{
                moisture: '수분',
                oil: '유분',
                sensitivity: '민감도',
                tension: '탄력',
              }[cat]} {percent(cat)}%
            </span>
            <div className={styles.barBackground}>
              <div
                className={styles.barFill}
                style={{ width: `${percent(cat)}%` }}
              />
            </div>
          </div>
        ))}
      </div>
    );
  }

  // 질문 UI
  const q = qs[idx];
  return (
    <div className={styles.page}>
      <h2>문제 {idx + 1} / {qs.length}</h2>
      <p className={styles.text}>{q.text}</p>
      <div className={styles.options}>
        {q.options.map((opt, i) => (
          <button
            key={i}
            onClick={() => onSelect(opt.score)}
            className={styles.option}
          >
            {opt.label}
          </button>
        ))}
      </div>
    </div>
  );
}



