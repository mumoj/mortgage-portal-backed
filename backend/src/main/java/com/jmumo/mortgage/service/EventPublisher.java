package com.jmumo.mortgage.service;

import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.event.ApplicationEvent;
import com.jmumo.mortgage.model.event.ApplicationEventPayload;
import com.jmumo.mortgage.model.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.applications}")
    private String applicationsTopic;

    public void publishApplicationEvent(Application application, EventType eventType) {
        try {
            // Create simplified payload without circular references
            ApplicationEventPayload payload = ApplicationEventPayload.builder()
                    .id(application.getId())
                    .nationalId(application.getNationalId())
                    .firstName(application.getFirstName())
                    .lastName(application.getLastName())
                    .email(application.getEmail())
                    .phoneNumber(application.getPhoneNumber())
                    .loanAmount(application.getLoanAmount())
                    .annualIncome(application.getAnnualIncome())
                    .employmentType(application.getEmploymentType())
                    .propertyAddress(application.getPropertyAddress())
                    .propertyValue(application.getPropertyValue())
                    .status(application.getStatus())
                    .applicantId(application.getApplicant().getId())
                    .applicantUsername(application.getApplicant().getUsername())
                    .createdAt(application.getCreatedAt())
                    .updatedAt(application.getUpdatedAt())
                    .build();

            ApplicationEvent event = ApplicationEvent.builder()
                    .applicationId(application.getId().toString())
                    .eventType(eventType)
                    .payload(payload)
                    .traceId(UUID.randomUUID().toString())
                    .version("1.0")
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(applicationsTopic, application.getId().toString(), event);
            log.info("Published application event: {} for application ID: {}", eventType, application.getId());

        } catch (Exception e) {
            log.error("Failed to publish application event for ID: {}", application.getId(), e);
            // Don't throw exception to avoid breaking the main application flow
        }
    }
}