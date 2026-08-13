-- ============================================================
-- MediCore — Migration 6: link prescriptions to appointment_id
-- Run in MySQL Workbench.
-- ============================================================
USE medicore_hms;

ALTER TABLE prescriptions ADD COLUMN appointment_id INT NULL AFTER doctor_id;
