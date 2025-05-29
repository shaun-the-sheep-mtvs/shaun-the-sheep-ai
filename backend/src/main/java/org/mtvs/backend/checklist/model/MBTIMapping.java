package org.mtvs.backend.checklist.model;

import jakarta.persistence.*;
import org.mtvs.backend.user.entity.User;      // ← 수정된 경로

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User.SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(User.SkinType skinType) {
        this.skinType = skinType;
    }
}
