package com.example.emirates;

public class selectFlights {

    static class Flights{
        String flightNo;
        String departureTime;
        String arrivalTime;
        String departureCity;
        String arrivalCity;
        String duration;
        String stops;
        String aircraftDetails;
        String price;

        public Flights(String flightNo, String departureTime, String arrivalTime,
                       String departureCity, String arrivalCity, String duration, String stops, String aircraftDetails, String price){
            this.flightNo = flightNo;
            this.departureTime = departureTime;
            this.departureCity = departureCity;
            this.arrivalTime = arrivalTime;
            this.arrivalCity = arrivalCity;
            this.duration = duration;
            this.stops = stops;
            this.aircraftDetails = aircraftDetails;
            this.price = price;
        }
    }
}
