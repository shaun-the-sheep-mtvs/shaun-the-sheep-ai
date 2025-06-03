package org.mtvs.backend.naver.image.api;


import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final NaverImageAPIRepository naverImageAPIRepository;
    private final ApiSearchImage apiSearchImage;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;


    public boolean isExistImage(String productName) {
        return productRepository.existsImageUrlByProductName(productName);
    }

    public void addImageUrl(String productName, String imageUrl) {
        Product product = productRepository.findByProductName(productName);
        product.setImageUrl(imageUrl);
        productRepository.save(product);

    }

}
