package org.mtvs.backend.checklist.controller;

import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.service.CheckListService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/checklist")
public class CheckListController {

    private final CheckListService service;

    public CheckListController(CheckListService service) {
        this.service = service;
    }

    /** 체크리스트 저장 */
    @PostMapping
    public CheckListResponse create(
            @RequestBody CheckListRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return service.create(userDetails.getUsername(), req);
    }

    /** 사용자의 모든 체크리스트 조회 */
    @GetMapping
    public List<CheckListResponse> findAll(@AuthenticationPrincipal CustomUserDetails customUserDetail) {
        return service.findAllForUser(customUserDetail.getUser().getUsername());
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
}








