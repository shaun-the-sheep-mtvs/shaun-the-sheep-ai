package org.mtvs.backend.recommend.controller;

import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.recommend.dto.RequestDTO;
import org.mtvs.backend.user.entity.User.SkinType;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.recommend.dto.ResponseDTO;
import org.mtvs.backend.recommend.dto.UserRequestDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("api/recommend")
public class RecommendController {

    @Value("${spring.gemini.api.key}")
    private String geminiApiKey;

    @Value("${spring.gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    @Autowired
    public RecommendController(@Qualifier("geminiRestTemplate") RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @PostMapping("/diagnoses")
    public ResponseEntity<String> diagnose(@RequestBody UserRequestDTO userRequestDTO) {
        // 사용자 ID로 데이터베이스에서 사용자 정보를 조회
        User user = userRepository.findById(userRequestDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자의 피부 타입과 고민 목록을 가져옴
        SkinType skinType = user.getSkinType();
        List<String> concerns = user.getTroubles();

        String geminiURL = geminiApiUrl + "?key=" + geminiApiKey;

        // Gemini API에 전달할 프롬프트 생성 - 간결하게 변경
        String prompt = String.format("제품3개추천,JSON반환:{피부:%s,고민:%s}",
                skinType, String.join(",", concerns));

        // Gemini에 전달하는 DTO 생성 후 prompt 담아서 전달
        RequestDTO request = new RequestDTO();
        request.createGeminiReqDto(prompt);
        String description = "";

        System.out.println("Gemini API에 요청을 보냅니다: " + prompt);

        try {
            ResponseDTO response = restTemplate.postForObject(geminiURL, request, ResponseDTO.class);
            description = response.getCandidates().get(0).getContent().getParts().get(0).getText();
        } catch (Exception e) {
            throw new RuntimeException("Internal error", e);
        }
        return ResponseEntity.ok(description);
    }
}