package org.mtvs.backend.user.service;

import java.util.Optional;

import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.dto.UserRegisterRequestDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.userskin.entity.Userskin;
import org.mtvs.backend.userskin.service.UserskinService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final UserskinService userskinService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User register(UserRegisterRequestDto userRegisterRequestDto) {
		// 1. 비즈니스 규칙 검증
		validateEmailFormat(userRegisterRequestDto.getEmail());
		validatePasswordStrength(userRegisterRequestDto.getPassword());
		validateUsernameFormat(userRegisterRequestDto.getUsername());

		// 2. 중복 확인
		if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
			log.warn("[회원가입] 중복 이메일: {}", userRegisterRequestDto.getEmail());
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}
		if (userRepository.existsByUsername(userRegisterRequestDto.getUsername())) {
			log.warn("[회원가입] 중복 사용자 이름: {}", userRegisterRequestDto.getUsername());
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
		}

		User user = User.builder()
			.email(userRegisterRequestDto.getEmail())
			.username(userRegisterRequestDto.getUsername())
			.password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
			.build();

		User savedUser = userRepository.save(user);

		log.info("[회원 가입] 완료 : 이메일: {}, 사용자 이름: {}", userRegisterRequestDto.getEmail(),
			userRegisterRequestDto.getUsername());

		return savedUser;
	}

	private void validateEmailFormat(String email) {
		if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
		}
	}

	private void validatePasswordStrength(String password) {
		if (password.length() < 8) {
			throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
		}
		if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
			throw new IllegalArgumentException("비밀번호는 대문자, 소문자, 숫자를 포함해야 합니다.");
		}
	}

	private void validateUsernameFormat(String username) {
		if (username.length() < 2 || username.length() > 20) {
			throw new IllegalArgumentException("사용자명은 2-20자 사이여야 합니다.");
		}
		if (!username.matches("^[a-zA-Z0-9가-힣]+$")) {
			throw new IllegalArgumentException("사용자명은 영문, 숫자, 한글만 사용 가능합니다.");
		}
	}

	/**
	 * 📍 사용자의 피부 타입 조회
	 * @param userId
	 * @return
	 */
	public ProblemDto loadUserSkinData(String userId) {
		Optional<Userskin> userskinOpt = userskinService.getActiveUserskinByUserId(userId);
		if (userskinOpt.isEmpty()) {
			throw new RuntimeException("피부 정보가 존재하지 않습니다. : " + userId);
		}

		Userskin userskin = userskinOpt.get();
		return new ProblemDto(
			userskinService.getSkinTypeString(userskin),
			userskinService.getConcernLabels(userskin)
		);
	}

	public User findByEmail(String email) {
		Optional<User> userOpt = userRepository.findByEmail(email);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("사용자를 찾을 수 없습니다.");
		}
		User user = userOpt.get();
		return user;
	}
}
