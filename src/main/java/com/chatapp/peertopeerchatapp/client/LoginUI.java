package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUI extends Application {

    private final UserService userService = new UserService();

    @Override
    public void start(Stage primaryStage) {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label messageLabel = new Label();

        // Login action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            int userId = userService.authenticateUser(username, password);
            if (userId != -1) {
                messageLabel.setText("Login successful! Welcome, " + username);
                openChatUI(username, userId);
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Registration action
        registerButton.setOnAction(e -> {
            RegistrationUI registrationUI = new RegistrationUI();
            registrationUI.start(new Stage());
        });

        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton, messageLabel);
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private void openChatUI(String username, int userId) {
        ChatUI chatUI = new ChatUI(username, userId);
        Stage chatStage = new Stage();
        chatUI.start(chatStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
