package org.mtvs.backend.recommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mtvs.backend.recommend.dto.ProductDTO;
import org.mtvs.backend.recommend.entity.Product;
import org.mtvs.backend.recommend.repository.ProductRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void saveProducts(JsonNode productJson, String email) {

        String userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));

        JsonNode tonerArray = productJson.get("recommendations").get("toner");
        JsonNode serumArray = productJson.get("recommendations").get("serum");
        JsonNode lotionArray = productJson.get("recommendations").get("lotion");
        JsonNode creamArray = productJson.get("recommendations").get("cream");

        // 각 배열을 순회하면서 개별 제품 저장
        for (JsonNode product : tonerArray) {
            saveProduct(product, userId);
        }

        for (JsonNode product : serumArray) {
            saveProduct(product, userId);
        }

        for (JsonNode product : lotionArray) {
            saveProduct(product, userId);
        }

        for (JsonNode product : creamArray) {
            saveProduct(product, userId);
        }

    }
    
    public void saveProduct(JsonNode jsonNode, String userId) {

        // ObjectNode 로 캐스팅
        ObjectNode objectNode = (ObjectNode) jsonNode;


        ProductDTO dto = new ProductDTO();
        // 제품명
        dto.setProductName(objectNode.get("제품명") != null ?
                objectNode.get("제품명").asText() : null);

        // 추천타입
        dto.setRecommendedType(objectNode.get("추천타입") != null ?
                objectNode.get("추천타입").asText() : null);

        // 성분 (JSON 배열 → List<String>)
        JsonNode ingredientsNode = objectNode.get("성분");
        List<String> ingredients = new ArrayList<>();
        if (ingredientsNode != null && ingredientsNode.isArray()) {
            for (JsonNode node : ingredientsNode) {
                ingredients.add(node.asText());
            }
        }
        dto.setIngredients(ingredients);

        // formulationType (추천타입 기반 매핑)
        String recommendedType = dto.getRecommendedType();
        if (recommendedType != null) {
            try {
                dto.setFormulationType(Product.FormulationType.valueOf(recommendedType));
            } catch (IllegalArgumentException e) {
                dto.setFormulationType(null);
            }
        }
        dto.setUserId(userId);

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Product 엔티티로 변환 및 저장
        Product product = dto.toEntity(user);
        productRepository.save(product);
    }

}