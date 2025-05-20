"use client";
import React, { useState } from "react";

export default function AIChatPage() {
  const [messages, setMessages] = useState<{ role: string; content: string }[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;
    const newMessages = [...messages, { role: "user", content: input }];
    setMessages(newMessages);
    setInput("");
    setIsLoading(true);

    const res = await fetch("/api/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ messages: newMessages }),
    });
    const data = await res.json();
    setMessages([...newMessages, { role: "ai", content: data.text }]);
    setIsLoading(false);
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