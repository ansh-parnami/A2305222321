package com.A2305222321.TEST.Backend_Test_Submission.Entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateUrlRequest {

    private String url;
    private Integer validity; // in minutes
    private String shortcode;
}