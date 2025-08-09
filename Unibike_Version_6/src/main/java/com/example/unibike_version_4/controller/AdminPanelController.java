package com.example.unibike_version_4.controller;

import com.example.unibike_version_4.model.Bicycle;
import com.example.unibike_version_4.model.Station;
import com.example.unibike_version_4.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPanelController {

    // --- Bicycle Table ---
    @FXML private TableView<Bicycle> bicycleTable;
    @FXML private TableColumn<Bicycle, String> idColumn;
    @FXML private TableColumn<Bicycle, String> stationColumn;
    @FXML private TableColumn<Bicycle, Boolean> availableColumn;

    // --- User Table ---
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Number> balanceColumn;

    // --- Form Inputs ---
    @FXML private TextField bicycleIdField;
    @FXML private CheckBox availableCheck;
    @FXML private ComboBox<String> stationComboBox;

    @FXML private TextField stationNameField;
    @FXML private TextField stationCapacityField;

    @FXML private Label messageLabel;

    // --- Data Lists ---
    private final ObservableList<Bicycle> bicycleList = FXCollections.observableArrayList();
    private final ObservableList<String> stationList = FXCollections.observableArrayList();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load all data from files
        User.loadFromFile();
        Bicycle.loadFromFile();
        Station.loadFromFile();

        // Setup Bicycle Table
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        stationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStationName()));
        availableColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isAvailable()));

        bicycleTable.setItems(bicycleList);

        // Setup User Table (plain getters because we removed JavaFX properties)
        userIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        balanceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getBalance()));

        userTable.setItems(userList);

        // Load initial data
        loadUsers();
        loadBicycles();
        loadStations();
    }

    /** Load All Users */
    private void loadUsers() {
        userList.clear();
        userList.addAll(User.getAllUsers());
    }

    /** Load All Bicycles */
    private void loadBicycles() {
        bicycleList.clear();
        bicycleList.addAll(Bicycle.getAllBicycles());
    }

    /** Load All Stations */
    private void loadStations() {
        stationList.clear();
        for (Station station : Station.getAllStations()) {
            stationList.add(station.getName());
        }
        stationComboBox.setItems(stationList);
    }

    /** Add Station */
    @FXML
    private void handleAddStation() {
        String name = stationNameField.getText().trim();
        int capacity = 10; // default

        if (!stationCapacityField.getText().isEmpty()) {
            try {
                capacity = Integer.parseInt(stationCapacityField.getText().trim());
            } catch (NumberFormatException e) {
                messageLabel.setText("Capacity must be a number!");
                return;
            }
        }

        if (name.isEmpty()) {
            messageLabel.setText("Station name cannot be empty!");
            return;
        }
        for(Station station: Station.getAllStations()){
            if(station.getName().equalsIgnoreCase(name)){
                messageLabel.setText("Station name already exists! Please choose a different name.");
                return;
            }
        }
        new Station(name, capacity);
        Station.saveToFile();

        loadStations();
        messageLabel.setText("Station added: " + name);
        stationNameField.clear();
        stationCapacityField.clear();
    }

    /** Add Bicycle */
    @FXML
    private void handleAddBicycle() {
        String bikeId = bicycleIdField.getText().trim();
        String stationName = stationComboBox.getValue();

        if (bikeId.isEmpty() || stationName == null) {
            messageLabel.setText("Please enter a bicycle ID and select a station.");
            return;
        }
        for(Bicycle bike: Bicycle.getAllBicycles()){
            if(bike.getId().equals(bikeId)){
                messageLabel.setText("Bicycle ID already exists! Please choose a different ID.");
                return;
            }
        }
        boolean available = availableCheck.isSelected();
        new Bicycle(bikeId, stationName, available);
        Bicycle.saveAllBicycles();

        loadBicycles();
        messageLabel.setText("Bicycle added: " + bikeId);

        bicycleIdField.clear();
        availableCheck.setSelected(false);
    }

    /** Mark selected as Available */
    @FXML
    private void handleMarkAvailable() {
        Bicycle selected = bicycleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAvailable(true);
            Bicycle.saveAllBicycles();
            messageLabel.setText("Bicycle " + selected.getId() + " marked as available.");
            loadBicycles();
        }
    }

    /** Mark selected as Unavailable */
    @FXML
    private void handleMarkUnavailable() {
        Bicycle selected = bicycleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAvailable(false);
            Bicycle.saveAllBicycles();
            messageLabel.setText("Bicycle " + selected.getId() + " marked as unavailable.");
            loadBicycles();
        }
    }

    /** Delete selected Bicycle */
    @FXML
    private void handleDeleteBicycle() {
        Bicycle selected = bicycleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Bicycle.getAllBicycles().remove(selected);
            Bicycle.saveAllBicycles();
            loadBicycles();
            messageLabel.setText("Bicycle " + selected.getId() + " deleted.");
        } else {
            messageLabel.setText("Select a bicycle to delete.");
        }
    }

    /** Logout and go back to login screen */
    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unibike_version_4/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
