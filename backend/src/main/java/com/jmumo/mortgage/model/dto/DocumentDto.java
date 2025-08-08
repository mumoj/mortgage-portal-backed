package com.jmumo.mortgage.model.dto;

import com.jmumo.mortgage.model.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String presignedUrl;
    private DocumentType documentType;
    private LocalDateTime createdAt;
}