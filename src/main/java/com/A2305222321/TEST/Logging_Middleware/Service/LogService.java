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

@Service
public class LogService {

    private final RestTemplate restTemplate;


    @Value("${logging.api.url}")
    private String logApiUrl;


    @Autowired
    public LogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void log(String authToken, String level, String pkg, String message) {

        LogRequest logRequest = new LogRequest("backend", level, pkg, message);


        HttpHeaders headers = new HttpHeaders();


        headers.set("Authorization","Bearer "+authToken);


        HttpEntity<LogRequest> requestEntity = new HttpEntity<>(logRequest, headers);

        System.out.println("Sending log to " + logApiUrl + ": " + logRequest);

        System.out.println("DEBUG: Final Authorization Header = " + headers.getFirst("Authorization"));

        try {

            ResponseEntity<String> response = restTemplate.postForEntity(
                    logApiUrl,
                    requestEntity,
                    String.class
            );
            System.out.println("Log API Response: " + response.getBody());
        } catch (RestClientException e) {

            System.err.println("Error calling Log API: " + e.getMessage());
        }
    }
}