package com.jmumo.mortgage.integration;

import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.entity.User;
import com.jmumo.mortgage.model.entity.UserRole;
import com.jmumo.mortgage.repository.UserRepository;
import com.jmumo.mortgage.service.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    // Mock the EventPublisher to avoid Kafka dependency
    @MockBean
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        // Create test user if it doesn't exist
        if (userRepository.findByUsername("testuser").isEmpty()) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("testuser@example.com")
                    .password("password")
                    .firstName("Test")
                    .lastName("User")
                    .role(UserRole.APPLICANT)
                    .enabled(true)
                    .build();
            userRepository.save(testUser);
        }

        // Mock the event publishing to avoid Kafka dependency
        doNothing().when(eventPublisher).publishApplicationEvent(any(), any());
    }

    @Test
    @WithMockUser(roles = "APPLICANT", username = "testuser")
    void createApplicationEndToEnd() throws Exception {
        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .nationalId("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .phoneNumber("+1234567890")
                .loanAmount(new BigDecimal("250000"))
                .annualIncome(new BigDecimal("75000"))
                .build();

        mockMvc.perform(post("/api/v1/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationalId").value("123456789"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}