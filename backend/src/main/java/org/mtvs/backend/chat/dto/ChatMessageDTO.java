package org.mtvs.backend.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
}