package org.mtvs.backend.checklist.dto;

public class MBTIdto {
    private String userId;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;

    public MBTIdto(String userId, Integer moisture, Integer oil, Integer sensitivity, Integer tension) {
        this.userId = userId;
        this.moisture = moisture;
        this.oil = oil;
        this.sensitivity = sensitivity;
        this.tension = tension;
    }

    public MBTIdto() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
}
