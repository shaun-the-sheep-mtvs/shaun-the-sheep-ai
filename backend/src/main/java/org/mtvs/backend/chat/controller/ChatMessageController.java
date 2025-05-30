package org.mtvs.backend.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.chat.dto.ChatMessageDTO;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            entity.setUserId(dto.getUserId());
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
    public ChatMessageDTO createMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatMessageDTO dto
    ) {
        dto.setUserId(userDetails.getUserId());        // ← 여기서 덮어쓰기
        ChatMessage saved = chatMessageService.save(toEntity(dto));
        return toDTO(saved);
    }

    @PostMapping("/bulk")
    public List<ChatMessageDTO> createMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<ChatMessageDTO> dtos
    ) {
        // userId 주입 후 저장
        return dtos.stream()
                .filter(dto -> "user".equals(dto.getRole()))
                // userId 주입
                .peek(dto -> dto.setUserId(userDetails.getUserId()))
                // DTO → Entity → 저장 → DTO 변환
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "templateKey", required = false) String templateKey,
            @RequestBody List<ChatMessageDTO> historyDto
    ) throws JsonProcessingException {
        String userId = userDetails.getUserId();

        // 1) DTO → Entity 변환
        List<ChatMessage> history = historyDto.stream()
                .map(dto -> {
                    ChatMessage msg = new ChatMessage();
                    msg.setUserId(dto.getUserId());
                    msg.setRole(dto.getRole());
                    msg.setContent(dto.getContent());
                    msg.setTimestamp(dto.getTimestamp());
                    return msg;
                })
                .collect(Collectors.toList());

        // 2) 마지막 user 메시지를 질문으로
        String userQuestion = history.stream()
                .filter(m -> "user".equals(m.getRole()))
                .map(ChatMessage::getContent)
                .reduce((first, second) -> second)
                .orElse("");

        // 3) AI 호출 (DB 저장 없이 메시지 생성만)
        ChatMessage aiMsg = chatMessageService.askAI_Single(
                userId, history, userQuestion, templateKey
        );

        String text = aiMsg.getContent();
        // 줄바꿈 기준으로 5줄짜리 요약인 경우에만 저장
        long lines = text.lines().count();
        boolean isSummary = lines == 5 && text.startsWith("1) 피부 타입:");

        // 4) “summary” 키일 때만 저장
        if (isSummary) {
            aiMsg = chatMessageService.save(aiMsg);
        }

        // 5) Entity → DTO 변환
        ChatMessageDTO responseDto = new ChatMessageDTO();
        responseDto.setUserId(aiMsg.getUserId());
        responseDto.setId(aiMsg.getId());
        responseDto.setRole(aiMsg.getRole());
        responseDto.setContent(aiMsg.getContent());
        responseDto.setTimestamp(aiMsg.getTimestamp());

        return ResponseEntity.ok(responseDto);
    }
} 