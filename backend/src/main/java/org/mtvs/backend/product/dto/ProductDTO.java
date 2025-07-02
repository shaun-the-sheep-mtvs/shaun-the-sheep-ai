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
    private Byte recommendedType;
    private List<String> ingredients;
    private String productName;
    private String imageUrl;

    public ProductDTO(String id, String formulation, Byte recommendedType, List<String> ingredients, String productName, String imageUrl) {
        this.id = id;
        this.formulation = formulation;
        this.recommendedType = recommendedType;
        this.ingredients = ingredients;
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    // 엔티티에서 DTO로 변환
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getFormulation(),
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
        product.setFormulation(this.formulation);
        product.setRecommendedType(this.recommendedType);
        product.setIngredients(this.ingredients);
        product.setProductName(this.productName);
        return product;
    }
}




