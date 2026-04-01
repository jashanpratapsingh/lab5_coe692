CREATE DATABASE IF NOT EXISTS Reservation_Rental_Lab5_DB;
USE Reservation_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS reservation (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(60), start_date DATE, end_date DATE, status VARCHAR(30));
CREATE TABLE IF NOT EXISTS reservation_item (id INT PRIMARY KEY AUTO_INCREMENT, reservation_id INT, equipment_id INT);
CREATE TABLE IF NOT EXISTS reservation_audit (id INT PRIMARY KEY AUTO_INCREMENT, reservation_id INT, action VARCHAR(40), action_time DATETIME);
