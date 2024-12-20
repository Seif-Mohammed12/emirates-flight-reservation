package com.example.emirates;

import java.util.Scanner;
import java.util.Set;

public class ManageBooking {
    // =================================================================================
    // Booking View Methods
    // =================================================================================
    public void viewBookingDetails() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("Your Bookings:");
        for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
            System.out.println("----------------------------");
            System.out.println(formatBookingDetails(booking));
        }
    }

    // =================================================================================
    // Seat Management Methods
    // =================================================================================
    public void changeSeat() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found. Cannot change seat.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Your Bookings:");
        displayBookingList();

        System.out.print("Select a booking to change seat (1 - " + AppContext.getBookings().size() + "): ");
        int bookingIndex = scanner.nextInt();
        scanner.nextLine();

        if (bookingIndex < 1 || bookingIndex > AppContext.getBookings().size()) {
            System.out.println("Invalid selection. Try again.");
            return;
        }

        BookingConfirmation.Booking selectedBooking = AppContext.getBookings().get(bookingIndex - 1);

        System.out.println("Currently booked seats:");
        displayBookedSeats(selectedBooking.getPassenger().getFlightClass());

        System.out.print("Enter new seat number: ");
        String newSeat = scanner.nextLine().trim();

        if (!isSeatAvailable(selectedBooking.getPassenger().getFlightClass(), newSeat)) {
            System.out.println("The seat is not available. Please choose a different seat.");
            return;
        }

        String oldSeat = selectedBooking.passenger.getSeat();
        selectedBooking.passenger.setSeat(newSeat);

        updateAppContextBookedSeats(selectedBooking.getPassenger().getFlightClass(), oldSeat, newSeat);

        System.out.println("Seat changed successfully to: " + newSeat);
    }

    // =================================================================================
    // Booking Cancellation Methods
    // =================================================================================
    public void cancelBooking() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found. Cannot cancel.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Your Bookings:");
        displayBookingList();

        System.out.print("Select a booking to cancel (1 - " + AppContext.getBookings().size() + "): ");
        int bookingIndex = scanner.nextInt();
        scanner.nextLine();

        if (bookingIndex < 1 || bookingIndex > AppContext.getBookings().size()) {
            System.out.println("Invalid selection. Try again.");
            return;
        }

        BookingConfirmation.Booking selectedBooking = AppContext.getBookings().get(bookingIndex - 1);

        System.out.print("Are you sure you want to cancel the booking with seat " + selectedBooking.passenger.getSeat()
                + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        String classType = selectedBooking.getPassenger().getFlightClass();
        String canceledSeat = selectedBooking.getPassenger().getSeat();
        AppContext.getBookedSeats(classType).remove(canceledSeat);

        AppContext.getBookings().remove(selectedBooking);
        System.out.println("Booking with seat " + canceledSeat + " has been successfully canceled.");
    }

    // =================================================================================
    // Display Helper Methods
    // =================================================================================
    private void displayBookingList() {
        int index = 1;
        for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
            System.out.println(
                    index + ". " + booking.passenger.getSeat() + " (" + booking.getFlight().getFlightNo() + ")");
            index++;
        }
    }

    private void displayBookedSeats(String flightClass) {
        Set<String> bookedSeats = AppContext.getBookedSeats(flightClass);
        if (bookedSeats.isEmpty()) {
            System.out.println("No seats are currently booked.");
        } else {
            System.out.println("Booked seats: " + String.join(", ", bookedSeats));
        }
    }

    // =================================================================================
    // Utility Methods
    // =================================================================================
    private String formatBookingDetails(BookingConfirmation.Booking booking) {
        String passengerName = AppContext.getLoggedInFirstName() + " " + AppContext.getLoggedInLastName();
        return "Booking ID: " + BookingConfirmation.Booking.generateBookingId() + "\n" +
                "Flight: " + booking.getFlight().getFlightNo() + "\n" +
                "Name: " + passengerName + "\n" +
                "Class: " + booking.passenger.getFlightClass() + "\n" +
                "Seat: " + booking.passenger.getSeat() + "\n" +
                "Departure: " + booking.getFlight().getDepartureCity() + "\n" +
                "Arrival: " + booking.getFlight().getArrivalCity() + "\n" +
                "Departure Date: " + booking.getDepartureDate() + "\n" +
                "Return Date: " + (booking.getReturnDate() != null ? booking.getReturnDate() : "N/A") + "\n" +
                "Total Price: EGP " + String.format("%.2f", booking.getUpdatedPrice());
    }

    private boolean isSeatAvailable(String flightClass, String newSeat) {
        Set<String> bookedSeats = AppContext.getBookedSeats(flightClass);
        return !bookedSeats.contains(newSeat);
    }

    private void updateAppContextBookedSeats(String flightClass, String oldSeat, String newSeat) {
        Set<String> bookedSeats = AppContext.getBookedSeats(flightClass);
        bookedSeats.remove(oldSeat);
        bookedSeats.add(newSeat);
        AppContext.setBookedSeats(flightClass, bookedSeats);
    }
}
