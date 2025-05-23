// src/main/java/org/mtvs/backend/checklist/repository/CheckListRepository.java
package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {
    List<CheckList> findByUserOrderByCreatedAtDesc(User user);
}

