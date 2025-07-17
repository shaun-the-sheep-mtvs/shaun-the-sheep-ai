package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.service.FormulationService;
import org.mtvs.backend.product.service.IngredientService;
import org.mtvs.backend.product.entity.Ingredient;
import org.mtvs.backend.user.entity.User;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private Byte formulationId;
    private String formulation; // Add string formulation field for frontend compatibility
    private Byte recommendedType;
    private List<String> ingredients;
    private String productName;
    private String imageUrl;

    public ProductDTO(Integer id, Byte formulationId, String formulation, Byte recommendedType, List<String> ingredients, String productName, String imageUrl) {
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
                new ArrayList<>(), // ingredients will be resolved separately if needed
                product.getProductName(),
                product.getImageUrl()
        );
    }
    
    // 엔티티에서 DTO로 변환 (ingredient names 포함)
    public static ProductDTO fromEntity(Product product, FormulationService formulationService, IngredientService ingredientService) {
        List<String> ingredientNames = new ArrayList<>();
        List<Integer> ingredientIds = product.getIngredientIds();
        
        for (Integer id : ingredientIds) {
            Ingredient ingredient = ingredientService.findById(id);
            if (ingredient != null) {
                ingredientNames.add(ingredient.getKoreanName());
            }
        }
        
        return new ProductDTO(
                product.getId(),
                product.getFormulationId(),
                formulationService.getEnglishNameById(product.getFormulationId()),
                product.getRecommendedType(),
                ingredientNames,
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
                new ArrayList<>(), // ingredients will be empty - use the version with IngredientService
                product.getProductName(),
                product.getImageUrl()
        );
    }

    // DTO 에서 엔티티로 변환 (ingredient names는 별도 처리 필요)
    public Product toEntity() {
        Product product = new Product();
        product.setFormulationId(this.formulationId);
        product.setRecommendedType(this.recommendedType);
        product.setProductName(this.productName); // sets id via setter
        product.setImageUrl(this.imageUrl);
        return product;
    }
}




