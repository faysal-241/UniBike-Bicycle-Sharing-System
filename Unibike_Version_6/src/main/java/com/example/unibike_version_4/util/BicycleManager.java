package com.example.unibike_version_4.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BicycleManager {
    private static final String FILE_PATH = "bicycles.txt";

    // Load all bicycles as String[] {id, station, available}
    public static List<String[]> loadBicycles() {
        List<String[]> bicycles = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return bicycles;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                bicycles.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bicycles;
    }

    // Add a new bicycle
    public static void addBicycle(String id, String station, boolean available) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(id + "," + station + "," + available + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update availability of a bicycle
    public static void updateBicycleAvailability(String id, boolean available) {
        List<String[]> bicycles = loadBicycles();

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            for (String[] bike : bicycles) {
                if (bike[0].equals(id)) {
                    fw.write(id + "," + bike[1] + "," + available + "\n");
                } else {
                    fw.write(String.join(",", bike) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
