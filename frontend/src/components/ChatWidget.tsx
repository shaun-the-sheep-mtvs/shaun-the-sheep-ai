'use client'

import React, { useEffect, useRef, useState } from 'react'
import type { ChatMessageDTO } from '@/types/ChatMessageDTO'
import { Send, User, Bot, X, PackageSearch, Droplet, AlertCircle, Smile } from 'lucide-react'
import apiConfig from '@/config/api'
import styles from './ChatWidget.module.css'

export default function ChatWidget() {
  const [open, setOpen] = useState(false)
  const [templateKey, setTemplateKey] = useState<string | null>(null)
  const [messages, setMessages] = useState<ChatMessageDTO[]>([
    {
      role: 'ai',
      content: '안녕하세요! 👋 상담을 시작해보세요.',
      timestamp: new Date().toISOString(),
    },
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // 채팅창 열릴 때 & 로딩 끝나면 입력창 포커스
  useEffect(() => {
    if (open && !loading) {
      inputRef.current?.focus()
    }
  }, [open, loading])

  // 새 메시지 오면 스크롤 다운
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  // 채팅창 토글
  const toggleOpen = () => {
    setOpen(o => !o)
  }

  // 퀵 액션 버튼 클릭 시 초기 안내 메시지 세팅
  const handleQuick = (key: string) => {
    setTemplateKey(key)
    let promptText = ''
    switch (key) {
      case 'PRODUCT_INQUIRY':
        promptText = '제품 문의를 선택하셨습니다. 사용하시는 제품이 기억나지 않으시면, 외관·질감·향기 등을 설명해 주세요.'
        break
      case 'INGREDIENT_INQUIRY':
        promptText = '성분 문의를 선택하셨습니다. 궁금하신 성분을 입력해 주세요 (예: 히알루론산, 비타민C…).'
        break
      case 'SKIN_TROUBLE':
        promptText = '트러블 상담을 선택하셨습니다. 현재 겪고 계신 트러블을 말씀해 주세요 (예: 건조함, 홍조…).'
        break
      case 'SKIN_TYPE':
        promptText = '피부 타입 상담을 선택하셨습니다. 피부 고민에 대해 말씀해주시면 피부 타입을 진단해 드리겠습니다.'
        break
      default:
        promptText = '안녕하세요! 상담을 시작해보세요.'
    }
    setMessages([
      {
        role: 'ai',
        content: promptText,
        timestamp: new Date().toISOString(),
      },
    ])
    setInput('')
    setTimeout(() => {
      inputRef.current?.focus()
    }, 0)
  }

  // 메시지 전송
  const handleSubmit = async () => {
    // 1) 토큰 없으면 로그인 안내
    const token = localStorage.getItem('accessToken')
    if (!token) {
      setMessages(prev => [
        ...prev,
        {
          role: 'ai',
          content: '이 기능은 로그인 후에 이용 가능합니다.',
          timestamp: new Date().toISOString(),
        },
      ])
      return
    }
    // 2) 입력 체크 + 로딩 중인지
    if (!input.trim() || loading) return

    // 3) 사용자 메시지 추가
    const userMsg: ChatMessageDTO = {
      role: 'user',
      content: input,
      timestamp: new Date().toISOString(),
    }
    setMessages(prev => [...prev, userMsg])
    setInput('')
    setLoading(true)

    try {
      // 백엔드 /ask 호출
      const url = new URL(apiConfig.endpoints.chat.base + '/ask')
      if (templateKey) {
        url.searchParams.set('templateKey', templateKey)
      }
      const res = await fetch(url.toString(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify([...messages, userMsg]),
      })
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`)
      }
      const aiDto = (await res.json()) as ChatMessageDTO
      setMessages(prev => [...prev, aiDto])
    } catch (err) {
      console.error(err)
      setMessages(prev => [
        ...prev,
        {
          role: 'ai',
          content: '서버가 이상이 생겼습니다. 잠시 후 다시 시도해주세요.',
          timestamp: new Date().toISOString(),
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.wrapper}>
      {open && (
        <div className={styles.window}>
          {/* 헤더 */}
          <div className={styles.header}>
            <div className={styles.headerTitle}>
              <Smile size={20} className={styles.headerIcon} />
              <span>뷰티 상담</span>
            </div>
            <button className={styles.closeBtn} onClick={toggleOpen}>
              <X size={18} />
            </button>
          </div>

          {/* 빠른 버튼 */}
          <div className={styles.quickActions}>
            <button onClick={() => handleQuick('PRODUCT_INQUIRY')}>
              <PackageSearch size={16} className={styles.quickIcon} />
              제품 문의
            </button>
            <button onClick={() => handleQuick('INGREDIENT_INQUIRY')}>
              <Droplet size={16} className={styles.quickIcon} />
              성분 문의
            </button>
            <button onClick={() => handleQuick('SKIN_TROUBLE')}>
              <AlertCircle size={16} className={styles.quickIcon} />
              트러블 상담
            </button>
            <button onClick={() => handleQuick('SKIN_TYPE')}>
              <Smile size={20} className={styles.headerIcon} />
              피부 타입
            </button>
          </div>

          {/* 대화 내역 */}
          <div className={styles.history}>
            {messages.map((m, i) => (
              <div
                key={i}
                className={m.role === 'user' ? styles.userMsg : styles.aiMsg}
              >
                <div className={styles.avatar}>
                  {m.role === 'user' ? <User /> : <Bot />}
                </div>
                <div className={styles.messageBubble}>
                  <p>{m.content}</p>
                  <span className={styles.time}>
                    {new Date(m.timestamp).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </span>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          {/* 입력 폼 */}
          <form
            className={styles.inputForm}
            onSubmit={e => {
              e.preventDefault()
              handleSubmit()
            }}
          >
            <input
              ref={inputRef}
              type="text"
              value={input}
              onChange={e => setInput(e.target.value)}
              disabled={loading}
              placeholder="질문을 입력하세요..."
              className={styles.input}
            />
            <button
              type="submit"
              disabled={!input.trim() || loading}
              className={styles.sendBtn}
            >
              <Send />
            </button>
          </form>
        </div>
      )}

      {/* 채팅열기/닫기 버블 */}
      <button
        className={styles.bubble}
        onClick={toggleOpen}
        aria-label="채팅 열기/닫기"
      >
        💬
      </button>
    </div>
  )
}
