package org.mtvs.backend.recommend.dto;

import org.mtvs.backend.user.entity.User;

import java.util.List;


public class UserRequestDTO {
    private Long userId;
    private User.SkinType skinType;
    private List<String> troubles;

    public UserRequestDTO(Long userId, User.SkinType skinType, List<String> troubles) {
        this.userId = userId;
        this.skinType = skinType;
        this.troubles = troubles;
    }

    public UserRequestDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User.SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(User.SkinType skinType) {
        this.skinType = skinType;
    }

    public List<String> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<String> troubles) {
        this.troubles = troubles;
    }
}


