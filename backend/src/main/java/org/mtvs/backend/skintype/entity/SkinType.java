package org.mtvs.backend.skintype.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "skin_types")
public class SkinType {
    
    @Id
    private Byte id;
    
    @Column(name = "english_name", nullable = false, unique = true)
    private String englishName;
    
    @Column(name = "korean_name", nullable = false, unique = true)
    private String koreanName;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    public SkinType(Byte id, String englishName, String koreanName, String description) {
        this.id = id;
        this.englishName = englishName;
        this.koreanName = koreanName;
        this.description = description;
    }
    
    // Static data for the 6 skin types
    public static final Object[][] SKIN_TYPE_DATA = {
        {(byte) 1, "sensitive", "민감성", "외부 자극에 민감하게 반응하는 피부"},
        {(byte) 2, "dry", "건성", "수분과 유분이 부족한 건조한 피부"},
        {(byte) 3, "oily", "지성", "과다한 유분 분비로 번들거리는 피부"},
        {(byte) 4, "combination", "복합성", "T존은 지성, U존은 건성인 복합 피부"},
        {(byte) 5, "dehydrated", "수분부족지성", "수분 부족으로 겉은 번들, 속은 건조한 피부"},
        {(byte) 6, "default", "표준형", "균형잡힌 건강한 피부"}
    };
    
    // Helper method to get skin type ID by English name
    public static Byte getIdByEnglishName(String englishName) {
        for (Object[] data : SKIN_TYPE_DATA) {
            if (data[1].equals(englishName)) {
                return (Byte) data[0];
            }
        }
        return (byte) 6; // default
    }
    
    // Helper method to get skin type ID by Korean name
    public static Byte getIdByKoreanName(String koreanName) {
        for (Object[] data : SKIN_TYPE_DATA) {
            if (data[2].equals(koreanName)) {
                return (Byte) data[0];
            }
        }
        return (byte) 6; // default
    }
    
    // Helper method to get English name by ID
    public static String getEnglishNameById(Byte id) {
        for (Object[] data : SKIN_TYPE_DATA) {
            if (data[0].equals(id)) {
                return (String) data[1];
            }
        }
        return "default";
    }
    
    // Helper method to get Korean name by ID
    public static String getKoreanNameById(Byte id) {
        for (Object[] data : SKIN_TYPE_DATA) {
            if (data[0].equals(id)) {
                return (String) data[2];
            }
        }
        return "표준형";
    }
}