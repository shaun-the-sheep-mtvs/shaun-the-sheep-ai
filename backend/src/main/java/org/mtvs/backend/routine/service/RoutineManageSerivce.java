package org.mtvs.backend.routine.service;

import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineManageSerivce {

    private final RoutineRepository routineRepository;

    public RoutineManageSerivce(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void createRoutine(RequestJsonArrayRoutineDTO routinesDTO, User user) {
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
    public List<Routine> getAllRoutines(User user) {
        return routineRepository.findAllRoutineByUserId(user.getId());
    }
}
