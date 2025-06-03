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
import java.time.format.DateTimeFormatter;
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
    [역할 부여]
    당신은 상세하고 친절한 뷰티 제품 전문가입니다. 피부 분석, 제품 추천 및 사용 루틴 컨설팅에 매우 능숙합니다.

    [역질문]
    • 위클리 관리를 위한 추가 피부 분석 진단 질문을 **총 5개**만 제시하세요.
    • “한 번에 하나씩 질문 → 사용자의 답변 → 그다음 질문” 과정을 반드시 지켜야 합니다.
    • 절대로 5개를 초과하지 마세요.
    • 각 질문은 최대 3줄 이내로 작성해주세요. (예: 세안법, 주 단위 피부 습관 등)

    [요청 사항]
    1) **제목 고정**
       최종 보고서의 제목은 반드시 “맞춤 위클리 루틴 추천”으로 고정합니다. 다른 곳에서는 절대 언급하지 마세요.

    2) **표(Table) 생성**
       - 표의 첫 번째 행(헤더)은 정확히 `| 요일   | 루틴                          |` 형태로 작성합니다.  
       - 두 번째 행(구분선)은 정확히 `| ------ | ----------------------------- |` 형태로 작성합니다.  
       - 그 아래부터 월요일부터 일요일까지, AI가 입력된 피부 상태·기존 루틴·개선된 루틴·추천 화장품 목록을 종합적으로 분석하여  
         각 요일별 “하루 루틴”을 구체적으로 작성합니다.  
       - ‘하루 루틴’ 예시(임의 예시가 아닌, AI가 판단하여 생성):
         ```
         | 월요일 | 클렌징  |
         | 화요일 | 토너 |
         | 수요일 | 수분 마스크 팩  |
         | 목요일 | 히알루론산 앰플  |
         | 금요일 | 진정 시트 마스크 팩 |
         | 토요일 | 스크럽/필링 제품 사용  |
         | 일요일 | 영양 크림  |
         ```
       - 위 예시는 AI가 생성할 형식을 보여주는 “형식 예시”일 뿐이며, 실제 출력에는 AI가 스스로 생성한 요일별 루틴만 나오도록 해야 합니다.  

    3) **표와 추가 지침 사이 구분선**  
       표가 끝난 뒤에는 반드시 세 개의 대시(`---`)를 한 줄로 넣어주세요.

    4) **추가 지침(💡 추가 지침) 섹션**  
       - 표 아래에는 다음과 같은 소제목 형태를 반드시 지켜야 합니다:
         ```
         ### 💡 추가 지침
         ```
       - 이 섹션에서는 AI가 “입력된 피부 상태·기존 루틴·개선된 루틴·추천 화장품 목록”을 종합 분석하여,  
         사용자의 피부를 최적화하기 위한 “추가 권장 사항”을 자유롭게 생성해야 합니다.  
         예시:  
         - “주 2회 수분 팩 사용: 피부가 건조해진 부위에 집중 보습을 돕습니다.”  
         - “저녁 루틴 시 레티놀 세럼 도입: 잔주름 예방과 피부 재생을 촉진합니다.”  
       - 위 두 가지 예시는 형식만 보여주는 가이드일 뿐이며, 실제 출력 시에는 AI가 입력 데이터를 바탕으로 새로운 조언을 작성해야 합니다.  
       - 각 조언 뒤에는 반드시 “간단한 이유”와 “기대 효과”를 함께 서술해야 합니다.

    ***레포트 작성 스타일***  
    - 전문적이고 신뢰감을 주면서도, 사용자가 쉽게 이해할 수 있도록 친절하고 상세하게 설명해주세요.  
    - 단순 정보 나열이 아닌, 각 요소 간의 연관성을 분석하고 그 이유를 명확히 밝혀주세요.  
    - 긍정적인 변화를 기대할 수 있도록 격려하는 어투를 사용해주세요.
    - 주제와 상관없는 질문에는 대응하지 마세요.
    """;
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

        // 0) 사용자 메시지를 히스토리에 추가
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setRole("user");
        userMsg.setContent(userQuestion);
        userMsg.setTimestamp(LocalDateTime.now());
        fullHistory.add(userMsg);

        // 1) 시스템 프롬프트(“총 5개만 질문” 등)를 가져온다 (role:"system"이 아님)
        String systemPrompt = promptCache.getOrDefault(sessionId, "");

        List<ChatMessage> truncated;
        if (fullHistory.size() > 10) {
            truncated = fullHistory.subList(fullHistory.size() - 10, fullHistory.size());
        } else {
            truncated = new ArrayList<>(fullHistory);
        }

        // 3) 메시지 배열 구성 (모두 role:"user" 또는 role:"assistant"만 사용)
        Map<String, Object> generationConfig = Map.of(
                "temperature",     0.1,
                "topK",            40,
                "topP",            0.95,
                "maxOutputTokens", 300
        );

        List<Map<String,Object>> contents = new ArrayList<>();

        // → 시스템 프롬프트도 user 역할로 포장한다
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));

        // → 과거 히스토리(질문/답변)도 user 또는 assistant로
        for (ChatMessage msg : truncated) {
            String role = msg.getRole().equals("user") ? "user" : "assistant";
            contents.add(Map.of(
                    "role",  role,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }

        // → 마지막으로 지금 받은 질문(userQuestion)을 한 번 더 추가
        contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", userQuestion))
        ));

        Map<String,Object> body = Map.of(
                "contents",         contents,
                "generationConfig", generationConfig
        );

        // 4) Gemini API 호출
        int maxRetries = 5, attempt = 0;
        long baseBackoff = 1_000L;
        Map<String,Object> res = null;

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
                        .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                        .block();
                break;
            } catch (WebClientResponseException.ServiceUnavailable e) {
                attempt++;
                if (attempt > maxRetries) {
                    throw new RuntimeException("AI 서비스가 중단되어 재시도 불가", e);
                }
                long jitter = ThreadLocalRandom.current().nextLong(0, 500);
                long waitTime = baseBackoff * (1L << (attempt - 1)) + jitter;
                try { Thread.sleep(waitTime); }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
                }
            } catch (WebClientResponseException e) {
                // 400~500 에러
                throw new RuntimeException(
                        "AI 호출 중 오류 발생. 상태코드=" + e.getRawStatusCode() +
                                ", 응답메시지=" + e.getResponseBodyAsString(), e
                );
            }
        }

        // 5) 응답 파싱
        if (res == null) {
            throw new RuntimeException("AI 응답이 비어 있습니다.");
        }
        List<Map<String,Object>> candidates = (List<Map<String,Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }
        Map<String,Object> contentMap = (Map<String,Object>) candidates.get(0).get("content");
        List<Map<String,Object>> parts = (List<Map<String,Object>>) contentMap.get("parts");
        String aiText = parts.get(0).get("text").toString().trim();

        // 6) DB에 저장 + 히스토리에 AI 응답 추가
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("ai");  // DB에는 ai로 저장
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());

        fullHistory.add(aiMsg);
        historyCache.put(sessionId, fullHistory);

        return aiMsg;
    }

    public void handleAiResponseAndMaybeSaveMd(String sessionId, ChatMessage aiMsg, String templateKey) {
        // AI 메시지는 항상 저장
        aiMsg.setPromptType(Prompt_Type.TOTAL);
        chatMessageRepository.save(aiMsg);

        // “맞춤 위클리 루틴 추천”이라는 문구가 AI 응답에 들어가 있으면 MD/JSON 파일 생성
        if (aiMsg.getContent() != null &&
                aiMsg.getContent().contains("맞춤 위클리 루틴 추천")) {
            saveAiResponseAsMdJson(sessionId, aiMsg.getContent());
        }
    }

    public String saveAiResponseAsMdJson(String userId, String aiText) {
        // 1) 저장할 디렉터리 생성
        Path dirPath = Paths.get(mdJsonStorageDir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("저장 폴더 생성 실패: " + mdJsonStorageDir, e);
            }
        }

        // 2) 파일명 생성: userId-생성날짜_타임스탬프.json
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = String.format("%s-%s-%d.json", userId, datePart, System.currentTimeMillis());
        Path filePath = dirPath.resolve(filename);

        // 3) JSON 구조에 Markdown 포함해서 쓰기
        try {
            Map<String, Object> jsonMap = new LinkedHashMap<>();
            jsonMap.put("generatedAt", now.toString());
            jsonMap.put("userId", userId);

            StringBuilder mdBuilder = new StringBuilder();
            mdBuilder.append("# AI 진단서\n\n");
            mdBuilder.append("**생성 시각**: ").append(now).append("\n\n");
            mdBuilder.append("```\n").append(aiText).append("\n```\n");
            jsonMap.put("markdown", mdBuilder.toString());

            String jsonString = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonMap);
            Files.write(filePath, jsonString.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new RuntimeException("AI 응답 JSON 저장 실패: " + filename, e);
        }

        return filename;
    }
}



