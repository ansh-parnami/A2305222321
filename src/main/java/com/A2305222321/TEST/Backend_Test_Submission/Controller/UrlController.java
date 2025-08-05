package com.A2305222321.TEST.Backend_Test_Submission.Controller;

import com.A2305222321.TEST.Backend_Test_Submission.Entity.CreateUrlRequest;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.CreateUrlResponse;
import com.A2305222321.TEST.Backend_Test_Submission.Service.UrlShortenerService;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.UrlMapping;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.UrlStatisticsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorturls")
    public ResponseEntity<CreateUrlResponse> createShortUrl(@RequestBody CreateUrlRequest request,
                                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authToken,
                                                            HttpServletRequest servletRequest) {
        UrlMapping urlMapping = urlShortenerService.createShortUrl(request, authToken);
        if (urlMapping == null) return ResponseEntity.status(HttpStatus.CONFLICT).build();  // custom shortcode conflict
        String shortLink = buildUrl(servletRequest, urlMapping.getShortcode());
        CreateUrlResponse response = new CreateUrlResponse(shortLink, urlMapping.getExpirationDate());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{shortcode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortcode,
                                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authToken,
                                                      HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        String ipAddress = request.getRemoteAddr();
        String originalUrl = urlShortenerService.getOriginalUrlAndLogClick(shortcode, referrer, ipAddress, authToken);

        if (originalUrl == null) return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @GetMapping("/shorturls/{shortcode}")
    public ResponseEntity<UrlStatisticsResponse> getUrlStatistics(@PathVariable String shortcode,
                                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authToken) {
        UrlStatisticsResponse stats = urlShortenerService.getUrlStatistics(shortcode, authToken);
        if (stats == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(stats);
    }

    private String buildUrl(HttpServletRequest request, String shortcode) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + shortcode;
    }
}
