package org.mtvs.backend.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mtvs.backend.auth.dto.AuthResponse;
import org.mtvs.backend.auth.dto.LoginDto;
import org.mtvs.backend.auth.dto.RegistrationDto;
import org.mtvs.backend.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
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

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto dto) {
        try {
            authService.registerUser(dto);
            return ResponseEntity
                    .ok(Map.of("message", "회원가입 성공"));
        } catch (IllegalArgumentException ex) {
            // 중복 이메일 같은 예외는 400으로 응답
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {

        String[] tokens = authService.loginUser(dto);
        return ResponseEntity.ok(new AuthResponse(tokens[0], tokens[1]));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // 1) 클라이언트 쿠키를 즉시 만료시켜 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", deleteCookie.toString());

        // 2) (선택) authService.invalidateRefreshToken(...) 같은 로직 호출

        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
}
