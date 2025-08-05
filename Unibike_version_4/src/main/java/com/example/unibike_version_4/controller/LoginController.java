package com.example.unibike_version_4.controller;

import com.example.unibike_version_4.model.User;
import com.example.unibike_version_4.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private ChoiceBox<String> roleChoiceBox;

    @FXML
    public void initialize() {
        // Load users from file so authentication works
        User.loadFromFile();
        System.out.println("Loaded users: " + User.getAllUsers().size());

        roleChoiceBox.getItems().addAll("User", "Admin");
        roleChoiceBox.setValue("User"); // Default role
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleChoiceBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            messageLabel.setVisible(true);
            return;
        }

        // ---------------- Admin Login ----------------
        if ("Admin".equalsIgnoreCase(role)) {
            if ("admin".equalsIgnoreCase(username) && "adminpass".equals(password)) {
                messageLabel.setText("Admin login successful!");
                messageLabel.setVisible(true);

                SessionManager.setLoggedInUser(null); // Admin has no User object

                // ✅ Load bicycles before showing Admin panel
                com.example.unibike_version_4.model.Bicycle.loadFromFile();

                HelloApplication.changeScene("admin-panel.fxml");
            } else {
                messageLabel.setText("Invalid Admin credentials.");
                messageLabel.setVisible(true);
            }
            return;
        }

        // ---------------- User Login ----------------
        User loggedInUser = User.authenticate(username, password);
        if (loggedInUser != null) {
            messageLabel.setText("Login successful!");
            messageLabel.setVisible(true);

            SessionManager.setLoggedInUser(loggedInUser);

            // ✅ Load bicycles before showing User view
            com.example.unibike_version_4.model.Bicycle.loadFromFile();

            HelloApplication.changeScene("user-view.fxml");
        } else {
            messageLabel.setText("Invalid username or password.");
            messageLabel.setVisible(true);
        }
    }


    @FXML
    private void onSignupLinkClick() throws IOException {
        HelloApplication.changeScene("signup.fxml");
    }
}
