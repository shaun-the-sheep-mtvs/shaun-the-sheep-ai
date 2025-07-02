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
@Table(name = "skin_concerns")
public class ConcernList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "label", nullable = false)
    private String label;
    
    @Column(name = "description", nullable = false)
    private String description;

    public ConcernList(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static final String[][] CONCERN_DATA = {
        {"dryness", "건조함"},
        {"oiliness", "번들거림"},
        {"sensitivity", "민감함"},
        {"elasticity", "탄력 저하"},
        {"redness", "홍조"},
        {"unevenTone", "톤 안정"},
        {"hyperpigment", "색소침착"},
        {"fineLines", "잔주름"},
        {"pores", "모공 케어"},
        {"breakouts", "트러블"},
        {"dullness", "칙칙함"},
        {"darkCircles", "다크써클"},
        {"roughTexture", "결 거칠음"}
    };
}
