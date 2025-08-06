package com.example.unibike_version_4.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public class Admin {
    private final StringProperty adminId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private String password;

    public Admin(String adminId, String name, String email, String password) {
        setAdminId(adminId);
        setName(name);
        setEmail(email);
        this.password = password;
    }

    // ---------------- Property getters ----------------
    public StringProperty idProperty() { return adminId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }

    // ---------------- Standard getters ----------------
    public String getAdminId() { return adminId.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getPassword() { return password; }

    // ---------------- Setters ----------------
    public void setAdminId(String id) { this.adminId.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPassword(String password) { this.password = password; }

    // ---------------- Station Management ----------------
    /** Add new station and save to file */
    public Station addStation(String name, int capacity) {
        Station station = Station.addStation(name, capacity);
        if (station != null) {
            Station.saveToFile();
        }
        return station;
    }

    // ---------------- Bicycle Management ----------------

    /** Remove a bicycle from a station */
    public void removeBicycleFromStation(Station station, Bicycle bicycle) {
        if (station != null && bicycle != null) {
            station.removeBicycle(bicycle);
            Bicycle.removeBicycle(bicycle);
            Station.saveToFile();
        }
    }

    // ---------------- User Management ----------------
    /** Get all registered users */
    public List<User> getAllUsers() {
        return User.getAllUsers(); // loads from file
    }

    /** Disable a user (example: could set balance to 0 or mark as inactive) */
    public void disableUser(User user) {
        if (user != null) {
            user.setActive(false);
            User.saveAllUsers();

        }
    }

    /** Reset a user password */
    public void resetUserPassword(User user, String newPassword) {
        if (user != null) {
            user.setPassword(newPassword);
            User.saveAllUsers();

        }
    }

    // ---------------- Authentication Override ----------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return email.equals(admin.email) && password.equals(admin.password);
    }
}
