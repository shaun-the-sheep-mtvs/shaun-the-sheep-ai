package org.mtvs.backend.controller;


import org.mtvs.backend.dto.RequestCreateRoutineDTO;
import org.mtvs.backend.service.RoutineManageSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class RoutineManageController {
    private final RoutineManageSerivce routineManageSerivce;

    public RoutineManageController(RoutineManageSerivce routineManageSerivce) {
        this.routineManageSerivce = routineManageSerivce;
    }
    @PostMapping("/routine/write")
    public ResponseEntity<Integer> write(RequestCreateRoutineDTO requestCreateRoutineDTO) {
        routineManageSerivce.createRoutine(requestCreateRoutineDTO);
        return ResponseEntity.ok(200);
    }


}
