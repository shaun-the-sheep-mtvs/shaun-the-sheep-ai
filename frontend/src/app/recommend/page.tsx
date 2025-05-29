'use client';

import Link from "next/link"
import { Bell, Home, ShoppingBag, Info, Heart, Sparkles, ClipboardList, Droplet, Loader } from "lucide-react"
import Image from "next/image"
import styles from "./recommend.module.css"
import { useState, useEffect } from "react"
import axios from "axios"
import { useCurrentUser } from "@/data/useCurrentUser";
import apiConfig from "@/config/api";

interface Product {
  id: string;
  formulation: string;
  ingredients: string[];
  recommendedType: string;
  productName: string;
  userId: string;
  imageUrl: string;
}

interface RecommendData {
  skinType: string;
  concerns: string[];
  recommendations: {
    토너: Product[];
    세럼: Product[];
    로션: Product[];
    크림: Product[];
  };
}

// 백엔드 응답 인터페이스 - 사용자 정보 + 제품 배열
interface BackendResponse {
  userInfo: {
    skinType: string;
    troubles: string[];
  };
  products: Product[];
}

// 백엔드 응답을 프론트엔드 형식으로 변환하는 함수
function parseBackendResponse(data: BackendResponse): RecommendData {
  // 응답 데이터 안전성 검사
  if (!data || !data.userInfo || !data.products) {
    console.error('백엔드 응답 구조가 올바르지 않습니다:', data);
    
    // 기본 데이터 반환
    return {
      skinType: '알 수 없음',
      concerns: ['정보 없음'],
      recommendations: {
        토너: [],
        세럼: [],
        로션: [],
        크림: []
      }
    };
  }
  
  // formulation을 기준으로 제품들을 분류
  const toners = data.products.filter(product => product.formulation === 'toner');
  const serums = data.products.filter(product => product.formulation === 'serum');
  const lotions = data.products.filter(product => product.formulation === 'lotion');
  const creams = data.products.filter(product => product.formulation === 'cream');
  
  return {
    skinType: data.userInfo.skinType || '알 수 없음',
    concerns: data.userInfo.troubles || ['정보 없음'],
    recommendations: {
      토너: toners,
      세럼: serums,
      로션: lotions,
      크림: creams
    }
  };
}

export default function RecommendPage() {
  const [recommendData, setRecommendData] = useState<RecommendData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user, loading: userLoading } = useCurrentUser();


  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const fetchData = async () => {
      try {
        const response = await axios.get(apiConfig.endpoints.recommend.user, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        
        // 백엔드 응답 로깅
        console.log('백엔드 응답(원본):', response.data);
        
        // 응답 데이터 검증 및 처리
        if (!response.data) {
          setError('저장된 추천 데이터가 없습니다. 체크리스트를 먼저 작성해주세요.');
          return;
        }

        // 백엔드에서 일관된 JSON 객체로 응답한다고 가정
        const parsedData = parseBackendResponse(response.data);
        
        // 추천 데이터가 비어있는지 확인
        const hasRecommendations = Object.values(parsedData.recommendations).some(
          category => category.length > 0
        );
        
        if (!hasRecommendations) {
          setError('저장된 추천 데이터가 없습니다. 체크리스트를 먼저 작성해주세요.');
          return;
        }
        
        setRecommendData(parsedData);
      } catch (err) {
        console.error('Error fetching data:', err);
        
        // 에러 타입에 따른 구체적인 메시지 설정
        if (axios.isAxiosError(err)) {
          const status = err.response?.status;
          if (status === 404) {
            setError('저장된 추천 데이터가 없습니다. 체크리스트를 먼저 작성해주세요.');
          } else if (status === 401) {
            setError('로그인이 필요합니다. 다시 로그인해주세요.');
          } else if (status && status >= 500) {
            setError('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
          } else {
            setError('추천 데이터를 불러오는데 실패했습니다.');
          }
        } else {
          setError('네트워크 연결을 확인해주세요.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // UI 표시용 헬퍼 함수들
  const getProductName = (product: Product) => product.productName || '제품명 없음';
  const getRecommendedType = (product: Product) => product.recommendedType || '정보 없음';
  const getIngredients = (product: Product) => product.ingredients || [];
  const getImgUrl = (product:Product) => {
    console.log('Product:', product);
    console.log('Image URL:', product.imageUrl);
    return product.imageUrl;
  };
  const handleBuyButtonClick = (product: Product) => {
    window.open(`https://www.coupang.com/np/search?component=&q=${product.productName}`, '_blank ');
  }

  if (loading) return (
    <div className={styles.container}>
      <header className={styles.header}>
        <Link href="/" className={styles.homeLink}>
          <Home className={styles.homeIcon} />
        </Link>
        <h1 className={styles.headerTitle}>스킨케어 추천</h1>
        <Bell className={styles.bellIcon} />
      </header>
      
      <div className={`${styles["content-wrapper"]} ${styles.centerContent}`}>
        <div className={styles.loadingContainer}>
          <div className={styles.loadingSpinner}>
            <Loader className={styles.loadingIcon} />
          </div>
          <p className={styles.loadingText}>저장된 추천 정보를 불러오는 중입니다</p>
          <div className={styles.loadingBar}>
            <div className={styles.loadingBarProgress}></div>
          </div>
        </div>
      </div>
    </div>
  );
  
  if (error) return (
    <div className={styles.container}>
      <header className={styles.header}>
        <Link href="/" className={styles.homeLink}>
          <Home className={styles.homeIcon} />
        </Link>
        <h1 className={styles.headerTitle}>스킨케어 추천</h1>
        <Bell className={styles.bellIcon} />
      </header>
      
      <div className={`${styles["content-wrapper"]} ${styles.centerContent}`}>
        <div className={styles.errorContainer}>
          <div className={styles.errorIcon}>❌</div>
          <p className={styles.errorText}>{error}</p>
          <div className={styles.errorButtons}>
            <button onClick={() => window.location.reload()} className={`${styles["checklist-button"]} ${styles.retryButton}`}>
              다시 시도하기
            </button>
            {error.includes('체크리스트') && (
              <Link href="/checklist" className={`${styles["checklist-button"]}`}>
                <ClipboardList size={16} />
                체크리스트 작성하러 가기
              </Link>
            )}
          </div>
        </div>
      </div>
    </div>
  );
  
  if (!recommendData) return (
    <div className={styles.container}>
      <header className={styles.header}>
        <Link href="/" className={styles.homeLink}>
          <Home className={styles.homeIcon} />
        </Link>
        <h1 className={styles.headerTitle}>스킨케어 추천</h1>
        <Bell className={styles.bellIcon} />
      </header>
      
      <div className={`${styles["content-wrapper"]} ${styles.centerContent}`}>
        <div className={styles.errorContainer}>
          <div className={styles.errorIcon}>📭</div>
          <p className={styles.errorText}>데이터가 없습니다</p>
          <Link href="/checklist" className={`${styles["checklist-button"]}`}>
            <ClipboardList size={16} />
            체크리스트 작성하러 가기
          </Link>
        </div>
      </div>
    </div>
  );

  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <Link href="/" className={styles.homeLink}>
          <Home className={styles.homeIcon} />
        </Link>
        <h1 className={styles.headerTitle}>맞춤형 스킨케어 추천</h1>
        <Bell className={styles.bellIcon} />
      </header>

      {/* 콘텐츠 래퍼 추가 - 데스크탑에서 사이드바와 메인 콘텐츠 구분 */}
      <div className={styles["content-wrapper"]}>
        {/* 사이드바 영역 - 반응형으로 배치 변경 */}
        <div className={styles.sidebar}>
          {/* Greeting Section */}
          <section className={styles["greeting-section"]}>
            <div className={styles["greeting-background"]}></div>
            <div className={styles["greeting-content"]}>
              <div className={styles["user-avatar"]}>
                <span className={styles["avatar-text"]}>{user?.username.charAt(0)}</span>
              </div>
              <div className={styles["greeting-text-container"]}>
                <div className={styles["greeting-text-wrapper"]}>
                  <p className={styles["greeting-label"]}>반갑습니다</p>
                  <p className={styles["greeting-text"]}>
                    <span className={styles["user-name"]}>
                    {userLoading ? 'Loading...' : user ? user.username : 'Guest'}
                      </span> 님!
                  </p>
                </div>
              </div>
            </div>
          </section>

          {/* Recommendation Section */}
          <section className={styles["recommendation-section"]}>
            <h2 className={styles["section-title"]}>회원님을 위한 스킨케어</h2>

            <div className={styles["recommendation-card"]}>
              <h3 className={styles["recommendation-title"]}>{recommendData.skinType} 피부 타입 ⭐</h3>
              <p className={styles["recommendation-text"]}>
                당신의 피부는 <strong>{recommendData.concerns.join(', ')}</strong>에 대한 케어가 필요합니다.
              </p>
              <div className={styles["recommendation-tip"]}>✨ 아래 추천 제품을 확인해보세요!</div>
            </div>

            <Link href="/checklist" className={styles["checklist-button"]}>
              <ClipboardList size={16} />
              체크리스트 작성하러 가기
            </Link>
          </section>
        </div>

        {/* 메인 콘텐츠 영역 */}
        <div className={styles["main-content"]}>
          {/* 카테고리 제목 개선 */}
          <div className={styles["main-title-container"]}>
            <div className={styles["title-background"]}></div>
            <h2 className={styles["main-title"]}>
              <Sparkles className={styles["title-icon-svg"]} />
              맞춤형 추천 제품
            </h2>
            <p className={styles["main-subtitle"]}>
              <strong>{recommendData.skinType}</strong> 피부를 위해 엄선된 완벽한 스킨케어 루틴으로 <br />
              {recommendData.concerns.length > 0 && 
                <span className={styles["highlight"]}> {recommendData.concerns.join(', ')}</span>} 고민을 해결해보세요! <br /> <br />
            </p>
            <div className={styles["title-divider"]}></div>
          </div>

          {/* Toner Section */}
          <section className={`${styles["product-section"]} ${styles["toner-section"]}`}>
            <div className={styles["category-header"]}>
              <div className={`${styles["category-icon"]} ${styles["toner-icon"]}`}>💧</div>
              <h2 className={styles["category-title"]}>토너</h2>
            </div>

            <div className={styles["product-grid"]}>
              {recommendData.recommendations.토너.map((product, index) => (
                <div key={`toner-${index}`} className={styles["product-card"]}>
                  <div className={styles["product-image-container"]}>
                  <img 
                    src={getImgUrl(product)} 
                    alt={getProductName(product)} 
                    width={120} 
                    height={160} 
                    className={styles["product-image"]}
                  />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{getProductName(product)}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["toner-attribute"]}`}>{getRecommendedType(product) || "피부 진정"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {getIngredients(product).length > 0 
                            ? getIngredients(product).map((ingredient: string, idx: number) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < getIngredients(product).length - 1 ? ', ' : ''}
                                </span>
                              ))
                            : <span className={styles["no-ingredients"]}>성분 정보가 없습니다</span>
                          }
                        </p>
                      </div>
                    </div>
                    <div className={styles["button-group"]}>
                      <button onClick={() => handleBuyButtonClick(product)} className={`${styles["buy-button"]} ${styles["toner-button"]}`} >
                        <ShoppingBag size={16} /> 구매하기
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Serum Section */}
          <section className={`${styles["product-section"]} ${styles["serum-section"]}`}>
            <div className={styles["category-header"]}>
              <div className={`${styles["category-icon"]} ${styles["serum-icon"]}`}>✨</div>
              <h2 className={styles["category-title"]}>세럼</h2>
            </div>

            <div className={styles["product-grid"]}>
              {recommendData.recommendations.세럼.map((product, index) => (
                <div key={`serum-${index}`} className={styles["product-card"]}>
                  <div className={styles["product-image-container"]}>
                  <img 
                    src={getImgUrl(product)} 
                    alt={getProductName(product)} 
                    width={120} 
                    height={160} 
                    className={styles["product-image"]}
                  />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{getProductName(product)}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["serum-attribute"]}`}>{getRecommendedType(product) || "수분 공급"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {getIngredients(product).length > 0 
                            ? getIngredients(product).map((ingredient: string, idx: number) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < getIngredients(product).length - 1 ? ', ' : ''}
                                </span>
                              ))
                            : <span className={styles["no-ingredients"]}>성분 정보가 없습니다</span>
                          }
                        </p>
                      </div>
                    </div>
                    <div className={styles["button-group"]}>
                      <button onClick={() => handleBuyButtonClick(product)} className={`${styles["buy-button"]} ${styles["serum-button"]}`}>
                        <ShoppingBag size={16} /> 구매하기
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Lotion Section */}
          <section className={`${styles["product-section"]} ${styles["lotion-section"]}`}>
            <div className={styles["category-header"]}>
              <div className={`${styles["category-icon"]} ${styles["lotion-icon"]}`}>🧴</div>
              <h2 className={styles["category-title"]}>로션</h2>
            </div>

            <div className={styles["product-grid"]}>
              {recommendData.recommendations.로션.map((product, index) => (
                <div key={`lotion-${index}`} className={styles["product-card"]}>
                  <div className={styles["product-image-container"]}>
                  <img 
                    src={getImgUrl(product)} 
                    alt={getProductName(product)} 
                    width={120} 
                    height={160} 
                    className={styles["product-image"]}
                  />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{getProductName(product)}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["lotion-attribute"]}`}>{getRecommendedType(product) || "보습 강화"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {getIngredients(product).length > 0 
                            ? getIngredients(product).map((ingredient: string, idx: number) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < getIngredients(product).length - 1 ? ', ' : ''}
                                </span>
                              ))
                            : <span className={styles["no-ingredients"]}>성분 정보가 없습니다</span>
                          }
                        </p>
                      </div>
                    </div>
                    <div className={styles["button-group"]}>
                      <button onClick={() => handleBuyButtonClick(product)} className={`${styles["buy-button"]} ${styles["lotion-button"]}`}>
                        <ShoppingBag size={16} /> 구매하기
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Cream Section */}
          <section className={`${styles["product-section"]} ${styles["cream-section"]}`}>
            <div className={styles["category-header"]}>
              <div className={`${styles["category-icon"]} ${styles["cream-icon"]}`}>🥛</div>
              <h2 className={styles["category-title"]}>크림</h2>
            </div>

            <div className={styles["product-grid"]}>
              {recommendData.recommendations.크림.map((product, index) => (
                <div key={`cream-${index}`} className={styles["product-card"]}>
                  <div className={styles["product-image-container"]}>
                  <img 
                    src={getImgUrl(product)} 
                    alt={getProductName(product)} 
                    width={120} 
                    height={160} 
                    className={styles["product-image"]}
                  />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{getProductName(product)}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["cream-attribute"]}`}>{getRecommendedType(product) || "보습 케어"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {getIngredients(product).length > 0 
                            ? getIngredients(product).map((ingredient: string, idx: number) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < getIngredients(product).length - 1 ? ', ' : ''}
                                </span>
                              ))
                            : <span className={styles["no-ingredients"]}>성분 정보가 없습니다</span>
                          }
                        </p>
                      </div>
                    </div>
                    <div className={styles["button-group"]}>
                      <button onClick={() => handleBuyButtonClick(product)} className={`${styles["buy-button"]} ${styles["cream-button"]}`}>
                        <ShoppingBag size={16} /> 구매하기
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </div>
  )
}