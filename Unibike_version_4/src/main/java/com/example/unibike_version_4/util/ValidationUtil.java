package com.example.unibike_version_4.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );

    // Password requirements: 8+ chars, at least one digit, one lowercase, one uppercase
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    );

    // Name validation: only letters, spaces, and hyphens
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[\\p{L} .'-]+$", Pattern.UNICODE_CHARACTER_CLASS
    );

    // Numeric ID validation
    private static final Pattern NUMERIC_ID_PATTERN = Pattern.compile(
            "^\\d{6,10}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidNumericId(String id) {
        return id != null && NUMERIC_ID_PATTERN.matcher(id).matches();
    }

    public static boolean isValidStationCapacity(int capacity) {
        return capacity > 0 && capacity <= 50;
    }

    public static boolean isValidBicycleId(String id) {
        return id != null && id.matches("^B\\d{3}$");
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean validateLoginCredentials(String email, String password) {
        return isValidEmail(email) && isValidPassword(password);
    }

    public static boolean validateRegistration(String name, String email, String password, String id) {
        return isValidName(name) &&
                isValidEmail(email) &&
                isValidPassword(password) &&
                isValidNumericId(id);
    }

    public static boolean validateStationInput(String name, String location, int capacity) {
        return isValidName(name) &&
                isValidName(location) &&
                isValidStationCapacity(capacity);
    }
}