package com.jmumo.mortgage.validation;

import com.jmumo.mortgage.model.entity.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@jakarta.validation.Constraint(validatedBy = DocumentTypeValidator.DocumentTypeValidatorImpl.class)
public @interface DocumentTypeValidator {

    String message() default "Invalid document type for file format";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};

    class DocumentTypeValidatorImpl implements ConstraintValidator<DocumentTypeValidator, Object> {

        private static final Map<DocumentType, Set<String>> ALLOWED_FILE_TYPES = Map.of(
                DocumentType.IDENTITY_PROOF, Set.of("pdf", "jpg", "jpeg", "png"),
                DocumentType.INCOME_PROOF, Set.of("pdf", "doc", "docx", "xls", "xlsx"),
                DocumentType.PROPERTY_DOCUMENTS, Set.of("pdf", "doc", "docx"),
                DocumentType.BANK_STATEMENTS, Set.of("pdf", "csv", "xls", "xlsx"),
                DocumentType.OTHER, Set.of("pdf", "doc", "docx", "jpg", "jpeg", "png")
        );

        @Override
        public void initialize(DocumentTypeValidator constraintAnnotation) {
            // Initialization logic if needed
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Let @NotNull handle null validation
            }

            // This validator can be used on objects that have both documentType and fileName
            if (value instanceof com.jmumo.mortgage.model.dto.UploadDocumentRequest request) {
                return validateDocumentTypeAndFileName(request.getDocumentType(), request.getFileName());
            }

            // If used directly on DocumentType enum
            if (value instanceof DocumentType) {
                return Arrays.asList(DocumentType.values()).contains(value);
            }

            return true;
        }

        private boolean validateDocumentTypeAndFileName(DocumentType documentType, String fileName) {
            if (documentType == null || fileName == null) {
                return false;
            }

            String fileExtension = getFileExtension(fileName).toLowerCase();
            Set<String> allowedExtensions = ALLOWED_FILE_TYPES.get(documentType);

            return allowedExtensions != null && allowedExtensions.contains(fileExtension);
        }

        private String getFileExtension(String fileName) {
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
                return "";
            }
            return fileName.substring(lastDotIndex + 1);
        }
    }
}