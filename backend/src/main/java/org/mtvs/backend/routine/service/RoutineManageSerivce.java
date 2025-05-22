package org.mtvs.backend.routine.service;

import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.dto.RequestRoutineAllDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineManageSerivce {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    @Autowired
    public RoutineManageSerivce(RoutineRepository routineRepository, UserRepository userRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
    }

    public void createRoutine(RequestJsonArrayRoutineDTO routinesDTO, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        routinesDTO.getRoutines()
                .forEach(routineDTO -> {
                                Routine routine = new Routine(
                                                    routineDTO.getName(),
                                                    routineDTO.getTime(),
                                                    routineDTO.getKind(),
                                                    routineDTO.getMethod(),
                                                    routineDTO.getOrders(),
                                                    user
                                                    );
                                routineRepository.save(routine);
                                }
                );
    }

    // 사용자의 모든 루틴을 조회하는 메소드
    public List<RequestRoutineAllDTO> getAllRoutines(User user) {
        return routineRepository.findAllRoutineDTOByUserId(user.getId());
    }
}
