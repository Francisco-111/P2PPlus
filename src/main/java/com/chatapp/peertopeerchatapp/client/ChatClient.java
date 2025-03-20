package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.MessageService;

import java.io.*;
import java.net.*;
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
        return currentConversationId;
    }

    public int getCurrentUserId() {
        return currentUserId;
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
        messageService.saveMessage(currentConversationId, currentUserId, message, "text");

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
        int conversationId = checkExistingConversation(peerUserId);

        if (conversationId == -1) {
            // No existing conversation, create a new one
            conversationId = createNewConversation(peerUserId);
        }

        setCurrentConversationId(conversationId);
        System.out.println("Conversation started with user: " + peerUserId);
    }

    // Method to check if a conversation exists between users
    private int checkExistingConversation(int peerUserId) {
        // Here, you would query your database to check if a conversation exists between the two users
        // This is a placeholder for the actual database check
        // return conversationId if found, otherwise -1
        return -1;  // Placeholder
    }

    // Method to create a new conversation and store participants
    private int createNewConversation(int peerUserId) {
        // Call the database to create a new conversation
        // Placeholder for creating the conversation and inserting participants
        // Return the new conversationId
        return 123;  // Placeholder value
    }
}
