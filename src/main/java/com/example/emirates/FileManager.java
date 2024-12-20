package com.example.emirates;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // =================================================================================
    // File Writing Operations
    // =================================================================================
    public static void writeFile(String filePath, List<String> lines, boolean append) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, append))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // =================================================================================
    // Flight Data Operations
    // =================================================================================
    public static List<selectFlights.Flights> loadFlightsFromCSV(String filePath, boolean rawData,
            List<String> rawLines) throws IOException {
        List<selectFlights.Flights> flights = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        boolean isFirstLine = true;

        for (String line : lines) {
            line = line.trim();
            if (isFirstLine || line.isEmpty()) {
                isFirstLine = false;
                continue;
            }

            if (rawData && rawLines != null) {
                rawLines.add(line);
            }

            if (line.isEmpty()) {
                continue;
            }

            String[] fields = line.split(",");
            if (fields.length == 9) {
                try {
                    String flightNo = fields[0].trim();
                    String departureCity = fields[1].trim();
                    String arrivalCity = fields[2].trim();
                    String departureTime = fields[3].trim();
                    String arrivalTime = fields[4].trim();
                    String duration = fields[5].trim();
                    String stops = fields[6].trim();
                    String aircraftDetails = fields[7].trim();
                    String price = fields[8].trim();

                    flights.add(new selectFlights.Flights(flightNo, departureTime, arrivalTime,
                            departureCity, arrivalCity, duration, stops, aircraftDetails, price));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid price format in line: " + line);
                }
            } else {
                System.err.println("Invalid line format: " + line);
            }
        }
        return flights;
    }

    // =================================================================================
    // User Data Operations
    // =================================================================================
    public static List<User> loadUsersFromFile(String filename) {
        List<User> usersList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String username = parts[6].trim();
                    String password = parts[7].trim();
                    String firstName = parts[0].trim();
                    String email = parts[2].trim();
                    String lastName = parts[1].trim();
                    usersList.add(new User(username, password, firstName, email, lastName));
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return usersList;
    }
}
