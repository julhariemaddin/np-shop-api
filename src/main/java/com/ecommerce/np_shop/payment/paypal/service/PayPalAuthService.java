package com.ecommerce.np_shop.payment.paypal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Service
public class PayPalAuthService {
    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String secret;
    @Value("${paypal.is.sandbox}")
    private boolean isSandbox;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getBaseUrl(){
        return isSandbox ? "https://api-m.sandbox.paypal.com" : "https://api-m.paypal.paypal.com";
    }

    public String getAccessToken(){
        try{
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()+"/v1/oauth2/token"))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type" , "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200){
            throw new RuntimeException("Failed to get PayPal access token");
        }
        JsonNode jsonNode = objectMapper.readTree(response.body());
        return jsonNode.get("access_token").asText();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
