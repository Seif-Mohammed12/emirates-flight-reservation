package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingConfirmationController {

    private selectFlights.Flights selectedFlight;
    private BookingConfirmation.Passenger passenger;
    private String selectedSeats;
    private String selectedClass;
    private String updatedPrice;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int totalPassengers;
    private String selectedDestination;
    private String selectedDeparture;
    private int adults;
    private int children;
    private String loggedInUsername;

    @FXML
    private Label flightNumberLabel, departureCityLabel, arrivalCityLabel;
    @FXML
    private Label departureTimeLabel, arrivalTimeLabel, durationLabel, stopsLabel, priceLabel;
    @FXML
    private Label departureDateLabel, returnDateLabel;
    @FXML
    private Label userNameLabel, userContactLabel, selectedSeatLabel, selectedClassLabel, passengerCountLabel;
    @FXML
    private Button proceedButton;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        System.out.println("Logged-in username in FlightsController: " + loggedInUsername);
    }

    public void setBookingDetails(selectFlights.Flights flight, BookingConfirmation.Passenger passenger, String seatDetails,
                                  String selectedClass, String updatedPrice, LocalDate departureDate, LocalDate returnDate,
                                  int adults, int children, String selectedDestination, String selectedDeparture) {


        if (flight == null || passenger == null || seatDetails == null || selectedClass == null || updatedPrice == null || departureDate == null || returnDate == null) {
            System.err.println("Error: One or more booking details are null.");
            return;
        }

        this.selectedFlight = flight;
        this.passenger = passenger;
        this.selectedSeats = seatDetails;
        this.selectedClass = selectedClass;
        this.updatedPrice = updatedPrice;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.adults = adults;
        this.children = children;
        this.selectedDestination = selectedDestination;
        this.selectedDeparture = selectedDeparture;
        totalPassengers = adults + children;

        flightNumberLabel.setText(flight.getFlightNo());
        departureCityLabel.setText(flight.getDepartureCity());
        arrivalCityLabel.setText(flight.getArrivalCity());
        departureTimeLabel.setText(flight.getDepartureTime());
        arrivalTimeLabel.setText(flight.getArrivalTime());
        durationLabel.setText(flight.getDuration());
        stopsLabel.setText(flight.getStops());
        priceLabel.setText(updatedPrice);

        userNameLabel.setText(passenger.getName());
        userContactLabel.setText(passenger.getContactMethod());

        selectedSeatLabel.setText(seatDetails);

        selectedClassLabel.setText(selectedClass);
        passengerCountLabel.setText(String.valueOf(totalPassengers));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        departureDateLabel.setText(departureDate.format(formatter));
        returnDateLabel.setText(returnDate.format(formatter));

    }

    @FXML
    private void proceedToPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment.fxml"));
            Parent paymentPage = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setFlightDetails(selectedFlight, selectedClass, adults, children, departureDate, returnDate, selectedSeats, updatedPrice, selectedDestination, selectedDeparture);
            paymentController.setPassengerInfo(passenger);
            paymentController.setLoggedInUsername(AppContext.getLoggedInUsername());
            Stage stage = (Stage) proceedButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                currentScene.setRoot(paymentPage);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), paymentPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToSeatSelection(ActionEvent event) {
        try {

            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlay.setPrefSize(((Node) event.getSource()).getScene().getWidth(),
                    ((Node) event.getSource()).getScene().getHeight());
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setStyle("-fx-progress-color: #D71920;");
            loadingIndicator.setLayoutX(overlay.getPrefWidth() / 2 - 20);
            loadingIndicator.setLayoutY(overlay.getPrefHeight() / 2 - 20);
            overlay.getChildren().add(loadingIndicator);

            Parent currentRoot = ((Node) event.getSource()).getScene().getRoot();
            ((Pane) currentRoot).getChildren().add(overlay);
            overlay.setVisible(true);

            // Load the SeatSelection.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SeatSelection.fxml"));
            Parent seatSelectionPage = loader.load();

            SeatSelectionController seatSelectionController = loader.getController();
            seatSelectionController.setSelectedClass(selectedClass);
            seatSelectionController.setSelectedDestination(selectedDestination);
            seatSelectionController.setSelectedDeparture(selectedDeparture);
            seatSelectionController.setAdults(adults);
            seatSelectionController.setChildren(children);
            seatSelectionController.setSelectedFlight(selectedFlight);
            seatSelectionController.setUpdatedPrice(updatedPrice);
            seatSelectionController.setDepartureDate(departureDate);
            seatSelectionController.setReturnDate(returnDate);
            seatSelectionController.setLoggedInUsername(AppContext.getLoggedInUsername());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(seatSelectionPage);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), seatSelectionPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
            overlay.setVisible(false);
            ((Pane) currentRoot).getChildren().remove(overlay); // Clean up the overlay
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
