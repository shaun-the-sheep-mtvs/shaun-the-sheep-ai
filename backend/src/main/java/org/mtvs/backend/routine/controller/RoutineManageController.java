package org.mtvs.backend.routine.controller;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.routine.dto.GroupIdDTO;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.dto.RequestRoutineAllDTO;
import org.mtvs.backend.routine.dto.RoutineDTO;
import org.mtvs.backend.routine.service.RoutineManageService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/readAll")
    public ResponseEntity<List<RequestRoutineAllDTO>> readAllRoutines(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(routineManageService.getAllRoutines(user.getUser().getUsername()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoutine(@RequestBody GroupIdDTO id){
        routineManageService.deleteRoutine(id.getGroupId());
        return ResponseEntity.ok("Routine deleted successfully");
    }
}
