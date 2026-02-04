package com.example.servicea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProviderController {

    private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        long startTime = System.currentTimeMillis();
        logger.info("[Service-A] Endpoint: /health, Status: Starting");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        
        long latency = System.currentTimeMillis() - startTime;
        logger.info("[Service-A] Endpoint: /health, Status: 200, Latency: {}ms", latency);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/echo")
    public ResponseEntity<Map<String, String>> echo(@RequestParam(name = "msg", required = false) String msg) {
        long startTime = System.currentTimeMillis();
        logger.info("[Service-A] Endpoint: /echo, Status: Starting, Message: {}", msg);
        
        Map<String, String> response = new HashMap<>();
        response.put("echo", msg != null ? msg : "");
        
        long latency = System.currentTimeMillis() - startTime;
        logger.info("[Service-A] Endpoint: /echo, Status: 200, Latency: {}ms", latency);
        
        return ResponseEntity.ok(response);
    }
}