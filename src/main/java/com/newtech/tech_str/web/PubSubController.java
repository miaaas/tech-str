package com.newtech.tech_str.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newtech.tech_str.service.PublisherService;

@RestController
public class PubSubController {

    @Autowired
    private PublisherService publisherService;

    @PostMapping("/message/publish")
    public String publishMessage(@RequestBody String message) {
        publisherService.publishMessage("MyTopic", message);
        return "Message published: " + message;
    }
}

