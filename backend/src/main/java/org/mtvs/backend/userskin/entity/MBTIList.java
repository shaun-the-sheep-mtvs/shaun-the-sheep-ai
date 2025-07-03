package org.mtvs.backend.userskin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.skintype.entity.SkinType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "skin_mbtis")
public class MBTIList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "mbti_code", nullable = false, unique = true)
    private String mbtiCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_type_id", nullable = false)
    private SkinType skinType;
    
    @Column(name = "description", nullable = false)
    private String description;

    public MBTIList(String mbtiCode, SkinType skinType, String description) {
        this.mbtiCode = mbtiCode;
        this.skinType = skinType;
        this.description = description;
    }

    public static final Object[][] MBTI_DATA = {
        {"DBIL", (byte) 2, "건조하고 민감하지 않은 탄력 부족 피부"},      // 건성
        {"DBIT", (byte) 2, "건조하고 민감하지 않은 탄력 있는 피부"},       // 건성
        {"DBSL", (byte) 2, "건조하고 민감한 탄력 부족 피부"},           // 건성
        {"DBST", (byte) 2, "건조하고 민감한 탄력 있는 피부"},            // 건성
        {"DOIL", (byte) 5, "건조하고 유분이 많은 탄력 부족 피부"},        // 수분부족지성
        {"DOIT", (byte) 5, "건조하고 유분이 많은 탄력 있는 피부"},         // 수분부족지성
        {"DOSL", (byte) 5, "건조하고 유분이 많은 민감한 탄력 부족 피부"},    // 수분부족지성
        {"DOST", (byte) 5, "건조하고 유분이 많은 민감한 탄력 있는 피부"},     // 수분부족지성
        {"MBIL", (byte) 4, "수분이 있고 민감하지 않은 탄력 부족 피부"},     // 복합성
        {"MBIT", (byte) 4, "수분이 있고 민감하지 않은 탄력 있는 피부"},      // 복합성
        {"MBSL", (byte) 1, "수분이 있고 민감한 탄력 부족 피부"},          // 민감성
        {"MBST", (byte) 1, "수분이 있고 민감한 탄력 있는 피부"},           // 민감성
        {"MOIL", (byte) 3, "수분과 유분이 많은 탄력 부족 피부"},          // 지성
        {"MOIT", (byte) 3, "수분과 유분이 많은 탄력 있는 피부"},           // 지성
        {"MOSL", (byte) 1, "수분과 유분이 많은 민감한 탄력 부족 피부"},     // 민감성
        {"MOST", (byte) 1, "수분과 유분이 많은 민감한 탄력 있는 피부"}      // 민감성
    };
}
