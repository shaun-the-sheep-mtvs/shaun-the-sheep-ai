// src/components/ChecklistResult.tsx
'use client';

import React from 'react';
import styles from './ChecklistResult.module.css';

export interface CheckListResponse {
  moisture: number;
  oil: number;
  sensitivity: number;
  tension: number;
}

interface Props {
  checklist: CheckListResponse;
}

export default function ChecklistResult({ checklist }: Props) {
  return (
    <div className={styles.checklistBox}>
      <h3>체크리스트 결과</h3>
      <div className={styles.barWrap}>
        <div>수분 <span>{checklist.moisture}%</span></div>
        <div className={styles.bar}>
          <div
            className={styles.barGold}
            style={{ width: `${checklist.moisture}%` }}
          />
        </div>

        <div>유분 <span>{checklist.oil}%</span></div>
        <div className={styles.bar}>
          <div
            className={styles.barGoldLight}
            style={{ width: `${checklist.oil}%` }}
          />
        </div>

        <div>민감도 <span>{checklist.sensitivity}%</span></div>
        <div className={styles.bar}>
          <div
            className={styles.barRed}
            style={{ width: `${checklist.sensitivity}%` }}
          />
        </div>

        <div>탄력 <span>{checklist.tension}%</span></div>
        <div className={styles.bar}>
          <div
            className={styles.barGray}
            style={{ width: `${checklist.tension}%` }}
          />
        </div>
      </div>
    </div>
  );
}
