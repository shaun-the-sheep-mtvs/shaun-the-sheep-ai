package org.mtvs.backend.auth.jwt.dto;

import org.mtvs.backend.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private User user;
	private String message;
	private String accessToken;
	private String refreshToken;
}
