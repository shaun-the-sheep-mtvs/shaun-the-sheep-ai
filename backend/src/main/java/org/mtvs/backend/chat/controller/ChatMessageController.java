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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

//    @PostMapping("/bulk")
//    public List<ChatMessageDTO> createMessages(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestBody List<ChatMessageDTO> dtos
//    ) {
//        // userId 주입 후 저장
//        return dtos.stream()
//                .filter(dto -> "user".equals(dto.getRole()))
//                // userId 주입
//                .peek(dto -> dto.setUserId(userDetails.getUserId()))
//                // DTO → Entity → 저장 → DTO 변환
//                .map(this::toEntity)
//                .map(chatMessageService::save)
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }

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
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "templateKey", required = false) String templateKey,
            @RequestBody Map<String, String> body   // { "userMessage": "질문 내용" }
    ) {
        String userId = userDetails.getUserId();
        String userQuestion = body.get("userMessage");

        // 1) sessionId가 누락되었거나 빈 문자열이라면, Bad Request로 리턴
        if (sessionId == null || sessionId.isBlank()) {
            ChatMessageDTO errorDto = new ChatMessageDTO();
                     errorDto.setRole("ai");
                     errorDto.setContent("세션이 유효하지 않습니다. 다시 시도해주세요.");
                     errorDto.setTimestamp(LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorDto);
        }

        // 2) AI 호출 (서비스 레이어에서 sessionId를 이용해 캐싱된 히스토리＋프롬프트를 결합)
        ChatMessage aiMsg = chatMessageService.askAI_Single(sessionId, userId, userQuestion);

        // 3) AI 텍스트가 5줄 요약 형식이라면 DB에 저장
        String text = aiMsg.getContent();
        long lines = text.lines().count();
        boolean isSummary = lines == 5 && text.startsWith("1) 피부 타입:");
        if (isSummary) {
            aiMsg = chatMessageService.save(aiMsg);
        }

        // 4) DTO로 변환 후 반환
        ChatMessageDTO responseDto = new ChatMessageDTO();
        responseDto.setId(aiMsg.getId());
        responseDto.setUserId(aiMsg.getUserId());
        responseDto.setRole(aiMsg.getRole());
        responseDto.setContent(aiMsg.getContent());
        responseDto.setTimestamp(aiMsg.getTimestamp());

        return ResponseEntity.ok(responseDto);
    }

    /**
     * “퀵 액션” 버튼을 누를 때마다, 최초로 시스템 프롬프트 + 빈 히스토리를 셋업해야 합니다.
     * 클라이언트가 퀵 액션(예: PRODUCT_INQUIRY) 버튼을 클릭한 순간에 이 메서드를 호출하세요.
     */
    @PostMapping("/init-session")
    public ResponseEntity<Void> initSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "templateKey", required = false) String templateKey
    ) {
        String actualUserId = userDetails.getUserId();
        // 서비스 레이어에 해당 sessionId, templateKey로 히스토리 초기화 요청
        chatMessageService.initSession(sessionId, actualUserId, templateKey);
        return ResponseEntity.ok().build();
    }
}