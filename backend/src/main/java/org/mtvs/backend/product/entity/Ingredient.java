package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ingredients")
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    private Integer id; // Hash of korean_name
    
    @Column(name = "korean_name", nullable = false)
    private String koreanName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructor for creating new ingredients
    public Ingredient(Integer id, String koreanName) {
        this.id = id;
        this.koreanName = koreanName;
        this.createdAt = LocalDateTime.now();
    }
}