// src/main/java/org/mtvs/backend/checklist/controller/CheckListController.java
package org.mtvs.backend.checklist.controller;

import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.service.CheckListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// src/main/java/org/mtvs/backend/checklist/controller/CheckListController.java
@RestController
@RequestMapping("/api/checklist")
public class CheckListController {
    private final CheckListService service;
    public CheckListController(CheckListService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CheckListResponse> create(
            @RequestBody CheckListRequest req) {
        // @AuthenticationPrincipal 삭제
        CheckListResponse created = service.create(
                req.getUsername(),  // username 으로 저장
                req
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<CheckListResponse>> list(
            @RequestParam String username) {
        // 조회 역시 쿼리 파라미터로 username 받아서
        List<CheckListResponse> list = service.findAllForUser(username);
        return ResponseEntity.ok(list);
    }
}





