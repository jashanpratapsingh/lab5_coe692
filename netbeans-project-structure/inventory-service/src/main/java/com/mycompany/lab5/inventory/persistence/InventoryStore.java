package com.mycompany.lab5.inventory.persistence;

import com.mycompany.lab5.inventory.helper.InventoryItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class InventoryStore {
    private final DBConnection db = new DBConnection();

    public Collection<InventoryItem> all() {
        ArrayList<InventoryItem> out = new ArrayList<>();
        String sql = "SELECT asset_tag, equipment_id, status FROM inventory_item";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new InventoryItem(rs.getString("asset_tag"), rs.getInt("equipment_id"), rs.getString("status")));
            }
        } catch (SQLException ignored) {
        }
        return out;
    }

    public InventoryItem updateStatus(String tag, String status) {
        String upd = "UPDATE inventory_item SET status=? WHERE asset_tag=?";
        String get = "SELECT asset_tag, equipment_id, status FROM inventory_item WHERE asset_tag=?";
        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setString(1, status);
                ps.setString(2, tag);
                if (ps.executeUpdate() == 0) {
                    return null;
                }
            }
            try (PreparedStatement ps2 = c.prepareStatement(get)) {
                ps2.setString(1, tag);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        return new InventoryItem(rs.getString("asset_tag"), rs.getInt("equipment_id"), rs.getString("status"));
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public InventoryItem updateStatusByEquipmentId(int equipmentId, String status) {
        String upd = "UPDATE inventory_item SET status=? WHERE equipment_id=? ORDER BY asset_tag ASC LIMIT 1";
        String get = "SELECT asset_tag, equipment_id, status FROM inventory_item WHERE equipment_id=? ORDER BY asset_tag ASC LIMIT 1";
        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setString(1, status);
                ps.setInt(2, equipmentId);
                if (ps.executeUpdate() == 0) {
                    return null;
                }
            }
            try (PreparedStatement ps2 = c.prepareStatement(get)) {
                ps2.setInt(1, equipmentId);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        return new InventoryItem(rs.getString("asset_tag"), rs.getInt("equipment_id"), rs.getString("status"));
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public boolean isEventProcessed(String eventId) {
        String sql = "SELECT 1 FROM inventory_event_log WHERE event_id=? LIMIT 1";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public void markEventProcessed(String eventId, String action, int reservationId, int equipmentId) {
        String sql = "INSERT INTO inventory_event_log(event_id, action, reservation_id, equipment_id) VALUES(?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, action);
            ps.setInt(3, reservationId);
            ps.setInt(4, equipmentId);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
