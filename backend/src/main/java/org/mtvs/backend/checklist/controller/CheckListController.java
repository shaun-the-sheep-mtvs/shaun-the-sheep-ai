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

@RestController
@RequestMapping("/api/checklists")
public class CheckListController {
    private final CheckListService service;

    public CheckListController(CheckListService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CheckListResponse> create(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody CheckListRequest req) {

        CheckListResponse created =
                service.create(user.getUsername(), req);

        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<CheckListResponse>> list(
            @AuthenticationPrincipal UserDetails user) {

        List<CheckListResponse> list =
                service.findAllForUser(user.getUsername());

        return ResponseEntity.ok(list);
    }
}

