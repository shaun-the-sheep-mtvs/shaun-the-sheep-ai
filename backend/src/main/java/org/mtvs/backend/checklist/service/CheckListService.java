package org.mtvs.backend.checklist.service;

import jakarta.transaction.Transactional;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.dto.MBTIdto;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CheckListService {
    private final CheckListRepository repo;
    private final UserRepository userRepository;    // UserRepository 대신 User 사용

    public CheckListService(CheckListRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public CheckListResponse create(String username, CheckListRequest req) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        CheckList entity = new CheckList();
        entity.setUser(user);
        entity.setMoisture(req.getMoisture());
        entity.setOil(req.getOil());
        entity.setSensitivity(req.getSensitivity());
        entity.setTension(req.getTension());

        CheckList saved = checkListRepo.save(entity);
        return toDto(saved);
    }

    public List<CheckListResponse> findAllForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
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

    public String getSkinTypeByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        User userEntity = user.get();
        Long userId = userEntity.getId();

        Optional<CheckList> checkList = checkListRepo.findFirstByUser_IdOrderByCreatedAtDesc(userId);

        if (checkList.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        CheckList result = checkList.get();
        Map<String, String> ratings = new HashMap<>();
        // M : moisture , D : dry
        ratings.put("moisture", result.getMoisture() >= 60 ? "M" : "D");
        // O : oil , B : Barren(빈약함)
        ratings.put("oil", result.getOil() >= 60 ? "O" : "B");
        // S : sensitivity, I : Insensitive
        ratings.put("sensitivity", result.getSensitivity() >= 60 ? "S" : "I");
        // L : laxity(느슨함)
        ratings.put("tension", result.getTension() >= 60 ? "T" : "L");

        // 피부 타입 조합 생성 (예: MOSL, DBIL 등)
        String skinType = ratings.get("moisture") + ratings.get("sensitivity") +
                         ratings.get("oil") + ratings.get("tension");
        System.out.println(skinType);
        return skinType;
    }
}




