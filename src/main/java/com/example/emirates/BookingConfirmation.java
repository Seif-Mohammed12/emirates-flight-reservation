package com.example.emirates;

import java.util.List;

import static com.example.emirates.PaymentController.generateBookingId;

public class BookingConfirmation {

    public static class Passenger {
        private String name;
        private String seat;
        private String contactMethod;
        private String flightClass;

        public Passenger(String name, String seat, String contactMethod) {
            this.name = name;
            this.seat = seat;
            this.contactMethod = contactMethod;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSeat() { return seat; }
        public void setSeat(String seat) { this.seat = seat; }

        public String getContactMethod() { return contactMethod; }
        public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }

        public String getFlightClass() { return flightClass; }
        public void setFlightClass(String flightClass) { this.flightClass = flightClass; }

        public void displayPassengerData() {
            System.out.println("Passenger Data:");
            System.out.println("Name: " + name);
            System.out.println("Seat: " + seat);
            System.out.println("Contact: " + contactMethod);
        }
    }

    public static class Booking {
        final selectFlights.Flights flight;
        final Passenger passenger;
        private final String bookingId;
        private String status;
        private double totalPrice;
        private String paymentId; // New field
        private double totalAmount;

        public Booking(selectFlights.Flights flight, Passenger passenger, String bookingId) {
            this.flight = flight;
            this.passenger = passenger;
            this.bookingId = generateBookingId();
            this.status = "Confirmed";
            this.totalPrice = parsePrice(flight.getPrice());
        }

        private double parsePrice(String price) {
            try {
                return Double.parseDouble(price.replace("EGP", "").replace(",", "").trim());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }


        public void displayBookingData() {
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Status: " + status);
            displayFlightData();
            passenger.displayPassengerData();
            System.out.println("Total Price: " + totalPrice);
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Passenger getPassenger() {
            return passenger;
        }

        public selectFlights.Flights getFlight() {
            return flight;
        }

        public double getTotalPrice() {
            return totalPrice;
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

    public Passenger createPassenger(String seat, String flightClass) {
        String name = AppContext.getLoggedInFirstName();
        String contactMethod = AppContext.getLoggedInUsername();
        return new Passenger(name, seat, contactMethod);
    }

    public Booking createBooking(selectFlights.Flights selectedFlight, Passenger passenger, String bookingId) {
        return new Booking(selectedFlight, passenger, bookingId);
    }

    public void displayBookingDetails(Booking booking) {
        booking.displayBookingData();
    }

    public void executeConsoleBooking(selectFlights.Flights selectedFlight, List<String> selectedSeats, String selectedClass, String bookingId) {
        if (selectedFlight == null || selectedSeats.isEmpty() || selectedClass == null) {
            System.out.println("Error: Missing booking details.");
            return;
        }

        String seatDetails = String.join(", ", selectedSeats);

        Passenger passenger = createPassenger(seatDetails, selectedClass);
        Booking booking = createBooking(selectedFlight, passenger, bookingId);


        displayBookingDetails(booking);
    }
}
