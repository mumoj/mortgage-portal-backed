package com.jmumo.mortgage.controller;

import com.jmumo.mortgage.model.dto.ApplicationDto;
import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.dto.DecisionRequest;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Mortgage application management")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create new application")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<ApplicationDto> createApplication(
            @Valid @RequestBody CreateApplicationRequest request,
            Authentication auth) {
        ApplicationDto application = applicationService.createApplication(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApplicationDto> getApplication(@PathVariable Long id, Authentication auth) {
        ApplicationDto application = applicationService.getApplication(id, auth);
        return ResponseEntity.ok(application);
    }

    @GetMapping
    @Operation(summary = "List applications with filters")
    public ResponseEntity<Page<ApplicationDto>> listApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @RequestParam(required = false) String nationalId,
            Authentication auth) {

        Page<ApplicationDto> applications = applicationService.listApplications(
                page, size, sort, status, createdFrom, createdTo, nationalId, auth);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/{id}/decision")
    @Operation(summary = "Make decision on application")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<ApplicationDto> makeDecision(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request,
            Authentication auth) {
        ApplicationDto application = applicationService.makeDecision(id, request, auth.getName());
        return ResponseEntity.ok(application);
    }
}