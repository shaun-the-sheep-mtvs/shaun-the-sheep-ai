package org.mtvs.backend.product.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.product.entity.Formulation;
import org.mtvs.backend.product.repository.FormulationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormulationService {
    
    private final FormulationRepository formulationRepository;
    
    /**
     * Get English name by formulation ID
     * @param formulationId formulation ID (1-8)
     * @return English name or null if not found
     */
    public String getEnglishNameById(Byte formulationId) {
        if (formulationId == null) return null;
        
        return formulationRepository.findById(formulationId)
                .map(Formulation::getEnglishName)
                .orElse(null);
    }
    
    /**
     * Get Korean name by formulation ID
     * @param formulationId formulation ID (1-8)
     * @return Korean name or null if not found
     */
    public String getKoreanNameById(Byte formulationId) {
        if (formulationId == null) return null;
        
        return formulationRepository.findById(formulationId)
                .map(Formulation::getKoreanName)
                .orElse(null);
    }
    
    /**
     * Get formulation ID by English name
     * @param englishName English formulation name
     * @return formulation ID or null if not found
     */
    public Byte getIdByEnglishName(String englishName) {
        if (englishName == null || englishName.trim().isEmpty()) return null;
        
        return formulationRepository.findIdByEnglishName(englishName.toLowerCase())
                .orElse(null);
    }
    
    /**
     * Get formulation ID by Korean name
     * @param koreanName Korean formulation name
     * @return formulation ID or null if not found
     */
    public Byte getIdByKoreanName(String koreanName) {
        if (koreanName == null || koreanName.trim().isEmpty()) return null;
        
        return formulationRepository.findIdByKoreanName(koreanName)
                .orElse(null);
    }
    
    /**
     * Get full formulation entity by ID
     * @param formulationId formulation ID
     * @return Formulation entity or null if not found
     */
    public Formulation getFormulationById(Byte formulationId) {
        if (formulationId == null) return null;
        
        return formulationRepository.findById(formulationId).orElse(null);
    }
    
    /**
     * Check if formulation ID exists
     * @param formulationId formulation ID to check
     * @return true if exists, false otherwise
     */
    public boolean existsById(Byte formulationId) {
        if (formulationId == null) return false;
        return formulationRepository.existsById(formulationId);
    }
}