package org.mtvs.backend.userskin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.userskin.entity.ConcernList;
import org.mtvs.backend.userskin.entity.MBTIList;
import org.mtvs.backend.checklist.repository.SkinConcernRepository;
import org.mtvs.backend.checklist.repository.SkinMBTIRepository;
import org.mtvs.backend.skintype.entity.SkinType;
import org.mtvs.backend.skintype.repository.SkinTypeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Order(2) // Run after SkinTypeDataInitializationService
public class MBTIDataInitializationService implements ApplicationRunner {

    private final SkinConcernRepository skinConcernRepository;
    private final SkinMBTIRepository skinMBTIRepository;
    private final SkinTypeRepository skinTypeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("[데이터 초기화] MBTI 및 Concern 데이터 초기화 시작");

        initializeSkinConcerns();
        initializeSkinMBTIs();

        log.info("[데이터 초기화] MBTI 및 Concern 데이터 초기화 완료");
    }

    private void initializeSkinConcerns() {
        if (skinConcernRepository.count() > 0) {
            log.info("Skin concerns data already exists. Skipping initialization.");
            return;
        }
        
        log.info("[데이터 초기화] SkinConcern 데이터 초기화 시작");

        int insertCount = 0;
        for (String[] concernData : ConcernList.CONCERN_DATA) {
            String label = concernData[0];
            String description = concernData[1];

            ConcernList skinConcern = new ConcernList(label, description);
            skinConcernRepository.save(skinConcern);
            insertCount++;
            log.debug("[데이터 초기화] SkinConcern 추가: {} - {}", label, description);
        }

        log.info("[데이터 초기화] SkinConcern 데이터 초기화 완료 - {} 개 추가", insertCount);
    }

    private void initializeSkinMBTIs() {
        if (skinMBTIRepository.count() > 0) {
            log.info("MBTI data already exists. Skipping initialization.");
            return;
        }
        
        log.info("[데이터 초기화] SkinMBTI 데이터 초기화 시작");

        int insertCount = 0;
        for (Object[] mbtiData : MBTIList.MBTI_DATA) {
            String mbtiCode = (String) mbtiData[0];
            Byte skinTypeId = (Byte) mbtiData[1];
            String description = (String) mbtiData[2];

            // Get SkinType entity by ID
            SkinType skinType = skinTypeRepository.findById(skinTypeId)
                    .orElseThrow(() -> new RuntimeException("SkinType not found for ID: " + skinTypeId));

            MBTIList skinMBTI = new MBTIList(mbtiCode, skinType, description);
            skinMBTIRepository.save(skinMBTI);
            insertCount++;
            log.debug("[데이터 초기화] SkinMBTI 추가: {} - {} - {}", mbtiCode, skinType.getKoreanName(), description);
        }

        log.info("[데이터 초기화] SkinMBTI 데이터 초기화 완료 - {} 개 추가", insertCount);
    }
}