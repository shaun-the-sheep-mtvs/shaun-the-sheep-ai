package org.mtvs.backend.auth.model;

import java.util.Collection;
import java.util.List;

import org.mtvs.backend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Parse roles from user.getRoles() - e.g., "ROLE_USER,ROLE_ADMIN"
		return user.getRoles() != null && !user.getRoles().isEmpty()
			? List.of(new SimpleGrantedAuthority(user.getRoles()))
			: List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	// User 객체를 바로 꺼내서 비교하는게 아니라 Dto로 꺼내서 그걸로 비교해쟈요...
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// Convenience methods for controllers
	public String getEmail() {
		return user.getEmail();
	}

	public String getUserId() {
		return user.getId();
	}
}
