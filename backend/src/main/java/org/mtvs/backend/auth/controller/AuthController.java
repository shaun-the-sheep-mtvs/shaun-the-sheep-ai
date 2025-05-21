package org.mtvs.backend.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mtvs.backend.auth.dto.AuthResponse;
import org.mtvs.backend.auth.dto.CurrentUserDto;
import org.mtvs.backend.auth.dto.LoginDto;
import org.mtvs.backend.auth.dto.RegistrationDto;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            // 1) 회원 생성
            authService.registerUser(dto);

            // 2) 바로 로그인 처리해서 토큰 발급
            //    (loginUser는 [accessToken, refreshToken] 순으로 반환)
            String[] tokens = authService.loginUser(
                    new LoginDto(dto.getEmail(), dto.getPassword())
            );

            // 3) AuthResponse DTO로 액세스/리프레시 토큰을 body에 담아 리턴
            return ResponseEntity.ok(new AuthResponse(tokens[0], tokens[1]));

        } catch (IllegalArgumentException ex) {
            // 중복 이메일 등 validation 예외
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

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> me(Authentication authentication) {
        // authentication.getPrincipal() 이 UserDetails 구현체여야 합니다.
        String email = authentication.getName();  // 보통 sub 또는 username
        User user    = authService.findByEmail(email);
        // DTO 로 노출할 정보만 담아 반환
        CurrentUserDto dto = new CurrentUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        return ResponseEntity.ok(dto);
    }
}
