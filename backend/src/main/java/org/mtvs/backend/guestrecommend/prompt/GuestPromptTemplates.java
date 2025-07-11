package org.mtvs.backend.guestrecommend.prompt;

import org.mtvs.backend.prompt.service.PromptService;
import org.mtvs.backend.prompt.template.PromptTemplate;

import java.util.List;
import java.util.Map;

public class GuestPromptTemplates {
    
    /**
     * 게스트 기본 추천 프롬프트를 생성합니다.
     *
     * @param skinType 피부 타입
     * @param concerns 피부 고민 목록
     * @param promptService 프롬프트 서비스
     * @return 생성된 프롬프트 텍스트
     */
    public static String createBasicRecommendationPrompt(String skinType, List<String> concerns, PromptService promptService) {
        Map<String, String> variables = PromptService.createVariables();
        variables.put("skinType", skinType);
        variables.put("concerns", String.join(", ", concerns));
        
        return promptService.getPrompt(PromptTemplate.GUEST_BASIC_RECOMMENDATION, variables);
    }
    
    /**
     * 게스트 스킨케어 상담 프롬프트를 생성합니다.
     *
     * @param skinType 피부 타입
     * @param concerns 피부 고민 목록
     * @param userQuestion 사용자 질문
     * @param promptService 프롬프트 서비스
     * @return 생성된 프롬프트 텍스트
     */
    public static String createConsultationPrompt(String skinType, List<String> concerns, String userQuestion, PromptService promptService) {
        Map<String, String> variables = PromptService.createVariables();
        variables.put("skinType", skinType);
        variables.put("concerns", String.join(", ", concerns));
        variables.put("userQuestion", userQuestion);
        
        return promptService.getPrompt(PromptTemplate.CHAT_SKINCARE_CONSULTATION, variables);
    }
}