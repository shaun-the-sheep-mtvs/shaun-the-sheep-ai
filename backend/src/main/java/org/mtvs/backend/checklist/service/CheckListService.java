package org.mtvs.backend.checklist.service;

import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckListService {
    private final CheckListRepository repo;
    private final AuthService authService;    // UserRepository 대신 AuthService 사용

    public CheckListService(CheckListRepository repo, AuthService authService) {
        this.repo = repo;
        this.authService = authService;
    }

    public CheckListResponse create(String username, CheckListRequest req) {
        User user = authService.findByUsername(username);  // Optional 풀고 예외처리
        CheckList entity = new CheckList();
        entity.setUser(user);
        entity.setMoisture(req.getMoisture());
        entity.setOil(req.getOil());
        entity.setSensitivity(req.getSensitivity());
        entity.setTension(req.getTension());
        CheckList saved = repo.save(entity);
        return toDto(saved);
    }

    public List<CheckListResponse> findAllForUser(String username) {
        User user = authService.findByUsername(username);
        return repo.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

