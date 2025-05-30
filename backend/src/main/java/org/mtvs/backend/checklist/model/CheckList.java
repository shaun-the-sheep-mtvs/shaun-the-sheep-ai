package org.mtvs.backend.checklist.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.session.GuestData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public CheckList createCheckListFromGuestData(GuestData guestData, User user) {
        CheckList checkList = new CheckList();
        checkList.setUser(user);
        checkList.setMoisture(guestData.getMoisture());
        checkList.setOil(guestData.getOil());
        checkList.setSensitivity(guestData.getSensitivity());
        checkList.setTension(guestData.getTension());
        checkList.setTroubles(guestData.getTroubles());
        // createdAt will be set automatically
        return checkList;
    }
}

