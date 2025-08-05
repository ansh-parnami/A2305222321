package com.A2305222321.TEST.Backend_Test_Submission.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    @Id
    private String shortcode;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;

    @OneToMany(mappedBy = "urlMapping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClickStatistic> statistics;
}