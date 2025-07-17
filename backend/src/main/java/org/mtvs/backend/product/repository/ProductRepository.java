package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT EXISTS (SELECT 1 FROM Product p WHERE p.productName = :productName AND p.imageUrl IS NOT NULL AND p.imageUrl != 'x' AND p.imageUrl != '')")
    boolean existsImageUrlByProductName(@Param("productName") String productName);
    
    @Query("SELECT p FROM Product p WHERE (p.imageUrl IS NULL OR p.imageUrl = 'x' OR p.imageUrl = '')")
    List<Product> findProductsWithoutValidImageUrl();

    Product findByProductName(String productName);

//    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:query%")
//    List<Product> findProductsByProductNameContaining(String query);
    List<Product> findProductsById(Integer productId);
//    List<Product> findByUserIdAndFormulationType(String userId, String formulationType);

    boolean existsProductByProductName(String productName);


    List<Product> findAllByImageUrl(String imageUrl);
    
    // Ingredient search methods
    @Query("""
        SELECT p FROM Product p 
        WHERE p.ingredientId1 = :ingredientId 
           OR p.ingredientId2 = :ingredientId 
           OR p.ingredientId3 = :ingredientId 
           OR p.ingredientId4 = :ingredientId 
           OR p.ingredientId5 = :ingredientId
        """)
    List<Product> findByIngredientId(@Param("ingredientId") Integer ingredientId);
    
    @Query("""
        SELECT DISTINCT p FROM Product p 
        WHERE p.ingredientId1 IN :ingredientIds 
           OR p.ingredientId2 IN :ingredientIds 
           OR p.ingredientId3 IN :ingredientIds 
           OR p.ingredientId4 IN :ingredientIds 
           OR p.ingredientId5 IN :ingredientIds
        """)
    List<Product> findByAnyIngredientIds(@Param("ingredientIds") List<Integer> ingredientIds);
}
