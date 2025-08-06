package com.example.unibike_version_4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.example.unibike_version_4.model.*;
import com.example.unibike_version_4.util.SessionManager;

import java.io.IOException;
import java.util.Optional;

public class UserController {

    // --- Available Bikes Table ---
    @FXML private TableView<Bicycle> availableBikesTable;
    @FXML private TableColumn<Bicycle, String> bikeIdColumn;
    @FXML private TableColumn<Bicycle, String> stationColumn;

    // --- Reserved Bikes Table ---
    @FXML private TableView<Bicycle> reservedBikesTable;
    @FXML private TableColumn<Bicycle, String> reservedBikeIdColumn;
    @FXML private TableColumn<Bicycle, String> reservedStationColumn;

    @FXML private Label messageLabel;
    @FXML private Label balanceLabel;

    private ObservableList<Bicycle> availableBikes = FXCollections.observableArrayList();
    private ObservableList<Bicycle> reservedBikes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table column bindings for available bikes
        bikeIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        stationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStationName()));

        // Table column bindings for reserved bikes
        reservedBikeIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        reservedStationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStationName()));

        refreshAvailableBikes();
        refreshReservedBikes();
        updateBalanceLabel();
    }

    /** Refresh the list of available bikes **/
    private void refreshAvailableBikes() {
        availableBikes.clear();
        for (Bicycle bike : Bicycle.getAllBicycles()) {
            if (bike.isAvailable()) {
                availableBikes.add(bike);
            }
        }
        availableBikesTable.setItems(availableBikes);
    }

    /** Refresh the list of reserved bikes **/
    private void refreshReservedBikes() {
        reservedBikes.clear();
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) return;

        for (Bicycle bike : Bicycle.getAllBicycles()) {
            if (bike.getStatus() == BicycleStatus.RESERVED) {
                // Find if this bike is reserved by the current user
                boolean isReservedByUser = Ride.getAllRides().stream()
                        .anyMatch(r -> r.getUser().equals(currentUser) &&
                                r.getBicycle().equals(bike) &&
                                r.getEndTime() == null);
                if (isReservedByUser) {
                    reservedBikes.add(bike);
                }
            }
        }
        reservedBikesTable.setItems(reservedBikes);
    }

    /** Handle bike reservation (start ride without instant charge) **/
    @FXML
    private void handleReserveBike() {
        Bicycle selectedBike = availableBikesTable.getSelectionModel().getSelectedItem();

        if (selectedBike == null) {
            messageLabel.setText("Please select a bike first.");
            return;
        }

        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) {
            messageLabel.setText("Error: No user session found.");
            return;
        }

        // Reserve the bike (no instant charge)
        selectedBike.setAvailable(false);
        selectedBike.setStatus(BicycleStatus.RESERVED);
        Bicycle.saveAllBicycles();

        // Start a new ride
        new Ride(currentUser, selectedBike);
        currentUser.addHistoryEntry("Reserved Bike", "Bike ID: " + selectedBike.getId());
        User.saveAllUsers();

        messageLabel.setText("Bike reserved successfully: " + selectedBike.getId());
        refreshAvailableBikes();
        refreshReservedBikes();
        updateBalanceLabel();
    }

    /** Handle returning a reserved bike (end ride & charge) **/
    @FXML
    private void handleReturnBike() {
        Bicycle selectedBike = reservedBikesTable.getSelectionModel().getSelectedItem();

        if (selectedBike == null) {
            messageLabel.setText("Please select a bike to return.");
            return;
        }

        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) {
            messageLabel.setText("Error: No user session found.");
            return;
        }

        // Find the ongoing ride for this bike
        Optional<Ride> rideOpt = Ride.getAllRides().stream()
                .filter(r -> r.getUser().equals(currentUser) &&
                        r.getBicycle().equals(selectedBike) &&
                        r.getEndTime() == null)  // ongoing ride
                .findFirst();


        if (rideOpt.isEmpty()) {
            messageLabel.setText("No active ride found for this bike.");
            return;
        }

        Ride ride = rideOpt.get();
        ride.endRide(); // This will calculate cost and deduct from balance

        // Mark bike available again
        selectedBike.setStatus(BicycleStatus.AVAILABLE);
        selectedBike.setAvailable(true);
        Bicycle.saveAllBicycles();

        currentUser.addHistoryEntry("Returned Bike", "Bike ID: " + selectedBike.getId() +
                " | Fare: " + ride.getCost() + " units");
        User.saveAllUsers();

        messageLabel.setText("Bike returned. Fare: " + ride.getCost() + " units");
        refreshAvailableBikes();
        refreshReservedBikes();
        updateBalanceLabel();
    }

    /** View user's ride history **/
    @FXML
    private void handleViewHistory() {
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) return;

        StringBuilder historyText = new StringBuilder("Your Ride History:\n");
        for (String entry : currentUser.getHistory()) {
            historyText.append("- ").append(entry).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ride History");
        alert.setHeaderText("Your Ride History");
        alert.setContentText(historyText.toString());
        alert.showAndWait();
    }

    /** View balance popup **/
    @FXML
    private void handleViewBalance() {
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Current Balance");
        alert.setHeaderText("Your Current Balance");
        alert.setContentText("Balance: " + currentUser.getBalance() + " units");
        alert.showAndWait();
    }

    /** Add balance to the current user's account **/
    @FXML
    private void handleAddBalance() {
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Balance");
        dialog.setHeaderText("Enter amount to add:");
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) throw new NumberFormatException();

                currentUser.deductBalance(-amount); // negative to add
                messageLabel.setText("Balance updated successfully!");
                updateBalanceLabel();
            } catch (NumberFormatException e) {
                messageLabel.setText("Invalid amount entered.");
            }
        });
    }

    /** Logout and go back to login page **/
    @FXML
    private void handleLogout() throws IOException {
        SessionManager.setLoggedInUser(null);
        HelloApplication.changeScene("login.fxml");
    }

    /** Update balance label on the UI **/
    private void updateBalanceLabel() {
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser != null && balanceLabel != null) {
            balanceLabel.setText("Balance: " + currentUser.getBalance() + " BDT");
        }
    }
}
