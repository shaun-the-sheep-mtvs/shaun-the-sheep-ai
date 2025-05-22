// src/app/layout.tsx
import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import AuthGuard from '@/components/AuthGuard';
import Nav from '@/components/Nav';

const geistSans = Geist({ variable: "--font-geist-sans", subsets: ["latin"] });
const geistMono = Geist_Mono({ variable: "--font-geist-mono", subsets: ["latin"] });

export const metadata: Metadata = {
    title: "Shaun the Sheep MTVS",
    description: "Shaun the Sheep MTVS is an ai aided web application for the skin care and beauty industry.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${geistSans.variable} ${geistMono.variable}`}>
      <head />
      <body>
        {/* 1) Nav는 AuthGuard 바깥에 둡니다 */}
        <Nav />

        {/* 2) AuthGuard는 페이지 콘텐츠만 감싸서 보호 */}
        <AuthGuard>
          {children}
        </AuthGuard>
      </body>
    </html>
  );
}

