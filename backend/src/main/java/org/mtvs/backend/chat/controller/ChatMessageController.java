package org.mtvs.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.chat.dto.ChatMessageDTO;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.entity.Prompt_Type;
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
        if (dto.getPromptType() != null) {
            // null 체크 후 enum 변환
            entity.setPromptType(Prompt_Type.valueOf(dto.getPromptType()));
        }
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
    public ResponseEntity<ChatMessageDTO> createMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatMessageDTO dto
    ) {
        // userId 덮어쓰기
        dto.setUserId(userDetails.getUserId());

        // 예: 프론트가 보낸 promptType 문자열이 "PRODUCT"라면 Prompt_Type.PRODUCT가 저장됨
        ChatMessage saved = chatMessageService.save(toEntity(dto));
        return ResponseEntity.ok(saved.toDTO());
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
    public ResponseEntity<?> askAI(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "templateKey", required = false) String templateKey,
            @RequestBody Map<String, String> body   // { "userMessage": "질문 내용" }
    ) {
        String userId = userDetails.getUserId();
        String userQuestion = body.get("userMessage");

        // 1) sessionId 검증
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "세션이 유효하지 않습니다. 다시 시도해주세요."));
        }
        if ("TOTAL_REPORT".equals(templateKey)) {
            ChatMessage userMsgEntity = new ChatMessage();
            userMsgEntity.setUserId(userId);
            userMsgEntity.setRole("user");
            userMsgEntity.setContent(userQuestion);
            userMsgEntity.setTimestamp(LocalDateTime.now());
            // promptType은 TOTAL_REPORT이므로 Prompt_Type.TOTAL로 설정
            userMsgEntity.setPromptType(Prompt_Type.TOTAL);
            chatMessageService.save(userMsgEntity);
        }
        // 2) AI 호출 (서비스 레이어)
        ChatMessage aiMsg = chatMessageService.askAI_Single(sessionId, userId, userQuestion);

        // 2) AI 메시지인 경우
        if ("ai".equals(aiMsg.getRole())) {
            // 2-1) templateKey → promptType enum으로 변환
            Prompt_Type pt = null;
            if (templateKey != null) {
                switch (templateKey) {
                    case "TOTAL_REPORT":
                        pt = Prompt_Type.TOTAL;
                        break;
                    case "PRODUCT_INQUIRY":
                        pt = Prompt_Type.PRODUCT;
                        break;
                    case "INGREDIENT_INQUIRY":
                        pt = Prompt_Type.INGREDIENT;
                        break;
                    case "SKIN_TYPE":
                        pt = Prompt_Type.SKIN_TYPE;
                        break;
                    default:
                        pt = Prompt_Type.TOTAL;
                }
            }
            aiMsg.setPromptType(pt);

            // 2-2) AI 응답 처리 + “5회차 도달 시 MD 저장” 로직
            chatMessageService.handleAiResponseAndMaybeSaveMd(sessionId, aiMsg, templateKey);
        }

        // 4) CUSTOMER_SUPPORT 템플릿인 경우
        if ("TOTAL_REPORT".equals(templateKey)) {
            // 4-1) AI 텍스트를 Markdown-JSON으로 저장
            chatMessageService.saveAiResponseAsMdJson(
                    sessionId, aiMsg.getContent());

            // 4-2) 프론트엔드에는 메시지만
            return ResponseEntity.ok(
                    Map.of("message", "진단서가 제출되었습니다.")
            );
        }

        // 5) 그 외 템플릿인 경우 → ChatMessageDTO 반환
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