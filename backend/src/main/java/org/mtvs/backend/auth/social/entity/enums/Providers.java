package org.mtvs.backend.auth.social.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Providers {
	KAKAO("KAKAO"),
	GOOGLE("GOOGLE"),
	NAVER("NAVER");

	private final String providerName;

	@JsonValue
	public String getProviderName() {
		return providerName;
	}
}
