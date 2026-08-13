-- ============================================================
-- MediCore — Migration 5: appointment fee/payment + seed staff
-- Run each line separately in MySQL Workbench (Ctrl+Enter per line).
-- ============================================================
USE medicore_hms;

ALTER TABLE appointments ADD COLUMN fee DECIMAL(8,2) NULL AFTER status;

ALTER TABLE appointments ADD COLUMN payment_status VARCHAR(20) DEFAULT 'unpaid' AFTER fee;

ALTER TABLE appointments ADD COLUMN bill_token VARCHAR(30) NULL AFTER payment_status;

-- ------------------------------------------------------------
-- Seed one Admin and one Doctor account so you can log in and
-- test the doctor/admin panel right away.
-- Password for BOTH accounts below is:  test123
-- (this is a plain-text password for now — same as the rest of
--  the project; hashing can be added later as a security upgrade)
-- ------------------------------------------------------------
INSERT INTO staff (full_name, role, department, email, password_hash, status) VALUES
('Admin User', 'admin', NULL, 'admin@medicore.com', 'test123', 'approved'),
('Dr. A. Sen', 'doctor', 'Cardiology', 'asen@medicore.com', 'test123', 'approved');
