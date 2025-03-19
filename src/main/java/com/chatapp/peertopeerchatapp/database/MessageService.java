package com.chatapp.peertopeerchatapp.database;

import java.sql.*;
import java.time.LocalDateTime;

public class MessageService {
    public void saveMessage(int conversationId, int senderId, String message, String messageType) {
        String sql = "INSERT INTO messages (conversation_id, sender_id, message_type, content, sent_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, messageType);
            stmt.setString(4, message);  // For text messages
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

