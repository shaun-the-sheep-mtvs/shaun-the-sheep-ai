"use client";

import React, { useState } from "react";
import styles from "../page.module.css";

// 예시 데이터 (실제 서비스에서는 props나 API로 전달)
const skinType = {
  main: "민감성 + 지성",
  tags: ["수부지", "여드름성", "열감성", "민감성"],
};

const routineData = {
  morning: [
    { name: "클렌징", desc: "저자극 약산성 폼 클렌저", buy: true },
    { name: "토너", desc: "수분 진정 토너", buy: true },
    { name: "에센스", desc: "수분공급 에센스", buy: true },
    { name: "크림", desc: "진정 보습 크림", buy: true },
    { name: "선크림", desc: "민감 피부용 선크림 SPF50", buy: true },
  ],
  night: [
    { name: "클렌징", desc: "저자극 약산성 폼 클렌저", buy: true },
    { name: "토너", desc: "수분 진정 토너", buy: true },
    { name: "에센스", desc: "수분공급 에센스", buy: true },
    { name: "크림", desc: "진정 보습 크림", buy: true },
  ],
};

const changeRecommend = [
  {
    before: { name: "일반 폼 클렌저", desc: "현재 사용 중인 클렌저" },
    after: { name: "저자극 폼 클렌저", desc: "피부 장벽을 보호하는 저자극 약산성 폼 클렌저로 교체 추천" },
  },
  {
    before: { name: "알콜함유 토너", desc: "현재 사용 중인 토너" },
    after: { name: "진정 토너", desc: "알콜이 없는 진정 토너로 교체 추천" },
  },
];

const extraRecommend = [
  { name: "히알루론산 에센스", desc: "수분 부족할 때 히알루론산 에센스를 추가로 사용해보세요.", buy: true },
  { name: "시카 세럼", desc: "피부 진정이 필요할 때 시카 성분의 세럼을 함께 사용해보세요.", buy: true },
];

export default function Step2() {
  const [tab, setTab] = useState<'morning' | 'night'>('morning');

  return (
    <div className={styles.wrapper} style={{ background: '#fff' }}>
      {/* 사이드바 */}
      <aside className={styles.sidebar}>
        <h2 className={styles.logo}>스킨케어</h2>
        <nav>
          <ul className={styles.menu}>
            <li>홈화면</li>
            <li className={styles.menuActive}>검사하기</li>
            <li>회원정보</li>
          </ul>
        </nav>
      </aside>
      {/* 메인 */}
      <main className={styles.mainContent} style={{ background: '#fff' }}>
        <div className={styles.greetingBar}>
          <span className={styles.greetingIcon}>👤</span>
          <span className={styles.greetingText}>○○님 안녕하세요</span>
        </div>
        {/* STEP 표시 */}
        <div className={styles.stepHeader}>
          <span className={styles.step}>STEP 1</span>
          <span className={styles.stepActive}>STEP 2</span>
        </div>
        <h2 className={styles.stepTitle}>맞춤 루틴 추천</h2>
        <div className={styles.stepDesc}>AI가 분석한 당신의 피부 타입에 맞는 루틴과 제품을 추천해 드립니다.</div>
        {/* 피부 타입 박스 */}
        <div className={styles.skinTypeBox}>
          <div className={styles.skinTypeMain}>{skinType.main}</div>
          <div className={styles.skinTypeTags}>
            {skinType.tags.map((tag, i) => (
              <span key={i} className={styles.skinTypeTag}>{tag}</span>
            ))}
          </div>
        </div>
        {/* 루틴 탭 */}
        <div className={styles.routineTabs}>
          <button className={tab === 'morning' ? styles.routineTabActive : styles.routineTab} onClick={() => setTab('morning')}>아침 루틴</button>
          <button className={tab === 'night' ? styles.routineTabActive : styles.routineTab} onClick={() => setTab('night')}>저녁 루틴</button>
        </div>
        {/* 루틴 카드 */}
        <div className={styles.routineCard}>
          <div className={styles.routineList}>
            {routineData[tab].map((item, idx) => (
              <div key={idx} className={styles.routineItem}>
                <div className={styles.routineNum}>{idx + 1}</div>
                <div className={styles.routineInfo}>
                  <div className={styles.routineName}>{item.name}</div>
                  <div className={styles.routineDesc}>{item.desc}</div>
                </div>
                {item.buy && <button className={styles.buyBtn}>구매하기</button>}
              </div>
            ))}
          </div>
        </div>
        {/* 제품 변경 및 추가 추천 */}
        <h3 className={styles.changeTitle}>제품 변경 및 추가 추천</h3>
        <div className={styles.changeBox}>
          <div className={styles.changeList}>
            {changeRecommend.map((rec, idx) => (
              <div key={idx} className={styles.changeRow}>
                <div className={styles.changeCol}>
                  <div className={styles.changeLabel}>현재 사용 중</div>
                  <div className={styles.changeName}>{rec.before.name}</div>
                  <div className={styles.changeDesc}>{rec.before.desc}</div>
                </div>
                <div className={styles.changeArrow}>↓</div>
                <div className={styles.changeCol}>
                  <div className={styles.changeLabelMint}>추천 제품</div>
                  <div className={styles.changeName}>{rec.after.name}</div>
                  <div className={styles.changeDesc}>{rec.after.desc}</div>
                  <button className={styles.buyBtn}>구매하기</button>
                </div>
              </div>
            ))}
          </div>
        </div>
        {/* 추가 활용 제품 */}
        <div className={styles.extraBox}>
          {extraRecommend.map((item, idx) => (
            <div key={idx} className={styles.extraCard}>
              <div className={styles.extraName}>{item.name}</div>
              <div className={styles.extraDesc}>{item.desc}</div>
              <button className={styles.buyBtn}>구매하기</button>
            </div>
          ))}
        </div>
        <div className={styles.bottomBtns}>
          <button className={styles.prevBtn}>이전 단계</button>
          <button className={styles.nextBtn}>완료</button>
        </div>
      </main>
    </div>
  );
}
