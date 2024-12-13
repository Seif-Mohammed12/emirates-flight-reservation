package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeSeatsController {

    @FXML
    private StackPane seatClassPane;

    @FXML
    private ScrollPane economyScroll;

    @FXML
    private ScrollPane businessScroll;

    @FXML
    private ScrollPane firstScroll;

    @FXML
    private GridPane economyGrid;

    @FXML
    private GridPane businessGrid;

    @FXML
    private GridPane firstGrid;

    @FXML
    private Button confirmChangeButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label titleLabel;

    private SeatSelection seatSelection;
    private BookingConfirmation.Booking currentBooking;

    @FXML
    public void initialize() {
        seatSelection = new SeatSelection();
        loadCustomFonts();
        if (AppContext.hasBookings()) {
            currentBooking = AppContext.getBookings().get(0);
            loadSeatSelectionForClass(currentBooking.getPassenger().getFlightClass());

            int totalPassengers = currentBooking.getPassenger().getAdults() + currentBooking.getPassenger().getChildren();
            seatSelection.setTotalPassengers(totalPassengers);

            preselectCurrentSeats(totalPassengers);
        }
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        titleLabel.setFont(customFontLarge);
        titleLabel.setStyle("-fx-font-weight: bold;");
    }

    private void loadSeatSelectionForClass(String flightClass) {
        economyScroll.setVisible(false);
        businessScroll.setVisible(false);
        firstScroll.setVisible(false);

        switch (flightClass) {
            case "Economy":
                economyScroll.setVisible(true);
                loadSeatingPlanFromAppContext(economyGrid, 20, 6, "Economy");
                break;

            case "Business":
                businessScroll.setVisible(true);
                loadSeatingPlanFromAppContext(businessGrid, 10, 4, "Business");
                break;

            case "First":
                firstScroll.setVisible(true);
                loadSeatingPlanFromAppContext(firstGrid, 6, 2, "First");
                break;
        }
    }

    private void loadSeatingPlanFromAppContext(GridPane grid, int rows, int cols, String classType) {
        // Get the booked seats for this class type from AppContext
        Set<String> bookedSeats = AppContext.getBookedSeats(classType);

        // Create the seating plan using the booked seats
        seatSelection.createSeatingPlan(grid, rows, cols, classType);

        // Style already booked seats
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton seatButton = (ToggleButton) node;
                if (bookedSeats.contains(seatButton.getText())) {
                    seatButton.setDisable(true);
                    seatButton.setStyle("-fx-background-color: red; -fx-min-width: 50px; -fx-min-height: 50px; " +
                            "-fx-border-radius: 10; -fx-background-radius: 10;");
                }
            }
        }
    }



    private void preselectCurrentSeats(int totalPassengers) {
        if (currentBooking == null || currentBooking.getPassenger().getSeat() == null) {
            return;
        }

        String[] currentSeats = currentBooking.getPassenger().getSeat().split(", ");
        GridPane grid = getGridForCurrentClass();

        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof javafx.scene.control.ToggleButton) {
                javafx.scene.control.ToggleButton seatButton = (javafx.scene.control.ToggleButton) node;
                for (String seat : currentSeats) {
                    if (seatButton.getText().equals(seat)) {
                        seatButton.setSelected(true);
                        seatButton.setDisable(true); // Disable already booked seats to prevent changes
                        seatButton.setStyle("-fx-min-width: 50px; -fx-min-height: 50px; " +
                                "-fx-background-color: gray; -fx-border-radius: 10; -fx-background-radius: 10;");
                        break;
                    }
                }
            }
        }
    }

    private GridPane getGridForCurrentClass() {
        switch (currentBooking.getPassenger().getFlightClass()) {
            case "Economy":
                return economyGrid;
            case "Business":
                return businessGrid;
            case "First":
                return firstGrid;
            default:
                return null;
        }
    }

    @FXML
    private void handleConfirmChangeSeat() {
        List<javafx.scene.control.ToggleButton> selectedSeats = seatSelection.getSelectedSeats();
        Stage stage = (Stage) confirmChangeButton.getScene().getWindow();
        int totalPassengers = currentBooking.getPassenger().getAdults() + currentBooking.getPassenger().getChildren();
        if (selectedSeats.size() != totalPassengers) {
            showStyledAlert("Please select exactly " + totalPassengers + " seats.", stage);
            return;
        }

        StringBuilder newSeats = new StringBuilder();
        for (javafx.scene.control.ToggleButton seatButton : selectedSeats) {
            newSeats.append(seatButton.getText()).append(", ");
        }

        // Remove the trailing comma and space
        if (newSeats.length() > 0) {
            newSeats.setLength(newSeats.length() - 2);
        }

        currentBooking.getPassenger().setSeat(newSeats.toString());
        showSuccessAlert("Seats successfully changed to: " + newSeats, stage);
        handleCancelChange();
    }

    @FXML
    private void handleCancelChange() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            mainController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) cancelButton.getScene().getWindow();
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
        text.setWrappingWidth(300); // Wrap the text for better readability
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
