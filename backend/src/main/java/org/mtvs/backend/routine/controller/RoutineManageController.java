package org.mtvs.backend.routine.controller;


import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.service.RoutineManageSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@CrossOrigin("*")
@RestController("/")
public class RoutineManageController {
    private final RoutineManageSerivce routineManageSerivce;

    public RoutineManageController(RoutineManageSerivce routineManageSerivce) {
        this.routineManageSerivce = routineManageSerivce;
    }
    @PostMapping("/api/routine/create")
    public ResponseEntity<Integer> write(@RequestBody RequestJsonArrayRoutineDTO routinesDTO) {
        System.out.println(routinesDTO);
        routineManageSerivce.createRoutine(routinesDTO);
        return ResponseEntity.ok(200);
    }


}
