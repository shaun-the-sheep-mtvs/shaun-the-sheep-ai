package org.mtvs.backend.user.service;

import java.util.Optional;

import org.mtvs.backend.auth.jwt.dto.SignUpRequestDto;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.userskin.entity.Userskin;
import org.mtvs.backend.userskin.service.UserskinService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserskinService userskinService;

	public User signUp(SignUpRequestDto signUpRequestDto) {
		log.info("[회원 가입] 서비스 호출 : 이메일={}, 닉네임={}", dto.getEmail(), dto.getUsername());

		// 이메일 존재 여부 확인
		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			log.warn("[회원가입] 이미 존재하는 이메일 요청 : 이메일={}", dto.getEmail());
			throw new RuntimeException("이미 존재하는 이메일입니다.");
		}

		User user = new User(
			dto.getEmail(),
			passwordEncoder.encode(dto.getPassword()),
			dto.getUsername()
		);
		userRepository.save(user);

		log.info("[회원 가입] 완료 : 이메일={}, 닉네임={}", dto.getEmail(), dto.getUsername());
		return user;
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
}
