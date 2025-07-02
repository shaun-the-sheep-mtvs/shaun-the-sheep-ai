package org.mtvs.backend.userskin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.global.entity.BaseEntity;
import org.mtvs.backend.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_skins")
public class Userskin extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private MBTIList skinType;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_skin_concerns",
        joinColumns = @JoinColumn(name = "user_skin_id"),
        inverseJoinColumns = @JoinColumn(name = "concern_id")
    )
    private List<ConcernList> concerns = new ArrayList<>();
    
    @Column(name = "analysis_date")
    private java.time.LocalDateTime analysisDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    public Userskin(User user) {
        this.user = user;
        this.analysisDate = java.time.LocalDateTime.now();
    }
    
    public Userskin(User user, MBTIList skinType, List<ConcernList> concerns) {
        this.user = user;
        this.skinType = skinType;
        this.concerns = concerns != null ? concerns : new ArrayList<>();
        this.analysisDate = java.time.LocalDateTime.now();
    }
}
