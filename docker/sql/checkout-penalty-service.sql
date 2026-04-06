CREATE DATABASE IF NOT EXISTS CheckoutPenalty_Rental_Lab5_DB;
USE CheckoutPenalty_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS rental_transaction (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(60), asset_tag VARCHAR(30), due_date DATE, return_date DATE);
CREATE TABLE IF NOT EXISTS return_inspection (id INT PRIMARY KEY AUTO_INCREMENT, rental_id INT, condition_note TEXT);
CREATE TABLE IF NOT EXISTS fine_record (id INT PRIMARY KEY AUTO_INCREMENT, rental_id INT, amount DECIMAL(10,2), reason VARCHAR(120));
CREATE TABLE IF NOT EXISTS payment_record (id INT PRIMARY KEY AUTO_INCREMENT, fine_id INT, amount DECIMAL(10,2), payment_time DATETIME);
