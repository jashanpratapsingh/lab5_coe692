package com.mycompany.lab5.checkoutpenalty.persistence;

import com.mycompany.lab5.checkoutpenalty.helper.RentalRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RentalStore {
    private final DBConnection db = new DBConnection();

    public RentalRecord checkout(String username, String assetTag, String dueDate) {
        String sql = "INSERT INTO rental_transaction(username, asset_tag, due_date) VALUES(?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, assetTag);
            ps.setString(3, dueDate);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new RentalRecord(keys.getInt(1), username, assetTag, dueDate);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public RentalRecord processReturn(int id, String returnDate) {
        String get = "SELECT id, username, asset_tag, due_date FROM rental_transaction WHERE id=?";
        String upd = "UPDATE rental_transaction SET return_date=? WHERE id=?";
        String insFine = "INSERT INTO fine_record(rental_id, amount, reason) VALUES(?,?,?)";
        try (Connection c = db.getConnection()) {
            RentalRecord out = null;
            try (PreparedStatement ps = c.prepareStatement(get)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        out = new RentalRecord(rs.getInt("id"), rs.getString("username"), rs.getString("asset_tag"), rs.getString("due_date"));
                    }
                }
            }
            if (out == null) return null;
            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setString(1, returnDate);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            out.setReturnDate(returnDate);
            long delay = ChronoUnit.DAYS.between(LocalDate.parse(out.getDueDate()), LocalDate.parse(returnDate));
            double fine = delay > 0 ? delay * 5.0 : 0.0;
            out.setFineAmount(fine);
            if (fine > 0) {
                try (PreparedStatement ps = c.prepareStatement(insFine)) {
                    ps.setInt(1, id);
                    ps.setDouble(2, fine);
                    ps.setString(3, "Late return");
                    ps.executeUpdate();
                }
            }
            return out;
        } catch (SQLException ignored) {
        }
        return null;
    }

    public List<RentalRecord> byUser(String user) {
        List<RentalRecord> out = new ArrayList<>();
        String sql = "SELECT id, username, asset_tag, due_date, return_date FROM rental_transaction WHERE username=? ORDER BY id DESC";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RentalRecord r = new RentalRecord(rs.getInt("id"), rs.getString("username"), rs.getString("asset_tag"), rs.getString("due_date"));
                    r.setReturnDate(rs.getString("return_date"));
                    out.add(r);
                }
            }
        } catch (SQLException ignored) {
        }
        return out;
    }
}
