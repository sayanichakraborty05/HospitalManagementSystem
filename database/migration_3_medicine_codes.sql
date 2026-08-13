-- ============================================================
-- MediCore — Migration 3: link frontend medicine codes (m1..m18)
-- to the real medicine_id in the database.
-- Run each line separately in MySQL Workbench (Ctrl+Enter per line).
-- ============================================================
USE medicore_hms;

SET SQL_SAFE_UPDATES = 0;

ALTER TABLE medicines ADD COLUMN code VARCHAR(10) NULL AFTER medicine_id;

UPDATE medicines SET code = 'm1'  WHERE name = 'Paracetamol 650mg (strip of 10)';
UPDATE medicines SET code = 'm2'  WHERE name = 'Amoxicillin 500mg (strip of 10)';
UPDATE medicines SET code = 'm3'  WHERE name = 'Atorvastatin 10mg (strip of 10)';
UPDATE medicines SET code = 'm4'  WHERE name = 'Metformin 500mg (strip of 15)';
UPDATE medicines SET code = 'm5'  WHERE name = 'Insulin Glargine (pen, 3ml)';
UPDATE medicines SET code = 'm6'  WHERE name = 'Aspirin 75mg (strip of 14)';
UPDATE medicines SET code = 'm7'  WHERE name = 'Cetirizine 10mg (strip of 10)';
UPDATE medicines SET code = 'm8'  WHERE name = 'ORS Sachets (box of 10)';
UPDATE medicines SET code = 'm9'  WHERE name = 'Azithromycin 500mg (strip of 3)';
UPDATE medicines SET code = 'm10' WHERE name = 'Pantoprazole 40mg (strip of 10)';
UPDATE medicines SET code = 'm11' WHERE name = 'Ibuprofen 400mg (strip of 15)';
UPDATE medicines SET code = 'm12' WHERE name = 'Amlodipine 5mg (strip of 10)';
UPDATE medicines SET code = 'm13' WHERE name = 'Vitamin D3 60K (bottle of 4)';
UPDATE medicines SET code = 'm14' WHERE name = 'Multivitamin Tablets (bottle of 30)';
UPDATE medicines SET code = 'm15' WHERE name = 'Cough Syrup 100ml';
UPDATE medicines SET code = 'm16' WHERE name = 'Salbutamol Inhaler';
UPDATE medicines SET code = 'm17' WHERE name = 'Omeprazole 20mg (strip of 10)';
UPDATE medicines SET code = 'm18' WHERE name = 'Levocetirizine 5mg (strip of 10)';
