package org.mtvs.backend.deeprecommend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.routine.entity.enums.Kinds;

@Entity
@Table(name = "deep_recommend")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeepRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deep_id;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Enumerated(EnumType.STRING)
    private Kinds kind;

    private Long existingProductId;

    private String suggest_product;
    private String reason;

    private Long routineGroupId;

    @ManyToOne
    @JoinColumn(name = "routine_change_id")
    private RoutineChange routineChange;

    public DeepRecommend(Action action, Kinds kind, String suggest_product, String reason, Long routineGroupId) {
        this.action = action;
        this.kind = kind;
        this.suggest_product = suggest_product;
        this.reason = reason;
        this.routineGroupId = routineGroupId;
    }
}
