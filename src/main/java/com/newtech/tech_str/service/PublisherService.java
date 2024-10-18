package com.newtech.tech_str.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;


@Service
public class PublisherService {

    @Autowired
    private final PubSubPublisherTemplate pubSubPublisherTemplate;

    public PublisherService(PubSubPublisherTemplate pubSubPublisherTemplate) {
        this.pubSubPublisherTemplate = pubSubPublisherTemplate;
    }

    public void publishMessage(String topic, String message) {
        pubSubPublisherTemplate.publish(topic, message);
    }
}
