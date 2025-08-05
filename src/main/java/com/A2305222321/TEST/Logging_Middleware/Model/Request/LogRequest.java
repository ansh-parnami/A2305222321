package com.A2305222321.TEST.Logging_Middleware.Model.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogRequest {
    private String stack;
    private String level;
    private String pkg; // Renamed from "package" because 'package' is a reserved keyword in Java
    private String message;
}