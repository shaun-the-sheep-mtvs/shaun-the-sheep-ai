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
        return naverImageAPIRepository.existsNaverImageByProductName(productName);
    }
    public void addImage(ImageDTO imageDTO) {
        String productName = imageDTO.getProductName();

        if(!naverImageAPIRepository.existsNaverImageByProductName(productName)){
            NaverImage naverImage = new NaverImage(imageDTO.getImageUrl(),productName);
            naverImageAPIRepository.save(naverImage);
            System.out.println(naverImage);
        }

    }

    public String getImage(String productName) {
        return naverImageAPIRepository.findImgUrlByProductName(productName);
    }
}
