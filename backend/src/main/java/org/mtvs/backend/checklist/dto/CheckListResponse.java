package org.mtvs.backend.checklist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckListResponse {
    private Long id;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;
    private List<String> troubles;
    private LocalDateTime createdAt;
}

