package org.mtvs.backend.analysis.repository;

import org.mtvs.backend.analysis.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
}
