package org.mtvs.backend.naver.image.api;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/naver")
public class NaverApiController {
    private final ApiSearchImage apiSearchImage;
    private final ProductService productService;
    private final NaverApiService naverApiService;
    @Autowired
    public NaverApiController(ApiSearchImage apiSearchImage, ProductService productService, NaverApiService naverApiService) {
        this.apiSearchImage = apiSearchImage;
        this.productService = productService;
        this.naverApiService = naverApiService;
    }

    //front에서 보낼것.
    @GetMapping
    public ResponseEntity<?> saveImage(@AuthenticationPrincipal CustomUserDetails user) {
        //dto's productName=> Texts
       List<ProductDTO> dtos =productService.getProducts(user.getUser().getId());
        System.out.println(dtos.toString());
        List<String> texts= new ArrayList<>();
        for(ProductDTO dto : dtos) {
            texts.add(dto.getProductName());
        }

        List<String> responses = new ArrayList<>();
        for(int i=0; i<texts.size(); i++){
            if(!naverApiService.isExistImage(texts.get(i))){
                //이미지가 존재하지 않을때만 get요청 전송후 응답리스트에 추가
                responses.add(apiSearchImage.get(apiSearchImage.urlEncode(texts.get(i))));
                System.out.println(responses);
                //json 파싱
                //i++
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        for(int j=0; j<responses.size(); j++){
            try {
                JsonNode rootNode = objectMapper.readTree(responses.get(j));
                System.out.println(rootNode.toString());
                JsonNode imageNode = rootNode.findValue("image");
                System.out.println(j);
                naverApiService.addImage(new ImageDTO(imageNode.toString().replaceAll("\"",""),texts.get(j)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok(responses);
    }

    /*
    * 1. get 3번때리기 -> 별로 안좋은거같음
    * get 3번때리고 응답 3번 받기. db저장하기 추천제품 띄우기.
    *
    *
    * */
}
