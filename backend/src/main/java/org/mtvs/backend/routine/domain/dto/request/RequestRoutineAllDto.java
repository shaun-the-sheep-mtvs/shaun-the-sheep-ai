package org.mtvs.backend.routine.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mtvs.backend.routine.domain.entity.Routine;
import org.mtvs.backend.routine.domain.entity.enums.Kinds;
import org.mtvs.backend.routine.domain.entity.enums.Time;

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
public class RequestRoutineAllDto {
    private String name;
    private Kinds kind;
    private String method;
    private int orders;
    private Time time;
    private Long groupId;
    public RequestRoutineAllDto(Routine routine) {
        this.name = routine.getName();
        this.kind = routine.getKind();
        this.method = routine.getMethod();
        this.orders = routine.getOrders();
        this.time = routine.getTime();
        this.groupId = routine.getRoutineGroupId();
    }
}

