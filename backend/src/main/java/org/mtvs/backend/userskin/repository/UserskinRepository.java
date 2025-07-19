package org.mtvs.backend.userskin.repository;

import org.mtvs.backend.userskin.entity.Userskin;
import org.mtvs.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserskinRepository extends JpaRepository<Userskin, Long> {
    
    Optional<Userskin> findByUserAndIsActiveTrue(User user);
    
    Optional<Userskin> findByUserIdAndIsActiveTrue(String userId);
    
    @Query("SELECT u FROM Userskin u WHERE u.user.id = :userId AND u.isActive = true ORDER BY u.analysisDate DESC")
    Optional<Userskin> findLatestActiveByUserId(@Param("userId") String userId);
    
    boolean existsByUserAndIsActiveTrue(User user);
}