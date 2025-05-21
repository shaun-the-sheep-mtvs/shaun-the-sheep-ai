import Link from 'next/link';
import React from 'react';

export default function ChecklistLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <nav style={{ padding: '1rem', background: '#f5f5f5' }}>
        <Link href="/">홈</Link> | <Link href="/checklist">검사하기</Link>
      </nav>
      <main>{children}</main>
    </>
  );
}

