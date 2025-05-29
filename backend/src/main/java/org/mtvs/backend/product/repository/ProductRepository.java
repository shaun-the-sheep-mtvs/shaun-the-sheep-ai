package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByUserId(String id);
    boolean existsByUserId(String id);

    List<Product> findByUserIdAndFormulationType(String userId, String formulationType);

    boolean existsProductByProductName(String productName);
}
