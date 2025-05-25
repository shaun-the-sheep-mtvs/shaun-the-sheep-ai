package org.mtvs.backend.deeprecommend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutineAnalysisDTO {

    private int user_id;
    private String skinType;
    private String trouble;

    private String message;

    public RoutineAnalysisDTO(int user_id, String skinType, String trouble, String message) {
        this.user_id = user_id;
        this.skinType = skinType;
        this.trouble = trouble;
        this.message = message;
    }


}
