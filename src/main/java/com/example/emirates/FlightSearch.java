package com.example.emirates;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlightSearch {
    private List<selectFlights.Flights> flights = new ArrayList<>();

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
            System.out.println("Duration: " + result.getDuration());
            System.out.println("Stops: " + result.getStops());
            System.out.println("Aircraft Details: " + result.getAircraftDetails());
            System.out.println("Price: " + result.getPrice());
        }
    }
}
