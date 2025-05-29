package org.mtvs.backend.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mtvs.backend.routine.entity.Routine;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;
import org.springframework.util.RouteMatcher;

/*
* ** Routine
*  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

*
*
*
* */

@Data
@AllArgsConstructor
public class RequestRoutineAllDTO {
    private String name;
    private Kinds kind;
    private String method;
    private int orders;
    private Time time;
    private Long groupId;
    public RequestRoutineAllDTO(Routine routine) {
        this.name = routine.getName();
        this.kind = routine.getKind();
        this.method = routine.getMethod();
        this.orders = routine.getOrders();
        this.time = routine.getTime();
        this.groupId = routine.getRoutineGroupId();
    }
}

