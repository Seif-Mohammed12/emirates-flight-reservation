package com.example.emirates;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password, String role) {
        super(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);
    }

    private static final String FLIGHTS_CSV_PATH = "flights.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public static void addFlight(String flightNo, String departureCity, String departureAirportCode,
                                 String arrivalCity, String arrivalAirportCode, String departureTime,
                                 String arrivalTime, String duration, String aircraftDetails, String price) throws IOException {
        String formattedFlightNo = flightNo.toUpperCase();
        String formattedDepartureCity = formatCityAndCode(departureCity, departureAirportCode);
        String formattedArrivalCity = formatCityAndCode(arrivalCity, arrivalAirportCode);
        String stops = "Non-stop";
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

    // Update flight
    public static void updateFlight(String flightNo, String departureCity, String departureAirportCode,
                                    String arrivalCity, String arrivalAirportCode, String departureTime,
                                    String arrivalTime, String duration, String aircraftDetails, String price) throws IOException {
        List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV(FLIGHTS_CSV_PATH, false, null);
        boolean flightFound = false;

        for (int i = 0; i < flights.size(); i++) {
            selectFlights.Flights flight = flights.get(i);
            if (flight.getFlightNo().equalsIgnoreCase(flightNo)) {
                flightFound = true;

                String combinedDepartureCity = formatCityAndCode(departureCity, departureAirportCode);
                String combinedArrivalCity = formatCityAndCode(arrivalCity, arrivalAirportCode);

                flights.set(i, new selectFlights.Flights(
                        flightNo,
                        formatTime(departureTime),
                        formatTime(arrivalTime),
                        combinedDepartureCity,
                        combinedArrivalCity,
                        duration,
                        "Non-stop",
                        shortenAircraftName(aircraftDetails),
                        formatPrice(price)
                ));
                break;
            }
        }

        if (flightFound) {
            saveFlightsToCSV(flights);
            System.out.println("Flight with number " + flightNo + " has been updated.");
        } else {
            System.out.println("Flight with number " + flightNo + " not found.");
        }
    }

    // Delete flight
    public static void deleteFlight(String flightNo) throws IOException {
        List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV(FLIGHTS_CSV_PATH, false, null);
        boolean flightRemoved = flights.removeIf(flight -> flight.getFlightNo().equalsIgnoreCase(flightNo));

        if (flightRemoved) {
            saveFlightsToCSV(flights);
            System.out.println("Flight with number " + flightNo + " has been deleted.");
        } else {
            System.out.println("Flight with number " + flightNo + " not found.");
        }
    }

    private static void saveFlightsToCSV(List<selectFlights.Flights> flights) throws IOException {
        List<String> updatedLines = new ArrayList<>();
        for (selectFlights.Flights flight : flights) {
            updatedLines.add(flight.toString());
        }
        FileManager.writeFile(FLIGHTS_CSV_PATH, updatedLines, false);
    }


    //bey5aly el price double we beysheel el egp el fel awl 3lshan yet2ry s7
    static String formatPrice(String price) {
        try {
            if (price.startsWith("EGP ")) {
                price = price.substring(4).trim();
            }
            double priceValue = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            return "EGP " + df.format(priceValue);
        } catch (NumberFormatException e) {
            System.err.println("Invalid price format: " + price);
            return "EGP 0.00";
        }
    }


    //bey3ml format lel time eno yb2a 12hr we am/pm we kda
    static String formatTime(String time) {
        try {
            if (time.matches("(0[1-9]|1[0-2]):[0-5]\\d\\s?(AM|PM|am|pm)")) {
                return time.toUpperCase();
            }
            LocalTime parsedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return parsedTime.format(TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("Invalid time format: " + time);
            return "";
        }
    }


    //beyfsl el city 3an el code bta3ha ex: dubai ... (dxb)
    private static String formatCityAndCode(String city, String code) {
        if (city.matches(".*\\(.*\\).*")) {
            return city;
        }
        return capitalizeFirstLetter(city) + " (" + code.toUpperCase() + ")";
    }

    //self explanatory
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }


    //brdo self explanatory
    private static String shortenAircraftName(String aircraftDetails) {
        return switch (aircraftDetails) {
            case "A380-800" -> "A380";
            case "B777-300ER" -> "B777";
            default -> aircraftDetails;
        };
    }

    //by3ml combine lel city m3 el code bta3ha on save bel index (city heya 0 hence fel awl wel code 1 etc)
    public static String parseCity(String combined) {
        int startIdx = combined.indexOf('(');
        if (startIdx != -1) {
            return combined.substring(0, startIdx).trim();
        }
        return combined.trim();
    }

    public static String parseCode(String combined) {
        int startIdx = combined.indexOf('(');
        int endIdx = combined.indexOf(')');
        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            return combined.substring(startIdx + 1, endIdx).trim();
        }
        return "";
    }

    //beyt2kd en el egp etshalet
    static String stripCurrencySymbol(String price) {
        if (price.startsWith("EGP ")) {
            return price.substring(4).trim();
        }
        return price.trim();
    }
}
