package com.example.emirates;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ManageBookingController {

    @FXML
    private ListView<String> bookingListView;

    @FXML
    private VBox viewBookingSection;

    @FXML
    private VBox changeSeatSection;

    @FXML
    private VBox cancelBookingSection;

    @FXML
    private Label refLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label flightLabel;
    @FXML
    private Label paymentIdLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label seatLabel;
    @FXML
    private Label flightClassLabel;
    @FXML
    private Label contactMethodLabel;

    @FXML
    private TextField newSeatField;

    private String action;
    private BookingConfirmation.Booking selectedBooking;

    @FXML
    public void initialize() {
        loadBookings();
        handleAction();
    }

    public void setAction(String action) {
        this.action = action;
    }

    private void loadBookings() {
        // Populate the booking list dynamically from AppContext
        if (AppContext.hasBookings()) {
            for (BookingConfirmation.Booking booking : AppContext.getBookings()) {
                bookingListView.getItems().add("Booking ID: " + booking.getBookingId());
            }
        }
    }

    private void handleAction() {
        if (action == null) return;

        switch (action.toUpperCase()) {
            case "VIEW":
                showViewBooking();
                break;
            case "CHANGE":
                showChangeSeat();
                break;
            case "CANCEL":
                showCancelBooking();
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    @FXML
    public void showViewBooking() {
        hideAllSections();
        int selectedIndex = bookingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showError("Please select a booking to view.");
            return;
        }
        selectedBooking = AppContext.getBookings().get(selectedIndex);

        // Populate details including payment and passenger information
        refLabel.setText("Booking ID: " + selectedBooking.getBookingId());
        nameLabel.setText("Passenger Name: " + selectedBooking.getPassenger().getName());
        flightLabel.setText("Flight Number: " + selectedBooking.getFlight().getFlightNo());
        paymentIdLabel.setText("Payment ID: " + selectedBooking.getPaymentId());
        totalAmountLabel.setText("Total Amount Paid: EGP " + String.format("%.2f", selectedBooking.getTotalAmount()));
        seatLabel.setText("Seat: " + selectedBooking.getPassenger().getSeat());
        flightClassLabel.setText("Class: " + selectedBooking.getPassenger().getFlightClass());
        contactMethodLabel.setText("Contact: " + selectedBooking.getPassenger().getContactMethod());

        viewBookingSection.setVisible(true);
    }

    @FXML
    public void showChangeSeat() {
        hideAllSections();
        int selectedIndex = bookingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showError("Please select a booking to change.");
            return;
        }
        selectedBooking = AppContext.getBookings().get(selectedIndex);
        changeSeatSection.setVisible(true);
    }

    @FXML
    public void handleChangeSeat() {
        String newSeat = newSeatField.getText();
        if (newSeat.isEmpty()) {
            showError("Please enter a valid seat number.");
            return;
        }
        if (checkSeatAvailability(selectedBooking.getFlight(), newSeat)) {
            selectedBooking.getPassenger().setSeat(newSeat);
            bookingListView.getItems().set(
                    bookingListView.getSelectionModel().getSelectedIndex(),
                    "Booking ID: " + selectedBooking.getBookingId()
            );
            showSuccess("Seat changed to: " + newSeat);
        } else {
            showError("Seat not available. Try another.");
        }
    }

    @FXML
    public void showCancelBooking() {
        hideAllSections();
        int selectedIndex = bookingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showError("Please select a booking to cancel.");
            return;
        }
        selectedBooking = AppContext.getBookings().get(selectedIndex);
        cancelBookingSection.setVisible(true);
    }

    @FXML
    public void handleCancelBooking() {
        AppContext.getBookings().remove(selectedBooking);
        bookingListView.getItems().remove(bookingListView.getSelectionModel().getSelectedIndex());
        showSuccess("Booking canceled.");
        hideAllSections();
    }

    private void hideAllSections() {
        viewBookingSection.setVisible(false);
        changeSeatSection.setVisible(false);
        cancelBookingSection.setVisible(false);
    }

    private boolean checkSeatAvailability(selectFlights.Flights flight, String newSeat) {
        // TODO: Replace with real logic
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
