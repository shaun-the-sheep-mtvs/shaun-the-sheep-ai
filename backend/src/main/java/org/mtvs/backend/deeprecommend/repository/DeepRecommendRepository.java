package org.mtvs.backend.deeprecommend.repository;

import org.mtvs.backend.deeprecommend.dto.RecommendResponseDTO;
import org.mtvs.backend.deeprecommend.entity.DeepRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeepRecommendRepository extends JpaRepository<DeepRecommend,Integer> {

    @Query("""
    SELECT new org.mtvs.backend.deeprecommend.dto.RecommendResponseDTO(
           d.action,
           d.kind,
           r.name,
           d.suggest_product,
           d.reason,
           d.routineGroupId
       )
       FROM DeepRecommend d
       LEFT JOIN Routine r on d.existingProductId = r.id
       WHERE d.routineGroupId = (
           SELECT MAX(rg.id)
           FROM RoutineGroup rg
           WHERE rg.userId = :userId
   )
""")
    List<RecommendResponseDTO> findLatestRecommendByUserId(@Param("userId") String userId);

//    @Query("""
//    SELECT new org.mtvs.backend.deeprecommend.dto.RecommendResponseDTO(
//        d.action,
//        d.kind,
//        r.routineName,
//        d.suggest_product,
//        d.reason,
//        d.routineGroupId
//    )
//    FROM DeepRecommend d
//    LEFT JOIN RoutineChange r on d.existingProductId = r.routineChangeId
//    WHERE r.routineGroupId = (
//        SELECT MAX(rg.id)
//        FROM RoutineGroup rg
//        WHERE rg.userId = :userId
//    )
//    """)
//    List<RecommendResponseDTO> findLatestRecommendByUserId(@Param("userId") Long userId);



}
