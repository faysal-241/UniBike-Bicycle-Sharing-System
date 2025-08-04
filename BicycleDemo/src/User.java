// User.java
// Represents a user in the Unibike system.

import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs for rental records

public class User {
    private String userId;
    private String name;
    private String password; // Added for login system
    private double balance;
    private String rentedBikeId; // ID of the bike currently rented by the user
    private long rentalStartTime; // Timestamp when the user rented the current bike
    private UserRole role; // Added for user roles
    private List<RentalRecord> rentalHistory; // Added for usage history

    public enum UserRole {
        STUDENT,
        GUEST,
        ADMIN
    }

    public User(String userId, String name, double initialBalance, String password, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.balance = initialBalance;
        this.password = password;
        this.rentedBikeId = null; // No bike rented initially
        this.rentalStartTime = 0; // No rental started initially
        this.role = role;
        this.rentalHistory = new ArrayList<>();
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public String getRentedBikeId() {
        return rentedBikeId;
    }

    public long getRentalStartTime() {
        return rentalStartTime;
    }

    public UserRole getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public List<RentalRecord> getRentalHistory() {
        return rentalHistory;
    }

    // Setters
    public void setRentedBikeId(String rentedBikeId) {
        this.rentedBikeId = rentedBikeId;
    }

    public void setRentalStartTime(long rentalStartTime) {
        this.rentalStartTime = rentalStartTime;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // Methods for balance management
    public void addBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println(name + "'s balance updated. New balance: $" + String.format("%.2f", this.balance));
        } else {
            System.out.println("Cannot add non-positive amount.");
        }
    }

    public boolean deductBalance(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            System.out.println(name + "'s balance updated. New balance: $" + String.format("%.2f", this.balance));
            return true;
        } else {
            System.out.println("Insufficient balance or invalid amount for " + name + ". Current balance: $" + String.format("%.2f", this.balance));
            return false;
        }
    }

    public void addRentalRecord(RentalRecord record) {
        if (record != null) {
            this.rentalHistory.add(record);
        }
    }

    @Override
    public String toString() {
        return "User ID: " + userId + ", Name: " + name + ", Role: " + role + ", Balance: $" + String.format("%.2f", balance) +
                ", Rented Bike: " + (rentedBikeId != null ? rentedBikeId : "None");
    }
}