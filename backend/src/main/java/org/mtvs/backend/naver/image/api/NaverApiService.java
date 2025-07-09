package org.mtvs.backend.naver.image.api;


import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final ApiSearchImage apiSearchImage;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;


    public boolean isExistImage(String productName) {
        return productRepository.existsImageUrlByProductName(productName);
    }

    public void addImageUrl(String productName, String imageUrl) {
        Product product = productRepository.findByProductName(productName);
        product.setImageUrl(imageUrl);
        System.out.println("save image url: " + imageUrl);
        productRepository.save(product);

    }

    public ResponseEntity<?> saveImageProductDB(CustomUserDetails user, ProductService productService) {

        List<ProductDTO> dtos = productService.getProductsByUserId(user.getUser().getId());

        Map<String, String> responses = new HashMap<>();
        List<String> productNames = new ArrayList<>();
        for (ProductDTO dto : dtos) {
            productNames.add(dto.getProductName());
        }


        int count = 0;
        for (int i = 0; i < productNames.size(); i++) {
            if (!isExistImage(productNames.get(i))) {
                responses.put(productNames.get(i), apiSearchImage.get(apiSearchImage.urlEncode(productNames.get(i))));
            }
            count++;
            if (count == 10) {
                try {
                    Thread.sleep(1010); // Sleep for just over 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Best practice
                    e.printStackTrace();
                }
                count = 0;
            }
        }

        System.out.println(responses);

        ObjectMapper objectMapper = new ObjectMapper();
        responses.forEach((productName, response) -> {
            try {
                JsonNode rootNode = objectMapper.readTree(response);
                JsonNode imageNode = rootNode.findValue("image");
                if(imageNode == null){
                    addImageUrl(productName,"x");
                }
                if(imageNode != null){
                    addImageUrl(productName,imageNode.toString().replaceAll("\"",""));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok(responses);
    }
}
