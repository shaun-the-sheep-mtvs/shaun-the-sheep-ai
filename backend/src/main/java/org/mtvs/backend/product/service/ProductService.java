package org.mtvs.backend.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.naver.image.api.ApiSearchImage;
import org.mtvs.backend.naver.image.api.NaverApiService;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.dto.ProductsWithUserInfoResponseDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.entity.ProductUserLink;
import org.mtvs.backend.product.repository.ProductRepository;
import org.mtvs.backend.product.repository.ProductUserLinkRepository;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.mtvs.backend.product.dto.ProductDTO.fromEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NaverApiService naverApiService;
    private final ProductUserLinkRepository productUserLinkRepository;
    private final ProductUserLinkService productUserLinkService;
    private final ApiSearchImage apiSearchImage;
    private final ObjectMapper objectMapper;

    public List<ProductDTO> getProductsByFormulation(String userId, String formulation, int limit) {
        // userId 에 해당되는 Product 리스트 불러오기
        List<ProductDTO> productsByUserId = getProductsByUserId(userId);
        List<ProductDTO> productsByFormulation = new ArrayList<>();

        // productsByUserId 에서 특정 제형인 productDTO 생성
        for (ProductDTO product : productsByUserId) {
            if (product.getFormulation().equals(formulation)) {
                productsByFormulation.add(product);
            }
        }
        return productsByFormulation.stream().limit(limit).collect(Collectors.toList());
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

    // 중복 방지 -> Set 사용
    public List<ProductDTO> findUniqueFromTwoQueries(String query) {
        List<ProductDTO> product1 = searchAllByBrandAndName(query);
        List<ProductDTO> product2 = searchAllByIngredient(query);

//        Set<ProductDTO> productSet = new HashSet<>();
        // 중복 방지 (ProductName 기준으로 중복 방지)
        Set<ProductDTO> productSet = new TreeSet<>(Comparator.comparing(ProductDTO::getProductName));
        productSet.addAll(product1);
        productSet.addAll(product2);

        return new ArrayList<>(productSet);
    }

    // 주어진 키워드(query)로 브랜드 및 제품 검색
    public List<ProductDTO> searchAllByBrandAndName(String query) {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                productDTOs.add(fromEntity(product));
            }
        }
        return productDTOs;
    }


    // 주어진 키워드(query)로 성분 검색
    public List<ProductDTO> searchAllByIngredient(String query) {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            for (int i = 0; i < product.getIngredients().size(); i++) {
                if (product.getIngredients().get(i).contains(query.toLowerCase())){
                    productDTOs.add(fromEntity(product));
                }
            }
        }
        return productDTOs;
    }


    // userId 에 해당된 제품 리스트(productDTO) 가져오기 (NaverApiController.java 사용)
    public List<ProductDTO> getProductsByUserId(String userId){
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new RuntimeException("User not found");
        }
        List<ProductUserLink> productIdList = productUserLinkRepository.findByUserId(userId);
        List<ProductDTO> productDTOs = new ArrayList<>();

        // productIdList 에 해당되는 productId 에 연결된 product 객체를 반환해서
        // 이를 productDTO 리스트 객체로 반환
        for (ProductUserLink productUserLink : productIdList) {
            productDTOs.add(fromEntity(productUserLink.getProduct()));
        }

        return productDTOs;
    }


    public void saveProducts(JsonNode productJson, String userId) {
        JsonNode tonerArray = productJson.get("recommendations").get("toner");
        JsonNode serumArray = productJson.get("recommendations").get("serum");
        JsonNode lotionArray = productJson.get("recommendations").get("lotion");
        JsonNode creamArray = productJson.get("recommendations").get("cream");

        // 각 배열을 순회하면서 개별 제품 저장 (예외 발생해도 전체 트랜잭션은 계속)
        for (JsonNode product : tonerArray) {
            try {
                saveProduct(product, userId, "toner");
            } catch (Exception e) {
                // 개별 제품 저장 실패해도 계속 진행
                System.out.println("토너 제품 저장 실패: " + e.getMessage());
            }
        }

        for (JsonNode product : serumArray) {
            try {
                saveProduct(product, userId, "serum");
            } catch (Exception e) {
                System.out.println("세럼 제품 저장 실패: " + e.getMessage());
            }
        }

        for (JsonNode product : lotionArray) {
            try {
                saveProduct(product, userId, "lotion");
            } catch (Exception e) {
                System.out.println("로션 제품 저장 실패: " + e.getMessage());
            }
        }

        for (JsonNode product : creamArray) {
            try {
                saveProduct(product, userId, "cream");
            } catch (Exception e) {
                System.out.println("크림 제품 저장 실패: " + e.getMessage());
            }
        }
    }

    public void saveProduct(JsonNode jsonNode, String userId, String formulation) {
        // ObjectNode 로 캐스팅
        ObjectNode objectNode = (ObjectNode) jsonNode;
        ProductDTO dto = new ProductDTO();
        // 제품명

        if(!productRepository.existsProductByProductName(objectNode.get("제품명").asText())){
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

            dto.setFormulation(formulation);
            dto.setImageUrl("");

            // Product 엔티티로 변환 및 저장 (예외는 상위 메서드에서 처리)
            Product product = dto.toEntity();
            productRepository.save(product);

            // 링크 테이블에 저장
            productUserLinkService.saveLinks(product, userId);
        }
        else{
            System.out.println("중복! 저장!");
        }
    }


    //x인 product Entity 찾는 로직
    private List<Product> getAllProductsWithNoURL(){
        return productRepository.findAllByImageUrl("x");
    }

    /**
     * Product ImageURL 없는거 갱신하는 함수입니다
     *
     * */
    public ResponseEntity<?> updateProductsWithNoURL() {
        try{
        List<Product> noURLproducts = productRepository.findAllByImageUrl("x");
        noURLproducts.forEach(noUrlproduct -> {
            apiSearchImage.get(apiSearchImage.urlEncode( noUrlproduct.getProductName()));
            try {
                System.out.println(noUrlproduct.getProductName());
                JsonNode rootNode = objectMapper.readTree(apiSearchImage.reGet(apiSearchImage.urlEncode(noUrlproduct.getProductName())));
                System.out.println(rootNode);
                JsonNode imageNode = rootNode.findValue("thumbnail");
                naverApiService.addImageUrl(noUrlproduct.getProductName(),imageNode.toString().replaceAll("\"",""));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(200);
    }
}