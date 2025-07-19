package org.mtvs.backend.skintype.service;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.skintype.entity.SkinType;
import org.mtvs.backend.skintype.repository.SkinTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkinTypeService {
    
    private final SkinTypeRepository skinTypeRepository;
    
    /**
     * Get all skin types
     */
    public List<SkinType> getAllSkinTypes() {
        return skinTypeRepository.findAll();
    }
    
    /**
     * Get skin type by ID
     */
    public Optional<SkinType> getSkinTypeById(Byte id) {
        return skinTypeRepository.findById(id);
    }
    
    /**
     * Get skin type by English name
     */
    public Optional<SkinType> getSkinTypeByEnglishName(String englishName) {
        return skinTypeRepository.findByEnglishName(englishName);
    }
    
    /**
     * Get skin type by Korean name
     */
    public Optional<SkinType> getSkinTypeByKoreanName(String koreanName) {
        return skinTypeRepository.findByKoreanName(koreanName);
    }
    
    /**
     * Map Korean skin type name to ID (for AI response processing)
     */
    public Byte mapKoreanNameToId(String koreanName) {
        return SkinType.getIdByKoreanName(koreanName);
    }
    
    /**
     * Map English skin type name to ID
     */
    public Byte mapEnglishNameToId(String englishName) {
        return SkinType.getIdByEnglishName(englishName);
    }
    
    /**
     * Get English name by ID
     */
    public String getEnglishNameById(Byte id) {
        return SkinType.getEnglishNameById(id);
    }
    
    /**
     * Get Korean name by ID
     */
    public String getKoreanNameById(Byte id) {
        return SkinType.getKoreanNameById(id);
    }
}