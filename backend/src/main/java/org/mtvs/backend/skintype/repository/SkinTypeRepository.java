package org.mtvs.backend.skintype.repository;

import org.mtvs.backend.skintype.entity.SkinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinTypeRepository extends JpaRepository<SkinType, Byte> {
    
    Optional<SkinType> findByEnglishName(String englishName);
    
    Optional<SkinType> findByKoreanName(String koreanName);
}