import { generateText } from 'ai';
import { openai } from '@ai-sdk/openai';
import { groq } from '@ai-sdk/groq';
import { xai } from '@ai-sdk/xai';
import { anthropic } from '@ai-sdk/anthropic';

export const runtime = 'edge';

export async function POST(req: Request) {
  const { messages } = await req.json();
  // Combine all messages into a single prompt (simple approach)
  const prompt = messages.map((m: any) => `${m.role === 'user' ? 'User' : 'AI'}: ${m.content}`).join('\n') + '\nAI:';

  const { text } = await generateText({
    model: openai.responses('gpt-4o-mini'),
    // model: groq('llama3-70b-8192'),
    // model: xai('grok-3'),
    // model: anthropic('claude-3-5-sonnet-20240620'),
    prompt,
  });

  return new Response(JSON.stringify({ text }), {
    headers: { 'Content-Type': 'application/json' },
  });
}