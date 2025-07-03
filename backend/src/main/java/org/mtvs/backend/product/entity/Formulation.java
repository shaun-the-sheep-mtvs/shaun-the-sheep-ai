package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.global.entity.BaseEntity;

@Data
@Table(name = "formulations")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Formulation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;
    
    @Column(name = "english_name", nullable = false, unique = true)
    private String englishName;
    
    @Column(name = "korean_name", nullable = false, unique = true)
    private String koreanName;
    
    @Column(name = "description")
    private String description;
}