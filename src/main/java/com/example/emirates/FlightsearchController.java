package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class FlightsearchController {

    private FlightSearch flightSearch;

    @FXML
    private TextField flightNumberField;

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private VBox flightDetailsBox;

    @FXML
    private Label departureCityLabel;
    @FXML
    private Label departureDetailsLabel;
    @FXML
    private Label flightNumberLabel;
    @FXML
    private Label arrivalCityLabel;
    @FXML
    private Label arrivalDetailsLabel;
    @FXML
    private Label estimatedDepartureLabel;
    @FXML
    private Label gateLabel;
    @FXML
    private Label departureDateLabel;
    @FXML
    private Label estimatedArrivalLabel;
    @FXML
    private Button iconbtnsearch;

    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void initialize() {
        // Load flights from the CSV file
        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV("flights.csv", false, null);
            flightSearch = new FlightSearch(flights);
        } catch (Exception e) {
        }
    }

    @FXML
    private void handleSearchFlight() {
        String flightNumber = flightNumberField.getText();
        LocalDate departureDate = departureDatePicker.getValue();

        Stage currentStage = (Stage) flightNumberField.getScene().getWindow(); // Get the current stage for alert ownership

        if (flightNumber.isEmpty() || departureDate == null) {
            showStyledAlert("Please fill in both flight number and departure date.", currentStage);
            return;
        }

        // Search for the flight using flight number
        selectFlights.Flights result = flightSearch.getFlightByFlightNumber(flightNumber);

        if (result == null) {
            showStyledAlert("Flight " + flightNumber + " not found.", currentStage);
            flightDetailsBox.setVisible(false);
        } else {
            // Populate flight details from selectFlights.Flights
            departureCityLabel.setText(result.getDepartureCity());
            departureDetailsLabel.setText("Scheduled: " + result.getDepartureTime());
            flightNumberLabel.setText(result.getFlightNo());
            arrivalCityLabel.setText(result.getArrivalCity());
            arrivalDetailsLabel.setText("Scheduled: " + result.getArrivalTime());

            // Populate additional data
            estimatedDepartureLabel.setText(result.getDepartureTime());
            gateLabel.setText(generateRandomGate()); // Assign random gate
            departureDateLabel.setText(departureDate.format(dateFormatter)); // Format departure date
            estimatedArrivalLabel.setText(result.getArrivalTime());

            flightDetailsBox.setVisible(true);
        }
    }

    @FXML
    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            mainController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) iconbtnsearch.getScene().getWindow();
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

    private String generateRandomGate() {
        // Define realistic gate patterns
        String[] gatePrefixes = {"A", "B", "C", "D", "E", "L"};
        int gateNumber = random.nextInt(20) + 1;
        String gatePrefix = gatePrefixes[random.nextInt(gatePrefixes.length)];
        return gatePrefix + gateNumber; // Example: A12, L4, C18
    }

    private void showStyledAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        dialogPane.setHeaderText(null);
        dialogPane.setGraphic(null);

        dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");

        dialogPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dialogPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);

        alert.showAndWait();
    }
}
