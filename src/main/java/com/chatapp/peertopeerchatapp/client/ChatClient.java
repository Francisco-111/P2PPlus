package com.chatapp.peertopeerchatapp.client;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ChatUI chatUI;

    public ChatClient(String serverAddress, int serverPort, ChatUI chatUI) {
        this.chatUI = chatUI;
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

    public void sendMessage(String message) {
        String timestamp = getCurrentTimestamp();
        String formattedMessage = "You [" + timestamp + "]: " + message;

        // Display sender's message locally
        chatUI.displayMessage(formattedMessage);

        // Send to server (server will not echo back to the sender)
        out.println("[" + timestamp + "] " + message);
    }


    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        return now.format(formatter);
    }
}
