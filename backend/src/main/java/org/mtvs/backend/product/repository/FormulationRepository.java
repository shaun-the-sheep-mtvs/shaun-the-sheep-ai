package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.entity.Formulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FormulationRepository extends JpaRepository<Formulation, Byte> {
    
    Optional<Formulation> findByEnglishName(String englishName);
    
    Optional<Formulation> findByKoreanName(String koreanName);
    
    @Query("SELECT f.id FROM Formulation f WHERE f.englishName = :englishName")
    Optional<Byte> findIdByEnglishName(@Param("englishName") String englishName);
    
    @Query("SELECT f.id FROM Formulation f WHERE f.koreanName = :koreanName")
    Optional<Byte> findIdByKoreanName(@Param("koreanName") String koreanName);
}