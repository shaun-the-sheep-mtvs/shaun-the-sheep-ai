package org.mtvs.backend.product.controller;

import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search/products")
    public List<ProductDTO> searchProducts(@RequestParam String mode, @RequestParam String word) {
        // 검색 조건에 따라 ProductDTO List 반환
        List<ProductDTO> productList = switch (mode) {
            // All 파트 검색 메소드
            case "all" -> productService.findUniqueFromTwoQueries(word);
            // 브랜드 이름 검색 메소드
            case "brand" -> productService.searchAllByBrandAndName(word);
            // 제품 이름 검색 메소드
            case "productName" -> productService.searchAllByBrandAndName(word);
            // 성분 이름 검색 메소드
            case "ingredient" -> productService.searchAllByIngredient(word);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };

        return productList;
    }
}
