package com.example.emirates;

import java.util.*;

public class AppContext {

    private static String loggedInUsername;
    private static String loggedInPassword;
    private static String loggedInFirstName;
    private static String loggedInLastName;
    private static String loggedInEmail;

    private static String selectedDestination;
    private static String selectedDeparture;

    private static final List<BookingConfirmation.Booking> bookings = new ArrayList<>();

    private static Map<String, Set<String>> bookedSeatsMap = new HashMap<>();

    public static Map<String, Set<String>> getBookedSeatsMap() {
        return bookedSeatsMap;
    }

    public static void setBookedSeatsMap(Map<String, Set<String>> map) {
        bookedSeatsMap = map;
    }

    public static Set<String> getBookedSeats(String classType) {
        return bookedSeatsMap.getOrDefault(classType, new HashSet<>());
    }

    public static void setBookedSeats(String classType, Set<String> seats) {
        bookedSeatsMap.put(classType, seats);
    }

    public static void addBooking(BookingConfirmation.Booking booking) {
        bookings.add(booking);
    }

    public static List<BookingConfirmation.Booking> getBookings() {
        return new ArrayList<>(bookings); // Return a copy to prevent modification
    }

    public static boolean hasBookings() {
        return !bookings.isEmpty();
    }

    public static void clearBookings() {
        bookings.clear();
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }
    public static String getLoggedInEmail() {
        return loggedInEmail;
    }

    public static void setLoggedInEmail(String email) {
        loggedInEmail = email;
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

    public static String getLoggedInLastName() {
        return loggedInLastName;
    }

    public static void setLoggedInLastName(String loggedInLastName) {
        AppContext.loggedInLastName = loggedInLastName;
    }
}
