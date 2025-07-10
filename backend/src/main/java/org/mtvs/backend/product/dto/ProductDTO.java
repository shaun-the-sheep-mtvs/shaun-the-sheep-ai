package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.service.FormulationService;
import org.mtvs.backend.user.entity.User;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private String id;
    private Byte formulationId;
    private String formulation; // Add string formulation field for frontend compatibility
    private Byte recommendedType;
    private List<String> ingredients;
    private String productName;
    private String imageUrl;

    public ProductDTO(String id, Byte formulationId, String formulation, Byte recommendedType, List<String> ingredients, String productName, String imageUrl) {
        this.id = id;
        this.formulationId = formulationId;
        this.formulation = formulation;
        this.recommendedType = recommendedType;
        this.ingredients = ingredients;
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    // 엔티티에서 DTO로 변환 (FormulationService 사용)
    public static ProductDTO fromEntity(Product product, FormulationService formulationService) {
        return new ProductDTO(
                product.getId(),
                product.getFormulationId(),
                formulationService.getEnglishNameById(product.getFormulationId()),
                product.getRecommendedType(),
                product.getIngredients(),
                product.getProductName(),
                product.getImageUrl()
        );
    }
    
    // Legacy method for backward compatibility - use FormulationService instead
    @Deprecated
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getFormulationId(),
                null, // formulation will be null - use the version with FormulationService
                product.getRecommendedType(),
                product.getIngredients(),
                product.getProductName(),
                product.getImageUrl()
        );
    }

    // DTO 에서 엔티티로 변환
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setFormulationId(this.formulationId);
        product.setRecommendedType(this.recommendedType);
        product.setIngredients(this.ingredients);
        product.setProductName(this.productName);
        product.setImageUrl(this.imageUrl);
        return product;
    }
}




