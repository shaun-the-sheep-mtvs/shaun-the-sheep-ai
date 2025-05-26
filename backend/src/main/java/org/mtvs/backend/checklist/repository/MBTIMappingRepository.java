package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.checklist.model.MBTIMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MBTIMappingRepository extends JpaRepository<MBTIMapping, String> {
}
