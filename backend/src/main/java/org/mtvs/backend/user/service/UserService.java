package org.mtvs.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /* step2. 피부 정보 조회 */
    public ProblemDto loadUserSkinData(String userId) {
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
