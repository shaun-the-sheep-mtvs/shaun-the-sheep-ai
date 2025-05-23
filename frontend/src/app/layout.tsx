// src/app/layout.tsx
import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Script from "next/script";
import Link from "next/link";

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});
const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
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
            className={`${geistSans.variable} ${geistMono.variable}`}
        >
        <head>
            <link rel="icon" href="/favicon.ico" />
            {/* 로그인 폼 스크립트 로드 */}
            <Script src="/scripts/login.js" strategy="beforeInteractive" />
        </head>
        <body>
        <nav style={{ padding: "1rem", textAlign: "right" }}>
            <Link href="/login">Login</Link> | <Link href="/register">Sign Up</Link>
        </nav>
        {children}
        </body>
        </html>
    );
}

