package org.mtvs.backend.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private String userId;
    private Long id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
}