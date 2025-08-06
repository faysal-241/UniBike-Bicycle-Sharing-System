package com.example.unibike_version_4.model;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ride {
    private String id;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final User user;
    private final Bicycle bicycle;
    private double cost;

    private static final List<Ride> allRides = new ArrayList<>();
    private static final Path FILE_PATH = Paths.get("src/main/resources/com/example/unibike_version_4/data/reservation.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Ride(User user, Bicycle bicycle) {
        this.id = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.user = user;
        this.bicycle = bicycle;
        this.endTime = null; // ongoing ride

        // Mark bike as in-use/reserved
        bicycle.setAvailable(false);
        bicycle.setStatus(BicycleStatus.RESERVED);
        Bicycle.saveAllBicycles();

        allRides.add(this);
        saveAllToFile();
    }

    /** End the ride and calculate cost */
    public void endRide() {
        this.endTime = LocalDateTime.now();
        calculateCost();

        // Deduct cost from user
        user.deductBalance(cost);

        // Mark bike as available again
        bicycle.setAvailable(true);
        bicycle.setStatus(BicycleStatus.AVAILABLE);
        Bicycle.saveAllBicycles();

        // Add to user's ride history
        user.addHistoryEntry("Returned Bike", "Bike ID: " + bicycle.getId() + ", Cost: " + cost);

        saveAllToFile();
    }

    /** Calculate cost based on ride duration */
    private void calculateCost() {
        Duration duration = Duration.between(startTime, endTime);
        long minutes = Math.max(duration.toMinutes(), 1); // at least 1 min
        cost = FareCalculator.calculateFare(minutes); // one bike type
    }

    // ---------------- Getters ----------------
    public String getId() { return id; }
    public User getUser() { return user; }
    public Bicycle getBicycle() { return bicycle; }
    public double getCost() { return cost; }
    public LocalDateTime getEndTime() { return endTime; }

    public String getDuration() {
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        Duration duration = Duration.between(startTime, end);
        return duration.toMinutes() + " min";
    }

    public String getStartTimeFormatted() {
        return startTime.format(FORMATTER);
    }

    public String getEndTimeFormatted() {
        return (endTime == null) ? "In Progress" : endTime.format(FORMATTER);
    }

    // ---------------- File Persistence ----------------
    public static void saveAllToFile() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Ride ride : allRides) {
                    writer.write(
                            ride.id + "," +
                                    ride.user.getId() + "," +
                                    ride.bicycle.getId() + "," +
                                    ride.getStartTimeFormatted() + "," +
                                    ride.getEndTimeFormatted() + "," +
                                    ride.cost
                    );
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile() {
        allRides.clear();
        if (!Files.exists(FILE_PATH)) return;

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Ride ride = parseRide(parts);
                    if (ride != null) allRides.add(ride);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Ride parseRide(String[] parts) {
        try {
            String id = parts[0];
            User user = User.findById(parts[1]);
            Bicycle bike = Bicycle.getAllBicycles().stream()
                    .filter(b -> b.getId().equals(parts[2]))
                    .findFirst().orElse(null);

            LocalDateTime start = LocalDateTime.parse(parts[3], FORMATTER);
            LocalDateTime end = parts[4].equals("In Progress") ? null : LocalDateTime.parse(parts[4], FORMATTER);
            double cost = Double.parseDouble(parts[5]);

            if (user == null || bike == null) return null;

            Ride ride = new Ride(user, bike);
            ride.id = id;
            ride.cost = cost;
            ride.endTime = end;

            // Restore status for ongoing rides
            if (end == null) {
                bike.setAvailable(false);
                bike.setStatus(BicycleStatus.RESERVED);
            }

            return ride;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Ride> getAllRides() {
        return allRides;
    }
}
