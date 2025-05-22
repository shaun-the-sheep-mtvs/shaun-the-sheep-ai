package org.mtvs.backend.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

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
}
