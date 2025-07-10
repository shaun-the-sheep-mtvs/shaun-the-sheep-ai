package org.mtvs.backend.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.dto.AuthResponse;
import org.mtvs.backend.auth.dto.LoginRequest;
import org.mtvs.backend.auth.dto.SignupRequest;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.util.JwtUtil;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.mtvs.backend.session.GuestData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CheckListRepository checkListRepository;

    /**
     * 회원가입
     *
     * @return
     */
    public User signup(SignupRequest dto, GuestData guestData) {
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

        // Handle guest data from request if present
        if (dto.getGuestData() != null) {
            try {
                CheckList checkList = new CheckList();
                checkList.setUser(user);
                
                // Extract data from guestData map
                Map<String, Object> guestDataMap = dto.getGuestData();
                checkList.setMoisture(((Number) guestDataMap.get("moisture")).intValue());
                checkList.setOil(((Number) guestDataMap.get("oil")).intValue());
                checkList.setSensitivity(((Number) guestDataMap.get("sensitivity")).intValue());
                checkList.setTension(((Number) guestDataMap.get("tension")).intValue());
                
                // Handle troubles list
                @SuppressWarnings("unchecked")
                List<String> troubles = (List<String>) guestDataMap.get("troubles");
                if (troubles != null) {
                    checkList.setTroubles(troubles);
                }
                
                checkListRepository.save(checkList);
                log.info("[회원 가입] 게스트 데이터 마이그레이션 완료 : 이메일={}", dto.getEmail());
            } catch (Exception e) {
                log.error("[회원 가입] 게스트 데이터 마이그레이션 실패 : 이메일={}, error={}", dto.getEmail(), e.getMessage());
                // Don't throw exception - allow signup to complete even if guest data migration fails
            }
        }

        log.info("[회원 가입] 완료 : 이메일={}, 닉네임={}", dto.getEmail(), dto.getUsername());
        return user;
    }

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
