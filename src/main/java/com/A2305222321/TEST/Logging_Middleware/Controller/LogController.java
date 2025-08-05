package com.A2305222321.TEST.Logging_Middleware.Controller;

import com.A2305222321.TEST.Logging_Middleware.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/test-logging")
    public String testLogging(@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {


        logService.log(
                authToken,
                "error",
                "handler",
                "received string, expected bool"
        );


        logService.log(
                authToken,
                "fatal",
                "db",
                "critical database connection failure."
        );


        logService.log(
                authToken,
                "info",
                "service",
                "User profile successfully updated."
        );

        return "Test logs have been sent to the external service!";
    }
}