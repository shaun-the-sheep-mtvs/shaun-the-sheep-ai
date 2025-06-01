package org.mtvs.backend.recommend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.naver.image.api.NaverApiService;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.dto.ProductsWithUserInfoResponseDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.repository.ProductRepository;
import org.mtvs.backend.product.service.ProductUserLinkService;
import org.mtvs.backend.recommend.dto.RequestDTO;
import org.mtvs.backend.product.service.ProductService;
import org.mtvs.backend.user.entity.User.SkinType;
import org.mtvs.backend.recommend.dto.ResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.mtvs.backend.session.GuestData;
import jakarta.servlet.http.HttpSession;
import org.mtvs.backend.checklist.service.CheckListService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class RecommendController {

    private final ProductRepository productRepository;
    private final ProductUserLinkService productUserLinkService;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final NaverApiService naverApiService;
    private final CheckListService checkListService;

    public RecommendController(RestTemplate restTemplate, ObjectMapper objectMapper, ProductService productService, UserRepository userRepository, ProductRepository productRepository, NaverApiService naverApiService, ProductUserLinkService productUserLinkService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.productService = productService;
        this.naverApiService = naverApiService;
        this.productUserLinkService = productUserLinkService;
    }

    @PostMapping("/api/recommend/diagnoses")
    public ResponseEntity<?> diagnose(@AuthenticationPrincipal CustomUserDetails customUserDetail) {

        // 사용자의 피부 타입과 고민 목록을 가져옴
        SkinType skinType = customUserDetail.getUser().getSkinType();
        List<String> concerns = customUserDetail.getUser().getTroubles();

        String geminiURL = geminiApiUrl;

        // Gemini API에 전달할 프롬프트 생성 - 간결하게 변경
        String prompt = String.format(
                "피부 타입: %s\n고민: %s\n\n" +
                        "다음 조건에 맞는 스킨케어 제품을 JSON 형식으로 추천해 주세요:\n" +
                        "- 각 제형(토너, 세럼, 로션, 크림)별로 3개의 제품을 추천합니다.\n" +
                        "- 각 제품에 대해 '제품명', '추천타입', '성분'을 한국어로 포함합니다.\n" +
                        "- '추천타입'은 '건성', '지성', '복합성', '민감성', '수분부족지성' 중 반드시 하나만 골라서 적어줘.\n" +
                        "- JSON의 키 중 skinType, concerns, recommendations는 반드시 영어로 작성되어야 합니다.\n" +
                        "- JSON의 키 중 '제품명', '추천타입', '성분'은 반드시 한국어로 작성되어야 합니다.\n" +
                        "- JSON의 키 중 'toner', 'serum', 'lotion', 'cream' 은 반드시 영어로 작성되어야 합니다.\n" +
                        "- 제품명, 추천타입, 성분명은 모두 한국어로 작성해 주세요.\n" +
                        "- '고민'은 문자열 배열(string[])이어야 합니다.\n" +
                        "- '성분'도 문자열 배열(string[])이어야 합니다.\n" +
                        "- '추천타입'은 제품의 특징을 설명하는 문자열입니다.\n" +
                        "- 실제 존재하는 한국 화장품 브랜드의 제품명을 사용해 주세요.\n" +
                        "응답은 JSON 코드 블록 없이 순수한 JSON 형식으로 제공해 주세요.",
                skinType, String.join(", ", concerns)
        );
    }

    // --- Utility: Call Gemini API and parse response ---
    private JsonNode callGeminiAndParse(String prompt) throws Exception {
        RequestDTO request = new RequestDTO();
        request.createGeminiReqDto(prompt);
        String rawResponse = "";
        try {
            ResponseDTO response = restTemplate.postForObject(geminiApiUrl, request, ResponseDTO.class);
            rawResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            String cleanedJson = rawResponse.replaceAll("(?s)```json\\s*|```\\s*", "");
            System.out.println("정제된 JSON: " + cleanedJson);

            // JSON으로 파싱
            // Gemini 응답값이 JSON 처럼 보이는 String 값이라 직렬화를 하기 위해 ObectMapper 사용
//            public JsonNode readTree(String content) throws JsonProcessingException, JsonMappingException {
//                this._assertNotNull("content", content);
//
//                try {
//                    return this._readTreeAndClose(this._jsonFactory.createParser(content));
//                } catch (JsonProcessingException e) {
//                    throw e;
//                } catch (IOException e) {
//                    throw JsonMappingException.fromUnexpectedIOE(e);
//                }
//            }

            JsonNode jsonNode = objectMapper.readTree(cleanedJson);
            System.out.println(jsonNode);
            productService.saveProducts(jsonNode,customUserDetail.getUser().getId());

            // 파싱된 JSON을 반환
            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            System.err.println("원본 응답: " + rawResponse);
            throw e;
        }
    }

    // --- Shared recommendation logic ---
    private ResponseEntity<?> handleRecommendation(String skinType, List<String> concerns, String userEmail) {
        try {
            // 1. Build prompt
            String prompt = buildGeminiPrompt(skinType != null ? skinType : "", concerns);
            // 2. Call Gemini
            JsonNode jsonNode = callGeminiAndParse(prompt);
            // 3. Save products for user if email is provided
            if (userEmail != null) {
                productService.saveProducts(jsonNode, userEmail);
            }
            // 4. Return recommendations
            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Gemini recommendation error: " + e.getMessage());
        }
    }

    // --- User Recommendation (Authenticated) ---
    @PostMapping("/api/recommend/diagnoses")
    public ResponseEntity<?> diagnose(@AuthenticationPrincipal CustomUserDetails customUserDetail) {
        SkinType skinType = customUserDetail.getUser().getSkinType();
        List<String> concerns = customUserDetail.getUser().getTroubles();
        String userEmail = customUserDetail.getUser().getEmail();
        return handleRecommendation(skinType != null ? skinType.toString() : "", concerns, userEmail);
    }

    // --- Guest Recommendation (Session-based) ---
    @PostMapping("/api/recommend/guest")
    public ResponseEntity<?> recommendForGuest(HttpSession session) {
        GuestData guestData = (GuestData) session.getAttribute("guestData");
        if (guestData == null) {
            return ResponseEntity.badRequest().body("No guest data in session");
        }
        String mbtiCode = checkListService.calculateMbtiForGuest(guestData);
        String skinType = checkListService.getSkinTypeForMbti(mbtiCode);
        List<String> concerns = guestData.getTroubles();
        // Pass null for userEmail so products are not saved for guests
        return handleRecommendation(skinType, concerns, null);
    }

    // --- Get 3 random products for user ---
    @GetMapping("api/recommend/random-recommendations")
    public List<ProductDTO> ThreeProducts(@AuthenticationPrincipal CustomUserDetails customUserDetail){
        String Id = customUserDetail.getUser().getId();
        List<ProductDTO> products = productService.getProducts(Id);
        Collections.shuffle(products);

        // 3개 반환
        return products.stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    // --- User recommendations with user info ---
    @GetMapping("api/recommend/user-recommendations")
    public ProductsWithUserInfoResponseDTO UserRecommendation(@AuthenticationPrincipal CustomUserDetails customUserDetail){
        String userId = customUserDetail.getUser().getId();
        return productService.getBalancedRecommendations(userId);
    }
}