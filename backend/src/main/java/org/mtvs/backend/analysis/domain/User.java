package org.mtvs.backend.analysis.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.mtvs.backend.analysis.domain.enums.SkinType;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> troubles;

    @CurrentTimestamp
    private LocalDateTime created_at;

    public User() {
    }

    public User(String username, String email, SkinType skinType, Map<String, String> troubles) {
        this.username = username;
        this.email = email;
        this.skinType = skinType;
        this.troubles = troubles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public Map<String, String> getTroubles() {
        return troubles;
    }

    public void setTroubles(Map<String, String> troubles) {
        this.troubles = troubles;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
