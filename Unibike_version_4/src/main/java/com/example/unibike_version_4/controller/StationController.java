package com.example.unibike_version_4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.example.unibike_version_4.model.*;
import java.util.List;

public class StationController {

    @FXML private ListView<Station> stationsListView;
    @FXML private Label stationNameLabel;
    @FXML private Label capacityLabel;
    @FXML private Label availableBikesLabel;
    @FXML private VBox stationDetailsBox;

    @FXML
    public void initialize() {
        // Load all stations
        List<Station> stations = Station.getAllStations();
        stationsListView.getItems().addAll(stations);

        stationsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showStationDetails(newVal)
        );

        stationDetailsBox.setVisible(false);
    }

    private void showStationDetails(Station station) {
        if (station != null) {
            stationNameLabel.setText(station.getName());
            availableBikesLabel.setText("Available Bikes: " + station.getAvailableBicycles().size());
            stationDetailsBox.setVisible(true);
        } else {
            stationDetailsBox.setVisible(false);
        }
    }

    @FXML
    private void handleRefresh() {
        stationsListView.getItems().clear();
        stationsListView.getItems().addAll(Station.getAllStations());
    }
}