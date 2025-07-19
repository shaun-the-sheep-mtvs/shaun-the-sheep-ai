package org.mtvs.backend.product.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ConcernMappingService {
    
    // Korean concern descriptions to ID mapping based on ConcernList.CONCERN_DATA
    private static final Map<String, Byte> KOREAN_TO_ID = new HashMap<>();
    
    static {
        KOREAN_TO_ID.put("건조함", (byte) 1);      // dryness
        KOREAN_TO_ID.put("번들거림", (byte) 2);     // oiliness
        KOREAN_TO_ID.put("민감함", (byte) 3);      // sensitivity
        KOREAN_TO_ID.put("탄력 저하", (byte) 4);    // elasticity
        KOREAN_TO_ID.put("홍조", (byte) 5);       // redness
        KOREAN_TO_ID.put("톤 안정", (byte) 6);     // unevenTone
        KOREAN_TO_ID.put("색소침착", (byte) 7);     // hyperpigment
        KOREAN_TO_ID.put("잔주름", (byte) 8);      // fineLines
        KOREAN_TO_ID.put("모공 케어", (byte) 9);    // pores
        KOREAN_TO_ID.put("트러블", (byte) 10);     // breakouts
        KOREAN_TO_ID.put("칙칙함", (byte) 11);     // dullness
        KOREAN_TO_ID.put("다크써클", (byte) 12);    // darkCircles
        KOREAN_TO_ID.put("결 거칠음", (byte) 13);   // roughTexture
    }
    
    // English label to ID mapping (backup)
    private static final Map<String, Byte> ENGLISH_TO_ID = new HashMap<>();
    
    static {
        ENGLISH_TO_ID.put("dryness", (byte) 1);
        ENGLISH_TO_ID.put("oiliness", (byte) 2);
        ENGLISH_TO_ID.put("sensitivity", (byte) 3);
        ENGLISH_TO_ID.put("elasticity", (byte) 4);
        ENGLISH_TO_ID.put("redness", (byte) 5);
        ENGLISH_TO_ID.put("uneventone", (byte) 6);
        ENGLISH_TO_ID.put("hyperpigment", (byte) 7);
        ENGLISH_TO_ID.put("finelines", (byte) 8);
        ENGLISH_TO_ID.put("pores", (byte) 9);
        ENGLISH_TO_ID.put("breakouts", (byte) 10);
        ENGLISH_TO_ID.put("dullness", (byte) 11);
        ENGLISH_TO_ID.put("darkcircles", (byte) 12);
        ENGLISH_TO_ID.put("roughTexture", (byte) 13);
    }
    
    /**
     * Convert Korean concern descriptions to concern IDs
     * @param concerns List of Korean concern descriptions
     * @return List of concern IDs (1-13), maximum 3 items, sorted
     */
    public List<Byte> mapConcernsToIds(List<String> concerns) {
        if (concerns == null || concerns.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Byte> concernIds = new HashSet<>();
        
        for (String concern : concerns) {
            if (concern == null || concern.trim().isEmpty()) {
                continue;
            }
            
            String cleanConcern = concern.trim();
            
            // Try Korean mapping first
            Byte id = KOREAN_TO_ID.get(cleanConcern);
            
            // Try English mapping if Korean fails
            if (id == null) {
                id = ENGLISH_TO_ID.get(cleanConcern.toLowerCase().replaceAll("\\s+", ""));
            }
            
            // Try partial matching for Korean concerns
            if (id == null) {
                for (Map.Entry<String, Byte> entry : KOREAN_TO_ID.entrySet()) {
                    if (cleanConcern.contains(entry.getKey()) || entry.getKey().contains(cleanConcern)) {
                        id = entry.getValue();
                        break;
                    }
                }
            }
            
            if (id != null) {
                concernIds.add(id);
            }
        }
        
        // Convert to sorted list, limit to 3
        return concernIds.stream()
                .sorted()
                .limit(3)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Convert single concern string to ID
     * @param concern Korean or English concern string
     * @return Concern ID (1-13) or null if not found
     */
    public Byte mapConcernToId(String concern) {
        if (concern == null || concern.trim().isEmpty()) {
            return null;
        }
        
        String cleanConcern = concern.trim();
        
        // Try Korean mapping first
        Byte id = KOREAN_TO_ID.get(cleanConcern);
        
        // Try English mapping if Korean fails
        if (id == null) {
            id = ENGLISH_TO_ID.get(cleanConcern.toLowerCase().replaceAll("\\s+", ""));
        }
        
        return id;
    }
    
    /**
     * Get Korean concern description by ID
     * @param concernId Concern ID (1-13)
     * @return Korean concern description or null if not found
     */
    public String getKoreanConcernById(Byte concernId) {
        return KOREAN_TO_ID.entrySet().stream()
                .filter(entry -> entry.getValue().equals(concernId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Validate concern IDs are in valid range and properly ordered
     * @param concernIds List of concern IDs
     * @return true if all IDs are valid (1-13) and sorted
     */
    public boolean validateConcernIds(List<Byte> concernIds) {
        if (concernIds == null || concernIds.isEmpty()) {
            return true;
        }
        
        // Check valid range (1-13)
        for (Byte id : concernIds) {
            if (id == null || id < 1 || id > 13) {
                return false;
            }
        }
        
        // Check if sorted
        for (int i = 1; i < concernIds.size(); i++) {
            if (concernIds.get(i - 1) >= concernIds.get(i)) {
                return false;
            }
        }
        
        return true;
    }
}