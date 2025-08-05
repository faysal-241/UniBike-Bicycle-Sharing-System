package com.example.unibike_version_4.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Bicycle {
    private String id;
    private String stationName;
    private boolean available;
    private BicycleStatus status = BicycleStatus.AVAILABLE;

    private static final List<Bicycle> allBicycles = new ArrayList<>();
    private static final Path FILE_PATH = Paths.get(
            "src/main/resources/com/example/unibike_version_4/data/bicycle.txt"
    );

    // ---------------- Constructor ----------------
    public Bicycle(String id, String stationName, boolean available) {
        this.id = id;
        this.stationName = stationName;
        this.available = available;
        this.status = available ? BicycleStatus.AVAILABLE : BicycleStatus.RESERVED;

        allBicycles.add(this);

        // Auto-link to station if exists
        Station station = Station.findByName(stationName);
        if (station != null) {
            station.addBicycle(this);
        }
    }

    // ---------------- Getters and Setters ----------------
    public String getId() { return id; }
    public String getStationName() { return stationName; }
    public boolean isAvailable() { return available; }

    public BicycleStatus getStatus() { return status; }

    public void setAvailable(boolean available) {
        this.available = available;
        this.status = available ? BicycleStatus.AVAILABLE : BicycleStatus.RESERVED;
    }

    public void setStatus(BicycleStatus status) {
        this.status = status;
        this.available = (status == BicycleStatus.AVAILABLE);
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
        Station station = Station.findByName(stationName);
        if (station != null && !station.getBicycles().contains(this)) {
            station.addBicycle(this);
        }
    }

    // ---------------- Static Bicycle Management ----------------
    public static List<Bicycle> getAllBicycles() {
        return allBicycles;
    }

    /** ✅ Add bicycle with auto-save */
    public static Bicycle addBicycle(String id, String stationName, boolean available) {
        if (allBicycles.stream().anyMatch(b -> b.getId().equals(id))) {
            return null; // ID already exists
        }
        Bicycle bike = new Bicycle(id, stationName, available);
        saveAllBicycles();
        return bike;
    }

    /** ✅ Remove bicycle with auto-save */
    public static void removeBicycle(Bicycle bike) {
        allBicycles.remove(bike);

        Station station = Station.findByName(bike.getStationName());
        if (station != null) {
            station.removeBicycle(bike);
        }

        saveAllBicycles();
    }

    // ---------------- File Persistence ----------------
    /** ✅ Save all bicycles to file */
    public static void saveAllBicycles() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                for (Bicycle bike : allBicycles) {
                    writer.write(
                            bike.id + "," +
                                    bike.stationName + "," +
                                    bike.available + "," +
                                    bike.status
                    );
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** ✅ Load bicycles from file */
    public static void loadFromFile() {
        allBicycles.clear();
        if (!Files.exists(FILE_PATH)) return;

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String id = parts[0];
                    String station = parts[1];
                    boolean available = Boolean.parseBoolean(parts[2]);

                    Bicycle bike = new Bicycle(id, station, available);

                    // If file contains status (4th column)
                    if (parts.length >= 4) {
                        try {
                            bike.setStatus(BicycleStatus.valueOf(parts[3]));
                        } catch (IllegalArgumentException e) {
                            bike.setStatus(available ? BicycleStatus.AVAILABLE : BicycleStatus.RESERVED);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
