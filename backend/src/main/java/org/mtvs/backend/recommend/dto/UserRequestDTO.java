package org.mtvs.backend.recommend.dto;

import org.mtvs.backend.user.entity.User;

import java.util.List;


public class UserRequestDTO {
    private String userId;
    private String skinType;
    private List<String> troubles;

    public UserRequestDTO(String userId, String skinType, List<String> troubles) {
        this.userId = userId;
        this.skinType = skinType;
        this.troubles = troubles;
    }

    public UserRequestDTO() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public List<String> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<String> troubles) {
        this.troubles = troubles;
    }
}


