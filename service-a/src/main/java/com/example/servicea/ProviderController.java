package com.example.servicea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProviderController {

    private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        logger.info("Service A: Received request for data");
        String data = "Hello from Service A!";
        logger.info("Service A: Responding with data: {}", data);
        return ResponseEntity.ok(data);
    }
}