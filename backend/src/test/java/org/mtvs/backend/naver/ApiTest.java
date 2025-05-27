package org.mtvs.backend.naver;


import org.junit.jupiter.api.*;
import org.mtvs.backend.naver.image.api.ApiSearchImage;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiTest {

    @BeforeEach()
    public void setUp() {


    }

    @DisplayName("API 요청 테스트")
    @Test
    public void requestTest() {
        //GIVEN
        String clientId = "TnNIUjJuo8gN5SXbqifW"; //애플리케이션 클라이언트 아이디값";
        String clientSecret = "18oUg19Ntx"; //애플리케이션 클라이언트 시크릿값";
        String url = "https://openapi.naver.com/v1/search/image";
        Map<String , String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        ApiSearchImage apiSearchImage = new ApiSearchImage();

        //WHEN
        /*(String apiUrl, Map<String, String> requestHeaders)*/
        String responseBody = apiSearchImage.get(url, requestHeaders);
        Assertions.assertFalse(responseBody.isEmpty());


    }
}
