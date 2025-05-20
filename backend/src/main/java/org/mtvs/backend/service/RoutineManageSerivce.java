package org.mtvs.backend.service;


import org.mtvs.backend.dto.RequestCreateRoutineDTO;
import org.mtvs.backend.dto.RequestRoutineAllDTO;
import org.mtvs.backend.entity.Routine;
import org.mtvs.backend.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineManageSerivce {

    private final RoutineRepository routineRepository;

    public RoutineManageSerivce(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public void createRoutine(RequestCreateRoutineDTO requestCreateRoutineDTO) {
        Routine routine = new Routine(
                requestCreateRoutineDTO.getName(),
                requestCreateRoutineDTO.getTime(),
                requestCreateRoutineDTO.getKind(),
                requestCreateRoutineDTO.getMethod()
        );
        routineRepository.save(routine);
    }


}
