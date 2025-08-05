package com.example.unibike_version_4.util;

import java.io.*;
import java.util.*;

public class ReservationManager {

    private static final String RESERVATION_FILE = "src/main/resources/data/reservations.txt";

    /**
     * Add a new reservation to file
     * Format: username,bikeId,date
     */
    public static void addReservation(String username, String bikeId, String date) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_FILE, true))) {
            bw.write(username + "," + bikeId + "," + date);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a reservation for the given user and bike
     */
    public static void removeReservation(String username, String bikeId) {
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) return;

        List<String> updatedReservations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    // Keep all lines that are NOT this reservation
                    if (!(parts[0].equals(username) && parts[1].equals(bikeId))) {
                        updatedReservations.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rewrite file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String res : updatedReservations) {
                bw.write(res);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all reservations for a specific user
     * Returns list of strings like: "B001 on 2025-08-01"
     */
    public static List<String> getUserReservations(String username) {
        List<String> userReservations = new ArrayList<>();
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) return userReservations;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String bikeId = parts[1];
                    String date = parts[2];
                    userReservations.add(bikeId + " on " + date);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userReservations;
    }

    /**
     * Check if a user already has an active reservation for a bike
     */
    public static boolean hasReservation(String username, String bikeId) {
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(bikeId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
