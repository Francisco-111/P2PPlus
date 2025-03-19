package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationUI extends Application {

    private final UserService userService = new UserService();

    @Override
    public void start(Stage primaryStage) {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            if (userService.registerUser(username, password, email)) {
                messageLabel.setText("Registration successful! You can now log in.");
            } else {
                messageLabel.setText("Registration failed. Username may already exist.");
            }
        });

        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, emailLabel, emailField, registerButton, messageLabel);
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Register");
        primaryStage.show();
    }
}
