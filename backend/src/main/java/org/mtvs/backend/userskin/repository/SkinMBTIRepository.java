package org.mtvs.backend.userskin.repository;

import java.util.Optional;
import org.mtvs.backend.userskin.entity.MBTIList; // adjust the import as needed
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkinMBTIRepository extends JpaRepository<MBTIList, Byte> {
    Optional<MBTIList> findByMbtiCode(String mbtiCode);
}
