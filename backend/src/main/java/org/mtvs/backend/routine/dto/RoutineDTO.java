package org.mtvs.backend.routine.dto;


/*
* ** Routine
*  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Kinds kind;

    @Enumerated(EnumType.STRING)
    private Time time;

    private int order;

    private String method;
*
*
*
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

///
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineDTO {
    private String name;
    private Kinds kind;
    private Time time;
    private String method;
    private int orders;
}
