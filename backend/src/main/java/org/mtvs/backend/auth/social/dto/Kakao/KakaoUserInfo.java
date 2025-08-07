package org.mtvs.backend.auth.social.dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoUserInfo {

	/* 카카오 사용자 정보 API 응답 구조
	* 해당 서비스에서 사용할 정보 : email, nickname
	* {
  "id": 123456789,
  "kakao_account": {
    "profile_nickname_needs_agreement": false,
    "profile_image_needs_agreement": false,
    "profile": {
      "nickname": "JordyTest",
      "thumbnail_image_url": "http://yyy.kakao.com/...",
      "profile_image_url": "http://yyy.kakao.com/...",
      "is_default_image": false
    },
    "has_email": true,
    "email_needs_agreement": false,
    "is_email_valid": true,
    "email": "jordy@kakao.com"
  }
}
	* */

	@JsonProperty("id")
	private Long id; // 카카오 사용자 고유 ID

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount; // 중첩된 DTO

}