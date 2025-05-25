package org.mtvs.backend.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ResponseEntity<ChatMessageDTO> getMessageById(
            @PathVariable("id") Long id     // ← 여기 이름을 명시
    ) {
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
    public ResponseEntity<ChatMessageDTO> updateMessage(
            @PathVariable("id") Long id,    // ← 마찬가지로
            @RequestBody ChatMessageDTO dto
    ) {
        return chatMessageService.findById(id)
                .map(existing -> {
                    ChatMessage entity = toEntity(dto);
                    entity.setId(id);
                    return ResponseEntity.ok(toDTO(chatMessageService.save(entity)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("id") Long id     // ← 그리고 여기에도
    ) {
        if (chatMessageService.findById(id).isPresent()) {
            chatMessageService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatMessageDTO> askAI(
            @RequestBody List<ChatMessageDTO> historyDto
    ) throws JsonProcessingException {
        // 1) DTO → Entity 변환
        List<ChatMessage> history = historyDto.stream()
                .map(dto -> {
                    ChatMessage msg = new ChatMessage();
                    msg.setRole(dto.getRole());
                    msg.setContent(dto.getContent());
                    msg.setTimestamp(dto.getTimestamp());
                    return msg;
                })
                .collect(Collectors.toList());

        // 2) userQuestion 추출: history 의 마지막 user 메시지를 질문으로
        String userQuestion = history.stream()
                .filter(m -> "user".equals(m.getRole()))
                .map(ChatMessage::getContent)
                .reduce((first, second) -> second)  // 마지막 메시지
                .orElse("");

        // 3) AI 서비스 호출 (단일 텍스트 생성)
        ChatMessage aiMsg = chatMessageService.askAI_Single(history, userQuestion);

        // 4) Entity → DTO 변환
        ChatMessageDTO responseDto = new ChatMessageDTO();
        responseDto.setId(aiMsg.getId());
        responseDto.setRole(aiMsg.getRole());
        responseDto.setContent(aiMsg.getContent());
        responseDto.setTimestamp(aiMsg.getTimestamp());

        return ResponseEntity.ok(responseDto);
    }
} 