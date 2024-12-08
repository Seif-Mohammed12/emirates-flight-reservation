package com.example.emirates;

import java.util.Scanner;
public class ManageBooking {


    private String bookedSeat;
    private String bookingId;

    public ManageBooking(String bookedSeat, String bookingId) {
        this.bookedSeat = bookedSeat;
        this.bookingId = bookingId;
    }

    public void viewBookingDetails() {
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Booked Seat: " + bookedSeat);
    }

    public void changeSeat() {
        // ellogic as en el use y change b nafso bs lw hatshof hat8erha l eh f eshta

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new seat number: ");
        String newSeat = scanner.nextLine();
        //*Important:*
        //1. *Seat Availability Check:*
        //- Implement logic to check if the newSeat is available.
        // - This usually involves interacting with a seat availability database or
        //  a method that checks seat availability.
        // 2. *Update Booking System:*
        //  - If the newSeat is available, update the booking system with the new seat.
        // - This typically involves database updates or modifications to data structures.

        System.out.println("Seat changed successfully!");
        // Update the bookedSeat variable
        this.bookedSeat = newSeat;
    }

    public void cancelBooking() {
        // *Important:*
        // 1. *Cancel Booking in System:*
        //    - Implement logic to cancel the booking in the system.
        //    - This usually involves removing the booking from the database or
        //      modifying data structures to reflect the cancellation.

        System.out.println("Booking canceled successfully!");
        // Clear the bookedSeat and bookingId
        this.bookedSeat = null;
        this.bookingId = null;
    }

}
