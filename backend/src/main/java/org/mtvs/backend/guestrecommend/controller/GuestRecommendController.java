package org.mtvs.backend.guestrecommend.controller;

import org.mtvs.backend.guestrecommend.dto.GuestRecommendRequestDTO;
import org.mtvs.backend.guestrecommend.dto.GuestRecommendResponseDTO;
import org.mtvs.backend.guestrecommend.service.GuestRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/recommend")
public class GuestRecommendController {
    
    private final GuestRecommendService guestRecommendService;
    
    public GuestRecommendController(GuestRecommendService guestRecommendService) {
        this.guestRecommendService = guestRecommendService;
    }
    
    /**
     * 게스트 추천 결과를 조회합니다.
     * 프론트엔드에서 게스트 모드일 때 호출됩니다.
     *
     * @param session HTTP 세션
     * @return 게스트 추천 결과
     */
    @GetMapping("/guest")
    public ResponseEntity<?> getGuestRecommendations(HttpSession session) {
        try {
            GuestRecommendResponseDTO recommendations = guestRecommendService.getRecommendations(session);
            
            if (recommendations == null) {
                return ResponseEntity.badRequest().body("게스트 추천 데이터가 없습니다. 체크리스트를 먼저 완료해 주세요.");
            }
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            System.err.println("Error in getGuestRecommendations: " + e.getMessage());
            return ResponseEntity.internalServerError().body("추천 데이터를 불러오는 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 게스트 추천을 생성합니다.
     * 선택적으로 사용되는 엔드포인트입니다.
     *
     * @param request 게스트 추천 요청 데이터
     * @param session HTTP 세션
     * @return 생성된 추천 결과
     */
    @PostMapping("/guest")
    public ResponseEntity<?> generateGuestRecommendations(@RequestBody GuestRecommendRequestDTO request, HttpSession session) {
        try {
            GuestRecommendResponseDTO recommendations = guestRecommendService.generateRecommendations(request, session);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            System.err.println("Error in generateGuestRecommendations: " + e.getMessage());
            return ResponseEntity.internalServerError().body("추천 생성 중 오류가 발생했습니다.");
        }
    }
}