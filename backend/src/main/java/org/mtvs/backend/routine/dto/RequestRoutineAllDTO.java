package org.mtvs.backend.routine.dto;

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
public class RequestRoutineAllDTO {
    private long id;
    private String name;
    private Kinds kind;
    private Time time;
    private int orders;
    private String method;
}
