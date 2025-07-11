package org.mtvs.backend.guestrecommend.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class GuestRecommendResponseDTO {
    private String skinType;
    private String mbtiCode;
    private JsonNode recommendations;

    public GuestRecommendResponseDTO() {}

    public GuestRecommendResponseDTO(String skinType, String mbtiCode, JsonNode recommendations) {
        this.skinType = skinType;
        this.mbtiCode = mbtiCode;
        this.recommendations = recommendations;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public String getMbtiCode() {
        return mbtiCode;
    }

    public void setMbtiCode(String mbtiCode) {
        this.mbtiCode = mbtiCode;
    }

    public JsonNode getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(JsonNode recommendations) {
        this.recommendations = recommendations;
    }
}