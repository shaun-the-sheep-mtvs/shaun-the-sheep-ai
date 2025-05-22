package org.mtvs.backend.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity // 테이블명이 user일 경우 일부 DB에서 예약어 충돌 가능하므로 users로 변경을 권장
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                       // 자동증가 PK

    @Column(unique = true, nullable = false)
    private String username;               // 로그인 ID

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column()
    private String roles;                  // ex. "ROLE_USER"
    /*
    *    User user = new User(
                dto.getUsername(),
                dto.getEmail(),        // 이메일 위치 수정
                passwordEncoder.encode(dto.getPassword()),  // 비밀번호 위치 수정
                LocalDateTime.now()
        );

    *
    *
    * */
    public User(String username, String email, String encode, LocalDateTime now) {
        this.username = username;
        this.email = email;
        this.password = encode;
        this.createdAt = now;
    }

    public enum SkinType {
        건성, 지성, 복합성, 민감성, 수부지
    }

    @Enumerated(EnumType.STRING)
    @Column(

    )
    private SkinType skinType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> troubles;         // 피부 고민 목록(JSONB)

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public List<String> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<String> troubles) {
        this.troubles = troubles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
