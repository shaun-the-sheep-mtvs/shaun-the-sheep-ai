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
        if (product != null) {
            product.setImageUrl(imageUrl);
            System.out.println("save image url for " + productName + ": " + imageUrl);
            productRepository.save(product);
        } else {
            System.out.println("Product not found: " + productName);
        }
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
                JsonNode itemsNode = rootNode.get("items");
                
                if (itemsNode != null && itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstItem = itemsNode.get(0);
                    JsonNode imageNode = firstItem.get("image");
                    
                    if (imageNode != null && !imageNode.asText().isEmpty()) {
                        String imageUrl = imageNode.asText();
                        addImageUrl(productName, imageUrl);
                        System.out.println("Successfully fetched image for: " + productName);
                    } else {
                        addImageUrl(productName, "x");
                        System.out.println("No image found for: " + productName);
                    }
                } else {
                    addImageUrl(productName, "x");
                    System.out.println("No search results for: " + productName);
                }
            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse JSON for product: " + productName + ", error: " + e.getMessage());
                addImageUrl(productName, "x");
            } catch (Exception e) {
                System.err.println("Unexpected error for product: " + productName + ", error: " + e.getMessage());
                addImageUrl(productName, "x");
            }
        });
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Process all products that don't have valid image URLs
     * This method can be called periodically or on-demand
     */
    public ResponseEntity<?> processProductsWithoutImages() {
        List<Product> productsWithoutImages = productRepository.findProductsWithoutValidImageUrl();
        
        if (productsWithoutImages.isEmpty()) {
            return ResponseEntity.ok("All products already have image URLs");
        }
        
        Map<String, String> responses = new HashMap<>();
        int count = 0;
        
        System.out.println("Processing " + productsWithoutImages.size() + " products without images");
        
        for (Product product : productsWithoutImages) {
            try {
                String productName = product.getProductName();
                String encodedName = apiSearchImage.urlEncode(productName);
                String response = apiSearchImage.get(encodedName);
                responses.put(productName, response);
                
                count++;
                // Rate limiting: 10 requests per second
                if (count % 10 == 0) {
                    Thread.sleep(1100); // Sleep for 1.1 seconds
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted while processing images");
                break;
            } catch (Exception e) {
                System.err.println("Error processing product: " + product.getProductName() + ", error: " + e.getMessage());
                // Continue with other products
            }
        }
        
        // Process the responses to update image URLs
        responses.forEach((productName, response) -> {
            try {
                JsonNode rootNode = objectMapper.readTree(response);
                JsonNode itemsNode = rootNode.get("items");
                
                if (itemsNode != null && itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstItem = itemsNode.get(0);
                    JsonNode imageNode = firstItem.get("image");
                    
                    if (imageNode != null && !imageNode.asText().isEmpty()) {
                        String imageUrl = imageNode.asText();
                        addImageUrl(productName, imageUrl);
                        System.out.println("Successfully updated image for: " + productName);
                    } else {
                        addImageUrl(productName, "x");
                        System.out.println("No image found for: " + productName);
                    }
                } else {
                    addImageUrl(productName, "x");
                    System.out.println("No search results for: " + productName);
                }
            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse JSON for product: " + productName + ", error: " + e.getMessage());
                addImageUrl(productName, "x");
            } catch (Exception e) {
                System.err.println("Unexpected error for product: " + productName + ", error: " + e.getMessage());
                addImageUrl(productName, "x");
            }
        });
        
        return ResponseEntity.ok(Map.of(
            "processed", productsWithoutImages.size(),
            "results", responses.size()
        ));
    }
    
    /**
     * Fetch image URL for a single product
     * Called when a new product is saved from Gemini
     */
    public String fetchImageUrlForProduct(String productName) {
        try {
            String encodedName = apiSearchImage.urlEncode(productName);
            String response = apiSearchImage.get(encodedName);
            
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemsNode = rootNode.get("items");
            
            if (itemsNode != null && itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode firstItem = itemsNode.get(0);
                JsonNode imageNode = firstItem.get("image");
                
                if (imageNode != null && !imageNode.asText().isEmpty()) {
                    return imageNode.asText();
                }
            }
            
            return "x"; // No image found
            
        } catch (Exception e) {
            System.err.println("Error fetching image for product: " + productName + ", error: " + e.getMessage());
            return "x";
        }
    }
}
