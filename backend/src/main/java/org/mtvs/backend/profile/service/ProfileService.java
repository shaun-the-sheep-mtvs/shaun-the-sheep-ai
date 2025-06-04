package org.mtvs.backend.profile.service;


import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.product.dto.UserInfoDTO;
import org.mtvs.backend.profile.dto.PasswordDTO;
import org.mtvs.backend.profile.dto.ResponseProfileDTO;
import org.mtvs.backend.profile.dto.UsernameDTO;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public ProfileService(UserRepository userRepository, RoutineRepository routineRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseProfileDTO getProfile(CustomUserDetails user) {
        /*
        *   profile 정보
            String username;
            String email;
            List<String> troubles;
            List<RoutinesDto> routines;
        * */
        return new ResponseProfileDTO(
                user.getEmail(),
                user.getUsername(),
                user.getUser().getTroubles(),
                routineRepository.findRoutinesByUserId(user.getUserId()),
                user.getUser().getCreatedAt()
                );
    }

    public void updatePassword(PasswordDTO passwordDTO, CustomUserDetails userDetails) {
        userDetails.getUser().setPassword(passwordEncoder.encode(passwordDTO.confirmPassword()));
        userRepository.save(userDetails.getUser());
        log.info("[비밀번호 변경]");
    }


    public void updateUsername(UsernameDTO usernameDTO, CustomUserDetails userDetails) {
        userDetails.getUser().setUsername(usernameDTO.username());
        userRepository.save(userDetails.getUser());
        log.info("[유저 이름 변경]");
    }
}
