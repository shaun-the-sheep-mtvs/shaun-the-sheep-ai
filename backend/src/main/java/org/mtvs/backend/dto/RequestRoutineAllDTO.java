package org.mtvs.backend.dto;

import org.mtvs.backend.entity.enums.Kinds;
import org.mtvs.backend.entity.enums.Time;

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
    private int order;
    private String method;
}
