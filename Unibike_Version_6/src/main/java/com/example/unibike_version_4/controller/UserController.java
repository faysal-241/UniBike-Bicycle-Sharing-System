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

    // --- Station ComboBoxes ---
    @FXML private ComboBox<String> stationFilterComboBox;
    @FXML private ComboBox<String> returnStationComboBox;

    private ObservableList<Bicycle> availableBikes = FXCollections.observableArrayList();
    private ObservableList<Bicycle> reservedBikes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ✅ Always load data from file before setting up UI
        Station.loadFromFile();
        Bicycle.loadFromFile();
        Ride.loadFromFile();
        User.loadFromFile();

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

        populateStationComboBoxes();

        // Filter available bikes on station selection
        stationFilterComboBox.setOnAction(event -> refreshAvailableBikesByStation());

        // Initial refresh
        refreshAvailableBikesByStation();
        refreshReservedBikes();
        updateBalanceLabel();
    }

    /** Populate both ComboBoxes with station names **/
    private void populateStationComboBoxes() {
        ObservableList<String> stationNames = FXCollections.observableArrayList();
        for (Station station : Station.getAllStations()) {
            stationNames.add(station.getName());
        }

        stationFilterComboBox.setItems(stationNames);
        returnStationComboBox.setItems(stationNames);

        if (!stationNames.isEmpty()) {
            stationFilterComboBox.getSelectionModel().selectFirst();
            returnStationComboBox.getSelectionModel().selectFirst();
        }
    }

    /** Refresh the list of available bikes based on selected station **/
    private void refreshAvailableBikesByStation() {
        String selectedStation = stationFilterComboBox.getSelectionModel().getSelectedItem();
        availableBikes.clear();

        if (selectedStation == null) return;

        for (Bicycle bike : Bicycle.getAllBicycles()) {
            if (bike.isAvailable() && selectedStation.equals(bike.getStationName())) {
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

    /** Handle bike reservation **/
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

        selectedBike.setAvailable(false);
        selectedBike.setStatus(BicycleStatus.RESERVED);
        Bicycle.saveAllBicycles();

        new Ride(currentUser, selectedBike);
        Ride.saveAllToFile();  // Save new ride to persist reservation
        currentUser.addHistoryEntry("Reserved Bike", "Bike ID: " + selectedBike.getId());
        User.saveAllUsers();

        messageLabel.setText("Bike reserved successfully: " + selectedBike.getId());
        refreshAvailableBikesByStation();
        refreshReservedBikes();
        updateBalanceLabel();
    }

    /** Handle returning a reserved bike **/
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

        Optional<Ride> rideOpt = Ride.getAllRides().stream()
                .filter(r -> r.getUser().equals(currentUser) &&
                        r.getBicycle().equals(selectedBike) &&
                        r.getEndTime() == null)
                .findFirst();

        if (rideOpt.isEmpty()) {
            messageLabel.setText("No active ride found for this bike.");
            return;
        }

        Ride ride = rideOpt.get();
        ride.endRide();
        Ride.saveAllToFile();  // Save ride changes after return

        selectedBike.setStatus(BicycleStatus.AVAILABLE);
        selectedBike.setAvailable(true);

        // Set return station
        String selectedReturnStation = returnStationComboBox.getSelectionModel().getSelectedItem();
        if (selectedReturnStation != null) {
            selectedBike.setStationName(selectedReturnStation);
            stationFilterComboBox.getSelectionModel().select(selectedReturnStation);
        }

        Bicycle.saveAllBicycles();

        currentUser.addHistoryEntry("Returned Bike", "Bike ID: " + selectedBike.getId() +
                " | Fare: " + ride.getCost() + " units");
        User.saveAllUsers();

        messageLabel.setText("Bike returned to " + selectedReturnStation + ". Fare: " + ride.getCost() + " units");
        refreshAvailableBikesByStation();
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

                currentUser.deductBalance(-amount);
                messageLabel.setText("Balance updated successfully!");
                updateBalanceLabel();
            } catch (NumberFormatException e) {
                messageLabel.setText("Invalid amount entered.");
            }
        });
    }

    /** Logout **/
    @FXML
    private void handleLogout() throws IOException {
        Bicycle.saveAllBicycles();
        Ride.saveAllToFile();
        User.saveAllUsers();
        SessionManager.setLoggedInUser(null);
        HelloApplication.changeScene("login.fxml");
    }

    /** Update balance label **/
    private void updateBalanceLabel() {
        User currentUser = SessionManager.getLoggedInUser();
        if (currentUser != null && balanceLabel != null) {
            balanceLabel.setText("Balance: " + currentUser.getBalance() + " BDT");
        }
    }
}
