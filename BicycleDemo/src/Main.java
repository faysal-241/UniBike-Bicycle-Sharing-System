// Main.java
// This class demonstrates the usage of the Unibike Campus Bicycle Sharing System.

import java.util.List;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UnibikeSystem system = new UnibikeSystem();
        Scanner scanner = new Scanner(System.in);

        // --- Initial Setup ---
        System.out.println("--- Initializing Unibike System ---");

        // Add some stations
        system.addStation(new Station("S001", "Main Campus Gate", "Lat:34.0, Lon:-118.0"));
        system.addStation(new Station("S002", "Library Plaza", "Lat:34.1, Lon:-118.1"));
        system.addStation(new Station("S003", "Student Union", "Lat:34.2, Lon:-118.2"));

        // Add some bikes to stations
        system.addBike(new Bike("B001", "S001"), "S001");
        system.addBike(new Bike("B002", "S001"), "S001");
        system.addBike(new Bike("B003", "S002"), "S002");
        system.addBike(new Bike("B004", "S002"), "S002");
        system.addBike(new Bike("B005", "S003"), "S003");

        // Register some users with roles and passwords (for initial setup, can be done via signup later)
        User adminUser = new User("U000", "Admin User", 1000.0, "adminpass", User.UserRole.ADMIN);
        User studentUser = new User("U001", "Alice Smith", 50.0, "alicepass", User.UserRole.STUDENT);
        User guestUser = new User("U002", "Bob Johnson", 20.0, "bobpass", User.UserRole.GUEST);
        User charlie = new User("U003", "Charlie Brown", 10.0, "charliepass", User.UserRole.STUDENT);

        system.registerUser(adminUser);
        system.registerUser(studentUser);
        system.registerUser(guestUser);
        system.registerUser(charlie);

        System.out.println("\n--- System Setup Complete ---");

        User currentUser = null; // To hold the currently logged-in user

        // --- Main Application Loop (Login/Signup) ---
        while (true) { // Loop indefinitely until explicit exit
            if (currentUser == null) {
                System.out.println("\n--- Unibike Main Menu ---");
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");

                int choice = -1;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1: // Login
                        System.out.println("\n--- Login Options ---");
                        System.out.println("1. Login as User");
                        System.out.println("2. Login as Admin");
                        System.out.println("3. Back to Main Menu");
                        System.out.print("Enter your login choice: ");
                        int loginChoice = -1;
                        try {
                            loginChoice = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            continue; // Go back to main menu
                        }

                        String loginId;
                        String loginPass;
                        switch (loginChoice) {
                            case 1: // Login as User
                                System.out.print("Enter User ID: ");
                                loginId = scanner.nextLine();
                                System.out.print("Enter Password: ");
                                loginPass = scanner.nextLine();
                                currentUser = system.loginUser(loginId, loginPass);
                                if (currentUser != null && currentUser.getRole() == User.UserRole.ADMIN) {
                                    System.out.println("Warning: You logged in as an Admin. Consider using 'Login as Admin' for full admin features.");
                                }
                                break;
                            case 2: // Login as Admin
                                System.out.print("Enter Admin User ID: ");
                                loginId = scanner.nextLine();
                                System.out.print("Enter Admin Password: ");
                                loginPass = scanner.nextLine();
                                currentUser = system.loginUser(loginId, loginPass);
                                if (currentUser != null && currentUser.getRole() != User.UserRole.ADMIN) {
                                    System.out.println("Error: User " + currentUser.getName() + " is not an Admin. Please login as a regular user.");
                                    currentUser = null; // Invalidate login if not an admin
                                }
                                break;
                            case 3: // Back to Main Menu
                                System.out.println("Returning to main menu.");
                                break;
                            default:
                                System.out.println("Invalid login choice. Returning to main menu.");
                                break;
                        }
                        break;
                    case 2: // Sign Up
                        System.out.println("\n--- New User Registration ---");
                        System.out.print("Enter New User ID: ");
                        String newUserId = scanner.nextLine();
                        System.out.print("Enter Your Name: ");
                        String newUserName = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String newUserPass = scanner.nextLine();
                        System.out.print("Enter Initial Balance (e.g., 20.0): ");
                        double initialBalance = 0.0;
                        try {
                            initialBalance = Double.parseDouble(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid balance. Setting to 0.0.");
                        }

                        // For simplicity, new users default to STUDENT role
                        User newUser = new User(newUserId, newUserName, initialBalance, newUserPass, User.UserRole.STUDENT);
                        system.registerUser(newUser);
                        System.out.println("Registration complete. You can now log in.");
                        break;
                    case 3: // Exit
                        System.out.println("Exiting Unibike System. Goodbye!");
                        scanner.close();
                        return; // Exit the program
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else { // User is logged in, show role-based menu
                System.out.println("\nWelcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")!");

                if (currentUser.getRole() == User.UserRole.ADMIN) {
                    // --- Admin Menu ---
                    System.out.println("\n--- Admin Panel ---");
                    System.out.println("1. View All Stations & Bikes");
                    System.out.println("2. Add New Bike");
                    System.out.println("3. Edit Station Details");
                    System.out.println("4. Remove Station");
                    System.out.println("5. Send Bike to Maintenance");
                    System.out.println("6. Bring Bike From Maintenance");
                    System.out.println("7. Remove Bike From System");
                    System.out.println("8. View User Rental History");
                    System.out.println("9. View All Users");
                    System.out.println("0. Logout");
                    System.out.print("Enter your choice: ");

                    int adminChoice = -1;
                    try {
                        adminChoice = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    switch (adminChoice) {
                        case 1: // View All Stations & Bikes
                            system.displayAllStations();
                            System.out.print("Enter Station ID to view bikes (or leave empty to skip): ");
                            String stationIdToView = scanner.nextLine();
                            if (!stationIdToView.isEmpty()) {
                                system.displayStationBikes(stationIdToView);
                            }
                            break;
                        case 2: // Add New Bike
                            System.out.print("Enter new Bike ID: ");
                            String newBikeId = scanner.nextLine();
                            System.out.print("Enter Station ID to add bike to: ");
                            String targetStationId = scanner.nextLine();
                            system.addBike(new Bike(newBikeId, targetStationId), targetStationId);
                            break;
                        case 3: // Edit Station Details
                            System.out.print("Enter Station ID to edit: ");
                            String editStationId = scanner.nextLine();
                            System.out.print("Enter New Station Name: ");
                            String newStationName = scanner.nextLine();
                            System.out.print("Enter New Station Location: ");
                            String newStationLocation = scanner.nextLine();
                            system.editStation(currentUser.getUserId(), editStationId, newStationName, newStationLocation);
                            break;
                        case 4: // Remove Station
                            System.out.print("Enter Station ID to remove (ensure it's empty): ");
                            String removeStationId = scanner.nextLine();
                            system.removeStation(currentUser.getUserId(), removeStationId);
                            break;
                        case 5: // Send Bike to Maintenance
                            System.out.print("Enter Bike ID to send to maintenance: ");
                            String bikeIdToMaintain = scanner.nextLine();
                            system.sendBikeToMaintenance(bikeIdToMaintain, currentUser.getUserId());
                            break;
                        case 6: // Bring Bike From Maintenance
                            System.out.print("Enter Bike ID to bring from maintenance: ");
                            String bikeIdFromMaintain = scanner.nextLine();
                            System.out.print("Enter Station ID to place bike at: ");
                            String placeStationId = scanner.nextLine();
                            system.bringBikeFromMaintenance(bikeIdFromMaintain, placeStationId, currentUser.getUserId());
                            break;
                        case 7: // Remove Bike From System
                            System.out.print("Enter Bike ID to permanently remove: ");
                            String bikeIdToRemove = scanner.nextLine();
                            system.removeBikeFromSystem(bikeIdToRemove, currentUser.getUserId());
                            break;
                        case 8: // View User Rental History
                            System.out.print("Enter User ID to view rental history: ");
                            String userHistoryId = scanner.nextLine();
                            system.displayUserRentalHistory(userHistoryId);
                            break;
                        case 9: // View All Users
                            system.displayAllUsers();
                            break;
                        case 0: // Logout
                            currentUser = null;
                            System.out.println("Logged out successfully.");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else { // User is STUDENT or GUEST
                    // --- User Menu ---
                    System.out.println("\n--- User Actions ---");
                    System.out.println("1. View Available Bicycles by Station/Location");
                    System.out.println("2. Borrow a Bicycle");
                    System.out.println("3. Return a Bicycle");
                    System.out.println("4. Pre-book a Bike");
                    System.out.println("5. View My Rental History");
                    System.out.println("6. View My Balance");
                    System.out.println("0. Logout");
                    System.out.print("Enter your choice: ");

                    int userChoice = -1;
                    try {
                        userChoice = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    switch (userChoice) {
                        case 1: // View Available Bicycles by Station/Location
                            system.displayAllStations();
                            System.out.print("Enter Station ID to view available bikes (or leave empty to skip): ");
                            String stationIdToView = scanner.nextLine();
                            if (!stationIdToView.isEmpty()) {
                                Station station = system.getStation(stationIdToView);
                                if (station != null) {
                                    List<Bike> availableBikes = station.getAvailableBikes();
                                    if (availableBikes.isEmpty()) {
                                        System.out.println("No available bikes at station " + station.getName() + ".");
                                    } else {
                                        System.out.println("--- Available Bikes at " + station.getName() + " ---");
                                        availableBikes.forEach(System.out::println);
                                    }
                                } else {
                                    System.out.println("Station with ID " + stationIdToView + " not found.");
                                }
                            }
                            break;
                        case 2: // Borrow a Bicycle
                            System.out.print("Enter Station ID to rent from: ");
                            String rentStationId = scanner.nextLine();
                            system.rentBike(currentUser.getUserId(), rentStationId);
                            break;
                        case 3: // Return a Bicycle
                            if (currentUser.getRentedBikeId() != null) {
                                System.out.println("You currently have Bike " + currentUser.getRentedBikeId() + " rented.");
                                System.out.print("Enter Station ID to return to: ");
                                String returnStationId = scanner.nextLine();
                                system.returnBike(currentUser.getUserId(), currentUser.getRentedBikeId(), returnStationId);
                            } else {
                                System.out.println("You do not have a bike rented.");
                            }
                            break;
                        case 4: // Pre-book a Bike
                            System.out.print("Enter Bike ID to reserve: ");
                            String bikeIdToReserve = scanner.nextLine();
                            System.out.print("Enter reservation duration in minutes (e.g., 15): ");
                            long duration = 0;
                            try {
                                duration = Long.parseLong(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid duration. Reservation failed.");
                                break;
                            }
                            system.reserveBike(currentUser.getUserId(), bikeIdToReserve, duration);
                            break;
                        case 5: // View My Rental History
                            system.displayUserRentalHistory(currentUser.getUserId());
                            break;
                        case 6: // View My Balance
                            system.displayUserStatus(currentUser.getUserId());
                            break;
                        case 0: // Logout
                            currentUser = null;
                            System.out.println("Logged out successfully.");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        }
    }
}