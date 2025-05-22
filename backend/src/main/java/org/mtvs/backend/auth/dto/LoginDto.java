package org.mtvs.backend.auth.dto;

public class LoginDto {
    private String email;
    private String password;

    // ① 기본 생성자 추가
    public LoginDto() { }

    // ② 기존 all-args 생성자
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ③ getter / setter
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
}

