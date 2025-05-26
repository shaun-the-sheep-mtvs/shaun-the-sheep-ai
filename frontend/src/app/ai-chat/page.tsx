'use client'
import React, { useState } from 'react'
import type { ChatMessageDTO } from '../../types/ChatMessageDTO'
import { Stethoscope, Send, Menu, User, Bot } from 'lucide-react'
import styles from './page.module.css'

export default function AIDoctorChatPage() {
  const [messages, setMessages] = useState<ChatMessageDTO[]>([
    {
      role: 'ai',
      content: '안녕하세요! 👋 AI 의사입니다. 건강 상담을 시작해보세요!',
      timestamp: new Date().toISOString(),
    },
  ])
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!input.trim()) return

    const userMessage: ChatMessageDTO = {
      role: 'user',
      content: input,
      timestamp: new Date().toISOString(),
    }
    const newMessages = [...messages, userMessage]
    setMessages(newMessages)
    setInput('')
    setIsLoading(true)

    try {
      // AI 호출 (Next.js API 혹은 직접 백엔드)
      const res = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ messages: newMessages }),
      })
      if (!res.ok) throw new Error(await res.text())
      const aiDto: ChatMessageDTO = await res.json()

      const aiMessage: ChatMessageDTO = {
        role: aiDto.role,
        content: aiDto.content,
        timestamp: aiDto.timestamp,
      }
      setMessages([...newMessages, aiMessage])

      // 백엔드 저장
      await fetch('http://localhost:8080/api/chat-messages/bulk', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify([userMessage, aiMessage]),
      })
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className={styles.page}>
      {/* Header */}
      <header className={styles.header}>
        <button className={styles.iconButton}>
          <Menu size={24} />
        </button>
        <h1 className={styles.logo}>
          <Stethoscope className={styles.logoIcon} /> AI Doctor
        </h1>
        <button className={styles.iconButton}>
          <User size={24} />
        </button>
      </header>

      <main className={styles.main}>
        {/* 환영 배너 */}
        <div className={styles.welcomeBanner}>
          <p className={styles.welcomeText}>
            <span className={styles.wave}>👋</span> 환영합니다!
          </p>
          <h2 className={styles.welcomeTitle}>
            <span className={styles.highlight}>건강상담</span> 님, 맞춤형 스킨케어를 시작해보세요!
          </h2>
        </div>

        {/* Chat 박스 */}
        <section className={styles.chatSection}>
          <div className={styles.chatHeader}>
            <h3>AI 의사 상담</h3>
            <p>건강 상태를 분석하여 맞춤형 케어를 제안합니다</p>
          </div>

          <div className={styles.messagesWrapper}>
            {messages.map((msg, i) => (
              <div
                key={i}
                className={
                  msg.role === 'user'
                    ? `${styles.message} ${styles.user}`
                    : `${styles.message} ${styles.ai}`
                }
              >
                <div className={styles.avatar}>
                  {msg.role === 'user' ? <User /> : <Bot />}
                </div>
                <div className={styles.bubble}>
                  <p>{msg.content}</p>
                  <span className={styles.time}>
                    {new Date(msg.timestamp).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </span>
                </div>
              </div>
            ))}
            {isLoading && (
              <div className={`${styles.message} ${styles.ai}`}>
                <div className={styles.avatar}>
                  <Bot />
                </div>
                <div className={styles.bubble}>
                  <span className={styles.loading}>...</span>
                </div>
              </div>
            )}
          </div>

          <form onSubmit={handleSubmit} className={styles.inputForm}>
            <input
              type="text"
              placeholder="건강 관련 질문을 입력해주세요..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={isLoading}
              className={styles.input}
            />
            <button
              type="submit"
              disabled={!input.trim() || isLoading}
              className={styles.sendButton}
            >
              <Send /> 전송
            </button>
          </form>
        </section>

        {/* 팁 & 현황 */}
        <div className={styles.cards}>
          <div className={styles.card}>
            <div className={styles.cardIcon}>💡</div>
            <div>
              <h4>건강 팁</h4>
              <p>규칙적인 운동과 충분한 수면이 건강의 기본입니다.</p>
            </div>
          </div>
          <div className={styles.card}>
            <div className={styles.cardIcon}>📊</div>
            <div>
              <h4>상담 현황</h4>
              <p>오늘 {messages.length}개의 메시지를 주고받았습니다.</p>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
