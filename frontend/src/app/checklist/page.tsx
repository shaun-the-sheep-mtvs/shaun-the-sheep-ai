'use client';

import { useEffect, useState } from 'react'
import { QUESTIONS, Question, Category } from '@/data/questions'
import styles from './page.checklist.module.css'

const MAX_SCORE_PER_CATEGORY = 2 * 3

export default function ChecklistPage() {
  const [qs, setQs]                   = useState<Question[]>([])
  const [idx, setIdx]                 = useState(0)
  const [answers, setAnswers]         = useState<{ cat: Category; score: number }[]>([])
  const [done, setDone]               = useState(false)
  const [selectedOption, setSelectedOption] = useState<number | null>(null)

  useEffect(() => {
    setQs(QUESTIONS)
  }, [])

  // 1) 문제별 합계
  const sums = answers.reduce<Record<Category, number>>((acc, { cat, score }) => {
    acc[cat] = (acc[cat] || 0) + score
    return acc
  }, { moisture: 0, oil: 0, sensitivity: 0, tension: 0 })

  // 2) % 계산
  const percent = (cat: Category) =>
      Math.round((sums[cat] / MAX_SCORE_PER_CATEGORY) * 100)

  // 3) 완료 시 서버에 저장 (한 번만)
  // ChecklistPage.tsx (useEffect 안)
useEffect(() => {
  if (!done) return;

  const token = localStorage.getItem('accessToken');
  if (!token) {
    console.error('No access token');
    return;
  }

  fetch('http://localhost:8080/api/checklist', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`   // ← 여기!!
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
  .then(data => console.log('saved', data))
  .catch(err => console.error(err));
}, [done]);


  if (!qs.length) {
    return <div className={styles.page}>로딩 중…</div>
  }

  // ===========================
  // 완료 화면
  // ===========================
  if (done) {
    const BAR_COLOR: Record<Category, string> = {
      moisture:    '#4caf50',
      oil:         '#2196f3',
      sensitivity: '#f44336',
      tension:     '#ff9800',
    }
    const getLabel = (p: number) =>
        p >= 80 ? '좋음' : p >= 60 ? '보통' : p >= 40 ? '주의' : '개선 필요'

    return (
        <div className={styles.page}>
          <div className={styles.container}>
            <h1 className={styles.title}>진단 결과</h1>
            {(['moisture','oil','sensitivity','tension'] as Category[]).map(cat => {
              const p     = percent(cat)
              const color = BAR_COLOR[cat]
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
              )
            })}
          </div>
        </div>
    )
  }

  // ===========================
  // 질문 화면
  // ===========================
  const q = qs[idx]
  const progress = Math.round((idx / qs.length) * 100)

  const onSelect = (score: number, optionIdx: number) => {
    // 1) 답안에 추가
    setAnswers(a => [...a, { cat: q.category, score }])
    // 2) 선택 표시
    setSelectedOption(optionIdx)
    // 3) 잠깐 보여주고 다음 문제로
    setTimeout(() => {
      setSelectedOption(null)
      if (idx + 1 >= qs.length) {
        setDone(true)
      } else {
        setIdx(i => i + 1)
      }
    }, 150)
  }

  return (
      <div className={styles.page}>
        <div className={styles.container}>
          <h1 className={styles.title}>
            질문. {idx + 1} / {qs.length}　{q.text}
          </h1>

          <div className={styles.progress}>
            <input
                type="range"
                min={0}
                max={100}
                value={progress}
                disabled
                style={{
                  background: `linear-gradient(
              to right,
            #ff8fab ${progress}%,
              #eee ${progress}%
             )`
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
  )
}
















