package org.mtvs.backend.routine.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
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

    private Long routineGroupId;

    public Routine(String name, Time time, Kinds kind, String method) {
        this.method = method;
        this.time = time;
        this.kind = kind;
        this.name = name;
    }
    public Routine(String name, Time time, Kinds kind, String method,int orders,User user, Long routineGroupId) {
        this.method = method;
        this.time = time;
        this.kind = kind;
        this.name = name;
        this.orders = orders;
        this.user = user;
        this.routineGroupId = routineGroupId;
    }

    //    유저 외래키
    @ManyToOne
    private User user;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @CreatedDate
    private LocalDateTime createdAt;
}

/*
*     private String name;
    private Kinds kind;
    private Time time;
    private String method;
*
*
* */