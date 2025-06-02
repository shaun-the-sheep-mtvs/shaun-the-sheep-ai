package org.mtvs.backend.product.repository;

import org.mtvs.backend.product.entity.ProductUserLink;
import org.mtvs.backend.product.entity.ProductUserLinkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductUserLinkRepository extends JpaRepository<ProductUserLink, ProductUserLinkId> {
    boolean existsByProductIdAndUserId(String productId, String userId);

    List<ProductUserLink> findByUserId(String userId);
}
