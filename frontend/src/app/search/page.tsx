"use client";

import React, { useState, useEffect } from "react";
import { useSearchParams, useRouter } from 'next/navigation';
import Navbar from "@/components/Navbar";
import { Search, ArrowLeft, ShoppingBag } from "lucide-react";
import { useCurrentUser } from '@/data/useCurrentUser';
import { apiConfig } from '@/config/api';
import styles from './search.module.css';

interface SearchResult {
  name: string;
  description: string;
  brand?: string;
  ingredients?: string[];
  imageUrl?: string;
  productName?: string;
  brandName?: string;
}

export default function SearchPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { user } = useCurrentUser();
  
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [searchType, setSearchType] = useState<'all' | 'brand' | 'productName' | 'ingredient'>('all');
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
    
    if (!token) {
      router.push('/login');
    }
  }, [router]);

  useEffect(() => {
    const query = searchParams.get('q');
    const type = searchParams.get('type') as 'all' | 'brand' | 'productName' | 'ingredient';
    
    if (query) {
      setSearchQuery(query);
      if (type && ['all', 'brand', 'productName', 'ingredient'].includes(type)) {
        setSearchType(type);
      }
      performSearch(query, type || 'all');
    }
  }, [searchParams]);

  const performSearch = async (query: string, type: string) => {
    if (!query.trim()) return;
    
    setIsSearching(true);
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) return;

      const searchParams = new URLSearchParams({
        mode: type,
        word: query
      });
      
      const response = await fetch(`${apiConfig.baseURL}/api/products/search/products?${searchParams.toString()}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      if (response.ok) {
        const results = await response.json();
        const transformedResults = results.map((product: any) => ({
          name: product.productName || product.name,
          description: product.brandName ? 
            `${product.brandName} - ${product.ingredients?.join(', ') || '성분 정보 없음'}` : 
            product.description || '설명 없음',
          brand: product.brandName,
          ingredients: product.ingredients,
          imageUrl: product.imageUrl
        }));
        setSearchResults(transformedResults);
      }
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      performSearch(searchQuery, searchType);
      // URL 업데이트
      const newParams = new URLSearchParams();
      newParams.set('q', searchQuery);
      newParams.set('type', searchType);
      router.push(`/search?${newParams.toString()}`);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    window.location.reload();
  };

  const getSearchTypeLabel = (type: string) => {
    switch (type) {
      case 'all': return '전체';
      case 'brand': return '브랜드';
      case 'productName': return '제품명';
      case 'ingredient': return '성분';
      default: return '전체';
    }
  };

  return (
    <div className={styles.wrapper}>
      <Navbar isLoggedIn={isLoggedIn} onLogout={handleLogout} />
      
      <main className={styles.mainContent}>
        <div className={styles.contentContainer}>
          {/* 뒤로가기 버튼 */}
          <button 
            onClick={() => router.back()}
            className={styles.backButton}
          >
            <ArrowLeft className={styles.backIcon} />
            뒤로가기
          </button>

          {/* 검색 헤더 */}
          <div className={styles.searchHeader}>
            <h1 className={styles.pageTitle}>검색 결과</h1>
            <div className={styles.searchInfo}>
              <span className={styles.searchQuery}>"{searchQuery}"</span>
              <span className={styles.searchTypeInfo}>({getSearchTypeLabel(searchType)} 검색)</span>
            </div>
          </div>

          {/* 새로운 검색 */}
          <div className={styles.searchSection}>
            <form className={styles.searchForm} onSubmit={handleSearchSubmit}>
              {/* 검색 타입 선택 */}
              <div className={styles.searchTypeSelector}>
                <span className={styles.searchTypeLabel}>검색 범위:</span>
                <div className={styles.searchTypeOptions}>
                  <label className={styles.searchTypeOption}>
                    <input
                      type="radio"
                      name="searchType"
                      value="all"
                      checked={searchType === 'all'}
                      onChange={(e) => setSearchType('all')}
                      className={styles.searchTypeRadio}
                    />
                    <span className={styles.searchTypeText}>전체</span>
                  </label>
                  <label className={styles.searchTypeOption}>
                    <input
                      type="radio"
                      name="searchType"
                      value="brand"
                      checked={searchType === 'brand'}
                      onChange={(e) => setSearchType('brand')}
                      className={styles.searchTypeRadio}
                    />
                    <span className={styles.searchTypeText}>브랜드</span>
                  </label>
                  <label className={styles.searchTypeOption}>
                    <input
                      type="radio"
                      name="searchType"
                      value="productName"
                      checked={searchType === 'productName'}
                      onChange={(e) => setSearchType('productName')}
                      className={styles.searchTypeRadio}
                    />
                    <span className={styles.searchTypeText}>제품명</span>
                  </label>
                  <label className={styles.searchTypeOption}>
                    <input
                      type="radio"
                      name="searchType"
                      value="ingredient"
                      checked={searchType === 'ingredient'}
                      onChange={(e) => setSearchType('ingredient')}
                      className={styles.searchTypeRadio}
                    />
                    <span className={styles.searchTypeText}>성분</span>
                  </label>
                </div>
              </div>

              <div className={styles.searchInputContainer}>
                <Search className={styles.searchIcon} />
                <input
                  type="text"
                  placeholder="제품명, 브랜드, 성분을 검색해보세요..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className={styles.searchInput}
                />
                <button 
                  type="submit" 
                  className={styles.searchButton}
                  disabled={isSearching}
                >
                  {isSearching ? '검색 중...' : '검색'}
                </button>
              </div>
            </form>
          </div>

          {/* 검색 결과 */}
          <div className={styles.resultsSection}>
            {isSearching ? (
              <div className={styles.loadingContainer}>
                <div className={styles.loadingSpinner}></div>
                <p>검색 중입니다...</p>
              </div>
            ) : searchResults.length > 0 ? (
              <>
                <div className={styles.resultsSummary}>
                  <span className={styles.resultsCount}>{searchResults.length}개</span>의 제품을 찾았습니다
                </div>
                <div className={styles.productGrid}>
                  {searchResults.map((product, index) => (
                    <div key={index} className={styles.productCard}>
                      <div className={styles.productImage}>
                        {product.imageUrl ? (
                          <img
                            src={product.imageUrl}
                            alt={product.name}
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.style.display = 'none';
                              const parent = target.parentElement;
                              if (parent) {
                                parent.innerHTML = `
                                  <div class="${styles.placeholderImage}">
                                    <div class="${styles.placeholderIcon}">📦</div>
                                    <div class="${styles.placeholderText}">이미지 없음</div>
                                  </div>
                                `;
                              }
                            }}
                          />
                        ) : (
                          <div className={styles.placeholderImage}>
                            <div className={styles.placeholderIcon}>📦</div>
                            <div className={styles.placeholderText}>이미지 없음</div>
                          </div>
                        )}
                      </div>
                      <div className={styles.productInfo}>
                        <h3 className={styles.productName}>{product.name}</h3>
                        <p className={styles.productDescription}>{product.description}</p>
                        {product.brand && (
                          <span className={styles.productBrand}>{product.brand}</span>
                        )}
                      </div>
                      <button className={styles.productButton}>
                        <ShoppingBag className={styles.buttonIcon} />
                        자세히 보기
                      </button>
                    </div>
                  ))}
                </div>
              </>
            ) : searchQuery ? (
              <div className={styles.noResults}>
                <div className={styles.noResultsIcon}>🔍</div>
                <h3>검색 결과가 없습니다</h3>
                <p>다른 검색어를 시도해보세요</p>
              </div>
            ) : null}
          </div>
        </div>
      </main>
    </div>
  );
} 