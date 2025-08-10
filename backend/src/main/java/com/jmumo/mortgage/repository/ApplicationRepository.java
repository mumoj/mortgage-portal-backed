package com.jmumo.mortgage.repository;

import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.ApplicationStatus;
import com.jmumo.mortgage.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findByApplicant(User applicant, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE "
            + "a.applicant = :applicant AND "
            + "(:status IS NULL OR a.status = :status) AND "
            + "(:createdFrom IS NULL OR a.createdAt >= :createdFrom) AND "
            + "(:createdTo IS NULL OR a.createdAt <= :createdTo) AND "
            + "(:nationalId IS NULL OR a.nationalId = :nationalId)")
    Page<Application> findByApplicantWithFilters(@Param("applicant") User applicant,
                                                 @Param("status") ApplicationStatus status,
                                                 @Param("createdFrom") LocalDateTime createdFrom,
                                                 @Param("createdTo") LocalDateTime createdTo,
                                                 @Param("nationalId") String nationalId,
                                                 Pageable pageable);

    @Query("SELECT a FROM Application a WHERE "
            + "(:status IS NULL OR a.status = :status) AND "
            + "(:createdFrom IS NULL OR a.createdAt >= :createdFrom) AND "
            + "(:createdTo IS NULL OR a.createdAt <= :createdTo) AND "
            + "(:nationalId IS NULL OR a.nationalId = :nationalId)")
    Page<Application> findWithFilters(@Param("status") ApplicationStatus status,
                                      @Param("createdFrom") LocalDateTime createdFrom,
                                      @Param("createdTo") LocalDateTime createdTo,
                                      @Param("nationalId") String nationalId,
                                      Pageable pageable);
}

