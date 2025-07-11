package org.mtvs.backend.guestrecommend.dto;

import java.util.List;

public class GuestRecommendRequestDTO {
    private String skinType;
    private List<String> concerns;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;

    public GuestRecommendRequestDTO() {}

    public GuestRecommendRequestDTO(String skinType, List<String> concerns, Integer moisture, Integer oil, Integer sensitivity, Integer tension) {
        this.skinType = skinType;
        this.concerns = concerns;
        this.moisture = moisture;
        this.oil = oil;
        this.sensitivity = sensitivity;
        this.tension = tension;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public List<String> getConcerns() {
        return concerns;
    }

    public void setConcerns(List<String> concerns) {
        this.concerns = concerns;
    }

    public Integer getMoisture() {
        return moisture;
    }

    public void setMoisture(Integer moisture) {
        this.moisture = moisture;
    }

    public Integer getOil() {
        return oil;
    }

    public void setOil(Integer oil) {
        this.oil = oil;
    }

    public Integer getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Integer sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Integer getTension() {
        return tension;
    }

    public void setTension(Integer tension) {
        this.tension = tension;
    }
}