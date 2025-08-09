-- Initialize database for development
-- This script creates test users with known passwords for development

-- Insert test users (password is 'password' for all users)
INSERT INTO users (username, email, password, first_name, last_name, role) VALUES
('applicant1', 'applicant1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Doe', 'APPLICANT'),
('applicant2', 'applicant2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Jane', 'Smith', 'APPLICANT'),
('officer1', 'officer1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Mike', 'Johnson', 'OFFICER'),
('officer2', 'officer2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Sarah', 'Wilson', 'OFFICER')
ON CONFLICT (username) DO NOTHING;

-- Insert sample applications for testing
INSERT INTO applications (
    national_id, first_name, last_name, email, phone_number,
    loan_amount, annual_income, employment_type, property_address,
    property_value, status, applicant_id
) VALUES
('123456789', 'John', 'Doe', 'john@example.com', '+1234567890',
 250000.00, 75000.00, 'FULL_TIME', '123 Main St, City, State 12345',
 300000.00, 'PENDING', (SELECT id FROM users WHERE username = 'applicant1')),
('987654321', 'Jane', 'Smith', 'jane@example.com', '+0987654321',
 180000.00, 60000.00, 'PART_TIME', '456 Oak Ave, City, State 12345',
 220000.00, 'UNDER_REVIEW', (SELECT id FROM users WHERE username = 'applicant2'))
ON CONFLICT DO NOTHING;