package com.A2305222321.TEST.Backend_Test_Submission.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateUrlResponse {
    private String shortLink;
    private LocalDateTime expiry;
}