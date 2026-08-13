-- ============================================================
-- MediCore Hospital Management System — Database Schema
-- Run this in MySQL Workbench / phpMyAdmin / mysql CLI first.
-- ============================================================

CREATE DATABASE IF NOT EXISTS medicore_hms;
USE medicore_hms;

-- ---------- Patients (patient-register.html) ----------
CREATE TABLE patients (
    patient_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name        VARCHAR(100) NOT NULL,
    dob              DATE,
    gender           VARCHAR(10),
    blood_group      VARCHAR(5),
    email            VARCHAR(100) UNIQUE NOT NULL,
    phone            VARCHAR(15) NOT NULL,
    pincode          VARCHAR(6),
    address           TEXT,
    emergency_contact VARCHAR(100),
    password_hash    VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------- Staff / Admin / Doctor / Pharmacy (login.html, register.html) ----------
CREATE TABLE staff (
    staff_id      INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('admin','doctor','pharmacy') NOT NULL,
    department    VARCHAR(60),
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status        ENUM('pending','approved','rejected') DEFAULT 'pending',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------- Appointments (appointment.html) ----------
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT NOT NULL,
    doctor_id      INT,              -- references staff.staff_id where role='doctor'
    department     VARCHAR(60) NOT NULL,
    appt_date      DATE NOT NULL,
    appt_time      TIME NOT NULL,
    reason         VARCHAR(255),
    status         ENUM('pending','confirmed','completed','cancelled') DEFAULT 'pending',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id)  REFERENCES staff(staff_id)
);

-- ---------- Pharmacy: medicines & delivery zones (pharmacy.html) ----------
CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    price       DECIMAL(8,2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0
);

CREATE TABLE delivery_zones (
    zone_id      INT AUTO_INCREMENT PRIMARY KEY,
    pincode      VARCHAR(6) UNIQUE NOT NULL,
    area         VARCHAR(100) NOT NULL,
    charge       DECIMAL(6,2) NOT NULL,
    eta_minutes  INT NOT NULL
);

-- ---------- Pharmacy orders ----------
CREATE TABLE orders (
    order_id        INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT NOT NULL,
    order_mode      ENUM('delivery','pickup') NOT NULL,
    zone_id         INT NULL,
    delivery_charge DECIMAL(6,2) DEFAULT 0,
    subtotal        DECIMAL(8,2) NOT NULL,
    total           DECIMAL(8,2) NOT NULL,
    status          ENUM('confirmed','preparing','out_for_delivery','delivered','ready_for_pickup') DEFAULT 'confirmed',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (zone_id) REFERENCES delivery_zones(zone_id)
);

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id      INT NOT NULL,
    medicine_id   INT NOT NULL,
    quantity      INT NOT NULL,
    price_each    DECIMAL(8,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

-- ---------- Lab tests (lab-test.html) ----------
CREATE TABLE lab_tests (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(120) NOT NULL,
    price   DECIMAL(8,2) NOT NULL
);

CREATE TABLE lab_bookings (
    booking_id  INT AUTO_INCREMENT PRIMARY KEY,
    patient_id  INT NOT NULL,
    booking_date DATE NOT NULL,
    mode        ENUM('home_collection','walk_in') NOT NULL,
    address     TEXT,
    total_amount DECIMAL(8,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

CREATE TABLE lab_booking_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    test_id    INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES lab_bookings(booking_id),
    FOREIGN KEY (test_id) REFERENCES lab_tests(test_id)
);

-- ---------- Prescriptions (prescription.html) ----------
CREATE TABLE prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT NOT NULL,
    doctor_id       INT,
    issued_date     DATE NOT NULL,
    valid_till      DATE,
    status          ENUM('active','refill_soon','expired') DEFAULT 'active',
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES staff(staff_id)
);

CREATE TABLE prescription_items (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id  INT NOT NULL,
    medicine_name    VARCHAR(120) NOT NULL,
    dosage           VARCHAR(100),
    duration         VARCHAR(50),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id)
);

-- ---------- Medical history (medical-history.html) ----------
CREATE TABLE medical_history (
    history_id  INT AUTO_INCREMENT PRIMARY KEY,
    patient_id  INT NOT NULL,
    visit_date  DATE NOT NULL,
    doctor_id   INT,
    diagnosis   VARCHAR(200),
    notes       TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES staff(staff_id)
);

-- ---------- Feedback (feedback.html) ----------
CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id  INT NULL,
    name        VARCHAR(100),
    department  VARCHAR(60),
    rating      TINYINT NOT NULL,
    comments    TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

-- ---------- Contact messages (contact.html) ----------
CREATE TABLE contact_messages (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    message    TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------- Emergency / ambulance requests (emergency.html) ----------
CREATE TABLE ambulance_requests (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    caller_name    VARCHAR(100) NOT NULL,
    phone          VARCHAR(15) NOT NULL,
    pickup_location TEXT NOT NULL,
    condition_text VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Seed data matching what's already shown in the frontend mocks
-- ============================================================
INSERT INTO medicines (name, price, stock) VALUES
('Paracetamol 650mg (strip of 10)', 35, 210),
('Amoxicillin 500mg (strip of 10)', 85, 6),
('Atorvastatin 10mg (strip of 10)', 120, 40),
('Metformin 500mg (strip of 15)', 60, 0),
('Insulin Glargine (pen, 3ml)', 640, 0),
('Aspirin 75mg (strip of 14)', 25, 150),
('Cetirizine 10mg (strip of 10)', 18, 95),
('ORS Sachets (box of 10)', 70, 60),
('Azithromycin 500mg (strip of 3)', 95, 22),
('Pantoprazole 40mg (strip of 10)', 75, 0),
('Ibuprofen 400mg (strip of 15)', 40, 130),
('Amlodipine 5mg (strip of 10)', 55, 8),
('Vitamin D3 60K (bottle of 4)', 110, 70),
('Multivitamin Tablets (bottle of 30)', 180, 50),
('Cough Syrup 100ml', 65, 0),
('Salbutamol Inhaler', 210, 14),
('Omeprazole 20mg (strip of 10)', 48, 88),
('Levocetirizine 5mg (strip of 10)', 30, 5);

INSERT INTO delivery_zones (pincode, area, charge, eta_minutes) VALUES
('713301', 'Asansol Town', 30, 75),
('713302', 'Asansol - Court More', 30, 75),
('713304', 'Asansol - Ushagram', 40, 105),
('713213', 'Durgapur City Centre', 60, 150),
('700001', 'Kolkata Central', 99, 1080);

INSERT INTO lab_tests (name, price) VALUES
('Complete Blood Count (CBC)', 300),
('Lipid Profile', 600),
('Thyroid Panel (T3, T4, TSH)', 450),
('Blood Sugar (Fasting)', 150),
('HbA1c', 500),
('Liver Function Test (LFT)', 700),
('Kidney Function Test (KFT)', 700),
('Vitamin D Test', 900),
('Vitamin B12 Test', 800),
('Urine Routine & Microscopy', 200),
('COVID-19 RT-PCR', 600),
('ECG (Heart Screening)', 350),
('Chest X-Ray', 500),
('Ultrasound - Whole Abdomen', 1200),
('MRI - Brain', 4500),
('Full Body Checkup Package', 2500);
