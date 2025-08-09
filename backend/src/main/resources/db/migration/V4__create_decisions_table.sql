-- V4: Create decisions table
CREATE TABLE decisions (
    id BIGSERIAL PRIMARY KEY,
    decision_type VARCHAR(20) NOT NULL,
    comments TEXT,
    approver VARCHAR(100) NOT NULL,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_decisions_application_id ON decisions(application_id);
CREATE INDEX idx_decisions_decision_type ON decisions(decision_type);
CREATE INDEX idx_decisions_approver ON decisions(approver);
CREATE INDEX idx_decisions_created_at ON decisions(created_at);

-- Add constraints
ALTER TABLE decisions ADD CONSTRAINT chk_decision_type CHECK (
    decision_type IN ('APPROVED', 'REJECTED')
);