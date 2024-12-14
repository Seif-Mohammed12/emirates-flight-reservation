package com.example.emirates;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UpdateFlightController {

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
    private ComboBox<String> arrivalAdjustmentComboBox;
    @FXML
    private TextField durationField;
    @FXML
    private MenuButton aircraftMenuButton;
    @FXML
    private TextField priceField;
    @FXML
    private Button searchButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label statusLabel, titlelabel;

    private String selectedAircraft;

    @FXML
    public void initialize() {
        for (MenuItem item : aircraftMenuButton.getItems()) {
            item.setOnAction(event -> {
                selectedAircraft = item.getText();
                aircraftMenuButton.setText(selectedAircraft);
            });
        }
        flightNoField.textProperty().addListener((observable, oldValue, newValue) -> {
            flightNoField.setText(newValue.toUpperCase());
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                priceField.setText(oldValue);
            }
        });

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

        arrivalAdjustmentComboBox.getSelectionModel().select("Same Day");
        arrivalAdjustmentComboBox.valueProperty().addListener((observable, oldValue, newValue) -> calculateDuration());

        loadCustomFonts();

        searchButton.setOnAction(e -> searchFlight());
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        titlelabel.setFont(customFontLarge);
        titlelabel.setStyle("-fx-font-weight: bold;");
    }


    private boolean isUpdatingTime = false;

    private void addTimeFormatter(TextField timeField) {
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdatingTime) {
                return; // Prevent recursive updates
            }
            try {
                isUpdatingTime = true; // Set the flag to prevent recursion

                if (newValue.length() > oldValue.length()) {
                    if (newValue.matches("\\d{1,2}") && newValue.length() == 2 && !newValue.contains(":")) {
                        timeField.setText(newValue + ":");
                        timeField.positionCaret(3); // Move caret after the colon
                    } else if (newValue.matches("\\d{1,2}:\\d{2}") && newValue.length() == 5) {
                        timeField.setText(newValue + " ");
                        timeField.positionCaret(6); // Move caret after the space
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

            if (hour > 12) hour = 12;
            if (minute > 59) minute = 59;

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

                if (arrivalAdjustmentComboBox.getValue().equals("Next Day")) {
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



    @FXML
    private void searchFlight() {
        String flightNo = flightNoField.getText().trim();

        if (flightNo.isEmpty()) {
            showAlert("Error", "Please enter a flight number to search.", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<selectFlights.Flights> flights = FileManager.loadFlightsFromCSV("flights.csv", false, null);

            for (selectFlights.Flights flight : flights) {
                if (flight.getFlightNo().equalsIgnoreCase(flightNo)) {
                    populateFields(flight); // Populate the fields with the flight details
                    statusLabel.setText("Flight found. Edit the details and save.");
                    return;
                }
            }

            // If no flight is found
            statusLabel.setText("Flight not found.");
            showAlert("Error", "No flight found with the provided flight number.", Alert.AlertType.ERROR);

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

    @FXML
    private void handleSave() {
        String flightNo = flightNoField.getText().trim();
        String departureCity = departureCityField.getText().trim();
        String departureCode = departureCodeField.getText().trim();
        String arrivalCity = arrivalCityField.getText().trim();
        String arrivalCode = arrivalCodeField.getText().trim();
        String departureTime = departureTimeField.getText().trim();
        String arrivalTime = arrivalTimeField.getText().trim();
        String duration = durationField.getText().trim();
        String arrivalAdjustment = arrivalAdjustmentComboBox.getValue() != null ? arrivalAdjustmentComboBox.getValue() : "Same Day";
        String updatedPrice = Admin.formatPrice(priceField.getText().trim());

        // Validate required fields
        if (flightNo.isEmpty() || departureCity.isEmpty() || departureCode.isEmpty() ||
                arrivalCity.isEmpty() || arrivalCode.isEmpty() || departureTime.isEmpty() ||
                arrivalTime.isEmpty() || duration.isEmpty() || updatedPrice.isEmpty() || selectedAircraft == null) {
            showAlert("Error", "All fields must be filled out to save changes.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Admin.updateFlight(
                    flightNo,
                    departureCity,
                    departureCode,
                    arrivalCity,
                    arrivalCode,
                    departureTime,
                    arrivalAdjustment.equals("Next Day") ? Admin.formatTime(arrivalTime) + " +1" : arrivalTime,
                    duration,
                    selectedAircraft,
                    updatedPrice
            );

            showAlert("Success", "Flight details updated successfully!", Alert.AlertType.INFORMATION);
            statusLabel.setText("Flight updated successfully.");
        } catch (IOException e) {
            showAlert("Error", "An error occurred while saving the flight details.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showStyledAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
