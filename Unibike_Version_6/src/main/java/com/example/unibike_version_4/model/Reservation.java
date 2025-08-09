package com.example.unibike_version_4.model;

import java.time.LocalDateTime;

public class Reservation {
    private String id;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final User user;
    private final Bicycle bicycle;
    private final Station station;
    private ReservationStatus status;

    public Reservation(LocalDateTime startTime, User user, Bicycle bicycle, Station station) {
        this.startTime = startTime;
        this.user = user;
        this.bicycle = bicycle;
        this.station = station;
        this.status = ReservationStatus.ACTIVE;

        // Update bicycle availability
        bicycle.setAvailable(false);
    }

    // Getters
    public Bicycle getBicycle() { return bicycle; }
    public LocalDateTime getTime() { return startTime; }
    public Station getStation() { return station; }
    public User getUser() { return user; }
    public ReservationStatus getStatus() { return status; }

    public void cancel() {
        if (status == ReservationStatus.ACTIVE) {
            status = ReservationStatus.CANCELLED;
            bicycle.setAvailable(true);

            if (user != null) {
                user.addHistoryEntry("Reservation Cancelled",
                        "Bike " + bicycle.getId() + " at " + station.getName());
            }
        }
    }

    public void complete() {
        if (status == ReservationStatus.ACTIVE) {
            status = ReservationStatus.COMPLETED;
            bicycle.setAvailable(false);

            if (user != null) {
                user.addHistoryEntry("Reservation Completed",
                        "Bike " + bicycle.getId() + " picked up");
            }
        }
    }

    @Override
    public String toString() {
        return "Reservation #" + (id != null ? id : "N/A") +
                " - Bike " + bicycle.getId() +
                " at " + station.getName();
    }
}

enum ReservationStatus {
    ACTIVE, CANCELLED, COMPLETED
}
