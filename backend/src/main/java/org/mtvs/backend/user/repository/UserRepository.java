package org.mtvs.backend.user.repository;

import org.mtvs.backend.user.dto.ProblemDto;
import org.mtvs.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByEmailContaining(String email, Pageable pageable);

    @Query("SELECT u FROM User u  WHERE u.username = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    @Query("""
    SELECT u.skinType as skinType, u.troubles as troubles
    FROM User u
    WHERE u.id = :userId
""")
    ProblemDto getProblemByUserId();

    @Query("SELECT new org.mtvs.backend.user.dto.ProblemDto(u.skinType, u.troubles) " +
            "FROM User u WHERE u.id = :userId")
    ProblemDto findAllRoutineByUserId(@Param("userId") String userId);
}
