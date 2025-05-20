package org.mtvs.backend.auth.dto;

import org.mtvs.backend.auth.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegistrationDto {
    private String username;
    private String email;
    private String password;

    public RegistrationDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toEntity(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(passwordEncoder.encode(this.password));
        user.setRoles("ROLE_USER");
        return user;
    }
}
