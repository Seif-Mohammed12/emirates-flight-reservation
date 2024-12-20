package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddFlightController {

    // FXML Injected Fields
    @FXML
    private TextField flightNumberField;
    @FXML
    private TextField departureCityField;
    @FXML
    private TextField departureAirportCodeField;
    @FXML
    private TextField arrivalCityField;
    @FXML
    private TextField arrivalAirportCodeField;
    @FXML
    private TextField departureTimeField;
    @FXML
    private TextField arrivalTimeField;
    @FXML
    private TextField durationField;
    @FXML
    private MenuButton aircraftMenuButton;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> arrivalTimeAdjustmentComboBox;
    @FXML
    private Label titlelabel;

    // Instance Variables
    private String selectedAircraft;
    private String loggedInUsername;
    private boolean isUpdatingTime = false;

    // Initialization Methods
    @FXML
    public void initialize() {
        for (MenuItem item : aircraftMenuButton.getItems()) {
            item.setOnAction(event -> {
                selectedAircraft = item.getText();
                aircraftMenuButton.setText(selectedAircraft);
            });
        }

        addTimeFormatter(departureTimeField);
        addTimeFormatter(arrivalTimeField);

        departureTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidFormattedTime(newValue)) {
                calculateDuration();
            }
        });

        arrivalTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidFormattedTime(newValue)) {
                calculateDuration();
            }
        });
        arrivalTimeAdjustmentComboBox.getSelectionModel().select("Same Day");
        arrivalTimeAdjustmentComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> calculateDuration());

        loadCustomFonts();

    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        titlelabel.setFont(customFontLarge);
        titlelabel.setStyle("-fx-font-weight: bold;");
    }

    // Time Handling Methods
    private void addTimeFormatter(TextField timeField) {
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdatingTime) {
                return;
            }
            try {
                isUpdatingTime = true;

                if (newValue.length() > oldValue.length()) {
                    if (newValue.matches("\\d{1,2}") && newValue.length() == 2 && !newValue.contains(":")) {
                        timeField.setText(newValue + ":");
                        timeField.positionCaret(3);
                    } else if (newValue.matches("\\d{1,2}:\\d{2}") && newValue.length() == 5) {
                        timeField.setText(newValue + " ");
                        timeField.positionCaret(6);
                    } else if (newValue.matches("\\d{1,2}:\\d{2} (A|P|a|p)")) {
                        timeField.setText(newValue.toUpperCase() + "M");
                        timeField.positionCaret(newValue.length() + 1);
                    } else if (!newValue.matches("\\d{0,2}:?\\d{0,2}\\s?(AM|PM|A|P|am|pm|a|p)?")) {
                        timeField.setText(oldValue);
                    }
                }
            } finally {
                isUpdatingTime = false;
            }
        });

        timeField.focusedProperty().addListener((observable, oldFocus, newFocus) -> {
            if (!newFocus) {
                String text = timeField.getText();
                if (!text.isEmpty() && !isValidFormattedTime(text)) {
                    showStyledAlert("Invalid Time Format: Please enter time in hh:mm AM/PM format.",
                            (Stage) timeField.getScene().getWindow());
                    timeField.requestFocus();
                } else {
                    timeField.setText(formatTime(text));
                }
            }
        });
    }

    private String formatTime(String time) {
        if (time.matches("(\\d{1,2}):(\\d{2})\\s?(AM|PM|am|pm)?")) {
            String[] parts = time.split("\\s+");
            String timePart = parts[0];
            String meridiem = parts.length > 1 ? parts[1].toUpperCase() : "AM";

            String[] timeParts = timePart.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            if (hour > 12)
                hour = 12;
            if (minute > 59)
                minute = 59;

            return String.format("%02d:%02d %s", hour, minute, meridiem);
        } else {
            return "";
        }
    }

    private boolean isValidFormattedTime(String time) {
        return time.matches("(0[1-9]|1[0-2]):[0-5]\\d (AM|PM|am|pm)");
    }

    private void calculateDuration() {
        try {
            String departure = departureTimeField.getText().trim();
            String arrival = arrivalTimeField.getText().trim();

            if (isValidFormattedTime(departure) && isValidFormattedTime(arrival)) {
                int departureInMinutes = convertTo24HourFormat(departure);
                int arrivalInMinutes = convertTo24HourFormat(arrival);

                if (arrivalTimeAdjustmentComboBox.getValue().equals("Next Day")) {
                    arrivalInMinutes += 24 * 60;
                }

                int durationMinutes = arrivalInMinutes - departureInMinutes;

                if (durationMinutes < 0) {
                    durationMinutes += 24 * 60;
                }

                int hours = durationMinutes / 60;
                int minutes = durationMinutes % 60;

                durationField.setText(hours + " hrs " + minutes + " mins");
            } else {
                durationField.setText("Invalid time");
            }
        } catch (Exception e) {
            durationField.setText("Invalid time");
        }
    }

    private int convertTo24HourFormat(String time) {
        try {
            String[] parts = time.split(" ");
            String[] timeParts = parts[0].split(":");

            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            String meridiem = parts[1].toUpperCase();

            if (meridiem.equals("PM") && hours != 12) {
                hours += 12;
            } else if (meridiem.equals("AM") && hours == 12) {
                hours = 0;
            }

            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }

    // Action Handler Methods
    @FXML
    private void handleAddFlight() {
        System.out.println("Add Flight button clicked");

        try {
            if (flightNumberField.getText().isEmpty() || departureCityField.getText().isEmpty()
                    || departureAirportCodeField.getText().isEmpty() || arrivalCityField.getText().isEmpty()
                    || arrivalAirportCodeField.getText().isEmpty() || departureTimeField.getText().isEmpty()
                    || arrivalTimeField.getText().isEmpty() || durationField.getText().isEmpty()
                    || priceField.getText().isEmpty() || selectedAircraft == null) {
                showStyledAlert("Validation Error: Please fill all fields and select an aircraft.",
                        (Stage) flightNumberField.getScene().getWindow());

                return;
            }

            String flightNo = flightNumberField.getText();
            String departureCity = departureCityField.getText();
            String departureAirportCode = departureAirportCodeField.getText();
            String arrivalCity = arrivalCityField.getText();
            String arrivalAirportCode = arrivalAirportCodeField.getText();
            String departureTime = departureTimeField.getText();
            String arrivalTime = arrivalTimeField.getText();
            String duration = durationField.getText();
            String price = priceField.getText();

            Admin.addFlight(flightNo, departureCity, departureAirportCode, arrivalCity, arrivalAirportCode,
                    departureTime, arrivalTime, duration, selectedAircraft, price);

            showSuccessAlert("Success: Flight added successfully!",
                    (Stage) flightNumberField.getScene().getWindow());

            handleCancel();
        } catch (Exception e) {
            e.printStackTrace();
            showStyledAlert("Error: An error occurred while adding the flight: " + e.getMessage(),
                    (Stage) flightNumberField.getScene().getWindow());

        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            AnchorPane adminPage = loader.load();
            AdminController adminController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            adminController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) flightNumberField.getScene().getWindow();
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

    // Alert Dialog Methods
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

    // Setter Methods
    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

}
