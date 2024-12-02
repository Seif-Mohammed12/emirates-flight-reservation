package com.example.emirates;

public class AppContext {

    private static String loggedInUsername;
    private static String loggedInPassword;
    private static String loggedInFirstName;

    private static String selectedDestination;
    private static String selectedDeparture;

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    // Getter and setter for loggedInFirstName
    public static String getLoggedInFirstName() {
        return loggedInFirstName;
    }

    public static void setLoggedInFirstName(String firstName) {
        loggedInFirstName = firstName;
    }

    public static String getSelectedDestination() {
        return selectedDestination;
    }
    public static String getSelectedDeparture() {
        return selectedDeparture;
    }

    public static void setSelectedDestination(String destination) {
        selectedDestination = destination;
    }
    public static void setSelectedDeparture(String departure) {
        selectedDeparture = departure;
    }

    public static String getLoggedInPassword() {
        return loggedInPassword;
    }

    public static void setLoggedInPassword(String loggedInPassword) {
        AppContext.loggedInPassword = loggedInPassword;


    }
}
