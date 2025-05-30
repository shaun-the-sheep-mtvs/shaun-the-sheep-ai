package org.mtvs.backend.deeprecommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.deeprecommend.entity.enums.Action;
import org.mtvs.backend.routine.entity.enums.Kinds;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendResponseDTO {

    private Action action;
    private Kinds kind;
    private String name;
    private String suggestProduct;
    private String reason;
    private Long routineGroupId;
}
