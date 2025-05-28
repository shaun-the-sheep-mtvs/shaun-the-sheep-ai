package org.mtvs.backend.naver.image.api;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NaverApiService {
    private final NaverImageAPIRepository naverImageAPIRepository;

    public NaverApiService(NaverImageAPIRepository naverImageAPIRepository) {
        this.naverImageAPIRepository = naverImageAPIRepository;
    }
    public boolean isExistImage(String productName) {
        return naverImageAPIRepository.findByProductName(productName);
    }
    public void addImage(ImageDTO imageDTO) {
        String productName = imageDTO.getProductName();
        NaverImage naverImage = new NaverImage(imageDTO.getImageUrl(),productName );
        if(naverImageAPIRepository.findByProductName(productName)){
            throw new RuntimeException("이미 등록된 사진.");
        }
        System.out.println(naverImage);
        naverImageAPIRepository.save(naverImage);
    }

    public String getImage(String productName) {
        return naverImageAPIRepository.findImgUrlByProductName(productName);
    }
}
