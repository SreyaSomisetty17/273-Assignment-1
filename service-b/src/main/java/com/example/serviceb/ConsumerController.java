package com.example.serviceb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/consume")
    public ResponseEntity<String> consumeData() {
        logger.info("Service B: Received request to consume data");
        String data = consumerService.getDataFromServiceA();
        logger.info("Service B: Responding with: {}", data);
        return ResponseEntity.ok(data);
    }
}