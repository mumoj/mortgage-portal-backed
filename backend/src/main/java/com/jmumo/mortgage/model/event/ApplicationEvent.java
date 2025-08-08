package com.jmumo.mortgage.model.event;

import com.jmumo.mortgage.model.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEvent {
    private String applicationId;
    private EventType eventType;
    private Application payload;
    private String traceId;
    private String version;
    private LocalDateTime timestamp;
}

