package com.jmumo.mortgage.repository;

import com.jmumo.mortgage.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
