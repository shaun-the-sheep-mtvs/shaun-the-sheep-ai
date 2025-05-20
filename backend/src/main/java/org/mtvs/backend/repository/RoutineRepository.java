package org.mtvs.backend.repository;

import org.mtvs.backend.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
}
