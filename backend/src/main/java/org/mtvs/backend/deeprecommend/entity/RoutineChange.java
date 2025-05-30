package org.mtvs.backend.deeprecommend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "routine_change")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RoutineChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineChangeId;

    @Column(name = "routine_name")
    private String routineName;

    @Enumerated(EnumType.STRING)
    private Kinds routineKind;

    @Enumerated(EnumType.STRING)
    private Time routineTime;

    @Column(name = "routine_orders")
    private int routineOrders;

    @Column(name = "routine_method")
    private String changeMethod;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long routineGroupId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @CreatedDate
    private LocalDateTime createdAt;

    public RoutineChange(String routineName, Kinds routineKind, Time routineTime, int routineOrders, String changeMethod, User user, Long routineGroupId) {
        this.routineName = routineName;
        this.routineKind = routineKind;
        this.routineTime = routineTime;
        this.routineOrders = routineOrders;
        this.changeMethod = changeMethod;
        this.user = user;
        this.routineGroupId = routineGroupId;
    }
}
