package com.jmumo.mortgage.repository;

import com.jmumo.mortgage.model.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
}
