package org.mtvs.backend.naver.image.api;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@Component
public class ApiSearchImage {
    @Value("${naver.api.id}")
    private String clientId ;
    @Value("${naver.api.secret}")
    private String clientSecret ;
    private  Map<String , String> requestHeaders = new HashMap<>();
    private String query = "?query=";
    private String display = "&display=";
    private int displayLength = 5;
    private String sortSim = "&sort=sim";

    //쇼핑몰
    String apiURL = "https://openapi.naver.com/v1/search/shop.json" ;

    //이미지검색 URL
    String searchURL = "https://openapi.naver.com/v1/search/image";

    public ApiSearchImage() {
    }


    /**
     * NaverApi를 사용하여 네이버 쇼핑몰에 Get요청을 보냅니다.
     * <p>반환값 사용 예
     *  <ul>
     *      <li>JsonNode rootNode = objectMapper.readTree(readBody);</b>
     *      <li>JsonNode imageNode = rootNode.findValue("image");</b>
     *  </ul>
     * </p>
     *
     * @param text : apiSearchImage.urlEncode('검색할 문자열')
     * @return readBody
     *
     */
    public String get(String text){
        System.out.println("GET SEND "+text);
        String URL = apiURL+query+text+display+displayLength+sortSim;
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        HttpURLConnection con = connect(URL);
        try {
            con.setRequestMethod("GET");

            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    public String reGet(String text) {
        System.out.println("GET SEND "+text);
        String URL = this.searchURL+query+text+display+(displayLength)+"&sort=sim";
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        HttpURLConnection con = connect(URL);
        try {
            con.setRequestMethod("GET");

            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    public HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    public String urlEncode(String text){
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        return text;
    }

    //
}
