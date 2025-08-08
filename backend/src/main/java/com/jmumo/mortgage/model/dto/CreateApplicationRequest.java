package com.jmumo.mortgage.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApplicationRequest {

    @NotBlank
    @Pattern(regexp = "^[0-9]{8,12}$")
    private String nationalId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    @DecimalMin("1000.00")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal annualIncome;

    private String employmentType;
    private String propertyAddress;
    private BigDecimal propertyValue;
}