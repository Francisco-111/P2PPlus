package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.UserService;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }

            int userId = userService.authenticateUser(username, password);
            if (userId != -1) {
                messageLabel.setText("Login successful! Welcome, " + username);
                openChatUI(username, userId, primaryStage);
            } else {
                messageLabel.setText("Invalid username or password.");
                clearMessageAfterDelay(messageLabel);
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

    private void openChatUI(String username, int userId, Stage primaryStage) {
        ChatUI chatUI = new ChatUI(username, userId);
        Stage chatStage = new Stage();
        chatUI.start(chatStage);
        primaryStage.close(); // Close the login window after successful login
    }

    private void clearMessageAfterDelay(Label messageLabel) {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> messageLabel.setText(""));
        pause.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
