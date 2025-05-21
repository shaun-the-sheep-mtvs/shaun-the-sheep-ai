"use client";
import React, { useEffect, useState } from "react";
import { Post } from "../types/Post";

const API_URL = "http://localhost:8080/api/posts";

export default function Home() {
  return (
    <main style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', fontFamily: 'sans-serif' }}>
      <h1>Hello, world!</h1>
    </main>
  );
}
