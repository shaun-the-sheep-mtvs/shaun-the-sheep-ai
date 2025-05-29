package org.mtvs.backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private static final String MBTI_SYSTEM_PROMPT = """
    당신은 피부 평가 전문가입니다.\s
    대화를 통해 사용자의 피부 상태를 완전히 이해하기 위해,\s
    1) 피부 타입(건성, 지성, 수분부족지성, 복합성, 민감성)을 분류할 수 있을 만큼 \s
       구체적인 진단 질문을 스스로 생성하여 한 번에 하나씩 순차적으로 물어보세요. \s
    2) 최소 5개 이상의 상호작용(질문+답변)을 통해 충분한 정보를 수집한 후, \s
    3) 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(\\\\n)로만** 구분된 **5줄**로 출력하세요. \s
    - 각 줄 앞뒤에 공백을 절대 추가하지 말고, 다른 구두점이나 글자를 붙이지 마세요.
            
       당신의 피부 타입: <건성|지성|수분부족지성|복합성|민감성>
       피부 고민: <건조함|번들거림|민감함|탄력 저하|홍조|톤 안정|색소침착|잔주름|모공 케어>
       타입 설명:
       케어 팁:
            
    예시(형식만):
            
    당신의 피부 타입: 수분부족지성 \s
    피부 고민: 탄력 저하 \s
    타입 설명: 유분은 충분하지만 수분·탄력 모두 부족해 피부가 당기고 처짐이 느껴져요. \s
    케어 팁: 고보습 세럼과 탄력 강화 오일을 함께 사용하세요.
            
    concern 값은 반드시 다음 중 하나입니다
    건조함
    번들거림
    민감함
    탄력 저하
    홍조
    톤 안정
    색소침착
    잔주름
    모공 케어

        “당신의 피부 타입은…,\s
         고민은…, \s
         해당 타입에 대한 설명과 케어 팁은 …”
        식으로 자연스럽고 친근하게 한 문단으로 풀어주세요.
""" ;
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
            String userId,
            List<ChatMessage> history,
            String userQuestion,
            String templateKey
    ) {
        // 1) 생성 설정
        Map<String,Object> generationConfig = Map.of(
                "temperature",      0.2,
                "topK",             40,
                "topP",             0.95,
                "maxOutputTokens",  1000           // 필요에 따라 늘리거나 줄여보세요
        );

        // 2) contents 리스트 만들기
        List<Map<String,Object>> contents = new ArrayList<>();

        // (1) system 프롬프트
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", MBTI_SYSTEM_PROMPT))
        ));

        // (2) 대화 히스토리
        for (ChatMessage msg : history) {
            String role = msg.getRole().equals("user")
                    ? "user"
                    : "model";                  // ai → model
            contents.add(Map.of(
                    "role",  role,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }

        // (3) 마지막 질문
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", userQuestion))
        ));

        // 3) request body
        Map<String,Object> body = Map.of(
                "contents",         contents,
                "generationConfig", generationConfig
        );

        // 4) API 호출 & 에러 로깅
        Map<String,Object> res = webClient.post()
                .uri(b -> b
                        .path("/v1beta/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build()
                )
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(errBody -> {
                                    System.err.println("=== 400 응답 바디 ===");
                                    System.err.println(errBody);
                                    return Mono.error(new RuntimeException("API 요청 에러: " + errBody));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();

        // 5) 결과 파싱
        List<Map<String,Object>> candidates = (List<Map<String,Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }
        Map<String,Object> contentMap = (Map<String,Object>) candidates.get(0).get("content");
        List<Map<String,Object>> parts     = (List<Map<String,Object>>) contentMap.get("parts");
        String aiText = parts.get(0).get("text").toString().trim();

        // 6) DB에 온전히 저장 (자르지 않음)
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("ai");
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(aiMsg);
    }
}