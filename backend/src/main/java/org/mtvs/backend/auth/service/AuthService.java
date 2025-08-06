package org.mtvs.backend.auth.service;

import java.util.Optional;

import org.mtvs.backend.auth.jwt.JwtUtil;
import org.mtvs.backend.auth.jwt.dto.AuthResponse;
import org.mtvs.backend.auth.jwt.dto.LoginRequest;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
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
	private final JwtUtil jwtUtil;

	/**
	 * 로그인 - 액세스 토큰과 리프레시 토큰 모두 반환
	 */
	public AuthResponse login(LoginRequest dto) {
		log.info("[로그인] 서비스 호출 : 사용자명={}", dto.getUsername());

		User user = userRepository.findByUsername(dto.getUsername())
			.orElseThrow(() -> {
				log.warn("[로그인] 실패 - 존재하지 않는 사용자 : 사용자명={}", dto.getUsername());
				return new RuntimeException("존재하지 않는 사용자입니다.");
			});

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			log.warn("[로그인] 실패 - 비밀번호 불일치 : 사용자명={}", dto.getUsername());
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}

		// 액세스 토큰과 리프레시 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getUsername(), user.getId());
		String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

		log.info("[로그인] 완료 : 사용자명={}", dto.getUsername());
		return new AuthResponse(accessToken, refreshToken);
	}

	/**
	 * 리프레시 토큰으로 새로운 액세스 토큰 발급
	 */
	public AuthResponse refreshToken(String refreshToken) {
		log.info("[토큰 갱신] 요청");

		try {
			// 리프레시 토큰 유효성 검증
			if (!jwtUtil.validateToken(refreshToken)) {
				log.warn("[토큰 갱신] 실패 - 유효하지 않은 리프레시 토큰");
				throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
			}

			// 리프레시 토큰에서 사용자 정보 추출
			String username = jwtUtil.getSubjectFromToken(refreshToken);

			// 사용자 존재 확인
			User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

			// 새로운 토큰 생성
			String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getUsername(), user.getId());
			String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

			log.info("[토큰 갱신] 완료 : 사용자명={}", username);
			return new AuthResponse(newAccessToken, newRefreshToken);

		} catch (Exception e) {
			log.error("[토큰 갱신] 오류 : {}", e.getMessage());
			throw new RuntimeException("토큰 갱신 중 오류가 발생했습니다.");
		}
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> getUserByLoginId(String loginId) {
		return userRepository.findByUsername(loginId);
	}

	public void deleteUser(CustomUserDetails user) {
		userRepository.delete(user.getUser());
	}
}
