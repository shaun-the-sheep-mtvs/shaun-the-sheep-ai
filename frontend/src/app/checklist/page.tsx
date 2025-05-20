'use client';

import { useState } from 'react';
import styles from './page.checklist.module.css';  // CSS 모듈 임포트

export default function ChecklistPage() {
  const labels = [
    '매우 건조함',
    '건조함',
    '보통',
    '촉촉함',
    '매우 촉촉함'
  ];

  const [selected, setSelected] = useState<number | null>(null);
  const [progress, setProgress] = useState(0);

  const onSelect = (idx: number) => {
    setSelected(idx);
    setProgress(((idx + 1) / labels.length) * 100);
  };

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <h1 className={styles.title}>피부유형 검사지</h1>

        {/* 진행도 슬라이더 (disabled) */}
        <div className={styles.progress}>
          <input
            type="range"
            min="0"
            max="100"
            value={progress}
            disabled
          />
        </div>

        {/* 5 버튼 옵션 */}
        <div className={styles.options}>
          {labels.map((text, i) => (
            <div key={i} className={styles.optionWrapper}>
              <button
                type="button"
                className={`${styles.option} ${
                  selected === i ? styles.selected : ''
                }`}
                onClick={() => onSelect(i)}
              />
              <span className={styles.optionLabel}>{text}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
