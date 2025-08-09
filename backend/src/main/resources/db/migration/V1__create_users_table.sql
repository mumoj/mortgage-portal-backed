CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    national_id VARCHAR(12) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    annual_income DECIMAL(15,2) NOT NULL,
    employment_type VARCHAR(50),
    property_address TEXT,
    property_value DECIMAL(15,2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applicant_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version INTEGER DEFAULT 0
);

-- Create indexes for performance and filtering
CREATE INDEX idx_applications_applicant_id ON applications(applicant_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_national_id ON applications(national_id);
CREATE INDEX idx_applications_created_at ON applications(created_at);
CREATE INDEX idx_applications_status_created_at ON applications(status, created_at);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_applications_updated_at
    BEFORE UPDATE ON applications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();