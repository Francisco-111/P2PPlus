package com.chatapp.peertopeerchatapp.server;

import com.chatapp.peertopeerchatapp.database.DatabaseUtility;
import com.chatapp.peertopeerchatapp.database.MessageService;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ChatServer {

    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        String formattedMessage = message;
        for (ClientHandler client : clients) {
            if (client != sender) { // Prevent sending the message back to the sender
                client.sendMessage(formattedMessage);
            }
        }
    }

    public void startPeerToPeerChat(int userId1, int userId2) {
        String sqlConversation = "INSERT INTO conversations (conversation_name) VALUES ('Peer-to-Peer Chat')";
        String sqlParticipants = "INSERT INTO participants (user_id, conversation_id) VALUES (?, ?)";

        try (Connection connection = DatabaseUtility.getConnection()) {
            connection.setAutoCommit(false); // Disable auto-commit for transaction

            try (PreparedStatement conversationStmt = connection.prepareStatement(sqlConversation, Statement.RETURN_GENERATED_KEYS)) {
                // Start a new conversation
                conversationStmt.executeUpdate();

                try (ResultSet conversationKeys = conversationStmt.getGeneratedKeys()) {
                    if (conversationKeys.next()) {
                        int conversationId = conversationKeys.getInt(1);

                        // Add both users to the participants table
                        try (PreparedStatement participantStmt = connection.prepareStatement(sqlParticipants)) {
                            // Add userId1 to participants
                            participantStmt.setInt(1, userId1);
                            participantStmt.setInt(2, conversationId);
                            participantStmt.executeUpdate();

                            // Add userId2 to participants
                            participantStmt.setInt(1, userId2);
                            participantStmt.setInt(2, conversationId);
                            participantStmt.executeUpdate();

                            // Commit transaction if everything is successful
                            connection.commit();
                        }
                    }
                }
            } catch (SQLException e) {
                connection.rollback(); // Rollback on failure
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception for the database connection
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        private int currentConversationId;
        private int currentSenderId;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getCurrentConversationId() {
            return this.currentConversationId;
        }

        public int getCurrentSenderId() {
            return this.currentSenderId;
        }

        public void sendMessage(String message) {
            out.println(message); // Sends message to the connected client
        }


        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    // Save the message to the database
                    int conversationId = getCurrentConversationId(); // You'll need to implement this
                    String messageType = "text"; // Same as above
                    MessageService messageService = new MessageService();
                    messageService.saveMessage(conversationId, getCurrentSenderId(), message, messageType);

                    // Broadcast the message to other clients
                    ChatServer.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
