package org.mtvs.backend.routine.controller;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.routine.dto.*;
import org.mtvs.backend.routine.service.RoutineManageService;
import org.mtvs.backend.user.dto.ProblemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class RoutineManageController {

    private final RoutineManageService routineManageService;

    @Autowired
    public RoutineManageController(RoutineManageService routineManageService) {
        this.routineManageService = routineManageService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRoutine(
            @RequestBody RequestJsonArrayRoutineDTO routinesDTO,
            @AuthenticationPrincipal CustomUserDetails user) {

        routineManageService.createRoutine(routinesDTO, user.getUser().getUsername());
        return ResponseEntity.ok("Routine created successfully");
    }

    /* step2. 기존 루틴 조회 */
    @GetMapping("/existing")
    public ResponseEntity<?> existingRoutine(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<RoutinesDto> routinesDto = routineManageService.getRoutineList(user.getUser().getId());
        return ResponseEntity.ok(routinesDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoutine(@RequestBody GroupIdDTO id){
        routineManageService.deleteRoutine(id.getGroupId());
        return ResponseEntity.ok("Routine deleted successfully");
    }
}
