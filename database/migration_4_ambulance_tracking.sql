-- ============================================================
-- MediCore — Migration 4: track ambulance requests per patient
-- Run each line separately in MySQL Workbench (Ctrl+Enter per line).
-- ============================================================
USE medicore_hms;

ALTER TABLE ambulance_requests ADD COLUMN patient_id INT NULL AFTER id;

ALTER TABLE ambulance_requests ADD COLUMN status VARCHAR(20) DEFAULT 'requested' AFTER condition_text;
