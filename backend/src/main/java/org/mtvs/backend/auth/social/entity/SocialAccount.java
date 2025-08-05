package org.mtvs.backend.auth.social.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "social_accounts")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "social_id")
	private Long id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "provider")
	private String provider;

	@Column(name = "provider_user_id")
	private Long providerUserId;

	@Column(name = "connected_at")
	private LocalDateTime connectedAt;

}
