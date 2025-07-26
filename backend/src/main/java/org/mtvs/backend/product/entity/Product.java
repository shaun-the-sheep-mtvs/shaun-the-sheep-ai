package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Integer id;

    @Column(name = "formulation_id")
    private Byte formulationId;

    @Column(name = "ingredient_id_1")
    private Integer ingredientId1;

    @Column(name = "ingredient_id_2")
    private Integer ingredientId2;

    @Column(name = "ingredient_id_3")
    private Integer ingredientId3;

    @Column(name = "ingredient_id_4")
    private Integer ingredientId4;

    @Column(name = "ingredient_id_5")
    private Integer ingredientId5;

    @Column(name = "recommended_type")
    private Byte recommendedType;

    @Column(name = "product_name")
    private String productName;
    
    public void setProductName(String productName) {
        this.productName = productName;
        if (productName != null) {
            this.id = productName.hashCode();
        }
    }

    // Custom constructor to ensure id is always set from productName
    public Product(String productName) {
        this.productName = productName;
        if (productName != null) {
            this.id = productName.hashCode();
        }
    }

    // Make setId private to prevent accidental manual setting
    private void setId(Integer id) {
        this.id = id;
    }

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

    // Ingredient helper methods
    public void setIngredientIds(Integer... ingredientIds) {
        // Clear all first
        this.ingredientId1 = null;
        this.ingredientId2 = null;
        this.ingredientId3 = null;
        this.ingredientId4 = null;
        this.ingredientId5 = null;
        
        // Assign in order (max 5)
        for (int i = 0; i < Math.min(ingredientIds.length, 5); i++) {
            switch(i) {
                case 0: this.ingredientId1 = ingredientIds[i]; break;
                case 1: this.ingredientId2 = ingredientIds[i]; break;
                case 2: this.ingredientId3 = ingredientIds[i]; break;
                case 3: this.ingredientId4 = ingredientIds[i]; break;
                case 4: this.ingredientId5 = ingredientIds[i]; break;
            }
        }
    }
    
    public List<Integer> getIngredientIds() {
        List<Integer> ids = new ArrayList<>();
        if (ingredientId1 != null) ids.add(ingredientId1);
        if (ingredientId2 != null) ids.add(ingredientId2);
        if (ingredientId3 != null) ids.add(ingredientId3);
        if (ingredientId4 != null) ids.add(ingredientId4);
        if (ingredientId5 != null) ids.add(ingredientId5);
        return ids;
    }
    
    public void addIngredientId(Integer ingredientId) {
        List<Integer> current = getIngredientIds();
        if (!current.contains(ingredientId) && current.size() < 5) {
            current.add(ingredientId);
            setIngredientIds(current.toArray(new Integer[0]));
        }
    }
    
    public void removeIngredientId(Integer ingredientId) {
        List<Integer> current = getIngredientIds();
        current.remove(ingredientId);
        setIngredientIds(current.toArray(new Integer[0]));
    }
}