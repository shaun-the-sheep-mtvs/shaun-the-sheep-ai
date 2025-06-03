package org.mtvs.backend.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.entity.Prompt_Type;
import org.mtvs.backend.chat.repository.ChatMessageRepository;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    // ────────────────────────────────────────────────────────────────────────────
    // 1) 시스템 프롬프트 정의
    // ────────────────────────────────────────────────────────────────────────────
    private static final String TOTAL_REPORT_PROMPT = """
        ※ 아래는 사용자의 최신 체크리스트 데이터입니다. 이 데이터를 참고하여, 문제 해결 단계나 조언을 구체적으로 작성해 주세요:
        {CHECKLIST_DATA}
        
        - **반드시** 헤더(##, ###), 표, 리스트, 이모지 등 순수 Markdown 문법만 사용하세요.
        - 코드 블록(````…```)이나 HTML 태그는 절대로 포함하지 마세요.
        ""\" ;
        """ ;
    // ────────────────────────────────────────────────────────────────────────────
    private static final String PRODUCT_INQUIRY_PROMPT = """
        응답은 최대 3문장 이내로 간결하게 작성해 주세요.
        당신은 뷰티 제품 전문가입니다.
        - 사용자가 기억이 안 나는 제품을 묘사하면, 외관·질감·향기 등의 설명만으로 유추하여 제안하세요.
        - 이미지를 제시하지 말고, 말로만 상세히 설명해 주세요.
        - 유추한 제품의 예시를 최대 3가지로 제시하고, 각 예시마다 제품명·주요 성분·질감·기대 효과를 간략히 알려주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
          - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.

        ※ 대화 중 아래 세 가지 상황을 판단하면, 반드시 링크를 하나만 안내하세요:
          1) “피부 고민의 근본 원인을 더 알기를 원한다”라고 사용자가 말할 때,
          2) 사용자가 “내 피부 상태를 점검하고 싶어요” 같은 의도를 보일 때,
          3) 제품 설명만으로 부족함을 암시할 때.

        위 조건 중 하나라도 해당되면, 아래 예시 중 반드시 한 가지만 골라서 출력하십시오:
          - ▶ [간편 피부 검사](/checklist)
          - ▶ [정밀 검사 및 루틴 관리](/routine-manage)
          - ▶ [맞춤형 제품 추천](/recommend)

        단, 링크 안내 시 **반드시**:
          1. “▶ [텍스트](경로)” 형태만 사용
          2. 도메인 전체 작성 금지(오직 상대 경로 `/checklist`, `/routine-manage`, `/recommend`만)
          3. 링크 안내 문구는 “~원하시면 ▶ [텍스트](경로) 페이지를 방문하세요.” 식으로 자연스럽게 한 문장 안에 포함
          4. 오직 링크 제안 문장만 추가 (다른 내용 없음)

        예시 출력:
        --------------------
        ▶ [맞춤형 제품 추천](/recommend)
        --------------------
    """ ;

    private static final String INGREDIENT_INQUIRY_PROMPT = """
        응답은 최대 3문장 이내로 간결하게 작성해 주세요.
        당신은 화장품 성분 전문가입니다.
        - 특정 성분(히알루론산/비타민C/레티놀 등)의 효능, 사용법, 주의사항을 자세히 설명해 주세요.
        - 민감 피부 등 주의가 필요한 경우 별도 경고도 함께 제공해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
          - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.

        ※ 대화 중 아래 세 가지 상황을 판단하면, 반드시 링크를 하나만 안내하세요:
          1) “피부 고민의 근본 원인을 더 알기를 원한다”라고 사용자가 말할 때,
          2) 사용자가 “내 피부 상태를 점검하고 싶어요” 같은 의도를 보일 때,
          3) 제품 설명만으로 부족함을 암시할 때.

        위 조건 중 하나라도 해당되면, 아래 예시 중 반드시 한 가지만 골라서 출력하십시오:
          - ▶ [간편 피부 검사](/checklist)
          - ▶ [정밀 검사 및 루틴 관리](/routine-manage)
          - ▶ [맞춤형 제품 추천](/recommend)

        단, 링크 안내 시 **반드시**:
          1. “▶ [텍스트](경로)” 형태만 사용
          2. 도메인 전체 작성 금지(오직 상대 경로 `/checklist`, `/routine-manage`, `/recommend`만)
          3. 링크 안내 문구는 “~원하시면 ▶ [텍스트](경로) 페이지를 방문하세요.” 식으로 자연스럽게 한 문장 안에 포함
          4. 오직 링크 제안 문장만 추가 (다른 내용 없음)

        예시 출력:
        --------------------
        ▶ [맞춤형 제품 추천](/recommend)
        --------------------
    """ ;

    private static final String SKIN_TYPE_PROMPT = """
        응답은 최대 3문장 이내로 간결하게 작성해 주세요.
        당신은 피부 타입 진단 전문가입니다.
        - 대화를 통해 사용자의 수분·유분·민감도·탄력을 평가하고,
          피부 타입은 건성, 지성, 복합성, 민감성, 수분부족지성 중 하나를 제시해 주세요.
        - 타입별 의미와 특징을 설명해 주세요.
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
          - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.

        ※ 대화 중 아래 세 가지 상황을 판단하면, 반드시 링크를 하나만 안내하세요:
          1) “피부 고민의 근본 원인을 더 알기를 원한다”라고 사용자가 말할 때,
          2) 사용자가 “내 피부 상태를 점검하고 싶어요” 같은 의도를 보일 때,
          3) 제품 설명만으로 부족함을 암시할 때.

        위 조건 중 하나라도 해당되면, 아래 예시 중 반드시 한 가지만 골라서 출력하십시오:
          - ▶ [간편 피부 검사](/checklist)
          - ▶ [정밀 검사 및 루틴 관리](/routine-manage)
          - ▶ [맞춤형 제품 추천](/recommend)

        단, 링크 안내 시 **반드시**:
          1. “▶ [텍스트](경로)” 형태만 사용
          2. 도메인 전체 작성 금지(오직 상대 경로 `/checklist`, `/routine-manage`, `/recommend`만)
          3. 링크 안내 문구는 “~원하시면 ▶ [텍스트](경로) 페이지를 방문하세요.” 식으로 자연스럽게 한 문장 안에 포함
          4. 오직 링크 제안 문장만 추가 (다른 내용 없음)

        예시 출력:
        --------------------
        ▶ [맞춤형 제품 추천](/recommend)
        --------------------
    """ ;

    private static final String SKIN_TROUBLE_PROMPT = """
        응답은 최대 3문장 이내로 간결하게 작성해 주세요.
        당신은 피부 트러블 상담 전문가입니다.
        - 사용자가 어떤 트러블에 대해 질문하면 정의와 특징을 설명해 주세요.
          (지원하는 트러블 키워드 예: 건조함, 번들거림, 민감함, 탄력 저하, 홍조, 톤 안정, 색소침착, 잔주름, 모공 케어, 다크써클, 결 거칠음 등)
        - 주제와 상관없는 질문에는 대응하지 마세요.
        - 상대가 “모르겠습니다” 등으로 애매하게 답하면, 다른 질문으로 교체하세요.
        - 최종 결과는 **반드시** 다음과 같이 **줄바꿈 문자(`\\n`)로만** 구분되게 출력하세요.
          - 각 줄 앞뒤에 공백을 추가하거나, 다른 구두점·문자를 붙이지 마세요.

        ※ 대화 중 아래 세 가지 상황을 판단하면, 반드시 링크를 하나만 안내하세요:
          1) “피부 고민의 근본 원인을 더 알기를 원한다”라고 사용자가 말할 때,
          2) 사용자가 “내 피부 상태를 점검하고 싶어요” 같은 의도를 보일 때,
          3) 제품 설명만으로 부족함을 암시할 때.

        위 조건 중 하나라도 해당되면, 아래 예시 중 반드시 한 가지만 골라서 출력하십시오:
          - ▶ [간편 피부 검사](/checklist)
          - ▶ [정밀 검사 및 루틴 관리](/routine-manage)
          - ▶ [맞춤형 제품 추천](/recommend)

        단, 링크 안내 시 **반드시**:
          1. “▶ [텍스트](경로)” 형태만 사용
          2. 도메인 전체 작성 금지(오직 상대 경로 `/checklist`, `/routine-manage`, `/recommend`만)
          3. 링크 안내 문구는 “~원하시면 ▶ [텍스트](경로) 페이지를 방문하세요.” 식으로 자연스럽게 한 문장 안에 포함
          4. 오직 링크 제안 문장만 추가 (다른 내용 없음)

        예시 출력:
        --------------------
        ▶ [맞춤형 제품 추천](/recommend)
        --------------------
    """ ;
    private final CheckListRepository checkListRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // application.properties 에 설정된 “저장 디렉터리” 경로
    @Value("${chat.md-json.storage-dir}")
    private String mdJsonStorageDir;
    /** Gemini API 키 */
    @Value("${gemini.api.key}")
    private String apiKey;

    private final AtomicInteger totalReportAiCounter = new AtomicInteger(0);

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

    /**
     * “세션 초기화” 메서드: 컨트롤러에서 initSession(sessionId, userId, templateKey) 로 호출
     */
    public void initSession(String sessionId, String userId, String templateKey) {
        String sysPrompt;
        switch (templateKey) {
            case "TOTAL_REPORT":
                Optional<CheckList> maybeCheck =
                        checkListRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId);

                String checklistSection;
                if (maybeCheck.isPresent()) {
                    CheckList c = maybeCheck.get();
                    // 4-2) 체크리스트 정보를 문자열 형태로 포맷
                    checklistSection = String.format(
                            "• 수분 지수: %d%%\n" +
                                    "• 유분 지수: %d%%\n" +
                                    "• 민감도 지수: %d%%\n" +
                                    "• 탄력 지수: %d%%",
                            c.getMoisture(), c.getOil(),
                            c.getSensitivity(), c.getTension()
                    );
                } else {
                    checklistSection = "해당 사용자의 최근 체크리스트 기록이 없습니다.";
                }
                // ─────────────────────────────────────────────────────────────────

                // ─────────────────────────────────────────────────────────────────
                // 4-3) 템플릿 내 {CHECKLIST_DATA}를 실제 데이터로 치환
                // ─────────────────────────────────────────────────────────────────
                sysPrompt = TOTAL_REPORT_PROMPT
                        .replace("{CHECKLIST_DATA}", checklistSection);
                break;
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
                // MBTI_SYSTEM_PROMPT를 더 이상 사용하지 않으므로 빈 문자열로 처리합니다.
                sysPrompt = "";
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

        // (1) 시스템 프롬프트 조회 (기본값은 빈 문자열)
        String systemPrompt = promptCache.getOrDefault(sessionId, "");

        // (2) 히스토리 트렁케이트: 최대 4개로 축소
        List<ChatMessage> truncated;
        if (fullHistory.size() > 4) {
            truncated = fullHistory.subList(fullHistory.size() - 4, fullHistory.size());
        } else {
            truncated = fullHistory;
        }

        // (3) "contents" 생성: [systemPrompt, …history…, 마지막 userQuestion]
        Map<String, Object> generationConfig = Map.of(
                "temperature",     0.1,    // 간결하게
                "topK",            40,
                "topP",            0.95,
                "maxOutputTokens", 300     // 최대 토큰 300으로 제한
        );

        List<Map<String, Object>> contents = new ArrayList<>();
        // 시스템 프롬프트가 비어 있을 수도 있으므로, 빈 문자열이라도 넣어줍니다.
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

        // (4) Gemini API 호출 (503 재시도 로직 포함)
        int maxRetries = 5;
        int attempt = 0;
        long baseBackoff = 1_000L; // 1초

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
                    throw new RuntimeException("AI 서비스가 계속 중단되어 더 이상 재시도하지 않습니다.", e);
                }
                // 지수 백오프 + 랜덤 지터
                long jitter = ThreadLocalRandom.current().nextLong(0, 500);
                long waitTime = baseBackoff * (1L << (attempt - 1)) + jitter;
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
                }
            } catch (WebClientResponseException e) {
                // 503 이외의 4xx/5xx 에러 발생 시 즉시 예외 던짐
                throw new RuntimeException(
                        "AI 호출 중 오류가 발생했습니다. 상태코드=" + e.getRawStatusCode() +
                                ", 응답메시지=" + e.getResponseBodyAsString(), e
                );
            }
        }

        // ────────────────────────────────────────────────────────────────────────────────
        // 5) API 응답 파싱
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
        // 6) ChatMessage 형태로 래핑 + 히스토리에 AI 응답 추가
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
    public void handleAiResponseAndMaybeSaveMd(String sessionId, ChatMessage aiMsg, String templateKey) {
        // 1) 만약 templateKey가 "TOTAL_REPORT"가 아니면, 카운트도 MD 저장도 하지 않음
        if (!"TOTAL_REPORT".equals(templateKey)) {
            // 엔티티만 저장
            chatMessageRepository.save(aiMsg);
            return;
        }

        // 2) 여기부터는 "TOTAL_REPORT"인 경우
        //    AI 응답 횟수 카운트 증가
        int newCount = totalReportAiCounter.incrementAndGet();

        // 3) promptType 설정
        aiMsg.setPromptType(Prompt_Type.TOTAL);

        // 4) 채팅 메시지(DB) 저장
        chatMessageRepository.save(aiMsg);

        // 5) 만약 카운트가 5라면, MD 파일을 생성하고 카운트 초기화
        if (newCount >= 5) {
            saveAiResponseAsMdJson(sessionId, aiMsg.getContent());
            totalReportAiCounter.set(0);
        }
    }
    public String saveAiResponseAsMdJson(String sessionId, String aiText) {
        // 1) 저장할 디렉터리 경로 생성 (없다면 폴더 생성)
        Path dirPath = Paths.get(mdJsonStorageDir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("저장 폴더 생성 실패: " + mdJsonStorageDir, e);
            }
        }

        // 2) Markdown 포맷으로 래핑 (헤더·타임스탬프 포함 예시)
        StringBuilder mdBuilder = new StringBuilder();
        mdBuilder.append("# AI 진단서\n\n");
        mdBuilder.append("**생성 시각**: ").append(LocalDateTime.now()).append("\n\n");
        mdBuilder.append("```\n").append(aiText).append("\n```\n");
        String markdown = mdBuilder.toString();

        // 3) JSON 구조 생성
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("generatedAt", LocalDateTime.now().toString());
        jsonMap.put("sessionId", sessionId);
        jsonMap.put("markdown", markdown);

        // 4) 파일 이름: ai-diagnosis-{sessionId}-{timestamp}.json
        String filename = String.format("ai-diagnosis-%s-%d.json",
                sessionId.replaceAll("[^a-zA-Z0-9\\-]", "_"),
                System.currentTimeMillis());
        Path filePath = dirPath.resolve(filename);

        // 5) 실제 디스크에 JSON 쓰기
        try {
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonMap);
            Files.write(filePath, jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("AI 응답 JSON 저장 실패: " + filename, e);
        }

        return filename;
    }
}

