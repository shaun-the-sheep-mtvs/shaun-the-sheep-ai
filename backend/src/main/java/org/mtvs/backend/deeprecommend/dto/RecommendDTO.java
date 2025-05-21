package org.mtvs.backend.deeprecommend.dto;

import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.deeprecommend.domain.Action;
import org.mtvs.backend.deeprecommend.domain.Kind;

@Getter
@Setter
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


}
