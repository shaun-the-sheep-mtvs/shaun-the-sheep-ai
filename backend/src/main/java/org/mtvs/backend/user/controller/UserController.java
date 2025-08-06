package org.mtvs.backend.user.controller;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// /**
	//  * 📍사용자 회원가입 (일반)
	//  * @param signUpRequestDto 일반 회원
	//  * */
	// @PostMapping("/signup")
	// public ResponseEntity<?> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
	// 	log.info("[회원가입] 요청 : 이메일={}, 닉네임={}", signUpRequestDto.getEmail(), signUpRequestDto.getUsername());
	// 	try {
	// 		userService.signUp(signUpRequestDto);
	// 		log.info("[회원가입] 성공 : 이메일={}", signUpRequestDto.getEmail());
	// 		return ResponseEntity.ok("회원가입 성공");
	// 	} catch (RuntimeException e) {
	// 		log.warn("[회원가입] 실패 : {}", e.getMessage());
	// 		return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
	// 	}
	// }

	/* step2. 피부 정보 조회 */
	@GetMapping("/skin-data")
	public ResponseEntity<?> getSkinData(@AuthenticationPrincipal CustomUserDetails user) {
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		ProblemDto problemDto = userService.loadUserSkinData(user.getUser().getId());
		if (problemDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("피부 정보가 없습니다.");
		}

		return ResponseEntity.ok(problemDto);
	}
}
