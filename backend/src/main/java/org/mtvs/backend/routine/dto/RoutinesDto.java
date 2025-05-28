package org.mtvs.backend.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Getter
@Setter
@AllArgsConstructor
public class RoutinesDto {

    private Long id;
    private String name;
    private Kinds kind;
    private String method;
    private int orders;
    private Time time;
    private Long routineGroupId;
}
