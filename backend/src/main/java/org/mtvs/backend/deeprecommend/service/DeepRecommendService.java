package org.mtvs.backend.deeprecommend.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.deeprecommend.config.OpenConfig;
import org.mtvs.backend.deeprecommend.dto.RecommendDTO;
import org.mtvs.backend.deeprecommend.dto.RequestDTO;
import org.mtvs.backend.deeprecommend.entity.DeepRecommend;
import org.mtvs.backend.deeprecommend.entity.RoutineChange;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.deeprecommend.repository.DeepRecommendRepository;
import org.mtvs.backend.deeprecommend.repository.RoutineChangeRepository;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class DeepRecommendService {

//    private final StringHttpMessageConverter stringHttpMessageConverter;

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final OpenConfig openConfig;
    private final DeepRecommendRepository deepRecommendRepository;
    private final RoutineChangeRepository routineChangeRepository;

    @Autowired
    public DeepRecommendService(RoutineRepository routineRepository, UserRepository userRepository, OpenConfig openConfig, DeepRecommendRepository deepRecommendRepository, RoutineChangeRepository routineChangeRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
        this.openConfig = openConfig;
        this.deepRecommendRepository = deepRecommendRepository;
        this.routineChangeRepository = routineChangeRepository;
    }

    public String askOpenAI(String userId) {

        ProblemDto getProblemByUsername = userRepository.findAllRoutineByUserId(userId);
        List<RoutinesDto> recommend = routineRepository.findAllRoutineByUserId(userId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json1 = mapper.writeValueAsString(recommend);
            String json2 = mapper.writeValueAsString(getProblemByUsername);

//            String prompt = String.format(
//                    "너는 이제 피부관리사야. 아래는 나의 루틴과 피부 고민이야:\n" +
//                            "루틴: %s\n" +
//                            "피부 고민: %s\n" +
//                            "\n" +
//                            "이 정보를 바탕으로, " +
//                            "위의 루틴에서 'orders'가 기존 루틴에 대한 설명인데, 기존 루틴이 괜찮으면 그대로 보여주고, 아니면 루틴 추천해서 변경해줘\n" +
//                            "반드시 기존 JSON 배열 형식에서 orders 컬럼만 수정해서 전달해줘: \n" +
//                            "그리고 기존 루틴에서 변경하면 좋을 **모든 제품들**을 다음 기준에 따라 추천해줘:\n" +
//                            "\n" +
//                            "1. 기존 제품을 대체하면 좋을 제품과 그 이유\n" +
//                            "2. 루틴에 추가하면 좋을 제품과 그 이유\n" +
//                            "3. 피부 개선을 위한 방향 \n" +
//                            "\n" +
//                            "반드시 아래 형식에 맞춰 **복수의 추천 항목을 JSON 배열 형식**으로 전달해줘:\n" +
//                            "\n" +
//                            "[\n" +
//                            "  {\n" +
//                            "    \"추천 제품\": \"제품명\",\n" +
//                            "    \"이유\": \"추천하는 이유\",\n" +
//                            "    \"변화\": \"대체\" // 또는 \"추가\"\n" +
//                            "    \"종류\": \"토너\",\"앰플\",\"크림\" 중\n" +
//                            "\n" +
//                            "  },\n" +
//                            "  {\n" +
//                            "    \"추천 제품\": \"제품명\",\n" +
//                            "    \"이유\": \"추천하는 이유\",\n" +
//                            "    \"변화\": \"추가\"\n" +
//                            " \"종류\": \"토너\",\"앰플\",\"크림\" 중\n" +
//                            "\n" +
//                            "  },\n" +
//                            "  ...",
//                    json1, json2
//            );

            String prompt = String.format(
                    "너는 피부관리 전문가야. 아래는 사용자의 스킨케어 루틴과 피부 고민 정보야:\n\n" +
                            "루틴(JSON): %s\n" +
                            "피부 고민: %s\n\n" +

                            "이제 아래 작업을 수행해줘:\n\n" +

                            "1. 루틴 검토 및 routineOrders 변경\n" +
                            "- 각 루틴 항목의 routineOrders 값이 적절한 루틴인지 검토해.\n" +
                            "- 적절하면 그대로 두고, 개선이 필요하면 해당 항목의 routineOrders 값만 추천해서 수정해줘.\n" +
                            "- 반드시 기존 JSON 배열 구조를 유지하되, **변경이 필요한 routineOrders 값만 수정**해서 아래와 같은 형식으로 출력해:\n\n" +
                            "[\n" +
                            "  {\n" +
                            "    \"routineName\": \"세안\",\n" +
                            "    \"routineKind\": \"CLEANER\",\n" +
                            "    \"routineTime\": \"MORNING\",\n" +
                            "    \"routineOrders\": 1,\n" +
                            "    \"changeMethod\": \"부드럽게 마사지하기\"\n" +
                            "  },\n" +
                            "  ...\n" +
                            "]\n\n" +

                            "2. 제품 추천\n" +
                            "- 기존 루틴에서 변경하거나 추가하면 좋을 **모든 제품**을 아래 기준에 따라 추천해줘:\n" +
                            "  - 기존 제품을 대체하면 좋을 제품과 그 이유\n" +
                            "  - 루틴에 추가하면 좋을 제품과 그 이유\n" +
                            "  - 피부 개선을 위한 방향\n\n" +
                            "- 반드시 아래 JSON 배열 형식으로 출력해:\n\n" +
                            "[\n" +
                            "  {\n" +
                            "    \"추천 제품\": \"제품명\",\n" +
                            "    \"이유\": \"추천하는 이유\",\n" +
                            "    \"변화\": \"대체\", // 또는 \"추가\"\n" +
                            "    \"종류\": \"토너\" // 또는 \"앰플\", \"크림\" 등\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"추천 제품\": \"제품명\",\n" +
                            "    \"이유\": \"추천하는 이유\",\n" +
                            "    \"변화\": \"추가\",\n" +
                            "    \"종류\": \"앰플\"\n" +
                            "  }\n" +
                            "]\n\n" +

                            "⚠️ 주의: 반드시 JSON 형식을 지켜서 출력하고, 설명은 절대 JSON 바깥에 쓰지 마.",
                    json1, json2
            );

            // RequestDTO 형식으로 변환하여 Gemini API에 전달
            RequestDTO request = new RequestDTO();
            request.createGeminiReqDto(prompt);
            String requestJson = mapper.writeValueAsString(request);
           // 텍스트 형식을 RequestDTO 구조로 변환
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

                    // 1. JSON 코드 블럭 제거
                    String cleanJson = textContent.replace("```json\n", "").replace("\n```", "").trim();

                    JsonNode actualData = mapper.readTree(cleanJson);
                    log.info(actualData.toString());

                    ObjectMapper objectMapper = new ObjectMapper();

                    /* 추천 루틴 */
                    // 2. "1. 루틴 검토" 부분만 파싱
                    String[] sections = cleanJson.split("2\\. 제품 추천");
                    String routineJsonPart = sections[0];

                    int startIndex = routineJsonPart.indexOf('[');
                    int endIndex = routineJsonPart.lastIndexOf(']') + 1;
                    String routineJsonArray = routineJsonPart.substring(startIndex, endIndex);

                    // 3. 실제 변환
                    List<RoutineChange> routineChanges = objectMapper.readValue(
                            routineJsonArray,
                            new TypeReference<List<RoutineChange>>() {}
                    );

                    User user = userRepository.findById(userId).orElseThrow();

                    for (RoutineChange rc : routineChanges) {
                        rc.setUser(user);
                    }

                    // 4. 저장 또는 반환 등 처리
                    routineChangeRepository.saveAll(routineChanges);


                    for(JsonNode item : actualData) {
                        String recommendProduct = item.get("추천 제품").asText();
                        String reason = item.get("이유").asText();

                        String actionText = item.get("변화").asText();
                        if ("대체".equals(actionText)) {
                            actionText = "Replace";
                        } else if ("추가".equals(actionText)) {
                            actionText = "Add";
                        }
                        Action action = Action.valueOf(actionText);

                        String kindText = item.get("종류").asText();
                        Kinds kind = Kinds.valueOf(kindText);


                        RecommendDTO recommend2 = new RecommendDTO();
                        recommend2.setSuggest_product(recommendProduct);
                        recommend2.setReason(reason);
                        recommend2.setAction(action);
                        recommend2.setKind(kind);

                        log.info(recommend2.toString());

                        DeepRecommend deepRecommend = new DeepRecommend(
                                recommend2.getAction(),
                                recommend2.getKind(),
                                recommend2.getSuggest_product(),
                                recommend2.getReason()
                        );

                        deepRecommendRepository.save(deepRecommend);
                    }
                    return response.getBody();

                } catch (Exception e) {
                    log.error("JSON 파싱 오류: ", e);
                    return "응답 처리 중 오류가 발생했습니다.";
                }
            }else{
                System.out.println("응답 본문: " + response.getBody());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}
