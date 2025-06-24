package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.checklist.model.SkinConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinConcernRepository extends JpaRepository<SkinConcern, Long> {
    boolean existsByLabel(String label);
} 