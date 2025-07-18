package org.mtvs.backend.deeprecommend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.deeprecommend.config.OpenConfig;
import org.mtvs.backend.deeprecommend.dto.RecommendResponseDTO;
import org.mtvs.backend.deeprecommend.dto.RequestDTO;
import org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO;
import org.mtvs.backend.deeprecommend.entity.DeepRecommend;
import org.mtvs.backend.deeprecommend.entity.RoutineChange;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.deeprecommend.repository.DeepRecommendRepository;
import org.mtvs.backend.deeprecommend.repository.RoutineChangeRepository;
import org.mtvs.backend.routine.dto.RoutineGroupDTO;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.repository.RoutineGroupRepository;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.userskin.service.UserskinService;
import org.mtvs.backend.userskin.entity.Userskin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DeepRecommendService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final OpenConfig openConfig;
    private final DeepRecommendRepository deepRecommendRepository;
    private final RoutineChangeRepository routineChangeRepository;
    private final RoutineGroupRepository routineGroupRepository;
    private final UserskinService userskinService;

    @Autowired
    public DeepRecommendService(RoutineRepository routineRepository, UserRepository userRepository, OpenConfig openConfig, DeepRecommendRepository deepRecommendRepository, RoutineChangeRepository routineChangeRepository, RoutineGroupRepository routineGroupRepository, UserskinService userskinService) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
        this.openConfig = openConfig;
        this.deepRecommendRepository = deepRecommendRepository;
        this.routineChangeRepository = routineChangeRepository;
        this.routineGroupRepository = routineGroupRepository;
        this.userskinService = userskinService;
    }

    public String askOpenAI(String userId) {

        // Get user skin data from Userskin entity
        Optional<Userskin> userskinOpt = userskinService.getActiveUserskinByUserId(userId);
        if (userskinOpt.isEmpty()) {
            throw new RuntimeException("User skin data not found for user: " + userId);
        }
        
        Userskin userskin = userskinOpt.get();
        ProblemDto getProblemByUsername = new ProblemDto(
            userskinService.getSkinTypeString(userskin),
            userskinService.getConcernLabels(userskin)
        );
        List<RoutinesDto> recommend = routineRepository.findRoutinesByUserId(userId);

        // Kinds Enum의 모든 값을 문자열로 나열
        StringBuilder kindsList = new StringBuilder();
        for (Kinds kind : Kinds.values()) {
            kindsList.append("\"").append(kind.getNameTypeJson()).append("\",");
        }
        // 마지막 쉼표 제거
        if (kindsList.length() > 0) {
            kindsList.setLength(kindsList.length() - 1);
        }
        String validKinds = kindsList.toString();


        try {
            ObjectMapper mapper = new ObjectMapper();
            // Ensure RoutinesDto includes routineId for the prompt
            String json1 = mapper.writeValueAsString(recommend);
            String json2 = mapper.writeValueAsString(getProblemByUsername);

            // 프롬프트 수정: JSON 객체 형태로 두 가지 섹션을 구분하도록 지시
            String prompt = String.format(
                    "너는 피부관리 전문가야. 아래는 사용자의 스킨케어 루틴과 피부 고민 정보야:\n\n" +
                            "루틴(JSON): %s\n" +
                            "피부 고민: %s\n" +

                            "이제 아래 작업을 수행해줘:\n\n" +

                            "1. 루틴 검토 및 changeMethod 변경\n" +
                            "- 각 루틴 항목의 changeMethod 값이 적절한 제품 사용법인지 검토해.\n" +
                            "- 적절하면 그대로 두고, 개선이 필요하면 해당 항목의 changeMethod 값만 3줄로 정리해서 최대 255자 이하로 내 피부 타입에 맞게 자세히 추천해서 수정해줘.\n" +
                            "- 반드시 다음 JSON 객체 내의 `루틴_검토` 키에 해당하는 JSON 배열 구조를 유지하되, **변경이 필요한 changeMethod 값만 수정**해서 출력하고, " +
                            "수정이 필요 없으면 기존 데이터랑 같이 '(현재 사용법 유지)'라고 붙여서 보여줘:\n\n" +

                            "2. 제품 추천\n" +
                            "- 기존에 사용 중인 제품 중 변경해서 쓰는게 더 좋은 제품이 있다면 추천해줘.\n" +
                            "- 이 경우, 'action'에 'REPLACE'를 입력해줘.\n" +
                            "- 그리고 기존에 사용 중인 제품에 추가하면 좋을 제품도 있다면 추천해줘.\n" +
                            "- 이 경우, 'action'에 'ADD'를 입력해줘.\n" +
                            "- 추천한 제품은 추천한 이유를 성분 기반으로 내 피부 타입에 맞게 자세히 설명해줘.\n" +
                            "- 반드시 'action'이 'REPLACE'일 경우, 추천 제품으로 변경할 existingProductId를 꼭 넣어줘.\n" +
                            "- 'action'이 'ADD'일 경우, existingProductId를 넣지 말아줘.\n" +

                            "```json\n" +
                            "{\n" + // 전체 응답을 객체로 감싸도록 변경
                            "  \"루틴_검토\": [\n" + // 루틴 검토는 이 키 아래 배열로
                            "    {\n" +
                            "      \"routineChangeId\": 123, \n" + // AI가 기존 루틴의 ID를 알 수 있도록 포함
                            "      \"routineName\": \"세안\",\n" +
                            "      \"routineKind\": \"[토너, 앰플, 크림, 로션, 세럼 ,팩 ,패드 ,스킨] 중\",\n" +
                            "      \"routineTime\": \"MORNING\",\n" +
                            "      \"routineOrders\": 1,\n" +
                            "      \"changeMethod\": \"수분크림을 바르기 전, 피부 타입에 맞는 앰플이나 세럼을 추가하여 보습력을 높임\"\n" +
                            "    },\n" +
                            "    ...\n" +
                            "  ],\n" +
                            "  \"제품_추천\": [\n" + // 제품 추천은 이 키 아래 배열로
                            "    {\n" +
                            "      \"existingProductId\": 123, \n" + // 추가된 필드: 기존 제품의 ID (해당하는 경우)
                            "      \"suggestProduct\": \"라운드랩 1025 독도 토너\",\n" +
                            "      \"reason\": \"민감한 피부를 진정시키고, 피부결을 정돈\",\n" +
                            "      \"action\": \"REPLACE\", // 또는 \"ADD\"\n" +
                            "      \"kind\": \"토너\" // **반드시 다음 값 중 하나를 사용해야 해: " + validKinds + "**\n" +
                            "    },\n" +
                            "    ...\n" +
                            "  ]\n" +
                            "}\n" +
                            "```\n\n" + // 닫는 ```

                            "⚠️ 주의: 반드시 위의 JSON 형식(단일 JSON 객체)을 지켜서 출력하고, 설명은 절대 JSON 바깥에 쓰지 마. 만약 JSON 외의 텍스트가 있다면, 그 텍스트는 JSON 블록 안에 포함시켜서 제공해줘.",
                    json1, json2
            );

            RequestDTO request = new RequestDTO();
            request.createGeminiReqDto(prompt);
            String requestJson = mapper.writeValueAsString(request);
            System.out.println("Gemini API 요청 본문: " + requestJson);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", openConfig.getKey());
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(openConfig.getUrl(), entity, String.class);

            log.info(response.getBody());
//            if(response.getStatusCode() == HttpStatus.OK) {
//                try {
//                    String responseBody = response.getBody();
//                    JsonNode rootNode = mapper.readTree(responseBody);
//
//                    String textContent = rootNode.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
//                    log.info("Gemini로부터 받은 원본 텍스트 내용: " + textContent);
//
//                    // 정규 표현식을 사용하여 ```json { ... } ``` 형태의 단일 JSON 객체 블록 찾기
//                    Pattern pattern = Pattern.compile("```json\\s*\n(\\{[\\s\\S]*?\\})\\s*\n```");
//                    Matcher matcher = pattern.matcher(textContent);
//
//                    String fullJsonContent = null;
//                    if (matcher.find()) {
//                        fullJsonContent = matcher.group(1); // ```json ... ``` 안의 내용 (전체 JSON 객체) 추출
//                        log.info("추출된 전체 JSON 내용: " + fullJsonContent);
//                    } else {
//                        log.error("Gemini 응답에서 예상한 JSON 객체 블록을 찾을 수 없습니다.");
//                        throw new IllegalStateException("Gemini 응답에 예상한 JSON 객체 블록이 포함되어 있지 않습니다.");
//                    }
//
//                    JsonNode actualData = mapper.readTree(fullJsonContent);
//
//                    // "루틴_검토" 배열 파싱
//                    JsonNode routineReviewNode = actualData.get("루틴_검토");
//                    if (routineReviewNode == null || !routineReviewNode.isArray()) {
//                        log.error("'루틴_검토' 키가 없거나 JSON 배열이 아닙니다.");
//                        throw new IllegalStateException("Gemini 응답에 '루틴_검토' 배열이 없습니다.");
//                    }
//                    List<RoutineChangeDTO> routineChanges = mapper.readValue(
//                            mapper.treeAsTokens(routineReviewNode),
//                            new TypeReference<List<RoutineChangeDTO>>() {}
//                    );
//
//                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ID: " + userId + "인 사용자를 찾을 수 없습니다."));
//                    // 사용자가 본인 루틴을 입력 -> 입력과 동시에 routine_group id 가 생성 -> 생성된 id를 불러와서 ai가 추천하는 제품을 routine_change 에 저장
//
//                    RoutineGroupDTO routineGroupDTO = routineGroupRepository.findLatestRoutineGroup(user.getId());
//
//                    System.out.println(routineGroupDTO.getId());
//
//                    List<RoutineChange> routineChangeList = new ArrayList<>();
//                    RoutineChange routineChange;
//                    for (RoutineChangeDTO rc : routineChanges) {
//                        routineChange = new RoutineChange(
//                                rc.getRoutineName(),
//                                rc.getRoutineKind(),
//                                rc.getRoutineTime(),
//                                rc.getRoutineOrders(),
//                                rc.getChangeMethod(),
//                                user,
//                                routineGroupDTO.getId()
//                        );
//                        routineChangeList.add(routineChange);
//                    }
//                    routineChangeRepository.saveAll(routineChangeList);
//
//                    // "제품_추천" 배열 파싱
//                    JsonNode productRecommendNode = actualData.get("제품_추천");
//                    if (productRecommendNode == null || !productRecommendNode.isArray()) {
//                        log.error("'제품_추천' 키가 없거나 JSON 배열이 아닙니다.");
//                        throw new IllegalStateException("Gemini 응답에 '제품_추천' 배열이 없습니다.");
//                    }
//
//                    for(JsonNode item : productRecommendNode) {
//                        // Attempt to get existing product ID; default to -1 if not found or null
//                        Long existingProductId = item.has("기존 제품 ID") && !item.get("기존 제품 ID").isNull()
//                                ? item.get("기존 제품 ID").asLong() : null; // Changed to Long, can be null
//
//                        String recommendProduct = item.get("추천 제품").asText();
//                        String reason = item.get("이유").asText();
//
//                        String actionText = item.get("변화").asText();
//                        Action action;
//                        if ("대체".equals(actionText)) {
//                            action = Action.Replace;
//                        } else if ("추가".equals(actionText)) {
//                            action = Action.Add;
//                        } else {
//                            log.warn("알 수 없는 액션 텍스트: " + actionText + ". Add로 기본 설정합니다.");
//                            action = Action.Add;
//                        }
//
//                        String kindText = item.get("종류").asText();
//                        Kinds kind = null;
////
//                        // Kinds Enum의 name 필드를 통해 Kinds 값을 찾음
//                        for (Kinds k : Kinds.values()) {
//                            if (k.getNameTypeJson().equalsIgnoreCase(kindText)) {
//                                kind = k;
//                                break;
//                            }
//                        }
//
//                        if (kind == null) {
//                            log.warn("Kinds Enum에 알 수 없는 종류 텍스트: " + kindText + ". 해당 추천 항목을 저장하지 않고 건너뜁니다.");
//                            continue;
//                        }
//
//                        if (kind != null) {
//                            DeepRecommend deepRecommend = new DeepRecommend(
//                                    action,
//                                    kind,
//                                    recommendProduct,
//                                    reason,
//                                    routineGroupDTO.getId()
//                            );
//
//                            /* 기존 제품을 추천 변경할 때 */
//                            if (existingProductId != null) {
//                                deepRecommend.setExistingProductId(existingProductId);
//                            }
//                            deepRecommendRepository.save(deepRecommend);
//                        }
//                    }
//                    return response.getBody();
//
//                } catch (Exception e) {
//                    log.error("JSON 파싱 또는 처리 오류: ", e);
//                    return "응답 처리 중 오류가 발생했습니다: " + e.getMessage();
//                }
//            }else{
//                log.error("Gemini API 요청 실패. 상태 코드: " + response.getStatusCode() + ", 본문: " + response.getBody());
//                return "Gemini API 요청에 실패했습니다.";
//            }

            if(response.getStatusCode() == HttpStatus.OK) {
                try {
                    String responseBody = response.getBody();
                    JsonNode rootNode = mapper.readTree(responseBody);

                    String textContent = rootNode.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
                    log.info("Gemini로부터 받은 원본 텍스트 내용: {}", textContent);

                    Pattern pattern = Pattern.compile("```json\\s*\n(\\{[\\s\\S]*?\\})\\s*\n```");
                    Matcher matcher = pattern.matcher(textContent);

                    String fullJsonContent;
                    if (matcher.find()) {
                        fullJsonContent = matcher.group(1);
                        log.info("추출된 전체 JSON 내용: {}", fullJsonContent);
                    } else {
                        // 만약 ```json ... ``` 블록이 없다면, textContent 자체가 JSON일 수 있다고 가정
                        // 또는 특정 LLM 모델은 마크다운 블록 없이 순수 JSON만 반환할 수 있음
                        log.warn("Gemini 응답에서 ```json ... ``` 블록을 찾을 수 없습니다. textContent를 직접 JSON으로 파싱 시도합니다.");
                        fullJsonContent = textContent; // textContent를 직접 사용
                        // 이 경우, fullJsonContent가 유효한 JSON인지 추가 검증이 필요할 수 있음
                        try {
                            mapper.readTree(fullJsonContent); // 유효성 검사
                        } catch (JsonProcessingException e) {
                            log.error("textContent를 JSON으로 직접 파싱하는데 실패했습니다. 내용은 다음과 같습니다: {}", fullJsonContent, e);
                            throw new IllegalStateException("Gemini 응답에서 유효한 JSON 객체 블록을 찾을 수 없거나 직접 파싱에 실패했습니다.");
                        }
                    }

                    JsonNode actualData = mapper.readTree(fullJsonContent);

                    // "루틴_검토" 배열 파싱 (기존 코드 유지)
                    JsonNode routineReviewNode = actualData.get("루틴_검토");
                    if (routineReviewNode == null || !routineReviewNode.isArray()) {
                        log.error("'루틴_검토' 키가 없거나 JSON 배열이 아닙니다.");
                        // 필요시 예외를 던지거나, 빈 리스트로 처리하는 등의 로직 추가 가능
                        // throw new IllegalStateException("Gemini 응답에 '루틴_검토' 배열이 없습니다.");
                    } else {
                        List<RoutineChangeDTO> routineChanges = mapper.readValue(
                                mapper.treeAsTokens(routineReviewNode),
                                new TypeReference<List<RoutineChangeDTO>>() {}
                        );

                        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ID: " + userId + "인 사용자를 찾을 수 없습니다."));
                        RoutineGroupDTO routineGroupDTO = routineGroupRepository.findLatestRoutineGroup(user.getId());
                        // routineGroupDTO가 null일 경우에 대한 방어 코드 추가 권장
                        if (routineGroupDTO == null) {
                            log.error("사용자 ID {}에 대한 최신 루틴 그룹을 찾을 수 없습니다.", userId);
                            // 적절한 예외 처리 또는 로직 중단
                            throw new IllegalStateException("최신 루틴 그룹 정보를 가져올 수 없습니다.");
                        }
                        System.out.println(routineGroupDTO.getId()); // 로깅으로 변경하거나 실제 필요한 경우에만 사용

                        List<RoutineChange> routineChangeList = new ArrayList<>();
                        for (RoutineChangeDTO rc : routineChanges) {
                            // RoutineChangeDTO의 필드들이 null이 아닌지 검사하는 로직 추가 권장
                            RoutineChange routineChange = new RoutineChange(
                                    rc.getRoutineName(),
                                    rc.getRoutineKind(),
                                    rc.getRoutineTime(),
                                    rc.getRoutineOrders(),
                                    rc.getChangeMethod(),
                                    user,
                                    routineGroupDTO.getId()
                            );
                            routineChangeList.add(routineChange);
                        }
                        if (!routineChangeList.isEmpty()) {
                            routineChangeRepository.saveAll(routineChangeList);
                        }
                    }


                    // "제품_추천" 배열 파싱 (수정된 부분)
                    JsonNode productRecommendNode = actualData.get("제품_추천");
                    if (productRecommendNode == null || !productRecommendNode.isArray()) {
                        log.error("'제품_추천' 키가 없거나 JSON 배열이 아닙니다. 해당 부분 처리를 건너뜁니다.");
                        // 제품 추천 정보가 필수가 아니라면 여기서 처리를 중단하고 다음 로직으로 넘어갈 수 있음
                        // throw new IllegalStateException("Gemini 응답에 '제품_추천' 배열이 없습니다.");
                    } else {
                        // 프롬프트에서 정의한 JSON 필드명 (영문)
                        final String FIELD_EXISTING_PRODUCT_ID = "existingProductId";
                        final String FIELD_SUGGEST_PRODUCT = "suggestProduct";
                        final String FIELD_REASON = "reason";
                        final String FIELD_ACTION = "action";
                        final String FIELD_KIND = "kind";

                        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ID: " + userId + "인 사용자를 찾을 수 없습니다."));
                        RoutineGroupDTO routineGroupDTO = routineGroupRepository.findLatestRoutineGroup(user.getId());
                        if (routineGroupDTO == null) {
                            log.error("사용자 ID {}에 대한 최신 루틴 그룹을 찾을 수 없습니다. (제품 추천 저장 불가)", userId);
                            throw new IllegalStateException("최신 루틴 그룹 정보를 가져올 수 없어 제품 추천을 저장할 수 없습니다.");
                        }


                        for (JsonNode item : productRecommendNode) {
                            // 필수 필드 존재 여부 및 null 체크 강화
                            if (!item.has(FIELD_SUGGEST_PRODUCT) || item.get(FIELD_SUGGEST_PRODUCT).isNull() ||
                                    !item.has(FIELD_REASON) || item.get(FIELD_REASON).isNull() ||
                                    !item.has(FIELD_ACTION) || item.get(FIELD_ACTION).isNull() ||
                                    !item.has(FIELD_KIND) || item.get(FIELD_KIND).isNull()) {
                                log.warn("제품 추천 항목에 필수 필드 ('{}', '{}', '{}', '{}') 중 일부가 누락되었거나 null입니다. Item: {}. 이 항목을 건너뜁니다.",
                                        FIELD_SUGGEST_PRODUCT, FIELD_REASON, FIELD_ACTION, FIELD_KIND, item.toString());
                                continue;
                            }

                            String recommendProduct = item.get(FIELD_SUGGEST_PRODUCT).asText();
                            String reason = item.get(FIELD_REASON).asText();
                            String actionText = item.get(FIELD_ACTION).asText();
                            String kindText = item.get(FIELD_KIND).asText();

                            Action actionEnum; // 변수명 변경 (기존: action)
                            if ("REPLACE".equalsIgnoreCase(actionText)) {
                                actionEnum = Action.Replace;
                            } else if ("ADD".equalsIgnoreCase(actionText)) {
                                actionEnum = Action.Add;
                            } else {
                                log.warn("알 수 없는 action 텍스트: '{}' 입니다. Item: {}. 이 추천 항목을 건너뜁니다.", actionText, item.toString());
                                continue;
                            }

                            Long parsedExistingProductId = null; // 변수명 변경 (기존: existingProductId)
                            if (item.has(FIELD_EXISTING_PRODUCT_ID) && !item.get(FIELD_EXISTING_PRODUCT_ID).isNull()) {
                                if (item.get(FIELD_EXISTING_PRODUCT_ID).isNumber()) {
                                    parsedExistingProductId = item.get(FIELD_EXISTING_PRODUCT_ID).asLong();
                                } else {
                                    log.warn("'{}' 필드가 존재하지만 숫자 타입이 아닙니다. 값: '{}', Item: {}.",
                                            FIELD_EXISTING_PRODUCT_ID, item.get(FIELD_EXISTING_PRODUCT_ID).asText(), item.toString());
                                    // REPLACE 액션일 경우 아래 로직에서 처리됨
                                }
                            }

                            Kinds kindEnum = null; // 변수명 변경 (기존: kind)
                            for (Kinds k : Kinds.values()) {
                                if (k.getNameTypeJson().equalsIgnoreCase(kindText)) {
                                    kindEnum = k;
                                    break;
                                }
                            }

                            if (kindEnum == null) {
                                log.warn("Kinds Enum에 알 수 없는 종류 텍스트: '{}' 입니다. Item: {}. 이 추천 항목을 건너뜁니다.", kindText, item.toString());
                                continue;
                            }

                            DeepRecommend deepRecommend = new DeepRecommend(
                                    actionEnum,
                                    kindEnum,
                                    recommendProduct,
                                    reason,
                                    routineGroupDTO.getId()
                            );

                            if (actionEnum == Action.Replace) {
                                if (parsedExistingProductId != null) {
                                    deepRecommend.setExistingProductId(parsedExistingProductId);
                                } else {
                                    // 'REPLACE' 액션의 경우 existingProductId는 프롬프트에 따라 필수적입니다.
                                    log.error("'REPLACE' 액션이지만 '{}' 필드가 누락되었거나, null이거나, 숫자 타입이 아닙니다. Item: {}. 이 추천 항목은 저장하지 않습니다.",
                                            FIELD_EXISTING_PRODUCT_ID, item.toString());
                                    continue; // 유효하지 않은 추천이므로 저장하지 않고 다음 항목으로 넘어감
                                }
                            }
                            // 'ADD' 액션의 경우, parsedExistingProductId 값을 사용하지 않으므로
                            // deepRecommend 객체의 existingProductId 필드는 기본값(일반적으로 null)을 유지합니다.

                            deepRecommendRepository.save(deepRecommend);
                        }
                    }
                    return response.getBody(); // 성공 시 원본 응답 반환

                } catch (JsonProcessingException e) {
                    log.error("JSON 파싱 또는 처리 오류: ", e);
                    return "응답 처리 중 오류가 발생했습니다: " + e.getMessage(); // 사용자에게 전달될 오류 메시지
                } catch (IllegalStateException e) {
                    log.error("처리 중 예상된 상태 예외 발생: ", e);
                    return "데이터 처리 중 문제가 발생했습니다: " + e.getMessage();
                } catch (RuntimeException e) { // 좀 더 넓은 범위의 RuntimeException 처리
                    log.error("처리 중 예기치 않은 런타임 오류 발생: ", e);
                    return "시스템 처리 중 예기치 않은 오류가 발생했습니다.";
                } catch (Exception e) { // 그 외 모든 Exception 처리
                    log.error("JSON 파싱 또는 처리 중 예측하지 못한 오류 발생: ", e);
                    return "알 수 없는 오류로 응답 처리에 실패했습니다.";
                }
            } else {
                log.error("Gemini API 요청 실패. 상태 코드: {}, 본문: {}", response.getStatusCode(), response.getBody());
                return "Gemini API 요청에 실패했습니다. 상태 코드: " + response.getStatusCodeValue();
            }
        } catch (JsonProcessingException e) {
            log.error("Gemini 요청을 위한 JSON 생성 중 오류 발생: ", e);
            throw new RuntimeException("JSON 요청 생성 중 오류 발생", e);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: ", e);
            return "예상치 못한 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /* step2. 맞춤 루틴 추천 조회 */
    public List<RoutineChangeDTO> getRoutineChangeList(String userId) {
        return routineChangeRepository.findRoutinesByUserId(userId);
    }
    public List<RoutineChangeDTO> getAllRoutineChangeList(String userId) {
        return routineChangeRepository.findAllRoutinesByUserId(userId);
    }

    /* step2. 제품 변경 및 추가 추천 */
    public List<RecommendResponseDTO> getRecommendResponseDTOList(String userId) {
        return deepRecommendRepository.findLatestRecommendByUserId(userId);
    }

    public List<RecommendResponseDTO> getAllecommendResponseDTOList(String userId) {
        return deepRecommendRepository.findAllRecommendByUserId(userId);
    }
}