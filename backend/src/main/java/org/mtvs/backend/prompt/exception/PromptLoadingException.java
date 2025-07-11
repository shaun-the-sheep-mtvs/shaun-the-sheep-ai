package org.mtvs.backend.prompt.exception;

public class PromptLoadingException extends RuntimeException {
    public PromptLoadingException(String message) {
        super(message);
    }

    public PromptLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}