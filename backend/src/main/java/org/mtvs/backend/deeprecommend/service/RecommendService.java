package org.mtvs.backend.deeprecommend.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mtvs.backend.deeprecommend.dto.RecommendDTO;
import org.mtvs.backend.deeprecommend.dto.RoutineAnalysisDTO;
import org.mtvs.backend.deeprecommend.repository.Recommendrepository;
import org.mtvs.backend.routine.dto.RequestRoutineAllDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RecommendService {

    private final StringHttpMessageConverter stringHttpMessageConverter;
    @Value("${open.api.key}")
    private String apiKey;
    private final Recommendrepository recommendrepository;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";

    @Autowired
    public RecommendService(Recommendrepository recommendrepository, StringHttpMessageConverter stringHttpMessageConverter) {
        this.recommendrepository = recommendrepository;
        this.stringHttpMessageConverter = stringHttpMessageConverter;
    }

    public String askOpenAI(RequestRoutineAllDTO requestRoutineAllDTO) {


//        // User - skin,troubles
//        List<자바 객체> user = repository.find();
//
//        1. 자바 객체 ->  DTO
//        User user = userRepository.findById(1L).get();
//        UserDTO dto = new UserDTO(user);
//
//        2. 자바 객체 -> Json
//        String json = objectMapper.writeValueAsString(user);
//
//        3. 파싱 코드 (Jackson ObjectMapper 사용)
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String json = "{\"name\":\"Alice\", \"age\":25}";
//        UserDTO user = objectMapper.readValue(json, UserDTO.class);
//
//        4. dto -> string
//        String json = new ObjectMapper().writeValueAsString(dto);
//
//        결론 : 파싱 안해도 될듯 / 뽑아오기만 하면 될듯
//

        List<Routine> recommend = RoutineRepository.getProblemByUsername(requestRoutineAllDTO);


        RestTemplate restTemplate = new RestTemplate();

        String requestJson = "/ + / {} =/ "; //

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);


        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);


        ObjectMapper mapper = new ObjectMapper();
        try {
//            JsonNode root = mapper.readTree(response.getBody()); // 파싱 코드 추가 -> 프롬프트 정하면 그대로 하면 될듯
//            root
//            RecommendDTO result = mapper.readValue(response.getBody(), RecommendDTO.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }else{
            return null;
        }


    }
}
