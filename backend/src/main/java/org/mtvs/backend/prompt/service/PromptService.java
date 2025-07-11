package org.mtvs.backend.prompt.service;

import org.mtvs.backend.prompt.exception.PromptLoadingException;
import org.mtvs.backend.prompt.template.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PromptService {
    
    private static final String PROMPTS_BASE_PATH = "prompts/";
    private final Map<PromptTemplate, String> promptCache = new ConcurrentHashMap<>();
    
    /**
     * 프롬프트 템플릿을 로드하고 변수를 치환합니다.
     *
     * @param template 프롬프트 템플릿
     * @param variables 치환할 변수들 (키-값 쌍)
     * @return 치환된 프롬프트 텍스트
     */
    public String getPrompt(PromptTemplate template, Map<String, String> variables) {
        String promptText = loadPromptTemplate(template);
        
        if (variables != null && !variables.isEmpty()) {
            return replaceVariables(promptText, variables);
        }
        
        return promptText;
    }
    
    /**
     * 프롬프트 템플릿을 로드합니다 (변수 치환 없음).
     *
     * @param template 프롬프트 템플릿
     * @return 원본 프롬프트 텍스트
     */
    public String getPrompt(PromptTemplate template) {
        return getPrompt(template, null);
    }
    
    /**
     * 프롬프트 템플릿을 로드합니다. 캐시를 사용하여 성능을 향상시킵니다.
     *
     * @param template 프롬프트 템플릿
     * @return 프롬프트 텍스트
     */
    private String loadPromptTemplate(PromptTemplate template) {
        return promptCache.computeIfAbsent(template, this::loadPromptFromFile);
    }
    
    /**
     * 파일에서 프롬프트를 로드합니다.
     *
     * @param template 프롬프트 템플릿
     * @return 프롬프트 텍스트
     */
    private String loadPromptFromFile(PromptTemplate template) {
        String filePath = PROMPTS_BASE_PATH + template.getFileName();
        
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                throw new PromptLoadingException("Prompt file not found: " + filePath);
            }
            
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new PromptLoadingException("Failed to load prompt template: " + template.name(), e);
        }
    }
    
    /**
     * 프롬프트 텍스트에서 변수를 치환합니다.
     * 변수 형식: ${변수명}
     *
     * @param promptText 원본 프롬프트 텍스트
     * @param variables 치환할 변수들
     * @return 치환된 프롬프트 텍스트
     */
    private String replaceVariables(String promptText, Map<String, String> variables) {
        String result = promptText;
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        
        return result;
    }
    
    /**
     * 캐시를 클리어합니다. 개발 환경에서 프롬프트 파일을 수정한 후 사용할 수 있습니다.
     */
    public void clearCache() {
        promptCache.clear();
    }
    
    /**
     * 헬퍼 메소드: 변수 맵을 쉽게 생성할 수 있도록 도와줍니다.
     *
     * @return 새로운 변수 맵
     */
    public static Map<String, String> createVariables() {
        return new HashMap<>();
    }
}