public class Bike {
    private String bikeId;
    private String currentStationId; // ID of the station where the bike is currently located
    private BikeStatus status;
    private String reservedByUserId; // ID of the user who reserved this bike
    private long reservationEndTime; // Timestamp when the reservation expires (in milliseconds)

    public enum BikeStatus {
        AVAILABLE,
        RENTED,
        IN_MAINTENANCE,
        RESERVED // New status for pre-booking
    }

    public Bike(String bikeId, String currentStationId) {
        this.bikeId = bikeId;
        this.currentStationId = currentStationId;
        this.status = BikeStatus.AVAILABLE; // Bikes are available by default when created
        this.reservedByUserId = null;
        this.reservationEndTime = 0;
    }

    // Getters
    public String getBikeId() {
        return bikeId;
    }

    public String getCurrentStationId() {
        return currentStationId;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public String getReservedByUserId() {
        return reservedByUserId;
    }

    public long getReservationEndTime() {
        return reservationEndTime;
    }

    // Setters
    public void setCurrentStationId(String currentStationId) {
        this.currentStationId = currentStationId;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public void setReservedByUserId(String reservedByUserId) {
        this.reservedByUserId = reservedByUserId;
    }

    public void setReservationEndTime(long reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    @Override
    public String toString() {
        String statusDetail = "";
        if (status == BikeStatus.RESERVED && reservedByUserId != null) {
            statusDetail = " (Reserved by " + reservedByUserId + ")";
        }
        return "Bike ID: " + bikeId + ", Status: " + status + statusDetail + ", Current Station: " + (currentStationId != null ? currentStationId : "N/A");
    }
}