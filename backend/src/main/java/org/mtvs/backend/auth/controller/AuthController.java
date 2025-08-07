package org.mtvs.backend.auth.controller;

import java.util.HashMap;

import org.mtvs.backend.auth.jwt.dto.AuthResponse;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.service.AuthService;
import org.mtvs.backend.auth.social.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final KakaoLoginService kakaoLoginService;

	/**
	 * 📍 사용자 회원가입 (카카오톡)
	 */
	@GetMapping("/login/kakao")
	public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode,
		HttpServletResponse httpServletResponse) {
		try {
			AuthResponse authResponse = kakaoLoginService.kakaoLogin(accessCode, httpServletResponse);

			log.info("카카오 로그인 성공: 사용자 ID = {}", authResponse.getUser().getId());
			return ResponseEntity.ok(authResponse);

		} catch (Exception e) {
			log.error("카카오 로그인 실패: {}", e.getMessage());
			return ResponseEntity.status(400).body("카카오 로그인 실패: " + e.getMessage());
		}
	}

	/*
	 * 로그인 - 액세스 토큰과 리프레시 토큰 반환
	 * */
	// @PostMapping("/login")
	// public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
	// 	log.info("[로그인] 요청 수신: 사용자명={}", dto.getUsername());
	//
	// 	try {
	// 		AuthResponse authResponse = authService.login(dto);
	// 		log.info("[로그인] 성공 : 사용자명={}", dto.getUsername());
	// 		return ResponseEntity.ok(authResponse);
	// 	} catch (RuntimeException e) {
	// 		log.warn("[로그인] 실패 : {}", e.getMessage());
	// 		return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
	// 	}
	// }

	/*
	 * 토큰 갱신 - 리프레시 토큰으로 새로운 액세스 토큰 발급
	 * */
	// @PostMapping("/refresh")
	// public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
	// 	log.info("[토큰 갱신] 요청 수신");
	//
	// 	try {
	// 		// Bearer 토큰에서 실제 토큰 추출
	// 		String token = refreshToken.startsWith("Bearer ")
	// 			? refreshToken.substring(7)
	// 			: refreshToken;
	//
	// 		AuthResponse authResponse = authService.refreshToken(token);
	// 		log.info("[토큰 갱신] 성공");
	// 		return ResponseEntity.ok(authResponse);
	// 	} catch (RuntimeException e) {
	// 		log.warn("[토큰 갱신] 실패 : {}", e.getMessage());
	// 		return ResponseEntity.status(401).body("토큰 갱신 실패: " + e.getMessage());
	// 	}
	// }

	/*
	 * 현재 사용자 정보 조회
	 * */
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("[현재 사용자 조회] 요청 수신");

		try {
			// CustomUserDetails에서 사용자 정보 추출
			var user = userDetails.getUser();

			// 응답 DTO 생성
			var response = new HashMap<String, Object>();
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