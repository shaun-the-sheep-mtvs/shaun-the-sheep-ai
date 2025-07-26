package org.mtvs.backend.product.controller;

import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.entity.Ingredient;
import org.mtvs.backend.product.service.ProductService;
import org.mtvs.backend.product.service.IngredientService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final IngredientService ingredientService;

    public ProductController(ProductService productService, IngredientService ingredientService) {
        this.productService = productService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/search/products")
    public List<ProductDTO> searchProducts(@RequestParam String mode, @RequestParam String word) {
        // 검색 조건에 따라 ProductDTO List 반환
        List<ProductDTO> productList = switch (mode) {
            case "all" -> productService.searchAllByIngredient(word);
            case "brand" -> productService.searchAllByBrandAndName(word);
            case "productName" -> productService.searchAllByBrandAndName(word);
            case "ingredient" -> productService.searchAllByIngredient(word);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };

        return productList;
    }
    
    // Dedicated ingredient search endpoint
    @GetMapping("/search/ingredient")
    public ResponseEntity<List<ProductDTO>> searchByIngredient(@RequestParam String koreanName) {
        List<ProductDTO> products = productService.searchAllByIngredient(koreanName);
        return ResponseEntity.ok(products);
    }
    
    // Get all ingredients
    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.findAll();
        return ResponseEntity.ok(ingredients);
    }
    
    // Get ingredient by ID
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.findById(id);
        if (ingredient != null) {
            return ResponseEntity.ok(ingredient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get products by ingredient ID
    @GetMapping("/ingredients/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByIngredientId(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.findById(id);
        if (ingredient == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProductDTO> products = productService.searchAllByIngredient(ingredient.getKoreanName());
        return ResponseEntity.ok(products);
    }
}
