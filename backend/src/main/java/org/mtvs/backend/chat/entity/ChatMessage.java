package org.mtvs.backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.chat.dto.ChatMessageDTO;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // "user" or "ai"
    private String content;
    private LocalDateTime timestamp;
    // Optionally, add a conversationId or userId for grouping

    public ChatMessageDTO toDTO() {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(this.id);
        dto.setRole(this.role);
        dto.setContent(this.content);
        dto.setTimestamp(this.timestamp);
        return dto;
    }
}