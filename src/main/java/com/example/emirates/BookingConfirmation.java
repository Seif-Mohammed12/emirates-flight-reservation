package com.example.emirates;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingConfirmation {

    public static class Passenger {
        private String name;
        private String seat;
        private String contactMethod;
        private String selectedClass;

        public Passenger(String name, String seat, String contactMethod, String selectedClass) {
            this.name = name;
            this.seat = seat;
            this.contactMethod = contactMethod;
            this.selectedClass = selectedClass;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSeat() { return seat; }
        public void setSeat(String seat) { this.seat = seat; }

        public String getContactMethod() { return contactMethod; }
        public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }

        public String getFlightClass() { return selectedClass; }
        public void setFlightClass(String selectedClass) { this.selectedClass = selectedClass; }

        public void displayPassengerData() {
            System.out.println("Passenger Data:");
            System.out.println("Name: " + name);
            System.out.println("Seat: " + seat);
            System.out.println("Contact: " + contactMethod);
            System.out.println("Class: " + selectedClass);
        }
    }

    public static class Booking {
        final selectFlights.Flights flight;
        final Passenger passenger;
        private final String bookingId;
        private String status;
        private double totalPrice;
        private double updatedPrice;
        private LocalDate departureDate, returnDate;

        public Booking(selectFlights.Flights flight, Passenger passenger, LocalDate departureDate, LocalDate returnDate, double updatedPrice) {
            this.flight = flight;
            this.passenger = passenger;
            this.bookingId = generateBookingId();
            this.status = "Confirmed";
            this.totalPrice = parsePrice(flight.getPrice());
            this.departureDate = departureDate;
            this.returnDate = returnDate;
            this.updatedPrice = updatedPrice;
        }

        public selectFlights.Flights getFlight() {
            return flight;
        }

        public Passenger getPassenger() {
            return passenger;
        }

        public LocalDate getDepartureDate() {
            return departureDate;
        }

        public LocalDate getReturnDate() {
            return returnDate;
        }
        public double getUpdatedPrice() {
            return updatedPrice;
        }

        public void setUpdatedPrice(double updatedPrice) {
            this.updatedPrice = updatedPrice;
        }

        private double parsePrice(String price) {
            try {
                return Double.parseDouble(price.replace("EGP", "").replace(",", "").trim());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public static String generateBookingId() {
            String airlineCode = "EK"; // Emirates code
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            String uniquePart = String.valueOf(System.currentTimeMillis()).substring(5);

            return airlineCode + "-" + datePart + "-" + uniquePart;
        }

        public void displayBookingData() {
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Status: " + status);
            displayFlightData();
            passenger.displayPassengerData();
            System.out.println("Total Price: " + totalPrice);
        }

        private void displayFlightData() {
            System.out.println("Flight Data:");
            System.out.println("Flight Number: " + flight.getFlightNo());
            System.out.println("Departure City: " + flight.getDepartureCity());
            System.out.println("Arrival City: " + flight.getArrivalCity());
            System.out.println("Departure Time: " + flight.getDepartureTime());
            System.out.println("Arrival Time: " + flight.getArrivalTime());
            System.out.println("Duration: " + flight.getDuration());
            System.out.println("Stops: " + flight.getStops());
            System.out.println("Aircraft Details: " + flight.getAircraftDetails());
            System.out.println("Price: " + flight.getPrice());
        }
    }

    public Passenger createPassenger(String seat, String selectedClass) {
        String name = AppContext.getLoggedInFirstName();
        String contactMethod = AppContext.getLoggedInUsername();
        return new Passenger(name, seat, contactMethod, selectedClass);
    }

    public Booking createBooking(selectFlights.Flights selectedFlight, Passenger passenger, LocalDate departureDate, LocalDate returnDate, double updatedPrice) {
        return new Booking(selectedFlight, passenger, departureDate, returnDate, updatedPrice);
    }

    public void displayBookingDetails(Booking booking) {
        booking.displayBookingData();
    }

    public void executeConsoleBooking(selectFlights.Flights selectedFlight, List<String> selectedSeats, String selectedClass, LocalDate departureDate, LocalDate returnDate, double updatedPrice) {
        if (selectedFlight == null || selectedSeats.isEmpty() || selectedClass == null) {
            System.out.println("Error: Missing booking details.");
            return;
        }

        String seatDetails = String.join(", ", selectedSeats);

        Passenger passenger = createPassenger(seatDetails, selectedClass);
        Booking booking = createBooking(selectedFlight, passenger, departureDate, returnDate, updatedPrice);

        displayBookingDetails(booking);
    }
}
