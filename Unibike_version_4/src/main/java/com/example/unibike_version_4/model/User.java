package com.example.unibike_version_4.model;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private double balance;
    private boolean active;

    // Ride and history
    private final List<Ride> rideHistory = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    // Global user list
    private static final List<User> allUsers = new ArrayList<>();

    // File paths
    private static final Path USER_FILE = Paths.get(
            "src/main/resources/com/example/unibike_version_4/data/user.txt"
    );
    private static final Path HISTORY_FILE = Paths.get(
            "src/main/resources/com/example/unibike_version_4/data/userhistory.txt"
    );

    // ---------------- Constructor ----------------
    public User(String id, String username, String email, String password, double balance, boolean active) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.active = active;
        allUsers.add(this);
    }

    // ---------------- Lookup Methods ----------------
    public static User findById(String id) {
        for (User user : allUsers) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public void setActive(boolean active) {
        this.active = active;
        saveAllUsers();
    }

    public void setPassword(String password) {
        this.password = password;
        saveAllUsers();
    }

    public static User authenticate(String username, String password) {
        return allUsers.stream()
                .filter(u -> u.username.equals(username) && u.password.equals(password) && u.active)
                .findFirst()
                .orElse(null);
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(allUsers);
    }

    // ---------------- Ride & History Methods ----------------
    public void addRide(Ride ride) {
        rideHistory.add(ride);
        addHistoryEntry("Ride", "Bicycle ID: " + ride.getBicycle().getId());
    }

    public void addHistoryEntry(String action, String details) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String entry = "- " + timestamp + " - " + action + ": " + details;

        history.add(entry);
        saveHistoryEntry(this.username, entry);
    }

    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    private static void saveHistoryEntry(String username, String entry) {
        try {
            Files.createDirectories(HISTORY_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(HISTORY_FILE,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(username + "|" + entry);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadHistoryFromFile() {
        if (!Files.exists(HISTORY_FILE)) return;

        try (BufferedReader reader = Files.newBufferedReader(HISTORY_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2); // username|historyentry
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String entry = parts[1].trim();

                    User u = getUserByUsername(username);
                    if (u != null) {
                        u.history.add(entry);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- Balance Methods ----------------
    public void deductBalance(double cost) {
        this.balance -= cost;
        if (this.balance < 0) this.balance = 0;
        saveAllUsers(); // ✅ Update file immediately
    }

    // ---------------- File Operations ----------------
    public static void loadFromFile() {
        allUsers.clear();
        if (!Files.exists(USER_FILE)) return;

        try (BufferedReader reader = Files.newBufferedReader(USER_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String id = parts[0];
                    String username = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    double balance = Double.parseDouble(parts[4]);
                    boolean active = Boolean.parseBoolean(parts[5]);

                    new User(id, username, email, password, balance, active);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load persistent history
        loadHistoryFromFile();
    }

    public static void saveUser(User user) {
        try {
            Files.createDirectories(USER_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(user.id + "," + user.username + "," + user.email + "," +
                        user.password + "," + user.balance + "," + user.active);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllUsers() {
        try {
            Files.createDirectories(USER_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (User user : allUsers) {
                    writer.write(user.id + "," + user.username + "," + user.email + "," +
                            user.password + "," + user.balance + "," + user.active);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUserByUsername(String username) {
        for (User u : allUsers) {
            if (u.username.equals(username)) return u;
        }
        return null;
    }

    // ---------------- Getters ----------------
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }
    public boolean isActive() { return active; }
    public String getName() { return username; }
}
