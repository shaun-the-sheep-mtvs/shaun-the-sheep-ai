package org.mtvs.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String userId;
    private Long id;
    private String role;
    private String promptType;
    private String content;
    private LocalDateTime timestamp;
}