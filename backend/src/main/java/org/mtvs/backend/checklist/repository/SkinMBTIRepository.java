package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.checklist.model.SkinMBTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinMBTIRepository extends JpaRepository<SkinMBTI, Long> {
    boolean existsByMbtiCode(String mbtiCode);
} 