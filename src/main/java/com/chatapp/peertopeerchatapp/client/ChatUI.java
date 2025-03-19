package com.chatapp.peertopeerchatapp.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatUI extends Application {

    private ChatClient client;
    private TextArea messageHistory;
    private TextField messageField;
    private Button sendButton;

    @Override
    public void start(Stage primaryStage) {
        client = new ChatClient("localhost", 12345, this);

        // UI Components
        messageHistory = new TextArea();
        messageHistory.setEditable(false);
        messageField = new TextField();
        sendButton = new Button("Send");

        // Send message on button click
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                client.sendMessage(message);
                messageField.clear();
            }
        });

        // Layout
        VBox layout = new VBox(10, messageHistory, messageField, sendButton);
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Peer-to-Peer Chat");
        primaryStage.show();
    }

    // Display messages with timestamps
    public void displayMessage(String message) {
        messageHistory.appendText(message + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}



