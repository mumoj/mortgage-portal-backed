package com.jmumo.mortgage.service;

import com.jmumo.mortgage.mapper.ApplicationMapper;
import com.jmumo.mortgage.model.dto.DocumentDto;
import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.Document;
import com.jmumo.mortgage.model.entity.DocumentType;
import com.jmumo.mortgage.repository.ApplicationRepository;
import com.jmumo.mortgage.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper mapper;

    @Value("${app.s3.bucket}")
    private String s3Bucket;

    @Value("${app.s3.region}")
    private String s3Region;

    public DocumentDto uploadDocument(Long applicationId, String fileName,
                                      String fileType, Long fileSize, DocumentType documentType) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        String s3Key = generateS3Key(applicationId, fileName);
        String s3Url = buildS3Url(s3Key);
        String presignedUrl = generatePresignedUrl(s3Key);

        Document document = Document.builder()
                .fileName(fileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .s3Url(s3Url)
                .presignedUrl(presignedUrl)
                .documentType(documentType)
                .application(application)
                .build();

        document = documentRepository.save(document);
        return mapper.toDto(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocumentsByApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return application.getDocuments().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentDto getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return mapper.toDto(document);
    }

    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        documentRepository.delete(document);
    }

    private String generateS3Key(Long applicationId, String fileName) {
        return String.format("applications/%d/documents/%s_%s",
                applicationId, UUID.randomUUID().toString(), fileName);
    }

    private String buildS3Url(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", s3Bucket, s3Region, s3Key);
    }

    private String generatePresignedUrl(String s3Key) {
        // In a real implementation, this would generate a presigned URL using AWS SDK
        // For now, return a mock URL with expiration
        LocalDateTime expiry = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        return String.format("https://%s.s3.%s.amazonaws.com/%s?X-Amz-Expires=3600&X-Amz-Date=%s",
                s3Bucket, s3Region, s3Key, expiry.toString());
    }
}