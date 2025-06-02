package org.mtvs.backend.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestData implements Serializable {
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;
    private List<String> troubles = new ArrayList<>();

    public GuestData() { }

    public GuestData(Integer moisture, Integer oil, Integer sensitivity, Integer tension, List<String> troubles) {
        this.moisture = moisture;
        this.oil = oil;
        this.sensitivity = sensitivity;
        this.tension = tension;
        this.troubles = troubles;
    }
}
