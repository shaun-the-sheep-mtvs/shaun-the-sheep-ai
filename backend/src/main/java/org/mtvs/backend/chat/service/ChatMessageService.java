package org.mtvs.backend.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.chat.entity.ChatMessage;
import org.mtvs.backend.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String apiKey;

    public List<ChatMessage> findAll() {
        return chatMessageRepository.findAll();
    }

    public Optional<ChatMessage> findById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public ChatMessage save(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public void deleteById(Long id) {
        chatMessageRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    public ChatMessage askAI_Single(List<ChatMessage> history, String userQuestion) {
        // 1) request body 구성
        Map<String,Object> generationConfig = Map.of(
                "temperature", 0.2,
                "topK",  40,             // 필요에 따라 조정
                "topP",  0.95,           // 필요에 따라 조정
                "maxOutputTokens", 256
        );

        Map<String,Object> contentEntry = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userQuestion))
        );

        Map<String,Object> body = Map.of(
                "contents", List.of(contentEntry),
                "generationConfig", generationConfig
        );

        // 2) API 호출
        Map<String,Object> res = webClient.post()
                .uri(b -> b
                        .path("/v1beta/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build()
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();

        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println(">>> AI raw response:\n" +
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 2) candidates 리스트 꺼내기
        List<Map<String,Object>> candidates =
                (List<Map<String,Object>>) res.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("AI output이 없습니다.");
        }

        // 3) 첫 번째 candidate의 content 맵 꺼내기
        Map<String,Object> contentMap =
                (Map<String,Object>) candidates.get(0).get("content");
        if (contentMap == null) {
            throw new RuntimeException("AI content가 없습니다.");
        }

        // 4) parts 배열에서 첫 번째 part, 그리고 text 꺼내기
        List<Map<String,Object>> parts =
                (List<Map<String,Object>>) contentMap.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("AI parts가 없습니다.");
        }
        String aiText = (String) parts.get(0).get("text");
        if (aiText == null) {
            throw new RuntimeException("AI text가 없습니다.");
        }

        // 5) 엔티티에 세팅 후 저장
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setRole("ai");
        aiMsg.setContent(aiText);
        aiMsg.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(aiMsg);

        return aiMsg;
    }

}
