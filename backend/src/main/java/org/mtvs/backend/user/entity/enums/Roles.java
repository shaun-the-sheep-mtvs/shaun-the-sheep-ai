package org.mtvs.backend.user.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Roles {
	USER("USER"),
	ADMIN("ADMIN");

	private final String roleName;

	@JsonValue
	public String getRoleName() {
		return roleName;
	}
}
