package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.mtvs.backend.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Table(name = "products")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "formulation")
    private String formulation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients", columnDefinition = "jsonb")
    private List<String> ingredients;

    @Column(name = "recommended_type")
    private Byte recommendedType;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "concern_id_1")
    private Byte concernId1;

    @Column(name = "concern_id_2")
    private Byte concernId2;

    @Column(name = "concern_id_3")
    private Byte concernId3;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserLink> productUserLinks = new ArrayList<>();

    // Helper methods with automatic ordering
    public void setConcerns(Byte... concernIds) {
        // Remove nulls and duplicates, then sort
        List<Byte> sortedConcerns = Arrays.stream(concernIds)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .limit(3)
            .collect(Collectors.toList());
        
        // Clear all concerns first
        this.concernId1 = null;
        this.concernId2 = null;
        this.concernId3 = null;
        
        // Assign in order
        if (sortedConcerns.size() > 0) this.concernId1 = sortedConcerns.get(0);
        if (sortedConcerns.size() > 1) this.concernId2 = sortedConcerns.get(1);
        if (sortedConcerns.size() > 2) this.concernId3 = sortedConcerns.get(2);
    }

    public List<Byte> getConcernIds() {
        List<Byte> concerns = new ArrayList<>();
        if (concernId1 != null) concerns.add(concernId1);
        if (concernId2 != null) concerns.add(concernId2);
        if (concernId3 != null) concerns.add(concernId3);
        return concerns; // Already in order
    }

    // Add a concern while maintaining order
    public void addConcern(Byte concernId) {
        List<Byte> current = getConcernIds();
        current.add(concernId);
        setConcerns(current.toArray(new Byte[0]));
    }

    // Remove a concern while maintaining order
    public void removeConcern(Byte concernId) {
        List<Byte> current = getConcernIds();
        current.remove(concernId);
        setConcerns(current.toArray(new Byte[0]));
    }
}