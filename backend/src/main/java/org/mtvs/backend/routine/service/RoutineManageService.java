package org.mtvs.backend.routine.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.routine.dto.*;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.entity.RoutineGroup;
import org.mtvs.backend.routine.repository.RoutineGroupRepository;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineManageService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineGroupRepository routineGroupRepository;

    @Transactional
    public void createRoutine(RequestJsonArrayRoutineDTO routinesDTO, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        RoutineGroup routineGroup = new RoutineGroup(
                user.getId()
        );

        routineGroupRepository.save(routineGroup);
        List<Routine> routines = routinesDTO.getRoutines().stream()
                .map(routineDTO -> new Routine(
                        routineDTO.getName(),
                        routineDTO.getTime(),
                        routineDTO.getKind(),
                        routineDTO.getMethod(),
                        routineDTO.getOrders(),
                        user,
                        routineGroup.getId()

                ))
                .collect(Collectors.toList());
        // 루틴 저장
        routineRepository.saveAll(routines);
    }

    // 사용자의 모든 루틴을 조회하는 메소드
    @Transactional
    public List<RequestRoutineAllDTO> getAllRoutines(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<RequestRoutineAllDTO> dtos = new ArrayList<>();
        routineRepository.findRoutinesByUser(user).forEach(routine -> {
            dtos.add(new RequestRoutineAllDTO(routine));
        });
        return dtos;
    }

    /* step2. 기존 루틴 조회 */
    public List<RoutinesDto> getRoutineList(String userId) {
        return routineRepository.findRoutinesByUserId(userId);
    }
    public List<RoutinesDto> getAllRoutineList(String userId) {
        return routineRepository.findAllRoutinesByUserId(userId);
    }

    public void deleteRoutine(long groupId, CustomUserDetails userDetails) {
        routineRepository.deleteAll(routineRepository.findRoutinesByRoutineGroupIdAndUser(groupId,userDetails.getUser())
        );
    }

    public void updateRoutine(Long routineGroupId , CustomUserDetails userDetails,List<RoutinesDto> routinesDtos) {

        List<Routine> routines =routineRepository.getRoutinesByRoutineGroupId(routineGroupId);
        for(Routine routine : routines){
        }
        routineRepository.saveAll(routines);
    }
}
