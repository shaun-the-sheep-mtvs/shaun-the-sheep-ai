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
    당신은 피부 평가 전문가입니다.  
    대화를 통해 사용자의 피부 상태를 완전히 이해하기 위해,  
    1) 피부 타입(건성, 지성, 수분부족지성, 복합성, 민감성)을 분류할 수 있을 만큼 \s
       구체적인 진단 질문을 스스로 생성하여 한 번에 하나씩 순차적으로 물어보세요. \s
    2) 최소 5개 이상의 상호작용(질문+답변)을 통해 충분한 정보를 수집한 후, \s
    3) **절대로 아래 JSON 매핑을 출력하지 말고 내부 참조용으로만 사용**하세요. \s
   4) 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(\\\\n)로만** 구분된 **5줄**로 출력하세요. \s
   - 각 줄 앞뒤에 공백을 절대 추가하지 말고, 다른 구두점이나 글자를 붙이지 마세요.
            
       당신의 피부 타입: <건성|지성|수분부족지성|복합성|민감성>
       피부 고민: <건조함|번들거림|민감함|탄력 저하|홍조|톤 안정|색소침착|잔주름|모공 케어>
       추천 MBTI 코드: <MOST|MOSL|…|DBIL|default>
       타입 설명: <해당 코드에 매핑된 description
       케어 팁: <해당 코드에 매핑된 advice>
            
    예시(형식만):
            
    당신의 피부 타입: 수분부족지성 \s
    피부 고민: 탄력 저하 \s
    추천 MBTI 코드: IIOE \s
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

        {
          "MOST": {
            "type": "민감성",
            "description": "수분과 유분이 충분하며 피부 탄력이 좋아 건강한 상태입니다.",
            "advice": "기본 보습·자외선 차단 루틴을 유지하고, 콜라겐·펩타이드 성분으로 탄력을 계속 관리하세요."
          },
          "MOSL": {
            "type": "민감성",
            "description": "수분과 유분은 충분하지만 탄력이 떨어지고 자극에 민감할 수 있어요.",
            "advice": "저자극 탄력 강화 세럼을 사용하고, 충분한 수분과 영양 공급을 해주세요."
          },
          "MBST": {
            "type": "민감성",
            "description": "수분은 풍부하나 유분이 부족해 건조함이 느껴질 수 있으며, 탄력은 좋은 편입니다.",
            "advice": "부드러운 오일 제품으로 유수분 균형을 맞추고, 탄력 증진 크림을 병행하세요."
          },
          "MBSL": {
            "type": "민감성",
            "description": "수분은 충분하지만 유분과 탄력 모두 부족해 피부가 당기고 민감해질 수 있어요.",
            "advice": "고보습·장벽 강화 오일·세럼으로 영양을 채우고, 탄력 케어를 병행하세요."
          },
          "MOIT": {
            "type": "지성",
            "description": "수분·유분·탄력 모두 좋은 균형 상태로, 안정적인 편입니다.",
            "advice": "기본 보습·자외선 차단 루틴을 유지하며, 탄력 유지 제품을 가볍게 사용하세요."
          },
          "MOIL": {
            "type": "지성",
            "description": "수분과 유분은 충분하나 탄력만 떨어져 피부 처짐이 느껴질 수 있어요.",
            "advice": "탄력 강화 세럼과 마사지로 리프팅 관리하세요."
          },
          "MBIT": {
            "type": "복합성",
            "description": "수분은 충분하나 유분이 부족해 건조함이 느껴지며, 탄력은 좋은 상태입니다.",
            "advice": "보습 오일·크림으로 유수분 밸런스를 맞추고, 탄력 유지를 위해 펩타이드 제품을 사용하세요."
          },
          "MBIL": {
            "type": "복합성",
            "description": "수분은 충분하지만 유분·탄력 모두 부족해 당김과 처짐이 동시에 나타날 수 있어요.",
            "advice": "고보습·탄력 강화 오일 세럼을 집중적으로 사용하세요."
          },
          "DOST": {
            "type": "수분부족지성",
            "description": "유분과 탄력은 좋으나 수분이 부족해 민감 반응이 나타날 수 있어요.",
            "advice": "저자극 수분 세럼과 마스크로 수분을 보충하고, 탄력 유지 루틴을 병행하세요."
          },
          "DOSL": {
            "type": "수분부족지성",
            "description": "수분·탄력 모두 부족해 처짐이 느껴지며 자극에도 민감해요.",
            "advice": "고보습·탄력 강화 크림과 진정 세럼을 동시에 사용하세요."
          },
          "DBST": {
            "type": "건성",
            "description": "수분·유분 부족으로 건조함이 심하나 탄력은 유지되고 있어요.",
            "advice": "고보습 크림과 오일로 영양을 채우고, 탄력 유지 제품을 함께 사용하세요."
          },
          "DBSL": {
            "type": "건성",
            "description": "수분·유분·탄력이 모두 부족해 피부가 거칠고 처짐이 심해요.",
            "advice": "장벽 강화·고보습·탄력 케어 제품을 함께 사용하여 집중 관리하세요."
          },
          "DOIT": {
            "type": "수분부족지성",
            "description": "유분과 탄력은 좋으나 수분이 부족해 당김이 느껴져요.",
            "advice": "수분 에센스와 마스크로 즉각적인 수분을 보충하세요."
          },
          "DOIL": {
            "type": "수분부족지성",
            "description": "유분은 충분하나 수분·탄력 모두 부족해 피부가 당기고 처짐이 느껴져요.",
            "advice": "고보습 세럼과 탄력 강화 오일을 함께 사용하세요."
          },
          "DBIT": {
            "type": "건성",
            "description": "수분·유분 부족으로 건조함이 있지만 탄력은 유지되고 있어요.",
            "advice": "보습 크림과 수분 세럼으로 피부 결을 개선하세요."
          },
          "DBIL": {
            "type": "건성",
            "description": "수분·유분·탄력이 모두 부족해 피부가 건조하고 처짐이 심해요.",
            "advice": "고보습·탄력 강화 루틴을 집중적으로 적용하세요."
          },
          "default": {
            "type": "표준형",
            "description": "피부 균형이 잘 잡힌 상태입니다.",
            "advice": "기본 보습과 탄력 관리 루틴을 꾸준히 지켜주세요."
          }
        }
        “당신의 피부 타입은…,\s
         고민은…, \s
         당신의 피부 MBTI는…, \s
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