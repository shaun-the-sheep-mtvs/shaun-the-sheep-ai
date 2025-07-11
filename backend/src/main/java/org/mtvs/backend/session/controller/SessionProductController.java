package org.mtvs.backend.session.controller;

import jakarta.servlet.http.HttpSession;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.session.service.SessionRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class SessionProductController {
    
    private final SessionRecoveryService sessionRecoveryService;
    
    public SessionProductController(SessionRecoveryService sessionRecoveryService) {
        this.sessionRecoveryService = sessionRecoveryService;
    }
    
    /**
     * 세션에서 랜덤 제품 3개를 조회합니다. (메인 페이지용)
     * 사용자와 게스트 모두 사용 가능합니다.
     * 
     * @param userDetails 사용자 정보 (로그인한 경우)
     * @param session HTTP 세션
     * @return 랜덤 제품 3개 또는 오류 메시지
     */
    @GetMapping("/session-random")
    public ResponseEntity<?> getRandomProductsFromSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session) {
        
        try {
            // 1. 세션에 유효한 제품 데이터가 있는지 확인
            if (!sessionRecoveryService.hasValidSessionProducts(session)) {
                
                // 2. 로그인한 사용자인 경우 DB에서 세션으로 로드 시도
                if (userDetails != null && userDetails.getUser() != null) {
                    String userId = userDetails.getUser().getId();
                    
                    if (sessionRecoveryService.loadUserProductsToSession(userId, session)) {
                        // 로드 성공, 계속 진행
                        System.out.println("Successfully loaded user products to session for user: " + userId);
                    } else {
                        // DB에도 데이터가 없음
                        return ResponseEntity.status(204).body(createNoDataResponse("체크리스트를 먼저 완료해주세요."));
                    }
                } else {
                    // 게스트이고 세션에 데이터가 없음
                    return ResponseEntity.status(204).body(createNoDataResponse("피부 분석을 먼저 완료해주세요."));
                }
            }
            
            // 3. 세션에서 랜덤 제품 조회
            List<Object> randomProducts = sessionRecoveryService.getRandomProductsFromSession(session);
            
            if (randomProducts.isEmpty()) {
                return ResponseEntity.status(204).body(createNoDataResponse("추천 제품이 없습니다. 체크리스트를 완료해주세요."));
            }
            
            // 4. 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("products", randomProducts);
            response.put("count", randomProducts.size());
            response.put("source", "session");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in getRandomProductsFromSession: " + e.getMessage());
            return ResponseEntity.internalServerError().body(createErrorResponse("제품 데이터를 불러오는 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 세션에서 카테고리별 제품을 조회합니다. (추천 페이지용)
     * 사용자와 게스트 모두 사용 가능합니다.
     * 
     * @param userDetails 사용자 정보 (로그인한 경우)
     * @param session HTTP 세션
     * @return 카테고리별 제품 또는 오류 메시지
     */
    @GetMapping("/session-balanced")
    public ResponseEntity<?> getBalancedProductsFromSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session) {
        
        try {
            // 1. 세션에 유효한 제품 데이터가 있는지 확인
            if (!sessionRecoveryService.hasValidSessionProducts(session)) {
                
                // 2. 로그인한 사용자인 경우 DB에서 세션으로 로드 시도
                if (userDetails != null && userDetails.getUser() != null) {
                    String userId = userDetails.getUser().getId();
                    
                    if (sessionRecoveryService.loadUserProductsToSession(userId, session)) {
                        // 로드 성공, 계속 진행
                        System.out.println("Successfully loaded user products to session for user: " + userId);
                    } else {
                        // DB에도 데이터가 없음
                        return ResponseEntity.status(204).body(createNoDataResponse("체크리스트를 먼저 완료해주세요."));
                    }
                } else {
                    // 게스트이고 세션에 데이터가 없음
                    return ResponseEntity.status(204).body(createNoDataResponse("피부 분석을 먼저 완료해주세요."));
                }
            }
            
            // 3. 세션에서 카테고리별 제품 조회
            Map<String, Object> balancedProducts = sessionRecoveryService.getBalancedProductsFromSession(session);
            
            if (balancedProducts.isEmpty()) {
                return ResponseEntity.status(204).body(createNoDataResponse("추천 제품이 없습니다. 체크리스트를 완료해주세요."));
            }
            
            // 4. 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("products", balancedProducts);
            response.put("source", "session");
            
            // 세션에서 피부 정보 추가
            @SuppressWarnings("unchecked")
            Map<String, Object> sessionData = (Map<String, Object>) session.getAttribute("sessionProducts");
            if (sessionData != null) {
                response.put("skinType", sessionData.get("skinType"));
                response.put("mbtiCode", sessionData.get("mbtiCode"));
                response.put("concerns", sessionData.get("concerns"));
            } else {
                // 게스트 분석 결과에서 정보 가져오기
                @SuppressWarnings("unchecked")
                Map<String, Object> guestAnalysis = (Map<String, Object>) session.getAttribute("guestAnalysis");
                if (guestAnalysis != null) {
                    response.put("skinType", guestAnalysis.get("skinTypeName"));
                    response.put("mbtiCode", guestAnalysis.get("mbtiCode"));
                    response.put("concerns", guestAnalysis.get("concernDescriptions"));
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in getBalancedProductsFromSession: " + e.getMessage());
            return ResponseEntity.internalServerError().body(createErrorResponse("제품 데이터를 불러오는 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 세션 제품 데이터를 클리어합니다.
     * 
     * @param session HTTP 세션
     * @return 결과 메시지
     */
    @DeleteMapping("/session-clear")
    public ResponseEntity<?> clearSessionProducts(HttpSession session) {
        try {
            sessionRecoveryService.clearSessionProducts(session);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "세션 제품 데이터가 클리어되었습니다.");
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error clearing session products: " + e.getMessage());
            return ResponseEntity.internalServerError().body(createErrorResponse("세션 클리어 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 데이터 없음 응답 생성
     */
    private Map<String, Object> createNoDataResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("redirect", "/checklist");
        response.put("hasData", false);
        return response;
    }
    
    /**
     * 오류 응답 생성
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("success", false);
        return response;
    }
}