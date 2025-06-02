package org.mtvs.backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private static final String MBTI_SYSTEM_PROMPT = """
        당신은 피부 평가 전문가입니다. \s
        대화를 통해 사용자의 피부 상태를 완전히 이해하기 위해 다음을 준수하세요:
                
        1. 피부 타입(건성, 지성, 수분부족지성, 복합성, 민감성)을 분류할 수 있을 만큼
           구체적인 진단 질문을 한 번에 하나씩, 최대 3줄 이내로 순차적으로 물어보세요.
        2. 최소 7회 이상의 질문·답변을 통해 충분한 정보를 수집하세요.
        3. 사용자가 질문 범위를 벗어나면 같은 질문을 다시 하세요.
        4. 피부와 관련되지 않은 질문이나 주제 변경 요청에는 응답하지 마세요.
        5. 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분된 **5줄**로 출력하세요.
           - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.
        6. 사용자가 “모르겠습니다” 등으로 애매하게 답하면, 즉시 다른 질문으로 교체하세요.
             
        마지막 단계에만, 다음과 같은 5줄 요약 형식으로만 답해주세요:
        1) 피부 타입: …
        2) 고민 키워드: …
        3) 설명: …
        4) 케어 팁: …
        5) 권장 루틴: …
        ———
                
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

            “당신의 피부 타입은…,
             고민은…,
             해당 타입에 대한 설명과 케어 팁은 …”
            식으로 자연스럽고 친근하게 한 문단으로 풀어주세요.
    """ ;

    private static final String PRODUCT_INQUIRY_PROMPT = """
        당신은 뷰티 제품 전문가입니다.
        - 사용자가 기억이 안 나는 제품을 묘사하면, 외관·질감·향기 등의 설명만으로 유추하여 제안하세요.
        - 이미지를 제시하지 말고, 말로만 상세히 설명해 주세요.
        - 유추한 제품의 예시를 최대 3가지로 제시하고, 각 예시마다 제품명·주요 성분·질감·기대 효과를 간략히 알려주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
                   - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.
    """ ;

    private static final String INGREDIENT_INQUIRY_PROMPT = """
        당신은 화장품 성분 전문가입니다.
        - 특정 성분(히알루론산/비타민C/레티놀 등)의 효능, 사용법, 주의사항을 자세히 설명해 주세요.
        - 민감 피부 등 주의가 필요한 경우 별도 경고도 함께 제공해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
                           - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.
    """ ;

    private static final String SKIN_TYPE_PROMPT = """
        당신은 피부 타입 진단 전문가입니다.
        - 대화를 통해 사용자의 수분·유분·민감도·탄력을 평가하고,
          피부 타입은 건성, 지성, 복합성, 민감성, 수분부족지성 중 하나를 제시해 주세요.
        - 타입별 의미와 특징을 설명해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
                           - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.
    """ ;

    private static final String SKIN_TROUBLE_PROMPT = """
        당신은 피부 트러블 상담 전문가입니다.
        - 사용자가 어떤 트러블에 대해 질문하면 정의와 특징을 설명해 주세요.
          (지원하는 트러블 키워드 예: 건조함, 번들거림, 민감함, 탄력 저하, 홍조, 톤 안정, 색소침착, 잔주름, 모공 케어, 다크써클, 결 거칠음 등)
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
                           - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.
    """ ;

    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public List<ChatMessage> findAll() { return chatMessageRepository.findAll(); }
    public Optional<ChatMessage> findById(Long id) { return chatMessageRepository.findById(id); }
    public ChatMessage save(ChatMessage message) { return chatMessageRepository.save(message); }
    public void deleteById(Long id) { chatMessageRepository.deleteById(id); }

    /**
     * “세션 초기화” 메서드: 컨트롤러에서 initSession(sessionId, userId, templateKey) 로 호출
     */
    public void initSession(String sessionId, String userId, String templateKey) {
        String sysPrompt;
        switch (templateKey) {
            case "PRODUCT_INQUIRY":
                sysPrompt = PRODUCT_INQUIRY_PROMPT;
                break;
            case "INGREDIENT_INQUIRY":
                sysPrompt = INGREDIENT_INQUIRY_PROMPT;
                break;
            case "SKIN_TYPE":
                sysPrompt = SKIN_TYPE_PROMPT;
                break;
            case "SKIN_TROUBLE":
                sysPrompt = SKIN_TROUBLE_PROMPT;
                break;
            default:
                sysPrompt = MBTI_SYSTEM_PROMPT;
        }
        promptCache.put(sessionId, sysPrompt);
        historyCache.put(sessionId, new ArrayList<>());
    }

    // in‐memory 캐시: sessionId → 지금까지 대화 히스토리
    private final Map<String, List<ChatMessage>> historyCache = new ConcurrentHashMap<>();

    // in‐memory 캐시: sessionId → 시스템 프롬프트
    private final Map<String, String> promptCache = new ConcurrentHashMap<>();

    /**
     * 실제 AI 호출 메서드: sessionId, userId, userQuestion을 받아 historyCache에서 꺼내 온 뒤 AI 요청
     */
    @SuppressWarnings("unchecked")
    public ChatMessage askAI_Single(String sessionId, String userId, String userQuestion) {
        List<ChatMessage> fullHistory = historyCache.computeIfAbsent(sessionId, k -> new ArrayList<>());


        // (0-1) 히스토리에 사용자 메시지 추가
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setRole("user");
        userMsg.setContent(userQuestion);
        userMsg.setTimestamp(LocalDateTime.now());
        fullHistory.add(userMsg);

        // (1) 시스템 프롬프트 조회
        String systemPrompt = promptCache.getOrDefault(sessionId, MBTI_SYSTEM_PROMPT);

        List<ChatMessage> truncated;
        if (fullHistory.size() > 6) {
            truncated = fullHistory.subList(fullHistory.size() - 6, fullHistory.size());
        } else {
            truncated = fullHistory;
        }

        // (2) "contents” 생성: [systemPrompt, …history…, 마지막 userQuestion]
        Map<String, Object> generationConfig = Map.of(
                "temperature",     0.2,
                "topK",            40,
                "topP",            0.95,
                "maxOutputTokens", 1000
        );
        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));
        for (ChatMessage msg : truncated) {
            String role = "user".equals(msg.getRole()) ? "user" : "model";
            contents.add(Map.of(
                    "role",  role,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }
        // 최종 userQuestion을 한 번 더 명시적으로 추가
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", userQuestion))
        ));
        Map<String, Object> body = Map.of(
                "contents",         contents,
                "generationConfig", generationConfig
        );

        int maxRetries = 3;
        int attempt = 0;
        long backoffMillis = 1_000L; // 첫 재시도 대기 1초

        Map<String, Object> res = null;
        while (true) {
            try {
                res = webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1beta/models/gemini-2.0-flash:generateContent")
                                .queryParam("key", apiKey)
                                .build()
                        )
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                break; // 성공적으로 응답을 받았다면 반복문 종료

            } catch (WebClientResponseException.ServiceUnavailable e) {
                attempt++;
                if (attempt > maxRetries) {
                    throw new RuntimeException("AI 서비스가 계속 중단 중입니다. 잠시 후 다시 시도해주세요.", e);
                }
                // 503이 발생하면 잠시 대기 후 재시도
                try {
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
                }
                backoffMillis *= 2; // 1초 → 2초 → 4초 …
            } catch (WebClientResponseException e) {
                // 503 이외의 4xx/5xx 에러
                throw new RuntimeException(
                        "AI 호출 중 오류가 발생했습니다. 상태코드=" + e.getRawStatusCode() +
                                ", 응답메시지=" + e.getResponseBodyAsString(), e
                );
            }
        }

        // ────────────────────────────────────────────────────────────────────────────────
        // 4) API 응답 파싱
        // ────────────────────────────────────────────────────────────────────────────────
        if (res == null) {
            throw new RuntimeException("AI 응답이 비어 있습니다.");
        }
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }
        Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
        String aiText = parts.get(0).get("text").toString().trim();

        // ────────────────────────────────────────────────────────────────────────────────
        // 5) ChatMessage 형태로 래핑 + 히스토리에 AI 응답 추가
        // ────────────────────────────────────────────────────────────────────────────────
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("ai");
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());

        fullHistory.add(aiMsg);
        historyCache.put(sessionId, fullHistory);

        return aiMsg;
    }
}