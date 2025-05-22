package org.mtvs.backend.deeprecommend.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.dto.ProblemDto;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.deeprecommend.config.OpenConfig;
import org.mtvs.backend.deeprecommend.dto.RecommendDTO;
import org.mtvs.backend.deeprecommend.dto.RequestDTO;
import org.mtvs.backend.deeprecommend.entity.DeepRecommend;
import org.mtvs.backend.deeprecommend.repository.DeepRecommendRepository;
import org.mtvs.backend.routine.dto.RoutineDTO;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
                    "너는 이제 피부관리사야. 아래는 나의 루틴과 피부 고민이야:\n루틴: %s\n피부 고민: %s\n" +
                            "이 정보를 바탕으로 기존 루틴에서 변경하면 좋을 제품과 그 이유, 추가하면 좋을 제품도 알려줘.\n" +
                            "형식은 반드시 아래와 같이 Json 형태로 전달해줘:\n" +
                            "추천 제품 : \"변경하면 좋을 제품\"\n" +
                            "이유 : \"작성한 이유\"\n" +
                            "추가 추천 제품 : \"추가하면 좋을 제품명\"",
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
            headers.set("Authorization", "Bearer " + openConfig.getKey());
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(openConfig.getUrl(), entity, String.class);


            if(response.getStatusCode() == HttpStatus.OK) {
                RecommendDTO recommend2 = new ObjectMapper().readValue(json1, RecommendDTO.class);

                DeepRecommend deepRecommend = new DeepRecommend(recommend2.getId(),recommend2.getAction(),recommend2.getKind(),recommend2.getSuggest_product(),recommend2.getReason());
                deepRecommendRepository.save(deepRecommend);

                return response.getBody();
            }else{
                System.out.println("응답 본문: " + response.getBody());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}
