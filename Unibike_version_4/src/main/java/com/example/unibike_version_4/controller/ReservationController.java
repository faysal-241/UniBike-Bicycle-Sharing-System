package com.example.unibike_version_4.controller;

import com.example.unibike_version_4.model.Bicycle;
import com.example.unibike_version_4.model.Station;
import com.example.unibike_version_4.util.BicycleManager;
import com.example.unibike_version_4.util.ReservationManager;
import com.example.unibike_version_4.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ReservationController {

    @FXML private ComboBox<Station> stationComboBox;
    @FXML private ComboBox<Bicycle> bicycleComboBox;
    @FXML private Button reserveButton;
    @FXML private Button cancelButton;
    @FXML private ListView<String> activeReservationsList;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        refreshStationsAndBicycles();
        loadUserReservations();

        // Update bicycles when a station is selected
        stationComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> refreshBicyclesForStation(newVal)
        );
    }

    /** Refresh both stations and bicycles from file */
    private void refreshStationsAndBicycles() {
        stationComboBox.getItems().clear();
        stationComboBox.getItems().addAll(Station.getAllStations());

        // Select the first station by default if exists
        if (!stationComboBox.getItems().isEmpty()) {
            stationComboBox.getSelectionModel().selectFirst();
            refreshBicyclesForStation(stationComboBox.getValue());
        } else {
            bicycleComboBox.getItems().clear();
        }
    }

    /** Refresh available bicycles for the selected station */
    private void refreshBicyclesForStation(Station station) {
        bicycleComboBox.getItems().clear();
        if (station != null) {
            bicycleComboBox.getItems().addAll(station.getAvailableBicycles());
        }
    }

    /** Load current user's reservations */
    private void loadUserReservations() {
        activeReservationsList.getItems().clear();
        String username = SessionManager.getLoggedInUsername();
        if (username != null) {
            activeReservationsList.getItems().addAll(
                    ReservationManager.getUserReservations(username)
            );
        }
    }

    /** Handle Reserve Button */
    @FXML
    private void handleReserve() {
        Bicycle selectedBike = bicycleComboBox.getValue();
        Station selectedStation = stationComboBox.getValue();
        String username = SessionManager.getLoggedInUsername();


        if (selectedBike == null || selectedStation == null) {
            messageLabel.setText("Select a station and a bicycle first.");
            return;
        }
        if (username == null) {
            messageLabel.setText("No user logged in.");
            return;
        }

        // 1️⃣ Add reservation
        ReservationManager.addReservation(username, selectedBike.getId(), LocalDate.now().toString());

        // 2️⃣ Mark bike as unavailable
        BicycleManager.updateBicycleAvailability(selectedBike.getId(), false);

        // 3️⃣ Update UI
        messageLabel.setText("Bike " + selectedBike.getId() + " reserved successfully!");
        refreshStationsAndBicycles();
        loadUserReservations();
    }

    /** Handle Cancel Button */
    @FXML
    private void handleCancel() {
        String selected = activeReservationsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a reservation to cancel.");
            return;
        }

        String username = SessionManager.getLoggedInUsername();
        if (username == null) return;

        // Parse reservation string (format: B001 on YYYY-MM-DD)
        String bikeId = selected.split(" ")[0];

        // 1️⃣ Remove reservation
        ReservationManager.removeReservation(username, bikeId);

        // 2️⃣ Mark bike as available again
        BicycleManager.updateBicycleAvailability(bikeId, true);

        // 3️⃣ Update UI
        messageLabel.setText("Reservation canceled for bike " + bikeId);
        refreshStationsAndBicycles();
        loadUserReservations();
    }
}
