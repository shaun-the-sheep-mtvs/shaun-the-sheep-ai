// src/main/java/org/mtvs/backend/checklist/dto/CheckListResponse.java
package org.mtvs.backend.checklist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckListResponse {
    private Long id;
    private Integer moisture;
    private Integer oil;
    private Integer sensitivity;
    private Integer tension;
    private LocalDateTime createdAt;
}

