package com.A2305222321.TEST.Backend_Test_Submission.Entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime clickTimestamp;
    private String referrer;
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shortcode", nullable = false)
    @JsonIgnore
    private UrlMapping urlMapping;
}
