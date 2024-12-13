package com.example.emirates;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlightSearch {
    private List<selectFlights.Flights> flights;

    public FlightSearch(List<selectFlights.Flights> flights) {
        this.flights = flights;
    }

    public List<selectFlights.Flights> getFlights() {
        return flights;
    }

    public selectFlights.Flights getFlightByFlightNumber(String flightNumber) {
        for (selectFlights.Flights flight : flights) {
            if (flight.getFlightNo().equalsIgnoreCase(flightNumber)) {
                return flight;
            }
        }
        return null;
    }

    public List<selectFlights.Flights> searchByRoute(String departureCity, String arrivalCity) {
        departureCity = departureCity.trim().toLowerCase();
        arrivalCity = arrivalCity.trim().toLowerCase();

        List<selectFlights.Flights> matchingFlights = new ArrayList<>();

        for (selectFlights.Flights flight : flights) {
            String flightDeparture = flight.getDepartureCity().trim().toLowerCase();
            String flightArrival = flight.getArrivalCity().trim().toLowerCase();

            if (flightDeparture.contains(departureCity) && flightArrival.contains(arrivalCity)) {
                matchingFlights.add(flight);
            }
        }
        return matchingFlights;
    }

    public void searchByFlightNumberInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the flight number: ");
        String flightNumber = scanner.nextLine();

        selectFlights.Flights result = getFlightByFlightNumber(flightNumber);
        if (result == null) {
            System.out.println("Flight " + flightNumber + " not found.");
        } else {
            System.out.println("Flight " + flightNumber + " found:");
            System.out.println("Departure City: " + result.getDepartureCity());
            System.out.println("Arrival City: " + result.getArrivalCity());
            System.out.println("Departure Time: " + result.getDepartureTime());
            System.out.println("Arrival Time: " + result.getArrivalTime());
            System.out.println("Price: " + result.getPrice());
        }
    }

    public void searchByRouteInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the flight departure: ");
        String departure = scanner.nextLine();
        System.out.print("Enter the flight destination: ");
        String destination = scanner.nextLine();

        List<selectFlights.Flights> results = searchByRoute(departure, destination);
        if (results.isEmpty()) {
            System.out.println("No flights found from " + departure + " to " + destination + ".");
        } else {
            System.out.println("Flights from " + departure + " to " + destination + ":");
            for (selectFlights.Flights flight : results) {
                System.out.println("Flight number: " + flight.getFlightNo());
                System.out.println("Departure Time: " + flight.getDepartureTime());
                System.out.println("Arrival Time: " + flight.getArrivalTime());
                System.out.println("Price: " + flight.getPrice());
            }
        }
    }

}
