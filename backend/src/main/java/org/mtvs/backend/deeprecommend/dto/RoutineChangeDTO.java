package org.mtvs.backend.deeprecommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineChangeDTO {

    private Long routineChangeId;
    private String routineName;
    private Kinds routineKind;
    private Time routineTime;
    private int routineOrders;
    private String changeMethod;
    private Long routineGroupId;
}
