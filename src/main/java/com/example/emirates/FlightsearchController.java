package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class FlightsearchController {

    // =================================================================================
    // FXML Fields
    // =================================================================================
    @FXML
    private TextField flightNumberField;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private TextField departureCityField;
    @FXML
    private TextField arrivalCityField;
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
    @FXML
    private VBox searchByNumberBox;
    @FXML
    private Button previousFlightButton;
    @FXML
    private Button nextFlightButton;
    @FXML
    private VBox searchByRouteBox;
    @FXML
    private Label depdate;
    @FXML
    private HBox departureDateBox;

    // =================================================================================
    // Instance Variables
    // =================================================================================
    private FlightSearch flightSearch;
    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private List<selectFlights.Flights> filteredFlights;
    private int currentFlightIndex = -1;

    // =================================================================================
    // Initialization Methods
    // =================================================================================
    public void initialize() {
        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV("flights.csv", false, null);
            flightSearch = new FlightSearch(flights);
        } catch (Exception e) {
            e.printStackTrace();
        }

        configureDatePickers(departureDatePicker, LocalDate.now());
    }

    private void configureDatePickers(DatePicker departureDatePicker, LocalDate today) {
        configureDatePicker(departureDatePicker, today);
    }

    private void configureDatePicker(DatePicker datePicker, LocalDate today) {
        if (datePicker.getValue() != null && datePicker.getValue().isBefore(today)) {
            datePicker.setValue(today);
        }

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isBefore(today)) {
                datePicker.setValue(today);
            }
        });
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date == null) ? "" : date.format(formatter);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(string, formatter);
            }
        });
    }

    // =================================================================================
    // Search View Management
    // =================================================================================
    public void showSearchByNumber() {
        searchByNumberBox.setVisible(true);
        searchByRouteBox.setVisible(false);

        clearSearch();
    }

    public void showSearchByRoute() {
        searchByNumberBox.setVisible(false);
        searchByRouteBox.setVisible(true);

        clearSearch();
        departureDateBox.setVisible(false);
        departureDateBox.setManaged(false);
    }

    private void clearSearch() {
        flightNumberField.clear();
        departureDatePicker.setValue(null);

        departureCityField.clear();
        arrivalCityField.clear();

        flightDetailsBox.setVisible(false);
    }

    // =================================================================================
    // Search Handlers
    // =================================================================================
    @FXML
    private void handleSearchFlight() {
        String flightNumber = flightNumberField.getText();
        LocalDate departureDate = departureDatePicker.getValue();

        Stage currentStage = (Stage) flightNumberField.getScene().getWindow();

        if (flightNumber.isEmpty() || departureDate == null) {
            showStyledAlert("Please fill in both flight number and departure date.", currentStage);
            return;
        }

        selectFlights.Flights result = flightSearch.getFlightByFlightNumber(flightNumber);

        if (result == null) {
            showStyledAlert("Flight " + flightNumber + " not found.", currentStage);
            flightDetailsBox.setVisible(false);
        } else {
            departureCityLabel.setText(result.getDepartureCity());
            departureDetailsLabel.setText("Scheduled: " + result.getDepartureTime());
            flightNumberLabel.setText(result.getFlightNo());
            arrivalCityLabel.setText(result.getArrivalCity());
            arrivalDetailsLabel.setText("Scheduled: " + result.getArrivalTime());
            estimatedDepartureLabel.setText(result.getDepartureTime());
            gateLabel.setText(generateRandomGate());
            departureDateLabel.setText(departureDate.format(dateFormatter));
            estimatedArrivalLabel.setText(result.getArrivalTime());
            nextFlightButton.setVisible(false);
            previousFlightButton.setVisible(false);

            flightDetailsBox.setVisible(true);
        }
    }

    @FXML
    private void handleSearchByRoute() {
        String departureCity = departureCityField.getText().trim();
        String arrivalCity = arrivalCityField.getText().trim();

        Stage currentStage = (Stage) departureCityField.getScene().getWindow();

        if (departureCity.isEmpty() || arrivalCity.isEmpty()) {
            showStyledAlert("Please fill in both departure city and arrival city.", currentStage);
            return;
        }

        filteredFlights = flightSearch.searchByRoute(departureCity, arrivalCity);

        if (filteredFlights.isEmpty()) {
            showStyledAlert("No flights found from " + departureCity + " to " + arrivalCity + ".", currentStage);
            flightDetailsBox.setVisible(false);
            return;
        }

        currentFlightIndex = 0;
        displayFlightDetails(filteredFlights.get(currentFlightIndex));

        departureDateBox.setVisible(false);

        nextFlightButton.setDisable(filteredFlights.size() <= 1);
        previousFlightButton.setDisable(true);
        nextFlightButton.setVisible(true);
        previousFlightButton.setVisible(true);
    }

    private void displayFlightDetails(selectFlights.Flights flight) {

        departureCityLabel.setText(flight.getDepartureCity());
        departureDetailsLabel.setText("Scheduled: " + flight.getDepartureTime());
        flightNumberLabel.setText(flight.getFlightNo());
        arrivalCityLabel.setText(flight.getArrivalCity());
        arrivalDetailsLabel.setText("Scheduled: " + flight.getArrivalTime());
        estimatedDepartureLabel.setText(flight.getDepartureTime());
        gateLabel.setText(generateRandomGate());
        estimatedArrivalLabel.setText(flight.getArrivalTime());

        flightDetailsBox.setVisible(true);
    }

    // =================================================================================
    // Navigation Methods
    // =================================================================================
    @FXML
    private void showPreviousFlight() {
        if (filteredFlights != null && currentFlightIndex > 0) {
            currentFlightIndex--;
            displayFlightDetails(filteredFlights.get(currentFlightIndex));

            previousFlightButton.setDisable(currentFlightIndex == 0);
            nextFlightButton.setDisable(currentFlightIndex == filteredFlights.size() - 1);
        }
    }

    @FXML
    private void showNextFlight() {
        if (filteredFlights != null && currentFlightIndex < filteredFlights.size() - 1) {
            currentFlightIndex++;
            displayFlightDetails(filteredFlights.get(currentFlightIndex));

            previousFlightButton.setDisable(currentFlightIndex == 0);
            nextFlightButton.setDisable(currentFlightIndex == filteredFlights.size() - 1);
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

    // =================================================================================
    // Utility Methods
    // =================================================================================
    private String generateRandomGate() {
        String[] gatePrefixes = { "A", "B", "C", "D", "E", "L" };
        int gateNumber = random.nextInt(20) + 1;
        String gatePrefix = gatePrefixes[random.nextInt(gatePrefixes.length)];
        return gatePrefix + gateNumber;
    }

    private void showStyledAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
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
