// src/main/java/org/mtvs/backend/checklist/service/CheckListService.java
package org.mtvs.backend.checklist.service;

import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.service.AuthService;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckListService {
    private final CheckListRepository repo;
    private final AuthService authService;

    public CheckListService(CheckListRepository repo, AuthService authService) {
        this.repo = repo;
        this.authService = authService;
    }

    public CheckListResponse create(String username, CheckListRequest req) {
        // 1) 사용자 조회
        User user = authService.findByUsername(username);

        // 2) 엔티티 생성
        CheckList entity = new CheckList();
        entity.setUser(user);
        entity.setMoisture(req.getMoisture());
        entity.setOil(req.getOil());
        entity.setSensitivity(req.getSensitivity());
        entity.setTension(req.getTension());

        // 3) 저장
        CheckList saved = repo.save(entity);

        // 4) DTO 로 변환하여 반환
        return toDto(saved);
    }

    public List<CheckListResponse> findAllForUser(String username) {
        User user = authService.findByUsername(username);
        return repo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** 엔티티 → DTO 변환 헬퍼 */
    private CheckListResponse toDto(CheckList e) {
        CheckListResponse dto = new CheckListResponse();
        dto.setId(e.getId());
        dto.setMoisture(e.getMoisture());
        dto.setOil(e.getOil());
        dto.setSensitivity(e.getSensitivity());
        dto.setTension(e.getTension());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}



