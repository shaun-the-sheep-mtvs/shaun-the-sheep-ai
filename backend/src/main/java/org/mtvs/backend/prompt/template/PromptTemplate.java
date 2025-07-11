package org.mtvs.backend.prompt.template;

public enum PromptTemplate {
    GUEST_BASIC_RECOMMENDATION("guest-basic-recommendation.txt"),
    USER_PRODUCT_RECOMMENDATION("user-product-recommendation.txt"),
    DEEP_ROUTINE_ANALYSIS("deep-routine-analysis.txt"),
    CHAT_SKINCARE_CONSULTATION("chat-skincare-consultation.txt");

    private final String fileName;

    PromptTemplate(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}