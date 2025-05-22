package org.mtvs.backend.checklist.controller;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.service.CheckListService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/checklist")
public class CheckListController {

    private final CheckListService service;

    public CheckListController(CheckListService service) {
        this.service = service;
    }

    /** 저장: Principal 로부터 username(email)을 꺼내서 서비스 호출 */
    @PostMapping
    public CheckListResponse create(
            @RequestBody CheckListRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return service.create(req, userDetails.getUsername());
    }

    @GetMapping
    public List<CheckListResponse> findAll(@AuthenticationPrincipal CustomUserDetails customUserDetail) {
        return service.findAllForCurrentUser(customUserDetail.getUser().getUsername());
    }
}








