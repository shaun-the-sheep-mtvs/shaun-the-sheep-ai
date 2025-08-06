package org.mtvs.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialSignUpRequestDto {
	private String email;
	private String password;
	private String username;
}
