package com.example.emirates;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password, String role) {
        super(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);
    }

    public static void addFlight(String flightNo, String departureCity, String arrivalCity, String departureTime,
                                 String arrivalTime, String duration, String stops, String aircraftDetails, String price) throws IOException {
        // Create a new Flights object
        selectFlights.Flights newFlight = new selectFlights.Flights(
                flightNo, departureTime, arrivalTime, departureCity, arrivalCity,
                duration, stops, aircraftDetails, price
        );

        // Append the new flight to the CSV file
        String filePath = "flights.csv";

        List<String> newLine = List.of(newFlight.toString());
        FileManager.writeFile(filePath, newLine, true);
        System.out.println("Flight added successfully.");
    }


    public static void deleteFlight(String flightNo) {
        String filePath = "flights.csv";

        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV(filePath, false, null);

            boolean flightRemoved = flights.removeIf(flight -> flight.getFlightNo().toLowerCase().equals(flightNo));

            if (flightRemoved) {
                List<String> updatedLines = new ArrayList<>();
                for (selectFlights.Flights flight : flights) {
                    updatedLines.add(flight.toString());
                }
                FileManager.writeFile(filePath, updatedLines, false);
                System.out.println("Flight with number " + flightNo + " has been deleted.");
            } else {
                System.out.println("Flight with number " + flightNo + " not found.");
            }

        } catch (IOException e) {
            System.err.println("Error while processing the file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void addNewUser(String username, String password, String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, List<User> users) {
        if (users.stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(username))) {
            System.out.println("User with this username already exists.");
            return;
        }

        User newUser = new User(firstName, lastName, email, phoneNumber, zipCode, address, username, password, "user");

        try {
            users.add(newUser);
            List<String> newUserData = List.of(newUser.toString());
            FileManager.writeFile("login.txt", newUserData, true);
            System.out.println("User added successfully to the file.");
        } catch (IOException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }


    // Validation helper
    private static boolean isValidUserData(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password) {
        return firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty() &&
                email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[a-zA-Z]{2,4}$") &&
                phoneNumber != null && phoneNumber.matches("^\\+20\\d{10}$") &&
                zipCode != null && !zipCode.isEmpty() &&
                address != null && !address.isEmpty() &&
                username != null && username.matches("^[a-zA-Z0-9_]+$") &&
                password != null && password.length() >= 8;
    }


    // Delete a user
    public void deleteUser(String username, List<User> users) {
        User userToDelete = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToDelete = user;
                break;
            }
        }

        if (userToDelete != null) {
            users.remove(userToDelete);
            saveUsersToFile(users);
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    // Save the updated users to a file
    private static void saveUsersToFile(List<User> users) {
        try {
            List<User> validUsers = users.stream()
                    .filter(user -> isValidUserData(user.getFirstName(), user.getLastName(), user.getEmail(),
                            user.getPhoneNumber(), user.getZipCode(), user.getAddress(), user.getUsername(), user.getPassword()))
                    .distinct() // Ensure no duplicates
                    .toList();

            List<String> userLines = validUsers.stream()
                    .map(User::toString)
                    .toList();

            // Write to file
            FileManager.writeFile("login.txt", userLines, true);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}