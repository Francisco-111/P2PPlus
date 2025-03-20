package com.chatapp.peertopeerchatapp.client;

import com.chatapp.peertopeerchatapp.database.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            // Input validation
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                messageLabel.setText("All fields are required.");
                return;
            }

            if (!isValidEmail(email)) {
                messageLabel.setText("Invalid email format.");
                return;
            }

            if (password.length() < 6) {
                messageLabel.setText("Password must be at least 6 characters.");
                return;
            }

            // Attempt registration
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
