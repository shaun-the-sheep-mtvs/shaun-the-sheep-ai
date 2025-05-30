package org.mtvs.backend.checklist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBTIdto {
    private String userId;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;

    public MBTIdto() {}

    public MBTIdto(String userId, Integer moisture, Integer oil, Integer sensitivity, Integer tension) {
        this.userId = userId;
        this.moisture = moisture;
        this.oil = oil;
        this.sensitivity = sensitivity;
        this.tension = tension;
    }

}
