package com.example.emirates;

public class selectFlights {

    public static class Flights {
        private String flightNo;
        String departureTime;
        String arrivalTime;
        String departureCity;
        String arrivalCity;
        String duration;
        String stops;
        String aircraftDetails;
        String price;

        public Flights(String flightNo, String departureTime, String arrivalTime,
                       String departureCity, String arrivalCity, String duration, String stops, String aircraftDetails, String price) {
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

        
        public String getFlightNo() {
            return flightNo;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public String getDepartureCity() {
            return departureCity;
        }

        public String getArrivalCity() {
            return arrivalCity;
        }

        public String getDuration() {
            return duration;
        }

        public String getStops() {
            return stops;
        }

        public String getAircraftDetails() {
            return aircraftDetails;
        }

        public String getPrice() {
            return price;
        }

        public String extractDepartureCode() {
            return extractCode(departureCity);
        }

        public String extractArrivalCode() {
            return extractCode(arrivalCity);
        }

        private String extractCode(String combinedCityCode) {
            int startIndex = combinedCityCode.indexOf("(");
            int endIndex = combinedCityCode.indexOf(")");

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                return combinedCityCode.substring(startIndex + 1, endIndex).toUpperCase();
            }
            return "";
        }

        @Override
        public String toString() {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    flightNo.trim(),
                    departureCity.trim(),
                    arrivalCity.trim(),
                    departureTime.trim(),
                    arrivalTime.trim(),
                    duration.trim(),
                    stops.trim(),
                    aircraftDetails.trim(),
                    price.trim());
        }
    }
}
