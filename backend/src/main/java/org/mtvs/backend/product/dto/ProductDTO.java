package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.user.entity.User;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private String id;
    private String formulation;
    private List<String> ingredients;
    private String recommendedType;
    private String productName;
    private String imageUrl;

    public ProductDTO(String id, String formulation, List<String> ingredients, String recommendedType, String productName, String imageUrl) {
        this.id = id;
        this.formulation = formulation;
        this.ingredients = ingredients;
        this.recommendedType = recommendedType;
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    // 엔티티에서 DTO로 변환
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getFormulationType(),
                product.getIngredients(),
                product.getRecommendedType(),
                product.getProductName(),
                product.getImageUrl()
        );
    }

    // DTO 에서 엔티티로 변환
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setFormulationType(this.formulation);
        product.setIngredients(this.ingredients);
        product.setRecommendedType(this.recommendedType);
        product.setProductName(this.productName);
        return product;
    }
}




