"use client"
import { useRouter } from 'next/navigation'
import { Home, FileText, MessageCircle, UserCircle, X } from "lucide-react"
import styles from "./Sidebar.module.css"

interface SidebarProps {
  isOpen: boolean
  onClose: () => void
  activeNav: string
  onNavClick: (navId: string) => void
}

const navigationItems = [
  { id: 'home',        label: '홈화면',   path: '/',           icon: Home },
  { id: 'examination', label: '검사하기', path: '/checklist', icon: FileText },
  { id: 'ai-chat',     label: 'AI 채팅',  path: '/ai-chat',    icon: MessageCircle },
  { id: 'profile',     label: '회원정보', path: '/profile',    icon: UserCircle },
]

export default function Sidebar({ isOpen, onClose, activeNav, onNavClick }: SidebarProps) {
  const router = useRouter()
  const handleClick = (path: string, id: string) => {
    onNavClick(id)
    router.push(path)    // ← 여기가 핵심
    onClose()
  }

  return (
    <>
      <div className={`${styles.overlay} ${isOpen ? styles.open : ''}`} onClick={onClose} />
      <div className={`${styles.sidebar} ${isOpen ? styles.open : ''}`}>
        {/* ... header 생략 ... */}
        <nav className={styles.nav}>
          {navigationItems.map(item => {
            const Icon = item.icon
            const isActive = activeNav === item.id
            return (
              <button
                key={item.id}
                className={`${styles.item} ${isActive ? styles.active : ''}`}
                onClick={() => handleClick(item.path, item.id)}
              >
                <Icon className={styles.icon} />
                <span className={styles.label}>{item.label}</span>
              </button>
            )
          })}
        </nav>
      </div>
    </>
  )
}
