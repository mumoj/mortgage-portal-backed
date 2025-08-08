package com.jmumo.mortgage.service;

import com.jmumo.mortgage.mapper.ApplicationMapper;
import com.jmumo.mortgage.model.dto.ApplicationDto;
import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.dto.DecisionRequest;
import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.model.entity.DecisionType;
import com.jmumo.mortgage.model.entity.User;
import com.jmumo.mortgage.model.entity.UserRole;
import com.jmumo.mortgage.model.event.EventType;
import com.jmumo.mortgage.repository.ApplicationRepository;
import com.jmumo.mortgage.repository.DecisionRepository;
import com.jmumo.mortgage.repository.UserRepository;
import com.jmumo.mortgage.service.ApplicationService;
import com.jmumo.mortgage.service.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DecisionRepository decisionRepository;
    @Mock
    private ApplicationMapper mapper;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;
    private Application testApplication;
    private CreateApplicationRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.APPLICANT)
                .build();

        testApplication = Application.builder()
                .id(1L)
                .nationalId("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .phoneNumber("+1234567890")
                .loanAmount(new BigDecimal("250000"))
                .annualIncome(new BigDecimal("75000"))
                .applicant(testUser)
                .build();

        testRequest = CreateApplicationRequest.builder()
                .nationalId("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .phoneNumber("+1234567890")
                .loanAmount(new BigDecimal("250000"))
                .annualIncome(new BigDecimal("75000"))
                .build();
    }

    @Test
    void createApplication_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapper.toEntity(testRequest)).thenReturn(testApplication);
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        when(mapper.toDto(testApplication)).thenReturn(new ApplicationDto());

        ApplicationDto result = applicationService.createApplication(testRequest, "testuser");

        assertNotNull(result);
        verify(eventPublisher).publishApplicationEvent(testApplication, EventType.CREATED);
    }

    @Test
    void getApplication_AsApplicant_Success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_APPLICANT")));
        when(mapper.toDto(testApplication)).thenReturn(new ApplicationDto());

        ApplicationDto result = applicationService.getApplication(1L, authentication);

        assertNotNull(result);
    }

    @Test
    void getApplication_AsOfficer_Success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_OFFICER")));
        when(mapper.toDto(testApplication)).thenReturn(new ApplicationDto());

        ApplicationDto result = applicationService.getApplication(1L, authentication);

        assertNotNull(result);
    }

    @Test
    void getApplication_AccessDenied() {
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@email.com")
                .role(UserRole.APPLICANT)
                .build();
        testApplication.setApplicant(otherUser);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_APPLICANT")));

        assertThrows(AccessDeniedException.class,
                () -> applicationService.getApplication(1L, authentication));
    }

    @Test
    void makeDecision_Approved() {
        DecisionRequest request = DecisionRequest.builder()
                .decisionType(DecisionType.APPROVED)
                .comments("Approved based on good credit history")
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        when(decisionRepository.save(any())).thenReturn(null);
        when(mapper.toDto(testApplication)).thenReturn(new ApplicationDto());

        ApplicationDto result = applicationService.makeDecision(1L, request, "officer1");

        assertNotNull(result);
        assertEquals(ApplicationStatus.APPROVED, testApplication.getStatus());
        verify(eventPublisher).publishApplicationEvent(testApplication, EventType.UPDATED);
    }
}