package org.mtvs.backend.checklist.dto;

public class CheckListRequest {
    private Long userId;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;
    private String username;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
