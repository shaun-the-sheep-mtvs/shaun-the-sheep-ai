package org.mtvs.backend.checklist.service;

import jakarta.transaction.Transactional;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.checklist.dto.CheckListRequest;
import org.mtvs.backend.checklist.dto.CheckListResponse;
import org.mtvs.backend.checklist.model.CheckList;
import org.mtvs.backend.checklist.repository.CheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.mtvs.backend.session.GuestData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Service
public class CheckListService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CheckListRepository checkListRepo;

    // Static MBTI code to SkinType mapping
    private static final Map<String, User.SkinType> MBTI_TO_SKIN_TYPE = Map.ofEntries(
        Map.entry("DBIL", User.SkinType.건성),
        Map.entry("DBIT", User.SkinType.건성),
        Map.entry("DBSL", User.SkinType.건성),
        Map.entry("DBST", User.SkinType.건성),
        Map.entry("DOIL", User.SkinType.수분부족지성),
        Map.entry("DOIT", User.SkinType.수분부족지성),
        Map.entry("DOSL", User.SkinType.수분부족지성),
        Map.entry("DOST", User.SkinType.수분부족지성),
        Map.entry("MBIL", User.SkinType.복합성),
        Map.entry("MBIT", User.SkinType.복합성),
        Map.entry("MBSL", User.SkinType.민감성),
        Map.entry("MBST", User.SkinType.민감성),
        Map.entry("MOIL", User.SkinType.지성),
        Map.entry("MOIT", User.SkinType.지성),
        Map.entry("MOSL", User.SkinType.민감성),
        Map.entry("MOST", User.SkinType.민감성)
    );

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

        // 5) 유저 엔티티에 세팅 후 저장
        user.setSkinType(MBTI_TO_SKIN_TYPE.get(mbtiCode));
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
        dto.setTroubles(e.getTroubles());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    // Helper method to calculate MBTI code from four values
    private String calculateMbtiCode(Integer moisture, Integer oil, Integer sensitivity, Integer tension) {
        String m = moisture != null && moisture >= 60 ? "M" : "D";
        String o = oil != null && oil >= 60 ? "O" : "B";
        String s = sensitivity != null && sensitivity >= 60 ? "S" : "I";
        String t = tension != null && tension >= 60 ? "T" : "L";
        return m + o + s + t;
    }

    // For guest
    public String calculateMbtiForGuest(GuestData guestData) {
        return calculateMbtiCode(
            guestData.getMoisture(),
            guestData.getOil(),
            guestData.getSensitivity(),
            guestData.getTension()
        );
    }

    // For user (by email)
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
        return calculateMbtiCode(
            result.getMoisture(),
            result.getOil(),
            result.getSensitivity(),
            result.getTension()
        );
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

    // MBTI 코드로 한글 SkinType 반환
    public String getSkinTypeForMbti(String mbtiCode) {
        User.SkinType type = MBTI_TO_SKIN_TYPE.get(mbtiCode);
        return type != null ? type.name() : "알 수 없음";
    }
}




