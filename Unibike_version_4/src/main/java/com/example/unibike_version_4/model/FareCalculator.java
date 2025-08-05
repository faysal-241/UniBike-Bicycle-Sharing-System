package com.example.unibike_version_4.model;

public class FareCalculator {
    private static final double BASE_RATE = 1.00;
    private static final double PER_MINUTE_RATE = 0.15;

    public static double calculateFare(long minutes) {
        return BASE_RATE + (minutes * PER_MINUTE_RATE);
    }
}
