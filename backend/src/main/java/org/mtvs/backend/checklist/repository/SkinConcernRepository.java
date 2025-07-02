package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.userskin.entity.ConcernList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinConcernRepository extends JpaRepository<ConcernList, Long> {
    boolean existsByLabel(String label);
    Optional<ConcernList> findByLabel(String label);
    Optional<ConcernList> findByDescription(String description);
    Optional<ConcernList> findByLabelOrDescription(String label, String description);
} 