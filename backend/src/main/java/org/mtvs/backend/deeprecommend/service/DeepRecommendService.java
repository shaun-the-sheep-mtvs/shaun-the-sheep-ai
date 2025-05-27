package org.mtvs.backend.deeprecommend.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.dto.ProblemDto;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
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
import org.mtvs.backend.routine.entity.enums.Kinds; // Kinds Enum import 확인
import org.mtvs.backend.routine.repository.RoutineGroupRepository;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    public DeepRecommendService(RoutineRepository routineRepository, UserRepository userRepository, OpenConfig openConfig, DeepRecommendRepository deepRecommendRepository, RoutineChangeRepository routineChangeRepository, RoutineGroupRepository routineGroupRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
        this.openConfig = openConfig;
        this.deepRecommendRepository = deepRecommendRepository;
        this.routineChangeRepository = routineChangeRepository;
        this.routineGroupRepository = routineGroupRepository;
    }

    public String askOpenAI(long userId) {

        ProblemDto getProblemByUsername = userRepository.findUserSkinDataByUserId(userId);
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
                            "- 각 루틴 항목의 changeMethod 값이 적절한 루틴인지 검토해.\n" +
                            "- 적절하면 그대로 두고, 개선이 필요하면 해당 항목의 changeMethod 값만 추천해서 수정해줘.\n" +
                            "- 반드시 다음 JSON 객체 내의 `루틴_검토` 키에 해당하는 JSON 배열 구조를 유지하되, **변경이 필요한 changeMethod 값만 수정**해서 출력해:\n\n" +
                            "```json\n" +
                            "{\n" + // 전체 응답을 객체로 감싸도록 변경
                            "  \"루틴_검토\": [\n" + // 루틴 검토는 이 키 아래 배열로
                            "    {\n" +
//                            "      \"routineId\": 123, \n" + // AI가 기존 루틴의 ID를 알 수 있도록 포함
                            "      \"routineName\": \"세안\",\n" +
                            "      \"routineKind\": \"[토너, 앰플, 크림, 로션, 세럼] 중\",\n" +
                            "      \"routineTime\": \"MORNING\",\n" +
                            "      \"routineOrders\": 1,\n" +
                            "      \"changeMethod\": \"수분크림을 바르기 전, 피부 타입에 맞는 앰플이나 세럼을 추가하여 보습력을 높임\"\n" +
                            "    },\n" +
                            "    ...\n" +
                            "  ],\n" +
                            "  \"제품_추천\": [\n" + // 제품 추천은 이 키 아래 배열로
                            "    {\n" +
                            "      \"기존 제품 ID\": 123, \n" + // 추가된 필드: 기존 제품의 ID (해당하는 경우)
                            "      \"추천 제품\": \"제품명\",\n" +
                            "      \"이유\": \"추천하는 이유\",\n" +
                            "      \"변화\": \"대체\", // 또는 \"추가\"\n" +
                            "      \"종류\": \"토너\" // **반드시 다음 값 중 하나를 사용해야 해: " + validKinds + "**\n" +
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
            if(response.getStatusCode() == HttpStatus.OK) {
                try {
                    String responseBody = response.getBody();
                    JsonNode rootNode = mapper.readTree(responseBody);

                    String textContent = rootNode.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
                    log.info("Gemini로부터 받은 원본 텍스트 내용: " + textContent);

                    // 정규 표현식을 사용하여 ```json { ... } ``` 형태의 단일 JSON 객체 블록 찾기
                    Pattern pattern = Pattern.compile("```json\\s*\n(\\{[\\s\\S]*?\\})\\s*\n```");
                    Matcher matcher = pattern.matcher(textContent);

                    String fullJsonContent = null;
                    if (matcher.find()) {
                        fullJsonContent = matcher.group(1); // ```json ... ``` 안의 내용 (전체 JSON 객체) 추출
                        log.info("추출된 전체 JSON 내용: " + fullJsonContent);
                    } else {
                        log.error("Gemini 응답에서 예상한 JSON 객체 블록을 찾을 수 없습니다.");
                        throw new IllegalStateException("Gemini 응답에 예상한 JSON 객체 블록이 포함되어 있지 않습니다.");
                    }

                    JsonNode actualData = mapper.readTree(fullJsonContent);

                    // "루틴_검토" 배열 파싱
                    JsonNode routineReviewNode = actualData.get("루틴_검토");
                    if (routineReviewNode == null || !routineReviewNode.isArray()) {
                        log.error("'루틴_검토' 키가 없거나 JSON 배열이 아닙니다.");
                        throw new IllegalStateException("Gemini 응답에 '루틴_검토' 배열이 없습니다.");
                    }
                    List<RoutineChangeDTO> routineChanges = mapper.readValue(
                            mapper.treeAsTokens(routineReviewNode),
                            new TypeReference<List<RoutineChangeDTO>>() {}
                    );

                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ID: " + userId + "인 사용자를 찾을 수 없습니다."));
                    RoutineGroupDTO routineGroupDTO = routineGroupRepository.findLatestRoutineGroup(user.getId());

                    List<RoutineChange> routineChangeList = new ArrayList<>();
                    RoutineChange routineChange;
                    for (RoutineChangeDTO rc : routineChanges) {
                        routineChange = new RoutineChange(
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
                    routineChangeRepository.saveAll(routineChangeList);

                    // "제품_추천" 배열 파싱
                    JsonNode productRecommendNode = actualData.get("제품_추천");
                    if (productRecommendNode == null || !productRecommendNode.isArray()) {
                        log.error("'제품_추천' 키가 없거나 JSON 배열이 아닙니다.");
                        throw new IllegalStateException("Gemini 응답에 '제품_추천' 배열이 없습니다.");
                    }

                    for(JsonNode item : productRecommendNode) {
                        // Attempt to get existing product ID; default to -1 if not found or null
                        Long existingProductId = item.has("기존 제품 ID") && !item.get("기존 제품 ID").isNull()
                                ? item.get("기존 제품 ID").asLong() : null; // Changed to Long, can be null

                        String recommendProduct = item.get("추천 제품").asText();
                        String reason = item.get("이유").asText();

                        String actionText = item.get("변화").asText();
                        Action action;
                        if ("대체".equals(actionText)) {
                            action = Action.Replace;
                        } else if ("추가".equals(actionText)) {
                            action = Action.Add;
                        } else {
                            log.warn("알 수 없는 액션 텍스트: " + actionText + ". Add로 기본 설정합니다.");
                            action = Action.Add;
                        }

                        String kindText = item.get("종류").asText();
                        Kinds kind = null;

                        // Kinds Enum의 name 필드를 통해 Kinds 값을 찾음
                        for (Kinds k : Kinds.values()) {
                            if (k.getNameTypeJson().equalsIgnoreCase(kindText)) {
                                kind = k;
                                break;
                            }
                        }

                        if (kind == null) {
                            log.warn("Kinds Enum에 알 수 없는 종류 텍스트: " + kindText + ". 해당 추천 항목을 저장하지 않고 건너뜁니다.");
                            continue;
                        }

                        if (kind != null) {
                            DeepRecommend deepRecommend = new DeepRecommend(
                                    action,
                                    kind,
                                    recommendProduct,
                                    reason,
                                    routineGroupDTO.getId()
                            );

                            /* 기존 제품을 추천 변경할 때 */
                            if (existingProductId != null) {
                                deepRecommend.setExistingProductId(existingProductId);
                            }
                            deepRecommendRepository.save(deepRecommend);
                        }
                    }
                    return response.getBody();

                } catch (Exception e) {
                    log.error("JSON 파싱 또는 처리 오류: ", e);
                    return "응답 처리 중 오류가 발생했습니다: " + e.getMessage();
                }
            }else{
                log.error("Gemini API 요청 실패. 상태 코드: " + response.getStatusCode() + ", 본문: " + response.getBody());
                return "Gemini API 요청에 실패했습니다.";
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
    public List<RoutineChangeDTO> getRoutineChangeList(Long userId) {
        return routineChangeRepository.findRoutinesByUserId(userId);
    }

    /* step2. 제품 변경 및 추가 추천 */
    public List<RecommendResponseDTO> getRecommendResponseDTOList(Long userId) {
        return deepRecommendRepository.findLatestRecommendByUserId(userId);
    }
}