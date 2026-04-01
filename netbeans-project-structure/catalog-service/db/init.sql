CREATE DATABASE IF NOT EXISTS Catalog_Rental_Lab5_DB;
USE Catalog_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS equipment_category (id INT PRIMARY KEY AUTO_INCREMENT, category_name VARCHAR(80));
CREATE TABLE IF NOT EXISTS equipment (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(120), category_id INT, description TEXT);
CREATE TABLE IF NOT EXISTS equipment_specs (id INT PRIMARY KEY AUTO_INCREMENT, equipment_id INT, spec_key VARCHAR(80), spec_value VARCHAR(255));

INSERT INTO equipment_category(id,category_name) VALUES (1,'Camera'),(2,'Audio'),(3,'Drone') ON DUPLICATE KEY UPDATE category_name=VALUES(category_name);
INSERT INTO equipment(id,name,category_id,description) VALUES (1,'Sony A6400 Camera',1,'Mirrorless camera'),(2,'Zoom H1n Recorder',2,'Portable recorder'),(3,'DJI Mini Drone',3,'Light drone') ON DUPLICATE KEY UPDATE name=VALUES(name), category_id=VALUES(category_id), description=VALUES(description);
