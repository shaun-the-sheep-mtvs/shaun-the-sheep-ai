package org.mtvs.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.dto.AuthResponse;
import org.mtvs.backend.auth.dto.LoginRequest;
import org.mtvs.backend.auth.dto.SignupRequest;
import org.mtvs.backend.auth.service.AuthService;
import org.mtvs.backend.auth.util.JwtUtil;
import org.mtvs.backend.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * 회원가입
     * */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest dto) {
        log.info("[회원가입] 요청 : 이메일={}, 닉네임={}", dto.getEmail(), dto.getUsername());
        try {
            authService.signup(dto);
            log.info("[회원가입] 성공 : 이메일={}", dto.getEmail());
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            log.warn("[회원가입] 실패 : {}", e.getMessage());
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }

    /*
     * 로그인 - 액세스 토큰과 리프레시 토큰 반환
     * */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        log.info("[로그인] 요청 수신: 이메일={}", dto.getEmail());

        try {
            AuthResponse authResponse = authService.login(dto);
            log.info("[로그인] 성공 : 이메일={}", dto.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            log.warn("[로그인] 실패 : {}", e.getMessage());
            return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
        }
    }

    /*
     * 토큰 갱신 - 리프레시 토큰으로 새로운 액세스 토큰 발급
     * */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        log.info("[토큰 갱신] 요청 수신");

        try {
            // Bearer 토큰에서 실제 토큰 추출
            String token = refreshToken.startsWith("Bearer ") 
                    ? refreshToken.substring(7) 
                    : refreshToken;

            AuthResponse authResponse = authService.refreshToken(token);
            log.info("[토큰 갱신] 성공");
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            log.warn("[토큰 갱신] 실패 : {}", e.getMessage());
            return ResponseEntity.status(401).body("토큰 갱신 실패: " + e.getMessage());
        }
    }

    /*
     * 현재 사용자 정보 조회
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal org.mtvs.backend.auth.model.CustomUserDetails userDetails) {
        log.info("[현재 사용자 조회] 요청 수신");

        try {
            // CustomUserDetails에서 사용자 정보 추출
            var user = userDetails.getUser();
            
            // 응답 DTO 생성
            var response = new java.util.HashMap<String, Object>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            
            log.info("[현재 사용자 조회] 성공 : 이메일={}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("[현재 사용자 조회] 실패 : {}", e.getMessage());
            return ResponseEntity.status(401).body("사용자 정보 조회 실패: " + e.getMessage());
        }
    }
}