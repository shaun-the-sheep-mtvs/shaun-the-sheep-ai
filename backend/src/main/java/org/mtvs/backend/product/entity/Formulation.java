package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.global.entity.BaseEntity;

@Data
@Table(name = "formulations")
@Entity
@NoArgsConstructor
public class Formulation extends BaseEntity {
    @Id
    private Byte id;
    
    @Column(name = "english_name", nullable = false, unique = true)
    private String englishName;
    
    @Column(name = "korean_name", nullable = false, unique = true)
    private String koreanName;
    
    @Column(name = "description")
    private String description;
    
    public Formulation(Byte id, String englishName, String koreanName, String description) {
        this.id = id;
        this.englishName = englishName;
        this.koreanName = koreanName;
        this.description = description;
    }
    
    // Static data for the 8 formulation types
    public static final Object[][] FORMULATION_DATA = {
        {(byte) 1, "toner", "토너", "피부 결을 정리하고 수분을 공급하는 기초 스킨케어 제품"},
        {(byte) 2, "ampoule", "앰플", "고농축 유효성분을 함유한 집중 케어 제품"},
        {(byte) 3, "cream", "크림", "영양과 수분을 공급하는 진한 질감의 보습 제품"},
        {(byte) 4, "serum", "세럼", "피부 깊숙이 침투하는 고농축 에센스"},
        {(byte) 5, "lotion", "로션", "가벼운 질감의 보습 제품"},
        {(byte) 6, "mask", "팩", "집중 케어를 위한 마스크 타입 제품"},
        {(byte) 7, "pad", "패드", "토너나 에센스가 함유된 패드형 제품"},
        {(byte) 8, "skin", "스킨", "가장 기본적인 기초 화장수"}
    };
}