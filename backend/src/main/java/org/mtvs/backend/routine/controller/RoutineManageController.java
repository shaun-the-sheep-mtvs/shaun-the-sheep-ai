package org.mtvs.backend.routine.controller;

import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.service.RoutineManageSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080/*")
@RestController("/")
public class RoutineManageController {
    private final RoutineManageSerivce routineManageSerivce;

    public RoutineManageController(RoutineManageSerivce routineManageSerivce) {
        this.routineManageSerivce = routineManageSerivce;
    }

    @PostMapping("/api/routine/create")
    public ResponseEntity<Integer> write(@RequestBody RequestJsonArrayRoutineDTO routinesDTO , @AuthenticationPrincipal User user) {
        System.out.println(routinesDTO);
        routineManageSerivce.createRoutine(routinesDTO,user);
        return ResponseEntity.ok(200);
    }

    // 사용자의 모든 루틴을 조회하는 엔드포인트
    @GetMapping("/api/routine/list")
    public ResponseEntity<List<Routine>> getAllRoutines(@AuthenticationPrincipal User user) {
        List<Routine> routines = routineManageSerivce.getAllRoutines(user);
        return ResponseEntity.ok(routines);
    }

    /*
    *
    * */


}
