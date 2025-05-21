package org.mtvs.backend.routine.service;


import org.mtvs.backend.routine.dto.RequestJsonArrayRoutineDTO;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.stereotype.Service;

@Service
public class RoutineManageSerivce {

    private final RoutineRepository routineRepository;

    public RoutineManageSerivce(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void createRoutine(RequestJsonArrayRoutineDTO routinesDTO) {
        routinesDTO.getRoutines()
                .forEach(routineDTO -> {
                                Routine routine = new Routine(
                                                    routineDTO.getName(),
                                                    routineDTO.getTime(),
                                                    routineDTO.getKind(),
                                                    routineDTO.getMethod()
                                                    );
                                routineRepository.save(routine);
                                }
                );
    }

}
