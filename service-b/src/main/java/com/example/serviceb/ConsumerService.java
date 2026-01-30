package com.example.serviceb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceAUrl = "http://localhost:8081/api/data";

    public String getDataFromServiceA() {
        try {
            logger.info("Service B: Calling Service A at {}", serviceAUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(serviceAUrl, String.class);
            String data = response.getBody();
            logger.info("Service B: Received data from Service A: {}", data);
            return data;
        } catch (RestClientException e) {
            logger.error("Service B: Failed to call Service A: {}", e.getMessage());
            return "Service A is unavailable";
        }
    }
}