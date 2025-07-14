package org.mtvs.backend.skintype.controller;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.skintype.dto.SkinTypeDTO;
import org.mtvs.backend.skintype.entity.SkinType;
import org.mtvs.backend.skintype.service.SkinTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skin-types")
@RequiredArgsConstructor
public class SkinTypeController {
    
    private final SkinTypeService skinTypeService;
    
    /**
     * Get all skin types
     */
    @GetMapping
    public ResponseEntity<List<SkinTypeDTO>> getAllSkinTypes() {
        List<SkinType> skinTypes = skinTypeService.getAllSkinTypes();
        List<SkinTypeDTO> skinTypeDTOs = skinTypes.stream()
                .map(SkinTypeDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skinTypeDTOs);
    }
    
    /**
     * Get skin type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SkinTypeDTO> getSkinTypeById(@PathVariable Byte id) {
        return skinTypeService.getSkinTypeById(id)
                .map(skinType -> ResponseEntity.ok(SkinTypeDTO.fromEntity(skinType)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get skin type by English name
     */
    @GetMapping("/english/{englishName}")
    public ResponseEntity<SkinTypeDTO> getSkinTypeByEnglishName(@PathVariable String englishName) {
        return skinTypeService.getSkinTypeByEnglishName(englishName)
                .map(skinType -> ResponseEntity.ok(SkinTypeDTO.fromEntity(skinType)))
                .orElse(ResponseEntity.notFound().build());
    }
}