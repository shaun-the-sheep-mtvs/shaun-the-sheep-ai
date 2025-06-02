package org.mtvs.backend.product.controller;

import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.service.ProductService;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

//    public List<ProductDTO> searchProducts(String keyword) {
//        List<ProductDTO> = switch (check){
//            case "all" -> productService.searchProductByWords(keyword);
//            case "brand" -> productService.searchProductsByBrand(keyword);
//            case "name" -> productService.searchProductsByName(keyword);
//            case "ingredients" -> productService.searchProductsByIngredients(keyword);
//        }
//
//        List<ProductDTO> productDTOs = productService.searchProducts(keyword);
//        return productDTOs;
//    }
}
