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
import org.mtvs.backend.userskin.service.UserskinService;

import java.util.List;
import java.util.Optional;


@Service
public class CheckListService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CheckListRepository checkListRepo;
    @Autowired
    private UserskinService userskinService;

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

        // 5) Userskin 엔티티에 스킨 정보 저장
        userskinService.createOrUpdateUserskin(user, mbtiCode, req.getTroubles());

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

        // Userskin에서 troubles 정보 가져오기
        return Optional.of(
                maybeDto.orElseGet(CheckListResponse::new)  // 빈 DTO 또는 실제 DTO
        ).map(dto -> {
            userskinService.getActiveUserskinByUser(user)
                .ifPresent(userskin -> dto.setTroubles(userskinService.getConcernLabels(userskin)));
            return dto;
        });
    }

}
