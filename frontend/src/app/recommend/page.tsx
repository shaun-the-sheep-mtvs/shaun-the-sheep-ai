import Link from "next/link"
import { Bell, Home } from "lucide-react"
import Image from "next/image"
import styles from "./recommend.module.css"

export default function RecommendPage() {
  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <Link href="/" className={styles.homeLink}>
          <Home className={styles.homeIcon} />
        </Link>
        <h1 className={styles.headerTitle}>스킨케어</h1>
        <Bell className={styles.bellIcon} />
      </header>

      {/* Greeting Section */}
      <section className={styles["greeting-section"]}>
        <div className={styles["greeting-content"]}>
          <div className={styles["user-avatar"]}>👤</div>
          <p className={styles["greeting-text"]}>
            안녕하세요, <span className={styles["user-name"]}>마라님</span>
          </p>
        </div>
      </section>

      {/* Recommendation Section */}
      <section className={styles["recommendation-section"]}>
        <h2 className={styles["section-title"]}>회원님에게 어떤 제품이 좋을까요?</h2>

        <div className={styles["recommendation-card"]}>
          <h3 className={styles["recommendation-title"]}>민감형 ⭐</h3>
          <p className={styles["recommendation-text"]}>
            당신의 피부는 외부 자극에 민감하게 반응하는 편입니다. 진정 효과가 있는 제품을 사용하는 것이 좋습니다.
          </p>
          <div className={styles["recommendation-tip"]}>자극이 적은 제품을 사용해보세요!</div>
        </div>

        <button className={styles["checklist-button"]}>체크리스트 작성하러 가기</button>
      </section>

      {/* Search Bar */}
      <div className={styles["search-container"]}>
        <div className={styles["search-bar"]}>
          <span className={styles["search-icon"]}>🔍</span>
          <input type="text" placeholder="검색" className={styles["search-input"]} />
        </div>
      </div>

      {/* Toner Section */}
      <section className={styles["product-section"]}>
        <div className={styles["category-header"]}>
          <div className={styles["category-icon"]}>💬</div>
          <h2 className={styles["category-title"]}>토너</h2>
        </div>

        <div className={styles["product-grid"]}>
          {[1, 2, 3].map((item) => (
            <div key={`toner-${item}`} className={styles["product-card"]}>
              <div className={styles["product-image-container"]}>
                <Image src="/placeholder.svg" alt="제품 이미지" width={120} height={160} className={styles["product-image"]} />
              </div>
              <div className={styles["product-info"]}>
                <p className={styles["product-title"]}>다이브인 자몽차 히알루론산 세럼 100ml 기획 (+수딩크림 50ml)</p>
                <p className={styles["product-attribute"]}>수분</p>
                <button className={`${styles["buy-button"]} ${styles["toner-button"]}`}>구매</button>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Serum Section */}
      <section className={styles["product-section"]}>
        <div className={styles["category-header"]}>
          <div className={styles["category-icon"]}>💬</div>
          <h2 className={styles["category-title"]}>세럼</h2>
        </div>

        <div className={styles["product-grid"]}>
          {[1, 2, 3].map((item) => (
            <div key={`serum-${item}`} className={styles["product-card"]}>
              <div className={styles["product-image-container"]}>
                <Image src="/placeholder.svg" alt="제품 이미지" width={120} height={160} className={styles["product-image"]} />
              </div>
              <div className={styles["product-info"]}>
                <p className={styles["product-title"]}>다이브인 자몽차 히알루론산 세럼 100ml 기획 (+수딩크림 50ml)</p>
                <p className={styles["product-attribute"]}>유분</p>
                <button className={`${styles["buy-button"]} ${styles["serum-button"]}`}>구매</button>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
