package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT EXISTS (SELECT 1 FROM Product p WHERE p.productName = :productName AND p.imageUrl IS NOT NULL AND p.imageUrl != 'x' AND p.imageUrl != '')")
    boolean existsImageUrlByProductName(@Param("productName") String productName);
    
    @Query("SELECT p FROM Product p WHERE (p.imageUrl IS NULL OR p.imageUrl = 'x' OR p.imageUrl = '')")
    List<Product> findProductsWithoutValidImageUrl();

    Product findByProductName(String productName);

//    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:query%")
//    List<Product> findProductsByProductNameContaining(String query);
    List<Product> findProductsById(String userId);
//    List<Product> findByUserIdAndFormulationType(String userId, String formulationType);

    boolean existsProductByProductName(String productName);


    List<Product> findAllByImageUrl(String imageUrl);
}
