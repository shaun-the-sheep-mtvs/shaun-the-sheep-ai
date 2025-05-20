package org.mtvs.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class ManageRoutineController {


    @GetMapping("/routine/read")
    public ResponseEntity<Integer> read() {
        return ResponseEntity.ok(200);
    }

    @PostMapping("/routine/write")
    public ResponseEntity<Integer> write() {
        return ResponseEntity.ok(200);
    }

    @
}
