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

    // Instance Variables
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

    // FXML Injected Fields
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

    // Setter Methods
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        System.out.println("Logged-in username in FlightsController: " + loggedInUsername);
    }

    public void setBookingDetails(selectFlights.Flights flight, BookingConfirmation.Passenger passenger,
            String seatDetails, String selectedClass, String updatedPrice,
            LocalDate departureDate, LocalDate returnDate, int adults, int children,
            String selectedDestination, String selectedDeparture) {
        // Input validation
        if (flight == null || passenger == null || seatDetails == null ||
                selectedClass == null || updatedPrice == null ||
                departureDate == null || returnDate == null) {
            System.err.println("Error: One or more booking details are null.");
            return;
        }

        // Set instance variables
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
        this.totalPassengers = adults + children;

        // Update UI labels
        updateFlightLabels();
        updateUserLabels();
        updateDateLabels();
    }

    // UI Update Methods
    private void updateFlightLabels() {
        flightNumberLabel.setText(selectedFlight.getFlightNo());
        departureCityLabel.setText(selectedFlight.getDepartureCity());
        arrivalCityLabel.setText(selectedFlight.getArrivalCity());
        departureTimeLabel.setText(selectedFlight.getDepartureTime());
        arrivalTimeLabel.setText(selectedFlight.getArrivalTime());
        durationLabel.setText(selectedFlight.getDuration());
        stopsLabel.setText(selectedFlight.getStops());
        priceLabel.setText(updatedPrice);
    }

    private void updateUserLabels() {
        userNameLabel.setText(passenger.getName());
        userContactLabel.setText(passenger.getContactMethod());
        selectedSeatLabel.setText(selectedSeats);
        selectedClassLabel.setText(selectedClass);
        passengerCountLabel.setText(String.valueOf(totalPassengers));
    }

    private void updateDateLabels() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        departureDateLabel.setText(departureDate.format(formatter));
        returnDateLabel.setText(returnDate.format(formatter));
    }

    // Navigation Methods
    @FXML
    private void proceedToPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment.fxml"));
            Parent paymentPage = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setFlightDetails(selectedFlight, selectedClass, adults, children,
                    departureDate, returnDate, selectedSeats, updatedPrice,
                    selectedDestination, selectedDeparture);
            paymentController.setPassengerInfo(passenger);
            paymentController.setLoggedInUsername(AppContext.getLoggedInUsername());

            performSceneTransition(paymentPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToSeatSelection(ActionEvent event) {
        try {
            showLoadingOverlay(event);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SeatSelection.fxml"));
            Parent seatSelectionPage = loader.load();

            setupSeatSelectionController(loader.getController());
            performSceneTransition((Node) event.getSource(), seatSelectionPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper Methods
    private void showLoadingOverlay(ActionEvent event) {
        Pane overlay = createLoadingOverlay(event);
        Parent currentRoot = ((Node) event.getSource()).getScene().getRoot();
        ((Pane) currentRoot).getChildren().add(overlay);
        overlay.setVisible(true);
    }

    private Pane createLoadingOverlay(ActionEvent event) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setPrefSize(((Node) event.getSource()).getScene().getWidth(),
                ((Node) event.getSource()).getScene().getHeight());

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setStyle("-fx-progress-color: #D71920;");
        loadingIndicator.setLayoutX(overlay.getPrefWidth() / 2 - 20);
        loadingIndicator.setLayoutY(overlay.getPrefHeight() / 2 - 20);

        overlay.getChildren().add(loadingIndicator);
        return overlay;
    }

    private void setupSeatSelectionController(SeatSelectionController controller) {
        controller.setSelectedClass(selectedClass);
        controller.setSelectedDestination(selectedDestination);
        controller.setSelectedDeparture(selectedDeparture);
        controller.setAdults(adults);
        controller.setChildren(children);
        controller.setSelectedFlight(selectedFlight);
        controller.setUpdatedPrice(updatedPrice);
        controller.setDepartureDate(departureDate);
        controller.setReturnDate(returnDate);
        controller.setLoggedInUsername(AppContext.getLoggedInUsername());
    }

    private void performSceneTransition(Parent newPage) {
        Stage stage = (Stage) proceedButton.getScene().getWindow();
        Scene currentScene = stage.getScene();

        FadeTransition fadeOut = createFadeTransition(currentScene.getRoot(), 1.0, 0.0);
        fadeOut.setOnFinished(e -> {
            currentScene.setRoot(newPage);
            createFadeTransition(newPage, 0.0, 1.0).play();
        });

        fadeOut.play();
    }

    private void performSceneTransition(Node sourceNode, Parent newPage) {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Scene currentScene = stage.getScene();

        FadeTransition fadeOut = createFadeTransition(currentScene.getRoot(), 1.0, 0.0);
        fadeOut.setOnFinished(e -> {
            currentScene.setRoot(newPage);
            createFadeTransition(newPage, 0.0, 1.0).play();
        });

        fadeOut.play();
    }

    private FadeTransition createFadeTransition(Node node, double fromValue, double toValue) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(fromValue);
        fade.setToValue(toValue);
        return fade;
    }
}
