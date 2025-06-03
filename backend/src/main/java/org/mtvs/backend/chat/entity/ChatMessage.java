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

    private String userId;
    private String role; // "user" or "ai"

    @Enumerated(EnumType.STRING)
    private Prompt_Type promptType; // TOTAL, PRODUCT, INGREDIENT, SKIN_TYPE

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    public ChatMessageDTO toDTO() {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setUserId(this.userId);
        dto.setId(this.id);
        dto.setRole(this.role);
        dto.setPromptType(String.valueOf(this.promptType));
        dto.setContent(this.content);
        dto.setTimestamp(this.timestamp);
        return dto;
    }
}
