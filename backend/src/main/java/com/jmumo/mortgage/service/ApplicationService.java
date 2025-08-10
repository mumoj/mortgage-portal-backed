package com.jmumo.mortgage.service;

import com.jmumo.mortgage.mapper.ApplicationMapper;
import com.jmumo.mortgage.model.dto.ApplicationDto;
import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.dto.DecisionRequest;
import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.model.entity.Decision;
import com.jmumo.mortgage.model.entity.DecisionType;
import com.jmumo.mortgage.model.entity.User;
import com.jmumo.mortgage.model.event.EventType;
import com.jmumo.mortgage.repository.ApplicationRepository;
import com.jmumo.mortgage.repository.DecisionRepository;
import com.jmumo.mortgage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final DecisionRepository decisionRepository;
    private final ApplicationMapper mapper;
    private final EventPublisher eventPublisher;

    public ApplicationDto createApplication(CreateApplicationRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = mapper.toEntity(request);
        application.setApplicant(user);
        application = applicationRepository.save(application);

        eventPublisher.publishApplicationEvent(application, EventType.CREATED);
        return mapper.toDto(application);
    }

    @Transactional(readOnly = true)
    public ApplicationDto getApplication(Long id, Authentication auth) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (hasAccess(application, auth)) {
            return mapper.toDto(application);
        }
        throw new AccessDeniedException("Access denied");
    }

    @Transactional(readOnly = true)
    public Page<ApplicationDto> listApplications(int page, int size, String sort,
                                                 ApplicationStatus status, LocalDateTime createdFrom,
                                                 LocalDateTime createdTo, String nationalId,
                                                 Authentication auth) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Application> applications;
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))) {
            applications = applicationRepository.findWithFilters(status, createdFrom, createdTo,
                    nationalId, pageable);
        } else {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow();
            applications = applicationRepository.findByApplicantWithFilters(user.getId(), status, createdFrom,
                    createdTo, nationalId, pageable);
        }
        return applications.map(mapper::toDto);
    }

    public ApplicationDto makeDecision(Long id, DecisionRequest request, String approver) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(request.getDecisionType() == DecisionType.APPROVED
                ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED);

        Decision decision = Decision.builder()
                .decisionType(request.getDecisionType())
                .comments(request.getComments())
                .approver(approver)
                .application(application)
                .build();

        decisionRepository.save(decision);

        application = applicationRepository.save(application);
        eventPublisher.publishApplicationEvent(application, EventType.UPDATED);

        return mapper.toDto(application);
    }

    private boolean hasAccess(Application application, Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                || application.getApplicant().getUsername().equals(auth.getName());
    }
}
