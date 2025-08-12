package org.mtvs.backend.user.controller;

import java.util.Map;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.dto.UserRegisterRequestDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.service.UserService;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	private final DefaultLifecycleProcessor defaultLifecycleProcessor;

	// 사용자 회원가입 (본인)
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserRegisterRequestDto userRegisterRequestDto) {
		log.info("[회원가입] 시작 - 이메일: {}, 사용자 이름: {}", userRegisterRequestDto.getEmail(),
			userRegisterRequestDto.getUsername());

		try {
			// 1. 입력값 검증
			if (userRegisterRequestDto.getUsername() == null || userRegisterRequestDto.getUsername().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("사용자 이름은 필수입니다.");
			}
			if (userRegisterRequestDto.getPassword() == null || userRegisterRequestDto.getPassword().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("비밀번호는 필수입니다.");
			}
			if (userRegisterRequestDto.getEmail() == null || userRegisterRequestDto.getEmail().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("이메일은 필수입니다.");
			}

			// 2. 서비스 호출
			User savedUSer = userService.register(userRegisterRequestDto);

			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
				"message", "회원가입이 완료되었습니다.",
				"user_id", savedUSer.getId(),
				"email", savedUSer.getEmail(),
				"username", savedUSer.getUsername()
			));

		} catch (IllegalArgumentException e) {
			log.warn("[회원가입] 비즈니스 예외: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			log.error("[회원가입] 시스템 오류: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("회원가입 중 오류가 발생했습니다.");
		}

	}

	// 사용자 정보 수정 (본인)
	@PutMapping("/me/username")
	public ResponseEntity<?> changeUsername(@RequestBody ChangeUsernameDto changeUsernameDto) {
		log.info("[사용자 닉네임 수정] 시작 - ");
		// 비밀번호 검증 로직
		// 현재 비밀번호 확인
		// 새 비밀번호 해시화
		// 세션 관리

	}

	@PutMapping("/me/password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
		// 닉네임 중복 검사
		// 단순 텍스트 업데이트
	}
	//
	// // 사용자 삭제 (본인)
	// @DeleteMapping("/profile")
	// public ResponseEntity<?> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
	//
	// }
	//
	// // 사용자 정보 조회 (본인)
	// @GetMapping("/profile")
	// public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
	//
	// }
	//
	// // 사용자 전체 조회 (관리자용)
	// @GetMapping("/list")
	// @PreAuthorize("hasRole('ADMIN')")
	// public ResponseEntity<?> getAllUsers(
	// 	@RequestParam(defaultValue = "0") int page,
	// 	@RequestParam(defaultValue = "10") int size) {
	// }
	//
	// // 사용자 단일 조회 (관리자용)
	// @GetMapping("/{userId}")
	// @PreAuthorize("hasRole('ADMIN')")
	// public ResponseEntity<?> getUserById(@PathVariable String userId) {
	//
	// }
	//
	// // 사용자 검색 (관리자용)
	// @GetMapping("/search")
	// @PreAuthorize("hasRole('ADMIN')")
	// public ResponseEntity<?> searchUsers(
	// 	@RequestParam(required = false) String username,
	// 	@RequestParam(required = false) String email,
	// 	@RequestParam(defaultValue = "0") int page,
	// 	@RequestParam(defaultValue = "10") int size) {
	//
	// }
	//
	// // 사용자 상태 변경 (관리자용)
	// @PatchMapping("/{userId}/status")
	// @PreAuthorize("hasRole('ADMIN')")
	// public ResponseEntity<?> updateUserStatus(
	// 	@PathVariable String userId,
	// 	@RequestBody UserStatusUpdateDto userStatusUpdateDto) {
	//
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
