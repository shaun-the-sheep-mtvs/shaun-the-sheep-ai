// src/app/page.checklist.tsx
'use client';

import React, { useEffect, useState } from 'react';
import { QUESTIONS, Question, Category } from '@/data/questions';
import { apiConfig } from '@/config/api';
import styles from './page.checklist.module.css';
import { CONCERNS } from '@/data/concerns';
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
  const [stage, setStage] = useState<'quiz'|'concerns'>('quiz');
  const [qs, setQs]                   = useState<Question[]>([]);
  const [idx, setIdx]                 = useState(0);
  const [answers, setAnswers]         = useState<{ cat: Category; score: number }[]>([]);
  const [done, setDone]               = useState(false);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const router = useRouter();
  const [progress, setProgress] = useState(0);
  const [selectedConcerns, setSelectedConcerns] = useState<string[]>([]);
  useEffect(() => {setProgress(Math.round((idx/qs.length) * 100));
  }, [idx]);

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

 if (stage === 'quiz') {
    // 아직 질문 미로딩
    if (!qs.length) return <div className={styles.page}>로딩 중…</div>;

    const q = qs[idx];
    const onSelect = (score:number, optIdx:number) => {
      setAnswers(a=>[...a,{cat:q.category,score}]);
      setSelectedOption(optIdx);
      setTimeout(()=>{
        setSelectedOption(null);
        if (idx+1 >= qs.length) {
          // 마지막 문항: 고민 선택 화면으로
          setStage('concerns');
        } else {
          setIdx(i=>i+1);
        }
      },150);
    };

    return (
      <div className={styles.page}>
        <div className={styles.container}>
          <h1 className={styles.title}>
            질문 {idx+1} / {qs.length}: {q.text}
          </h1>
          {/* 진행도 슬라이더 */}
          <div
            className={styles.resultProgress}
            style={{
              '--bar-color': '#ff8fab',
              '--range-percentage': `${progress}%`,
              backgroundSize: `${progress}% 100%`
            } as React.CSSProperties}
          >
            <input type="range" min={0} max={100} value={progress} readOnly/>
          </div>
          {/* 선택지 */}
          <div className={styles.options}>
            {q.options.map((opt,i)=>(
              <div key={i} className={styles.optionWrapper}>
                <button
                  className={`${styles.option} ${selectedOption===i?styles.selected:''}`}
                  onClick={()=>onSelect(opt.score,i)}
                />
                <span className={styles.optionLabel}>{opt.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  // ▶ 2단계: concerns 화면
  const submitAll = (concernIds: string[]) => {
   // id → label 매핑
   const labels = concernIds
     .map(id => CONCERNS.find(c => c.id === id)?.label)
     .filter((l): l is string => !!l);

   const body = {
     moisture:    percent('moisture'),
     oil:         percent('oil'),
     sensitivity: percent('sensitivity'),
     tension:     percent('tension'),
     troubles:    labels       // 이제 label 배열로 POST
   };
    const token = localStorage.getItem('accessToken');
    fetch(apiConfig.endpoints.checklist.base, {
      method:'POST',
      headers:{
        'Content-Type':'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(body)
    })
    .then(res=>{ if(!res.ok) throw new Error(); return res.json()})
    .then(()=> router.push('/'))
    .catch(()=> alert('제출 실패, 다시 시도해주세요.'));
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
      <div className={styles.container}>
        <h1 className={styles.title}>피부 고민을 선택해주세요</h1>
        <div className={styles.concernsGrid}>
          {CONCERNS.map(c=>(
            <button
              key={c.id}
              type="button"
              className={`${styles.concernBtn} ${selectedConcerns.includes(c.id)?styles.active:''}`}
              onClick={()=>{
                // 토글
                setSelectedConcerns(prev=>{
                  return prev.includes(c.id)
                    ? prev.filter(x=>x!==c.id)
                    : [...prev, c.id];
                });
              }}
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

















