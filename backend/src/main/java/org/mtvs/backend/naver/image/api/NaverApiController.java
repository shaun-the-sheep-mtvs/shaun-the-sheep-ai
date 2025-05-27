package org.mtvs.backend.naver.image.api;


import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.product.dto.ProductDTO;
import org.mtvs.backend.product.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/naver")
public class NaverApiController {
    private final ApiSearchImage apiSearchImage;
    private final ProductService productService;
    public NaverApiController(ApiSearchImage apiSearchImage, ProductService productService) {
        this.apiSearchImage = apiSearchImage;
        this.productService = productService;
    }

    @GetMapping
    public String[] getImage(@AuthenticationPrincipal CustomUserDetails user) {
      //수정요망  List<ProductDTO> dtos =productService.getProducts(user.getUser().getId());
        String[] text= new String[3];
    /*    for(ProductDTO dto : dtos) {
            int i=0;
            text[i]=dto.getProductName();
            i++;
            if(text.length==i){
                break;
            }
        }
*/
        text[0] ="이니스프리 그린티 밸런싱 스킨";
        text[1] ="라운드랩 1025 독도 토너";
        text[2] ="닥터지 레드 블레미쉬 클리어 수딩 토너";
        String[] response = new String[3] ;
        for(int i=0; i<response.length; i++){
            response[i] = apiSearchImage.get(apiSearchImage.urlEncode(text[i]));
        }
        return response;
    }
    /*
    * 1. get 3번때리기 -> 별로 안좋은거같음
    * get 3번때리고 응답 3번 받기. db저장하기 추천제품 띄우기.
    *
    *
    * */
}
