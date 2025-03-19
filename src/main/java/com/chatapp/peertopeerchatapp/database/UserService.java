package com.chatapp.peertopeerchatapp.database;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    // Register a new user with hashed password
    public boolean registerUser(String username, String password, String email) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Authenticate user by verifying password
    public int authenticateUser(String username, String password) {
        String sql = "SELECT user_id, password_hash FROM users WHERE username = ?";
        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return rs.getInt("user_id"); // Return user_id if authenticated
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if authentication fails
    }
}
