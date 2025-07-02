package org.mtvs.backend.userskin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.userskin.entity.ConcernList;
import org.mtvs.backend.userskin.entity.MBTIList;
import org.mtvs.backend.checklist.repository.SkinConcernRepository;
import org.mtvs.backend.checklist.repository.SkinMBTIRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationRunner {

    private final SkinConcernRepository skinConcernRepository;
    private final SkinMBTIRepository skinMBTIRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("[데이터 초기화] 마스터 데이터 초기화 시작");
        
        initializeSkinConcerns();
        initializeSkinMBTIs();
        
        log.info("[데이터 초기화] 마스터 데이터 초기화 완료");
    }

    private void initializeSkinConcerns() {
        log.info("[데이터 초기화] SkinConcern 데이터 초기화 시작");
        
        int insertCount = 0;
        for (String[] concernData : ConcernList.CONCERN_DATA) {
            String label = concernData[0];
            String description = concernData[1];
            
            // 이미 존재하는지 확인
            if (!skinConcernRepository.existsByLabel(label)) {
                ConcernList skinConcern = new ConcernList(label, description);
                skinConcernRepository.save(skinConcern);
                insertCount++;
                log.debug("[데이터 초기화] SkinConcern 추가: {} - {}", label, description);
            }
        }
        
        log.info("[데이터 초기화] SkinConcern 데이터 초기화 완료 - {} 개 추가", insertCount);
    }

    private void initializeSkinMBTIs() {
        log.info("[데이터 초기화] SkinMBTI 데이터 초기화 시작");
        
        int insertCount = 0;
        for (String[] mbtiData : MBTIList.MBTI_DATA) {
            String mbtiCode = mbtiData[0];
            String koreanName = mbtiData[1];
            String description = mbtiData[2];
            
            // 이미 존재하는지 확인
            if (!skinMBTIRepository.existsByMbtiCode(mbtiCode)) {
                MBTIList skinMBTI = new MBTIList(mbtiCode, koreanName, description);
                skinMBTIRepository.save(skinMBTI);
                insertCount++;
                log.debug("[데이터 초기화] SkinMBTI 추가: {} - {} - {}", mbtiCode, koreanName, description);
            }
        }
        
        log.info("[데이터 초기화] SkinMBTI 데이터 초기화 완료 - {} 개 추가", insertCount);
    }
}