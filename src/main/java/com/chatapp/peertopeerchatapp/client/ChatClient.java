package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.DatabaseUtility;
import com.chatapp.peertopeerchatapp.database.MessageService;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ChatUI chatUI;
    private int currentConversationId;
    private int currentUserId;
    private MessageService messageService;

    // Constructor
    public ChatClient(String serverAddress, int serverPort, ChatUI chatUI, int userId) {
        this.chatUI = chatUI;
        this.currentUserId = userId;
        this.messageService = new MessageService();

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server.");

            // Start listening for messages
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        chatUI.displayMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for conversation ID and user ID
    public int getCurrentConversationId() {
        return this.currentConversationId;
    }

    public int getCurrentUserId() {
        return this.currentUserId;
    }

    // Set current conversation ID
    public void setCurrentConversationId(int conversationId) {
        this.currentConversationId = conversationId;
    }

    // Send message method
    public void sendMessage(String message) {
        String timestamp = getCurrentTimestamp();
        String formattedMessage = "You [" + timestamp + "]: " + message;

        // Display sender's message locally
        chatUI.displayMessage(formattedMessage);

        // Save message to the database using MessageService
        messageService.saveMessage(currentConversationId, getCurrentUserId(), message, "text");

        // Send message to the server
        out.println("[" + timestamp + "] " + message);
    }

    // Get the current timestamp
    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        return now.format(formatter);
    }

    // Check if a P2P conversation exists, if not, create one
    public void startConversation(int peerUserId) {
        // Check if a conversation already exists between the current user and the peer
        int conversationId = checkExistingConversation(getCurrentUserId(),peerUserId);

        if (conversationId == -1) {
            // No existing conversation, create a new one
            conversationId = createNewConversation(getCurrentUserId(),peerUserId);
        }

        setCurrentConversationId(conversationId);
        System.out.println("Conversation started with user: " + peerUserId);
    }

    // Method to check if a conversation exists between users
    private int checkExistingConversation(int currentUserId, int peerUserId) {
        String sql = """
        SELECT c.conversation_id
        FROM conversations c
        JOIN participants p1 ON c.conversation_id = p1.conversation_id
        JOIN participants p2 ON c.conversation_id = p2.conversation_id
        WHERE p1.user_id = ? AND p2.user_id = ? AND c.is_group_chat = false
    """;

        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, currentUserId);
            preparedStatement.setInt(2, peerUserId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("conversation_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no conversation found
    }


    // Method to create a new conversation and store participants
    private int createNewConversation(int currentUserId, int peerUserId) {
        String insertConversationSQL = """
        INSERT INTO conversations (is_group_chat) VALUES (false) RETURNING conversation_id
    """;
        String insertParticipantSQL = """
        INSERT INTO participants (conversation_id, user_id) VALUES (?, ?)
    """;

        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement conversationStmt = connection.prepareStatement(insertConversationSQL);
             PreparedStatement participantStmt = connection.prepareStatement(insertParticipantSQL)) {

            // Insert the new conversation
            ResultSet resultSet = conversationStmt.executeQuery();
            int conversationId = -1;
            if (resultSet.next()) {
                conversationId = resultSet.getInt("conversation_id");
            }

            // Add both users to the participants table
            participantStmt.setInt(1, conversationId);
            participantStmt.setInt(2, currentUserId);
            participantStmt.executeUpdate();

            participantStmt.setInt(1, conversationId);
            participantStmt.setInt(2, peerUserId);
            participantStmt.executeUpdate();

            connection.commit(); // Commit transaction
            System.out.println("New conversation created with ID: " + conversationId);
            return conversationId;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if creation failed
    }

}
