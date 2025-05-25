package org.mtvs.backend.routine.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Time {
    MORNING("MORNING"),
    NIGHT("NIGHT");


    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }
}