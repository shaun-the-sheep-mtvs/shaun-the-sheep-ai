package org.mtvs.backend.auth.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

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
