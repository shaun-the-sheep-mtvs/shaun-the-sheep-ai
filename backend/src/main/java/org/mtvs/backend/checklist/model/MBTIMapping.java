package org.mtvs.backend.checklist.model;

import jakarta.persistence.*;
import org.mtvs.backend.user.entity.User;      // ← 수정된 경로

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mbti_mapping")
public class MBTIMapping {
    @Id
    @Column(length = 4)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type", nullable = false)
    private User.SkinType skinType;

    protected MBTIMapping() {}

    public MBTIMapping(String code, User.SkinType skinType) {
        this.code = code;
        this.skinType = skinType;
    }

}
