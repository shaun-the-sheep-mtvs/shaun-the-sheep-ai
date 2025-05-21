package org.mtvs.backend.routine.repository;

import org.mtvs.backend.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    @Query("SELECT  r.name , r.kind ,r.method , r.orders ,r.time  FROM Routine r WHERE r.user.id = :userId")
    List<Routine> findAllRoutineByUserId(@Param("userId") Long userId);


}
