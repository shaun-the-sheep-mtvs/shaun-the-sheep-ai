package org.mtvs.backend.product.service;

import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.entity.ProductUserLink;
import org.mtvs.backend.product.repository.ProductUserLinkRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductUserLinkService {

    private final ProductUserLinkRepository productUserLinkRepository;

    public ProductUserLinkService(ProductUserLinkRepository productUserLinkRepository) {
        this.productUserLinkRepository = productUserLinkRepository;
    }

    public void saveLinks(Product product, String userId) {
        ProductUserLink productUserLink = new ProductUserLink();
        productUserLink.setProductId(product.getId());
        productUserLink.setUserId(userId);
        productUserLinkRepository.save(productUserLink);
    }
}
