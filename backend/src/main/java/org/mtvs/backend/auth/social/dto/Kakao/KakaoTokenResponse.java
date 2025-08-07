package org.mtvs.backend.auth.social.dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KakaoTokenResponse {
	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Integer expiresIn;

	@JsonProperty("refresh_token")
	private String refreshToken; // refresh_token은 갱신 시 없을 수도 있으므로 null 허용

	@JsonProperty("refresh_token_expires_in")
	private Integer refreshTokenExpiresIn; // refresh_token_expires_in도 null 허용

	private String scope;
}
