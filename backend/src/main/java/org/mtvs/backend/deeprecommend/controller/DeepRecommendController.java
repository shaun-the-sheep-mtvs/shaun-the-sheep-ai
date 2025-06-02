package org.mtvs.backend.deeprecommend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.deeprecommend.dto.RecommendResponseDTO;
import org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO;
import org.mtvs.backend.deeprecommend.service.DeepRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/deep")
@RequiredArgsConstructor
public class DeepRecommendController {

    private final DeepRecommendService recommendService;

    @PostMapping("/recommend")
    public String ask(@AuthenticationPrincipal CustomUserDetails user){
        return recommendService.askOpenAI(user.getUser().getId());
    }

    /* step2. 맞춤 루틴 추천 조회 */
    @GetMapping("/routine-change")
    public List<RoutineChangeDTO> getRoutineChangeList(@AuthenticationPrincipal CustomUserDetails user) {
        return recommendService.getRoutineChangeList(user.getUser().getId());
    }

    /* step2. 제품 변경 및 추가 추천 */
    @GetMapping("/product-recommend")
    public ResponseEntity<List<RecommendResponseDTO>> getRecommendResponseDTOList(@AuthenticationPrincipal CustomUserDetails user) {
        List<RecommendResponseDTO> list= recommendService.getRecommendResponseDTOList(user.getUser().getId());
        return ResponseEntity.ok(list);
    }
}
