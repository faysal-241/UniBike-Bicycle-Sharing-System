package com.example.unibike_version_4.controller;

import com.example.unibike_version_4.model.User;
import com.example.unibike_version_4.util.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // ---------- Validation ----------
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("All fields are required.", Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", Color.RED);
            return;
        }

        // Reload users from file to check for duplicates
        User.loadFromFile();
        if (User.getAllUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            showMessage("Username already taken.", Color.RED);
            return;
        }

        // ---------- Create and Save User ----------
        String userId = "U" + System.currentTimeMillis();
        User newUser = new User(userId, username, email, password, 0.0, true);

        // Save to file
        User.saveUser(newUser);

        showMessage("Signup successful! Please log in.", Color.GREEN);

        // Optional: Clear fields
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        HelloApplication.changeScene("login.fxml"); // <- use correct FXML file name
    }

    private void showMessage(String text, Color color) {
        messageLabel.setTextFill(color);
        messageLabel.setText(text);
        messageLabel.setVisible(true);
    }
}
