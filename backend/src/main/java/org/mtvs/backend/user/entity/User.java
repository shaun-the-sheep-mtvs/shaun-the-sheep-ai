package org.mtvs.backend.user.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.mtvs.backend.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity // 테이블명이 user일 경우 일부 DB에서 예약어 충돌 가능하므로 users로 변경을 권장
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;               // 로그인 ID

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;                  // ex. "ROLE_USER"

    public enum SkinType {
        건성, 지성, 복합성, 민감성, 수분부족지성
    }

    @Enumerated(EnumType.STRING)
    @Column(

    )
    private SkinType skinType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> troubles = new ArrayList<>();

    public User() {}

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = "ROLE_USER";  // Default role
    }
}
