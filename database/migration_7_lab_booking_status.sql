-- ============================================================
-- MediCore — Migration 7: status tracking for lab bookings
-- Run in MySQL Workbench.
-- ============================================================
USE medicore_hms;

ALTER TABLE lab_bookings ADD COLUMN status VARCHAR(20) DEFAULT 'booked' AFTER total_amount;
