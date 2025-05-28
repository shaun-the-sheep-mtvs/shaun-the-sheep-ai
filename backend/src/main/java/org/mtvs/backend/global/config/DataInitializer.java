package org.mtvs.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.mtvs.backend.checklist.model.MBTIMapping;
import org.mtvs.backend.checklist.repository.MBTIMappingRepository;
import org.mtvs.backend.user.entity.User.SkinType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MBTIMappingRepository mbtiMappingRepository;

    @Bean
    public CommandLineRunner loadMbtiMappings() {
        return args -> {
            // 이미 데이터가 있다면 초기화 하지 않음
            if (mbtiMappingRepository.count() > 0) {
                return;
            }

            List<MBTIMapping> mappings = List.of(
                    new MBTIMapping("MOST", SkinType.민감성),
                    new MBTIMapping("MOSL", SkinType.민감성),
                    new MBTIMapping("MBST", SkinType.민감성),
                    new MBTIMapping("MBSL", SkinType.민감성),
                    new MBTIMapping("MOIT", SkinType.지성),
                    new MBTIMapping("MOIL", SkinType.지성),
                    new MBTIMapping("MBIT", SkinType.복합성),
                    new MBTIMapping("MBIL", SkinType.복합성),
                    new MBTIMapping("DOST", SkinType.수분부족지성),
                    new MBTIMapping("DOSL", SkinType.수분부족지성),
                    new MBTIMapping("DBST", SkinType.건성),
                    new MBTIMapping("DBSL", SkinType.건성),
                    new MBTIMapping("DOIT", SkinType.수분부족지성),
                    new MBTIMapping("DOIL", SkinType.수분부족지성),
                    new MBTIMapping("DBIT", SkinType.건성),
                    new MBTIMapping("DBIL", SkinType.건성)
            );

            mbtiMappingRepository.saveAll(mappings);
        };
    }
}
