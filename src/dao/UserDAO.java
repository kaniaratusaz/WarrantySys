package dao;

import model.Admin;
import model.Role;
import model.Staff;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String roleStr = rs.getString("role");
                    Role role = Role.valueOf(roleStr);

                    if (role == Role.ADMIN) {
                        return new Admin(id, username, password);
                    } else {
                        return new Staff(id, username, password);
                    }
                }
            }
        }
        return null;
    }
}
