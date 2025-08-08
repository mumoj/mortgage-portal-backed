package com.jmumo.mortgage.service;

import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.event.ApplicationEvent;
import com.jmumo.mortgage.model.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.applications}")
    private String applicationsTopic;

    public void publishApplicationEvent(Application application, EventType eventType) {
        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(application.getId().toString())
                .eventType(eventType)
                .payload(application)
                .traceId(UUID.randomUUID().toString())
                .version("1.0")
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(applicationsTopic, application.getId().toString(), event);
    }
}