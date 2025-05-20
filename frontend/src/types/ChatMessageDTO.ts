export interface ChatMessageDTO {
    id?: number;
    role: string; // "user" or "ai"
    content: string;
    timestamp?: string; // ISO string
  }