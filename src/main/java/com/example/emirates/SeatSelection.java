package com.example.emirates;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

public class SeatSelection {
    private final Random random = new Random();
    private final List<ToggleButton> selectedSeats = new ArrayList<>();
    private int totalPassengers = 1;

    public void createSeatingPlan(GridPane grid, int rows, int cols, String classType) {
        char[] seatLabels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        grid.getChildren().clear();

        int leftSeats = 0, rightSeats = 0;

        switch (classType) {
            case "Economy":
                leftSeats = 3;
                rightSeats = 3;
                break;
            case "Business":
                leftSeats = 2;
                rightSeats = 2;
                break;
            case "First":
                leftSeats = 1;
                rightSeats = 1;
                break;
        }

        Set<String> bookedSeats = AppContext.getBookedSeats(classType);
        if (bookedSeats.isEmpty()) {
            bookedSeats = new HashSet<>();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < (leftSeats + rightSeats + 1); col++) {
                    if (col == leftSeats) {
                        continue; 
                    }

                    String seatLabel = (col < seatLabels.length) ? String.valueOf(seatLabels[col < leftSeats ? col : col - 1]) : "X";
                    String seatId = seatLabel + (row + 1);

                    if (random.nextDouble() < 0.2) { 
                        bookedSeats.add(seatId);
                    }
                }
            }
            AppContext.setBookedSeats(classType, bookedSeats);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < (leftSeats + rightSeats + 1); col++) {
                if (col == leftSeats) {
                    continue; 
                }

                String seatLabel = (col < seatLabels.length) ? String.valueOf(seatLabels[col < leftSeats ? col : col - 1]) : "X";
                String seatId = seatLabel + (row + 1);
                ToggleButton seatButton = new ToggleButton(seatId);
                styleAvailableSeat(seatButton);

                if (bookedSeats.contains(seatId)) {
                    styleBookedSeat(seatButton);
                }

                seatButton.setOnAction(e -> handleSeatSelection(seatButton));

                grid.add(seatButton, col, row);
                GridPane.setHalignment(seatButton, HPos.CENTER);
                GridPane.setValignment(seatButton, VPos.CENTER);
            }
        }

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
    }

    private void styleAvailableSeat(ToggleButton seatButton) {
        seatButton.setStyle("-fx-min-width: 60px; -fx-min-height: 60px; -fx-background-color: green; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0.4, 2, 2);");
    }

    private void styleBookedSeat(ToggleButton seatButton) {
        seatButton.setStyle("-fx-background-color: red;");
        seatButton.setDisable(true);
    }

    private void handleSeatSelection(ToggleButton seatButton) {
        if (!seatButton.isDisabled()) {
            if (seatButton.isSelected()) {
                if (selectedSeats.size() >= totalPassengers) {
                    seatButton.setSelected(false);
                    showStyledAlert("You cannot select more seats than the total number of passengers.", (Stage) seatButton.getScene().getWindow());
                } else {

                    seatButton.setStyle("-fx-min-width: 50px; -fx-min-height: 50px; " +
                            "-fx-background-color: blue; -fx-border-radius: 10; -fx-background-radius: 10;");
                    selectedSeats.add(seatButton);
                }
            } else {
                seatButton.setStyle("-fx-min-width: 60px; -fx-min-height: 60px; " +
                        "-fx-background-color: green; -fx-border-radius: 10; -fx-background-radius: 10;");
                selectedSeats.remove(seatButton);
            }
        }
    }


    public void resetSelectedSeats() {
        for (ToggleButton seat : selectedSeats) {
            if (!seat.isDisabled()) {
                seat.setStyle("-fx-background-color: green;");
                seat.setSelected(false);
            }
        }
        selectedSeats.clear();
    }

    public List<ToggleButton> getSelectedSeats() {
        return selectedSeats;
    }
    public void selectSeat(ToggleButton seat) {
        selectedSeats.add(seat);
    }

    public void deselectSeat(ToggleButton seat) {
        selectedSeats.remove(seat);
    }
    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
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

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setStyle("-fx-font-size: 16px; -fx-fill: #721c24;");
        text.setWrappingWidth(300);
        textFlow.getChildren().add(text);
        
        dialogPane.setContent(textFlow);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);
        dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");
        dialogPane.setMinWidth(Region.USE_COMPUTED_SIZE);
        dialogPane.setPrefWidth(350); 
        dialogPane.setMaxWidth(400);

        alert.showAndWait();
    }
}
