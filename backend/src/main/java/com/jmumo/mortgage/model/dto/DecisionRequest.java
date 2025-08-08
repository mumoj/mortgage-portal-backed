package com.jmumo.mortgage.model.dto;

import com.jmumo.mortgage.model.entity.DecisionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionRequest {

    @NotNull
    private DecisionType decisionType;

    private String comments;
}
