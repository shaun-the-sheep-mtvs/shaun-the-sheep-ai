package org.mtvs.backend.userskin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mtvs.backend.userskin.entity.ConcernList; // adjust the import as needed

import java.util.Optional;

public interface SkinConcernRepository extends JpaRepository<ConcernList, Byte> {
    Optional<ConcernList> findByLabel(String label);
    Optional<ConcernList> findByDescription(String description);
}
