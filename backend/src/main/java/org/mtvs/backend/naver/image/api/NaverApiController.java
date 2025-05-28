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


    /*
    * 해당하는 제품명이 없을때만 API 요청을 통해 이미지를 전달하고,
    * Json->
    * DB에 저장 시켜야함.
    * 1. text에 해당하는 제품을 db에서 갖고온다.
    * 2. 갖고 온거를 String[] 응답으로 전달해준다.
    * 3. 없다면 Get요청을 보내서 응답을 전달한다. -> 또한 db에 저장도
    * //밥먹고할일 db만들기.
    * 첫 3개 제품 이미지 띄우기 -> 성공
    * 성공시 맞춤형 제품 추천받기 이미지 띄우기도 해보기.
    * */
    //front에서 보낼것.
    @GetMapping
    public ResponseEntity<?> saveImage(@AuthenticationPrincipal CustomUserDetails user) {
        //dto's productName=> Texts
       List<ProductDTO> dtos =productService.getProducts(user.getUser().getId());
        List<String> texts= new ArrayList<>();
        for(ProductDTO dto : dtos) {
            texts.add(dto.getProductName());
        }
        //recommend 응답 한번에 몇개오나 분석.
        List<String> responses = new ArrayList<>();
        for(int i=0; i<texts.size(); i++){
            if(!naverApiService.isExistImage(texts.get(i))){
                responses.add(apiSearchImage.get(apiSearchImage.urlEncode(texts.get(i))));
                //json 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode rootNode = objectMapper.readTree(responses.get(i));
                    JsonNode imageNode = rootNode.findValue("image");
                    naverApiService.addImage(new ImageDTO(imageNode.toString().replaceAll("\"",""),texts.get(i)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
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
