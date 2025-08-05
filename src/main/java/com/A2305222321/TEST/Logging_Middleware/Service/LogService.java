package com.A2305222321.TEST.Logging_Middleware.Service;


import com.A2305222321.TEST.Logging_Middleware.Model.Request.LogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LogService {

    private final RestTemplate restTemplate;
    private String cachedToken;
    private LocalDateTime tokenExpiry;


    @Value("${logging.api.url}")
    private String logApiUrl;


    @Autowired
    public LogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    private String getAuthToken() {
        if (cachedToken != null && tokenExpiry != null && tokenExpiry.isAfter(LocalDateTime.now())) {
            return cachedToken;
        }

        String authUrl = "http://20.244.56.144/evaluation-service/auth";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = """
    {
      "email": "anshparnami1@gmail.com",
      "name": "ANSH PARNAMI",
      "rollNo": "A2305222321",
      "accessCode": "yvhdda",
      "clientID": "90bff6d7-3d3b-442c-a4ff-f6334811f472",
      "clientSecret": "nscqtWZYPvvgUgpM"
    }
    """;

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, entity, Map.class);
            String token = (String) response.getBody().get("access_token");
            Integer expiryEpoch = (Integer) response.getBody().get("expires_in");
            cachedToken = token;
            tokenExpiry = LocalDateTime.ofEpochSecond(expiryEpoch, 0, java.time.ZoneOffset.UTC);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void log(String level, String source, String tag, String message) {
        String token = getAuthToken();
        if (token == null) return;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "level", level,
                "source", source,
                "tag", tag,
                "message", message
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(logApiUrl, entity, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}