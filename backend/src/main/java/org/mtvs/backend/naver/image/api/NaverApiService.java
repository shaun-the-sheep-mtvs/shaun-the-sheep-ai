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

    public NaverApiService(NaverImageAPIRepository naverImageAPIRepository, ProductRepository productRepository) {
        this.naverImageAPIRepository = naverImageAPIRepository;
        this.productRepository = productRepository;
    }
    public boolean isExistImage(String productName) {
        return productRepository.existsImageUrlByProductName(productName);
    }

    public void addImageUrl(String productName, String imageUrl) {
        Product product = productRepository.findByProductName(productName);
        product.setImageUrl(imageUrl);
        productRepository.save(product);

//        NaverImage naverImage = new NaverImage(imageDTO.getImageUrl(),productName );
//        if(!naverImageAPIRepository.existsNaverImageByProductName(productName)){
//            naverImageAPIRepository.save(naverImage);
//        }
//        System.out.println(naverImage);

    }





    public void addImage(ImageDTO imageDTO) {
        String productName = imageDTO.getProductName();

        if(!naverImageAPIRepository.existsNaverImageByProductName(productName)){
            NaverImage naverImage = new NaverImage(imageDTO.getImageUrl(),productName);
            naverImageAPIRepository.save(naverImage);
            System.out.println(naverImage);
        }

    }

    /**
     * url이 x인 NaverImage 찾는 메서드
     * @return List<NaverImage> 엔터티
    */
    private List<NaverImage> findNoUrlImages(){
        List<NaverImage> naverImages = naverImageAPIRepository.findNaverImagesByImgUrlIs("x");
        return naverImages;
    }

    @Transactional
    public void updateImage(ImageDTO imageDTO) {
        try{
            List<NaverImage> noImages = findNoUrlImages();
            for(NaverImage noImage : noImages){
                noImage.updateImgUrl(imageDTO.getImageUrl());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ResponseEntity<?> findAndUpdateImages() {
        List<NaverImage> noImages = findNoUrlImages();
        try {
        for(NaverImage noImage : noImages){
                JsonNode rootNode = objectMapper.readTree(apiSearchImage.get(apiSearchImage.urlEncode(noImage.getProductName())));
                System.out.println(rootNode.toString());
                JsonNode imageNode = rootNode.findValue("image");

            }
        }

        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        };
        return ResponseEntity.ok().body(noImages);
    }

//    public String getImage(String productName) {
//        return naverImageAPIRepository.findImgUrlByProductName(productName);
//    }
}
