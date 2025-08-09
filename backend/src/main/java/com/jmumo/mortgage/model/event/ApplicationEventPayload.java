package com.jmumo.mortgage.model.event;

import com.jmumo.mortgage.model.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEventPayload {
    private Long id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private BigDecimal loanAmount;
    private BigDecimal annualIncome;
    private String employmentType;
    private String propertyAddress;
    private BigDecimal propertyValue;
    private ApplicationStatus status;
    private Long applicantId;
    private String applicantUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}