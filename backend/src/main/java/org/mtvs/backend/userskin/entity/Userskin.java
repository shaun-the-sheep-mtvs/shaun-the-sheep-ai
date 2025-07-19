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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern1_id")
    private ConcernList concern1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern2_id")
    private ConcernList concern2;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern3_id")
    private ConcernList concern3;
    
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
        if (concerns != null && !concerns.isEmpty()) {
            this.concern1 = concerns.size() > 0 ? concerns.get(0) : null;
            this.concern2 = concerns.size() > 1 ? concerns.get(1) : null;
            this.concern3 = concerns.size() > 2 ? concerns.get(2) : null;
        }
        this.analysisDate = java.time.LocalDateTime.now();
    }
    
    public List<ConcernList> getConcerns() {
        List<ConcernList> concerns = new ArrayList<>();
        if (concern1 != null) concerns.add(concern1);
        if (concern2 != null) concerns.add(concern2);
        if (concern3 != null) concerns.add(concern3);
        return concerns;
    }
}
