package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CancelBookingController {

    @FXML
    private VBox detailsContainer;
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
    private Button cancelBookingButton;
    @FXML
    private Button backButton;
    @FXML
    private Label headerLabel, instructionLabel, warningLabel;

    private BookingConfirmation.Booking selectedBooking;

    @FXML
    public void initialize() {
        selectedBooking = AppContext.getBookings().get(0);
        populateBookingDetails(selectedBooking);
        loadCustomFonts();

    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 16);
        headerLabel.setFont(customFontLarge);
        headerLabel.setStyle("-fx-font-weight: bold;");
        instructionLabel.setFont(customFontSmall);
        instructionLabel.setStyle("-fx-font-weight: bold;");
        warningLabel.setFont(customFontSmall);
        warningLabel.setStyle("-fx-font-weight: bold;");

    }

    private void populateBookingDetails(BookingConfirmation.Booking booking) {
        bookingIdLabel.setText("Booking ID: " + BookingConfirmation.Booking.generateBookingId());
        flightNumberLabel.setText("Flight: " + booking.getFlight().getFlightNo());
        passengerNameLabel.setText("Name: " + AppContext.getLoggedInFirstName() + " " + AppContext.getLoggedInLastName());
        flightClassLabel.setText("Class: " + booking.getPassenger().getFlightClass());
        seatLabel.setText("Seat(s): " + booking.getPassenger().getSeat());
        departureLabel.setText("Departure: " + booking.getFlight().getDepartureCity());
        arrivalLabel.setText("Arrival: " + booking.getFlight().getArrivalCity());
        departureDateLabel.setText("Departure Date: " + (booking.getDepartureDate() != null ? booking.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        returnDateLabel.setText("Return Date: " + (booking.getReturnDate() != null ? booking.getReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        totalPriceLabel.setText("Total Price: EGP " + String.format("%.2f", booking.getUpdatedPrice()));
    }

    @FXML
    private void handleCancelBooking() {
        if (selectedBooking == null) {
            showErrorAlert("No booking has been selected for cancellation.", (Stage) cancelBookingButton.getScene().getWindow());
            return;
        }

        boolean confirmed = showStyledConfirmationAlert("Cancel Booking",
                "Are you sure you want to cancel this booking?",
                "A cancellation fee will be applied. This action cannot be undone.", (Stage) cancelBookingButton.getScene().getWindow());
        if (!confirmed) {
            return;
        }

        double refundAmount = VisaPayment.calculateRefundAmount(selectedBooking.getUpdatedPrice());

        AppContext.clearBookings();

        
        showSuccessAlert("Your booking has been successfully cancelled.\n" +
                        "Refund Amount: EGP " + String.format("%.2f", refundAmount),
                (Stage) cancelBookingButton.getScene().getWindow());

        gotoMain();
    }

    private void disableCancelBookingButton() {
        cancelBookingButton.setDisable(true);
    }

    private void showErrorAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        dialogPane.setHeaderText(null);
        dialogPane.setGraphic(null);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setWrappingWidth(150);
        textFlow.getChildren().add(text);
        dialogPane.setContent(textFlow);

        dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
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
}
