package com.jmumo.mortgage.controller;

import com.jmumo.mortgage.model.dto.DocumentDto;
import com.jmumo.mortgage.model.dto.UploadDocumentRequest;
import com.jmumo.mortgage.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/documents")
@Tag(name = "Documents", description = "Document management for applications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @Operation(summary = "Upload document metadata")
    @PreAuthorize("hasRole('APPLICANT') or hasRole('OFFICER')")
    public ResponseEntity<DocumentDto> uploadDocument(
            @PathVariable Long applicationId,
            @Valid @RequestBody UploadDocumentRequest request) {
        DocumentDto document = documentService.uploadDocument(
                applicationId,
                request.getFileName(),
                request.getFileType(),
                request.getFileSize(),
                request.getDocumentType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping
    @Operation(summary = "Get documents for application")
    public ResponseEntity<List<DocumentDto>> getDocuments(@PathVariable Long applicationId) {
        List<DocumentDto> documents = documentService.getDocumentsByApplication(applicationId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get specific document")
    public ResponseEntity<DocumentDto> getDocument(
            @PathVariable Long applicationId,
            @PathVariable Long documentId) {
        DocumentDto document = documentService.getDocument(documentId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete document")
    @PreAuthorize("hasRole('APPLICANT') or hasRole('OFFICER')")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long applicationId,
            @PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
