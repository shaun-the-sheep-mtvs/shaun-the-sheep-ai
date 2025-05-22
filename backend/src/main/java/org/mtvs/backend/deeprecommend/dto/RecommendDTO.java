package org.mtvs.backend.deeprecommend.dto;

import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.routine.entity.enums.Kinds;

public class RecommendDTO {

    private int user_id;
    private Action action;
    private Kinds kind; // 크림,앰플,크림
    private String suggest_product;
    private String reason;

    public RecommendDTO() {
    }

    public RecommendDTO(int user_id, Action action, Kinds kind, String suggest_product, String reason) {
        this.user_id = user_id;
        this.action = action;
        this.kind = kind;
        this.suggest_product = suggest_product;
        this.reason = reason;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Kinds getKind() {
        return kind;
    }

    public void setKind(Kinds kind) {
        this.kind = kind;
    }

    public String getSuggest_product() {
        return suggest_product;
    }

    public void setSuggest_product(String suggest_product) {
        this.suggest_product = suggest_product;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
