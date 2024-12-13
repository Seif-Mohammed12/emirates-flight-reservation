package com.example.emirates;

import java.util.Optional;
import java.util.Scanner;

public class ManageBooking {

    /**
     * Displays all bookings of the logged-in user.
     * Uses AppContext to fetch and display bookings.
     */
    public void viewBookingDetails() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("Your Bookings:");
        for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
            System.out.println("----------------------------");
            booking.displayBookingData();
        }
    }

    public void changeSeat() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found. Cannot change seat.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // Display bookings
        System.out.println("Your Bookings:");
        int index = 1;
        for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
            System.out.println(index + ". " + booking.passenger.getSeat());
            index++;
        }

        // Select a booking
        System.out.print("Select a booking to change seat (1 - " + AppContext.getBookings().size() + "): ");
        int bookingIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (bookingIndex < 1 || bookingIndex > AppContext.getBookings().size()) {
            System.out.println("Invalid selection. Try again.");
            return;
        }

        // Get selected booking
        BookingConfirmation.Booking selectedBooking = AppContext.getBookings().get(bookingIndex - 1);

        // Enter new seat
        System.out.print("Enter new seat number: ");
        String newSeat = scanner.nextLine();

        // Check seat availability (Implement this logic)
        boolean isSeatAvailable = checkSeatAvailability(selectedBooking.flight, newSeat);
        if (!isSeatAvailable) {
            System.out.println("The seat is not available. Please choose a different seat.");
            return;
        }

        // Update the seat in the booking
        selectedBooking.passenger.setSeat(newSeat);
        System.out.println("Seat changed successfully to: " + newSeat);
    }

    public void cancelBooking() {
        if (!AppContext.hasBookings()) {
            System.out.println("No bookings found. Cannot cancel.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // Display bookings
        System.out.println("Your Bookings:");
        int index = 1;
        for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
            System.out.println(index + ". " + booking.passenger.getSeat());
            index++;
        }

        // Select a booking
        System.out.print("Select a booking to cancel (1 - " + AppContext.getBookings().size() + "): ");
        int bookingIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (bookingIndex < 1 || bookingIndex > AppContext.getBookings().size()) {
            System.out.println("Invalid selection. Try again.");
            return;
        }

        // Remove the selected booking
        BookingConfirmation.Booking selectedBooking = AppContext.getBookings().remove(bookingIndex - 1);
        System.out.println("Booking with seat " + selectedBooking.passenger.getSeat() + " has been canceled.");
    }

    /**
     * Mock method to check seat availability.
     * Replace with actual seat availability logic as required.
     *
     * @param flight   The flight to check.
     * @param newSeat  The seat number to check.
     * @return True if the seat is available; otherwise, false.
     */
    private boolean checkSeatAvailability(selectFlights.Flights flight, String newSeat) {
        // TODO: Implement actual seat availability check
        // Currently, assume all seats are available
        return true;
    }
}
