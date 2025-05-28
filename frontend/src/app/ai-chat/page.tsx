'use client'

import React, { useEffect, useRef, useState } from 'react'
import { useRouter } from 'next/navigation'
import type { ChatMessageDTO } from '../../types/ChatMessageDTO'
import { Stethoscope, Send, Menu, User, Bot } from 'lucide-react'
import styles from './page.module.css'
import Sidebar from '@/components/Sidebar'
import { useCurrentUser } from '@/data/useCurrentUser'
import apiConfig from '@/config/api'

export default function AIDoctorChatPage() {
  const router = useRouter()
  const { user, loading } = useCurrentUser()
  const [isSidebarOpen, setIsSidebarOpen] = useState(false)
  const [activeNav, setActiveNav] = useState("ai-chat")

  // 로그아웃 처리: 토큰 제거 후 /login으로 이동
  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    router.push('/login')
  }

  // 마운트 시 로그인 상태 체크
  useEffect(() => {
    if (!localStorage.getItem('accessToken')) {
      router.push('/login')
    }
  }, [router])

  const [messages, setMessages] = useState<ChatMessageDTO[]>([
    {
      role: "ai",
      content: "안녕하세요! 👋여러분의 도우미 Shaun 입니다. 상담을 시작해보세요!",
      timestamp: new Date().toISOString(),
    },
  ])
  const [input, setInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // 새 메시지가 추가될 때마다 스크롤 맨 아래로
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [messages])

  const toggleSidebar = () => {
    setIsSidebarOpen(prev => !prev)
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!input.trim()) return
    
    const token = localStorage.getItem('accessToken')
    const userMessage: ChatMessageDTO = {
      role: "user",
      content: input,
      timestamp: new Date().toISOString(),
    }
    const newMessages = [...messages, userMessage]
    setMessages(newMessages)
    setInput("")
    setIsLoading(true)

     try {
      // 1) AI 호출
      const res = await fetch("http://localhost:8080/api/chat-messages/ask?templateKey=…", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,        // ← 여기에 넣어주세요
        },
        body: JSON.stringify(newMessages),
      })
      if (!res.ok) throw new Error(`AI 호출 실패: ${await res.text()}`)

      const aiDto: ChatMessageDTO = await res.json()
      const aiMessage: ChatMessageDTO = {
        role: aiDto.role,
        content: aiDto.content,
        timestamp: aiDto.timestamp,
      }
      setMessages([...newMessages, aiMessage])

      // 대화 기록 백엔드에 저장
      await fetch(apiConfig.endpoints.chat.base + "/bulk", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,        // ← 여기에 넣어주세요
        },
        body: JSON.stringify([userMessage, aiMessage]),
      })
    } catch (err) {
      console.error(err)
      // 오류 메시지 표시
      const errorMessage: ChatMessageDTO = {
        role: "ai",
        content: "죄송합니다. 일시적인 오류가 발생했습니다. 다시 시도해 주세요.",
        timestamp: new Date().toISOString(),
      }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setIsLoading(false)
    }
  }

  const handleNavClick = (navId: string) => {
    setActiveNav(navId)
  }

  return (
    <div className={styles.page}>
      {/* 사이드바 */}
      <Sidebar
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
        activeNav={activeNav}
        onNavClick={handleNavClick}
      />

      {/* 헤더 */}
      <header className={styles.header}>
        <button className={styles.iconButton} onClick={toggleSidebar}>
          <Menu size={24} />
        </button>
        <h1 className={styles.logo}>
          <Stethoscope className={styles.logoIcon} /> AI Counseling
        </h1>
        <button className={styles.logoutButton} onClick={handleLogout}>
          로그아웃
        </button>
      </header>

      {/* 메인 콘텐츠 */}
      <main className={styles.main}>
        {/* 환영 배너 */}
        <div className={styles.welcomeBanner}>
          <p className={styles.welcomeText}>
            <span className={styles.wave}>👋</span> 환영합니다!
          </p>
          <h2 className={styles.welcomeTitle}>
            {loading
              ? <span>…로딩 중…</span>
              : <><span className={styles.highlight}>{user?.username ?? '사용자'}</span>님, 맞춤형 스킨케어를 시작해보세요!</>
            }
          </h2>
        </div>

        {/* 채팅 영역 */}
        <section className={styles.chatSection}>
          <div className={styles.chatHeader}>
            <h3>AI 상담</h3>
            <p>건강 상태를 분석하여 맞춤형 케어를 제안합니다</p>
          </div>

          <div className={styles.messagesWrapper}>
            {messages.map((msg, i) => (
              <div
                key={i}
                className={`${styles.message} ${msg.role === "user" ? styles.user : styles.ai}`}
              >
                <div className={styles.avatar}>
                  {msg.role === "user" ? <User /> : <Bot />}
                </div>
                <div className={styles.bubble}>
                  <p>{msg.content}</p>
                  <span className={styles.time}>
                    {new Date(msg.timestamp).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </span>
                </div>
              </div>
            ))}

            {isLoading && (
              <div className={`${styles.message} ${styles.ai}`}>
                <div className={styles.avatar}><Bot /></div>
                <div className={styles.bubble}>
                  <span className={styles.loading}>AI가 응답을 생성하고 있습니다...</span>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          <form onSubmit={handleSubmit} className={styles.inputForm}>
            <input
              type="text"
              placeholder="궁금하신 피부 관련 질문을 입력해주세요..."
              value={input}
              onChange={e => setInput(e.target.value)}
              disabled={isLoading}
              className={styles.input}
            />
            <button type="submit" disabled={!input.trim() || isLoading} className={styles.sendButton}>
              <Send /> 전송
            </button>
          </form>
        </section>
      </main>
    </div>
  )
}
