-- ============================================================
-- MediCore — Migration 2 (safe / re-runnable version)
-- Uses IF NOT EXISTS so it won't fail if run more than once.
-- Run this in MySQL Workbench.
-- ============================================================
USE medicore_hms;

-- Appointments
ALTER TABLE appointments MODIFY patient_id INT NULL;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS patient_name VARCHAR(100) NULL AFTER patient_id;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS doctor_name VARCHAR(100) NULL AFTER doctor_id;

-- Lab bookings
ALTER TABLE lab_bookings MODIFY patient_id INT NULL;
ALTER TABLE lab_bookings ADD COLUMN IF NOT EXISTS patient_name VARCHAR(100) NULL AFTER patient_id;

-- Pharmacy orders
ALTER TABLE orders MODIFY patient_id INT NULL;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS customer_name VARCHAR(100) NULL AFTER patient_id;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS customer_email VARCHAR(100) NULL AFTER customer_name;
