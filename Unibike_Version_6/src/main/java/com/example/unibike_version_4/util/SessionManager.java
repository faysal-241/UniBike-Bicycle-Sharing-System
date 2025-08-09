package com.example.unibike_version_4.util;

import com.example.unibike_version_4.model.User;

public class SessionManager {
    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // ✅ For controllers expecting username
    public static String getLoggedInUsername() {
        return (loggedInUser != null) ? loggedInUser.getId() : null;
    }
}

