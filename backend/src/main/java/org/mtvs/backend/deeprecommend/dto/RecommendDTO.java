package org.mtvs.backend.deeprecommend.dto;

import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.deeprecommend.entity.enums.Kind;

public class RecommendDTO {

    private int id;
    private Action action;
    private Kind kind; // 크림,앰플,크림
    private String suggest_product;
    private String reason;

    public RecommendDTO(int id, String suggest_product, String reason) {
        this.id = id;
        this.suggest_product = suggest_product;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
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
