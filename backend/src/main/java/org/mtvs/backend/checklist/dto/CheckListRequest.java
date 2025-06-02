package org.mtvs.backend.checklist.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckListRequest {
    private String userId;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;
    private List<String> troubles = new ArrayList<>();
    private String username;

    public CheckListRequest() {}

    public CheckListRequest(String userId, Integer moisture, Integer oil, Integer sensitivity, Integer tension, List<String> troubles, String username) {
        this.userId = userId;
        this.moisture = moisture;
        this.oil = oil;
        this.sensitivity = sensitivity;
        this.tension = tension;
        this.troubles = troubles;
        this.username = username;
    }
}

