package org.mtvs.backend.routine.repository;

import org.mtvs.backend.routine.dto.RoutineGroupDTO;
import org.mtvs.backend.routine.entity.RoutineGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoutineGroupRepository extends JpaRepository<RoutineGroup, Long> {

    @Query("SELECT new org.mtvs.backend.routine.dto.RoutineGroupDTO(r.id, r.userId) " +
            "FROM RoutineGroup r " +
            "WHERE r.id = (" +
            "   SELECT MAX(r2.id) FROM RoutineGroup r2 WHERE r2.userId = :userId" +
            ")")
    RoutineGroupDTO findLatestRoutineGroup(@Param("userId") String userId);
}
