package org.mtvs.backend.auth.controller;

import org.mtvs.backend.auth.dto.LoginDto;
import org.mtvs.backend.auth.dto.RegistrationDto;
import org.mtvs.backend.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto dto) {
        try {
            authService.register(dto);
            return ResponseEntity
                    .ok(Map.of("message", "회원가입 성공"));
        } catch (IllegalArgumentException ex) {
            // 중복 이메일 같은 예외는 400으로 응답
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
            // 또는 HTTP 409를 쓰고 싶으면:
            // return ResponseEntity
            //     .status(HttpStatus.CONFLICT)
            //     .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        log.info("●●● /auth/login 호출, payload = {}", dto);
        try {
            authService.login(dto);
            return ResponseEntity.ok(Map.of("message", "로그인 성공"));
        } catch (IllegalArgumentException ex) {
            log.warn("로그인 실패: {}", ex.getMessage());
            // 400 Bad Request 로 에러 메시지 전달
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
