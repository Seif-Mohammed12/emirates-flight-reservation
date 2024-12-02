package com.example.emirates;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class SeatPlanController {

    @FXML
    private StackPane planeContainer;

    @FXML
    private VBox seatInfoBox;

    private String selectedClass; // Store the selected class (Economy, Business, First)

    @FXML
    public void initialize() {
        // Create the plane layout

    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
        System.out.println("Selected Class set to: " + selectedClass);

        // Update the seat layout based on the class
        updateSeatLayout();
    }

    private void updateSeatLayout() {
        // Clear previous seat info and plane container
        seatInfoBox.getChildren().clear();
        // Display selected class in seatInfoBox
        Label classLabel = new Label("Selected Class: " + selectedClass);
        classLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");
        seatInfoBox.getChildren().add(classLabel);
    }

    private VBox createSeatLayout() {
        VBox seatLayout = new VBox(10); // Vertical box with spacing between rows
        seatLayout.setAlignment(Pos.TOP_CENTER);

        // Simulating seats (rows and columns) based on the class
        int rows = 10; // Default number of rows
        int columns = 4; // Default number of columns (per row)

        if ("Business".equalsIgnoreCase(selectedClass)) {
            rows = 8;
            columns = 3;
        } else if ("First".equalsIgnoreCase(selectedClass)) {
            rows = 4;
            columns = 2;
        }

        for (int i = 0; i < rows; i++) { // Rows of seats
            HBox row = new HBox(10); // Horizontal box with spacing between seats
            row.setAlignment(Pos.CENTER);

            for (int j = 0; j < columns; j++) { // Columns of seats
                Button seat = new Button("Seat " + (char) ('A' + j) + (i + 1)); // Seat label (A1, B1, etc.)
                seat.setPrefSize(50, 50); // Seat size
                seat.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-font-size: 12px;");
                seat.setOnAction(e -> handleSeatSelection(seat.getText())); // Seat selection action
                row.getChildren().add(seat);
            }

            seatLayout.getChildren().add(row);
        }
        return seatLayout;
    }

    private void handleSeatSelection(String seat) {
        // Handle seat selection logic
        seatInfoBox.getChildren().clear();
        Label seatLabel = new Label("Selected Seat: " + seat);
        seatLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        seatInfoBox.getChildren().add(seatLabel);
    }
}
