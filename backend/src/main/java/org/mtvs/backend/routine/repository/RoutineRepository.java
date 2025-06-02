package org.mtvs.backend.routine.repository;

import org.mtvs.backend.deeprecommend.dto.RoutineChangeDTO;
import org.mtvs.backend.deeprecommend.entity.RoutineChange;
import org.mtvs.backend.routine.dto.RequestRoutineAllDTO;
import org.mtvs.backend.routine.dto.RoutineDTO;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    @Query("SELECT  r.name , r.kind ,r.method , r.orders ,r.time  FROM Routine r WHERE r.user.id = :userId")
    List<RequestRoutineAllDTO> findAllRoutineDTOByUserId(@Param("userId") String userId);


    @Query("SELECT new org.mtvs.backend.routine.dto.RoutinesDto(r.id, r.name, r.kind, r.method, r.orders, r.time, r.routineGroupId) " +
            "FROM Routine r WHERE r.user.id = :userId")
    List<RoutinesDto> findAllRoutineByUserId(@Param("userId") String userId);


    List<Routine> findRoutinesByUser(User user);

    List<Routine> findRoutinesByRoutineGroupId(Long routineGroupId);
    @Query("SELECT new org.mtvs.backend.routine.dto.RoutinesDto(r.id, r.name, r.kind, r.method, r.orders, r.time, r.routineGroupId) " +
            "FROM Routine r " +
            "WHERE r.routineGroupId = (" +
            "   SELECT MAX(r2.routineGroupId) " +
            "   FROM Routine r2 " +
            "   WHERE r2.user.id = :userId" +
            ")")
    List<RoutinesDto> findRoutinesByUserId(@Param("userId") String userId);

    @Query("SELECT new org.mtvs.backend.routine.dto.RoutinesDto(r.id, r.name, r.kind, r.method, r.orders, r.time, r.routineGroupId) " +
            "FROM Routine r " +
            "WHERE r.routineGroupId IN (" +
            "   SELECT r2.routineGroupId " +
            "   FROM Routine r2 " +
            "   WHERE r2.user.id = :userId" +
            ")")
    List<RoutinesDto> findAllRoutinesByUserId(@Param("userId") String userId);

    Iterable<? extends Routine> findRoutinesByRoutineGroupIdAndUser(long groupId, User user);
}
