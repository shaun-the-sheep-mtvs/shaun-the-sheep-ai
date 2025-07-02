package org.mtvs.backend.skintype.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Order(1) // Run first, before MBTI data initialization
public class SkinTypeDataInitializationService implements ApplicationRunner {
    
    private final SkinTypeRepository skinTypeRepository;
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeSkinTypes();
    }
    
    private void initializeSkinTypes() {
        if (skinTypeRepository.count() == 0) {
            log.info("Initializing skin types data...");
            
            for (Object[] data : SkinType.SKIN_TYPE_DATA) {
                SkinType skinType = new SkinType(
                    (Byte) data[0],        // id
                    (String) data[1],      // englishName
                    (String) data[2],      // koreanName
                    (String) data[3]       // description
                );
                skinTypeRepository.save(skinType);
            }
            
            log.info("Skin types data initialization completed. {} skin types created.", SkinType.SKIN_TYPE_DATA.length);
        } else {
            log.info("Skin types data already exists. Skipping initialization.");
        }
    }
}