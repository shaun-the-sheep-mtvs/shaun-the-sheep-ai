package org.mtvs.backend.auth.dto;

import org.mtvs.backend.auth.model.User;

import java.util.List;

public class ProblemDto {
    private User.SkinType skinType;
    private List<String> troubles;
}
