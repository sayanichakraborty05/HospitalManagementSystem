-- ============================================================
-- MediCore — Migration 2: allow guest (non-logged-in) submissions
-- Run this AFTER schema.sql, whenever database session hocche.
-- ============================================================
USE medicore_hms;

-- Appointments: patient_id optional, store name/doctor-name directly
ALTER TABLE appointments
    MODIFY patient_id INT NULL,
    ADD COLUMN patient_name VARCHAR(100) NULL AFTER patient_id,
    ADD COLUMN doctor_name VARCHAR(100) NULL AFTER doctor_id;

-- Lab bookings: patient_id optional, store name directly
ALTER TABLE lab_bookings
    MODIFY patient_id INT NULL,
    ADD COLUMN patient_name VARCHAR(100) NULL AFTER patient_id;

-- Pharmacy orders: patient_id optional, store customer name/email directly
ALTER TABLE orders
    MODIFY patient_id INT NULL,
    ADD COLUMN customer_name VARCHAR(100) NULL AFTER patient_id,
    ADD COLUMN customer_email VARCHAR(100) NULL AFTER customer_name;
