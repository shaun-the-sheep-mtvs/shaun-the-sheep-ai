import { ReactNode } from 'react';

export const metadata = {
  title: '피부유형 검사',
};

// app/layout.tsx — 루트 레이아웃
export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <head />
      <body>
        {/* 전역 네비게이션, 글로벌 스타일 등 */}
        {children}
      </body>
    </html>
  );
}

