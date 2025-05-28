'use client';

import React, { useEffect, useState } from 'react';
import { apiConfig } from '@/config/api';
import styles from './page.module.css';
import { useRouter } from 'next/navigation';

interface ProductImage {
  image: string;
  title: string;
}

export default function TestWeekPage() {
  const router = useRouter();
  const [images, setImages] = useState<ProductImage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchNaverData = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        console.log('Current token:', token);
        
        if (!token) {
          console.log('No token found');
          setError('로그인이 필요합니다.');
          return;
        }

        console.log('Fetching Naver API...');
        const response = await fetch(`${apiConfig.baseURL}/api/naver`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const text = await response.text();
        console.log('Raw response:', text);
        
        try {
          const jsonArray = JSON.parse(text);
          const extractedImages: ProductImage[] = [];
          
          jsonArray.forEach((jsonStr: string) => {
            const item = JSON.parse(jsonStr);
            if (item.items && item.items[0]) {
              extractedImages.push({
                image: item.items[0].image,
                title: item.items[0].title.replace(/<[^>]*>/g, '') // HTML 태그 제거
              });
            }
          });
          
          console.log('Extracted images:', extractedImages);
          setImages(extractedImages);
        } catch (parseError) {
          console.error('JSON parse error:', parseError);
          setError('데이터 형식이 올바르지 않습니다.');
        }
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('데이터를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchNaverData();
  }, []);

  if (loading) {
    return (
      <div className={styles.container}>
        <div className={styles.loading}>로딩 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.container}>
        <div className={styles.error}>{error}</div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>네이버 API 테스트</h1>
      {images.length > 0 ? (
        <div className={styles.imageGrid}>
          {images.map((item, index) => (
            <div key={index} className={styles.imageCard}>
              <img src={item.image} alt={item.title} className={styles.productImage} />
              <div className={styles.productInfo}>
                <h3 className={styles.productTitle}>{item.title}</h3>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles.error}>상품 데이터가 없습니다.</div>
      )}
    </div>
  );
}
