package org.mtvs.backend.skintype.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.skintype.entity.SkinType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkinTypeDTO {
    
    private Byte id;
    private String englishName;
    private String koreanName;
    private String description;
    
    public static SkinTypeDTO fromEntity(SkinType skinType) {
        return new SkinTypeDTO(
            skinType.getId(),
            skinType.getEnglishName(),
            skinType.getKoreanName(),
            skinType.getDescription()
        );
    }
    
    public SkinType toEntity() {
        return new SkinType(id, englishName, koreanName, description);
    }
}