package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ViewBookingController {

    // =================================================================================
    // FXML Injected Fields
    // =================================================================================
    @FXML
    private Label headerLabel;
    @FXML
    private Label bookingIdLabel;
    @FXML
    private Label flightNumberLabel;
    @FXML
    private Label passengerNameLabel;
    @FXML
    private Label flightClassLabel;
    @FXML
    private Label seatLabel;
    @FXML
    private Label departureLabel;
    @FXML
    private Label arrivalLabel;
    @FXML
    private Label departureDateLabel;
    @FXML
    private Label returnDateLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private VBox detailsContainer;
    @FXML
    private Button backButton;

    // =================================================================================
    // Initialization Methods
    // =================================================================================
    @FXML
    public void initialize() {
        populateBookingDetails();
        loadCustomFonts();
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 32);
        headerLabel.setFont(customFontLarge);
        headerLabel.setStyle("-fx-font-weight: bold;");
    }

    // =================================================================================
    // Booking Details Methods
    // =================================================================================
    private void populateBookingDetails() {
        if (!AppContext.hasBookings()) {
            showNoBookingsAlert();
            return;
        }

        BookingConfirmation.Booking booking = AppContext.getBookings().get(0);

        bookingIdLabel.setText("Booking ID: " + BookingConfirmation.Booking.generateBookingId());
        flightNumberLabel.setText("Flight: " + booking.getFlight().getFlightNo());
        passengerNameLabel
                .setText("Name: " + AppContext.getLoggedInFirstName() + " " + AppContext.getLoggedInLastName());
        flightClassLabel.setText("Class: " + booking.getPassenger().getFlightClass());
        seatLabel.setText("Seat(s): " + booking.getPassenger().getSeat());
        departureLabel.setText("Departure: " + booking.getFlight().getDepartureCity());
        arrivalLabel.setText("Arrival: " + booking.getFlight().getArrivalCity());
        departureDateLabel.setText("Departure Date: " + (booking.getDepartureDate() != null
                ? booking.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A"));
        returnDateLabel.setText("Return Date: " + (booking.getReturnDate() != null
                ? booking.getReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A"));
        totalPriceLabel.setText("Total Price: EGP " + String.format("%.2f", booking.getUpdatedPrice()));
    }

    // =================================================================================
    // Navigation Methods
    // =================================================================================
    @FXML
    private void gotoMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            mainController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(mainPage);
                Platform.runLater(() -> {
                    mainController.titleLabel.requestFocus();
                });

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =================================================================================
    // Alert Methods
    // =================================================================================
    private void showNoBookingsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Bookings Found");
        alert.setHeaderText(null);
        alert.setContentText("You have no bookings. Please make a booking first.");
        alert.showAndWait();
    }
}
