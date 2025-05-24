package org.mtvs.backend.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.mtvs.backend.auth.model.User;

import java.util.List;

@Getter
@Setter
public class ProblemDto {
    private User.SkinType skinType;
    private List<String> troubles;

    public ProblemDto(User.SkinType skinType, List<String> troubles) {
        this.skinType = skinType;
        this.troubles = troubles;
    }
}
