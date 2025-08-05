package org.mtvs.backend.auth.social.service;

import org.mtvs.backend.auth.social.dto.Kakao.KakaoTokenResponse;
import org.mtvs.backend.auth.social.dto.Kakao.KakaoUserInfo;
import org.mtvs.backend.auth.social.entity.SocialAccount;
import org.mtvs.backend.auth.social.repository.SocialAccountRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.user.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;

public class KakaoAuthService {
	private final RestTemplate restTemplate;
	private final SocialAccountRepository socialAccountRepository;
	private final UserService userService;

	@Value("${kakao.client_id}")
	private String kakaoClientId;

	@Value("${kakao.redirect_url}")
	private String kakaoRedirectUri;

	@Value("${kakao.client_secret}")
	private String kakaoClientSecret;

	public KakaoAuthService(RestTemplate restTemplate, SocialAccountRepository socialAccountRepository,
		UserRepository userRepository) {
		this.restTemplate = restTemplate;
		this.socialAccountRepository = socialAccountRepository;
	}

	/**
	 * 📍
	 * @param code
	 * @param httpServletResponse
	 * @return
	 */

	public User kakaoLogin(String code, HttpServletResponse httpServletResponse) {
		// 1. 받은 'code' 를 이용해 카카오 토큰 발급 API에 POST 요청
		String tokenApiUrl = "https://kauth.kakao.com/oauth/token";

		// HTTP 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP 요청 본문 설정 (grant_type, client_id, redirect_uri, code, client_secret)
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("code", code);
		body.add("client_id", kakaoClientId);
		body.add("redirect_uri", kakaoRedirectUri);
		body.add("client_secret", kakaoClientSecret); // 민감 정보, 서버에서만 사용!

		// HTTP 요청 엔티티 생성
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

		// 실제 POST 요청 실행 및 응답 받기
		ResponseEntity<KakaoTokenResponse> responseEntity = restTemplate.postForEntity(tokenApiUrl, requestEntity,
			KakaoTokenResponse.class);

		// 2. 응답으로 받은 Access Token 추출
		String accessToken = responseEntity.getBody().getAccessToken();

		// 3. Access Token으로 사용자 정보 API에 GET 요청
		KakaoUserInfo userInfo = getUserInfoWithToken(accessToken);

		// 4. DB에 사용자 정보가 있는지 확인/저장하고 우리 서비스의 User 객체 반환
		User user = findOrCreateUser(userInfo);

		// 5. JWT 생성 및 쿠키에 저장 (httpServletRespose 사용)
		String jwtToken = generateJwt(user);
		Cookie cookie = new Cookie("kakao_jwt", jwtToken);
		httpServletResponse.addCookie(cookie);

		return user;
	}

	public User findOrCreateUser(KakaoUserInfo userInfo) {
		// 1. userInfo에서 사용자의 카카오 고유 ID를 가지고 social_accounts에서 회원 찾기
		SocialAccount socialAccount = socialAccountRepository.findSocialAccountByProviderUserId(userInfo.getId());
		if (socialAccount == null) {
			// 고유 ID가 없을 경우, 회원가입 진행
			User user = userService.
		}

	}

	/**
	 * 📍
	 * @param accessToken
	 * @return
	 */

	public KakaoUserInfo getUserInfoWithToken(String accessToken) {
		// 1. 카카오 사용자 정보 API 엔드포인트 지정
		String userInfoApiUrl = "https://kapi.kakao.com/v2/user/me";

		// 2. HTTP 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		// 3. HTTP 요청 엔티티 생성
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		// 4. HTTP GET 요청 전송 및 응답 받기
		try {
			ResponseEntity<KakaoUserInfo> responseEntity = restTemplate.exchange(
				userInfoApiUrl, HttpMethod.GET, requestEntity, KakaoUserInfo.class
			);

			// KakaoUserInfo 객체 반환
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				return responseEntity.getBody();
			} else {
				// 실패했을 경우(카카오 서버에서 응답이 올 경우) 예외 처리
				throw new RuntimeException("카카오 사용자 정보 요청 실패: " + responseEntity.getStatusCode());
			}
		} catch (Exception e) {
			// 요청 자체가 실패했을 경우 (네트워크 오류) 예외 처리
			throw new RuntimeException("카카오 API 통신 중 오류 발생", e);
		}
	}
}
