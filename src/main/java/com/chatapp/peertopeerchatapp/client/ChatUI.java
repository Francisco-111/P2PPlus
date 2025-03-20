package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.DatabaseUtility;
import com.chatapp.peertopeerchatapp.database.UserService;
import com.chatapp.peertopeerchatapp.server.ChatServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatUI extends Application {

    private final UserService userService = new UserService();
    private String username;
    private int userId;
    private ChatClient chatClient; // To interact with the ChatClient

    private TextArea messageHistory;
    private TextField messageField;
    private Button sendButton;

    // Constructor to accept username and userId
    public ChatUI(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        messageHistory = new TextArea();
        messageHistory.setEditable(false);
        messageField = new TextField();
        sendButton = new Button("Send");

        // Search for a user to start a peer-to-peer chat
        Label searchLabel = new Label("Search for user:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        Label searchResultLabel = new Label();

        // Search button action
        searchButton.setOnAction(e -> {
            String peerUsername = searchField.getText();
            if (!peerUsername.isEmpty() && userService.userExists(peerUsername)) {
                searchResultLabel.setText("User found: " + peerUsername);
                // Get userId of the peer and start the chat
                int peerUserId = getUserId(peerUsername);
                startPeerToPeerChat(peerUserId); // Start peer-to-peer chat
                searchResultLabel.setText("Peer-to-peer chat started with " + peerUsername);
            } else {
                searchResultLabel.setText("User not found.");
            }
        });

        // Send button action for message sending
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                // Send the message via the ChatClient
                chatClient.sendMessage(message);
                messageField.clear(); // Clear message field after sending
            }
        });

        // Layout configuration
        VBox layout = new VBox(10, messageHistory, messageField, sendButton, searchLabel, searchField, searchButton, searchResultLabel);
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat - " + username);
        primaryStage.show();
    }

    // Method to retrieve userId from the database based on username
    private int getUserId(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Method to initiate peer-to-peer chat
    private void startPeerToPeerChat(int peerUserId) {
        // First, create the chat conversation in the database (both users are added as participants)
        userService.startPeerToPeerChat(userId, peerUserId);

        // Create the ChatClient and set the conversation
        chatClient = new ChatClient("localhost", 12345, this, userId); // Assuming localhost and port 12345 for now
        chatClient.startConversation(peerUserId);

        // Now, you can proceed with opening the chat window and display the messages
        openChatWindow(peerUserId); // Open the chat window for both users
    }

    // Method to display incoming messages in the message history
    public void displayMessage(String message) {
        messageHistory.appendText(message + "\n");
    }

    // Method to open the chat window for the two users
    private void openChatWindow(int peerUserId) {
        // Code to open the actual chat window for users with userId and peerUserId
        System.out.println("Opening chat window for users " + userId + " and " + peerUserId);
        // This could be a new scene or a dialog where the two users can start chatting
    }
}
