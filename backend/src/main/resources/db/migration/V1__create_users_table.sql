-- V1: Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'APPLICANT',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Insert default test users
INSERT INTO users (username, email, password, first_name, last_name, role) VALUES
('applicant1', 'applicant1@example.com', '$2a$10$mPQwb1WZtdM5eVIvoC/3pOjImLUt5gQGUzICN65ax4grGJ1Fda.CG', 'John', 'Doe', 'APPLICANT'),
('officer1', 'officer1@example.com', '$2a$10$mPQwb1WZtdM5eVIvoC/3pOjImLUt5gQGUzICN65ax4grGJ1Fda.CG', 'Jane', 'Smith', 'OFFICER');