package com.A2305222321.TEST.Backend_Test_Submission.Service;

import com.A2305222321.TEST.Backend_Test_Submission.Entity.ClickStatistic;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.CreateUrlRequest;
import com.A2305222321.TEST.Backend_Test_Submission.Repository.ClickStatisticsRepository;
import com.A2305222321.TEST.Backend_Test_Submission.Repository.UrlMappingRepository;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.UrlMapping;
import com.A2305222321.TEST.Backend_Test_Submission.Entity.UrlStatisticsResponse;
import com.A2305222321.TEST.Logging_Middleware.Service.LogService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
@Service
public class UrlShortenerService {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORTCODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlMappingRepository urlMappingRepository;
    private final ClickStatisticsRepository clickStatisticsRepository;
    private final LogService logService;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository,
                               ClickStatisticsRepository clickRepository,
                               LogService logService) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickStatisticsRepository = clickRepository;
        this.logService = logService;
    }

    public UrlMapping createShortUrl(CreateUrlRequest request) {
        String shortcode;
        if (request.getShortcode() != null && !request.getShortcode().isBlank()) {
            shortcode = request.getShortcode();
            if (urlMappingRepository.existsById(shortcode)) {
                logService.log("warn", "service", "Shortener", "Custom shortcode conflict: " + shortcode);
                return null;
            }
        } else {
            shortcode = generateUniqueShortcode();
        }

        long validityMinutes = request.getValidity() != null ? request.getValidity() : 30;

        UrlMapping urlMapping = UrlMapping.builder()
                .shortcode(shortcode)
                .originalUrl(request.getUrl())
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(validityMinutes))
                .build();

        UrlMapping saved = urlMappingRepository.save(urlMapping);
        logService.log("info", "service", "Shortener", "Created short link with code: " + shortcode);
        return saved;
    }

    @Transactional
    public String getOriginalUrlAndLogClick(String shortcode, String referrer, String ipAddress) {
        UrlMapping urlMapping = urlMappingRepository.findById(shortcode).orElse(null);

        if (urlMapping == null) {
            logService.log("error", "redirect", "Shortener", "Shortcode not found: " + shortcode);
            return null;
        }

        if (urlMapping.getExpirationDate().isBefore(LocalDateTime.now())) {
            logService.log("warn", "redirect", "Shortener", "Expired link accessed: " + shortcode);
            return null;
        }

        ClickStatistic stat = ClickStatistic.builder()
                .urlMapping(urlMapping)
                .clickTimestamp(LocalDateTime.now())
                .referrer(referrer)
                .location("Noida, Uttar Pradesh, India") // Static location
                .build();
        clickStatisticsRepository.save(stat);

        logService.log("info", "redirect", "Shortener", "Redirecting shortcode " + shortcode);
        return urlMapping.getOriginalUrl();
    }

    public UrlStatisticsResponse getUrlStatistics(String shortcode) {
        UrlMapping urlMapping = urlMappingRepository.findById(shortcode).orElse(null);

        if (urlMapping == null) {
            logService.log("error", "statistics", "Shortener", "No stats for shortcode: " + shortcode);
            return null;
        }

        logService.log("info", "statistics", "Shortener", "Fetched stats for shortcode: " + shortcode);

        return UrlStatisticsResponse.builder()
                .originalUrl(urlMapping.getOriginalUrl())
                .creationDate(urlMapping.getCreationDate())
                .expiryDate(urlMapping.getExpirationDate())
                .totalClicks(urlMapping.getStatistics().size())
                .clicks(urlMapping.getStatistics().stream()
                        .map(stat -> UrlStatisticsResponse.ClickDto.builder()
                                .timestamp(stat.getClickTimestamp())
                                .referrer(stat.getReferrer())
                                .location(stat.getLocation())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private String generateUniqueShortcode() {
        String shortcode;
        do {
            shortcode = generateRandomString(SHORTCODE_LENGTH);
        } while (urlMappingRepository.existsById(shortcode));
        return shortcode;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}