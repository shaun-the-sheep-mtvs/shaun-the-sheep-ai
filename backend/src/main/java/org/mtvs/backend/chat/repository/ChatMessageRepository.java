package org.mtvs.backend.chat.repository;

import org.mtvs.backend.chat.dto.ChatMessageDTO;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 기존: List<ChatMessageDTO> findByUserId(String userId);
    // 수정: 엔티티를 반환하도록
    List<ChatMessage> findByUserId(String userId);
}