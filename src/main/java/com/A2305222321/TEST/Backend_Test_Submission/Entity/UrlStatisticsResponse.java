package com.A2305222321.TEST.Backend_Test_Submission.Entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UrlStatisticsResponse {
    private String originalUrl;
    private LocalDateTime creationDate;
    private LocalDateTime expiryDate;
    private long totalClicks;
    private List<ClickDto> clicks;

    @Data
    @Builder
    public static class ClickDto {
        private LocalDateTime timestamp;
        private String referrer;
        private String location;
    }
}
