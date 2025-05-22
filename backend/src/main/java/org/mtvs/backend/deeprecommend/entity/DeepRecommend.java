package org.mtvs.backend.deeprecommend.entity;

import jakarta.persistence.*;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.deeprecommend.entity.enums.Kind;
@Entity
public class DeepRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deep_id;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Enumerated(EnumType.STRING)
    private Kind kind; // 크림,앰플,크림
    private String suggest_product;
    private String reason;

    public DeepRecommend(int deep_id, Action action, Kind kind, String suggest_product, String reason) {
        this.deep_id = deep_id;
        this.action = action;
        this.kind = kind;
        this.suggest_product = suggest_product;
        this.reason = reason;
    }

    public DeepRecommend() {

    }
}
