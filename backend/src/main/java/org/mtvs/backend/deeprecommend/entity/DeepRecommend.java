package org.mtvs.backend.deeprecommend.entity;

import jakarta.persistence.*;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.routine.entity.enums.Kinds;

@Entity
public class DeepRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deep_id;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Enumerated(EnumType.STRING)
    private Kinds kind;

    private String suggest_product;
    private String reason;


    public DeepRecommend( Action action, Kinds kind, String suggest_product, String reason) {
        this.action = action;
        this.kind = kind;
        this.suggest_product = suggest_product;
        this.reason = reason;
    }

    public DeepRecommend() {

    }
}
