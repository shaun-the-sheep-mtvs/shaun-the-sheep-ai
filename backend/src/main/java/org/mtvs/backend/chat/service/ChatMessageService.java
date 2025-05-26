package org.mtvs.backend.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private static final Map<String,String> TEMPLATE_PROMPTS = Map.of(
            "skin_dry",
            "당신은 피부 보습 전문가입니다. ‘피부가 건조해요’라는 질문이 들어오면, " +
                    "다음 조건을 반드시 지켜 답변하세요:\n" +
                    "1) 공백 포함 **240자 이내**로 문장을 끝낼것\n" +
                    "2) 한 문단(한 줄)으로 작성\n" +
                    "3) “troubles”는 [건조함, 번들거림, 민감함, …, 결 거칠음] 중 하나만 언급\n\n" +
                    "– 문제 원인 요약\n" +
                    "– 추천 제품 및 사용법\n" +
                    "– 추가 팁",
            "skin_acne",
            "당신은 여드름 치료 전문가입니다. ‘여드름 고민’ 질문이 들어오면, " +
                    "다음 조건을 반드시 지켜 답변하세요:\n" +
                    "1) 공백 포함 **240자 이내**로 문장을 끝낼것\n" +
                    "2) 한 문단(한 줄)으로 작성\n" +
                    "3) “troubles”는 [건조함, 번들거림, 민감함, …, 결 거칠음] 중 하나만 언급\n\n" +
                    "- 원인\n" +
                    "- 식습관/생활습관 조언\n" +
                    "- 추천 성분"
    );
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String apiKey;

    public List<ChatMessage> findAll() {
        return chatMessageRepository.findAll();
    }

    public Optional<ChatMessage> findById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public ChatMessage save(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public void deleteById(Long id) {
        chatMessageRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    public ChatMessage askAI_Single(
            List<ChatMessage> history,
            String userQuestion,
            String templateKey
    ) {
        // 1) 생성 설정
        Map<String,Object> generationConfig = Map.of(
                "temperature", 0.2,
                "topK", 40,
                "topP", 0.95,
                "maxOutputTokens", 255,
                "stopSequences", List.of("\n\n")
        );

        // 2) contents 리스트 만들기 (템플릿이 있으면 맨 앞에 system 역할로 삽입)
        List<Map<String,Object>> contents = new ArrayList<>();
        if (templateKey != null && TEMPLATE_PROMPTS.containsKey(templateKey)) {
            String prompt = TEMPLATE_PROMPTS.get(templateKey);
            contents.add(Map.of(
                    "role", "system",
                    "parts", List.of(Map.of("text", prompt))
            ));
        }
        // user 메시지
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userQuestion))
        ));

        // 3) request body
        Map<String,Object> body = Map.of(
                "contents", contents,
                "generationConfig", generationConfig
        );

        // 4) API 호출
        Map<String,Object> res = webClient.post()
                .uri(b -> b
                        .path("/v1beta/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build()
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();

        // 5) 로깅
        try {
            System.out.println(">>> AI raw response:\n"
                    + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(res));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 6) 결과 파싱
        List<Map<String,Object>> candidates = (List<Map<String,Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }
        Map<String,Object> contentMap = (Map<String,Object>) candidates.get(0).get("content");
        if (contentMap == null) {
            throw new RuntimeException("AI content가 없습니다.");
        }
        List<Map<String,Object>> parts = (List<Map<String,Object>>) contentMap.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("AI parts가 없습니다.");
        }
        // 6) parse
        String aiText = (String) parts.get(0).get("text");
        if (aiText == null) {
            throw new RuntimeException("AI text가 없습니다.");
        }

        int maxLen = 250;
        if (aiText.length() > maxLen) {
            aiText = aiText.substring(0, maxLen);
        }

        // 7) DB에 저장
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setRole("ai");
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(aiMsg);
    }
}