package com.example.serviceb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        long startTime = System.currentTimeMillis();
        logger.info("[Service-B] Endpoint: /health, Status: Starting");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        
        long latency = System.currentTimeMillis() - startTime;
        logger.info("[Service-B] Endpoint: /health, Status: 200, Latency: {}ms", latency);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/call-echo")
    public ResponseEntity<Map<String, Object>> callEcho(@RequestParam(name = "msg", required = false) String msg) {
        long startTime = System.currentTimeMillis();
        logger.info("[Service-B] Endpoint: /call-echo, Status: Starting, Message: {}", msg);
        
        try {
            Map<String, Object> result = consumerService.callServiceAEcho(msg);
            long latency = System.currentTimeMillis() - startTime;
            logger.info("[Service-B] Endpoint: /call-echo, Status: 200, Latency: {}ms", latency);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            logger.error("[Service-B] Endpoint: /call-echo, Status: 503, Latency: {}ms, Error: {}", latency, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Service A is unavailable");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }
    }
}