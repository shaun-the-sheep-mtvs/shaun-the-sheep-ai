import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export async function POST(request: NextRequest) {
  const { messages } = await request.json()
  const res = await fetch(
    `${process.env.BACKEND_URL || 'http://localhost:8080'}/api/chat-messages/ask`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(messages),
    }
  )
  const aiDto = await res.json()
  return NextResponse.json(aiDto, { status: res.status })
}
