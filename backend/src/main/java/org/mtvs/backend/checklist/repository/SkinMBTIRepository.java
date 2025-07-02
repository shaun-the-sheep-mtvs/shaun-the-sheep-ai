package org.mtvs.backend.checklist.repository;

import org.mtvs.backend.userskin.entity.MBTIList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinMBTIRepository extends JpaRepository<MBTIList, Long> {
    boolean existsByMbtiCode(String mbtiCode);
    Optional<MBTIList> findByMbtiCode(String mbtiCode);
} 