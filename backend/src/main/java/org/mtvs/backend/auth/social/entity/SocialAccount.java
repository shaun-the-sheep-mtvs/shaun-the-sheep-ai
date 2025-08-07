package org.mtvs.backend.auth.social.entity;

import java.time.LocalDateTime;

import org.mtvs.backend.auth.social.entity.enums.Providers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "social_accounts")
public class SocialAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "social_id", nullable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "provider", nullable = false)
	@Enumerated(EnumType.STRING)
	private Providers provider;

	@Column(name = "provider_user_id", nullable = false)
	private Long providerUserId;

	@Column(name = "connected_at", nullable = false)
	@Builder.Default
	private LocalDateTime connectedAt = LocalDateTime.now();

}
