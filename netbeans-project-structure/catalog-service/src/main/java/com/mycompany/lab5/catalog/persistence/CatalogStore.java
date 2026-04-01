package com.mycompany.lab5.catalog.persistence;

import com.mycompany.lab5.catalog.helper.Equipment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogStore {
    private final DBConnection db = new DBConnection();

    public List<Equipment> search(String q) {
        List<Equipment> out = new ArrayList<>();
        String sql = "SELECT e.id, e.name, COALESCE(c.category_name,'Unknown') AS category FROM equipment e LEFT JOIN equipment_category c ON e.category_id = c.id WHERE (? IS NULL OR ? = '' OR LOWER(e.name) LIKE CONCAT('%',LOWER(?),'%') OR LOWER(c.category_name) LIKE CONCAT('%',LOWER(?),'%'))";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Equipment(rs.getInt("id"), rs.getString("name"), rs.getString("category")));
                }
            }
        } catch (SQLException ignored) {
        }
        return out;
    }
}
