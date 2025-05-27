package org.mtvs.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import org.mtvs.backend.auth.dto.LoginDto;
import org.mtvs.backend.auth.dto.ProblemDto;
import org.mtvs.backend.auth.dto.RegistrationDto;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.auth.util.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public RegistrationDto registerUser(RegistrationDto dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 순서 잘 지키기
        User user = new User(
                dto.getUsername(),
                dto.getEmail(),// 이메일 위치 수정
                passwordEncoder.encode(dto.getPassword()),  // 비밀번호 위치 수정
                LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);
        return new RegistrationDto(savedUser.getUsername(), savedUser.getEmail(), savedUser.getPassword());
    }

    public String[] loginUser(LoginDto userLoginDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userLoginDTO.getEmail());

        // 이메일로 이미 존재하는 회원인지 확인
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User does not exist");
        }

        // 로그인 시 비밀번호가 일치하지 않을 경우 예외를 던져 인증 실패
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 로그인 시 비밀번호가 일치할 경우 각각 액세스 토큰과 리프레시 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(existingUser.get().getEmail(), existingUser.get().getUsername(),existingUser.get().getId());
        String refreshToken = jwtUtil.generateRefreshToken(existingUser.get().getEmail());

        // 두 토큰을 클라이언트에 반환
        return new String[]{accessToken, refreshToken};
    }

    public String refreshToken (String refreshToken) {

        // 리프레시 토큰이 유효한지 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        try {
            // 클레임이란?
            Claims claims = jwtUtil.parseClaims(refreshToken);

            // 토큰에서 사용자 정보 추출
            String userEmail = claims.getSubject();
            String username = claims.get("username", String.class);
            long id = claims.get("id", Long.class);
            if (userEmail == null || userEmail.isEmpty()) {
                throw new IllegalArgumentException("Invalid refresh token: no user identifier found");
            }
            // 리프레시 토큰이 유효하면
            return jwtUtil.generateAccessToken(userEmail, username,id);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token has expired", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing refresh token", e);
        }
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email)
                );
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));
    }

    public Optional<User> getUserByLoginId(String loginId) {
        return userRepository.findByUsername(loginId);
    }

    /* step2. 피부 정보 조회 */
    public ProblemDto loadUserSkinData(Long userId) {
        ProblemDto skinData = userRepository.findUserSkinDataByUserId(userId);
        if (skinData == null) {
            throw new RuntimeException("피부 정보가 존재하지 않습니다. : " + userId);
        }

        return new ProblemDto(
                skinData.getSkinType(),
                skinData.getTroubles()
        );
    }
}
