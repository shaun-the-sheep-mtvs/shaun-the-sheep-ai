"use client";
import React from "react";

export default function LoginLayout({
                                      children,
                                    }: {
  children: React.ReactNode;
}) {
  return <>{children}</>;  // <-- html/head는 루트에서만!
}


