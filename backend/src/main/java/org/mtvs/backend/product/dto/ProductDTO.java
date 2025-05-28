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
    private String userId;

    //jun's
    private String imageUrl;

    // 모든 필드를 받는 생성자
    public ProductDTO(String id, String formulation, List<String> ingredients,
                     String recommendedType, String productName, String userId) {
        this.id = id;
        this.formulation = formulation;
        this.ingredients = ingredients;
        this.recommendedType = recommendedType;
        this.productName = productName;
        this.userId = userId;
    }

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.formulation = product.getFormulationType();
        this.ingredients = product.getIngredients();
        this.recommendedType = product.getRecommendedType();
        this.productName = product.getProductName();
        this.userId = product.getUser().getId();
    }

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
        product.setFormulationType(this.formulation);
        product.setIngredients(this.ingredients);
        product.setRecommendedType(this.recommendedType);
        product.setProductName(this.productName);
        product.setUser(user);
        return product;
    }
}




