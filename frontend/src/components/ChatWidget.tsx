// components/ChatWidget.tsx
'use client'

import React, { useEffect, useRef, useState } from 'react'
import { v4 as uuidv4 } from 'uuid'         // npm install uuid
import type { ChatMessageDTO } from '@/types/ChatMessageDTO'
import {
  Send,
  User,
  Bot,
  X,
  PackageSearch,
  Droplet,
  AlertCircle,
  Smile,
} from 'lucide-react'
import { usePathname } from 'next/navigation'
import apiConfig from '@/config/api'
import styles from './ChatWidget.module.css'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'

export default function ChatWidget() {
  // ────────────────────────────────────────────────────────────────────────────
  // 1) 로컬 상태 정의
  // ────────────────────────────────────────────────────────────────────────────
  const [open, setOpen] = useState(false)
  const [templateKey, setTemplateKey] = useState<string | null>(null)
  const [sessionId, setSessionId] = useState<string>('')
  const [messages, setMessages] = useState<ChatMessageDTO[]>([
    {
      role: 'ai',
      content: '안녕하세요! 👋 상담을 시작해보세요.',
      timestamp: new Date().toISOString(),
    },
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)

  // ────────────────────────────────────────────────────────────────────────────
  // 1-1) “클라이언트 마운트 여부” 플래그
  // ────────────────────────────────────────────────────────────────────────────
  const [mounted, setMounted] = useState(false)

  const inputRef = useRef<HTMLInputElement>(null)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // 현재 경로를 구하기 위해 usePathname() 사용 (Next.js App Router 용)
  const pathname = usePathname()

  // ────────────────────────────────────────────────────────────────────────────
  // 2) “버블 클릭을 비활성화”할 조건 결정
  //    - 토큰이 없거나, 현재 경로가 /login 또는 /signup 일 때
  //    - mounted 되기 전에는 항상 disabled
  // ────────────────────────────────────────────────────────────────────────────
  let token: string | null = null
  if (mounted) {
    token = localStorage.getItem('accessToken')
  }
  const isAuthPage = pathname === '/login' || pathname === '/signup'
  const isDisabled = !mounted || !token || isAuthPage

  // ────────────────────────────────────────────────────────────────────────────
  // 3) 마운트 시 sessionId 생성 + mounted=true
  // ────────────────────────────────────────────────────────────────────────────
  useEffect(() => {
    setMounted(true)              // ← 여기서 마운트 완료를 표시합니다.
    const newSession = uuidv4()
    setSessionId(newSession)
  }, [])

  // ────────────────────────────────────────────────────────────────────────────
  // 4) open/로딩 상태가 바뀔 때 입력창 포커스
  // ────────────────────────────────────────────────────────────────────────────
  useEffect(() => {
    if (open && !loading) {
      inputRef.current?.focus()
    }
  }, [open, loading])

  // ────────────────────────────────────────────────────────────────────────────
  // 5) messages 변경 시 스크롤 아래로
  // ────────────────────────────────────────────────────────────────────────────
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  // ────────────────────────────────────────────────────────────────────────────
  // 6) 채팅창 열고 닫기 (toggle)
  //    비활성화 상태라면 아무 동작 안 함
  // ────────────────────────────────────────────────────────────────────────────
  const toggleOpen = () => {
    if (isDisabled) return
    setOpen(o => !o)
  }

  // ────────────────────────────────────────────────────────────────────────────
  // 7) 퀵 액션 버튼 클릭
  // ────────────────────────────────────────────────────────────────────────────
  const handleQuick = (key: string) => {
    if (isDisabled) return

    setTemplateKey(key)

    // 1) 백엔드에 init-session 호출
    const token = localStorage.getItem('accessToken')
    fetch(
      `${apiConfig.endpoints.chat.base}/init-session?sessionId=${sessionId}` +
        (key ? `&templateKey=${key}` : ''),
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      }
    ).catch(err => {
      console.error('init-session 에러:', err)
    })

    // 2) 화면 메시지 초기화 + 첫 안내 텍스트 세팅
    let promptText = ''
    switch (key) {
      case 'CUSTOMER_SUPPORT':
        promptText =
          '지금까지의 정보를 바탕으로 진단서를 작성해드릴게요.'
        break
      case 'PRODUCT_INQUIRY':
        promptText =
          '제품 문의를 선택하셨습니다. 사용하시는 제품이 기억나지 않으시면, 외관·질감·향기 등을 설명해 주세요.'
        break
      case 'INGREDIENT_INQUIRY':
        promptText =
          '성분 문의를 선택하셨습니다. 궁금하신 성분을 입력해 주세요 (예: 히알루론산, 비타민C…).'
        break
      case 'SKIN_TROUBLE':
        promptText =
          '트러블 상담을 선택하셨습니다. 현재 겪고 계신 트러블을 말씀해 주세요 (예: 건조함, 홍조…).'
        break
      case 'SKIN_TYPE':
        promptText =
          '피부 타입 상담을 선택하셨습니다. 피부 고민에 대해 말씀해주시면 피부 타입을 진단해 드리겠습니다.'
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

  // ────────────────────────────────────────────────────────────────────────────
  // 8) 질문 전송
  // ────────────────────────────────────────────────────────────────────────────
  const handleSubmit = async () => {
    if (isDisabled) return
    if (!input.trim() || loading) return

    const userMsg: ChatMessageDTO = {
      role: 'user',
      content: input,
      timestamp: new Date().toISOString(),
    }

    setMessages(prev => [...prev, userMsg])
    setInput('')
    setLoading(true)

    try {
      const url = new URL(apiConfig.endpoints.chat.base + '/ask')
      url.searchParams.set('sessionId', sessionId)
      if (templateKey) {
        url.searchParams.set('templateKey', templateKey)
      }

      const token = localStorage.getItem('accessToken')
      const res = await fetch(url.toString(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ userMessage: userMsg.content }),
      })
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`)
      }
      const aiDto = (await res.json()) as ChatMessageDTO

      // CUSTOMER_SUPPORT 템플릿인 경우, AI 응답 대신 고정 메시지를 보여줌
      if (templateKey === 'CUSTOMER_SUPPORT') {
        setMessages(prev => [
          ...prev,
          {
            role: 'ai',
            content: '진단서가 제출되었습니다.',
            timestamp: new Date().toISOString(),
          },
        ])
      } else {
        setMessages(prev => [...prev, aiDto])
      }
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

  // ────────────────────────────────────────────────────────────────────────────
  // 9) JSX 반환
  // ────────────────────────────────────────────────────────────────────────────
  return (
    <div className={styles.wrapper}>
      {/* 채팅창 윈도우: open 상태이면서 비활성화가 아닐 때만 그려줌 */}
      {open && !isDisabled && (
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

          {/* 퀵 액션 버튼 */}
          <div className={styles.quickActions}>
            <button onClick={() => handleQuick('CUSTOMER_SUPPORT')}>
              종합 리포트
            </button>
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
                <div className={styles.messageBubble} style={{ whiteSpace: 'pre-line' }}>
                  <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {m.content}
                  </ReactMarkdown>
                  <span className={styles.time}>
                    {new Date(m.timestamp).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </span>
                </div>
              </div>
            ))}

            {/* 로딩 버블 */}
            {loading && (
              <div className={styles.aiMsg}>
                <div className={styles.avatar}>
                  <Bot />
                </div>
                <div className={styles.loadingBubble}>
                  <span className={styles.dot}></span>
                  <span className={styles.dot}></span>
                  <span className={styles.dot}></span>
                </div>
              </div>
            )}

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
              disabled={loading || !templateKey}
              placeholder={
                templateKey
                  ? '질문을 입력하세요...'
                  : '먼저 질문하고 싶은 영역 선택해주세요'
              }
              className={styles.input}
            />
            <button
              type="submit"
              disabled={!input.trim() || loading || !templateKey}
              className={styles.sendBtn}
            >
              <Send />
            </button>
          </form>
        </div>
      )}

      {/* 채팅 열기/닫기 버블 */}
      <button
        className={styles.bubble}
        onClick={toggleOpen}
        disabled={isDisabled}
        aria-label="채팅 열기/닫기"
        style={
          isDisabled
            ? { cursor: 'not-allowed', opacity: 0.5 }
            : {}
        }
      >
        💬
      </button>
    </div>
  )
}
