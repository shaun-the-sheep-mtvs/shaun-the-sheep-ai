package org.mtvs.backend.auth.social.dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Profile {

	@JsonProperty("nickname")
	private String nickname;
}