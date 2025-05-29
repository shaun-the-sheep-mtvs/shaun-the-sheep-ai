package org.mtvs.backend.checklist.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.mtvs.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklists")
public class CheckList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자(회원)와의 연관관계.
     * checklists.user_id → users.id
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /** 수분 값 (예: 1~5) */
    @Column(nullable = false)
    private Integer moisture;

    /** 유분 값 (예: 1~5) */
    @Column(nullable = false)
    private Integer oil;

    /** 민감도 값 (예: 1~5) */
    @Column(nullable = false)
    private Integer sensitivity;

    /** 탄력(긴장도) 값 (예: 1~5) */
    @Column(name = "tension", nullable = false)
    private Integer tension;

    private List<String> troubles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- 생성자, getter/setter ---

    public CheckList() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getMoisture() {
        return moisture;
    }

    public void setMoisture(Integer moisture) {
        this.moisture = moisture;
    }

    public Integer getOil() {
        return oil;
    }

    public void setOil(Integer oil) {
        this.oil = oil;
    }

    public Integer getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Integer sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Integer getTension() {
        return tension;
    }

    public void setTension(Integer tension) {
        this.tension = tension;
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
}

