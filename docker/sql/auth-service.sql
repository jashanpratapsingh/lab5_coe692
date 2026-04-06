CREATE DATABASE IF NOT EXISTS Auth_Rental_Lab5_DB;
USE Auth_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) UNIQUE, password_hash VARCHAR(200));
CREATE TABLE IF NOT EXISTS roles (id INT PRIMARY KEY AUTO_INCREMENT, role_name VARCHAR(40) UNIQUE);
CREATE TABLE IF NOT EXISTS user_roles (user_id INT, role_id INT, PRIMARY KEY(user_id, role_id));
CREATE TABLE IF NOT EXISTS refresh_tokens (id INT PRIMARY KEY AUTO_INCREMENT, user_id INT, token VARCHAR(255), expires_at DATETIME);

INSERT INTO users(username,password_hash) VALUES('student1','password123'),('staff1','password123') ON DUPLICATE KEY UPDATE username=username;
