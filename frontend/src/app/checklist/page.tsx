'use client';

import { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import styles from './page.checklist.module.css';

const MAX_SCORE_PER_CATEGORY = 2 * 3; // 문항당 최대 2점 × 3문제

export default function ChecklistPage() {
  const TOTAL = QUESTIONS.length;    // 12문제
  const [qs, setQs]       = useState<Question[]>([]);
  const [idx, setIdx]     = useState(0);
  const [answers, setAns] = useState<{ cat: Category; score: number }[]>([]);
  const [done, setDone]   = useState(false);

  useEffect(() => {
    // 순서 고정: QUESTIONS 전체 (필요하면 shuffle)
    setQs(QUESTIONS);
  }, []);

  // 로딩 상태
  if (qs.length === 0) {
    return <div className={styles.page}>로딩 중…</div>;
  }

  // 답변 핸들러
  const onSelect = (score: number) => {
    setAns([...answers, { cat: qs[idx].category, score }]);
    if (idx + 1 >= qs.length) {
      setDone(true);
    } else {
      setIdx(idx + 1);
    }
  };

  // ===========================
  // 완료 시: 결과 화면
  // ===========================
  if (done) {
    // 카테고리별 합산
    const sum = answers.reduce<Record<Category, number>>((acc, { cat, score }) => {
      acc[cat] = (acc[cat] || 0) + score;
      return acc;
    }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 });

    // % 환산
    const percent = (cat: Category) => Math.round((sum[cat] / MAX_SCORE_PER_CATEGORY) * 100);

    return (
      <div className={styles.page}>
        <div className={styles.container}>
          <h1 className={styles.title}>체크리스트 결과</h1>
          {(['moisture','oil','sensitivity','tension'] as Category[]).map(cat => (
            <div key={cat} className={styles.optionWrapper /* 재활용: 한 줄 묶음 */}>
              <span className={styles.optionLabel /* 재활용: 레이블 스타일 */}>
                {{
                  moisture: '수분',
                  oil: '유분',
                  sensitivity: '민감도',
                  tension: '탄력'
                }[cat]} {percent(cat)}%
              </span>
              <div className={styles.progress /* 재활용: 슬라이더 백그라운드 */}>
                {/* 여기선 range 대신 바 형태로 */}
                <div
                  className={styles.selected /* 재활용: 채워진 부분 스타일 */} 
                  style={{ width: `${percent(cat)}%`, height: '12px', borderRadius: '6px', background: '#ff8fab' }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // ===========================
  // 질문 UI
  // ===========================
  const q = qs[idx];
  const progress = Math.round((idx / TOTAL) * 100);

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        {/* 타이틀: 문제 번호 + 질문 텍스트 */}
        <h1 className={styles.title}>
          문제 {idx + 1} / {TOTAL}　{q.text}
        </h1>

        {/* 진행도 슬라이더 */}
        <div className={styles.progress}>
          <input type="range" min={0} max={100} value={progress} disabled />
        </div>

        {/* 옵션 버튼 그룹 */}
        <div className={styles.options}>
          {q.options.map((opt, i) => {
            // 현재 문제에서 내가 선택한 값(없으면 undefined)
            const isSelected = answers[idx]?.score === opt.score;
            return (
              <div key={i} className={styles.optionWrapper}>
                <button
                  type="button"
                  className={`${styles.option} ${isSelected ? styles.selected : ''}`}
                  onClick={() => onSelect(opt.score)}
                />
                <span className={styles.optionLabel}>{opt.label}</span>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}




