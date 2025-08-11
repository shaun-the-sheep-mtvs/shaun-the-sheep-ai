package org.mtvs.backend.routine.presentation;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.routine.domain.dto.request.RequestGroupIdDto;
import org.mtvs.backend.routine.domain.dto.request.RequestRoutinesListDto;
import org.mtvs.backend.routine.domain.dto.RoutinesDto;
import org.mtvs.backend.routine.application.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
public class RoutineController {

    private final RoutineService routineService;

    @Autowired
    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }



    @PostMapping("/create")
    public ResponseEntity<String> createRoutine(
            @RequestBody RequestRoutinesListDto routinesDTO,
            @AuthenticationPrincipal CustomUserDetails user) {

        routineService.createRoutine(routinesDTO, user.getUser().getUsername());
        return ResponseEntity.ok("Routine created successfully");
    }

    /* step2. 기존 루틴 조회 */
    @GetMapping("/existing")
    public ResponseEntity<?> existingRoutine(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        List<RoutinesDto> routinesDto = routineService.getRoutineList(user.getUser().getId());
        return ResponseEntity.ok(routinesDto);
    }

    @GetMapping("/all-existing")
    public ResponseEntity<?> AllexistingRoutine(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        List<RoutinesDto> routinesDto = routineService.getAllRoutineList(user.getUser().getId());
        return ResponseEntity.ok(routinesDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoutine(@RequestBody RequestGroupIdDto id, @AuthenticationPrincipal CustomUserDetails user) {
        routineService.deleteRoutine(id.getGroupId(),user);
        return ResponseEntity.ok("Routine deleted successfully");
    }
}
