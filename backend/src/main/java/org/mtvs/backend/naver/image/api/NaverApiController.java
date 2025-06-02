package org.mtvs.backend.naver.image.api;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.*;

@RestController
@RequestMapping("/api/naver")
@RequiredArgsConstructor
public class NaverApiController {
    private final ApiSearchImage apiSearchImage;
    private final ProductService productService;
    private final NaverApiService naverApiService;


    //체크리스트시 api요청
    @GetMapping
    public ResponseEntity<?> saveImage(@AuthenticationPrincipal CustomUserDetails user) {
            return naverApiService.saveImageProductDB(user,productService);
    }


    //페이지 새로고침시.
    @GetMapping("/re")
    public ResponseEntity<?> reSaveImageFromDB() {
        return productService.updateProductsWithNoURL();
    }

}
