package org.mtvs.backend.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Getter
@Setter
public class RoutinesDto {
    public RoutinesDto(Long id, String name, Kinds kind, String method, int orders, Time time, Long routineGroupId) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        System.out.println(kind);
        this.method = method;
        this.orders = orders;
        this.time = time;
        this.routineGroupId = routineGroupId;
    }

    private Long id;
    private String name;
    private Kinds kind;
    private String method;
    private int orders;
    private Time time;
    private Long routineGroupId;
}
