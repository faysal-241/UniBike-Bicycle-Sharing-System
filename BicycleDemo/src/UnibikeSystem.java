// UnibikeSystem.java
// The core class managing all operations of the Unibike Campus Bicycle Sharing System.

import java.util.HashMap;
import java.util.Map;
import java.util.UUID; // For generating unique IDs for rental records

public class UnibikeSystem {
    private Map<String, User> users;
    private Map<String, Station> stations;
    private Map<String, Bike> bikes; // All bikes in the system, regardless of location

    private static final double RENTAL_RATE_PER_MINUTE = 0.10; // Example rental rate: $0.10 per minute
    private static final double MINIMUM_BALANCE_FOR_RENTAL = 5.0; // Minimum balance required to rent a bike

    public UnibikeSystem() {
        this.users = new HashMap<>();
        this.stations = new HashMap<>();
        this.bikes = new HashMap<>();
    }

    // --- User Management ---
    public void registerUser(User user) {
        if (users.containsKey(user.getUserId())) {
            System.out.println("Error: User with ID " + user.getUserId() + " already exists.");
        } else {
            users.put(user.getUserId(), user);
            System.out.println("User " + user.getName() + " (" + user.getRole() + ") registered successfully.");
        }
    }

    /**
     * Authenticates a user based on their ID and password.
     *
     * @param userId   The ID of the user.
     * @param password The password of the user.
     * @return The User object if authentication is successful, null otherwise.
     */
    public User loginUser(String userId, String password) {
        User user = users.get(userId);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + user.getName());
            return user;
        } else {
            System.out.println("Login failed for user ID: " + userId + ". Invalid credentials.");
            return null;
        }
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void displayAllUsers() {
        System.out.println("\n--- Registered Users ---");
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        users.values().forEach(System.out::println);
    }

    public void displayUserStatus(String userId) {
        User user = getUser(userId);
        if (user != null) {
            System.out.println("\n--- User Status for " + user.getName() + " (" + user.getRole() + ") ---");
            System.out.println(user);
        } else {
            System.out.println("User with ID " + userId + " not found.");
        }
    }

    /**
     * Displays the rental history for a specific user.
     *
     * @param userId The ID of the user whose history is to be displayed.
     */
    public void displayUserRentalHistory(String userId) {
        User user = getUser(userId);
        if (user == null) {
            System.out.println("User with ID " + userId + " not found. Cannot display rental history.");
            return;
        }
        System.out.println("\n--- Rental History for " + user.getName() + " ---");
        if (user.getRentalHistory().isEmpty()) {
            System.out.println("No rental history found for " + user.getName() + ".");
        } else {
            user.getRentalHistory().forEach(System.out::println);
        }
    }

    // --- Station Management ---
    public void addStation(Station station) {
        if (stations.containsKey(station.getStationId())) {
            System.out.println("Error: Station with ID " + station.getStationId() + " already exists.");
        } else {
            stations.put(station.getStationId(), station);
            System.out.println("Station " + station.getName() + " added successfully.");
        }
    }

    public Station getStation(String stationId) {
        return stations.get(stationId);
    }

    public void displayAllStations() {
        System.out.println("\n--- Available Stations ---");
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }
        stations.values().forEach(System.out::println);
    }

    public void displayStationBikes(String stationId) {
        Station station = getStation(stationId);
        if (station != null) {
            System.out.println("\n--- Bikes at Station " + station.getName() + " (" + station.getStationId() + ") ---");
            if (station.getBikes().isEmpty()) {
                System.out.println("No bikes at this station.");
            } else {
                station.getBikes().forEach(System.out::println);
            }
        } else {
            System.out.println("Station with ID " + stationId + " not found.");
        }
    }

    /**
     * Admin function: Edits the name and location of an existing station.
     *
     * @param adminUserId The ID of the admin performing the action.
     * @param stationId   The ID of the station to edit.
     * @param newName     The new name for the station.
     * @param newLocation The new location for the station.
     */
    public void editStation(String adminUserId, String stationId, String newName, String newLocation) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can edit stations.");
            return;
        }

        Station station = getStation(stationId);
        if (station == null) {
            System.out.println("Edit Failed: Station " + stationId + " not found.");
            return;
        }
        station.setName(newName);
        station.setLocation(newLocation);
        System.out.println("Station " + stationId + " updated to Name: " + newName + ", Location: " + newLocation);
    }

    /**
     * Admin function: Removes a station from the system.
     * Requires all bikes to be moved from the station first.
     *
     * @param adminUserId The ID of the admin performing the action.
     * @param stationId   The ID of the station to remove.
     */
    public void removeStation(String adminUserId, String stationId) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can remove stations.");
            return;
        }

        Station station = getStation(stationId);
        if (station == null) {
            System.out.println("Removal Failed: Station " + stationId + " not found.");
            return;
        }
        if (!station.getBikes().isEmpty()) {
            System.out.println("Removal Failed: Station " + stationId + " still has " + station.getBikes().size() + " bikes. Please move them first.");
            return;
        }
        stations.remove(stationId);
        System.out.println("Station " + stationId + " removed successfully.");
    }

    // --- Bike Management ---
    public void addBike(Bike bike, String stationId) {
        Station station = getStation(stationId);
        if (station == null) {
            System.out.println("Error: Station " + stationId + " not found. Cannot add bike.");
            return;
        }
        if (bikes.containsKey(bike.getBikeId())) {
            System.out.println("Error: Bike with ID " + bike.getBikeId() + " already exists in the system.");
            return;
        }
        bikes.put(bike.getBikeId(), bike);
        station.addBike(bike); // Add bike to the station's list
        System.out.println("Bike " + bike.getBikeId() + " added to system and placed at station " + station.getName());
    }

    public Bike getBike(String bikeId) {
        return bikes.get(bikeId);
    }

    /**
     * Admin function: Edits the status of a bike.
     *
     * @param adminUserId The ID of the admin performing the action.
     * @param bikeId      The ID of the bike to edit.
     * @param newStatus   The new status for the bike.
     */
    public void editBikeStatus(String adminUserId, String bikeId, Bike.BikeStatus newStatus) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can edit bike status.");
            return;
        }

        Bike bike = getBike(bikeId);
        if (bike == null) {
            System.out.println("Edit Failed: Bike " + bikeId + " not found.");
            return;
        }
        bike.setStatus(newStatus);
        System.out.println("Bike " + bikeId + " status updated to " + newStatus + " by Admin " + admin.getName() + ".");
    }

    /**
     * Admin function: Removes a bike permanently from the system.
     *
     * @param adminUserId The ID of the admin performing the action.
     * @param bikeId      The ID of the bike to remove.
     */
    public void removeBikeFromSystem(String adminUserId, String bikeId) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can remove bikes from the system.");
            return;
        }

        Bike bike = getBike(bikeId);
        if (bike == null) {
            System.out.println("Removal Failed: Bike " + bikeId + " not found.");
            return;
        }
        if (bike.getStatus() == Bike.BikeStatus.RENTED) {
            System.out.println("Removal Failed: Bike " + bikeId + " is currently rented. Cannot remove.");
            return;
        }
        // Remove from its current station if it's at one
        if (bike.getCurrentStationId() != null) {
            Station currentStation = getStation(bike.getCurrentStationId());
            if (currentStation != null) {
                currentStation.removeBike(bikeId);
            }
        }
        bikes.remove(bikeId);
        System.out.println("Bike " + bikeId + " permanently removed from the system by Admin " + admin.getName() + ".");
    }

    // --- Core Operations: Renting and Returning Bikes ---

    /**
     * Allows a user to rent an available bike from a specified station.
     * Handles reservation logic.
     *
     * @param userId    The ID of the user.
     * @param stationId The ID of the station to rent from.
     */
    public void rentBike(String userId, String stationId) {
        User user = getUser(userId);
        Station station = getStation(stationId);

        if (user == null) {
            System.out.println("Rental Failed: User " + userId + " not found.");
            return;
        }
        if (station == null) {
            System.out.println("Rental Failed: Station " + stationId + " not found.");
            return;
        }
        if (user.getRentedBikeId() != null) {
            System.out.println("Rental Failed: " + user.getName() + " already has a bike (" + user.getRentedBikeId() + ") rented. Please return it first.");
            return;
        }
        if (user.getBalance() < MINIMUM_BALANCE_FOR_RENTAL) {
            System.out.println("Rental Failed: " + user.getName() + " has insufficient balance ($" + String.format("%.2f", user.getBalance()) + "). Minimum required: $" + String.format("%.2f", MINIMUM_BALANCE_FOR_RENTAL));
            return;
        }

        // Check for expired reservations before renting
        checkAndExpireReservations();

        // Find an available or reserved bike at the station
        Bike bikeToRent = null;
        for (Bike bike : station.getBikes()) {
            if (bike.getStatus() == Bike.BikeStatus.AVAILABLE) {
                bikeToRent = bike;
                break;
            } else if (bike.getStatus() == Bike.BikeStatus.RESERVED && bike.getReservedByUserId().equals(userId)) {
                // User is trying to rent their own reserved bike
                bikeToRent = bike;
                System.out.println("Renting your reserved bike " + bike.getBikeId() + ".");
                break;
            }
        }

        if (bikeToRent == null) {
            System.out.println("Rental Failed: No available bikes at station " + station.getName() + " (" + station.getStationId() + ").");
            return;
        }

        // If the bike is reserved by someone else, prevent rental
        if (bikeToRent.getStatus() == Bike.BikeStatus.RESERVED && !bikeToRent.getReservedByUserId().equals(userId)) {
            System.out.println("Rental Failed: Bike " + bikeToRent.getBikeId() + " is reserved by another user (" + bikeToRent.getReservedByUserId() + ").");
            return;
        }

        // Perform the rental
        bikeToRent.setStatus(Bike.BikeStatus.RENTED);
        bikeToRent.setCurrentStationId(null); // Bike is no longer at a station
        bikeToRent.setReservedByUserId(null); // Clear reservation info
        bikeToRent.setReservationEndTime(0);
        station.removeBike(bikeToRent.getBikeId()); // Remove from station's list
        user.setRentedBikeId(bikeToRent.getBikeId());
        user.setRentalStartTime(System.currentTimeMillis()); // Set rental start time

        System.out.println("Success: " + user.getName() + " rented Bike " + bikeToRent.getBikeId() + " from Station " + station.getName() + ".");
        System.out.println("Remember to return the bike to any station when done.");
    }

    /**
     * Allows a user to return a rented bike to a specified station.
     * Calculates ride duration and fare.
     *
     * @param userId          The ID of the user returning the bike.
     * @param bikeId          The ID of the bike being returned.
     * @param returnStationId The ID of the station where the bike is returned.
     */
    public void returnBike(String userId, String bikeId, String returnStationId) {
        User user = getUser(userId);
        Bike bike = getBike(bikeId);
        Station returnStation = getStation(returnStationId);

        if (user == null) {
            System.out.println("Return Failed: User " + userId + " not found.");
            return;
        }
        if (bike == null) {
            System.out.println("Return Failed: Bike " + bikeId + " not found in the system.");
            return;
        }
        if (returnStation == null) {
            System.out.println("Return Failed: Return station " + returnStationId + " not found.");
            return;
        }
        if (!bike.getStatus().equals(Bike.BikeStatus.RENTED)) {
            System.out.println("Return Failed: Bike " + bikeId + " is not currently rented. Its status is " + bike.getStatus() + ".");
            return;
        }
        if (!user.getRentedBikeId().equals(bikeId)) {
            System.out.println("Return Failed: User " + user.getName() + " did not rent Bike " + bikeId + ". They rented " + user.getRentedBikeId() + ".");
            return;
        }

        long rentalEndTime = System.currentTimeMillis();
        long rentalStartTime = user.getRentalStartTime();
        long durationMillis = rentalEndTime - rentalStartTime;
        double durationMinutes = (double) durationMillis / (1000 * 60);

        // Simple fare calculation: per minute
        double rentalCost = durationMinutes * RENTAL_RATE_PER_MINUTE;
        if (rentalCost < 0) rentalCost = 0; // Prevent negative cost if time somehow goes backwards

        System.out.println("Calculating rental cost for Bike " + bikeId + " (Duration: " + String.format("%.2f", durationMinutes) + " minutes)...");
        if (!user.deductBalance(rentalCost)) {
            System.out.println("Return Failed: " + user.getName() + " has insufficient balance to cover rental cost ($" + String.format("%.2f", rentalCost) + "). Please add funds.");
            // In a real system, you might put the bike into a 'unpaid' status or mark the user as owing.
            // For now, we'll prevent the return if balance is insufficient.
            return;
        }

        // Perform the return
        bike.setStatus(Bike.BikeStatus.AVAILABLE);
        bike.setCurrentStationId(returnStation.getStationId());
        returnStation.addBike(bike); // Add bike back to the station's list
        user.setRentedBikeId(null); // User no longer has a rented bike
        user.setRentalStartTime(0); // Reset rental start time for user

        // Create and add rental record to user's history
        String recordId = UUID.randomUUID().toString();
        RentalRecord record = new RentalRecord(recordId, userId, bikeId, user.getRentalHistory().isEmpty() ? "N/A" : user.getRentalHistory().get(user.getRentalHistory().size() - 1).getRentStationId(), rentalStartTime);
        record.setReturnStationId(returnStationId);
        record.setRentalEndTime(rentalEndTime);
        record.setCost(rentalCost);
        user.addRentalRecord(record);


        System.out.println("Success: Bike " + bikeId + " returned by " + user.getName() + " to Station " + returnStation.getName() + ".");
        System.out.println("Rental cost: $" + String.format("%.2f", rentalCost) + " deducted from " + user.getName() + "'s balance.");
    }

    // --- Maintenance Operations (Admin only) ---
    public void sendBikeToMaintenance(String bikeId, String adminUserId) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can send bikes to maintenance.");
            return;
        }

        Bike bike = getBike(bikeId);
        if (bike == null) {
            System.out.println("Error: Bike " + bikeId + " not found.");
            return;
        }
        if (bike.getStatus() == Bike.BikeStatus.RENTED) {
            System.out.println("Error: Bike " + bikeId + " is currently rented and cannot be sent to maintenance.");
            return;
        }
        if (bike.getCurrentStationId() != null) {
            Station currentStation = getStation(bike.getCurrentStationId());
            if (currentStation != null) {
                currentStation.removeBike(bikeId); // Remove from station if it was there
            }
        }
        bike.setStatus(Bike.BikeStatus.IN_MAINTENANCE);
        bike.setCurrentStationId(null); // No longer at a station
        System.out.println("Bike " + bikeId + " sent to maintenance by Admin " + admin.getName() + ".");
    }

    public void bringBikeFromMaintenance(String bikeId, String stationId, String adminUserId) {
        User admin = getUser(adminUserId);
        if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
            System.out.println("Permission Denied: Only admins can bring bikes from maintenance.");
            return;
        }

        Bike bike = getBike(bikeId);
        Station station = getStation(stationId);
        if (bike == null) {
            System.out.println("Error: Bike " + bikeId + " not found.");
            return;
        }
        if (station == null) {
            System.out.println("Error: Station " + stationId + " not found. Cannot place bike.");
            return;
        }
        if (bike.getStatus() != Bike.BikeStatus.IN_MAINTENANCE) {
            System.out.println("Error: Bike " + bikeId + " is not in maintenance. Its status is " + bike.getStatus() + ".");
            return;
        }
        bike.setStatus(Bike.BikeStatus.AVAILABLE);
        station.addBike(bike); // Add bike back to the station
        System.out.println("Bike " + bikeId + " brought from maintenance and placed at station " + station.getName() + " by Admin " + admin.getName() + ".");
    }

    // --- Pre-booking/Reservation System ---

    /**
     * Allows a user to reserve an available bike for a specified duration.
     *
     * @param userId          The ID of the user making the reservation.
     * @param bikeId          The ID of the bike to reserve.
     * @param durationMinutes The duration of the reservation in minutes.
     */
    public void reserveBike(String userId, String bikeId, long durationMinutes) {
        User user = getUser(userId);
        Bike bike = getBike(bikeId);

        if (user == null) {
            System.out.println("Reservation Failed: User " + userId + " not found.");
            return;
        }
        if (bike == null) {
            System.out.println("Reservation Failed: Bike " + bikeId + " not found.");
            return;
        }
        if (bike.getStatus() != Bike.BikeStatus.AVAILABLE) {
            System.out.println("Reservation Failed: Bike " + bikeId + " is not available for reservation. Current status: " + bike.getStatus() + ".");
            return;
        }
        if (user.getRentedBikeId() != null) {
            System.out.println("Reservation Failed: " + user.getName() + " already has a bike (" + user.getRentedBikeId() + ") rented. Cannot reserve another.");
            return;
        }

        // Set bike status to RESERVED
        bike.setStatus(Bike.BikeStatus.RESERVED);
        bike.setReservedByUserId(userId);
        bike.setReservationEndTime(System.currentTimeMillis() + (durationMinutes * 60 * 1000)); // Calculate end time

        System.out.println("Success: Bike " + bikeId + " reserved by " + user.getName() + " for " + durationMinutes + " minutes.");
    }

    /**
     * Allows a user to cancel their reservation.
     *
     * @param userId The ID of the user cancelling the reservation.
     * @param bikeId The ID of the bike whose reservation is being cancelled.
     */
    public void cancelReservation(String userId, String bikeId) {
        User user = getUser(userId);
        Bike bike = getBike(bikeId);

        if (user == null) {
            System.out.println("Cancellation Failed: User " + userId + " not found.");
            return;
        }
        if (bike == null) {
            System.out.println("Cancellation Failed: Bike " + bikeId + " not found.");
            return;
        }
        if (bike.getStatus() != Bike.BikeStatus.RESERVED || !bike.getReservedByUserId().equals(userId)) {
            System.out.println("Cancellation Failed: Bike " + bikeId + " is not reserved by " + user.getName() + " or is not in RESERVED status.");
            return;
        }

        // Revert bike status to AVAILABLE and clear reservation info
        bike.setStatus(Bike.BikeStatus.AVAILABLE);
        bike.setReservedByUserId(null);
        bike.setReservationEndTime(0);

        System.out.println("Success: Reservation for Bike " + bikeId + " cancelled by " + user.getName() + ".");
    }

    /**
     * Checks all reserved bikes and sets them back to AVAILABLE if their reservation time has expired.
     * In a real system, this would be called periodically (e.g., by a background thread).
     */
    public void checkAndExpireReservations() {
        long currentTime = System.currentTimeMillis();
        for (Bike bike : bikes.values()) {
            if (bike.getStatus() == Bike.BikeStatus.RESERVED && bike.getReservationEndTime() > 0 && bike.getReservationEndTime() < currentTime) {
                System.out.println("Reservation for Bike " + bike.getBikeId() + " by " + bike.getReservedByUserId() + " has expired. Setting status to AVAILABLE.");
                bike.setStatus(Bike.BikeStatus.AVAILABLE);
                bike.setReservedByUserId(null);
                bike.setReservationEndTime(0);
            }
        }
    }
}