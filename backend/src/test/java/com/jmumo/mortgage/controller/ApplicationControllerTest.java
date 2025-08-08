package com.jmumo.mortgage.controller;

import com.jmumo.mortgage.controller.ApplicationController;
import com.jmumo.mortgage.model.dto.ApplicationDto;
import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ApplicationService applicationService;

    @Test
    @WithMockUser(roles = "APPLICANT")
    void createApplication_Success() throws Exception {
        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .nationalId("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .phoneNumber("+1234567890")
                .loanAmount(new BigDecimal("250000"))
                .annualIncome(new BigDecimal("75000"))
                .build();

        ApplicationDto response = ApplicationDto.builder()
                .id(1L)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationService.createApplication(any(CreateApplicationRequest.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void listApplications_Success() throws Exception {
        ApplicationDto app = ApplicationDto.builder()
                .id(1L)
                .status(ApplicationStatus.PENDING)
                .build();

        Page<ApplicationDto> page = new PageImpl<>(List.of(app));

        when(applicationService.listApplications(anyInt(), anyInt(), anyString(), any(),
                any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/applications")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}