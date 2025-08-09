package com.jmumo.mortgage.repository;

import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.model.entity.User;
import com.jmumo.mortgage.model.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.APPLICANT)
                .build();
        testUser = userRepository.save(testUser);

        testApplication = Application.builder()
                .nationalId("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .phoneNumber("+1234567890")
                .loanAmount(new BigDecimal("250000"))
                .annualIncome(new BigDecimal("75000"))
                .applicant(testUser)
                .build();
        testApplication = applicationRepository.save(testApplication);
    }

    @Test
    void findByApplicantSuccess() {
        Page<Application> result = applicationRepository.findByApplicant(testUser, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(testApplication.getId(), result.getContent().get(0).getId());
    }

    @Test
    void findWithFiltersByStatus() {
        Page<Application> result = applicationRepository.findWithFilters(
                ApplicationStatus.PENDING, null, null, null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findWithFiltersByNationalId() {
        Page<Application> result = applicationRepository.findWithFilters(
                null, null, null, "123456789", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }
}