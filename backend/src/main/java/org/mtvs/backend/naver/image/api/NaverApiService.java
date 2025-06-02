package org.mtvs.backend.naver.image.api;


import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NaverApiService {
    private final NaverImageAPIRepository naverImageAPIRepository;
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

//    public String getImage(String productName) {
//        return naverImageAPIRepository.findImgUrlByProductName(productName);
//    }
}
