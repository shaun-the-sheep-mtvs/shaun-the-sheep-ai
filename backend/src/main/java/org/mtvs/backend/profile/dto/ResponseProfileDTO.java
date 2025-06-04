package org.mtvs.backend.profile.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.mtvs.backend.routine.dto.RoutinesDto;
import org.mtvs.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseProfileDTO {
    private String email;
    private String username;
    private List<String> troubles;
    private List<RoutinesDto> routines;
    private LocalDateTime createdAt;
}
