package com.example.unibike_version_4.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {

    // Use a runtime-safe file in the current working directory
    private static final Path USER_FILE = Paths.get(System.getProperty("user.dir"), "user.txt");

    /**
     * Check if username already exists
     */
    public static boolean userExists(String username) {
        try {
            if (!Files.exists(USER_FILE)) return false;
            List<String> lines = Files.readAllLines(USER_FILE);
            for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 && parts[1].trim().equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Save a new user with full details
     */
    public static void saveUser(String userId, String username, String email, String password, double balance) {
        try {
            // Ensure the file exists
            Files.createDirectories(USER_FILE.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    USER_FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(userId.trim() + "," +
                        username.trim() + "," +
                        email.trim() + "," +
                        password.trim() + "," +
                        balance);
                writer.newLine();
            }

            System.out.println("User saved: " + username + " to " + USER_FILE.toAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate username & password for login
     */
    public static boolean validateUser(String username, String password) {
        try {
            if (!Files.exists(USER_FILE)) {
                System.out.println("User file not found: " + USER_FILE.toAbsolutePath());
                return false;
            }

            List<String> lines = Files.readAllLines(USER_FILE);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String storedUsername = parts[1].trim();
                    String storedPassword = parts[3].trim();

                    if (storedUsername.equals(username.trim()) &&
                            storedPassword.equals(password.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
