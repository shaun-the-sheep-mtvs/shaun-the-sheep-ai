'use client';

import { useEffect, useState, useRef } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { apiConfig } from '@/config/api';

export default function KakaoCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const hasProcessed = useRef(false); // 중복 실행 방지

  useEffect(() => {
    const handleKakaoCallback = async () => {
      // 이미 처리했다면 중복 실행 방지
      if (hasProcessed.current) {
        return;
      }
      hasProcessed.current = true;

      try {
        // URL에서 code 파라미터 추출
        const code = searchParams.get('code');
        const errorParam = searchParams.get('error');

        console.log('카카오 콜백 처리 시작 - code:', code ? '존재함' : '없음');

        if (errorParam) {
          throw new Error('카카오 로그인이 취소되었습니다.');
        }

        if (!code) {
          throw new Error('인증 코드를 받지 못했습니다.');
        }

        // 백엔드 카카오 로그인 API 호출
        console.log('백엔드 API 호출 시작');
        const response = await fetch(`${apiConfig.endpoints.auth.kakaoLogin}?code=${code}`, {
          method: 'GET',
          credentials: 'include', // 쿠키 포함
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('백엔드 응답 오류:', response.status, errorText);
          throw new Error(`카카오 로그인 처리 중 오류가 발생했습니다. (${response.status})`);
        }

        const userData = await response.json();
        console.log('카카오 로그인 성공:', userData);

        // 로그인 성공 후 메인 페이지로 이동
        router.push('/');
        
      } catch (error) {
        console.error('카카오 로그인 오류:', error);
        setError(error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.');
        setLoading(false);
      }
    };

    handleKakaoCallback();
  }, [searchParams, router]); // 의존성 배열 최소화

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        flexDirection: 'column'
      }}>
        <h2>카카오 로그인 처리 중...</h2>
        <p>잠시만 기다려주세요.</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        flexDirection: 'column'
      }}>
        <h2>로그인 오류</h2>
        <p>{error}</p>
        <button onClick={() => router.push('/login')}>
          로그인 페이지로 돌아가기
        </button>
      </div>
    );
  }

  return null;
} 