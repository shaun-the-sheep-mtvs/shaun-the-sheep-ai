package org.mtvs.backend.checklist.model;

import jakarta.persistence.*;
import org.mtvs.backend.auth.model.User;

@Entity
@Table(name = "mbti_mapping")
public class MBTIMapping {
    @Id
    @Column(length = 4)
    private String code;            // MOST, MOSL, …

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
