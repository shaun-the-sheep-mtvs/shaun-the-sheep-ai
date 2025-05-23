package org.mtvs.backend.analysis.service;

import org.mtvs.backend.analysis.domain.Routine;
import org.mtvs.backend.analysis.dto.RoutineRequestDto;
import org.mtvs.backend.analysis.repository.RoutineRepository;
import org.springframework.stereotype.Service;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;

    public RoutineService(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public RoutineRequestDto requestRoutine(int routineId) {
        Routine routine = routineRepository.findById(routineId).orElse(null);
        return new RoutineRequestDto(
                routine.getId(),
                routine.getName(),
                routine.getKind().getName(),
                routine.getTime().getName(),
                routine.getOrder(),
                routine.getMethod(),
                routine.getUser().getId()
        );
    }
}
