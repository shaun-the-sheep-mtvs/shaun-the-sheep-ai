package org.mtvs.backend.guestrecommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mtvs.backend.guestrecommend.dto.GuestRecommendRequestDTO;
import org.mtvs.backend.guestrecommend.dto.GuestRecommendResponseDTO;
import org.mtvs.backend.guestrecommend.prompt.GuestPromptTemplates;
import org.mtvs.backend.prompt.service.PromptService;
import org.mtvs.backend.recommend.dto.RequestDTO;
import org.mtvs.backend.recommend.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuestRecommendService {
    
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PromptService promptService;
    
    public GuestRecommendService(RestTemplate restTemplate, ObjectMapper objectMapper, PromptService promptService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.promptService = promptService;
    }
    
    /**
     * 게스트 추천을 생성하고 세션에 저장합니다.
     *
     * @param request 게스트 추천 요청 데이터
     * @param session HTTP 세션
     * @return 생성된 추천 결과
     */
    public GuestRecommendResponseDTO generateRecommendations(GuestRecommendRequestDTO request, HttpSession session) {
        try {
            // 프롬프트 생성
            String prompt = GuestPromptTemplates.createBasicRecommendationPrompt(
                request.getSkinType(), 
                request.getConcerns(), 
                promptService
            );
            
            System.out.println("Generated prompt for guest: " + prompt);
            
            // Gemini API 호출
            RequestDTO geminiRequest = new RequestDTO();
            geminiRequest.createGeminiReqDto(prompt);
            
            ResponseDTO response = restTemplate.postForObject(geminiApiUrl, geminiRequest, ResponseDTO.class);
            String rawResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            
            System.out.println("Gemini raw response: " + rawResponse);
            
            // 마크다운 코드 블록 제거
            String cleanedJson = rawResponse.replaceAll("(?s)```json\\s*|```\\s*", "");
            System.out.println("Cleaned JSON: " + cleanedJson);
            
            JsonNode recommendationsJson = objectMapper.readTree(cleanedJson);
            
            // 응답 DTO 생성
            GuestRecommendResponseDTO responseDTO = new GuestRecommendResponseDTO(
                request.getSkinType(),
                "GUEST", // 게스트의 경우 고정값 또는 별도 계산
                recommendationsJson
            );
            
            // 세션에 저장 (기존 형식)
            session.setAttribute("guestRecommendations", recommendationsJson);
            session.setAttribute("guestRecommendationData", responseDTO);
            
            // 통합 세션 형식으로도 저장
            saveGuestProductsToSession(recommendationsJson, request, session);
            
            System.out.println("Guest recommendations generated and saved to session");
            
            return responseDTO;
            
        } catch (Exception e) {
            System.err.println("Error generating guest recommendations: " + e.getMessage());
            throw new RuntimeException("게스트 추천 생성 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 세션에서 게스트 추천 결과를 조회합니다.
     *
     * @param session HTTP 세션
     * @return 저장된 추천 결과
     */
    public GuestRecommendResponseDTO getRecommendations(HttpSession session) {
        try {
            // 세션에서 데이터 조회
            GuestRecommendResponseDTO responseDTO = (GuestRecommendResponseDTO) session.getAttribute("guestRecommendationData");
            
            if (responseDTO == null) {
                // 대안으로 개별 데이터 조회 시도
                JsonNode recommendations = (JsonNode) session.getAttribute("guestRecommendations");
                @SuppressWarnings("unchecked")
                Map<String, Object> analysisResult = (Map<String, Object>) session.getAttribute("guestAnalysisResult");
                
                if (recommendations != null && analysisResult != null) {
                    responseDTO = new GuestRecommendResponseDTO(
                        (String) analysisResult.get("skinTypeName"),
                        (String) analysisResult.get("mbtiCode"),
                        recommendations
                    );
                }
            }
            
            return responseDTO;
            
        } catch (Exception e) {
            System.err.println("Error retrieving guest recommendations: " + e.getMessage());
            throw new RuntimeException("게스트 추천 조회 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 기존 게스트 분석 결과를 바탕으로 추천을 생성합니다.
     * CheckListController에서 호출됩니다.
     *
     * @param skinType 피부 타입
     * @param concerns 피부 고민 목록
     * @param session HTTP 세션
     */
    public void generateRecommendationsFromAnalysis(String skinType, List<String> concerns, HttpSession session) {
        try {
            GuestRecommendRequestDTO request = new GuestRecommendRequestDTO();
            request.setSkinType(skinType);
            request.setConcerns(concerns);
            
            generateRecommendations(request, session);
            
        } catch (Exception e) {
            System.err.println("Error generating recommendations from analysis: " + e.getMessage());
            // 실패해도 체크리스트 프로세스는 계속 진행되도록 예외를 던지지 않음
        }
    }
    
    /**
     * 게스트 추천 결과를 통합 세션 형식으로 저장합니다.
     * 
     * @param recommendationsJson Gemini API 응답 JSON
     * @param request 게스트 추천 요청 데이터
     * @param session HTTP 세션
     */
    private void saveGuestProductsToSession(JsonNode recommendationsJson, GuestRecommendRequestDTO request, HttpSession session) {
        try {
            Map<String, Object> sessionProducts = new HashMap<>();
            
            // 추천 결과에서 제품 정보 추출
            JsonNode recommendations = recommendationsJson.get("recommendations");
            if (recommendations != null) {
                // 각 카테고리별 제품 처리
                Map<String, Object> productsByCategory = new HashMap<>();
                
                // 카테고리별 제품 저장
                String[] categories = {"toner", "serum", "lotion", "cream"};
                for (String category : categories) {
                    JsonNode categoryProducts = recommendations.get(category);
                    if (categoryProducts != null && categoryProducts.isArray()) {
                        productsByCategory.put(category, categoryProducts);
                    }
                }
                
                // 랜덤 제품 3개 생성 (각 카테고리에서 1개씩, 부족하면 랜덤)
                List<JsonNode> randomProducts = new ArrayList<>();
                for (String category : categories) {
                    JsonNode categoryProducts = (JsonNode) productsByCategory.get(category);
                    if (categoryProducts != null && categoryProducts.size() > 0) {
                        randomProducts.add(categoryProducts.get(0));
                        if (randomProducts.size() >= 3) break;
                    }
                }
                
                sessionProducts.put("random", randomProducts);
                sessionProducts.put("balanced", productsByCategory);
            }
            
            // 메타데이터 추가
            sessionProducts.put("timestamp", System.currentTimeMillis());
            sessionProducts.put("skinType", request.getSkinType());
            sessionProducts.put("mbtiCode", "GUEST");
            sessionProducts.put("concerns", request.getConcerns());
            
            // 세션에 저장
            session.setAttribute("sessionProducts", sessionProducts);
            
            System.out.println("Guest products saved to unified session format");
            
        } catch (Exception e) {
            System.err.println("Error saving guest products to session: " + e.getMessage());
        }
    }
}