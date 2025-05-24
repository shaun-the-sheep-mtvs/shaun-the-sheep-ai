package org.mtvs.backend.routine.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.dto.RequestRoutineAllDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.entity.RoutineGroup;
import org.mtvs.backend.routine.repository.RoutineGroupRepository;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<RequestRoutineAllDTO> getAllRoutines(User user) {
        return routineRepository.findAllRoutineDTOByUserId(user.getId());
    }
}
