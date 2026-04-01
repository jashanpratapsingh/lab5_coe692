CREATE DATABASE IF NOT EXISTS Frontend_Rental_Lab5_DB;
USE Frontend_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS ui_audit (
  id INT PRIMARY KEY AUTO_INCREMENT,
  action_name VARCHAR(100) NOT NULL,
  actor VARCHAR(80),
  action_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS ui_runtime_config (
  cfg_key VARCHAR(60) PRIMARY KEY,
  cfg_value VARCHAR(255) NOT NULL,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO ui_runtime_config(cfg_key, cfg_value)
VALUES ('theme', 'default'), ('release', 'lab5')
ON DUPLICATE KEY UPDATE cfg_value = VALUES(cfg_value);
