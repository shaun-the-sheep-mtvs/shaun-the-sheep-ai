package org.mtvs.backend.routine.dto;

import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Getter
@Setter
public class RoutinesDto {
    private String name;
    private Kinds kind;
    private String method;
    private int orders;
    private Time time;

    public RoutinesDto(String name, Kinds kind, String method, int orders, Time time) {
        this.name = name;
        this.kind = kind;
        this.method = method;
        this.orders = orders;
        this.time = time;
    }

}
