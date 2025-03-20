package com.chatapp.peertopeerchatapp.database;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    // Register a new user with hashed password
    public boolean registerUser(String username, String password, String email) {
        if (userExists(username)) {
            return false; // User already exists
        }
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
    public boolean userExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If result exists, user found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to create a new conversation (Peer-to-Peer Chat)
    public void startPeerToPeerChat(int userId1, int userId2) {
        String sqlConversation = "INSERT INTO conversations (conversation_name) VALUES ('Peer-to-Peer Chat')";
        String sqlParticipants = "INSERT INTO participants (user_id, conversation_id) VALUES (?, ?)";

        try (Connection connection = DatabaseUtility.getConnection()) {
            // Start a new conversation
            PreparedStatement conversationStmt = connection.prepareStatement(sqlConversation, Statement.RETURN_GENERATED_KEYS);
            conversationStmt.executeUpdate();

            ResultSet conversationKeys = conversationStmt.getGeneratedKeys();
            if (conversationKeys.next()) {
                int conversationId = conversationKeys.getInt(1);

                // Add both users to the participants table
                PreparedStatement participantStmt = connection.prepareStatement(sqlParticipants);
                participantStmt.setInt(1, userId1);
                participantStmt.setInt(2, conversationId);
                participantStmt.executeUpdate();

                participantStmt.setInt(1, userId2);
                participantStmt.setInt(2, conversationId);
                participantStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
