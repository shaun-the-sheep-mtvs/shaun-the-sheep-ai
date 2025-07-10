package org.mtvs.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.dto.AuthResponse;
import org.mtvs.backend.auth.dto.LoginRequest;
import org.mtvs.backend.auth.dto.SignupRequest;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.util.JwtUtil;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.mtvs.backend.userskin.entity.Userskin;
import org.mtvs.backend.userskin.entity.MBTIList;
import org.mtvs.backend.userskin.entity.ConcernList;
import org.mtvs.backend.userskin.repository.UserskinRepository;
import org.mtvs.backend.userskin.repository.SkinMBTIRepository;
import org.mtvs.backend.userskin.repository.SkinConcernRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CheckListRepository checkListRepository;
    private final UserskinRepository userskinRepository;
    private final SkinMBTIRepository skinMBTIRepository;
    private final SkinConcernRepository skinConcernRepository;

    /**
     * 회원가입
     *
     * @return
     */
    public User signup(SignupRequest dto) {
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
                Map<String, Object> guestDataMap = dto.getGuestData();
                
                // Create CheckList for raw data (backwards compatibility)
                CheckList checkList = new CheckList();
                checkList.setUser(user);
                checkList.setMoisture(((Number) guestDataMap.get("moisture")).intValue());
                checkList.setOil(((Number) guestDataMap.get("oil")).intValue());
                checkList.setSensitivity(((Number) guestDataMap.get("sensitivity")).intValue());
                checkList.setTension(((Number) guestDataMap.get("tension")).intValue());
                
                @SuppressWarnings("unchecked")
                List<String> troubles = (List<String>) guestDataMap.get("troubles");
                if (troubles != null) {
                    checkList.setTroubles(troubles);
                }
                checkListRepository.save(checkList);
                
                // Create Userskin entity with proper relationships if enhanced data is available
                if (guestDataMap.get("mbtiId") != null && guestDataMap.get("concerns") != null) {
                    // Deactivate any existing active userskin
                    Optional<Userskin> existingUserskin = userskinRepository.findByUserAndIsActiveTrue(user);
                    existingUserskin.ifPresent(userskin -> {
                        userskin.setIsActive(false);
                        userskinRepository.save(userskin);
                    });
                    
                    // Get MBTI entity by ID
                    Byte mbtiId = ((Number) guestDataMap.get("mbtiId")).byteValue();
                    Optional<MBTIList> mbtiEntity = skinMBTIRepository.findById(mbtiId);
                    
                    if (mbtiEntity.isPresent()) {
                        Userskin userskin = new Userskin(user);
                        userskin.setSkinType(mbtiEntity.get());
                        
                        // Set concerns from guest data
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> concernsData = (List<Map<String, Object>>) guestDataMap.get("concerns");
                        if (concernsData != null && !concernsData.isEmpty()) {
                            List<ConcernList> concerns = new ArrayList<>();
                            for (Map<String, Object> concernData : concernsData) {
                                Byte concernId = ((Number) concernData.get("id")).byteValue();
                                Optional<ConcernList> concernEntity = skinConcernRepository.findById(concernId);
                                concernEntity.ifPresent(concerns::add);
                            }
                            
                            // Set up to 3 concerns
                            if (concerns.size() > 0) userskin.setConcern1(concerns.get(0));
                            if (concerns.size() > 1) userskin.setConcern2(concerns.get(1));
                            if (concerns.size() > 2) userskin.setConcern3(concerns.get(2));
                        }
                        
                        userskinRepository.save(userskin);
                        log.info("[회원 가입] 게스트 Userskin 데이터 마이그레이션 완료 : 이메일={}, MBTI ID={}", 
                            dto.getEmail(), mbtiId);
                    }
                }
                
                log.info("[회원 가입] 게스트 데이터 마이그레이션 완료 : 이메일={}", dto.getEmail());
            } catch (Exception e) {
                log.error("[회원 가입] 게스트 데이터 마이그레이션 실패 : 이메일={}, error={}", dto.getEmail(), e.getMessage());
                e.printStackTrace();
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
