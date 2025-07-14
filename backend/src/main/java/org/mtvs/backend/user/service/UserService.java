package org.mtvs.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.userskin.service.UserskinService;
import org.mtvs.backend.userskin.entity.Userskin;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserskinService userskinService;

    /* step2. 피부 정보 조회 */
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
