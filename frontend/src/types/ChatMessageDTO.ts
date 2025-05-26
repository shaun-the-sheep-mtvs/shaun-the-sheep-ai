export interface ChatMessageDTO {
  role: 'user' | 'ai'
  content: string
  timestamp: string  // ISO 문자열, e.g. "2025-05-27T08:45:00.000Z"
}