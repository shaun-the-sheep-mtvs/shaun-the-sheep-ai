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
        - 추천 예시는 최대 3가지로 제시하고, 각 예시마다 제품명·주요 성분·질감·기대 효과를 간략히 알려주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
    """ ;

    private static final String INGREDIENT_INQUIRY_PROMPT = """
        당신은 화장품 성분 전문가입니다.
        - 특정 성분(히알루론산/비타민C/레티놀 등)의 효능, 사용법, 주의사항을 자세히 설명해 주세요.
        - 민감 피부 등 주의가 필요한 경우 별도 경고도 함께 제공해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
    """ ;

    private static final String SKIN_TYPE_PROMPT = """
        당신은 피부 타입 진단 전문가입니다.
        - 대화를 통해 사용자의 수분·유분·민감도·탄력을 평가하고,
          피부 타입은 건성, 지성, 복합성, 민감성, 수분부족지성 중 하나를 제시해 주세요.
        - 타입별 의미와 특징을 설명해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
    """ ;

    private static final String SKIN_TROUBLE_PROMPT = """
        당신은 피부 트러블 상담 전문가입니다.
        - 사용자가 어떤 트러블에 대해 질문하면 정의와 특징을 설명해 주세요.
          (지원하는 트러블 키워드 예: 건조함, 번들거림, 민감함, 탄력 저하, 홍조, 톤 안정, 색소침착, 잔주름, 모공 케어, 다크써클, 결 거칠음 등)
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
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
        // ----------------------------
        // 0) templateKey에 따라 사용할 시스템 프롬프트를 선택
        // ----------------------------
        String systemPrompt;
        if ("PRODUCT_INQUIRY".equals(templateKey)) {
            systemPrompt = PRODUCT_INQUIRY_PROMPT;
        }
        else if ("INGREDIENT_INQUIRY".equals(templateKey)) {
            systemPrompt = INGREDIENT_INQUIRY_PROMPT;
        }
        else if ("SKIN_TYPE".equals(templateKey)) {
            systemPrompt = SKIN_TYPE_PROMPT;
        }
        else if ("SKIN_TROUBLE".equals(templateKey)) {
            systemPrompt = SKIN_TROUBLE_PROMPT;
        }
        else {
            // 아무 키값도 안 들어오면 MBTI_SYSTEM_PROMPT를 기본으로 사용
            systemPrompt = MBTI_SYSTEM_PROMPT;
        }

        // ----------------------------
        // 1) 생성 설정
        // ----------------------------
        Map<String,Object> generationConfig = Map.of(
                "temperature",      0.2,
                "topK",             40,
                "topP",             0.95,
                "maxOutputTokens",  1000
        );

        // ----------------------------
        // 2) contents 리스트 만들기
        // ----------------------------
        List<Map<String,Object>> contents = new ArrayList<>();

        // (1) system 프롬프트
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));

        // (2) 대화 히스토리
        for (ChatMessage msg : history) {
            String role = msg.getRole().equals("user")
                    ? "user"
                    : "model";  // ai → model
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

        // ----------------------------
        // 3) request body 구성
        // ----------------------------
        Map<String,Object> body = Map.of(
                "contents",         contents,
                "generationConfig", generationConfig
        );

        // ----------------------------
        // 4) API 호출 & 에러 처리
        // ----------------------------
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

        // ----------------------------
        // 5) 결과 파싱
        // ----------------------------
        List<Map<String,Object>> candidates = (List<Map<String,Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }
        Map<String,Object> contentMap = (Map<String,Object>) candidates.get(0).get("content");
        List<Map<String,Object>> parts     = (List<Map<String,Object>>) contentMap.get("parts");
        String aiText = parts.get(0).get("text").toString().trim();

        // ----------------------------
        // 6) DB에 저장하지 않고 ChatMessage 객체만 만들어서 반환
        //    (컨트롤러에서 “5줄 요약”인지 확인 후 save)
        // ----------------------------
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("ai");
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());
        return aiMsg;
    }
}