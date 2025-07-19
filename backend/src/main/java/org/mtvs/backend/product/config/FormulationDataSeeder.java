package org.mtvs.backend.product.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtvs.backend.product.entity.Formulation;
import org.mtvs.backend.product.repository.FormulationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FormulationDataSeeder implements CommandLineRunner {

    private final FormulationRepository formulationRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedFormulationData();
    }

    private void seedFormulationData() {
        log.info("Starting formulation data seeding process...");
        
        int insertedCount = 0;
        int updatedCount = 0;
        
        for (Object[] data : Formulation.FORMULATION_DATA) {
            Byte id = (Byte) data[0];
            String englishName = (String) data[1];
            String koreanName = (String) data[2];
            String description = (String) data[3];
            
            // Check if formulation with this ID already exists
            if (formulationRepository.existsById(id)) {
                // Update existing record if data has changed
                Formulation existing = formulationRepository.findById(id).orElse(null);
                if (existing != null && hasDataChanged(existing, englishName, koreanName, description)) {
                    existing.setEnglishName(englishName);
                    existing.setKoreanName(koreanName);
                    existing.setDescription(description);
                    formulationRepository.save(existing);
                    updatedCount++;
                    log.debug("Updated formulation ID {}: {}", id, englishName);
                }
            } else {
                // Insert new record
                Formulation formulation = new Formulation(id, englishName, koreanName, description);
                formulationRepository.save(formulation);
                insertedCount++;
                log.debug("Inserted formulation ID {}: {}", id, englishName);
            }
        }
        
        log.info("Formulation data seeding completed. Inserted: {}, Updated: {}", insertedCount, updatedCount);
    }
    
    private boolean hasDataChanged(Formulation existing, String englishName, String koreanName, String description) {
        return !englishName.equals(existing.getEnglishName()) ||
               !koreanName.equals(existing.getKoreanName()) ||
               !description.equals(existing.getDescription());
    }
}