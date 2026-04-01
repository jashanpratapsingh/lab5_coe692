package com.mycompany.lab5.reservation.persistence;

import com.mycompany.lab5.reservation.messaging.ReservationEventPublisher;
import com.mycompany.lab5.reservation.helper.Reservation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationStore {
    private final DBConnection db = new DBConnection();
    private final ReservationEventPublisher eventPublisher = new ReservationEventPublisher();

    public Reservation create(Reservation input) {
        String sql = "INSERT INTO reservation(username, start_date, end_date, status) VALUES(?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, input.getUsername());
            ps.setString(2, input.getStartDate());
            ps.setString(3, input.getEndDate());
            ps.setString(4, "CREATED");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    Reservation created = new Reservation(id, input.getUsername(), input.getEquipmentId(), input.getStartDate(), input.getEndDate(), "CREATED");
                    insertReservationItem(c, id, input.getEquipmentId());
                    eventPublisher.publish(created, "CREATED");
                    return created;
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public List<Reservation> all() {
        List<Reservation> out = new ArrayList<>();
        String sql = "SELECT id, username, start_date, end_date, status FROM reservation ORDER BY id DESC";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Reservation(rs.getInt("id"), rs.getString("username"), 0, rs.getString("start_date"), rs.getString("end_date"), rs.getString("status")));
            }
        } catch (SQLException ignored) {
        }
        return out;
    }

    public Reservation cancel(int id) {
        String upd = "UPDATE reservation SET status='CANCELLED' WHERE id=?";
        String get = "SELECT id, username, start_date, end_date, status FROM reservation WHERE id=?";
        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) {
                    return null;
                }
            }
            try (PreparedStatement ps2 = c.prepareStatement(get)) {
                ps2.setInt(1, id);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        Reservation cancelled = new Reservation(rs.getInt("id"), rs.getString("username"), getEquipmentIdByReservation(c, id), rs.getString("start_date"), rs.getString("end_date"), rs.getString("status"));
                        eventPublisher.publish(cancelled, "CANCELLED");
                        return cancelled;
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    private void insertReservationItem(Connection c, int reservationId, int equipmentId) {
        if (equipmentId <= 0) {
            return;
        }
        String sql = "INSERT INTO reservation_item(reservation_id, equipment_id) VALUES(?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setInt(2, equipmentId);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private int getEquipmentIdByReservation(Connection c, int reservationId) {
        String sql = "SELECT equipment_id FROM reservation_item WHERE reservation_id=? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("equipment_id");
                }
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }
}
