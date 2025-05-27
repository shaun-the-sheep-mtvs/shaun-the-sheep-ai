package org.mtvs.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* step2. 피부 정보 조회 */
    @GetMapping("/skin-data")
    public ResponseEntity<?> getSkinData(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        ProblemDto problemDto = userService.loadUserSkinData(user.getUser().getId());
        if (problemDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("피부 정보가 없습니다.");
        }

        return ResponseEntity.ok(problemDto);
    }
}
