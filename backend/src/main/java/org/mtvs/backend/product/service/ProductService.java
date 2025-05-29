package org.mtvs.backend.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mtvs.backend.naver.image.api.NaverApiService;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.dto.ProductsWithUserInfoResponseDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.repository.ProductRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NaverApiService naverApiService;
    public ProductService(ProductRepository productRepository, UserRepository userRepository, NaverApiService naverApiService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.naverApiService = naverApiService;
    }

    public List<ProductDTO> getProductsByFormulation(String userId, String formulation, int limit) {
        List<Product> products = productRepository.findByUserIdAndFormulationType(userId, formulation);
        //object->dto
        List<ProductDTO> productsDTO = new ArrayList<>();
        for(Product product : products){
            productsDTO.add(new ProductDTO(product));
        }
        //dto set imageUrl
        for(ProductDTO product : productsDTO){
            product.setImageUrl(naverApiService.getImage(product.getProductName()));
        }
        Collections.shuffle(productsDTO);

        return productsDTO.stream().limit(limit).collect(Collectors.toList());
    }

    public ProductsWithUserInfoResponseDTO getBalancedRecommendations(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ProductDTO> selectedProducts = new ArrayList<>();

        // 각 제형별로 3개씩 추가
        selectedProducts.addAll(getProductsByFormulation(userId, "toner", 3));
        selectedProducts.addAll(getProductsByFormulation(userId, "serum", 3));
        selectedProducts.addAll(getProductsByFormulation(userId, "lotion", 3));
        selectedProducts.addAll(getProductsByFormulation(userId, "cream", 3));
        System.out.println(ProductsWithUserInfoResponseDTO.create(user, selectedProducts));
        return ProductsWithUserInfoResponseDTO.create(user, selectedProducts);
    }



    public List<ProductDTO> getProducts(String userId){
        // userId에 해당된 추천 제품들이 있는지 확인
        boolean exists = productRepository.existsByUserId(userId);
        if (!exists) {
            throw new RuntimeException("해당 유저의 추천 제품이 존재하지 않습니다. 체크리스트는 최초 1회 필요합니다.");
        }
        // userId에 해당된 추천 제품 리스트 가져오기
        List<Product> products = productRepository.findByUserId(userId);
        List<ProductDTO> productDTOs = new ArrayList<>();

        // 엔티티를 DTO로 변환
        for (Product product : products) {
            ProductDTO productDTO = ProductDTO.fromEntity(product);
            productDTOs.add(productDTO);
        }
        return productDTOs;
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
            saveProduct(product, userId, "toner");
        }

        for (JsonNode product : serumArray) {
            saveProduct(product, userId, "serum");
        }

        for (JsonNode product : lotionArray) {
            saveProduct(product, userId, "lotion");
        }

        for (JsonNode product : creamArray) {
            saveProduct(product, userId, "cream");
        }

    }

    public void saveProduct(JsonNode jsonNode, String userId, String formulation) {

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
//        String recommendedType = dto.getRecommendedType();
//        if (recommendedType != null) {
//            try {
//                dto.setFormulationType(Product.FormulationType.valueOf(recommendedType));
//            } catch (IllegalArgumentException e) {
//                dto.setFormulationType(null);
//            }
//        }
        dto.setFormulation(formulation);
        dto.setUserId(userId);

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Product 엔티티로 변환 및 저장
        Product product = dto.toEntity(user);
        productRepository.save(product);
    }

}