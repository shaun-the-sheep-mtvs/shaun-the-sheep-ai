import { ReactNode } from 'react';

export const metadata = {
  title: '피부유형 검사',
};

export default function ChecklistLayout({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <html lang="ko">
      <body>
        {/* 여기에는 nav를 아예 빼고, children(=page.tsx)만 렌더링 */}
        {children}
      </body>
    </html>
  );
}
