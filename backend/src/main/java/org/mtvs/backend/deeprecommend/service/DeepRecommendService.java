package org.mtvs.backend.deeprecommend.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.dto.ProblemDto;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.deeprecommend.config.OpenConfig;
import org.mtvs.backend.deeprecommend.dto.RecommendDTO;
import org.mtvs.backend.deeprecommend.dto.RequestDTO;
import org.mtvs.backend.deeprecommend.entity.DeepRecommend;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.deeprecommend.repository.DeepRecommendRepository;
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


    @Autowired
    public DeepRecommendService(RoutineRepository routineRepository, UserRepository userRepository, DeepRecommendRepository deepRecommendRepository, OpenConfig openConfig) {
        this.routineRepository = routineRepository;
//        this.stringHttpMessageConverter = stringHttpMessageConverter;
        this.userRepository = userRepository;
        this.deepRecommendRepository = deepRecommendRepository;
        this.openConfig = openConfig;

    }

    public String askOpenAI(long userId) {

        ProblemDto getProblemByUsername = userRepository.findAllRoutineByUserId(userId);
        List<RoutinesDto> recommend = routineRepository.findAllRoutineByUserId(userId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json1 = mapper.writeValueAsString(recommend);
            String json2 = mapper.writeValueAsString(getProblemByUsername);

            String prompt = String.format(
                    "너는 이제 피부관리사야. 아래는 나의 루틴과 피부 고민이야:\n" +
                            "루틴: 아누아 어성초 토너, 웰라쥬 100, 토리든 수분크림 \n" +
                            "피부 고민: %s\n" +
                            "\n" +
                            "이 정보를 바탕으로, 기존 루틴에서 변경하면 좋을 **모든 제품들**을 다음 기준에 따라 추천해줘:\n" +
                            "\n" +
                            "1. 기존 제품을 대체하면 좋을 제품과 그 이유\n" +
                            "2. 루틴에 추가하면 좋을 제품과 그 이유\n" +
                            "3. 피부 개선을 위한 방향 \n" +
                            "\n" +
                            "반드시 아래 형식에 맞춰 **복수의 추천 항목을 JSON 배열 형식**으로 전달해줘:\n" +
                            "\n" +
                            "[\n" +
                            "  {\n" +
                            "    \"추천 제품\": \"제품명\",\n" +
                            "    \"이유\": \"추천하는 이유\",\n" +
                            "    \"변화\": \"대체\" // 또는 \"추가\"\n" +
                            "    \"종류\": \"토너\",\"앰플\",\"크림\" 중\n" +
                            "\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"추천 제품\": \"제품명\",\n" +
                            "    \"이유\": \"추천하는 이유\",\n" +
                            "    \"변화\": \"추가\"\n" +
                            " \"종류\": \"토너\",\"앰플\",\"크림\" 중\n" +
                            "\n" +
                            "  },\n" +
                            "  ...",
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


                    String cleanJson = textContent.replace("```json\n", "").replace("\n```", "").trim();

                    JsonNode actualData = mapper.readTree(cleanJson);
                    log.info(actualData.toString());

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
