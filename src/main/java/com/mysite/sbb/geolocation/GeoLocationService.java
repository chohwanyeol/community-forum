package com.mysite.sbb.geolocation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class GeoLocationService {

    private static final String GEOLOCATION_URL = "https://geolocation.apigw.ntruss.com/geolocation/v2/geoLocation";

    public String getGeoLocation(String ipAddress) {
        // API 호출 URL 생성
        String url = GEOLOCATION_URL + "?ip=" + ipAddress + "&ext=f&responseFormatType=json&enc=utf8";

        // 타임스탬프를 밀리초로 생성
        String timestamp = String.valueOf(System.currentTimeMillis());

        // 실제 Access Key를 입력
        String accessKey = "ncp_iam_BPAMKR1Sz7odQUt7tLdF";

        // API 서명을 생성
        String signature = generateSignature(timestamp, accessKey);

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-timestamp", timestamp);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", signature);

        // HttpEntity를 사용하여 헤더와 URL을 요청에 포함
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate을 사용하여 API 호출 (exchange 사용)
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // JSON 응답에서 r1, r2 값 추출
        String result = extractRegionInfo(responseEntity.getBody());

        return result; // 지역 정보 반환 (r1, r2)
    }


    private String extractRegionInfo(String jsonResponse) {
        try {
            // ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // "geoLocation" 노드를 가져옴
            JsonNode geoLocationNode = rootNode.path("geoLocation");

            // r1, r2 값을 가져옴
            String r1 = geoLocationNode.path("r1").asText("Unknown");
            String r2 = geoLocationNode.path("r2").asText("Unknown");

            // r1, r2 결합하여 반환
            return r1 + " " + r2;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing location data";
        }
    }

    private String generateSignature(String timestamp, String accessKey) {
        String secretKey = "ncp_iam_BPKMKRIEsTOFifLNo8OodBD84wOknVE4bI";
        String message = timestamp + accessKey;

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(message.getBytes());
            return new String(Base64.getEncoder().encode(hash));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
