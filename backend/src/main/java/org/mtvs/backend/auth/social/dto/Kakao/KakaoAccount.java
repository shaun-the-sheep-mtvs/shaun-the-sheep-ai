package org.mtvs.backend.auth.social.dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoAccount {

	@JsonProperty("profile")
	private Profile profile; // 중첩된 DTO

	@JsonProperty("email")
	private String email; // 사용자가 동의했을 경우에만 제공

	// 이외 필요한 필드들을 추가
	// @JsonProperty("gender")
	// private String gender;
	// ...
}