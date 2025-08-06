package com.example.unibike_version_4.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Station {
    private String name;
    private int capacity;
    private String location; // Optional for admin use
    private final List<Bicycle> bicycles = new ArrayList<>();

    private static final List<Station> allStations = new ArrayList<>();
    private static final Path FILE_PATH = Paths.get("src/main/resources/com/example/unibike_version_4/data/station.txt");

    // ---------------- Constructor ----------------
    public Station(String name, int capacity) {
        this(name, capacity, "Unknown"); // default location
    }

    public Station(String name, int capacity, String location) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        allStations.add(this);
    }

    // ---------------- Getters and Setters ----------------
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }

    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setLocation(String location) { this.location = location; }

    public List<Bicycle> getBicycles() { return bicycles; }

    /** ✅ Get only available bicycles */
    public List<Bicycle> getAvailableBicycles() {
        return bicycles.stream()
                .filter(Bicycle::isAvailable)
                .collect(Collectors.toList());
    }

    public void addBicycle(Bicycle bike) {
        if (!bicycles.contains(bike) && bicycles.size() < capacity) {

            bicycles.add(bike);
        }
    }

    public void removeBicycle(Bicycle bike) {
        bicycles.remove(bike);
    }

    // ---------------- Static Methods ----------------
    public static List<Station> getAllStations() {
        return allStations;
    }

    /** ✅ Find a station by name */
    public static Station findByName(String name) {
        return allStations.stream()
                .filter(s -> s.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /** ✅ Add new station and save to file */
    public static Station addStation(String name, int capacity) {
        return addStation(name, capacity, "Unknown");
    }

    public static Station addStation(String name, int capacity, String location) {
        // Prevent duplicates
        if (findByName(name) != null) return null;

        Station station = new Station(name, capacity, location);
        saveToFile();
        return station;
    }

    /** ✅ Remove station and save to file */
    public static void removeStation(Station station) {
        allStations.remove(station);
        saveToFile();
    }

    // ---------------- File Persistence ----------------
    /** Save all stations to file */
    public static void saveToFile() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                for (Station station : allStations) {
                    // Save name,capacity,location
                    writer.write(station.name + "," + station.capacity + "," + station.location);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load all stations from file */
    public static void loadFromFile() {
        allStations.clear();

        if (!Files.exists(FILE_PATH)) return;

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0];
                    int capacity = Integer.parseInt(parts[1]);
                    String location = parts.length >= 3 ? parts[2] : "Unknown";

                    // Avoid duplicates
                    if (findByName(name) == null) {
                        new Station(name, capacity, location);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
