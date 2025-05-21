package org.mtvs.backend.deeprecommend.repository;
import org.apache.catalina.User;
import org.mtvs.backend.deeprecommend.dto.RoutineAnalysisDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Recommendrepository extends JpaRepository<User,Integer> {

    @Query("SELECT new org.mtvs.backend.deeprecommend.dto.RoutineAnalysisDTO(u.id, u.skinType, u.troubles, a.message) " +
            "FROM User u " +
            "JOIN Analy a " +
            "WHERE u.id = :userId")
    List<RoutineAnalysisDTO> findUserAnalysisByUserId(@Param("userId") int userId);

}
