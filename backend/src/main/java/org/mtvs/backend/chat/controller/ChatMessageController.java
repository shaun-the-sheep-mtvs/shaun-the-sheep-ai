package org.mtvs.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.dto.ChatMessageDTO;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat-messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    private ChatMessageDTO toDTO(ChatMessage entity) {
        return entity.toDTO();
    }
    
        private ChatMessage toEntity(ChatMessageDTO dto) {
            ChatMessage entity = new ChatMessage();
            entity.setId(dto.getId());
            entity.setRole(dto.getRole());
            entity.setContent(dto.getContent());
            entity.setTimestamp(dto.getTimestamp());
            return entity;
    }

    @GetMapping
    public List<ChatMessageDTO> getAllMessages() {
        return chatMessageService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatMessageDTO> getMessageById(@PathVariable Long id) {
        return chatMessageService.findById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ChatMessageDTO createMessage(@RequestBody ChatMessageDTO dto) {
        ChatMessage saved = chatMessageService.save(toEntity(dto));
        return toDTO(saved);
    }

    @PostMapping("/bulk")
    public List<ChatMessageDTO> createMessages(@RequestBody List<ChatMessageDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .map(chatMessageService::save)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatMessageDTO> updateMessage(@PathVariable Long id, @RequestBody ChatMessageDTO dto) {
        return chatMessageService.findById(id)
                .map(existing -> {
                    ChatMessage entity = toEntity(dto);
                    entity.setId(id);
                    return ResponseEntity.ok(toDTO(chatMessageService.save(entity)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (chatMessageService.findById(id).isPresent()) {
            chatMessageService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 