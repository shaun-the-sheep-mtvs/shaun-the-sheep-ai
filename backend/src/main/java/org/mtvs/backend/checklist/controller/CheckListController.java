package org.mtvs.backend.checklist.controller;

import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.service.CheckListService;
import org.mtvs.backend.recommend.controller.RecommendController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.mtvs.backend.session.GuestData;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/checklist")
public class CheckListController {

    private final CheckListService service;
    private final RecommendController recommendController;

    public CheckListController(CheckListService service, RecommendController recommendController) {
        this.service = service;
        this.recommendController = recommendController;
    }

    /** 체크리스트 저장 */
    @PostMapping
    public CheckListResponse create(
            @RequestBody CheckListRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        //체크리스트 저장 (DB에 저장)
        CheckListResponse result = service.create(req, userDetails.getUsername());
        //recommend 컨트롤러에 전달 하여 추천 알고리즘 실행
        recommendController.diagnose(userDetails);

        return result;
    }

    /** 사용자의 모든 체크리스트 조회 */
    @GetMapping
    public List<CheckListResponse> findAll(@AuthenticationPrincipal CustomUserDetails customUserDetail) {
        return service.findAllForCurrentUser(customUserDetail.getUser().getUsername());
    }

    @GetMapping("/mbti")
    public String MBTIResponse(@AuthenticationPrincipal CustomUserDetails customUserDetail) {
        String email = customUserDetail.getUser().getEmail();
        log.debug("Getting skin type for user: {}", email);
        System.out.println(email);
        
        String MBTI = service.getSkinTypeByEmail(email);
        log.debug("MBTI: {}", MBTI);
        System.out.println(MBTI);

        return MBTI;
    }

    @GetMapping("/latest")
    public ResponseEntity<CheckListResponse> getLatest(@AuthenticationPrincipal CustomUserDetails me) {
        return service.findLatestForUser(me.getUsername())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(new CheckListResponse()));  // 빈 DTO( troubles = null or empty )
    }

    // /** 게스트 체크리스트 제출 */
    // @PostMapping("/guest")
    // public ResponseEntity<?> guestChecklist(@RequestBody GuestData guestData, HttpSession session) {
    //     // 1. MBTI 코드 계산
    //     String mbtiCode = service.calculateMbtiForGuest(guestData);
    //     // 2. SkinType 조회 (한글명 반환)
    //     String skinType = service.getSkinTypeForMbti(mbtiCode);

    //     // 3. (Optional) Save guest checklist to session
    //     session.setAttribute("guestChecklist", guestData);

    //     // 4. Call recommendation/diagnosis logic for guest
    //     recommendController.diagnoseGuest(guestData, mbtiCode, skinType, session);

    //     // 5. Log the result
    //     log.info("Guest checklist submitted: mbti={}, skinType={}", mbtiCode, skinType);

    //     // 6. Return result
    //     return ResponseEntity.ok(Map.of(
    //         "mbti", mbtiCode,
    //         "skinType", skinType
    //     ));
    // }
}








