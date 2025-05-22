package org.mtvs.backend.recommend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.recommend.dto.RequestDTO;
import org.mtvs.backend.auth.model.User.SkinType;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.recommend.dto.ResponseDTO;
import org.mtvs.backend.recommend.dto.UserRequestDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping
public class RecommendController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecommendController(@Qualifier("geminiRestTemplate") RestTemplate restTemplate,
                              UserRepository userRepository,
                              ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/api/recommend/diagnoses")
    public ResponseEntity<?> diagnose(@AuthenticationPrincipal CustomUserDetails customUserDetail) {

        // 사용자의 피부 타입과 고민 목록을 가져옴
        SkinType skinType = customUserDetail.getUser().getSkinType();
        List<String> concerns = customUserDetail.getUser().getTroubles();

        String geminiURL = geminiApiUrl + "?key=" + geminiApiKey;

        // Gemini API에 전달할 프롬프트 생성 - 간결하게 변경
        String prompt = String.format(
                "피부 타입: %s\n고민: %s\n\n" +
                        "다음 조건에 맞는 스킨케어 제품을 JSON 형식으로 추천해 주세요:\n" +
                        "- 각 제형(토너, 세럼, 로션, 크림)별로 3개의 제품을 추천합니다.\n" +
                        "- 각 제품에 대해 '제품명', '추천타입', '성분'을 한국어로 포함합니다.\n" +
                        "- JSON의 키 중 skinType, concerns, recommendations는 반드시 영어로 작성되어야 합니다." +
                        "- JSON의 키 중 '제품명', '추천타입', '성분'은 반드시 한국어로 작성되어야 합니다.\n" +
                        "- JSON의 키 중 'toner', 'serum', 'lotion', 'cream' 은 반드시 영어로 작성되어야 합니다.\n" +
                        "- 제품명, 추천타입, 성분명은 모두 한국어로 작성해 주세요.\n" +
                        "- '고민'은 문자열 배열(string[])이어야 합니다.\n" +
                        "- '성분'도 문자열 배열(string[])이어야 합니다.\n" +
                        "- '추천타입'은 제품의 특징을 설명하는 문자열입니다.\n" +
                        "- 실제 존재하는 한국 화장품 브랜드의 제품명을 사용해 주세요.\n" +
                        "- 가격대를 다양하게 포함해 주세요 (저가, 중가, 고가).\n\n" +
                        "응답은 JSON 코드 블록 없이 순수한 JSON 형식으로 제공해 주세요.",
                skinType, String.join(", ", concerns)
        );

        // Gemini에 전달하는 DTO 생성 후 prompt 담아서 전달
        RequestDTO request = new RequestDTO();
        request.createGeminiReqDto(prompt);
        String rawResponse = "";

        System.out.println("Gemini API에 요청을 보냅니다: " + prompt);

        try {
            // Gemini API 호출
            ResponseDTO response = restTemplate.postForObject(geminiURL, request, ResponseDTO.class);
            rawResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            System.out.println(rawResponse);

            // 마크다운 코드 블록 제거 (```json과 ```)
            String cleanedJson = rawResponse.replaceAll("(?s)```json\\s*|```\\s*", "");
            System.out.println("정제된 JSON: " + cleanedJson);

            // JSON으로 파싱
            JsonNode jsonNode = objectMapper.readTree(cleanedJson);

            // 파싱된 JSON을 반환 (자동으로 직렬화됨)
            return ResponseEntity.ok(jsonNode);

        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            System.err.println("원본 응답: " + rawResponse);

            // 오류 시 원본 문자열 반환 (프론트엔드에서 처리)
            return ResponseEntity.ok(rawResponse);
        }
    }
}