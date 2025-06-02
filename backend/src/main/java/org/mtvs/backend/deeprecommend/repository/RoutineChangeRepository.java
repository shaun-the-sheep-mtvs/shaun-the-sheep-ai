package org.mtvs.backend.deeprecommend.repository;

import org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO;
import org.mtvs.backend.deeprecommend.entity.RoutineChange;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineChangeRepository extends JpaRepository<RoutineChange, Long> {

    @Query("SELECT new org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO(r.routineChangeId, r.routineName, r.routineKind, r.routineTime, r.routineOrders, r.changeMethod, r.routineGroupId) " +
            "FROM RoutineChange r " +
            "WHERE r.routineGroupId = (" +
            "   SELECT MAX(r2.id) " +
            "   FROM RoutineGroup r2 " +
            "   WHERE r2.userId = :userId" +
            ")")
    List<RoutineChangeDTO> findRoutinesByUserId(@Param("userId") String userId);
    @Query("""
    SELECT new org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO(
        r.routineChangeId,
        r.routineName,
        r.routineKind,
        r.routineTime,
        r.routineOrders,
        r.changeMethod,
        r.routineGroupId
    )
    FROM RoutineChange r
    WHERE r.routineGroupId IN (
        SELECT r2.id
        FROM RoutineGroup r2
        WHERE r2.userId = :userId
    )
""")
    List<RoutineChangeDTO> findAllRoutinesByUserId(@Param("userId") String userId);
}
