// src/app/layout.tsx
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Script from "next/script";
import Link from "next/link";

const inter = Inter({
    subsets: ["latin"],
    variable: "--font-inter",
});

export const metadata: Metadata = {
    title: "Shaun the Sheep MTVS",
    description: "Shaun the Sheep MTVS is an ai aided web application for the skin care and beauty industry.",
};

export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode;
}) {
    return (
        <html
            lang="en"
            className={inter.variable}
        >
        <head>
            {/* 로그인 폼 스크립트 로드 */}
            <Script src="/scripts/login.js" strategy="beforeInteractive" />
        </head>
        <body>
        {children}
        </body>
        </html>
    );
}

