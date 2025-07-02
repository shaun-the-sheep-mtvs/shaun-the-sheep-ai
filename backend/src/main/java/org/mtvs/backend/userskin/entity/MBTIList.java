package org.mtvs.backend.userskin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "skin_mbtis")
public class MBTIList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "mbti_code", nullable = false, unique = true)
    private String mbtiCode;
    
    @Column(name = "korean_name", nullable = false)
    private String koreanName;
    
    @Column(name = "description", nullable = false)
    private String description;

    public MBTIList(String mbtiCode, String koreanName, String description) {
        this.mbtiCode = mbtiCode;
        this.koreanName = koreanName;
        this.description = description;
    }

    public static final String[][] MBTI_DATA = {
        {"DBIL", "건성", "건조하고 민감하지 않은 탄력 부족 피부"},
        {"DBIT", "건성", "건조하고 민감하지 않은 탄력 있는 피부"},
        {"DBSL", "건성", "건조하고 민감한 탄력 부족 피부"},
        {"DBST", "건성", "건조하고 민감한 탄력 있는 피부"},
        {"DOIL", "수분부족지성", "건조하고 유분이 많은 탄력 부족 피부"},
        {"DOIT", "수분부족지성", "건조하고 유분이 많은 탄력 있는 피부"},
        {"DOSL", "수분부족지성", "건조하고 유분이 많은 민감한 탄력 부족 피부"},
        {"DOST", "수분부족지성", "건조하고 유분이 많은 민감한 탄력 있는 피부"},
        {"MBIL", "복합성", "수분이 있고 민감하지 않은 탄력 부족 피부"},
        {"MBIT", "복합성", "수분이 있고 민감하지 않은 탄력 있는 피부"},
        {"MBSL", "민감성", "수분이 있고 민감한 탄력 부족 피부"},
        {"MBST", "민감성", "수분이 있고 민감한 탄력 있는 피부"},
        {"MOIL", "지성", "수분과 유분이 많은 탄력 부족 피부"},
        {"MOIT", "지성", "수분과 유분이 많은 탄력 있는 피부"},
        {"MOSL", "민감성", "수분과 유분이 많은 민감한 탄력 부족 피부"},
        {"MOST", "민감성", "수분과 유분이 많은 민감한 탄력 있는 피부"}
    };
}
