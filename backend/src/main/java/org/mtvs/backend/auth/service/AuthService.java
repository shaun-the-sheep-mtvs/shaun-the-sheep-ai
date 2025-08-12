package org.mtvs.backend.auth.service;

import javax.naming.AuthenticationException;

import org.mtvs.backend.auth.jwt.JwtProvider;
import org.mtvs.backend.auth.jwt.dto.AuthResponse;
import org.mtvs.backend.auth.jwt.dto.LoginRequestDto;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

	// 직접 userRepository를 호출하는게 아니라 UserService로 해주는게 왜 나은 거 같지?
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final JwtProvider jwtProvider;

	/**
	 * 📍 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
	 * @param loginRequestDto 로그인 요청 정보 (이메일, 비밀번호 포함)
	 * @return AuthResponse 로그인 성공 시 사용자 정보 및 JWT 토큰
	 * @throws IllegalArgumentException 로그인 요청 정보가 유효하지 않은 경우
	 * @throws AuthenticationException 이메일 또는 비밀번호가 일치하지 않는 경우
	 * @throws RuntimeException JWT 토큰 생성에 실패한 경우
	 */
	public AuthResponse login(LoginRequestDto loginRequestDto) {

		if (loginRequestDto == null) {
			throw new IllegalArgumentException("로그인 요청 정보는 필수입니다");
		}
		if (loginRequestDto.getEmail() == null || loginRequestDto.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다.");
		}
		if (loginRequestDto.getPassword() == null || loginRequestDto.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다.");
		}

		log.info("[로그인] 시작 : 사용자 이메일: {}", loginRequestDto.getEmail());

		try {
			// 1. 사용자 조회
			User user = userService.findByEmail(loginRequestDto.getEmail());

			// 2. 비밀번호 확인
			if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
				log.warn("[로그인] 실패 - 비밀번호 불일치 : 사용자 이메일 :{}", loginRequestDto.getEmail());
				throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
			}
			// 3. 액세스 토큰과 리프레시 토큰 생성
			String accessToken = jwtProvider.createAccessToken(user);
			String refreshToken = jwtProvider.createRefreshToken(user);

			log.info("[로그인] 완료 : 사용자 이메일 :{}", user.getEmail());

			// 4. 응답 생성
			return AuthResponse.builder()
				.user(user)
				.message("일반 로그인 성공")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			log.error("[로그인] 실패 - 시스템 오류 : 사용자 이메일: {}, 오류: {}", loginRequestDto.getEmail(), e.getMessage());
			throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 리프레시 토큰으로 새로운 액세스 토큰 발급
	 */
	// public AuthResponse refreshToken(String refreshToken) {
	// 	log.info("[토큰 갱신] 요청");
	//
	// 	try {
	// 		// 리프레시 토큰 유효성 검증
	// 		if (!jwtUtil.validateToken(refreshToken)) {
	// 			log.warn("[토큰 갱신] 실패 - 유효하지 않은 리프레시 토큰");
	// 			throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
	// 		}
	//
	// 		// 리프레시 토큰에서 사용자 정보 추출
	// 		String username = jwtUtil.getSubjectFromToken(refreshToken);
	//
	// 		// 사용자 존재 확인
	// 		User user = userRepository.findByUsername(username)
	// 			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	//
	// 		// 새로운 토큰 생성
	// 		String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getUsername(), user.getId());
	// 		String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
	//
	// 		log.info("[토큰 갱신] 완료 : 사용자명={}", username);
	// 		return new AuthResponse(newAccessToken, newRefreshToken);
	//
	// 	} catch (Exception e) {
	// 		log.error("[토큰 갱신] 오류 : {}", e.getMessage());
	// 		throw new RuntimeException("토큰 갱신 중 오류가 발생했습니다.");
	// 	}
	// }
	// public Optional<User> getUserByEmail(String email) {
	// 	return userRepository.findByEmail(email);
	// }
	//
	// public Optional<User> getUserByLoginId(String loginId) {
	// 	return userRepository.findByUsername(loginId);
	// }
	public void deleteUser(CustomUserDetails user) {
		userRepository.delete(user.getUser());
	}
}
