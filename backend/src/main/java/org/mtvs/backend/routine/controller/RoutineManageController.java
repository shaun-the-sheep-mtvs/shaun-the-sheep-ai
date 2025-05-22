package org.mtvs.backend.routine.controller;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.service.AuthService;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.service.RoutineManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080/*")
@RestController("/")
public class RoutineManageController {


    private final RoutineManageService routineManageSerivce;
    private final AuthService userService;

    @Autowired
    public RoutineManageController(RoutineManageService routineManageSerivce,AuthService userService) {
        this.routineManageSerivce = routineManageSerivce;
        this.userService = userService;
    }
    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(SecurityContextHolder.getContext());
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        if (principal instanceof UserDetails userDetails) {
            System.out.println(userDetails.getUsername());
            return userService.getUserByLoginId(userDetails.getUsername())
                    .map(user -> user.getUsername()) // UUID 반환
                    .orElse(null);
        }
        return null;
    }

    private UserDetails getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    @PostMapping("/api/routine/create")
    public ResponseEntity<Integer> write(@RequestBody RequestJsonArrayRoutineDTO routinesDTO , @AuthenticationPrincipal CustomUserDetails user) {
        routineManageSerivce.createRoutine(routinesDTO,user.getUser().getUsername());
        return ResponseEntity.ok(200);
    }
/*

    // 사용자의 모든 루틴을 조회하는 엔드포인트
    @GetMapping("/api/routine/list")
    public ResponseEntity<List<RequestRoutineAllDTO>> getAllRoutines(@AuthenticationPrincipal User user) {
        System.out.println(user);
        List<RequestRoutineAllDTO> routines = routineManageSerivce.getAllRoutines(user);

        return ResponseEntity.ok(routines);
    }
*/

    /*
    *
    * */


}
