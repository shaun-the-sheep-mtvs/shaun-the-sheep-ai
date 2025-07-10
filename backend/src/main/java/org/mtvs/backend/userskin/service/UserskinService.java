package org.mtvs.backend.userskin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.userskin.entity.ConcernList;
import org.mtvs.backend.userskin.entity.MBTIList;
import org.mtvs.backend.userskin.entity.Userskin;
import org.mtvs.backend.userskin.repository.UserskinRepository;
import org.mtvs.backend.checklist.repository.SkinConcernRepository;
import org.mtvs.backend.checklist.repository.SkinMBTIRepository;
import org.mtvs.backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserskinService {
    
    private final UserskinRepository userskinRepository;
    private final SkinMBTIRepository skinMBTIRepository;
    private final SkinConcernRepository skinConcernRepository;
    
    @Transactional
    public Userskin createOrUpdateUserskin(User user, String mbtiCode, List<String> concernLabels) {
        // 기존 활성 스킨 프로필을 비활성화
        Optional<Userskin> existingUserskin = userskinRepository.findByUserAndIsActiveTrue(user);
        if (existingUserskin.isPresent()) {
            existingUserskin.get().setIsActive(false);
            userskinRepository.save(existingUserskin.get());
        }
        
        // MBTI 정보 조회
        Optional<MBTIList> mbtiList = skinMBTIRepository.findByMbtiCode(mbtiCode);
        if (mbtiList.isEmpty()) {
            throw new RuntimeException("Invalid MBTI code: " + mbtiCode);
        }
        
        // 관심사 정보 조회 - 최대 3개까지만 처리
        List<ConcernList> concerns = concernLabels.stream()
            .limit(3) // 최대 3개로 제한
            .map(labelOrDescription -> {
                // 먼저 영어 라벨로 찾아보고, 없으면 한국어 설명으로 찾기
                Optional<ConcernList> concern = skinConcernRepository.findByLabel(labelOrDescription);
                if (concern.isEmpty()) {
                    concern = skinConcernRepository.findByDescription(labelOrDescription);
                }
                return concern.orElseThrow(() -> new RuntimeException("Invalid concern label or description: " + labelOrDescription));
            })
            .collect(Collectors.toList());
        
        // 새로운 스킨 프로필 생성
        Userskin newUserskin = new Userskin(user, mbtiList.get(), concerns);
        return userskinRepository.save(newUserskin);
    }
    
    @Transactional(readOnly = true)
    public Optional<Userskin> getActiveUserskinByUser(User user) {
        return userskinRepository.findByUserAndIsActiveTrue(user);
    }
    
    @Transactional(readOnly = true)
    public Optional<Userskin> getActiveUserskinByUserId(String userId) {
        return userskinRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    @Transactional(readOnly = true)
    public Optional<Userskin> getLatestUserskinByUserId(String userId) {
        return userskinRepository.findLatestActiveByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public String getSkinTypeString(Userskin userskin) {
        if (userskin == null || userskin.getSkinType() == null) {
            return "알 수 없음";
        }
        return userskin.getSkinType().getSkinType().getKoreanName();
    }
    
    @Transactional(readOnly = true)
    public String getMbtiCode(Userskin userskin) {
        if (userskin == null || userskin.getSkinType() == null) {
            return null;
        }
        return userskin.getSkinType().getMbtiCode();
    }
    
    @Transactional(readOnly = true)
    public List<String> getConcernLabels(Userskin userskin) {
        if (userskin == null || userskin.getConcerns() == null) {
            return List.of();
        }
        return userskin.getConcerns().stream()
            .map(ConcernList::getDescription)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public boolean hasActiveUserskin(User user) {
        return userskinRepository.existsByUserAndIsActiveTrue(user);
    }
}