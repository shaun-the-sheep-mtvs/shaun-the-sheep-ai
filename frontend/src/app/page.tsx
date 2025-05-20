import React from "react";
import styles from "./page.module.css";
import Link from 'next/link'

// 예시 데이터 (props/API로 대체 가능)
const checklist = { 수분: 70, 유분: 50, 민감도: 65, 탄력: 45 };
const analysis = {
  type: "민감형",
  description: "당신의 피부는 외부 자극에 민감하게 반응하는 타입입니다.",
  advice: "자극이 적은 제품을 사용해보세요!",
};
const products = [
  { name: "수분", description: "진정효과 수분공급 민감피부용 에센스" },
  { name: "진정", description: "피부 진정케어 세럼 민감 피부용" },
  { name: "보습", description: "저자극 수분 크림 민감 피부용" },
];

export default function Home() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [form, setForm] = useState<{ title: string; content: string }>({ title: "", content: "" });
  const [editing, setEditing] = useState<Post | null>(null);

  useEffect(() => {
    fetch(API_URL)
      .then(res => res.json())
      .then(setPosts);
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editing) {
      const res = await fetch(`${API_URL}/${editing.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...editing, ...form }),
      });
      if (res.ok) {
        const updated = await res.json();
        setPosts(posts.map(p => (p.id === editing.id ? updated : p)));
        setEditing(null);
        setForm({ title: "", content: "" });
      }
    } else {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      if (res.ok) {
        setPosts([...posts, await res.json()]);
        setForm({ title: "", content: "" });
      }
    }
  };

  const handleEdit = (post: Post) => {
    setEditing(post);
    setForm({ title: post.title, content: post.content });
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (res.ok) setPosts(posts.filter(p => p.id !== id));
  };

  return (
    <div className={styles.wrapper}>
      {/* 사이드바 */}
      <aside className={styles.sidebar}>
        <h2 className={styles.logo}>스킨케어</h2>
        <nav>
          <ul className={styles.menu}>
            <li className={styles.menuActive}>홈화면</li>
            <li><Link href="/checklist">검사하기</Link></li>
            <li>AI 채팅</li>
            <li>회원정보</li>
          </ul>
        </nav>
      </aside>
      {/* 메인 */}
      <main className={styles.mainContent}>
        <div className={styles.greetingRight}>○○님 안녕하세요</div>
        <section className={styles.resultSection}>
          <div className={styles.checklistBox}>
            <h3>체크리스트 결과</h3>
            <div className={styles.barWrap}>
              <div>수분 <span>{checklist.수분}%</span></div>
              <div className={styles.bar}><div style={{width: `${checklist.수분}%`}} className={styles.barGold}></div></div>
              <div>유분 <span>{checklist.유분}%</span></div>
              <div className={styles.bar}><div style={{width: `${checklist.유분}%`}} className={styles.barGoldLight}></div></div>
              <div>민감도 <span>{checklist.민감도}%</span></div>
              <div className={styles.bar}><div style={{width: `${checklist.민감도}%`}} className={styles.barRed}></div></div>
              <div>탄력 <span>{checklist.탄력}%</span></div>
              <div className={styles.bar}><div style={{width: `${checklist.탄력}%`}} className={styles.barGray}></div></div>
            </div>
          </div>
          <div className={styles.analysisBox}>
            <h3>분석 결과</h3>
            <div className={styles.analysisType}>{analysis.type}</div>
            <div className={styles.analysisDesc}>{analysis.description}</div>
            <div className={styles.analysisAdvice}>{analysis.advice}</div>
          </div>
        </section>
        {/* 추천 제품 */}
        <section className={styles.productSection}>
          <h4>회원님에게 어떤 제품이 좋을까요?</h4>
          <div className={styles.productList}>
            {products.map((p, i) => (
              <div key={i} className={styles.productCard}>
                <div className={styles.productImg}></div>
                <div className={styles.productName}>{p.name}</div>
                <div className={styles.productDesc}>{p.description}</div>
                <button className={styles.buyBtn}>자세히 보기</button>
              </div>
            ))}
          </div>
        </section>
        {/* 더 자세한 추천 */}
        <section className={styles.recommendSection}>
          <div className={styles.recommendTitle}>더 자세한 추천을 원한다면?</div>
          <button className={styles.goBtn}>검사 받으러 GO!</button>
          <div className={styles.concernTitle}>고민별 추천</div>
          <div className={styles.concernDesc}>어떤 부분을 개선하고 싶으신가요?</div>
          <div className={styles.concernList}>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>😅</div>
              <div className={styles.concernLabel}>유분</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>😬</div>
              <div className={styles.concernLabel}>여드름</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥲</div>
              <div className={styles.concernLabel}>건조</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥵</div>
              <div className={styles.concernLabel}>열감</div>
            </div>
            <div className={styles.concernItem}>
              <div className={styles.concernIcon}>🥹</div>
              <div className={styles.concernLabel}>홍조</div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}