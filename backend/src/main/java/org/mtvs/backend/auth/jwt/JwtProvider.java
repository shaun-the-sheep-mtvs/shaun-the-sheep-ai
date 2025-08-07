package org.mtvs.backend.auth.jwt;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.mtvs.backend.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	private final long validityInMs = 3600000; // 1시간
	private final long refreshInMs = 60000;

	/**
	 * 📍주어진 사용자(User) 정보를 바탕으로 JWT Refresh Token을 생성
	 * @param user 토큰을 생성할 사용자 객체
	 * @return 생성된 JWT Refresh Token 문자열
	 * @throws IllegalArgumentException 사용자 객체가 null이거나 사용자 ID가 없는 경우
	 * @throws RuntimeException JWT 토큰 생성과정에서 오류가 발생한 경우
	 */
	public String createRefreshToken(User user) {
		if (user == null) {
			throw new IllegalArgumentException("사용자 객체는 null일 수 없습니다.");
		}
		if (user.getId() == null || user.getId().trim().isEmpty()) {
			throw new IllegalArgumentException("사용자 ID는 필수입니다.");
		}
		log.debug("Refresh Token 생성을 시작합니다. 사용자 ID : {}, 이메일 : {}", user.getEmail(), user.getId());

		try {
			Date now = new Date();
			Date expiryDate = new Date(now.getTime() + refreshInMs);
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

			String refreshToken = Jwts.builder()
				.setSubject(user.getId())
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.setId(UUID.randomUUID().toString())
				.claim("type", "refresh")
				.claim("iss", "shaun-the-sheep-ai")
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

			log.debug("Refresh Token 생성 완료. 사용자 ID : {}, 만료시간 : {}", user.getId(), expiryDate);

			return refreshToken;
		} catch (Exception e) {
			log.error("Refresh Token 생성 실패. 사용자 ID : {}, 오류 : {}", user.getId(), e);
			throw new RuntimeException("Refresh Token 생성에 실패했습니다.", e);
		}
	}

	/**
	 * 📍주어진 사용자(User) 정보를 바탕으로 JWT Access Token을 생성
	 * @param user 토큰을 생성할 사용자 객체. null이거나 필수 정보가 없으면 안됨
	 * @return 생성된 JWT Access Token 문자열
	 * @throws IllegalArgumentException 사용자 객체가 null이거나 필수 정보(ID, 권한)가 없는 경우
	 * @throws RuntimeException JWT 토큰 생성 과정에서 오류가 발생한 경우
	 */
	public String createAccessToken(User user) {
		if (user == null) {
			throw new IllegalArgumentException("사용자 객체는 null일 수 없습니다.");
		}
		if (user.getId() == null || user.getId().trim().isEmpty()) {
			throw new IllegalArgumentException("사용자 ID는 필수입니다.");
		}
		if (user.getRoles() == null) {
			throw new IllegalArgumentException("사용자 권한은 null일 수 없습니다.");
		}
		log.debug("Access Token 생성을 시작합니다. 사용자 ID : {}, 이메일 : {}", user.getEmail(), user.getId());

		try {
			Date now = new Date();
			Date expiryDate = new Date(now.getTime() + validityInMs);
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

			String AccessToken = Jwts.builder()
				.setSubject(user.getId())
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.setId(UUID.randomUUID().toString())
				.claim("id", user.getId())
				.claim("username", user.getUsername())
				.claim("email", user.getEmail())
				.claim("roles", user.getRoles())
				.claim("type", "access")
				.claim("iss", "shaun-the-sheep-ai")
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

			log.debug("Access Token 생성 완료. 사용자 ID : {}, 만료시간 : {}", user.getId(), expiryDate);

			return AccessToken;

		} catch (Exception e) {
			log.error("Access Token 생성 실패. 사용자 ID : {}, 오류 : {}", user.getId(), e);
			throw new RuntimeException("Access Token 생성에 실패했습니다.", e);
		}
	}

	/*
	 * JWT에서 이메일 추출
	 * */
	public String getEmailFromToken(String token) {
		token = cleanToken(token);
		log.debug("[JWT 파싱] 이메일 추출 시작");

		Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

		String email = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();

		log.debug("[JWT 파싱 완료] 이메일: {}", email);
		return email;
	}

	/*
	 * 토큰 유효성 검사
	 * */
	public boolean validateToken(String token) {
		try {
			token = cleanToken(token);
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			log.debug("[JWT 유효성 검사] 유효성 검사 통과");
			return true;

		} catch (Exception e) {
			log.warn("[JWT 유효성 검사 실패] 에러: {}", e.getMessage());
			return false;
		}
	}

	private String cleanToken(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return token;
	}

}