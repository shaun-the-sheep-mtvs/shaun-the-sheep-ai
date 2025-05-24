package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {
    List<CheckList> findByUserOrderByCreatedAtDesc(User user);
    
    // Spring Data JPA 메소드 네이밍 컨벤션 사용
    Optional<CheckList> findFirstByUser_IdOrderByCreatedAtDesc(Long userId);
}
