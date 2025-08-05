package com.example.unibike_version_4.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsageHistory {
    private final LocalDateTime timestamp;
    private final String userId;
    private final String action;
    private final String details;
    private final double amount;

    public UsageHistory(String userId, String action, String details) {
        this(userId, action, details, 0.0);
    }

    public UsageHistory(String userId, String action, String details, double amount) {
        this.timestamp = LocalDateTime.now();
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.amount = amount;
    }

    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public double getAmount() { return amount; }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String toDisplayString() {
        String base = getFormattedTimestamp() + " - " + action;
        if (!details.isEmpty()) base += ": " + details;
        if (amount > 0) base += String.format(" [$%.2f]", amount);
        return base;
    }

    @Override
    public String toString() {
        return "UsageHistory{" +
                "timestamp=" + getFormattedTimestamp() +
                ", userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", details='" + details + '\'' +
                ", amount=" + amount +
                '}';
    }
}