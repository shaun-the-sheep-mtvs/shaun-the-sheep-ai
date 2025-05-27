"use client";
import React, { useState } from "react";
import { ChatMessageDTO } from "../../types/ChatMessageDTO";
import { apiConfig } from "../../config/api";

export default function AIChatPage() {
  const [messages, setMessages] = useState<ChatMessageDTO[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    // 1. Add user message to local state
    const userMessage: ChatMessageDTO = {
      role: "user",
      content: input,
      timestamp: new Date().toISOString(),
    };
    const newMessages = [...messages, userMessage];
    setMessages(newMessages);
    setInput("");
    setIsLoading(true);

    // 2. Get AI response
    const res = await fetch("/api/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ messages: [...newMessages] }),
    });
    const data = await res.json();

    // 3. Add AI message to local state
    const aiMessage: ChatMessageDTO = {
      role: "ai",
      content: data.text,
      timestamp: new Date().toISOString(),
    };
    const updatedMessages = [...newMessages, aiMessage];
    setMessages(updatedMessages);
    setIsLoading(false);

    // 4. Save both user and AI messages to backend
    await fetch(apiConfig.baseURL + "/api/chat-messages/bulk", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify([userMessage, aiMessage]),
    });
  };

  return (
    <main style={{ maxWidth: 600, margin: "2rem auto", fontFamily: "sans-serif" }}>
      <h1>AI Chat</h1>
      <form onSubmit={handleSubmit} style={{ marginBottom: 24 }}>
        <input
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="Ask something..."
          style={{ width: "100%", marginBottom: 8, padding: 8 }}
          disabled={isLoading}
        />
        <button type="submit" disabled={isLoading || !input.trim()} style={{ padding: "8px 16px" }}>
          Send
        </button>
      </form>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {messages.map((m, i) => (
          <li key={i} style={{ marginBottom: 12 }}>
            <b>{m.role === "user" ? "You" : "AI"}:</b> {m.content}
          </li>
        ))}
      </ul>
    </main>
  );
}