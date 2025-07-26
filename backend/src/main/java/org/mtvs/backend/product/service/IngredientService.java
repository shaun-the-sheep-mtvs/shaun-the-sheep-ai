package org.mtvs.backend.product.service;

import org.mtvs.backend.product.entity.Ingredient;
import org.mtvs.backend.product.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class IngredientService {
    
    private final IngredientRepository ingredientRepository;
    
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }
    
    public Set<Integer> resolveIngredientIds(List<String> koreanNames) {
        Set<Integer> ids = new HashSet<>();
        
        for (String name : koreanNames) {
            Integer id = hashIngredient(name);
            
            if (!ingredientRepository.existsById(id)) {
                Ingredient ingredient = new Ingredient(id, name);
                ingredientRepository.save(ingredient);
            }
            
            ids.add(id);
        }
        
        return ids;
    }
    
    public Integer hashIngredient(String korean) {
        return korean.trim()
                    .replaceAll("\\s+", "")
                    .toLowerCase()
                    .hashCode();
    }
    
    public Ingredient findById(Integer id) {
        return ingredientRepository.findById(id).orElse(null);
    }
    
    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }
}