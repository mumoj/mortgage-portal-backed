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

    @Query("SELECT a FROM Application a WHERE a.applicant.id = :applicantId "
            + "AND (:#{#status == null} = true OR a.status = :status) "
            + "AND (:#{#createdFrom == null} = true OR a.createdAt >= :createdFrom) "
            + "AND (:#{#createdTo == null} = true OR a.createdAt <= :createdTo) "
            + "AND (:#{#nationalId == null} = true OR a.nationalId = :nationalId)")
    Page<Application> findByApplicantWithFilters(@Param("applicantId") Long applicantId,
                                                 @Param("status") ApplicationStatus status,
                                                 @Param("createdFrom") LocalDateTime createdFrom,
                                                 @Param("createdTo") LocalDateTime createdTo,
                                                 @Param("nationalId") String nationalId,
                                                 Pageable pageable);

    @Query("SELECT a FROM Application a WHERE "
            + "(:#{#status == null} = true OR a.status = :status) "
            + "AND (:#{#createdFrom == null} = true OR a.createdAt >= :createdFrom) "
            + "AND (:#{#createdTo == null} = true OR a.createdAt <= :createdTo) "
            + "AND (:#{#nationalId == null} = true OR a.nationalId = :nationalId)")
    Page<Application> findWithFilters(@Param("status") ApplicationStatus status,
                                      @Param("createdFrom") LocalDateTime createdFrom,
                                      @Param("createdTo") LocalDateTime createdTo,
                                      @Param("nationalId") String nationalId,
                                      Pageable pageable);
}

