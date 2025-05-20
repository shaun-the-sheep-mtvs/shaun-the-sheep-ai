import { generateText } from 'ai';
// import { openai } from '@ai-sdk/openai';
import { groq } from '@ai-sdk/groq';

export const runtime = 'edge';

export async function POST(req: Request) {
  const { messages } = await req.json();
  // Combine all messages into a single prompt (simple approach)
  const prompt = messages.map((m: any) => `${m.role === 'user' ? 'User' : 'AI'}: ${m.content}`).join('\n') + '\nAI:';

  const { text } = await generateText({
    // model: openai.responses('gpt-4o'),
    model: groq('llama3-70b-8192'),
    prompt,
  });

  return new Response(JSON.stringify({ text }), {
    headers: { 'Content-Type': 'application/json' },
  });
}