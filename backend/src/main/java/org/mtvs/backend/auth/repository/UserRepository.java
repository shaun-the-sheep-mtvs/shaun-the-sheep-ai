package org.mtvs.backend.auth.repository;

import org.mtvs.backend.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);  // 필요 시
    // boolean existsByPassword(String password);  // 삭제
}
