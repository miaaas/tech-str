package com.practice.kafka.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.practice.kafka.service.KafkaProducerService;

@RestController
@RequestMapping("/api/kafka")
public class MessageController {

    @Autowired
    private final KafkaProducerService kafkaProducerService;

    
    public MessageController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/publish")
    public String publishMessage(@RequestParam("message") String message) {
        kafkaProducerService.sendMessage(message);
        return "Message published successfully!";
    }
}

