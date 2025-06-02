package org.mtvs.backend.routine.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Kinds {
    토너("토너"),
    앰플("앰플"),
    크림("크림"),
    세럼("세럼"),
    로션("로션"),
    팩("팩"),
    패드("패드"),
    스킨("스킨");

    private final String name;

    @JsonValue
    public String getNameTypeJson() {
        return name;
    }

}
