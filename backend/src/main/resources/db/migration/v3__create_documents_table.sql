-- V3: Create documents table
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    s3_url VARCHAR(500) NOT NULL,
    presigned_url VARCHAR(1000),
    document_type VARCHAR(50) NOT NULL,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_documents_application_id ON documents(application_id);
CREATE INDEX idx_documents_document_type ON documents(document_type);
CREATE INDEX idx_documents_created_at ON documents(created_at);

-- Add constraints
ALTER TABLE documents ADD CONSTRAINT chk_file_size_positive CHECK (file_size > 0);
ALTER TABLE documents ADD CONSTRAINT chk_document_type CHECK (
    document_type IN ('IDENTITY_PROOF', 'INCOME_PROOF', 'PROPERTY_DOCUMENTS', 'BANK_STATEMENTS', 'OTHER')
);