package org.mtvs.backend.profile.controller;


import org.apache.coyote.Response;
import org.mtvs.backend.auth.controller.AuthController;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.service.AuthService;
import org.mtvs.backend.profile.dto.PasswordDTO;
import org.mtvs.backend.profile.dto.ResponseProfileDTO;
import org.mtvs.backend.profile.dto.UsernameDTO;
import org.mtvs.backend.profile.service.ProfileService;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.routine.service.RoutineManageService;
import org.mtvs.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/profile")  // RequestMapping 추가
public class ProfileController {
    ProfileService profileService;
    AuthService authService;
    RoutineManageService routineManageService;

    @Autowired
    public ProfileController(ProfileService profileService, AuthService authService, RoutineManageService routineManageService) {
        this.authService = authService;
        this.profileService = profileService;
        this.routineManageService = routineManageService;
    }

/*
*   완성!!
* */
    @GetMapping
    public ResponseEntity<ResponseProfileDTO> getProfile(@AuthenticationPrincipal CustomUserDetails user) {
        ResponseProfileDTO responseDTO = profileService.getProfile(user);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordDTO passwordDTO, @AuthenticationPrincipal CustomUserDetails user) {
        profileService.updatePassword(passwordDTO,user);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/username")
    public ResponseEntity<?> updateUserName(@RequestBody UsernameDTO usernameDTO, @AuthenticationPrincipal CustomUserDetails user){
        profileService.updateUsername(usernameDTO,user);
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/routines/update")
    public ResponseEntity<?> updateRoutines(@RequestBody RoutinesDto routinesDTO,@AuthenticationPrincipal CustomUserDetails user){
//        routineManageService.updateRoutine(routinesDTO.getRoutineGroupId(),user);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/routines/delete")
    public ResponseEntity<?> deleteRoutines(@RequestBody RoutinesDto routinesDTO,@AuthenticationPrincipal CustomUserDetails user){
        routineManageService.deleteRoutine(routinesDTO.getRoutineGroupId(),user);
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> userRoutine(@AuthenticationPrincipal CustomUserDetails user){
        authService.deleteUser(user);
        return ResponseEntity.ok("success");
    }
}
