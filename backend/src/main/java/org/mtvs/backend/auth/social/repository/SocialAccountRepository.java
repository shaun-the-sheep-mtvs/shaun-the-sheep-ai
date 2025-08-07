package org.mtvs.backend.auth.social.repository;

import org.mtvs.backend.auth.social.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
	SocialAccount findSocialAccountByProviderUserId(Long providerUserId);

}
