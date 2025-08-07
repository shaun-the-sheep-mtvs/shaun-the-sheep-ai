package org.mtvs.backend.auth.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * 📍주어진 사용자(User) 정보를 바탕으로 JWT Access Token을 생성
	 * @param user 객체
	 * @return JWT Access Token 문자열
	 */
	public String createAccessToken(User user) {
		log.debug("Access Token 생성을 시작합니다. 사용자 이메일: {}", user.getEmail());

		// Jwt Claim 구성
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", user.getId());
		claims.put("username", user.getUsername());
		claims.put("email", user.getEmail());
		claims.put("roles", user.getRoles());

		// generateToken 메서드 호출
		String token = generateToken(claims, user.getEmail(), validityInMs);
		log.debug("Access Token 생성이 완료되었습니다.");
		return token;
	}

	/**
	 * 📍주어진 Claim, Subject, 만료 시간을 사용하여 JWT 토큰을 생성합니다.
	 * @param claims        토큰에 포함될 비공개 클레임(Claim) 맵
	 * @param subject       토큰의 주제(Subject). 일반적으로 사용자의 식별자(ID 또는 이메일)를 사용합니다.
	 * @param validityInMs  토큰의 유효 기간(밀리초 단위)
	 * @return 서명된 JWT 토큰 문자열
	 */
	public String generateToken(Map<String, Object> claims, String subject, long validityInMs) {
		log.debug("JWT 토큰 생성을 위한 최종 단계");

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + validityInMs);
		Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
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