public class RentalRecord {
    private String recordId;
    private String userId;
    private String bikeId;
    private String rentStationId;
    private String returnStationId;
    private long rentalStartTime; // Timestamp in milliseconds
    private long rentalEndTime;   // Timestamp in milliseconds
    private double cost;

    public RentalRecord(String recordId, String userId, String bikeId, String rentStationId, long rentalStartTime) {
        this.recordId = recordId;
        this.userId = userId;
        this.bikeId = bikeId;
        this.rentStationId = rentStationId;
        this.rentalStartTime = rentalStartTime;
        this.returnStationId = null; // Set upon return
        this.rentalEndTime = 0;      // Set upon return
        this.cost = 0.0;             // Calculated upon return
    }

    // Getters
    public String getRecordId() {
        return recordId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getRentStationId() {
        return rentStationId;
    }

    public String getReturnStationId() {
        return returnStationId;
    }

    public long getRentalStartTime() {
        return rentalStartTime;
    }

    public long getRentalEndTime() {
        return rentalEndTime;
    }

    public double getCost() {
        return cost;
    }

    // Setters
    public void setReturnStationId(String returnStationId) {
        this.returnStationId = returnStationId;
    }

    public void setRentalEndTime(long rentalEndTime) {
        this.rentalEndTime = rentalEndTime;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        long durationMinutes = (rentalEndTime > rentalStartTime) ? (rentalEndTime - rentalStartTime) / (1000 * 60) : 0;
        return "  Record ID: " + recordId +
                ", Bike ID: " + bikeId +
                ", Rented From: " + rentStationId +
                ", Returned To: " + (returnStationId != null ? returnStationId : "N/A") +
                ", Duration: " + durationMinutes + " mins" +
                ", Cost: $" + String.format("%.2f", cost);
    }
}