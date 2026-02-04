package com.example.serviceb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    private final RestTemplate restTemplate;
    private final String serviceAUrl;

    public ConsumerService(@Value("${service.a.url}") String serviceAUrl,
                           @Value("${service.a.timeout}") int timeout) {
        this.serviceAUrl = serviceAUrl;
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
        logger.info("ConsumerService initialized with Service A URL: {} and timeout: {}ms", serviceAUrl, timeout);
    }

    public Map<String, Object> callServiceAEcho(String msg) {
        long startTime = System.currentTimeMillis();
        String echoUrl = serviceAUrl + "/echo" + (msg != null ? "?msg=" + msg : "");
        
        try {
            logger.info("[Service-B] Calling Service A at: {}", echoUrl);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(echoUrl, Map.class);
            long callLatency = System.currentTimeMillis() - startTime;
            
            logger.info("[Service-B] Successfully received response from Service A, Latency: {}ms", callLatency);
            
            Map<String, Object> result = new HashMap<>();
            result.put("service_a_response", response.getBody());
            result.put("service_b_message", "Successfully called Service A");
            result.put("total_latency_ms", callLatency);
            
            return result;
            
        } catch (RestClientException e) {
            long callLatency = System.currentTimeMillis() - startTime;
            logger.error("[Service-B] Failed to call Service A after {}ms: {}", callLatency, e.getMessage());
            throw new RuntimeException("Failed to connect to Service A: " + e.getMessage());
        }
    }
}