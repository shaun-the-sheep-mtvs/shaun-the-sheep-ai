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
import java.util.Timer;

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

    //체크리스트시 api요청
    @GetMapping
    public ResponseEntity<?> saveImage(@AuthenticationPrincipal CustomUserDetails user) {
        // dto's productName=> Texts
        // 윤지 : getProduct 메소드에서 getProductsByUserId 메소드로 변경
       List<ProductDTO> dtos = productService.getProductsByUserId(user.getUser().getId());

//        System.out.println("dtosSize = " + dtos.size());

        List<String> texts= new ArrayList<>();
        List<String> notAlreadyUploads= new ArrayList<>();
        for(ProductDTO dto : dtos) {
            texts.add(dto.getProductName());
        }
        List<String> responses = new ArrayList<>();

        int count = 0;
        int sum=0;
        for(int i=0; i<texts.size(); i++){
            if(!naverApiService.isExistImage(texts.get(i))){
                //이미지가 존재하지 않을때만 get요청 전송후 응답리스트에 추가
                notAlreadyUploads.add(texts.get(i));
                responses.add(apiSearchImage.get(apiSearchImage.urlEncode(texts.get(i))));
                count++;
                sum++;
                System.out.println(count);
                if(count == 10){
                    try{
                        Thread.sleep(1010);
                        sum+=count;
                        count=0;

                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                //json 파싱
                //i++
            }
        }

        System.out.println(responses);

        ObjectMapper objectMapper = new ObjectMapper();
        for(int j=0; j<responses.size(); j++){
            try {
                JsonNode rootNode = objectMapper.readTree(responses.get(j));
                JsonNode imageNode = rootNode.findValue("image");
                naverApiService.addImageUrl(texts.get(j),imageNode.toString().replaceAll("\"",""));
                System.out.println("value j= "+j);
                System.out.println(texts.get(j));
                if(imageNode == null){
                    //JsonNode reRootNode = objectMapper.readTree(apiSearchImage.reGet(apiSearchImage.urlEncode(notAlreadyUploads.get(j))));
                    //JsonNode reimageNode = rootNode.findValue("image").elements().next();
                    naverApiService.addImage(new ImageDTO("x", notAlreadyUploads.get(j)));
                }
                if(imageNode != null){
                    naverApiService.addImage(new ImageDTO(imageNode.toString().replaceAll("\"",""),notAlreadyUploads.get(j)));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok(responses);
    }


    //페이지 새로고침시.
    @GetMapping("/re")
    public ResponseEntity<?> reSaveImageFromDB() {
        return naverApiService.findAndUpdateImages();
    }

    /*
    * 1. get 3번때리기 -> 별로 안좋은거같음
    * get 3번때리고 응답 3번 받기. db저장하기 추천제품 띄우기.
    *
    *
    * */
}
