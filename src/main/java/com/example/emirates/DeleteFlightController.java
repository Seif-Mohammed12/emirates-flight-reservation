package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class DeleteFlightController {

    // =================================================================================
    // FXML Injected Fields
    // =================================================================================
    @FXML
    private TextField flightNoField;
    @FXML
    private TextField departureCityField;
    @FXML
    private TextField departureCodeField;
    @FXML
    private TextField arrivalCityField;
    @FXML
    private TextField arrivalCodeField;
    @FXML
    private TextField departureTimeField;
    @FXML
    private TextField arrivalTimeField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField priceField;
    @FXML
    private MenuButton aircraftMenuButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label statusLabel, titlelabel;

    private String selectedAircraft;

    // =================================================================================
    // Initialization Methods
    // =================================================================================
    @FXML
    public void initialize() {
        flightNoField.textProperty().addListener((observable, oldValue, newValue) -> {
            flightNoField.setText(newValue.toUpperCase());
        });

        loadCustomFonts();

        setFieldsEditable(false);

        searchButton.setOnAction(e -> searchFlight());
        deleteButton.setOnAction(e -> handleDelete());
        cancelButton.setOnAction(e -> handleCancel());
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        titlelabel.setFont(customFontLarge);
        titlelabel.setStyle("-fx-font-weight: bold;");
    }

    private void setFieldsEditable(boolean editable) {
        departureCityField.setEditable(editable);
        departureCodeField.setEditable(editable);
        arrivalCityField.setEditable(editable);
        arrivalCodeField.setEditable(editable);
        departureTimeField.setEditable(editable);
        arrivalTimeField.setEditable(editable);
        durationField.setEditable(editable);
        priceField.setEditable(editable);
    }

    // =================================================================================
    // Flight Search and Population Methods
    // =================================================================================
    @FXML
    private void searchFlight() {
        String flightNo = flightNoField.getText().trim();

        if (flightNo.isEmpty()) {
            showStyledAlert("Error: Please enter a flight number to search.",
                    (Stage) flightNoField.getScene().getWindow());
            return;
        }

        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV("flights.csv", false, null);

            for (selectFlights.Flights flight : flights) {
                if (flight.getFlightNo().equalsIgnoreCase(flightNo)) {
                    populateFields(flight);
                    statusLabel.setText("Flight found. Review details before deleting.");
                    return;
                }
            }

            statusLabel.setText("Flight not found.");
            showStyledAlert("Error: No flight found with the provided flight number.",
                    (Stage) flightNoField.getScene().getWindow());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while searching for the flight.", Alert.AlertType.ERROR);
        }
    }

    private void populateFields(selectFlights.Flights flight) {
        departureCityField.setText(Admin.parseCity(flight.getDepartureCity()));
        departureCodeField.setText(Admin.parseCode(flight.getDepartureCity()));
        arrivalCityField.setText(Admin.parseCity(flight.getArrivalCity()));
        arrivalCodeField.setText(Admin.parseCode(flight.getArrivalCity()));
        departureTimeField.setText(flight.getDepartureTime());
        arrivalTimeField.setText(flight.getArrivalTime());
        durationField.setText(flight.getDuration());
        aircraftMenuButton.setText(flight.getAircraftDetails());
        selectedAircraft = flight.getAircraftDetails();
        priceField.setText(Admin.stripCurrencySymbol(flight.getPrice()));
    }

    // =================================================================================
    // Action Handler Methods
    // =================================================================================
    @FXML
    private void handleDelete() {
        String flightNo = flightNoField.getText().trim();

        if (flightNo.isEmpty()) {
            showStyledAlert("Error: Please search for a flight before attempting to delete.",
                    (Stage) flightNoField.getScene().getWindow());
            return;
        }

        Stage parentStage = (Stage) deleteButton.getScene().getWindow();
        boolean confirmDeletion = showStyledConfirmationAlert(
                "Confirm Deletion",
                "Are you sure you want to delete this flight?",
                "This action cannot be undone. Proceed with caution.",
                parentStage);

        if (confirmDeletion) {
            try {
                Admin.deleteFlight(flightNo);
                showSuccessAlert("Success: Flight deleted successfully!", parentStage);
                clearFields();
                statusLabel.setText("Flight deleted successfully.");
            } catch (IOException e) {
                showStyledAlert("Error: An error occurred while deleting the flight.", parentStage);
            }
        } else {
            statusLabel.setText("Flight deletion canceled.");
        }
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            AnchorPane adminPage = loader.load();
            AdminController adminController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            adminController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {

                currentScene.setRoot(adminPage);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), adminPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        flightNoField.clear();
        departureCityField.clear();
        departureCodeField.clear();
        arrivalCityField.clear();
        arrivalCodeField.clear();
        departureTimeField.clear();
        arrivalTimeField.clear();
        durationField.clear();
        aircraftMenuButton.setText("Select Aircraft");
        priceField.clear();
        statusLabel.setText("");
    }

    // =================================================================================
    // Alert and Dialog Methods
    // =================================================================================
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void showSuccessAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("success-dialog");
        dialogPane.setHeaderText(null);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setWrappingWidth(300);
        textFlow.getChildren().add(text);
        dialogPane.setContent(textFlow);

        dialogPane.setStyle("-fx-background-color: #d4edda; -fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-border-color: #388e3c; -fx-border-width: 2px;");

        alert.setHeaderText(null);
        alert.setGraphic(null);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);
        alert.showAndWait();
    }

    private boolean showStyledConfirmationAlert(String title, String header, String message, Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(null);

        TextFlow textFlow = new TextFlow();
        Text headerText = new Text(header + "\n\n");
        headerText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 14px;");
        textFlow.getChildren().addAll(headerText, messageText);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(textFlow);
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("confirmation-dialog");
        dialogPane.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 20; -fx-border-radius: 20;" +
                "-fx-border-color: rgba(220, 220, 220, 0.8); -fx-padding: 20;");

        alert.initOwner(parentStage);
        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.initModality(Modality.WINDOW_MODAL);
        alertStage.initStyle(StageStyle.UNDECORATED);
        alertStage.setAlwaysOnTop(true);
        alertStage.toFront();

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}
