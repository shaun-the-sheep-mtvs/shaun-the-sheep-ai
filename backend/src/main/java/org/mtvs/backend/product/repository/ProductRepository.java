package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT EXISTS (SELECT 1 FROM Product p WHERE p.productName = :productName AND p.imageUrl IS NOT NULL)")
    boolean existsImageUrlByProductName(@Param("productName") String productName);

    Product findByProductName(String productName);

}
