CREATE DATABASE IF NOT EXISTS Inventory_Rental_Lab5_DB;
USE Inventory_Rental_Lab5_DB;
CREATE TABLE IF NOT EXISTS inventory_item (asset_tag VARCHAR(30) PRIMARY KEY, equipment_id INT, status VARCHAR(30));
CREATE TABLE IF NOT EXISTS inventory_status_log (id INT PRIMARY KEY AUTO_INCREMENT, asset_tag VARCHAR(30), status VARCHAR(30), changed_at DATETIME);
CREATE TABLE IF NOT EXISTS maintenance_ticket (id INT PRIMARY KEY AUTO_INCREMENT, asset_tag VARCHAR(30), issue_note TEXT, is_open BOOLEAN);
CREATE TABLE IF NOT EXISTS inventory_event_log (
  id INT PRIMARY KEY AUTO_INCREMENT,
  event_id VARCHAR(120) UNIQUE,
  action VARCHAR(40),
  reservation_id INT,
  equipment_id INT,
  processed_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO inventory_item(asset_tag,equipment_id,status) VALUES ('CAM-01',1,'AVAILABLE'),('AUD-01',2,'AVAILABLE'),('DRN-01',3,'MAINTENANCE') ON DUPLICATE KEY UPDATE equipment_id=VALUES(equipment_id), status=VALUES(status);
