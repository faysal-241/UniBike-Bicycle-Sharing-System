// Station.java
// Represents a bicycle station in the Unibike system.

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Station {
    private String stationId;
    private String name;
    private String location;
    private List<Bike> bikes; // List of bikes currently at this station

    public Station(String stationId, String name, String location) {
        this.stationId = stationId;
        this.name = name;
        this.location = location;
        this.bikes = new ArrayList<>(); // Initialize with an empty list of bikes
    }

    // Getters
    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<Bike> getBikes() {
        return bikes;
    }

    // Setters for editing station details (for admin panel)
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Methods to manage bikes at the station
    public void addBike(Bike bike) {
        if (bike != null) {
            this.bikes.add(bike);
            bike.setCurrentStationId(this.stationId); // Update bike's current station
            System.out.println("Bike " + bike.getBikeId() + " added to station " + name);
        }
    }

    public Bike removeBike(String bikeId) {
        Bike bikeToRemove = null;
        for (Bike bike : bikes) {
            if (bike.getBikeId().equals(bikeId)) {
                bikeToRemove = bike;
                break;
            }
        }
        if (bikeToRemove != null) {
            this.bikes.remove(bikeToRemove);
            bikeToRemove.setCurrentStationId(null); // Bike is no longer at this station (conceptually, it's removed from this station's inventory)
            System.out.println("Bike " + bikeId + " removed from station " + name);
            return bikeToRemove;
        } else {
            System.out.println("Bike " + bikeId + " not found at station " + name);
            return null;
        }
    }

    public List<Bike> getAvailableBikes() {
        return bikes.stream()
                .filter(bike -> bike.getStatus() == Bike.BikeStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Station ID: " + stationId + ", Name: " + name + ", Location: " + location +
                ", Bikes at Station: " + bikes.size();
    }
}