import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'
import type { ChatMessageDTO } from '../../../types/ChatMessageDTO'

export async function POST(request: NextRequest) {
  console.log('[API /api/chat] GOT HIT')
  console.log('GEMINI_API_URL:', process.env.GEMINI_API_URL)
  console.log('GEMINI_API_KEY set?:', !!process.env.GEMINI_API_KEY)
  // 1) 클라이언트로부터 메시지 히스토리 가져오기
  const { messages } = await request.json() as { messages: ChatMessageDTO[] }

  // 2) Gemini API 정보
  const url = process.env.GEMINI_API_URL!
  const apiKey = process.env.GEMINI_API_KEY!
  if (!apiKey) {
    return NextResponse.json({ error: 'GEMINI_API_KEY is missing' }, { status: 500 })
  }

  // 3) 메시지 히스토리 → 하나의 프롬프트로 변환
  //    (원하시는 방식으로 템플릿을 조정하세요)
  const prompt = messages
    .map(m => `${m.role === 'user' ? 'User' : 'Assistant'}: ${m.content}`)
    .join('\n')
    + '\nAssistant:'

  // 4) Gemini REST 호출
  const geminiRes = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify({
      prompt: {
        text: prompt
      },
      // 선택적 파라미터: maxOutputTokens, temperature 등
      temperature: 0.7,
      maxOutputTokens: 512,
    })
  })

  if (!geminiRes.ok) {
    const errText = await geminiRes.text()
    return NextResponse.json({ error: errText }, { status: geminiRes.status })
  }

  const geminiJson = await geminiRes.json()
  // 5) Gemini 응답 파싱하기
  //   모델 응답 구조는 버전에 따라 다르지만, 대략 이 위치에 텍스트가 들어 있습니다:
  const aiText = geminiJson.candidates?.[0]?.content?.text
  if (typeof aiText !== 'string') {
    return NextResponse.json({ error: 'Invalid Gemini response' }, { status: 500 })
  }

  // 6) 클라이언트로 돌려줄 DTO
  const aiDto: ChatMessageDTO = {
    role: 'ai',
    content: aiText.trim(),
    timestamp: new Date().toISOString(),
  }

  return NextResponse.json(aiDto, { status: 200 })
}

