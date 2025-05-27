// src/app/layout.tsx
import type { Metadata } from "next";
import { GeistSans, GeistMono } from 'geist/font';
import "./globals.css";
import AuthGuard from '@/components/AuthGuard';
import Nav from '@/components/Nav';
import Script from 'next/script';

const geistSans = GeistSans;
const geistMono = GeistMono;

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
        </head>
        <body>
            {children}
        </body>
        </html>
    );
}

