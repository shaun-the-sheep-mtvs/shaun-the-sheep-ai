package org.mtvs.backend.session.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.service.ProductService;
import org.mtvs.backend.userskin.service.UserskinService;
import org.mtvs.backend.userskin.entity.Userskin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionRecoveryService {
    
    private final ProductService productService;
    private final UserskinService userskinService;
    private final ObjectMapper objectMapper;
    
    public SessionRecoveryService(ProductService productService, UserskinService userskinService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.userskinService = userskinService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * 사용자 로그인 후 DB에서 제품 데이터를 세션으로 로드합니다.
     * 
     * @param userId 사용자 ID
     * @param session HTTP 세션
     * @return 성공 여부
     */
    public boolean loadUserProductsToSession(String userId, HttpSession session) {
        try {
            // 1. 사용자 제품 데이터 조회
            List<ProductDTO> userProducts = productService.getProductsByUserId(userId);
            
            if (userProducts.isEmpty()) {
                System.out.println("No products found for user: " + userId);
                return false;
            }
            
            // 2. 사용자 피부 정보 조회
            Optional<Userskin> userskinOpt = userskinService.getActiveUserskinByUserId(userId);
            
            // 3. 세션 제품 데이터 구조 생성
            Map<String, Object> sessionProducts = createSessionProductsStructure(userProducts, userskinOpt);
            
            // 4. 세션에 저장
            session.setAttribute("sessionProducts", sessionProducts);
            
            System.out.println("Successfully loaded " + userProducts.size() + " products to session for user: " + userId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading user products to session: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 세션에 유효한 제품 데이터가 있는지 확인합니다.
     * 
     * @param session HTTP 세션
     * @return 유효한 데이터 존재 여부
     */
    public boolean hasValidSessionProducts(HttpSession session) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> sessionProducts = (Map<String, Object>) session.getAttribute("sessionProducts");
            
            if (sessionProducts == null) {
                return false;
            }
            
            // 타임스탬프 체크
            Long timestamp = (Long) sessionProducts.get("timestamp");
            if (timestamp != null) {
                long currentTime = System.currentTimeMillis();
                long sessionAge = currentTime - timestamp;
                
                // 30분 이상 된 세션은 무효 처리
                if (sessionAge > 30 * 60 * 1000) {
                    session.removeAttribute("sessionProducts");
                    return false;
                }
            }
            
            // 제품 데이터 존재 여부 확인
            @SuppressWarnings("unchecked")
            List<Object> randomProducts = (List<Object>) sessionProducts.get("random");
            @SuppressWarnings("unchecked")
            Map<String, Object> balancedProducts = (Map<String, Object>) sessionProducts.get("balanced");
            
            return (randomProducts != null && !randomProducts.isEmpty()) || 
                   (balancedProducts != null && !balancedProducts.isEmpty());
            
        } catch (Exception e) {
            System.err.println("Error checking session products: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 세션 제품 데이터에서 랜덤 제품 3개를 추출합니다.
     * 
     * @param session HTTP 세션
     * @return 랜덤 제품 리스트
     */
    @SuppressWarnings("unchecked")
    public List<Object> getRandomProductsFromSession(HttpSession session) {
        try {
            Map<String, Object> sessionProducts = (Map<String, Object>) session.getAttribute("sessionProducts");
            
            if (sessionProducts == null) {
                return List.of();
            }
            
            List<Object> randomProducts = (List<Object>) sessionProducts.get("random");
            return randomProducts != null ? randomProducts : List.of();
            
        } catch (Exception e) {
            System.err.println("Error getting random products from session: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * 세션 제품 데이터에서 카테고리별 제품을 추출합니다.
     * 
     * @param session HTTP 세션
     * @return 카테고리별 제품 맵
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBalancedProductsFromSession(HttpSession session) {
        try {
            Map<String, Object> sessionProducts = (Map<String, Object>) session.getAttribute("sessionProducts");
            
            if (sessionProducts == null) {
                return Map.of();
            }
            
            Map<String, Object> balancedProducts = (Map<String, Object>) sessionProducts.get("balanced");
            return balancedProducts != null ? balancedProducts : Map.of();
            
        } catch (Exception e) {
            System.err.println("Error getting balanced products from session: " + e.getMessage());
            return Map.of();
        }
    }
    
    /**
     * 제품 리스트를 세션 데이터 구조로 변환합니다.
     * 
     * @param userProducts 사용자 제품 리스트
     * @param userskinOpt 사용자 피부 정보
     * @return 세션 제품 데이터 구조
     */
    private Map<String, Object> createSessionProductsStructure(List<ProductDTO> userProducts, Optional<Userskin> userskinOpt) {
        Map<String, Object> sessionProducts = new HashMap<>();
        
        // 랜덤 제품 3개 선택
        List<ProductDTO> randomProducts = userProducts.stream()
            .limit(3)
            .toList();
        
        // formulation별 제품 분류
        Map<String, List<ProductDTO>> productsByFormulation = new HashMap<>();
        for (ProductDTO product : userProducts) {
            productsByFormulation.computeIfAbsent(product.getFormulation(), k -> new java.util.ArrayList<>()).add(product);
        }
        
        // 세션 구조 생성
        sessionProducts.put("random", randomProducts);
        sessionProducts.put("balanced", productsByFormulation);
        sessionProducts.put("timestamp", System.currentTimeMillis());
        
        // 피부 정보 추가
        if (userskinOpt.isPresent()) {
            Userskin userskin = userskinOpt.get();
            sessionProducts.put("skinType", userskinService.getSkinTypeString(userskin));
            sessionProducts.put("concerns", userskinService.getConcernLabels(userskin));
        }
        
        return sessionProducts;
    }
    
    /**
     * 세션 제품 데이터를 클리어합니다.
     * 
     * @param session HTTP 세션
     */
    public void clearSessionProducts(HttpSession session) {
        session.removeAttribute("sessionProducts");
    }
}