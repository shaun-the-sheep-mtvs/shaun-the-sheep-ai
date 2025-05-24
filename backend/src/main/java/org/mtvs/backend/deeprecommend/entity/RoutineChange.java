package org.mtvs.backend.deeprecommend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.routine.entity.enums.Kinds;
import org.mtvs.backend.routine.entity.enums.Time;

@Entity
@Table(name = "routine_change")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int routineChangeId;

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
}
