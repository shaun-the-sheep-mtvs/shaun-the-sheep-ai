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
    boolean existsById(String id);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);  // 필요 시
    // boolean existsByPassword(String password);  // 삭제

    @Query("SELECT u FROM User u  WHERE u.username = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Optional<String> findUserIdByEmail(@Param("email") String email);
}


