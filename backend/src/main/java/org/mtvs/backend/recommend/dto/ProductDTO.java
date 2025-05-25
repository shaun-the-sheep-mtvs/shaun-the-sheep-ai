package org.mtvs.backend.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.recommend.entity.Product;
import org.mtvs.backend.user.entity.User;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private String id;

    private Product.FormulationType formulationType;

    private List<String> ingredients;

    private String recommendedType;

    private String productName;

    private String userId;


    // 엔티티에서 DTO로 변환
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getFormulationType(),
                product.getIngredients(),
                product.getRecommendedType(),
                product.getProductName(),
                product.getUser() != null ? product.getUser().getId() : null
        );
    }

    // DTO에서 엔티티로 변환
    public Product toEntity(User user) {
        Product product = new Product();
        product.setId(this.id);
        product.setFormulationType(this.formulationType);
        product.setIngredients(this.ingredients);
        product.setRecommendedType(this.recommendedType);
        product.setProductName(this.productName);
        product.setUser(user);
        return product;
    }
}