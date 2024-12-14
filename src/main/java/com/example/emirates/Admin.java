package com.example.emirates;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password, String role) {
        super(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);
    }

    private static final String FLIGHTS_CSV_PATH = "flights.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public static void addFlight(String flightNo, String departureCity, String departureAirportCode,
                                 String arrivalCity, String arrivalAirportCode, String departureTime,
                                 String arrivalTime, String duration, String aircraftDetails, String price) throws IOException {
        String formattedFlightNo = flightNo.toUpperCase();//bey5aly el airport code dayman kolo capital

        String formattedDepartureCity = capitalizeFirstLetter(departureCity) + " (" + departureAirportCode.toUpperCase() + ")";
        String formattedArrivalCity = capitalizeFirstLetter(arrivalCity) + " (" + arrivalAirportCode.toUpperCase() + ")";

        String stops = "Non-stop";//mfeesh transit ehna non stop bas el7

        String shortenedAircraft = shortenAircraftName(aircraftDetails);
        String formattedPrice = formatPrice(price);
        String formattedDepartureTime = formatTime(departureTime);
        String formattedArrivalTime = formatTime(arrivalTime);

        String flightDetails = String.join(",",
                formattedFlightNo,
                formattedDepartureCity,
                formattedArrivalCity,
                formattedDepartureTime,
                formattedArrivalTime,
                duration,
                stops,
                shortenedAircraft,
                formattedPrice
        );

        FileManager.writeFile(FLIGHTS_CSV_PATH, List.of(flightDetails), true);

        System.out.println("Flight added successfully: " + flightDetails);
    }

    //bey5aly fy egp abl el price we by2lbo double
    private static String formatPrice(String price) {
        try {
            double priceValue = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            return "EGP " + df.format(priceValue);
        } catch (NumberFormatException e) {
            System.err.println("Invalid price format: " + price);
            return "EGP 0.00";
        }
    }

    //by3ml format lel time ykoon 12hr we am/pm
    private static String formatTime(String time) {
        try {
            if (time.matches("(0[1-9]|1[0-2]):[0-5]\\d\\s?(AM|PM|am|pm)")) {
                return time.toUpperCase();
            }

            LocalTime parsedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return parsedTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } catch (Exception e) {
            System.err.println("Invalid time format: " + time);
            return "";
        }
    }

    //bey5aly awel harf dayman capital
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    //fel choices maktoob no3 el tayara bel kamel howa by5aleeha 22sr
    private static String shortenAircraftName(String aircraftDetails) {
        return switch (aircraftDetails) {
            case "A380-800" -> "A380";
            case "B777-300ER" -> "B777";
            default -> aircraftDetails;
        };
    }

    public static void deleteFlight(String flightNo) {
        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV(FLIGHTS_CSV_PATH, false, null);

            boolean flightRemoved = flights.removeIf(flight -> flight.getFlightNo().equalsIgnoreCase(flightNo));

            if (flightRemoved) {
                List<String> updatedLines = new ArrayList<>();
                for (selectFlights.Flights flight : flights) {
                    updatedLines.add(flight.toString());
                }

                FileManager.writeFile(FLIGHTS_CSV_PATH, updatedLines, false);
                System.out.println("Flight with number " + flightNo + " has been deleted.");
            } else {
                System.out.println("Flight with number " + flightNo + " not found.");
            }

        } catch (IOException e) {
            System.err.println("Error while processing the file: " + e.getMessage());
        }
    }

    // Update Flight
    public static void updateFlight(String flightNo) {
        Scanner scanner = new Scanner(System.in);

        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV(FLIGHTS_CSV_PATH, false, null);
            boolean flightFound = false;

            for (selectFlights.Flights flight : flights) {
                if (flight.getFlightNo().equalsIgnoreCase(flightNo)) {
                    flightFound = true;

                    System.out.println("Updating flight: " + flightNo);
                    System.out.print("Enter new departure city (" + flight.getDepartureCity() + "): ");
                    flight.departureCity = scanner.nextLine();
                    System.out.print("Enter new arrival city (" + flight.getArrivalCity() + "): ");
                    flight.arrivalCity = scanner.nextLine();
                    System.out.print("Enter new departure time (" + flight.getDepartureTime() + "): ");
                    flight.departureTime = scanner.nextLine();
                    System.out.print("Enter new arrival time (" + flight.getArrivalTime() + "): ");
                    flight.arrivalTime = scanner.nextLine();
                    System.out.print("Enter new duration (" + flight.getDuration() + "): ");
                    flight.duration = scanner.nextLine();
                    System.out.print("Enter new stops (" + flight.getStops() + "): ");
                    flight.stops = scanner.nextLine();
                    System.out.print("Enter new aircraft details (" + flight.getAircraftDetails() + "): ");
                    flight.aircraftDetails = scanner.nextLine();
                    System.out.print("Enter new price (" + flight.getPrice() + "): ");
                    flight.price = scanner.nextLine();
                    break;
                }
            }

            if (flightFound) {
                List<String> updatedLines = new ArrayList<>();
                for (selectFlights.Flights flight : flights) {
                    updatedLines.add(flight.toString());
                }

                FileManager.writeFile(FLIGHTS_CSV_PATH, updatedLines, false);
                System.out.println("Flight with number " + flightNo + " has been updated.");
            } else {
                System.out.println("Flight with number " + flightNo + " not found.");
            }

        } catch (IOException e) {
            System.err.println("Error while processing the file: " + e.getMessage());
        }
    }

}
