package org.mtvs.backend.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.user.entity.User;

import java.util.List;

@Getter
@Setter
public class ProblemDto {
    private String skinType;
    private List<String> troubles;

    public ProblemDto(String skinType, List<String> troubles) {
        this.skinType = skinType;
        this.troubles = troubles;
    }
}
