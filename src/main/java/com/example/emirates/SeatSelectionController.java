package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SeatSelectionController {

    private String selectedClass;
    private String selectedDestination;
    private String selectedDeparture;
    private int adults;
    private int children;
    private String loggedInUsername;
    private selectFlights.Flights selectedFlight; // If flight-specific details are needed

    @FXML
    private GridPane economyGrid, businessGrid, firstGrid;
    @FXML
    private ScrollPane economyScroll, businessScroll, firstScroll;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label titleLabel;

    private final SeatSelection seatSelection = new SeatSelection();

    public void initialize() {
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 36);
        titleLabel.setFont(customFontSmall);
        titleLabel.setStyle("-fx-font-weight: bold;");
        seatSelection.createSeatingPlan(economyGrid, 20, 6, "Economy");
        seatSelection.createSeatingPlan(businessGrid, 10, 4, "Business");
        seatSelection.createSeatingPlan(firstGrid, 6, 2, "First");

        economyScroll.setVisible(false);
        businessScroll.setVisible(false);
        firstScroll.setVisible(false);

        loginButton.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginButton.setText("Logout");
            }
        });
        loginButton.setOnMouseExited(event -> updateLoginButton());

        updateLoginButton();
        if (selectedClass == null) {
            selectedClass = "Economy"; // Default to "Economy" if not set
        }
        displaySelectedClassGrid();
    }

    public void setSelectedClass(String classType) {
        this.selectedClass = classType;
        displaySelectedClassGrid();
    }

    public void setSelectedDestination(String destination) {
        this.selectedDestination = destination;
    }

    public void setSelectedDeparture(String departure) {
        this.selectedDeparture = departure;
    }

    public void setAdults(int adults) {
        this.adults = adults;
        updateTotalPassengers();
    }

    public void setChildren(int children) {
        this.children = children;
        updateTotalPassengers();
    }

    private void updateTotalPassengers() {
        int totalPassengers = adults + children;
        seatSelection.setTotalPassengers(totalPassengers);
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public void setSelectedFlight(selectFlights.Flights flight) {
        this.selectedFlight = flight;
    }

    private void displaySelectedClassGrid() {
        economyScroll.setVisible(false);
        businessScroll.setVisible(false);
        firstScroll.setVisible(false);

        switch (selectedClass) {
            case "Economy":
                economyScroll.setVisible(true);
                break;
            case "Business":
                businessScroll.setVisible(true);
                break;
            case "First":
                firstScroll.setVisible(true);
                break;
        }

        seatSelection.resetSelectedSeats();
    }

    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginButton.setText("Hello, " + loggedInFirstName);
        } else {
            loginButton.setText("Login");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Flights.fxml"));
            Parent flightsLayout = loader.load();

            FlightsController flightsController = loader.getController();

            // Pass back the updated data to FlightsController
            flightsController.setLoggedInUsername(loggedInUsername);
            flightsController.setSelectedClass(selectedClass);
            flightsController.setSelectedDestination(selectedDestination);
            flightsController.setSelectedDeparture(selectedDeparture);
            flightsController.setAdults(adults);
            flightsController.setChildren(children);
            flightsController.updateFlightCards();

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(flightsLayout);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), flightsLayout);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

