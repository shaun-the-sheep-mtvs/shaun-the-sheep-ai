package org.mtvs.backend.routine.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Kinds kind;

    @Enumerated(EnumType.STRING)
    private Time time;

    private int orders;

    private String method;

    public Routine(String method, Time time, Kinds kind, String name) {
        this.method = method;
        this.time = time;
        this.kind = kind;
        this.name = name;
    }

    //    유저 외래키
//    private String



}

/*
*     private String name;
    private Kinds kind;
    private Time time;
    private String method;
*
*
* */