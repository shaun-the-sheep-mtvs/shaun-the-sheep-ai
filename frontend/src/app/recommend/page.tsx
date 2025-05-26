'use client';

import Link from "next/link"
import { Bell, Home, ShoppingBag, Info, Heart, Sparkles, ClipboardList, Droplet, Loader } from "lucide-react"
import Image from "next/image"
import styles from "./recommend.module.css"
import { useState, useEffect } from "react"
import axios from "axios"
import { useCurrentUser } from "@/data/useCurrentUser";

interface Product {
  제품명: string;
  추천타입: string;
  성분: string[];
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

// 백엔드 응답 인터페이스
interface BackendResponse {
  skinType: string;
  concerns: string[];
  recommendations: {
    toner: Array<{
      제품명: string;
      추천타입: string;
      성분: string[];
    }>;
    serum: Array<{
      제품명: string;
      추천타입: string;
      성분: string[];
    }>;
    lotion: Array<{
      제품명: string;
      추천타입: string;
      성분: string[];
    }>;
    cream: Array<{
      제품명: string;
      추천타입: string;
      성분: string[];
    }>;
  };
}

// 백엔드 응답을 프론트엔드 형식으로 변환하는 함수
function parseBackendResponse(data: BackendResponse): RecommendData {
  // 응답 데이터 안전성 검사
  if (!data || !data.recommendations) {
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
  
  // 각 제품 카테고리 변환 함수
  const convertProducts = (products: Array<any> = []) => {
    return products.map(item => ({
      제품명: item.제품명 || '제품명 없음',
      추천타입: item.추천타입 || '정보 없음',
      성분: item.성분 || []
    }));
  };
  
  return {
    skinType: data.skinType || '알 수 없음',
    concerns: data.concerns || ['정보 없음'],
    recommendations: {
      토너: convertProducts(data.recommendations.toner),
      세럼: convertProducts(data.recommendations.serum),
      로션: convertProducts(data.recommendations.lotion),
      크림: convertProducts(data.recommendations.cream)
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
        const response = await axios.post('http://localhost:8080/api/recommend/diagnoses', {}, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        
        // 백엔드 응답 로깅
        console.log('백엔드 응답(원본):', response.data);
        
        // 백엔드에서 문자열 형태로 JSON이 전달될 수 있으므로 파싱 시도
        let parsedResponse;
        try {
          // 응답이 문자열인 경우 JSON으로 파싱
          if (typeof response.data === 'string') {
            // Gemini API가 반환하는 마크다운 형식의 코드 블록 마커 제거
            let cleanedJson = response.data;
            
            // 시작 코드 블록 마커(```json, ```) 제거
            cleanedJson = cleanedJson.replace(/^```json\s*|^```\s*/m, '');
            
            // 끝 코드 블록 마커(```) 제거
            cleanedJson = cleanedJson.replace(/\s*```\s*$/m, '');
            
            console.log('정제된 JSON 문자열:', cleanedJson);
            
            // 정제된 문자열 파싱
            parsedResponse = JSON.parse(cleanedJson);
            console.log('파싱된 백엔드 응답:', parsedResponse);
          } else {
            // 이미 객체인 경우 그대로 사용
            parsedResponse = response.data;
          }
        } catch (parseError) {
          console.error('JSON 파싱 오류:', parseError, '원본 데이터:', response.data);
          parsedResponse = null;
        }
        
        // 테스트용 더미 데이터 (백엔드 응답이 예상과 다른 경우)
        if (!parsedResponse || !parsedResponse.recommendations) {
          console.warn('백엔드 응답이 예상 형식과 다르므로 더미 데이터를 사용합니다.');
          
          // 더미 데이터 - 백엔드에서 제공한 형식과 동일하게 구성
          const dummyData = {
            skin_type: "건성",
            concerns: ["건조함", "각질"],
            recommendations: {
              toner: [
                {
                  제품명: "라운드랩 1025 독도 토너",
                  추천이유: "피부 진정 및 수분 공급에 탁월합니다.",
                  성분: ["해양 심층수", "판테놀", "알란토인"]
                }
              ],
              serum: [
                {
                  제품명: "토리든 다이브인 세럼",
                  추천이유: "저분자 히알루론산이 피부 속까지 수분을 채워줍니다.",
                  성분: ["저분자 히알루론산", "D-판테놀", "알란토인"]
                }
              ],
              lotion: [
                {
                  제품명: "세라비 모이스춰라이징 로션",
                  추천이유: "세라마이드 성분이 피부 장벽을 강화합니다.",
                  성분: ["세라마이드", "히알루론산", "글리세린"]
                }
              ],
              cream: [
                {
                  제품명: "피지오겔 DMT 크림",
                  추천이유: "건조하고 민감한 피부에 진정 효과를 제공합니다.",
                  성분: ["피지오겔 복합체", "스쿠알란", "글리세린"]
                }
              ]
            }
          };
          const parsedData = parseBackendResponse(dummyData as unknown as BackendResponse);
          setRecommendData(parsedData);
        } else {
          // 정상적인 백엔드 응답인 경우
          const parsedData = parseBackendResponse(parsedResponse);
          setRecommendData(parsedData);
        }
      } catch (err) {
        setError('데이터를 불러오는데 실패했습니다.');
        console.error('Error fetching data:', err);
        
        // 오류 발생 시 더미 데이터 표시 (선택적)
        // 실제 상용 환경에서는 적절한 오류 처리를 해야 함
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleBuyButtonClick = (product: Product) => {
    window.open(`https://www.coupang.com/np/search?component=&q=${product.제품명}`, '_blank ');
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
          <p className={styles.loadingText}>맞춤형 스킨케어 정보를 불러오는 중입니다</p>
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
          <button onClick={() => window.location.reload()} className={`${styles["checklist-button"]} ${styles.retryButton}`}>
            다시 시도하기
          </button>
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
              <strong>{recommendData.skinType}</strong> 피부를 위해 엄선된 완벽한 스킨케어 루틴으로
              {recommendData.concerns.length > 0 && 
                <span className={styles["highlight"]}> {recommendData.concerns.join(', ')}</span>} 고민을 해결해보세요!
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
                    <Image src="/placeholder.svg" alt={product.제품명} width={120} height={160} className={styles["product-image"]} />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{product.제품명}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["toner-attribute"]}`}>{product.추천타입 || "피부 진정"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {product.성분.length > 0 
                            ? product.성분.map((ingredient, idx) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < product.성분.length - 1 ? ', ' : ''}
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
                    <Image src="/placeholder.svg" alt={product.제품명} width={120} height={160} className={styles["product-image"]} />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{product.제품명}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["serum-attribute"]}`}>{product.추천타입 || "수분 공급"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {product.성분.length > 0 
                            ? product.성분.map((ingredient, idx) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < product.성분.length - 1 ? ', ' : ''}
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
                    <Image src="/placeholder.svg" alt={product.제품명} width={120} height={160} className={styles["product-image"]} />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{product.제품명}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["lotion-attribute"]}`}>{product.추천타입 || "보습 강화"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {product.성분.length > 0 
                            ? product.성분.map((ingredient, idx) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < product.성분.length - 1 ? ', ' : ''}
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
                    <Image src="/placeholder.svg" alt={product.제품명} width={120} height={160} className={styles["product-image"]} />
                    <div className={styles["product-badge"]}>추천</div>
                  </div>
                  <div className={styles["product-info"]}>
                    <div className={styles["product-details"]}>
                      <p className={styles["product-title"]}>{product.제품명}</p>
                      <div className={styles["tooltip"]}>
                        <p className={`${styles["product-attribute"]} ${styles["cream-attribute"]}`}>{product.추천타입 || "보습 케어"}</p>
                      </div>
                      <div className={styles["product-ingredients-container"]}>
                        <p className={styles["ingredients-label"]}>
                          <Droplet size={14} className={styles["ingredients-icon"]} />
                          주요 성분
                        </p>
                        <p className={styles["product-ingredients"]}>
                          {product.성분.length > 0 
                            ? product.성분.map((ingredient, idx) => (
                                <span key={idx} className={styles["ingredient-item"]}>
                                  {ingredient}{idx < product.성분.length - 1 ? ', ' : ''}
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