package org.mtvs.backend.checklist.service;

import jakarta.transaction.Transactional;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.mtvs.backend.checklist.repository.MBTIMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class CheckListService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CheckListRepository checkListRepo;
    @Autowired
    private MBTIMappingRepository mbtiMappingRepo;

    // 기존 repo, userRepo 주입 생략

    @Transactional
    public CheckListResponse create(CheckListRequest req, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        CheckList entity = new CheckList();
        entity.setUser(user);
        entity.setMoisture(req.getMoisture());
        entity.setOil(req.getOil());
        entity.setSensitivity(req.getSensitivity());
        entity.setTension(req.getTension());
        CheckList saved = checkListRepo.save(entity);

        String mbtiCode = getSkinTypeByEmail(user.getEmail());

        // 4) 매핑 테이블에서 한글 SkinType(enum) 조회
        User.SkinType newSkinType = mbtiMappingRepo.findById(mbtiCode)
                .orElseThrow(() ->
                        new IllegalStateException("Unknown MBTI code: " + mbtiCode))
                .getSkinType();

        // 5) 유저 엔티티에 세팅 후 저장
        user.setSkinType(newSkinType);
        user.setTroubles(req.getTroubles());
        userRepo.save(user);

        return toDto(saved);
    }

    @Transactional
    public List<CheckListResponse> findAllForCurrentUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return checkListRepo.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toDto).toList();
    }

    private CheckListResponse toDto(CheckList e) {
        CheckListResponse dto = new CheckListResponse();
        dto.setId(e.getId());
        dto.setMoisture(e.getMoisture());
        dto.setOil(e.getOil());
        dto.setSensitivity(e.getSensitivity());
        dto.setTension(e.getTension());
        dto.setTroubles(e.getTroubles());        // ← 추가
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    public String getSkinTypeByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        User userEntity = user.get();
        String userId = userEntity.getId();

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
        String skinType = ratings.get("moisture") + ratings.get("oil") +
                          ratings.get("sensitivity") + ratings.get("tension");
        System.out.println(skinType);
        return skinType;
    }
    @Transactional
    public Optional<CheckListResponse> findLatestForUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // 최신 체크리스트 가져오기
        Optional<CheckListResponse> maybeDto = checkListRepo
                .findFirstByUser_IdOrderByCreatedAtDesc(user.getId())
                .map(this::toDto);

        // 체크리스트가 있든 없든, user.getTroubles() 로 덮어씌운 DTO 리턴
        return Optional.of(
                maybeDto.orElseGet(CheckListResponse::new)  // 빈 DTO 또는 실제 DTO
        ).map(dto -> {
            dto.setTroubles(user.getTroubles());       // ← User 엔티티의 troubles
            return dto;
        });
    }
}




