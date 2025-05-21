package org.mtvs.backend.routine.repository;

import org.mtvs.backend.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
}
